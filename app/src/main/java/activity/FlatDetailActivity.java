package activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.Target;
import com.hb.dialog.dialog.ConfirmDialog;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.transformers.Transformer;
import com.suyong.sunshineflat.MapActivity;
import com.suyong.sunshineflat.MyApplication;
import com.suyong.sunshineflat.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Util.AlterDialogUtils;
import Util.DBUtils;
import Util.LoadingDialog;
import Util.MyThreadPool;
import Util.ObserableUtils;
import Util.StatusBarUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import pojo.Constans;
import pojo.ErshouDetail;
import pojo.ForeignFlatDetail;
import pojo.LoupanDetail;
import pojo.User;
import pojo.ZufangDetail;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rxPermission.RxPermissionConsumer;
import rxPermission.RxPermissionManager;

public class FlatDetailActivity extends AppCompatActivity {
    private String TAG="FlatDetailActivity";
    private String picUrl;
    private String detailUrl;
    private String province;
    private String cityName;
    private String isGuoNei;
    private String addressPre;
    private Integer flag;
    private String LouPanName;
    private ProgressDialog LoadDialog;
    private List<String>banner_Images = new ArrayList<>();
    private List<String>banner_title = new ArrayList<>();

    //二手房信息里的控件
    TextView total_price;
    TextView price_perMi;
    TextView room_hall;
    TextView toward;
    TextView area;
    TextView village_name;
    TextView village_address;
    TextView map;
    TextView vr;
    TextView agent;
    TextView phoneNumber;
    TextView base_1;TextView base_2;TextView base_3;TextView base_4;
    TextView base_5;TextView base_6;TextView base_7;TextView base_8;
    TextView base_9;TextView base_10;TextView base_11;TextView base_12;
    TextView transaction_1;TextView transaction_2;TextView transaction_3;
    TextView transaction_4;TextView transaction_5;TextView transaction_6;
    TextView transaction_7;TextView transaction_8;
    TextView features_1;TextView features_2;TextView features_3;TextView features_4;
    ImageView des_img;
    LinearLayout des_content;

    //租房里的控件
    TextView vr1;
    TextView total_price1;
    TextView room_hall1;
    TextView toward1;
    TextView area1;
    TextView rent_way1;
    TextView agent1;
    TextView map1;
    TextView address;
    TextView phoneNumber1;
    TextView Base_1;TextView Base_2;TextView Base_3;TextView Base_4;
    TextView Base_5;TextView Base_6;TextView Base_7;TextView Base_8;
    TextView Base_9;TextView Base_10;TextView Base_11;TextView Base_12;
    TextView Base_13;
    TextView price_detail_1;TextView price_detail_2;TextView price_detail_3;
    TextView price_detail_4;TextView price_detail_5;

    //楼盘的控件
    TextView total_price2;
    TextView price_perMi2;
    TextView detail_address;
    TextView last_open_date;
    TextView agent2;
    TextView phoneNumber2;
    TextView map2;
    TextView vr2;
    TextView planning_info_1;TextView planning_info_2;TextView planning_info_3;
    TextView planning_info_4;TextView planning_info_5;TextView planning_info_6;
    TextView planning_info_7;TextView planning_info_8;
    TextView matching_info_1;TextView matching_info_2;TextView matching_info_3;
    TextView matching_info_4;TextView matching_info_5;TextView matching_info_6;
    TextView matching_info_7;
    LinearLayout huxingtu;

    //国外楼盘里的控件
    TextView total_price3;
    TextView room_hall3;
    TextView area3;
    TextView detail_address3;
    TextView last_open_date3;
    TextView agent3;
    TextView phoneNumber3;
    TextView map3;
    TextView vr3;
    TextView project_detail_info_1;TextView project_detail_info_2;TextView project_detail_info_3;
    TextView project_detail_info_4;TextView project_detail_info_5;TextView project_detail_info_6;
    TextView project_detail_info_7;TextView project_detail_info_8;TextView project_detail_info_9;
    TextView project_detail_info_10;TextView project_detail_info_11;TextView project_detail_info_12;
    TextView sellpoint_1; TextView sellpoint_2; TextView sellpoint_3;
    TextView sellpoint_4; TextView sellpoint_5; TextView sellpoint_6;
    LinearLayout huxingtu3;

