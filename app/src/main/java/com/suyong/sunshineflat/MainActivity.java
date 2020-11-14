package com.suyong.sunshineflat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.hb.dialog.dialog.ConfirmDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import Fragment.BaseFragment;
import Fragment.HomeFragment;
import Fragment.LoveFragment;
import Fragment.MessageFragment;
import Fragment.PersonFragment;
import Util.AlterDialogUtils;
import Util.StorgeUtils;
import rxPermission.RxPermissionConsumer;
import rxPermission.RxPermissionManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<BaseFragment> fragmentList = new ArrayList<>();
    private String[] mFragmentTagList = {"homeFragment", "loveFragment","messageFragment", "personFragment"};
    private FragmentManager fragmentManager;
    private MyApplication app;
    StorgeUtils utils;
    private BaseFragment CurrentFragment = null;
    private RelativeLayout homePage,lovePage,messagePage,personPage;
    private RadioButton Btn_homePage,Btn_lovePage,Btn_messagePage,Btn_personPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        RxPermissionManager.requestStoragePermissions(new RxPermissions(this) ,new RxPermissionConsumer(){
            @Override
            public void agree() {
                utils = new StorgeUtils(MainActivity.this);
                utils.copyDrawableToSd(MainActivity.this,R.drawable.pic_default);
            }

            @Override
            public void refuse() {
                ConfirmDialog confirmDialog = AlterDialogUtils.createTipDialog(MainActivity.this,"为了用户更好的体验，你需要通过此权限！");
                confirmDialog.setClickListener(new ConfirmDialog.OnBtnClickListener() {
                    @Override
                    public void ok() {
                        confirmDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void cancel() {
                        confirmDialog.dismiss();
                        finish();
                    }
                });
                confirmDialog.show();
            }

            @Override
            public void handOpen() {
                ConfirmDialog confirmDialog = AlterDialogUtils.createTipDialog(MainActivity.this,"你上次选择了不在提示，现在您需要去设置,手动开启权限！");
                confirmDialog.setClickListener(new ConfirmDialog.OnBtnClickListener() {
                    @Override
                    public void ok() {
                        confirmDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void cancel() {
                        confirmDialog.dismiss();
                        finish();
                    }
                });
                confirmDialog.show();
            }

        });
    }

    private void initView() {

        app = (MyApplication)getApplication();
        homePage = findViewById(R.id.homePage);
        lovePage = findViewById(R.id.lovePage);
        messagePage = findViewById(R.id.messagePage);
        personPage = findViewById(R.id.personPage);
        Btn_homePage = findViewById(R.id.homePage_button);
        Btn_lovePage = findViewById(R.id.lovePage_button);
        Btn_messagePage = findViewById(R.id.messagePage_button);
        Btn_personPage = findViewById(R.id.personPage_button);
        homePage.setOnClickListener(this);
        lovePage.setOnClickListener(this);
        messagePage.setOnClickListener(this);
        personPage.setOnClickListener(this);
        //初始化界面
        HomeFragment homeFragment = new HomeFragment();
        LoveFragment loveFragment =  new LoveFragment();
        MessageFragment messageFragment= new MessageFragment();
        PersonFragment personFragment=new PersonFragment();
        fragmentList.add(0,homeFragment);
        fragmentList.add(1,loveFragment);
        fragmentList.add(2,messageFragment);
        fragmentList.add(3,personFragment);
        CurrentFragment = fragmentList.get(0);
        //初始化首次进入时的Fragment
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, CurrentFragment, mFragmentTagList[0]);
        transaction.commitAllowingStateLoss();
        changeState(true,false,false,false);
    }

    // 转换Fragment
    void switchFragment(BaseFragment to, String tag){
        if(CurrentFragment != to){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(!to.isAdded()){
                // 没有添加过:
                // 隐藏当前的，添加新的，显示新的
                transaction.hide(CurrentFragment).add(R.id.fragment_container, to, tag).show(to);
            }else{
                // 隐藏当前的，显示新的
                transaction.hide(CurrentFragment).show(to);
            }
            CurrentFragment = to;
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.homePage:
                changeState(true,false,false,false);
                switchFragment(fragmentList.get(0), mFragmentTagList[0]);
                break;
            case R.id.lovePage:
                switchFragment(fragmentList.get(1), mFragmentTagList[1]);
                changeState(false,true,false,false);
                break;
            case R.id.messagePage:
                if(app.getUser()!=null) {
                    switchFragment(fragmentList.get(2), mFragmentTagList[2]);
                    changeState(false, false, true, false);
                }else{
                    startActivity(new Intent(this,LoginActivity.class));
                }
                break;
            case R.id.personPage:
                switchFragment(fragmentList.get(3), mFragmentTagList[3]);
                changeState(false,false,false,true);
                break;
        }

    }

    /**
     *刷新bottom_banner里的按钮的选中状态
     * @param b 主页
     * @param b1 推荐
     * @param b2 消息
     * @param b3 我的
     */
    private void changeState(boolean b, boolean b1, boolean b2, boolean b3) {
        Btn_homePage.setChecked(b);
        Btn_lovePage.setChecked(b1);
        Btn_messagePage.setChecked(b2);
        Btn_personPage.setChecked(b3);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}

//        refreshLayout = findViewById(R.id.refreshable_view);
//        listView = findViewById(R.id.list_view);
//        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
//        listView.setAdapter(adapter);
//        refreshLayout.initAdapter(adapter);
//        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.PullToRefreshListener() {
//            @Override
//            public void onRefresh() {
//                try {
//                    Thread.sleep(3000);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            items.add("suyong");
//                            adapter.notifyDataSetChanged();
//                        }
//                    });
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                refreshLayout.finishRefreshing();
//            }
//        },0);