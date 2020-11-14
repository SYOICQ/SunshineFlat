package com.suyong.sunshineflat;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Types;

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

public class RegisterActivity extends AppCompatActivity {

    private Dialog progressDialog ;
    private String code;
    final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000,1000);
    private String flag = "";

    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.register_phone_number)
    EditText register_phone_number;
    @BindView(R.id.register_Code)
    EditText register_Code;
    @BindView(R.id.register_password)
    EditText register_password;
    @BindView(R.id.register_protocol)
    RadioButton register_protocol;
    @BindView(R.id.send_Code_request)
    TextView send_Code_request;
    @BindView(R.id.btn_register)
    Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);
        StatusBarUtils.setStatusBarMode(this,true,android.R.color.white);
        ButterKnife.bind(this);
        btn_register.setClickable(false);
        initEvent();
    }

    private void initEvent() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send_Code_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(register_phone_number.getText().toString().trim())) {
                    if(register_phone_number.getText().toString().trim().length()<11){
                        Toast.makeText(RegisterActivity.this,"电话号码格式错误！",Toast.LENGTH_SHORT).show();
                    }
                    code = RadomNumber.randomCode();
                    RadomNumber.sendCode(register_phone_number.getText().toString().trim(),code).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
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
                                    Toast.makeText(RegisterActivity.this,"发送成功！",Toast.LENGTH_SHORT).show();
                                    myCountDownTimer.start();
                                }else{
                                    Toast.makeText(RegisterActivity.this,"发送失败！",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    Toast.makeText(RegisterActivity.this,"请输入电话号码！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        register_protocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btn_register.setClickable(true);
                    btn_register.setBackgroundResource(R.drawable.login_confirm_focus);
                }else{
                    btn_register.setClickable(false);
                    btn_register.setBackgroundResource(R.drawable.login_confirm_no_focus);
                }
            }
        });
        register_protocol.setOnClickListener(new View.OnClickListener() {
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
        btn_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(register_phone_number.getText().toString().trim())
                        ||TextUtils.isEmpty(register_Code.getText().toString().trim())
                ||TextUtils.isEmpty(register_password.getText().toString().trim())){
                    Toast.makeText(RegisterActivity.this,"请完善您的填写！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!code.equals(register_Code.getText().toString().trim())) {Toast.makeText(RegisterActivity.this,"验证码错误！",Toast.LENGTH_SHORT).show();return;}
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(()->{
                            progressDialog = LoadingDialog.createLoadingDialog(RegisterActivity.this, "登录中...");
                            progressDialog.show();
                        });
                        try {
                                StorgeUtils utils = new StorgeUtils(RegisterActivity.this);
                                //需要创建新的用户
                                String[] column1 ={register_phone_number.getText().toString().trim(),
                                        register_password.getText().toString().trim(),"暂无昵称",
                                        utils.getCachePath()+"jpg/"+R.drawable.pic_default+".png","0","0","0","0","0","0","0"};
                                int[] type1 = {Types.CHAR,Types.CHAR,Types.CHAR,Types.BLOB,Types.INTEGER,Types.INTEGER,Types.INTEGER,Types.CHAR,Types.CHAR,Types.CHAR,Types.CHAR};
                                String sql2 = "insert into user values(?,?,?,?,?,?,?,?,?,?,?)";
                                DBUtils.CUD(column1,type1,sql2);
                                User user = new User();
                                user.setPhone(column1[0]);
                                user.setPassword(column1[1]);
                                user.setNickname(column1[2]);
                                user.setPic(column1[3]);
                                ((MyApplication)getApplication()).setUser(user);
                                EMClient.getInstance().createAccount(user.getPhone(),user.getPhone());
                                Log.d("user:",user.toString());
                                runOnUiThread(()->{
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(()->{
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this,"注册失败！",Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                };
                MyThreadPool.getInstance().submit(runnable);

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            send_Code_request.setClickable(false);
            send_Code_request.setTextColor(Color.rgb(121, 210, 249));
            send_Code_request.setText(l/1000+"s");
        }
        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            send_Code_request.setText("获取验证码");
            //设置可点击
            send_Code_request.setClickable(true);
            send_Code_request.setTextColor(Color.parseColor("#80000000"));
        }
    }
}
