package Fragment;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.dialog.dialog.ConfirmDialog;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.jaeger.library.StatusBarUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.suyong.sunshineflat.LoginActivity;
import com.suyong.sunshineflat.MyApplication;
import com.suyong.sunshineflat.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import CustomView.MyScrollView;
import Util.AlterDialogUtils;
import Util.DBUtils;
import Util.LoadingDialog;
import Util.MyThreadPool;
import Util.StatusBarUtils;
import Util.StringAndBitMapTools;
import activity.AdminReceiveApplyActivity;
import activity.MyListActivity;
import activity.UserApplyActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pojo.User;
import rxPermission.RxPermissionConsumer;
import rxPermission.RxPermissionManager;

public class PersonFragment extends  BaseFragment {
    private static final int CHOOSE_PHOTO = 1;

    private Unbinder unbinder;
    private Handler mHandler;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    @BindView(R.id.scrollView)
    MyScrollView scrollView;

    @BindView(R.id.llTitle)
    FrameLayout title;


    @BindView(R.id.new_flat)
    TextView new_flat;
    @BindView(R.id.ershou_flat)
    TextView ershou_flat;
    @BindView(R.id.foreign_flat)
    TextView foreign_flat;
    @BindView(R.id.zufang_flat)
    TextView zufang_flat;
    @BindView(R.id.my_pinjia)
    TextView my_pinjia;
    @BindView(R.id.info_detail)
    TextView info_detail;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.touxiang)
    ImageView touxiang;
    @BindView(R.id.apply_list)
    ImageView apply_list;
    @BindView(R.id.my_lvshi)
    TextView my_lvshi;
    @BindView(R.id.my_jinji)
    TextView my_jinji;
    @BindView(R.id.my_admin)
    TextView my_admin;

    private ProgressDialog LoadDialog;

    private boolean flag = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StatusBarUtil.setColor(getActivity(),Color.parseColor("#0099FF"));
        View view=inflater .inflate(R.layout.person_fragment ,container,false) ;
        unbinder = ButterKnife.bind(this, view);
        initView();
        initEvent();
        Log.d("PersonFragment", "onCreateView");
        onRefresh();
        EventBus.getDefault().register(this);
        return view;
    }

    private void initView() {
        LoadDialog = LoadingDialog.createLoadingDialog(getActivity(),"提示","加载中...");
        mHandler = new Handler();
        //设置  Header 为 Material风格
        mSmartRefreshLayout.setRefreshHeader(new MaterialHeader(getContext()).setShowBezierWave(true));
        mSmartRefreshLayout.setEnableLoadMore(false);
        /*下拉刷新*/
        mSmartRefreshLayout.setOnRefreshListener((refreshLayout) ->{
                    //延时展示，延时2秒
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mSmartRefreshLayout!=null) {
                                onRefresh();
                                mSmartRefreshLayout.finishRefresh();
                            }
                        }
                    },2000);
                }
        );
    }

    private void initEvent() {
        title.setAlpha(0);
        scrollView.setScrollViewListener(( scrollView,x,y,oldx,oldy)->{
            //            y表示当前滑动条的纵坐标
            //            oldy表示前一次滑动的纵坐标
                if(y<500) {
                    float alpha = ((float) y) / 450;
                    title.setAlpha(alpha);
                    StatusBarUtils.setStatusBarMode(getActivity(),true,android.R.color.white);
                }else if(y>=500){
                    StatusBarUtil.setColor(getActivity(),Color.parseColor("#0099FF"));
                }
        });

        username.setOnClickListener((v)->{
            if(((MyApplication)getActivity().getApplication()).getUser()==null){
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }else{
                User u = ((MyApplication)getActivity().getApplication()).getUser();
                final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(getActivity()).builder()
                        .setTitle("请输入新的用户名")
                        .setEditText(u.getNickname());
                myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            MyThreadPool.getInstance().submit(()->{
                                try {
                                    getActivity().runOnUiThread(()->{
                                        LoadDialog.show();
                                    });
                                    String newName = myAlertInputDialog.getResult();
                                    String[] column = {newName,u.getPhone()};
                                    int[] type = {Types.CHAR,Types.CHAR};
                                    String sql = "update user set nickname = ? where phone = ?";
                                    DBUtils.CUD(column,type,sql);
                                    getActivity().runOnUiThread(()->{
                                        LoadDialog.dismiss();
                                        myAlertInputDialog.dismiss();
                                        onRefresh();
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    getActivity().runOnUiThread(()->{
                                        LoadDialog.dismiss();
                                        Toast.makeText(getActivity(),"更新失败！",Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });

                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myAlertInputDialog.dismiss();
                    }
                });
                myAlertInputDialog.show();
            }
        });
        //新房点击
         new_flat.setOnClickListener((v) ->{
             User user = ((MyApplication)getActivity().getApplication()).getUser();
             if(user==null){Toast.makeText(getActivity(),"请先登录!",Toast.LENGTH_SHORT).show();return;}
                Intent intent = new Intent(getActivity(), MyListActivity.class);
                intent.putExtra("title","新房源浏览记录");
                intent.putExtra("phone",user.getPhone());
                intent.putExtra("type","楼盘");
                startActivity(intent);
         });
         //二手房点击
         ershou_flat.setOnClickListener((v) ->{
             User user = ((MyApplication)getActivity().getApplication()).getUser();
             if(user==null){Toast.makeText(getActivity(),"请先登录!",Toast.LENGTH_SHORT).show();return;}
             Intent intent = new Intent(getActivity(), MyListActivity.class);
             intent.putExtra("title","二手房源浏览记录");
             intent.putExtra("phone",user.getPhone());
             intent.putExtra("type","二手房");
             startActivity(intent);
         });
         //国外点击
         foreign_flat.setOnClickListener((v) ->{
             User user = ((MyApplication)getActivity().getApplication()).getUser();
             if(user==null){Toast.makeText(getActivity(),"请先登录!",Toast.LENGTH_SHORT).show();return;}
             Intent intent = new Intent(getActivity(), MyListActivity.class);
             intent.putExtra("title","国外房源浏览记录");
             intent.putExtra("phone",user.getPhone());
             intent.putExtra("type","国外楼盘");
             startActivity(intent);
         });
         //租房点击
         zufang_flat.setOnClickListener((v) ->{
             User user = ((MyApplication)getActivity().getApplication()).getUser();
             if(user==null){Toast.makeText(getActivity(),"请先登录!",Toast.LENGTH_SHORT).show();return;}
             Intent intent = new Intent(getActivity(), MyListActivity.class);
             intent.putExtra("title","租房源浏览记录");
             intent.putExtra("phone",user.getPhone());
             intent.putExtra("type","租房");
             startActivity(intent);
         });
        //我喜欢的点击
         my_pinjia.setOnClickListener((v) ->{
             User user = ((MyApplication)getActivity().getApplication()).getUser();
             if(user==null){Toast.makeText(getActivity(),"请先登录!",Toast.LENGTH_SHORT).show();return;}
             Intent intent = new Intent(getActivity(), MyListActivity.class);
             intent.putExtra("title","我喜欢的浏览记录");
             intent.putExtra("phone",user.getPhone());
             intent.putExtra("type","我喜欢");
             startActivity(intent);
         });
         //修改密码点击
         info_detail.setOnClickListener((v) ->{
             if(((MyApplication)getActivity().getApplication()).getUser()==null){
                 Toast.makeText(getActivity(),"请先登陆!",Toast.LENGTH_SHORT).show();
             }else{
                 User u = ((MyApplication)getActivity().getApplication()).getUser();
                 final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(getActivity()).builder()
                         .setTitle("请输入新的密码(字母加数字)")
                         .setEditText(u.getPassword());
                 myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         MyThreadPool.getInstance().submit(()->{
                             try {
                                 getActivity().runOnUiThread(()->{
                                     LoadDialog.show();
                                 });
                                 String newPassword = myAlertInputDialog.getResult();
                                 String[] column = {newPassword,u.getPhone()};
                                 int[] type = {Types.CHAR,Types.CHAR};
                                 String sql = "update user set password = ? where phone = ?";
                                 DBUtils.CUD(column,type,sql);
                                 getActivity().runOnUiThread(()->{
                                     LoadDialog.dismiss();
                                     myAlertInputDialog.dismiss();
                                     onRefresh();
                                     Toast.makeText(getActivity(),"修改成功！",Toast.LENGTH_SHORT).show();
                                 });

                             } catch (Exception e) {
                                 e.printStackTrace();
                                 getActivity().runOnUiThread(()->{
                                     LoadDialog.dismiss();
                                     Toast.makeText(getActivity(),"更新失败！",Toast.LENGTH_SHORT).show();
                                 });
                             }
                         });

                     }
                 }).setNegativeButton("取消", new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         myAlertInputDialog.dismiss();
                     }
                 });
                 myAlertInputDialog.show();
             }
         });
         //头像点击
         touxiang.setOnClickListener((v)->{
             if(((MyApplication)getActivity().getApplication()).getUser()!=null) {
                 checkPermission();
             }
         });
         //拥有管理员权限的人才有的申请申请列表的功能
        apply_list.setOnClickListener((v)->{
            startActivity(new Intent(getActivity(), AdminReceiveApplyActivity.class));
        });
        //律师认证
         my_lvshi.setOnClickListener((v)->{
             User user = ((MyApplication)getActivity().getApplication()).getUser();
             if(user==null){ Toast.makeText(getActivity(),"请先登陆!",Toast.LENGTH_SHORT).show();return;}
             if(user.getLawerFlag()==1){Toast.makeText(getActivity(),"你已经是律师了哦!",Toast.LENGTH_SHORT).show();return;}
             openUserApplyActivity(user.getPhone(),"律师认证","律师认证");
         });
        //经纪人认证
         my_jinji.setOnClickListener((v)->{
             User user = ((MyApplication)getActivity().getApplication()).getUser();
             if(user==null){ Toast.makeText(getActivity(),"请先登陆!",Toast.LENGTH_SHORT).show();return;}
             if(user.getAgentFlag()==1){Toast.makeText(getActivity(),"你已经是经纪人了哦!",Toast.LENGTH_SHORT).show();return;}
             openUserApplyActivity(user.getPhone(),"经纪人认证","经纪人认证");
         });
        //管理员认证
         my_admin.setOnClickListener((v)->{
             User user = ((MyApplication)getActivity().getApplication()).getUser();
             if(user==null){ Toast.makeText(getActivity(),"请先登陆!",Toast.LENGTH_SHORT).show();return;}
             if(user.getAdminFlag()==1){Toast.makeText(getActivity(),"你已经是管理员了哦!",Toast.LENGTH_SHORT).show();return;}
             openUserApplyActivity(user.getPhone(),"管理员认证","管理员认证");
         });
    }

    private void openUserApplyActivity(String phone, String title, String flag) {
        MyThreadPool.getInstance().submit(()->{
            try{
                getActivity().runOnUiThread(() -> {
                    LoadDialog.show();
                });
                String[] columns = new String[]{phone};
                int [] types = new int[]{Types.CHAR};
                String sql = "select phone from ApplyList where phone = ?";
                ArrayList<HashMap<String,String>> d = DBUtils.query(columns,types,sql);
                if(d.size()>0){
                    getActivity().runOnUiThread(() -> {
                        LoadDialog.dismiss();
                        Toast.makeText(getActivity(),"请等待上一次的提交结果!",Toast.LENGTH_SHORT).show();
                        return ;
                    });
                }else{
                    getActivity().runOnUiThread(() -> {
                        LoadDialog.dismiss();
                        Intent intent = new Intent(getActivity(), UserApplyActivity.class);
                        intent.putExtra("title",title);
                        intent.putExtra("flag",flag);
                        intent.putExtra("phone",phone);
                        startActivity(intent);
                    });
                }
            }catch(Exception e){
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    LoadDialog.dismiss();
                    Toast.makeText(getActivity(),"出错啦!",Toast.LENGTH_SHORT).show();
                    return ;
                });
            }
        });
    }

    private void checkPermission() {
        RxPermissionManager.requestStoragePermissions(new RxPermissions(getActivity()) ,new RxPermissionConsumer(){
            @Override
            public void agree() {
                openAlbum();
            }

            @Override
            public void refuse() {
                ConfirmDialog confirmDialog = AlterDialogUtils.createTipDialog(getActivity(),"为了用户更好的体验，你需要通过此权限！");
                confirmDialog.setClickListener(new ConfirmDialog.OnBtnClickListener() {
                    @Override
                    public void ok() {
                        confirmDialog.dismiss();
                    }

                    @Override
                    public void cancel() {
                        confirmDialog.dismiss();
                    }
                });
                confirmDialog.show();
            }

            @Override
            public void handOpen() {
                ConfirmDialog confirmDialog = AlterDialogUtils.createTipDialog(getActivity(),"你上次选择了不在提示，现在您需要去设置,手动开启权限！");
                confirmDialog.setClickListener(new ConfirmDialog.OnBtnClickListener() {
                    @Override
                    public void ok() {
                        confirmDialog.dismiss();
                    }

                    @Override
                    public void cancel() {
                        confirmDialog.dismiss();
                    }
                });
                confirmDialog.show();
            }

        });
    }


    @Override //当activity的oncreate执行后调用
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("PersonFragment", "onActivityCreated");
    }

    @Override//与Activity的一样，此时可以看到Fragment的界面，但还不能交互
    public void onStart() {
        super.onStart();
        Log.d("PersonFragment", "onStart");

    }

    private void onRefresh() {
        User user = ((MyApplication)getActivity().getApplication()).getUser();
        if(user!=null){
            Log.d("PersonFrag:onRefresh", user.toString());
            LoadDialog.show();
            MyThreadPool.getInstance().submit(()->{
                String[] column = {user.getPhone()};
                int[] type = {Types.CHAR};
                String sql3 = "select * from user where phone =?";
                ArrayList<HashMap<String,String>> user_data = null;
                try {
                    user_data = DBUtils.query(column,type,sql3);
                    HashMap<String,String> ll = user_data.get(0);
                    User user1 = new User();
                    user1.setPhone(ll.get("phone"));
                    user1.setPassword(ll.get("password"));
                    user1.setNickname(ll.get("nickname"));
                    user1.setPic(ll.get("pic"));
                    user1.setAdminFlag(Integer.parseInt(ll.get("admin_flag")));
                    user1.setLawerFlag(Integer.parseInt(ll.get("lawer_flag")));
                    user1.setAgentFlag(Integer.parseInt(ll.get("agent_flag")));
                    user1.setLawerId(ll.get("lawerId"));
                    user1.setLawerpwd(ll.get("lawerpwd"));
                    user1.setAgentId(ll.get("agentId"));
                    user1.setAgentpwd(ll.get("agentpwd"));

                    Log.d("user1:",user1.toString());
                    getActivity().runOnUiThread(()->{
                        ((MyApplication)getActivity().getApplication()).setUser(user1);
                        User user2 = ((MyApplication)getActivity().getApplication()).getUser();
                        username.setText(user2.getNickname());
                        touxiang.setImageBitmap(StringAndBitMapTools.stringToBitmap(user2.getPic()));
                        LoadDialog.dismiss();
                        if(user1.getAdminFlag()==1){
                            apply_list.setVisibility(View.VISIBLE);
                        }else{
                            apply_list.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(()->{
                        LoadDialog.dismiss();
                    });
                }
            });
        }
    }

    @Override//与用户交互
    public void onResume() {
        super.onResume();
        Log.d("PersonFragment", "onResume");
    }

    @Override//可见不可交互
    public void onPause() {
        super.onPause();
        Log.d("PersonFragment", "onPause");
    }

    @Override//不可见，fragment进入后台模式
    public void onStop() {
        super.onStop();
        Log.d("PersonFragment", "onStop");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(String flag) {
        if("刷新".equals(flag))
        onRefresh();
    }

    @Override//Fragment被销毁的时候
    public void onDestroy() {
        super.onDestroy();
        Log.d("PersonFragment", "onDestroy");
        EventBus.getDefault().unregister(this);
    }

    @Override //解除与Activity关联时调用
    public void onDetach() {
        super.onDetach();
        Log.d("PersonFragment", "OnDetach");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * 打开系统相册
     */
    private void openAlbum() {
        // 使用Intent来跳转
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        // setType是设置类型，只要系统中满足该类型的应用都会被调用，这里需要的是图片
        intent.setType("image/*");
        // 打开满足条件的程序，CHOOSE_PHOTO是一个常量，用于后续进行判断，下面会说
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /**
     * 处理返回结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 上面的CHOOSE_PHOTO就是在这里用于判断
            case CHOOSE_PHOTO:
                // 判断手机系统版本号
                Log.d("onActivityResult",requestCode+"::"+resultCode);
                if (resultCode == -1) {
                    if(flag) {
                        flag = false;
                        if (Build.VERSION.SDK_INT >= 19) {
                            // 手机系统在4.4及以上的才能使用这个方法处理图片
                            Log.d("onActivityResult", "handleImageOnKitKat");
                            handleImageOnKitKat(data);
                        } else {
                            // 手机系统在4.4以下的使用这个方法处理图片
                            handleImageBeforeKitKat(data);
                            Log.d("onActivityResult", "handleImageBeforeKitKat");
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    //@TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = null;
        //如果是document类型的uri
        if(DocumentsContract.isDocumentUri(getActivity(),uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径
            imagePath = uri.getPath();
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，使用普通方式处理
            imagePath = getImagePath(uri, null);
        }
        //上传图片
        Log.d("imagePath:", imagePath);
        uploadImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        // 根据得到的图片路径显示图片
        uploadImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 上传头像
     */
    private void uploadImage(String imagePath){
        Log.d("imagePath!!!!!!!!!!:", imagePath);
        User user = ((MyApplication)getActivity().getApplication()).getUser();
        MyThreadPool.getInstance().submit(()->{
            getActivity().runOnUiThread(()->{
                LoadDialog.show();
            });

            try{
                String[] column = {imagePath,user.getPhone()};
                Log.d("imagePath:", imagePath);
                int[] type = {Types.BLOB,Types.CHAR};
                String sql = "update user set pic = ? where phone = ?";
                DBUtils.CUD(column,type,sql);
                getActivity().runOnUiThread(()->{
                    LoadDialog.dismiss();
                    onRefresh();
                    flag = true;
                });
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(()->{
                    LoadDialog.dismiss();
                    Toast.makeText(getActivity(),"更新失败！",Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}
