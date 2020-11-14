package Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.suyong.sunshineflat.MyApplication;
import com.suyong.sunshineflat.R;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Util.DBUtils;
import Util.MyThreadPool;
import Util.RecommendUtils;
import Util.StatusBarUtils;
import activity.FlatDetailActivity;
import adapter.MyListActivityAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pojo.MyListFlat;
import pojo.User;

public class LoveFragment extends BaseFragment {
    private Unbinder unbinder;
    private List<String> mDatas;
    private Handler mHandler;
    private List<MyListFlat> Maxdata = new ArrayList<>();

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.data_Empty)
    LinearLayout empty_view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StatusBarUtils.setStatusBarMode(getActivity(),true,android.R.color.white);
        View view=inflater .inflate(R.layout.love_fragment ,container,false) ;
        unbinder = ButterKnife.bind(this, view);
        initData();
        initEvent();
        return view;
    }

    private void initData() {
        mHandler = new Handler();
        recyclerView.setVisibility(View.GONE);
        empty_view.setVisibility(View.VISIBLE);
    }

    private void initEvent() {
//        LoveAdapter adapter = new LoveAdapter(getActivity(), mDatas);
//        //设置列表布局
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setAdapter(adapter);

        //设置 Header 为 贝塞尔雷达 样式
        mSmartRefreshLayout.setRefreshHeader(new BezierRadarHeader(getActivity()).setEnableHorizontalDrag(true));
        //设置 Footer 为 球脉冲 样式
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.autoRefresh();
        /*下拉刷新*/
        mSmartRefreshLayout.setOnRefreshListener((refreshLayout) ->{
                    //延时展示，延时2秒
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OnRefresh();//刷新加载数据

                        }
                    },2000);
        }
        );

