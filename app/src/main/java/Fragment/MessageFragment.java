package Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.suyong.sunshineflat.MyApplication;
import com.suyong.sunshineflat.R;

import org.greenrobot.eventbus.EventBus;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import CustomView.MyListView;
import Util.DBUtils;
import Util.LoadingDialog;
import Util.MyThreadPool;
import activity.chatActivity;
import adapter.MessageFragmentAdapter;
import adapter.simpleAdapter1;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pojo.ServiceUser;
import pojo.User;

public class MessageFragment extends  BaseFragment implements View.OnClickListener, EMMessageListener {


    @BindView(R.id.qiehuan)
    ImageView qiehuan;
    @BindView(R.id.search)
    ImageView search;

    @BindView(R.id.listView)
    MyListView li;
    List<ServiceUser> data = new ArrayList<>();
    MessageFragmentAdapter adapter;

    Map<String, EMConversation> conversations;

    private  PopupWindow popupWindow;
    private Unbinder unbinder;
    private boolean isLoginFlag=false;
    private ProgressDialog LoadDialog;
    private String currentLoginUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment,container,false);
        unbinder = ButterKnife.bind(this, view);
        adapter = new MessageFragmentAdapter(data,getActivity());
        li.setAdapter(adapter);
        LoadDialog = LoadingDialog.createLoadingDialog(getActivity(),"提示","查询中...");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initOnClickEvent();
    }

    //初始化点击事件
    private void initOnClickEvent() {
       initAdapter();

        qiehuan.setOnClickListener((v)->{
            showPopupWindow();
        });

        search.setOnClickListener((v)->{
            if(!isLoginFlag) {
                Toast.makeText(getActivity(),"请您先点击右边的切换按钮！",Toast.LENGTH_SHORT).show();
                return;
            }
            User uer = ((MyApplication)getActivity().getApplication()).getUser();
            MyThreadPool.getInstance().submit(()->{
                try {
                    getActivity().runOnUiThread(()->{
                        LoadDialog.show();
                    });
                    List<ServiceUser> lvshiList = new ArrayList<>();
                    List<ServiceUser> jinjiList = new ArrayList<>();
                    String[] sql = {"select * from user where lawer_flag = ?",
                            "select * from user where agent_flag = ?"};
                    String[] columns = {"1"};
                    int[] types = {Types.INTEGER};
                    for (int i = 0; i < sql.length; i++) {
                        ArrayList<HashMap<String, String>> re = DBUtils.query(columns, types, sql[i]);
                        if(i==0){
                            if(re.size()<=0) {continue;}
                            for(HashMap<String, String>t:re){
                                if(t.get("phone").equals(uer.getPhone())) continue;
                                ServiceUser user = new ServiceUser();
                                user.setId(t.get("lawerId"));
                                user.setName(t.get("nickname"));
                                user.setImagePhoto(t.get("pic"));
                                lvshiList.add(user);
                            }
                        }else if(i==1){
                            if(re.size()<=0) {continue;}
                            for(HashMap<String, String>t:re){
                                if(t.get("phone").equals(uer.getPhone())) continue;
                                ServiceUser user = new ServiceUser();
                                user.setId(t.get("agentId"));
                                user.setName(t.get("nickname"));
                                user.setImagePhoto(t.get("pic"));
                                jinjiList.add(user);
                            }
                        }
                    }

                    getActivity().runOnUiThread(()->{
                        showPopupUser(lvshiList,jinjiList);
                        LoadDialog.dismiss();
                    });
                }catch(Exception e){
                    e.printStackTrace();
                    getActivity().runOnUiThread(()->{
                        LoadDialog.dismiss();
                    });
                }
            });
        });
    }

    private void initAdapter() {
        adapter.setOnItemClick((user) ->{
            //Toast.makeText(getActivity(),user.getId(),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), chatActivity.class);
            intent.putExtra("sendId",currentLoginUserId);
            EventBus.getDefault().postSticky(user);
            startActivity(intent);
        });
        li.setAdapter(adapter);
        li.setOnRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onRefresh();
                        li.finishRefresh();
                    }
                },2000);

            }
        },0);
    }


    //更新消息列表
    private void onRefresh() {
        if(!isLoginFlag){ Toast.makeText(getActivity(),"请先点击切换按钮，切换用户！",Toast.LENGTH_SHORT).show();return;}
        if(conversations==null||conversations.size()==0) {
            data.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(getActivity(),"还没有聊天记录，快去找人聊天吧！",Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> tdata = new ArrayList<>();
        Iterator it = conversations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            EMConversation value = (EMConversation) entry.getValue();
            tdata.add(key);
        }
        if(tdata.size()==0) return;
            MyThreadPool.getInstance().submit(() -> {
                try {
                    getActivity().runOnUiThread(() -> {
                        LoadDialog.show();
                    });
                    //data.clear();
                    List<ServiceUser> result = new ArrayList<>();
                    for (String phone : tdata) {
                        String sql = "select * from user where  lawerId= ? or agentId = ? or phone = ? ";
                        String[] columns = {phone, phone, phone};
                        int[] type = {Types.CHAR, Types.CHAR, Types.CHAR};
                        HashMap<String, String> d = DBUtils.query(columns, type, sql).get(0);
                        ServiceUser u = new ServiceUser();
                        u.setId(phone);
                        u.setName(d.get("nickname"));
                        u.setImagePhoto(d.get("pic"));
                        result.add(u);

                    }
                    getActivity().runOnUiThread(() -> {
                        data.clear();
                        data.addAll(result);
                        adapter = new MessageFragmentAdapter(data,getActivity());
                        initAdapter();
                        LoadDialog.dismiss();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(() -> {
                        LoadDialog.dismiss();
                    });
                }
            });

    }

    //显示popupWindow
    private void showPopupWindow() {
        //获取自定义菜单的布局文件
        View popupWindow_view = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow_dialog, null, false);
        //创建popupWindow，设置宽度和高度
        popupWindow = new PopupWindow(popupWindow_view, 300, 600, true);
        LinearLayout lvshi = popupWindow_view.findViewById(R.id.lvshi);
        LinearLayout jinji = popupWindow_view.findViewById(R.id.jinji);
        LinearLayout normal_user = popupWindow_view.findViewById(R.id.normal_user);
        //内部控件的点击事件
        lvshi.setOnClickListener(this);
        jinji.setOnClickListener(this);
        normal_user.setOnClickListener(this);
        //设置菜单的显示位置
        popupWindow.showAsDropDown(qiehuan, -250, 20);
        //兼容5.0点击其他位置隐藏popupWindow
        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (popupWindow.isShowing()) {
                        popupWindow.dismiss();//隐藏菜单
                        popupWindow = null;//初始化菜单
                    }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:

                        break;
                }
                return false;
            }
        });
    }

    //显示律师和经纪人
    private void showPopupUser(List<ServiceUser> lvshiList,List<ServiceUser> jinjiList) {
        //获取自定义菜单的布局文件
        View popupWindow_view = LayoutInflater.from(getActivity()).inflate(R.layout.lawer_jinji_search, null, false);
        //创建popupWindow，设置宽度和高度
        popupWindow = new PopupWindow(popupWindow_view, 400, 600, true);
        ListView lvshi = popupWindow_view.findViewById(R.id.lvshi_list);
        ListView jinji = popupWindow_view.findViewById(R.id.jinji_list);
        simpleAdapter1 lvshiAdapter = new simpleAdapter1(lvshiList,getActivity());
        simpleAdapter1 jinjiAdapter = new simpleAdapter1(jinjiList,getActivity());
        lvshi.setAdapter(lvshiAdapter);
        jinji.setAdapter(jinjiAdapter);
        //内部控件的点击事件
        lvshi.setOnItemClickListener(( parent, view,position,id)->{
           // Toast.makeText(getActivity(),lvshiList.get(position).getId(),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), chatActivity.class);
            EventBus.getDefault().postSticky(lvshiList.get(position));
            startActivity(intent);
            popupWindow.dismiss();
        });
        jinji.setOnItemClickListener(( parent, view,position,id)->{
            //Toast.makeText(getActivity(),jinjiList.get(position).getId(),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), chatActivity.class);
            EventBus.getDefault().postSticky(jinjiList.get(position));
            startActivity(intent);
            popupWindow.dismiss();
        });
        //设置菜单的显示位置
        popupWindow.showAsDropDown(search, 10, 20);
        //兼容5.0点击其他位置隐藏popupWindow
        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();//隐藏菜单
                            popupWindow = null;//初始化菜单
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:

                        break;
                }
                return false;
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        User user = ((MyApplication)getActivity().getApplication()).getUser();
        int id = v.getId();
        switch (id){
            case R.id.lvshi:{
                if(user.getLawerFlag()!=1) {
                    Toast.makeText(getActivity(),"你还不是律师哦！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isLoginFlag) {
                    EMClient.getInstance().logout(true);
                }
                EMClient.getInstance().login(user.getLawerId(), user.getLawerpwd(), new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        getActivity().runOnUiThread(()->{
                            currentLoginUserId = user.getLawerId();
                            Toast.makeText(getActivity(),"切换律师成功！",Toast.LENGTH_SHORT).show();
                            EMClient.getInstance().chatManager().addMessageListener(MessageFragment.this);
                            conversations = EMClient.getInstance().chatManager().getAllConversations();
                            qiehuan.setBackground(getResources().getDrawable(R.drawable.lvshi));
                            onRefresh();
                        });

                        isLoginFlag = true;
                    }

                    @Override
                    public void onError(int i, String s) {
                        getActivity().runOnUiThread(()->{
                            Toast.makeText(getActivity(),"切换失败！",Toast.LENGTH_SHORT).show();

                        });

                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
                popupWindow.dismiss();
            }
            break;
            case R.id.jinji:{
                if(user.getAgentFlag()!=1) {
                    Toast.makeText(getActivity(),"你还不是经纪人哦！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isLoginFlag) {
                    EMClient.getInstance().logout(true);
                }
                EMClient.getInstance().login(user.getAgentId(), user.getAgentpwd(), new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        getActivity().runOnUiThread(()->{
                            currentLoginUserId = user.getAgentId();
                            Toast.makeText(getActivity(),"切换经纪人成功！",Toast.LENGTH_SHORT).show();
                            EMClient.getInstance().chatManager().addMessageListener(MessageFragment.this);
                            qiehuan.setBackground(getResources().getDrawable(R.drawable.jinji));
                            conversations = EMClient.getInstance().chatManager().getAllConversations();
                            onRefresh();
                        });
                        isLoginFlag = true;
                    }

                    @Override
                    public void onError(int i, String s) {
                        getActivity().runOnUiThread(()->{
                            Toast.makeText(getActivity(),"切换失败！",Toast.LENGTH_SHORT).show();
                        });

                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
                popupWindow.dismiss();
            }
            break;
            case R.id.normal_user:
                if(isLoginFlag) {
                    EMClient.getInstance().logout(true);
                }
                EMClient.getInstance().login(user.getPhone(), user.getPhone(), new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        getActivity().runOnUiThread(()->{
                            currentLoginUserId = user.getPhone();
                            Toast.makeText(getActivity(),"普通用户身份！",Toast.LENGTH_SHORT).show();
                            EMClient.getInstance().chatManager().addMessageListener(MessageFragment.this);
                            qiehuan.setBackground(getResources().getDrawable(R.drawable.normal_user));
                            conversations = EMClient.getInstance().chatManager().getAllConversations();
                            onRefresh();
                        });
                        isLoginFlag = true;
                    }

                    @Override
                    public void onError(int i, String s) {
                        getActivity().runOnUiThread(()->{
                            Toast.makeText(getActivity(),"切换失败！",Toast.LENGTH_SHORT).show();
                        });

                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
                popupWindow.dismiss();
                break;
        }
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {



        //更新会话列表
        conversations = EMClient.getInstance().chatManager().getAllConversations();
        if(conversations==null||conversations.size()==0) { return;}
        List<String> tdata = new ArrayList<>();
        Iterator it = conversations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            EMConversation value = (EMConversation) entry.getValue();
            tdata.add(key);
        }
        if(tdata.size()==0) return;
        data.clear();
        try {
            for (String phone : tdata) {
                String sql = "select * from user where  lawerId= ? or agentId = ? or phone = ? ";
                String[] columns = {phone, phone, phone};
                int[] type = {Types.CHAR, Types.CHAR, Types.CHAR};
                HashMap<String, String> d = DBUtils.query(columns, type, sql).get(0);
                if (d.size() == 0) return;
                ServiceUser u = new ServiceUser();
                u.setId(phone);
                u.setName(d.get("nickname"));
                u.setImagePhoto(d.get("pic"));
                data.add(u);
                getActivity().runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                });
            }
        }catch(Exception e){
            e.printStackTrace();
        }



    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }
}
