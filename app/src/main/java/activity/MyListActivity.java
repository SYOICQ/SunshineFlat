package activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.dialog.myDialog.ActionSheetDialog;
import com.hb.dialog.myDialog.MyAlertDialog;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.suyong.sunshineflat.R;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Util.DBUtils;
import Util.LoadingDialog;
import Util.MyThreadPool;
import Util.StatusBarUtils;
import adapter.MyListActivityAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import pojo.MyListFlat;

public class MyListActivity extends AppCompatActivity {

    private String title1 ;

    private String phone ;
    private String type ;
    private ProgressDialog LoadDialog;

    @BindView(R.id.my_list_activity_list)
    RecyclerView recyclerView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.data_Empty)
    LinearLayout empty_view;

    private List<MyListFlat> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_list_activity);
        StatusBarUtils.setStatusBarMode(this,true, Color.parseColor("#0099FF"));
        ButterKnife.bind(this);
        LoadDialog = LoadingDialog.createLoadingDialog(this,"提示","加载中...");
        receiveData();
    }

    private void receiveData() {
        data.clear();
        Intent intent = getIntent();
        title1 = intent.getStringExtra("title");
        title.setText(title1);
        phone = intent.getStringExtra("phone");
        type = intent.getStringExtra("type");
        MyThreadPool.getInstance().submit(()->{
            runOnUiThread(()->{LoadDialog.show();});
            try{
                String sql = "";
                if("楼盘".equals(type)){
                    sql = "select * from MyLouPan where phone = ?";
                    String[] columns = new String[]{phone};
                    int [] types = new int[]{Types.CHAR};
                    dealwithNormal(sql,phone,type,columns,types);
                }else if("二手房".equals(type)){
                    sql = "select * from  MyErshouFang where phone = ?";
                    String[] columns = new String[]{phone};
                    int [] types = new int[]{Types.CHAR};
                    dealwithNormal(sql,phone,type,columns,types);
                }else if("国外楼盘".equals(type)){
                    sql = "select * from  MyForeignLouPan where phone = ?";
                    String[] columns = new String[]{phone};
                    int [] types = new int[]{Types.CHAR};
                    dealwithNormal(sql,phone,type,columns,types);
                }else if("租房".equals(type)){
                    sql = "select * from  MyZuFang where phone = ?";
                    String[] columns = new String[]{phone};
                    int [] types = new int[]{Types.CHAR};
                    dealwithNormal(sql,phone,type,columns,types);
                }else if("我喜欢".equals(type)){
                    dealWithMyLove(phone);
                }

                if(data.size()>0){
                    MyListActivityAdapter adapter = new MyListActivityAdapter(this,data);
                    adapter.setOnclicklistener((flat)->{
                        Intent mIntent = new Intent(this, FlatDetailActivity.class);
                        Bundle bundle = new Bundle();
                        if("国内".equals(flat.getIsGuoNei())) {
                            bundle.putString("picUrl",flat.getPicUrl());
                            bundle.putString("LouPanName",flat.getLouPanName());
                            bundle.putInt("flag",flat.getFlag());
                            bundle.putString("isGuoNei",flat.getIsGuoNei());
                            bundle.putString("CurrentProvince",flat.getProvince());
                            bundle.putString("CurrentCityName",flat.getCityName());
                            bundle.putString("detailUrl",flat.getDetailUrl());
                            bundle.putString("addressPre",flat.getAddressPre());
                        }else{
                            bundle.putString("picUrl", flat.getPicUrl());
                            bundle.putInt("flag", flat.getFlag());
                            bundle.putString("isGuoNei", flat.getIsGuoNei());
                            bundle.putString("LouPanName", flat.getLouPanName());
                            bundle.putString("CurrentProvince", flat.getProvince());
                            bundle.putString("CurrentCityName", flat.getCityName());
                            bundle.putString("detailUrl", flat.getDetailUrl());
                        }
                        mIntent.putExtra("Message",bundle);
                        startActivity(mIntent);
                    });
                    adapter.setOnLongclicklistener((flat)->{
                        if("我喜欢".equals(type)){
                            ActionSheetDialog dialog = new ActionSheetDialog(this).builder().setTitle("请选择")
                                    .addSheetItem("修改评分", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            String isGuoNei = flat.getIsGuoNei();
                                            int flag = flat.getFlag();
                                            String detailUrl = flat.getDetailUrl();
                                            final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(MyListActivity.this).builder()
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
                                                            String[] columns =new String[]{score,"1",phone,detailUrl};
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
                                                                Toast.makeText(MyListActivity.this,"修改成功!",Toast.LENGTH_SHORT).show();
                                                            });
                                                        }catch(Exception e){
                                                            e.printStackTrace();
                                                            runOnUiThread(()->{
                                                                LoadDialog.dismiss();
                                                                Toast.makeText(MyListActivity.this,"修改失败!",Toast.LENGTH_SHORT).show();
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
                                    }).addSheetItem("取消喜欢", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            String isGuoNei = flat.getIsGuoNei();
                                            int flag = flat.getFlag();
                                            String detailUrl = flat.getDetailUrl();
                                            final MyAlertDialog myAlertDialog = new MyAlertDialog(MyListActivity.this).builder()
                                                    .setTitle("呜呜呜...")
                                                    .setMsg("主人你不爱我了吗？")
                                                    .setPositiveButton("确认", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            MyThreadPool.getInstance().submit(()-> {
                                                                try{
                                                                    runOnUiThread(()->{
                                                                        LoadDialog.show();
                                                                    });
                                                                    String sql = "";
                                                                    String[] columns =new String[]{phone,detailUrl};
                                                                    int[] types = new int[]{Types.CHAR,Types.CHAR};
                                                                    if("国内".equals(isGuoNei)) {
                                                                        if (flag == 0) {
                                                                            sql = "delete from MyLouPan where phone = ? and detailUrl = ?";
                                                                        } else if (flag == 1) {
                                                                            sql = "delete from MyErshouFang where phone = ? and detailUrl = ?";
                                                                        } else if (flag == 2) {
                                                                            sql = "delete from MyZuFang where phone = ? and detailUrl = ?";
                                                                        }
                                                                    }else if("国外".equals(isGuoNei)){
                                                                        sql = "delete from MyForeignLouPan where phone = ? and detailUrl = ?";
                                                                    }
                                                                    DBUtils.CUD(columns,types,sql);
                                                                    runOnUiThread(()->{
                                                                        LoadDialog.dismiss();
                                                                        receiveData();
                                                                        //Toast.makeText(MyListActivity.this,"添加成功!",Toast.LENGTH_SHORT).show();
                                                                    });
                                                                }catch(Exception e){
                                                                    e.printStackTrace();
                                                                    runOnUiThread(()->{
                                                                        LoadDialog.dismiss();
                                                                        //Toast.makeText(MyListActivity.this,"失败!",Toast.LENGTH_SHORT).show();
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    }).setNegativeButton("取消", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                        }
                                                    });
                                            myAlertDialog.show();
                                        }
                                    });
                            dialog.show();
                        }else{
                            ActionSheetDialog dialog = new ActionSheetDialog(this).builder().setTitle("请选择")
                                    .addSheetItem("删除", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            String isGuoNei = flat.getIsGuoNei();
                                            int flag = flat.getFlag();
                                            String detailUrl = flat.getDetailUrl();
                                            MyAlertDialog myAlertDialog = new MyAlertDialog(MyListActivity.this).builder()
                                                    .setTitle("确认删除吗？")
                                                    .setMsg("")
                                                    .setPositiveButton("确认", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            MyThreadPool.getInstance().submit(()-> {
                                                                try{
                                                                    runOnUiThread(()->{
                                                                        LoadDialog.show();
                                                                    });
                                                                    String sql = "";
                                                                    String[] columns =new String[]{phone,detailUrl};
                                                                    int[] types = new int[]{Types.CHAR,Types.CHAR};
                                                                    if("国内".equals(isGuoNei)) {
                                                                        if (flag == 0) {
                                                                            sql = "delete from MyLouPan where phone = ? and detailUrl = ?";
                                                                        } else if (flag == 1) {
                                                                            sql = "delete from MyErshouFang where phone = ? and detailUrl = ?";
                                                                        } else if (flag == 2) {
                                                                            sql = "delete from MyZuFang where phone = ? and detailUrl = ?";
                                                                        }
                                                                    }else if("国外".equals(isGuoNei)){
                                                                        sql = "delete from MyForeignLouPan where phone = ? and detailUrl = ?";
                                                                    }
                                                                    DBUtils.CUD(columns,types,sql);
                                                                    runOnUiThread(()->{
                                                                        LoadDialog.dismiss();
                                                                        //Toast.makeText(MyListActivity.this,"添加成功!",Toast.LENGTH_SHORT).show();
                                                                        receiveData();
                                                                    });
                                                                }catch(Exception e){
                                                                    e.printStackTrace();
                                                                    runOnUiThread(()->{
                                                                        LoadDialog.dismiss();
                                                                        //Toast.makeText(MyListActivity.this,"失败!",Toast.LENGTH_SHORT).show();
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    }).setNegativeButton("取消", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                        }
                                                    });
                                            myAlertDialog.show();
                                        }
                                    });
                            dialog.show();
                        }
                    });

                    runOnUiThread(()-> {
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        recyclerView.setAdapter(adapter);
                        LoadDialog.dismiss();
                    });

                } else{
                    runOnUiThread(()-> {
                        recyclerView.setVisibility(View.GONE);
                        empty_view.setVisibility(View.VISIBLE);
                        LoadDialog.dismiss();
                    });
                }
            }catch(Exception e){
                e.printStackTrace();
                runOnUiThread(()->{
                    LoadDialog.dismiss();
                    Toast.makeText(this,"查询失败!",Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void dealwithNormal(String sql,String phone,String type, String[]columns,int [] types) throws Exception{
        ArrayList<HashMap<String,String>>data1 = DBUtils.query(columns,types,sql);
            for (HashMap<String, String> map:data1){
                MyListFlat flat = new MyListFlat();
                flat.setTitle_text(map.get("title"));
                flat.setImage(map.get("picUrl"));
                //设置flood
                if("楼盘".equals(type)){
                    flat.setFlood(map.get("address"));
                }else if("二手房".equals(type)){
                    flat.setFlood(map.get("villiage_address"));
                }else if("国外楼盘".equals(type)){
                    flat.setFlood(map.get("address"));
                }else if("租房".equals(type)){
                    flat.setFlood(map.get("address"));
                }else if("我喜欢".equals(type)){

                }
                //设置houseInfo
                if("楼盘".equals(type)){
                    flat.setHouseInfo(map.get("Hallstr"));
                }else if("二手房".equals(type)){
                    flat.setHouseInfo(map.get("room")+"室"+map.get("hall")
                            +"厅 | "+map.get("area")+"平米 | "+map.get("toward")
                    );
                }else if("国外楼盘".equals(type)){
                    flat.setHouseInfo(map.get("room")+" "+map.get("minArea")+"-"+map.get("maxArea")+"㎡");
                }else if("租房".equals(type)){
                    flat.setHouseInfo(map.get("area")+"㎡ / "+map.get("room")+"室"+map.get("hall")+"厅");
                }else if("我喜欢".equals(type)){

                }
                //followInfo
                if("楼盘".equals(type)){
                    flat.setFollowInfo("详情请点击");
                }else if("二手房".equals(type)){
                    flat.setFollowInfo("详情请点击");
                }else if("国外楼盘".equals(type)){
                    flat.setFollowInfo("详情请点击");
                }else if("租房".equals(type)){
                    flat.setFollowInfo("详情请点击");
                }else if("我喜欢".equals(type)){

                }
                //设置价格
                if("楼盘".equals(type)){
                    String str = map.get("price");
                    if(!"0.0".equals(str)){
                        str+="万/套";
                    }else if(!"0.0".equals(map.get("price_perMi"))){
                        str = map.get("price_perMi");
                        str+="元/平";
                    }else{
                        str="价格暂定";
                    }
                    flat.setPrice(str);
                }else if("二手房".equals(type)){
                    String str = map.get("total_price");
                    if(!"0.0".equals(str)){
                        str+="万/套";
                    }else if(!"0.0".equals(map.get("price_perMi"))){
                        str = map.get("price_perMi");
                        str+="元/平";
                    }else{
                        str="价格暂定";
                    }
                    flat.setPrice(str);
                } else if("国外楼盘".equals(type)){
                    flat.setPrice(map.get("minPrice")+"-"+map.get("maxPrice")+"万元");
                }else if("租房".equals(type)){
                    flat.setPrice(map.get("price")+"元/月");
                }else if("我喜欢".equals(type)){

                }
                flat.setFlag(Integer.parseInt(map.get("flag")));
                flat.setIsGuoNei(map.get("isGuoNei"));
                flat.setDetailUrl(map.get("detailUrl"));
                flat.setProvince(map.get("province"));
                flat.setCityName(map.get("cityName"));
                flat.setAddressPre(map.get("addressPre"));
                flat.setLouPanName(map.get("LouPanName"));
                flat.setPicUrl(map.get("picUrl"));
                flat.setProvince(map.get("province"));
                data.add(flat);
            }
    }

    //处理我的喜欢
    private void dealWithMyLove(String phone) throws Exception{
        String[] sql = new String[]{
                "select * from MyLouPan where phone = ? and love_flag = ?",
           "select * from  MyErshouFang where phone = ? and love_flag = ?",
            "select * from  MyZuFang where phone = ? and love_flag = ?",
                "select * from  MyForeignLouPan where phone = ? and love_flag = ?",
        };
        String[] fag= new String[]{"楼盘","二手房","租房","国外楼盘"};
        for(int i=0;i<sql.length;i++) {
            String[] columns = new String[]{phone,"1"};
            int [] types = new int[]{Types.CHAR,Types.INTEGER};
            dealwithNormal(sql[i],phone,fag[i],columns,types);
        }
    }

}