//        /*上拉加载*/
//        mSmartRefreshLayout.setOnLoadMoreListener((refreshLayout)-> {
//                    //延时展示，延时2秒
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            initData();
//                            adapter.refresh(mDatas);
//                            mSmartRefreshLayout.finishLoadMore();
//                        }
//                    },2000);
//        }
//        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void OnRefresh() {
        MyThreadPool.getInstance().submit(()->{
        try {

            User user = ((MyApplication) getActivity().getApplication()).getUser();
            if (user == null) {
                getActivity().runOnUiThread(()->{
                    if(mSmartRefreshLayout!=null) {
                        mSmartRefreshLayout.finishRefresh();
                    }
                    ///显示用户还未登陆的提示
                    Toast.makeText(getActivity(),"你还没有登陆哦!",Toast.LENGTH_SHORT).show();
                });
                return;
            }
           if(TextUtils.isEmpty(getAllUser(user.getPhone()))){
               getActivity().runOnUiThread(()->{
                   if(mSmartRefreshLayout!=null) {
                       mSmartRefreshLayout.finishRefresh();
                   }
                   //显示暂无推荐
                   recyclerView.setVisibility(View.GONE);
                   empty_view.setVisibility(View.VISIBLE);
                   Toast.makeText(getActivity(),"尴尬,你喜欢的房源太少啦，无法推荐!",Toast.LENGTH_SHORT).show();
               });
               return;
           }


            String recommendUser = user.getPhone();
            String sql = "select phone from user";
            ArrayList<HashMap<String, String>> list = DBUtils.query(null, null, sql);
            List<String> data = new ArrayList<>();
            String str = "";
            for (HashMap<String, String> u : list) {
                String id = u.get("phone");
                str = getAllUser(id);
                if(TextUtils.isEmpty(str)) continue;
                String love_list = str;
                data.add(id + love_list);
            }
            LinkedHashMap<String, Double> map2 = RecommendUtils.getRceommend(data, recommendUser, data.size());

            //没有推荐数据
            if(map2.size()==0){
                getActivity().runOnUiThread(()->{
                    if(mSmartRefreshLayout!=null) {
                        mSmartRefreshLayout.finishRefresh();
                    }
                    //显示暂无推荐
                    recyclerView.setVisibility(View.GONE);
                    empty_view.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(),"尴尬,你喜欢的房源太少啦，无法推荐!",Toast.LENGTH_SHORT).show();
                });
                return;
            }

            //执行到这儿说明程序可以向用户推荐数据
            //遍历输出
           String[] url = new String[10];
            int k=0;
            for (Map.Entry<String, Double> entry : map2.entrySet()) {
               Log.d("after",entry.getKey() + ":" + entry.getValue());
                if(k==10){break;}
               url[k]=entry.getKey();
                k++;
            }
            Maxdata.clear();
            dealWithMyLove(url);
            getActivity().runOnUiThread(()->{
                if(mSmartRefreshLayout!=null) {
                    mSmartRefreshLayout.finishRefresh();
                }
                //显示数据
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                MyListActivityAdapter adapter = new MyListActivityAdapter(getActivity(),Maxdata);
                adapter.setOnclicklistener((flat)->{
                    Intent mIntent = new Intent(getActivity(), FlatDetailActivity.class);
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
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                empty_view.setVisibility(View.GONE);
            });
        }catch (Exception e){
            e.printStackTrace();
            getActivity().runOnUiThread(()->{
                if(mSmartRefreshLayout!=null) {
                    mSmartRefreshLayout.finishRefresh();
                }
            });
        }
        });
    }


    private String getAllUser(String phone) throws Exception {
        String[] sql = new String[]{
                "select * from MyLouPan where phone = ? and love_flag = ?",
                "select * from  MyErshouFang where phone = ? and love_flag = ?",
                "select * from  MyZuFang where phone = ? and love_flag = ?",
                "select * from  MyForeignLouPan where phone = ? and love_flag = ?",
        };
        StringBuilder builder = new StringBuilder();
        builder.append(" ");
        for(int i=0;i<sql.length;i++) {
            String[] columns = new String[]{phone, "1"};
            int[] types = new int[]{Types.CHAR, Types.INTEGER};
            ArrayList<HashMap<String, String>> data1 = DBUtils.query(columns, types, sql[i]);
            if(data1.size()==0) continue;
            for(HashMap<String, String> d:data1){
                builder.append(d.get("detailUrl"));
                builder.append(" ");
            }
        }
        String result = builder.deleteCharAt(builder.length() - 1).toString();
        return  result;
    }


    //处理我的喜欢
    private void dealWithMyLove(String []detailUrl) throws Exception {
        String[] sql = new String[]{
                "select * from MyLouPan where detailUrl = ?",
                "select * from  MyErshouFang where detailUrl = ?",
                "select * from  MyZuFang where detailUrl = ?",
                "select * from  MyForeignLouPan where detailUrl = ?",
        };
        String[] sql1 = new String[]{
                "select * from MyLouPan where phone = ? and love_flag = ?",
                "select * from  MyErshouFang where phone = ? and love_flag = ?",
                "select * from  MyZuFang where phone = ? and love_flag = ?",
                "select * from  MyForeignLouPan where phone = ? and love_flag = ?",
        };
        String[] fag = new String[]{"楼盘", "二手房", "租房", "国外楼盘"};
//        for (int i = 0; i < sql.length; i++) {
//            String[] columns = new String[]{phone, "1"};
//            int[] types = new int[]{Types.CHAR, Types.INTEGER};
//            dealwithNormal(sql[i], phone, fag[i], columns, types);
//        }
        for(int i=0;i<detailUrl.length;i++){
            String[] column = new String[]{detailUrl[i]};
            int[] type = new int[]{Types.CHAR};
            for(int j=0;j<sql.length;j++){
                ArrayList< HashMap<String,String>>d1 = DBUtils.query(column,type,sql[j]);
                if(d1!=null&&d1.size()>0){
                    HashMap<String,String> d = d1.get(0);
                    dealwithNormal(d,fag[j]);
                    break;
                }
            }
            Log.d("LoveFragment", "data.size():" + Maxdata.size());
        }
    }

    private void dealwithNormal( HashMap<String,String>map ,String type) throws Exception{


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
        Maxdata.add(flat);

    }
}
