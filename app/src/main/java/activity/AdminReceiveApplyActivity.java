package activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.suyong.sunshineflat.MyApplication;
import com.suyong.sunshineflat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Util.DBUtils;
import Util.LoadingDialog;
import Util.MyThreadPool;
import Util.RadomNumber;
import Util.StatusBarUtils;
import adapter.AdminReceiveApplyAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import pojo.Apply;
import pojo.User;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 实现管理员查看申请
 */
public class AdminReceiveApplyActivity extends AppCompatActivity {

    private ProgressDialog LoadDialog;

    List<Apply> data = new ArrayList<>();

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.data_Empty)
    LinearLayout empty_view;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_admin_receive);
        //绑定初始化ButterKnife
        ButterKnife.bind(this);
        StatusBarUtils.setStatusBarMode(this,true,R.color.flatDetail_ActionBar);
        LoadDialog = LoadingDialog.createLoadingDialog(this,"提示","加载中...");
        initEvent();
        Onfresh();
    }

    private void initEvent() {
        back.setOnClickListener((v)->{
            finish();
        });
    }

    private void Onfresh() {
        data.clear();
        MyThreadPool.getInstance().submit(()->{
           runOnUiThread(()->{
               LoadDialog.show();
           });
           try{
                String sql = "select * from ApplyList";
               ArrayList<HashMap<String,String>> d= DBUtils.query(null,null,sql);
                if(d.size()>0){
                    for(HashMap<String,String> t:d){
                        Apply apply = new Apply();
                        apply.setPhone(t.get("phone"));
                        apply.setReson(t.get("content"));
                        apply.setPic(t.get("pic"));
                        apply.setAdminFlag(Integer.parseInt(t.get("admin_flag")));
                        apply.setLawerFlag(Integer.parseInt(t.get("lawer_flag")));
                        apply.setAgentFlag(Integer.parseInt(t.get("agent_flag")));
                        data.add(apply);
                       //Log.d("AdminReceiveApplyActivi", data.toString());
                    }
                    runOnUiThread(()->{
                        User user = ((MyApplication)getApplication()).getUser();
                        AdminReceiveApplyAdapter adapter = new AdminReceiveApplyAdapter(this,data);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        adapter.setOnclicklistener((Apply apply, String flag) ->{
                            if("agree".equals(flag)){
                                String response = "恭喜您，成功通过审核！";
                                RadomNumber.sendCode(apply.getPhone(),response).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d("AdminReceiveApplyActivi", e.getMessage().toString());
                                    }

                                    @Override
                                    public void onNext(String s) {
                                        editUser(apply);
                                    }
                                });
                                deleteCurrentApply(apply.getPhone());
                            }else if("refuse".equals(flag)){
                                String response = "对不起，遗憾的通知您没有通过审核!";
                                RadomNumber.sendCode(apply.getPhone(),response).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d("AdminReceiveApplyActivi", e.getMessage().toString());
                                    }

                                    @Override
                                    public void onNext(String s) {
                                        try {
                                            JSONObject json = new JSONObject(s);
                                            if(json.getInt("code")==0){
                                                Toast.makeText(AdminReceiveApplyActivity.this,"已发送拒绝短信！",Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(AdminReceiveApplyActivity.this,"发送失败！"+json.getInt("code"),Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                deleteCurrentApply(apply.getPhone());
                            }
                        });
                        recyclerView.setAdapter(adapter);
                        LoadDialog.dismiss();
                        recyclerView.setVisibility(View.VISIBLE);
                        empty_view.setVisibility(View.GONE);
                    });
                }else{
                    runOnUiThread(()->{
                        LoadDialog.dismiss();
                        recyclerView.setVisibility(View.GONE);
                        empty_view.setVisibility(View.VISIBLE);
                    });
                }
           } catch (Exception e){
               e.printStackTrace();
               runOnUiThread(()->{
                   LoadDialog.dismiss();
                   Toast.makeText(this,"查询失败!",Toast.LENGTH_SHORT).show();
               });
           }

        });
    }

    private void editUser(Apply apply) {
        MyThreadPool.getInstance().submit(()->{
            try {
                String sql = "";
                String[] columns = new String[]{"1",apply.getPhone()};
                int[] types = new int[]{Types.INTEGER,Types.CHAR};
                if(apply.getAdminFlag()==1) {
                    sql = "update user set admin_flag = ? where phone = ?";
                } else if(apply.getLawerFlag()==1) {
                    sql = "update user set lawer_flag = ? where phone = ?";
                } else if(apply.getAgentFlag()==1) {
                    sql = "update user set agent_flag = ? where phone = ?";
                }
                DBUtils.CUD(columns, types, sql);
                //还要为用户分配环信的账号和密码
                if(apply.getAdminFlag()!=1) {
                    String name = apply.getPhone() + RadomNumber.randomCode();
                    String pwd = apply.getPhone() + RadomNumber.randomCode();
                    EMClient.getInstance().createAccount(name, pwd);
                    String sql1 = "";
                    String[] columns1 = new String[]{name, pwd, apply.getPhone()};
                    int[] types1 = new int[]{Types.CHAR, Types.CHAR, Types.CHAR};
                    if (apply.getLawerFlag() == 1) {
                        sql1 = "update user set lawerId = ?,lawerpwd = ? where phone = ?";
                    } else if (apply.getAgentFlag() == 1) {
                        sql1 = "update user set agentId = ? ,agentpwd = ? where phone = ?";
                    }
                    DBUtils.CUD(columns1, types1, sql1);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    private void deleteCurrentApply(String phone) {
        MyThreadPool.getInstance().submit(()->{
            try {
                String[] columns = new String[]{phone};
                int[] types = new int[]{Types.CHAR};
                String sql = "delete from ApplyList where phone = ?";
                DBUtils.CUD(columns, types, sql);
                runOnUiThread(()->{
                    Onfresh();
                });
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

}