    @BindView(R.id.fab_love)
    FloatingActionButton fab_love;
    @BindView(R.id.banner_1)
    XBanner banner;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        flag= getIntent().getBundleExtra("Message").getInt("flag");
        isGuoNei = getIntent().getBundleExtra("Message").getString("isGuoNei");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if("国内".equals(isGuoNei)) {
            if (flag == 0) {
                setContentView(R.layout.activity_loupan_detail);
                initView(flag);
            } else if (flag == 1) {
                setContentView(R.layout.activity_ershou_detail);
                initView(flag);
            } else if (flag == 2) {
                setContentView(R.layout.activity_zufang_detail);
                initView(flag);
            }
        }else if("国外".equals(isGuoNei)){
            setContentView(R.layout.activity_foreign_loupan);
            initViewForeign(flag);
        }
        //绑定初始化ButterKnife
        ButterKnife.bind(this);
        StatusBarUtils.setStatusBarMode(this,true,R.color.flatDetail_ActionBar);
        LoadDialog = LoadingDialog.createLoadingDialog(this,"提示","加载中...");
        init();
        initEvent();
    }

    //初始化国外控件
    private void initViewForeign(int flag) {
        switch (flag){
            case 0:
                 total_price3= findViewById(R.id.total_price);
                 room_hall3= findViewById(R.id.room_hall);
                 area3= findViewById(R.id.area);
                 detail_address3= findViewById(R.id.detail_address);
                 last_open_date3= findViewById(R.id.last_open_date);
                 agent3= findViewById(R.id.agent);
                 phoneNumber3= findViewById(R.id.phoneNumber);
                 map3= findViewById(R.id.map);
                 //vr3= findViewById(R.id.vr);
                 project_detail_info_1= findViewById(R.id.project_detail_info_1);
                 project_detail_info_2= findViewById(R.id.project_detail_info_2);
                 project_detail_info_3= findViewById(R.id.project_detail_info_3);
                 project_detail_info_4= findViewById(R.id.project_detail_info_4);
                 project_detail_info_5= findViewById(R.id.project_detail_info_5);
                 project_detail_info_6= findViewById(R.id.project_detail_info_6);
                 project_detail_info_7= findViewById(R.id.project_detail_info_7);
                 project_detail_info_8= findViewById(R.id.project_detail_info_8);
                 project_detail_info_9= findViewById(R.id.project_detail_info_9);
                 project_detail_info_10= findViewById(R.id.project_detail_info_10);
                 project_detail_info_11= findViewById(R.id.project_detail_info_11);
                 project_detail_info_12= findViewById(R.id.project_detail_info_12);
                 sellpoint_1= findViewById(R.id.sellpoint_1);
                 sellpoint_2= findViewById(R.id.sellpoint_2);
                 sellpoint_3= findViewById(R.id.sellpoint_3);
                 sellpoint_4= findViewById(R.id.sellpoint_4);
                 sellpoint_5= findViewById(R.id.sellpoint_5);
                 sellpoint_6= findViewById(R.id.sellpoint_6);
                 huxingtu3= findViewById(R.id.huxingtu);
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }
    }

    //初始化国内控件
    private void initView(int flag) {
        switch (flag){
            case 0:
                 total_price2= findViewById(R.id.total_price);
                 price_perMi2= findViewById(R.id.price_perMi);
                 detail_address= findViewById(R.id.detail_address);
                 last_open_date= findViewById(R.id.last_open_date);
                 agent2= findViewById(R.id.agent);
                 phoneNumber2= findViewById(R.id.phoneNumber);
                 map2= findViewById(R.id.map);
                 vr2= findViewById(R.id.vr);
                 planning_info_1= findViewById(R.id.planning_info_1);
                 planning_info_2= findViewById(R.id.planning_info_2);
                 planning_info_3= findViewById(R.id.planning_info_3);
                 planning_info_4= findViewById(R.id.planning_info_4);
                 planning_info_5= findViewById(R.id.planning_info_5);
                 planning_info_6= findViewById(R.id.planning_info_6);
                 planning_info_7= findViewById(R.id.planning_info_7);
                 planning_info_8= findViewById(R.id.planning_info_8);
                 matching_info_1= findViewById(R.id.matching_info_1);
                 matching_info_2= findViewById(R.id.matching_info_2);
                 matching_info_3= findViewById(R.id.matching_info_3);
                 matching_info_4= findViewById(R.id.matching_info_4);
                 matching_info_5= findViewById(R.id.matching_info_5);
                 matching_info_6= findViewById(R.id.matching_info_6);
                 matching_info_7= findViewById(R.id.matching_info_7);
                 huxingtu= findViewById(R.id.huxingtu);
                break;
            case 1:
                total_price = findViewById(R.id.total_price);
                price_perMi = findViewById(R.id.price_perMi);
                room_hall= findViewById(R.id.room_hall);
                toward= findViewById(R.id.toward);
                area= findViewById(R.id.area);
                village_name= findViewById(R.id.village_name);
                village_address= findViewById(R.id.village_address);
                map = findViewById(R.id.map);
                vr = findViewById(R.id.vr);
                agent= findViewById(R.id.agent);
                phoneNumber= findViewById(R.id.phoneNumber);
                 base_1= findViewById(R.id.base_1);
                 base_2= findViewById(R.id.base_2);
                 base_3= findViewById(R.id.base_3);
                 base_4= findViewById(R.id.base_4);
                 base_5= findViewById(R.id.base_5);
                 base_6= findViewById(R.id.base_6);
                 base_7= findViewById(R.id.base_7);
                 base_8= findViewById(R.id.base_8);
                 base_9= findViewById(R.id.base_9);
                 base_10= findViewById(R.id.base_10);
                 base_11= findViewById(R.id.base_11);
                 base_12= findViewById(R.id.base_12);
                 transaction_1= findViewById(R.id.transaction_1);
                 transaction_2= findViewById(R.id.transaction_2);
                 transaction_3= findViewById(R.id.transaction_3);
                 transaction_4= findViewById(R.id.transaction_4);
                 transaction_5= findViewById(R.id.transaction_5);
                 transaction_6= findViewById(R.id.transaction_6);
                 transaction_7= findViewById(R.id.transaction_7);
                 transaction_8= findViewById(R.id.transaction_8);
                 features_1= findViewById(R.id.features_1);
                 features_2= findViewById(R.id.features_2);
                 features_3= findViewById(R.id.features_3);
                 features_4= findViewById(R.id.features_4);
                 des_img= findViewById(R.id.des_img);
                 des_content= findViewById(R.id.des_content);
                break;
            case 2:
                 vr1 = findViewById(R.id.vr);
                 map1 = findViewById(R.id.map);
                 address = findViewById(R.id.address);
                 total_price1 = findViewById(R.id.total_price);
                 room_hall1  = findViewById(R.id.room_hall);
                 toward1= findViewById(R.id.toward);
                 area1= findViewById(R.id.area);
                 rent_way1= findViewById(R.id.rent_way);
                 agent1= findViewById(R.id.agent);
                 phoneNumber1= findViewById(R.id.phoneNumber);
                 Base_1= findViewById(R.id.base_1);
                 Base_2= findViewById(R.id.base_2);
                 Base_3= findViewById(R.id.base_3);
                 Base_4= findViewById(R.id.base_4);
                 Base_5= findViewById(R.id.base_5);
                 Base_6= findViewById(R.id.base_6);
                 Base_7= findViewById(R.id.base_7);
                 Base_8= findViewById(R.id.base_8);
                 Base_9= findViewById(R.id.base_9);
                 Base_10= findViewById(R.id.base_10);
                 Base_11= findViewById(R.id.base_11);
                Base_12= findViewById(R.id.base_12);
                Base_13= findViewById(R.id.base_13);
                 price_detail_1= findViewById(R.id.price_detail_1);
                 price_detail_2= findViewById(R.id.price_detail_2);
                 price_detail_3= findViewById(R.id.price_detail_3);
                 price_detail_4= findViewById(R.id.price_detail_4);
                 price_detail_5= findViewById(R.id.price_detail_5);
                break;
            default:
                break;
        }
    }

    private void initEvent() {
        back.setOnClickListener((v)->{
            finish();
        });
        fab_love.setOnClickListener((v)->{
//                Toast.makeText(FlatDetailActivity.this, "点击了", Toast.LENGTH_SHORT).show();
            User user = ((MyApplication)getApplication()).getUser();
            if(user!=null){
                addMyLove(user);
            }else{
                Toast.makeText(FlatDetailActivity.this, "你还没有登陆哦!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //添加到我的喜欢
    private void addMyLove(User user) {
        final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(this).builder()
                .setTitle("请输入您对该房源的评价分数(1-10)")
                .setEditText("");
        myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String score = myAlertInputDialog.getResult();
                MyThreadPool.getInstance().submit(()-> {
                    try{
                        runOnUiThread(()->{
                            LoadDialog.show();
                        });
                        String sql = "";
                        String[] columns =new String[]{score,"1",user.getPhone(),detailUrl};
                        int[] types = new int[]{Types.INTEGER,Types.INTEGER,Types.CHAR,Types.CHAR};
                        if("国内".equals(isGuoNei)) {
                            if (flag == 0) {
                                sql = "update MyLouPan  set score = ? ,love_flag = ? where phone = ? and detailUrl = ?";
                            } else if (flag == 1) {
                                sql = "update MyErshouFang set score = ? ,love_flag = ? where phone = ? and detailUrl = ?";
                            } else if (flag == 2) {
                                sql = "update MyZuFang set score = ? ,love_flag = ? where phone = ? and detailUrl = ?";
                            }
                        }else if("国外".equals(isGuoNei)){
                            sql = "update MyForeignLouPan set score = ? ,love_flag = ? where phone = ? and detailUrl = ?";
                        }
                        DBUtils.CUD(columns,types,sql);
                        runOnUiThread(()->{
                            LoadDialog.dismiss();
                            myAlertInputDialog.dismiss();
                            Toast.makeText(FlatDetailActivity.this,"添加成功!",Toast.LENGTH_SHORT).show();
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                        runOnUiThread(()->{
                            LoadDialog.dismiss();
                            Toast.makeText(FlatDetailActivity.this,"添加失败!",Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onResume() {
        super.onResume();
        banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        banner.stopAutoPlay();
    }

    private void init() {
        Intent intent = getIntent();
        //从intent取出bundle
        Bundle bundle = intent.getBundleExtra("Message");
        detailUrl = bundle.getString("detailUrl");
        province = bundle.getString("CurrentProvince");
        cityName = bundle.getString("CurrentCityName");
        addressPre = bundle.getString("addressPre");
        LouPanName = bundle.getString("LouPanName");
        picUrl = bundle.getString("picUrl");
        Log.d(TAG, detailUrl +":"+isGuoNei+":"+ province+":" + cityName+":"+addressPre);
        onRefresh(flag,detailUrl,province,cityName,addressPre);
    }

    //刷新界面
    private void onRefresh(Integer flag, String detailUrl, String province, String cityName, String addressPre) {

        if("国内".equals(isGuoNei)){
            LoadDialog.show();
            ObserableUtils.parseGuoNei(flag,detailUrl,province,cityName,addressPre).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>(){

                @Override
                public void onCompleted() {
                    LoadDialog.dismiss();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(FlatDetailActivity.this, "解析详情界面出错啦！", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,e.getMessage());
                    LoadDialog.dismiss();
                }

                @Override
                public void onNext(List<Object> objects) {
                    switch (flag){
                        case 0:
                            //新房
                            LoupanDetail loupanDetail = (LoupanDetail) objects.get(0);
                            refrshLoupan(loupanDetail);
                            break;
                        case 1:
                            //二手房
                            ErshouDetail ershouDetail = (ErshouDetail) objects.get(0);
                            refreshErshou(ershouDetail);
                            break;
                        case 2:
                            //租房
                            ZufangDetail zufangDetail = (ZufangDetail) objects.get(0);
                            refreshZufang(zufangDetail);
                            break;
                        default:
                            break;
                    }

                }
            });
        }else if("国外".equals(isGuoNei)){
            LoadDialog.show();
            ObserableUtils.parseGuoWai(flag,detailUrl).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>(){

                @Override
                public void onCompleted() {
                    LoadDialog.dismiss();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(FlatDetailActivity.this, "解析详情界面出错啦！", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,e.getMessage());
                    LoadDialog.dismiss();
                }

                @Override
                public void onNext(List<Object> objects) {
                    switch (flag){
                        case 0:
                            //新房
                            ForeignFlatDetail loupanDetail = (ForeignFlatDetail) objects.get(0);
                            refrshForeignLouPan(loupanDetail);
                            break;
                        case 1:
                            //二手房
                            break;
                        case 2:
                            //租房
                            break;

                        default:
                            break;
                    }
                }
            });
        }

    }

    //更新国外楼盘界面
    private void refrshForeignLouPan(ForeignFlatDetail loupanDetail) {
       List<String>img_url=loupanDetail.getImg_url();
       if(img_url!=null&&img_url.size()>0){
           for(String url:img_url){
               banner_title.add(" ");
               banner_Images.add(url);
           }
       }
        // 为XBanner绑定数据
        banner .setData(banner_Images,banner_title);//第二个参数为提示文字资源集合
        // XBanner适配数据
        banner.setmAdapter(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                Glide.with(FlatDetailActivity.this).load(banner_Images.get(position)).into((ImageView) view);
            }
        });
        // 设置XBanner的页面切换特效，选择一个即可，总的大概就这么多效果啦，欢迎使用
        banner.setPageTransformer(Transformer.Default);//横向移动
        // 设置XBanner页面切换的时间，即动画时长
        banner.setPageChangeDuration(1000);
        banner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, int position) {
                showImageDetail(banner_Images.get(position));
            }
        });
        title.setText(setData(loupanDetail.getTitle()));
        List<Double>priceList = loupanDetail.getPrice();
        String price="";
        if(priceList.size()==1){
            price = priceList.get(0)+"";
        }else if(priceList.size()==2){
            price = priceList.get(0)+"-"+priceList.get(1);
        }
        total_price3.setText(setData(price));
        List<String> roomList = loupanDetail.getHuxingText();
        String room="";
        for(String s:roomList){
            room = room+s+"/";
        }
        int pos = room.lastIndexOf("/");
        room_hall3.setText(setData(room.substring(0,pos)));
        List<Double>areaList = loupanDetail.getPrice();
        String area="";
        if(areaList.size()==1){
            area = areaList.get(0)+"";
        }else if(areaList.size()==2){
            area = areaList.get(0)+"-"+areaList.get(1);
        }
        area3.setText(setData(area)+"平米");
        detail_address3.setText(setData(loupanDetail.getDetail_address()));
        last_open_date3.setText(setData(loupanDetail.getLast_open_date()));
        agent3.setText(setData(loupanDetail.getAgent()));
        phoneNumber3.setText(setData(loupanDetail.getPuhoneNumber()));
        phoneNumber3.setOnClickListener((v)->{
            String p = phoneNumber3.getText().toString().trim();
            if(TextUtils.isEmpty(p)) return;
            requestTelephone(p);
        });
        map3.setOnClickListener((v)->{
            //startMapActivity(detail_address3.getText().toString().trim());
            Toast.makeText(this,"暂不支持!",Toast.LENGTH_SHORT).show();
        });
//        vr3.setOnClickListener((v)->{
//
//        });
        HashMap<String,String> project_detail_info = loupanDetail.getProject_detail_info();
        project_detail_info_1.setText(setData(project_detail_info.get("local_name")));
        project_detail_info_2.setText(setData(project_detail_info.get("chinese_name")));
        project_detail_info_3.setText(setData(project_detail_info.get("position")));
        project_detail_info_4.setText(setData(project_detail_info.get("zip_code")));
        project_detail_info_5.setText(setData(project_detail_info.get("own_year")));
        project_detail_info_6.setText(setData(project_detail_info.get("heating_way")));
        project_detail_info_7.setText(setData(project_detail_info.get("property_company")));
        project_detail_info_8.setText(setData(project_detail_info.get("flat_number")));
        project_detail_info_9.setText(setData(project_detail_info.get("area")));
        project_detail_info_10.setText(setData(project_detail_info.get("property_tax")));
        project_detail_info_1.setText(setData(project_detail_info.get("car_num")));
        project_detail_info_12.setText(setData(project_detail_info.get("management_price")));
        HashMap<String,String> sellpoint = loupanDetail.getSelling_point();
        sellpoint_1.setText(setData(sellpoint.get("核心卖点")));
        sellpoint_2.setText(setData(sellpoint.get("小区介绍")));
        sellpoint_3.setText(setData(sellpoint.get("周边配套")));
        sellpoint_4.setText(setData(sellpoint.get("周边规划")));
        sellpoint_5.setText(setData(sellpoint.get("教育资源")));
        sellpoint_6.setText(setData(sellpoint.get("投资前景")));
        //huxingtu3
        List<HashMap<String,String>> huxingTu = loupanDetail.getHuxing_detail();
        for(int i=0;i<huxingTu.size();i++){
            View itemView = LayoutInflater.from(this).inflate(R.layout.huxingtu_item , null , false) ;
            ImageView huxing_img = itemView.findViewById(R.id.huxing_img);
            TextView huxing_type = itemView.findViewById(R.id.huxing_type);
            TextView huxing_area = itemView.findViewById(R.id.huxing_area);
            TextView huxing_price = itemView.findViewById(R.id.huxing_price);
            TextView huxing_status = itemView.findViewById(R.id.huxing_status);
            HashMap<String,String> itemData = huxingTu.get(i);
            Glide.with(this).load(itemData.get("img")).into(huxing_img);
            huxing_type.setText(setData(itemData.get("title")));
            huxing_area.setText(setData(itemData.get("area")));
            huxing_price.setText(setData(itemData.get("price"))+"万/套");
            huxing_status.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener((v)->{
                showImageDetail(itemData.get("img"));
            });
            huxingtu3.addView(itemView);
        }
        //添加到海外浏览记录
        addMyForeignLouPan(loupanDetail);
    }

    //添加到我的海外房源观看记录
    private void addMyForeignLouPan(ForeignFlatDetail loupanDetail) {
        User user = ((MyApplication)getApplication()).getUser();
        if(user ==null) return;
        MyThreadPool.getInstance().submit(()-> {
            try{
                String[] column1 = new String[]{detailUrl,user.getPhone()};
                int[] type1 = new int[]{Types.CHAR, Types.CHAR};
                String sql1 = "select * from MyForeignLouPan where detailUrl = ? and phone = ?";
                ArrayList<HashMap<String,String>>d = DBUtils.query(column1, type1, sql1);
                if(d.size()>0) return;

                List<Double>priceList = loupanDetail.getPrice();
                Double minPrice=0.0;
                Double maxPrice=0.0;
                if(priceList.size()==1){
                    minPrice = 0.0;
                    maxPrice=priceList.get(0);
                }else if(priceList.size()==2){
                    minPrice = priceList.get(0);
                    maxPrice = priceList.get(1);
                }

                List<String> roomList = loupanDetail.getHuxingText();
                String room="";
                for(String s:roomList){
                    room = room+s+"/";
                }
                int pos = room.lastIndexOf("/");

                List<Double>areaList = loupanDetail.getPrice();
                Double minArea=0.0;
                Double maxArea=0.0;
                if(areaList.size()==1){
                    minArea=0.0;
                    maxArea = areaList.get(0);
                }else if(areaList.size()==2){
                    minArea = areaList.get(0);
                    maxArea = areaList.get(1);
                }

                String[] cloumn = new String[]{
                        user.getPhone(),
                        loupanDetail.getTitle(), String.valueOf(minPrice),String.valueOf(maxPrice),
                        room.substring(0,pos), String.valueOf(minArea),String.valueOf(maxArea),
                        loupanDetail.getDetail_address(),
                        String.valueOf(flag), isGuoNei, detailUrl,
                        province, cityName, "", LouPanName, picUrl,"0","0"
                };
                int[] type = new int[]{Types.CHAR, Types.CHAR, Types.DOUBLE, Types.DOUBLE
                        , Types.CHAR, Types.DOUBLE, Types.DOUBLE, Types.CHAR,
                        Types.INTEGER, Types.CHAR, Types.CHAR, Types.CHAR,
                        Types.CHAR, Types.CHAR, Types.CHAR, Types.CHAR,Types.INTEGER,Types.INTEGER};
                String sql = "insert into MyForeignLouPan values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                DBUtils.CUD(cloumn, type, sql);
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    int pos;

    //调用打电话接口
    private void requestTelephone(String phoneNum) {
        RxPermissionManager.requestTelephonePermissions(new RxPermissions(this) ,new RxPermissionConsumer(){
                    @Override
                    public void agree() {
                        pos = phoneNum.indexOf("转");
                        if(pos==-1) pos = phoneNum.indexOf("-");
                        if(pos==-1) return;
                        ConfirmDialog confirmDialog = AlterDialogUtils.createTipDialog(FlatDetailActivity.this,"您确定要打电话吗?(拨通后请输入转接号码:)"+phoneNum.substring(pos));
                        confirmDialog.setClickListener(new ConfirmDialog.OnBtnClickListener() {
                            @Override
                            public void ok() {
                                if("未知".equals(phoneNum)||TextUtils.isEmpty(phoneNum)) return;
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                Uri data = Uri.parse("tel:" + phoneNum.substring(0,pos));
                                intent.setData(data);
                                startActivity(intent);
                            }

                            @Override
                            public void cancel() {
                                confirmDialog.dismiss();
                            }
                        });
                        confirmDialog.show();
                    }

                    @Override
                    public void refuse() {
                        ConfirmDialog confirmDialog = AlterDialogUtils.createTipDialog(FlatDetailActivity.this,"你拒绝了权限，功能受限！");
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
                        ConfirmDialog confirmDialog = AlterDialogUtils.createTipDialog(FlatDetailActivity.this,"你需要去设置,手动开启权限！");
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

    //更新新房界面
    private void refrshLoupan(LoupanDetail loupanDetail) {
        //效果图
        List<String> rendering_url = loupanDetail.getRendering_url();
        //实景图
         List<String> vr_url = loupanDetail.getVr_url();
        //样板间
         List<String> sample_url = loupanDetail.getSample_url();
        //小区配套
         List<String> Village_set = loupanDetail.getVillage_set();
        //预售许可证
         List<String> Presale_license = loupanDetail.getPresale_license();
        //开发商营业执照
         List<String> Developer_business_license = loupanDetail.getDeveloper_business_license();
         if(rendering_url!=null&&rendering_url.size()>0) {
             for (String url : rendering_url) {
                 banner_title.add("效果图");
                 banner_Images.add(url);
             }
         }
        if(vr_url!=null&&vr_url.size()>0) {
            for (String url : vr_url) {
                banner_title.add("实景图");
                banner_Images.add(url);
            }
        }
        if(sample_url!=null&&sample_url.size()>0) {
            for (String url : sample_url) {
                banner_title.add("样板间");
                banner_Images.add(url);
            }
        }
        if(Village_set!=null&&Village_set.size()>0) {
            for (String url : Village_set) {
                banner_title.add("小区配套");
                banner_Images.add(url);
            }
        }
        if(Presale_license!=null&&Presale_license.size()>0) {
            for (String url : Presale_license) {
                banner_title.add("预售许可证");
                banner_Images.add(url);
            }
        }
        if(Developer_business_license!=null&&Developer_business_license.size()>0) {
            for (String url : Developer_business_license) {
                banner_title.add("开发商营业执照");
                banner_Images.add(url);
            }
        }
        // 为XBanner绑定数据
        banner .setData(banner_Images,banner_title);//第二个参数为提示文字资源集合
        // XBanner适配数据
        banner.setmAdapter(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                Glide.with(FlatDetailActivity.this).load(banner_Images.get(position)).into((ImageView) view);
            }
        });
        // 设置XBanner的页面切换特效，选择一个即可，总的大概就这么多效果啦，欢迎使用
        banner.setPageTransformer(Transformer.Default);//横向移动
        // 设置XBanner页面切换的时间，即动画时长
        banner.setPageChangeDuration(1000);
        banner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, int position) {
                showImageDetail(banner_Images.get(position));
            }
        });
        title.setText(setData(loupanDetail.getTitle()));
        total_price2.setText(setData(loupanDetail.getPrice()+"")+"万/套");
        price_perMi2.setText(setData(loupanDetail.getPrice_perMi()+"")+"元/平");
        detail_address.setText(setData(loupanDetail.getDetail_address()));
        last_open_date.setText(setData(loupanDetail.getLast_open_date()));
        agent2.setText(setData(loupanDetail.getAgent()));
        phoneNumber2.setText(setData(loupanDetail.getPhoneNumber()));
        phoneNumber2.setOnClickListener((v)->{
            String p = phoneNumber2.getText().toString().trim();
            if(TextUtils.isEmpty(p)) return;
            requestTelephone(p);
        });
        map2.setOnClickListener((v)->{
            startMapActivity(detail_address.getText().toString().trim()+title.getText().toString().trim());
        });
        vr2.setOnClickListener((v)->{
            if(vr_url!=null&&vr_url.size()>0) {
                MyThreadPool.getInstance().submit(()->{
                    runOnUiThread(()->{
                        LoadDialog.show();
                    });
                    List<Bitmap> result = new ArrayList<>();
                    List<String> hh = Constans.getVrList();
                    for(String url :hh){
                        Bitmap bitmap = getbitmap(url);
                        if(bitmap!=null) result.add(bitmap);
                    }
                    runOnUiThread(()->{
                        LoadDialog.dismiss();
                        if(result.size()==0){
                            Toast.makeText(this,"网络好像不给力!",Toast.LENGTH_SHORT).show();
                        }else{
                            EventBus.getDefault().postSticky(result);
                            startActivity(new Intent(this,openGLActivity.class));
                        }
                    });
                });
            }else{
                Toast.makeText(this,"暂无VR图!",Toast.LENGTH_SHORT).show();
            }
        });
        HashMap<String ,Object> planning_info = loupanDetail.getPlanning_info();
        planning_info_1.setText(setData((String)planning_info.get("建筑类型")));
        planning_info_2.setText(setData((String)planning_info.get("绿化率")));
        planning_info_3.setText(setData((String)planning_info.get("占地面积")));
        planning_info_4.setText(setData((String)planning_info.get("容积率")));
        planning_info_5.setText(setData((String)planning_info.get("建筑面积")));
        planning_info_6.setText(setData((String)planning_info.get("物业类型")));
        planning_info_7.setText(setData((String)planning_info.get("规划户数")));
        planning_info_8.setText(setData((String)planning_info.get("产权年限")));
        HashMap<String ,String> matching_info =loupanDetail.getMatching_info();
        matching_info_1.setText(setData(matching_info.get("物业公司")));
        matching_info_2.setText(setData(matching_info.get("车位配比")));
        matching_info_3.setText(setData(matching_info.get("物业费")));
        matching_info_4.setText(setData(matching_info.get("供暖方式")));
        matching_info_5.setText(setData(matching_info.get("供水方式")));
        matching_info_6.setText(setData(matching_info.get("供电方式")));
        matching_info_7.setText(setData(matching_info.get("车位")));
        //huxingtu
        List<HashMap<String,String>> huxingTu = loupanDetail.getHuxing_info();
        for(int i=0;i<huxingTu.size();i++){
            View itemView = LayoutInflater.from(this).inflate(R.layout.huxingtu_item , null , false) ;
            ImageView huxing_img = itemView.findViewById(R.id.huxing_img);
            TextView huxing_type = itemView.findViewById(R.id.huxing_type);
            TextView huxing_area = itemView.findViewById(R.id.huxing_area);
            TextView huxing_price = itemView.findViewById(R.id.huxing_price);
            TextView huxing_status = itemView.findViewById(R.id.huxing_status);
            HashMap<String,String> itemData = huxingTu.get(i);
            Glide.with(this).load(itemData.get("imgUrl")).into(huxing_img);
            huxing_type.setText(setData(itemData.get("type")));
            huxing_area.setText(setData(itemData.get("area")));
            huxing_price.setText(setData(itemData.get("price"))+"万/套");
            huxing_status.setText(setData(itemData.get("status")));
            itemView.setOnClickListener((v)->{
                showImageDetail(itemData.get("imgUrl"));
            });
            huxingtu.addView(itemView);
        }
        //添加到新房的浏览记录
        addMyLouPan(loupanDetail);

    }

    String Hallstr = "";
    Double maxArea;
    Double minArea;
    //添加到新房的浏览记录
    private void addMyLouPan(LoupanDetail loupanDetail) {
        User user = ((MyApplication)getApplication()).getUser();
        if(user ==null) return;
        //huxingtu
        List<HashMap<String,String>> huxingTu = loupanDetail.getHuxing_info();
        if(huxingTu.size()>0) {
            Double[] areaData = new Double[huxingTu.size()];
            int[] roomData = new int[huxingTu.size()];
            for (int i = 0; i < huxingTu.size(); i++) {
                HashMap<String, String> itemData = huxingTu.get(i);
                String type1 = itemData.get("type");
                String area1 = itemData.get("area");
                int dex = area1.indexOf("m²");
                Double area = Double.valueOf(area1.substring(0, dex));
                areaData[i] = area;
                dex = type1.indexOf("室");
                int room = Integer.parseInt(type1.substring(0, dex));
                roomData[i] = room;
            }
            maxArea = areaData[0];
            for (int i = 1; i < areaData.length; i++) {
                if (areaData[i] > maxArea) {
                    maxArea = areaData[i];
                }
            }
            minArea = areaData[0];
            for (int i = 1; i < areaData.length; i++) {
                if (areaData[i] < minArea) {
                    minArea = areaData[i];
                }
            }
            Set<Integer> set = new HashSet<Integer>();
            for (int i : roomData) set.add(i);
            Integer[] integers = new Integer[set.size()];
            int k = 0;
            for (Integer str : set) {
                integers[k++] = str;
            }

            for (int i2 : integers) {
                Hallstr += i2 + "室/";
            }
        }else{
            maxArea = 0.0;
            minArea = 0.0;
            Hallstr=" ";
        }
        MyThreadPool.getInstance().submit(()-> {
            try{
                String[] column1 = new String[]{detailUrl,user.getPhone()};
                int[] type1 = new int[]{Types.CHAR, Types.CHAR};
                String sql1 = "select * from MyLouPan where detailUrl = ? and phone = ?";
                ArrayList<HashMap<String,String>>d = DBUtils.query(column1, type1, sql1);
                if(d.size()>0) return;

                String[] cloumn = new String[]{
                        user.getPhone(),
                        loupanDetail.getTitle(),detail_address.getText().toString().trim()+title.getText().toString().trim(),
                        String.valueOf(loupanDetail.getPrice()), String.valueOf(loupanDetail.getPrice_perMi()),
                        String.valueOf(maxArea),String.valueOf(minArea),Hallstr,
                        String.valueOf(flag), isGuoNei, detailUrl,
                        province, cityName, addressPre, " ", picUrl,"0","0"
                };
                int[] type = new int[]{Types.CHAR, Types.CHAR, Types.CHAR, Types.DOUBLE
                        , Types.DOUBLE, Types.DOUBLE, Types.DOUBLE, Types.CHAR,
                        Types.INTEGER, Types.CHAR, Types.CHAR, Types.CHAR,
                        Types.CHAR, Types.CHAR, Types.CHAR, Types.CHAR,Types.INTEGER,Types.INTEGER};
                String sql = "insert into MyLouPan values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                DBUtils.CUD(cloumn, type, sql);
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    //更新租房界面
    private void refreshZufang(ZufangDetail zufangDetail) {
        title.setText(setData(zufangDetail.getTitle()));
        for (String url:zufangDetail.getImagUrl()) {
            banner_title.add(" ");
            banner_Images.add(url);
        }
        // 为XBanner绑定数据
        banner .setData(banner_Images,banner_title);//第二个参数为提示文字资源集合
        // XBanner适配数据
        banner.setmAdapter(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                Glide.with(FlatDetailActivity.this).load(banner_Images.get(position)).into((ImageView) view);
            }
        });
        // 设置XBanner的页面切换特效，选择一个即可，总的大概就这么多效果啦，欢迎使用
        banner.setPageTransformer(Transformer.Default);//横向移动
        // 设置XBanner页面切换的时间，即动画时长
        banner.setPageChangeDuration(1000);
        banner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, int position) {
                showImageDetail(banner_Images.get(position));
            }
        });
        total_price1.setText(setData(zufangDetail.getPrice()+""));
        room_hall1.setText(setData(zufangDetail.getRoom()+"室"+zufangDetail.getHall()+"厅"+zufangDetail.getToilet()+"卫"));
        toward1.setText(setData(zufangDetail.getToward()));
        area1.setText(setData(zufangDetail.getArea()+"平米"));
        rent_way1.setText(setData(zufangDetail.getRent_way()));
        agent1.setText(setData(zufangDetail.getAgent()));
        phoneNumber1.setText(setData(zufangDetail.getPhoneNumber()));
        phoneNumber1.setOnClickListener((v)->{
            String p = phoneNumber1.getText().toString().trim();
            if(TextUtils.isEmpty(p)) return;
            requestTelephone(p);
        });
        HashMap<String,String> Base = zufangDetail.getBase();
        Base_1.setText(setData(Base.get("面积")));
        Base_2.setText(setData(Base.get("朝向")));
        Base_3.setText(setData(Base.get("维护")));
        Base_4.setText(setData(Base.get("入住")));
        Base_5.setText(setData(Base.get("楼层")));
        Base_6.setText(setData(Base.get("电梯")));
        Base_7.setText(setData(Base.get("车位")));
        Base_8.setText(setData(Base.get("用水")));
        Base_9.setText(setData(Base.get("用电")));
        Base_10.setText(setData(Base.get("燃气")));
        Base_11.setText(setData(Base.get("采暖")));
        Base_12.setText(setData(Base.get("租期")));
        Base_13.setText(setData(Base.get("看房")));
        HashMap<String,String> price_detail = zufangDetail.getPrice_detail();
        price_detail_1.setText(setData(price_detail.get("付款方式")));
        price_detail_2.setText(setData(price_detail.get("租金 (元/月)")));
        price_detail_3.setText(setData(price_detail.get("押金 (元)")));
        price_detail_4.setText(setData(price_detail.get("服务费 (元)")));
        price_detail_5.setText(setData(price_detail.get("中介费 (元)")));
        address.setText(setData(zufangDetail.getAddress()));
        map1.setOnClickListener((v)->{
            startMapActivity(address.getText().toString().trim());
        });
        vr1.setOnClickListener((v)->{
            //vr看图
            MyThreadPool.getInstance().submit(()->{
                runOnUiThread(()->{
                    LoadDialog.show();
                });
                List<Bitmap> result = new ArrayList<>();
                List<String> hh = Constans.getVrList();
                for(String url :hh){
                    Bitmap bitmap = getbitmap(url);
                    if(bitmap!=null) result.add(bitmap);
                }
                runOnUiThread(()->{
                    LoadDialog.dismiss();
                    if(result.size()==0){
                        Toast.makeText(this,"网络好像不给力!",Toast.LENGTH_SHORT).show();
                    }else{
                        EventBus.getDefault().postSticky(result);
                        startActivity(new Intent(this,openGLActivity.class));
                    }
                });
            });
        });
        //添加到租房浏览记录
        addMyZuFang(zufangDetail);

    }

    //添加到租房浏览记录
    private void addMyZuFang(ZufangDetail zufangDetail) {
        User user = ((MyApplication)getApplication()).getUser();
        if(user ==null) return;
        MyThreadPool.getInstance().submit(()-> {
            try{
                String[] column1 = new String[]{detailUrl,user.getPhone()};
                int[] type1 = new int[]{Types.CHAR, Types.CHAR};
                String sql1 = "select * from MyZuFang where detailUrl = ? and phone = ?";
                ArrayList<HashMap<String,String>>d = DBUtils.query(column1, type1, sql1);
                if(d.size()>0) return;


                String[] cloumn = new String[]{
                        user.getPhone(),
                        zufangDetail.getTitle(),address.getText().toString().trim(),
                        String.valueOf(zufangDetail.getPrice()),String.valueOf(zufangDetail.getRoom()),
                        String.valueOf(zufangDetail.getHall()), String.valueOf(zufangDetail.getToilet()),
                        zufangDetail.getToward(), String.valueOf(zufangDetail.getArea()),
                        String.valueOf(flag), isGuoNei, detailUrl,
                        province, cityName, addressPre, " ", picUrl,"0","0"
                };
                int[] type = new int[]{Types.CHAR, Types.CHAR, Types.CHAR, Types.DOUBLE
                        , Types.DOUBLE, Types.DOUBLE, Types.DOUBLE, Types.CHAR, Types.DOUBLE,
                        Types.INTEGER, Types.CHAR, Types.CHAR, Types.CHAR,
                        Types.CHAR, Types.CHAR, Types.CHAR, Types.CHAR,Types.INTEGER,Types.INTEGER};
                String sql = "insert into MyZuFang values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                DBUtils.CUD(cloumn, type, sql);
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    //更新二手房界面
    private void refreshErshou(ErshouDetail ershouDetail) {
        for (Map.Entry<String, String> entry :  ershouDetail.getImageUrl().entrySet()) {
            banner_title.add(entry.getKey());
            banner_Images.add(entry.getValue());
        }
        title.setText(setData(ershouDetail.getTitle()));
        total_price.setText(ershouDetail.getTotalPrice()+"万");
        price_perMi.setText(ershouDetail.getPrice_perMi()+"元/平米");
        room_hall.setText(ershouDetail.getRoom()+"室"+ershouDetail.getHall()+"厅");
        toward.setText(setData(ershouDetail.getToward()));
        area.setText(setData(ershouDetail.getArea()+"平米"));
        village_name.setText(setData(ershouDetail.getVillage_name()));
        village_address.setText(setData(ershouDetail.getVillage_address()));
        map.setOnClickListener((view)->{
            //调用地图
            startMapActivity(village_address.getText().toString().trim());
        });
        vr.setOnClickListener((v)->{
            //vr看图
            MyThreadPool.getInstance().submit(()->{
                runOnUiThread(()->{
                    LoadDialog.show();
                });
                List<Bitmap> result = new ArrayList<>();
                List<String> hh = Constans.getVrList();
                for(String url :hh){
                    Bitmap bitmap = getbitmap(url);
                    if(bitmap!=null) result.add(bitmap);
                }
                runOnUiThread(()->{
                    LoadDialog.dismiss();
                    if(result.size()==0){
                        Toast.makeText(this,"网络好像不给力!",Toast.LENGTH_SHORT).show();
                    }else{
                        EventBus.getDefault().postSticky(result);
                        startActivity(new Intent(this,openGLActivity.class));
                    }
                });
            });
        });

        agent.setText(setData(ershouDetail.getAgent()));
        phoneNumber.setText(setData(ershouDetail.getPhoneNumber()));
        phoneNumber.setOnClickListener((v)->{
            //调用打电话
            String p = phoneNumber.getText().toString().trim();
            if(TextUtils.isEmpty(p)) return;
            requestTelephone(p);
        });
        HashMap<String,String> base =  ershouDetail.getBase();
        base_1.setText(setData(base.get("房屋户型")));
        base_2.setText(setData(base.get("所在楼层")));
        base_3.setText(setData(base.get("建筑面积")));
        base_4.setText(setData(base.get("户型结构")));
        base_5.setText(setData(base.get("建筑类型")));
        base_6.setText(setData(base.get("房屋朝向")));
        base_7.setText(setData(base.get("建筑结构")));
        base_8.setText(setData(base.get("装修情况")));
        base_9.setText(setData(base.get("梯户比例")));
        base_10.setText(setData(base.get("供暖方式")));
        base_11.setText(setData(base.get("配备电梯")));
        base_12.setText(setData(base.get("产权年限")));
        HashMap<String,String> transaction=ershouDetail.getTransaction();
        transaction_1.setText(setData(transaction.get("挂牌时间")));
        transaction_2.setText(setData(transaction.get("交易权属")));
        transaction_3.setText(setData(transaction.get("上次交易")));
        transaction_4.setText(setData(transaction.get("房屋用途")));
        transaction_5.setText(setData(transaction.get("房屋年限")));
        transaction_6.setText(setData(transaction.get("产权所属")));
        transaction_7.setText(setData(transaction.get("抵押信息")));
        transaction_8.setText(setData(transaction.get("房本备件")));
        HashMap<String,String> features=ershouDetail.getFeatures();
        features_1.setText(setData(features.get("核心卖点")));
        features_2.setText(setData(features.get("房型介绍")));
        features_3.setText(setData(features.get("周边配套")));
        features_4.setText(setData(features.get("小区介绍")));
        des_img.setOnClickListener((v)->{
            showImageDetail(ershouDetail.getImageUrl().get("户型图"));
        });
        Glide.with(this).load(ershouDetail.getImageUrl().get("户型图")).into(des_img);
        //des_content
        List<String> des  = ershouDetail.getDes();
        if(des!=null&&des.size()>0){
            for(String d:des){
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                p.setMargins(10,10,10,10);
                TextView tv = new TextView(this);
                tv.setTextColor(getResources().getColor(R.color.black_font));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tv.setText(d);
                des_content.addView(tv,p);
            }
        }

        // 为XBanner绑定数据
        banner .setData(banner_Images,banner_title);//第二个参数为提示文字资源集合
        // XBanner适配数据
        banner.setmAdapter(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                Glide.with(FlatDetailActivity.this).load(banner_Images.get(position)).into((ImageView) view);
            }
        });
        // 设置XBanner的页面切换特效，选择一个即可，总的大概就这么多效果啦，欢迎使用
        banner.setPageTransformer(Transformer.Default);//横向移动
        // 设置XBanner页面切换的时间，即动画时长
        banner.setPageChangeDuration(1000);
        banner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, int position) {
                //Toast.makeText(FlatDetailActivity.this, "点击了第"+position+"图片", Toast.LENGTH_SHORT).show();
                String url = banner_Images.get(position);
                showImageDetail(url);
            }
        });
        //添加到我的二手房浏览记录
        addMyErshouFang(ershouDetail);
    }

    //添加到我的二手房浏览记录
    private void addMyErshouFang(ErshouDetail ershouDetail) {
        User user = ((MyApplication)getApplication()).getUser();
        if(user ==null) return;
        MyThreadPool.getInstance().submit(()-> {
            try{
                String[] column1 = new String[]{detailUrl,user.getPhone()};
                int[] type1 = new int[]{Types.CHAR, Types.CHAR};
                String sql1 = "select * from MyErshouFang where detailUrl = ? and phone = ?";
                ArrayList<HashMap<String,String>>d = DBUtils.query(column1, type1, sql1);
                if(d.size()>0) return;

                String[] cloumn = new String[]{
                        user.getPhone(),
                        ershouDetail.getTitle(), ershouDetail.getVillage_name(), ershouDetail.getVillage_address(),
                        String.valueOf(ershouDetail.getTotalPrice()),
                        String.valueOf(ershouDetail.getPrice_perMi()),
                        String.valueOf(ershouDetail.getRoom()), String.valueOf(ershouDetail.getHall()),
                        String.valueOf(ershouDetail.getArea()), ershouDetail.getToward(),
                        String.valueOf(flag), isGuoNei, detailUrl,
                        province, cityName, addressPre, " ", picUrl,"0","0"
                };
                int[] type = new int[]{Types.CHAR, Types.CHAR, Types.CHAR, Types.CHAR
                        , Types.DOUBLE, Types.DOUBLE, Types.INTEGER, Types.INTEGER, Types.DOUBLE,
                        Types.CHAR, Types.INTEGER, Types.CHAR, Types.CHAR, Types.CHAR,
                        Types.CHAR, Types.CHAR, Types.CHAR, Types.CHAR,Types.INTEGER,Types.INTEGER};
                String sql = "insert into MyErshouFang values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                DBUtils.CUD(cloumn, type, sql);
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    //显示图片详情
    private void showImageDetail(String detailUrl) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View imgEntryView = inflater.inflate(R.layout.photo_detail, null);
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        ImageView imgView = imgEntryView.findViewById(R.id.large_image);
        imgView.setOnClickListener((v)->{
            dialog.dismiss();
        });
        Glide.with(this)
                .load(detailUrl)
                .asBitmap()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .format(DecodeFormat.PREFER_ARGB_8888)//设置图片解码格式
                .placeholder(R.drawable.loading)
                .error(R.drawable.pic_notfound)
                .into(imgView);
        dialog.setContentView(imgEntryView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void startMapActivity(String address){
        Log.d(TAG,address);
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("address",address);
        startActivity(intent);
    }

    private String setData(String s){
        if("0.0".equals(s)) return  "暂无数据";
        if(!TextUtils.isEmpty(s)) return s;
        return "暂无数据";
    }

    public static Bitmap getbitmap(String imageUri) {
        // 显示网络上的图片
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

}
