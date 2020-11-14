package activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.suyong.sunshineflat.MyApplication;
import com.suyong.sunshineflat.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import Util.LoadingDialog;
import Util.StringAndBitMapTools;
import adapter.MsgAdapter;
import pojo.Msg;
import pojo.ServiceUser;
import pojo.User;

public class chatActivity extends AppCompatActivity implements EMMessageListener {

    Bitmap receiveBitmap ;
    Bitmap sendBitmap;

    private String sendId;
    private ImageView touxiang;
    private TextView  userName;
    private RecyclerView recyclerView;
    private EditText mInputTextView;
    private Button send ;
    private List<Msg> msgList;
    private MsgAdapter adapter;
    private  String chatId ="";
    android.app.ProgressDialog loadDialog;

    private void init(){
        loadDialog = LoadingDialog.createLoadingDialog(this,"提示","加载中...");
        sendId = getIntent().getStringExtra("sendId");
        touxiang = findViewById(R.id.touxiang);
        userName = findViewById(R.id.name);
        msgList = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.text_content);
        mInputTextView = (EditText)findViewById(R.id.edit_message_input);
        send = (Button)findViewById(R.id.btn_send);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //聊天对象Id
        EventBus.getDefault().register(this);
    }

    private void refreshMessage() {
        //获取此会话的所有消息
            try {
                runOnUiThread(()->{loadDialog.show();});
                List<EMMessage> messages = EMClient.getInstance().chatManager().getConversation(chatId).getAllMessages();
                for (EMMessage message : messages) {
                    //Msg msg = new Msg(((EMTextMessageBody)message.getBody()).getMessage(),Msg.Type_Recived,receiveBitmap,sendBitmap);
                    String from = message.getStringAttribute("from");
                    String to  = message.getStringAttribute("to");
                    String content = ((EMTextMessageBody) message.getBody()).getMessage();
                    if(from==null||to==null) continue;
                    if(from.equals(chatId)){
                        Msg msg = new Msg(content,Msg.Type_Recived,receiveBitmap,sendBitmap);
                        msgList.add(msg);
                        adapter.notifyItemInserted(msgList.size()-1);
                        recyclerView.scrollToPosition(msgList.size()-1);
                    }else if(from.equals(sendId)){
                        Msg msg = new Msg(content,Msg.Type_Send,receiveBitmap,sendBitmap);
                        msgList.add(msg);
                        adapter.notifyItemInserted(msgList.size()-1);
                        recyclerView.scrollToPosition(msgList.size()-1);
                    }
                }
                loadDialog.dismiss();
            }catch(Exception e){
                e.printStackTrace();
                loadDialog.dismiss();
            }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)//
            // sticky =true,可以接收粘性事件。
    public void onEvent(ServiceUser user)  {
        init();
        receiveBitmap = StringAndBitMapTools.stringToBitmap(user.getImagePhoto());
        User user1 = ((MyApplication)getApplication()).getUser();
        sendBitmap = StringAndBitMapTools.stringToBitmap((user1.getPic()));
        touxiang.setImageBitmap(receiveBitmap);
        userName.setText(user.getName());
        chatId = user.getId();
        initEvent(chatId);
        refreshMessage();
    }

private void initEvent(String chatId) {
        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String content = mInputTextView.getText().toString();
                //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                if (!"".equals(content)) {
                    EMMessage message = EMMessage.createTxtSendMessage(content, chatId);
                    message.setAttribute("from",sendId);
                    message.setAttribute("to",chatId);
                    //如果是群聊，设置chattype，默认是单聊
                    message.setChatType(EMMessage.ChatType.Chat);
                    //发送消息
                    EMClient.getInstance().chatManager().sendMessage(message);
                    message.setMessageStatusCallback(new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            showToast("send sucess");
                            //进行界面的刷新
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Msg msg = new Msg(content,Msg.Type_Send,receiveBitmap,sendBitmap);
                                    msgList.add(msg);
                                    adapter.notifyItemInserted(msgList.size()-1);
                                    recyclerView.scrollToPosition(msgList.size()-1);
                                    mInputTextView.setText("");
                                }
                            });


                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e("Art", i + "," + s);
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                }
            }
        });
    }

    private void showToast(String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(chatActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        for (EMMessage message : list) {
            try {
                //Msg msg = new Msg(((EMTextMessageBody)message.getBody()).getMessage(),Msg.Type_Recived,receiveBitmap,sendBitmap);
                String from = message.getStringAttribute("from");
                String to = message.getStringAttribute("to");
                String content = ((EMTextMessageBody) message.getBody()).getMessage();
                if(from==null||to==null) continue;
                if (from.equals(chatId)&&to.equals(sendId)) {
                    Msg msg = new Msg(content, Msg.Type_Recived, receiveBitmap, sendBitmap);
                    msgList.add(msg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(msgList.size() - 1);
                            recyclerView.scrollToPosition(msgList.size() - 1);
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(this);
        EventBus.getDefault().unregister(this);
    }
}
