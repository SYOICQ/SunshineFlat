package activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hb.dialog.dialog.ConfirmDialog;
import com.suyong.sunshineflat.R;

import java.sql.Types;

import Util.AlterDialogUtils;
import Util.DBUtils;
import Util.LoadingDialog;
import Util.MyThreadPool;
import Util.StatusBarUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 实现用户提交申请
 */
public class UserApplyActivity extends AppCompatActivity {

    private ProgressDialog LoadDialog;


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.userapply_title)
    TextView userapply_title;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.reson)
    EditText reson;
    @BindView(R.id.pic_yulan)
    ImageView pic_yulan;
    @BindView(R.id.confirm)
    Button confirm;


    private static final int CHOOSE_PHOTO = 1;
    //标题
    private String title;
    //申请的类别
    private String flag;
    //身份证地址
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userapply);
        //绑定初始化ButterKnife
        ButterKnife.bind(this);
        StatusBarUtils.setStatusBarMode(this,true,R.color.flatDetail_ActionBar);
        LoadDialog = LoadingDialog.createLoadingDialog(this,"提示","加载中...");
        receiveData();
        initEvent();
    }

    private void receiveData() {
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        flag = intent.getStringExtra("flag");
        phone .setText(intent.getStringExtra("phone"));
        userapply_title.setText(title);
    }

    private void initEvent() {
        back.setOnClickListener((v)->{
            finish();
        });
         pic_yulan.setOnClickListener((v)->{
             CheckPermission();
         });
         confirm.setOnClickListener((v)->{
             String content = reson.getText().toString().trim();
             String phone1 = phone.getText().toString().trim();
             MyThreadPool.getInstance().submit(()->{
                try {
                    runOnUiThread(() -> {
                        LoadDialog.show();
                    });
                    String[] columns = new String[]{};
                    if("管理员认证".equals(flag)){
                        columns = new String[]{phone1,content,url,"1","0","0"};
                    }else if("律师认证".equals(flag)){
                        columns = new String[]{phone1,content,url,"0","1","0"};
                    }else if("经纪人认证".equals(flag)){
                        columns = new String[]{phone1,content,url,"0","0","1"};
                    }
                    String sql= "insert into ApplyList values(?,?,?,?,?,?)";
                    int[] types = new int[]{Types.CHAR,Types.CHAR,Types.BLOB,Types.INTEGER,Types.INTEGER,Types.INTEGER};
                    DBUtils.CUD(columns,types,sql);
                    runOnUiThread(() -> {
                        Toast.makeText(this,"提交成功!",Toast.LENGTH_SHORT).show();
                        LoadDialog.dismiss();
                        finish();
                    });
                }catch(Exception e){
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(this,"提交失败!",Toast.LENGTH_SHORT).show();
                        LoadDialog.dismiss();
                    });
                }
             });
         });
    }



    private void CheckPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            // 调用系统相册
            openAlbum();
        }
    }
    @Override
    // 申请用户权限
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    // 用户允许授权
                    /* ————调用系统相册————*/
                    openAlbum();
                } else {
                    // 用户拒绝授权
                    ConfirmDialog confirmDialog = AlterDialogUtils.createTipDialog(UserApplyActivity.this,"你拒绝了权限，你需要同意才可以上传！");
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
                break;
            default:
        }
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
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 手机系统在4.4及以上的才能使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 手机系统在4.4以下的使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = null;
        //如果是document类型的uri
        if(DocumentsContract.isDocumentUri(this,uri)){
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
        url = imagePath;
        Glide.with(this).load(url).into(pic_yulan);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        // 根据得到的图片路径显示图片
        url=imagePath;
        Glide.with(this).load(url).into(pic_yulan);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
