package com.suyong.sunshineflat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import Util.DBUtils;
import Util.LoadingDialog;
import Util.MyThreadPool;
import Util.RadomNumber;
import Util.StatusBarUtils;
import Util.StorgeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import pojo.User;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class LoginActivity extends AppCompatActivity {

    private Dialog progressDialog ;
    private String code;
    private String flag = "";
    final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000,1000);
    private boolean account_passwd_flag = false;
    @BindView(R.id.btn_register)
    TextView btn_register;
    @BindView(R.id.send_Code_request)
    TextView btn_send_codeRequest;
    @BindView(R.id.account_login)
    TextView btn_account_passwd_login;
    @BindView(R.id.login_phone_number)
    EditText login_number;
    @BindView(R.id.login_Code)
    EditText login_Code;
    @BindView(R.id.btn_login)
    Button login;
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.login_title_image)
    ImageView title_image;
    @BindView(R.id.title_tip)
    TextView title_tip;
    @BindView(R.id.login_protocol)
    RadioButton login_protocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        StatusBarUtils.setStatusBarMode(this,true,android.R.color.white);
        ButterKnife.bind(this);
        initEvent();
        login.setClickable(false);
        InputFilter[] filters = {new MyInputFilter("0123456789")};
        login_Code.setFilters(filters);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void initEvent() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        btn_send_codeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(login_number.getText().toString().trim())) {
                    if(login_number.getText().toString().trim().length()<11){
                        Toast.makeText(LoginActivity.this,"电话号码格式错误！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    code = RadomNumber.randomCode();
                    RadomNumber.sendCode(login_number.getText().toString().trim(),code).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(String s) {
                            try {
                                JSONObject json = new JSONObject(s);
                                if(json.getInt("code")==0){
                                    Toast.makeText(LoginActivity.this,"发送成功！",Toast.LENGTH_SHORT).show();
                                    myCountDownTimer.start();
                                }else{
                                    Toast.makeText(LoginActivity.this,"发送失败！",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this,"请输入电话号码！",Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(account_passwd_flag){
                    //用户名密码登陆
                    if(login_protocol.isChecked()){
                        if(TextUtils.isEmpty(login_number.getText().toString().trim())
                                ||TextUtils.isEmpty(login_Code.getText().toString().trim())){
                            Toast.makeText(LoginActivity.this,"请完善您的填写！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                String[] column = {login_number.getText().toString().trim(),login_Code.getText().toString().trim()};
                                int[] type = {Types.CHAR,Types.CHAR};
                                String sql = "select * from user where phone =? and password=?";
                                runOnUiThread(()->{
                                    progressDialog = LoadingDialog.createLoadingDialog(LoginActivity.this, "登录中...");
                                    progressDialog.show();
                                });
                                try {
                                    ArrayList<HashMap<String,String>> data = DBUtils.query(column,type,sql);
                                    if(data.size()>0){
                                        HashMap<String,String> t= data.get(0);
                                        User user = new User();
                                        user.setPhone(t.get("phone"));
                                        user.setPassword(t.get("password"));
                                        user.setNickname(t.get("nickname"));
                                        user.setPic(t.get("pic"));
                                        user.setAdminFlag(Integer.parseInt(t.get("admin_flag")));
                                        user.setLawerFlag(Integer.parseInt(t.get("lawer_flag")));
                                        user.setAgentFlag(Integer.parseInt(t.get("agent_flag")));
                                        user.setLawerId(t.get("lawerId"));
                                        user.setLawerpwd(t.get("lawerpwd"));
                                        user.setAgentId(t.get("agentId"));
                                        user.setAgentpwd(t.get("agentpwd"));

                                        Log.d("user:",user.toString());
                                        runOnUiThread(()->{
                                            ((MyApplication)getApplication()).setUser(user);
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().post("刷新");
                                            finish();
                                        });
                                    }else{
                                        Toast.makeText(LoginActivity.this,"账号不存在或密码错误！",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    runOnUiThread(()->{
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this,"登录失败！",Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        };
                        MyThreadPool.getInstance().submit(runnable);
                    }else{
                        Toast.makeText(LoginActivity.this,"请先同意协议！",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //手机号验证码登录
                    if(login_protocol.isChecked()){
                        if(TextUtils.isEmpty(login_number.getText().toString().trim())
                                ||TextUtils.isEmpty(login_Code.getText().toString().trim())){
                            Toast.makeText(LoginActivity.this,"请完善您的填写！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(login_Code.getText().toString().trim().length()>6||!code.equals(login_Code.getText().toString().trim())) {Toast.makeText(LoginActivity.this,"验证码错误！",Toast.LENGTH_SHORT).show();return;}
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                String[] column = {login_number.getText().toString().trim()};
                                int[] type = {Types.CHAR};
                                String sql = "select phone from user where phone =?";
                                runOnUiThread(()->{
                                    progressDialog = LoadingDialog.createLoadingDialog(LoginActivity.this, "登录中...");
                                    progressDialog.show();
                                });
                                try {
                                    ArrayList<HashMap<String,String>> data = DBUtils.query(column,type,sql);
                                    if(data.size()==0){
                                        StorgeUtils utils = new StorgeUtils(LoginActivity.this);
                                       //需要创建新的用户(账号密码初始时相同,头像系统自动上传，后期可以自己上传头像修改)
                                        String[] column1 ={login_number.getText().toString().trim(),
                                                login_number.getText().toString().trim(),"暂无昵称",
                                                utils.getCachePath()+"jpg/"+R.drawable.pic_default+".png","0","0","0","0","0","0","0"};
                                        int[] type1 = {Types.CHAR,Types.CHAR,Types.CHAR,Types.BLOB,Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.CHAR,Types.CHAR,Types.CHAR,Types.CHAR};
                                        String sql2 = "insert into user values(?,?,?,?,?,?,?,?,?,?,?)";
                                        DBUtils.CUD(column1,type1,sql2);
                                        String sql3 = "select * from user where phone =?";
                                        ArrayList<HashMap<String,String>> user_data = DBUtils.query(column,type,sql3);
                                        HashMap<String,String> ll = user_data.get(0);
                                        User user = new User();
                                        user.setPhone(ll.get("phone"));
                                        user.setPassword(ll.get("password"));
                                        user.setNickname(ll.get("nickname"));
                                        user.setPic(ll.get("pic"));
                                        user.setAdminFlag(Integer.parseInt(ll.get("admin_flag")));
                                        user.setLawerFlag(Integer.parseInt(ll.get("lawer_flag")));
                                        user.setAgentFlag(Integer.parseInt(ll.get("agent_flag")));
                                        user.setLawerId(ll.get("lawerId"));
                                        user.setLawerpwd(ll.get("lawerpwd"));
                                        user.setAgentId(ll.get("agentId"));
                                        user.setAgentpwd(ll.get("agentpwd"));
                                        EMClient.getInstance().createAccount(user.getPhone(),user.getPhone());
                                        Log.d("user:",user.toString());
                                        runOnUiThread(()->{
                                            ((MyApplication)getApplication()).setUser(user);
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().post("刷新");
                                            finish();
                                        });
                                    }else{
                                       //需要加载用户信息
                                        String sql1 = "select * from user where phone =?";
                                        HashMap<String,String> tmp = DBUtils.query(column,type,sql1).get(0);
                                        User user = new User();
                                        user.setNickname(tmp.get("nickname"));
                                        user.setPassword(tmp.get("password"));
                                        user.setPhone(tmp.get("phone"));
                                        user.setPic(tmp.get("pic"));
                                        user.setAdminFlag(Integer.parseInt(tmp.get("admin_flag")));
                                        user.setLawerFlag(Integer.parseInt(tmp.get("lawer_flag")));
                                        user.setAgentFlag(Integer.parseInt(tmp.get("agent_flag")));
                                        user.setLawerId(tmp.get("lawerId"));
                                        user.setLawerpwd(tmp.get("lawerpwd"));
                                        user.setAgentId(tmp.get("agentId"));
                                        user.setAgentpwd(tmp.get("agentpwd"));

                                        Log.d("user!!:",user.toString());
                                        runOnUiThread(()->{
                                            ((MyApplication)getApplication()).setUser(user);
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().post("刷新");
                                            finish();
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    runOnUiThread(()->{
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this,"登录失败！",Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        };
                        MyThreadPool.getInstance().submit(runnable);
                    }else{
                        Toast.makeText(LoginActivity.this,"请先同意协议！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_account_passwd_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initThing();
            }
        });
        login_protocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    login.setClickable(true);
                    login.setBackgroundResource(R.drawable.login_confirm_focus);
                }else{
                    login.setClickable(false);
                    login.setBackgroundResource(R.drawable.login_confirm_no_focus);
                }
            }
        });
        login_protocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("suyong".equals(flag)){
                    ((RadioButton)v).setChecked(false);
                    flag="";
                }else{
                    ((RadioButton)v).setChecked(true);
                    flag="suyong";
                }
            }
        });
    }

    private void initThing() {
        account_passwd_flag = true;
        login_Code.setHint("请输入密码(字母加数字)");
        title_tip.setVisibility(View.GONE);
        title_image.setImageResource(R.drawable.title_image);
        btn_send_codeRequest.setVisibility(View.GONE);
        InputFilter[] filters = {new MyInputFilter("qwertyuiopasdfghjklzxcvbnm0123456789")};
        login_Code.setFilters(filters);
    }

    public class MyInputFilter extends LoginFilter.UsernameFilterGeneric {
        private String mAllowedDigits;

        public MyInputFilter( String digits ) {
            mAllowedDigits = digits;
        }

        @Override
        public boolean isAllowed(char c) {
            if (mAllowedDigits.indexOf(c) != -1) {
                return true;
            }
            return false;
        }
    }

    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            btn_send_codeRequest.setClickable(false);
            btn_send_codeRequest.setTextColor(Color.rgb(121, 210, 249));
            btn_send_codeRequest.setText(l/1000+"s");
        }
        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            btn_send_codeRequest.setText("获取验证码");
            //设置可点击
            btn_send_codeRequest.setClickable(true);
            btn_send_codeRequest.setTextColor(Color.parseColor("#80000000"));
        }
    }

}
