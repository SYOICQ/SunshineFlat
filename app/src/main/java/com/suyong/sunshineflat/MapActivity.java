package com.suyong.sunshineflat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.hb.dialog.myDialog.MyAlertInputDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Util.LoadingDialog;
import Util.MyThreadPool;
import Util.PoiOverlay;

public class MapActivity extends AppCompatActivity implements OnGetGeoCoderResultListener{

    private  final String TAG = "MapActivity";

    //当前中心点
    LatLng currentPoint = null;
    //存放的周围搜索的结果
    HashMap<String,List<Object>> posList = new HashMap<>();
    private int k =0;
    Button switch_mapType;
    Button search_nearBy;
    int[] mapTypes = new int[]{BaiduMap.MAP_TYPE_NORMAL,BaiduMap.MAP_TYPE_SATELLITE};
    String[] mapTypesString = new String[]{"普通模式","卫星模式"};
    private ProgressDialog LoadDialog;
    String address = "";

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    GeoCoder mCoder = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        address = getIntent().getStringExtra("address").replaceAll(" ","").replaceAll("-","").replaceAll("/","");
        initView();
        drawPoint();
    }

    private void drawPoint() {
        Log.d("MapActivity", address);
        int countryPos = address.indexOf("国");
        int provincePos = address.indexOf("省");
        String city = address.substring(countryPos+1,provincePos);
        String add = address.substring(provincePos+1);
        Log.d("MapActivity", city+":::"+add);
        LoadDialog.show();
        MyThreadPool.getInstance().submit(() ->{
            mCoder.geocode(new GeoCodeOption()
                    .city(city)
                    .address(add));
        });
    }

    private void initView() {
        LoadDialog = LoadingDialog.createLoadingDialog(this,"提示","加载中...");
        switch_mapType = findViewById(R.id.switch_mapType);
        switch_mapType.setText(mapTypesString[k]);
        search_nearBy = findViewById(R.id.search_nearBy);
        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(mapTypes[k]);
        mCoder = GeoCoder.newInstance();
        intEvent();
    }

    private void intEvent() {
        switch_mapType.setOnClickListener((v)->{
            k++;
            switch_mapType.setText(mapTypesString[k%2]);
            mBaiduMap.setMapType(mapTypes[k%2]);
        });
        mCoder.setOnGetGeoCodeResultListener(this);
        search_nearBy.setOnClickListener((v)->{
            final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(this).builder()
                    .setTitle("请输入你要搜索的内容比如(酒店、饭店、学校)")
                    .setEditText("");
            myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nearbyPoiSearch(currentPoint, myAlertInputDialog.getResult());
                    myAlertInputDialog.dismiss();
                }
            }).setNegativeButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myAlertInputDialog.dismiss();
                }
            });
            myAlertInputDialog.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mCoder.destroy();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if (null != geoCodeResult && null != geoCodeResult.getLocation()) {
            if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
                return;
            } else {
                double latitude = geoCodeResult.getLocation().latitude;
                double longitude = geoCodeResult.getLocation().longitude;
                Log.d("MapActivity", latitude + ":" + longitude);
                //定义Maker坐标点
                LatLng point = new LatLng(latitude, longitude);
                currentPoint = point;
                addOneMarker(point);
                //移动
                moveToPoint(point);
                //搜索附近设施
                LoadDialog.dismiss();
            }
        }
    }

    private void moveToPoint(LatLng point) {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }


    /**
     * 周边poi检索示例
     */
    public void nearbyPoiSearch(LatLng center,String content) {
        MyThreadPool.getInstance().submit(()->{
        //创建poi检索实例
        PoiSearch poiSearch = PoiSearch.newInstance();
        //创建poi监听者
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if(poiResult.getAllPoi()==null||poiResult.getAllPoi().size()<=0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MapActivity.this,"尴尬,好像没有哦！",Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                //获取POI检索结果
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    mBaiduMap.clear();
                    //创建地区marker
                   addOneMarker(center);
                   //创建PoiOverlay对象
                   PoiOverlay poiOverlay = new PoiOverlay(mBaiduMap);
                    List<PoiInfo>poi = poiResult.getAllPoi();
                    posList.clear();
                    if(poi.size()<=0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapActivity.this,"尴尬,好像没有哦！",Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    for(int i=0;i<poi.size();i++){
                        LatLng position = poi.get(i).location;
                        Log.d("MapActivity", poi.get(i).address+poi.get(i).name);
                        BitmapDescriptor bitmap = null;
                        if("饭店".equals(content)||"酒店".equals(content)||"餐馆".equals(content)){
                            bitmap = BitmapDescriptorFactory
                                    .fromResource(R.drawable.restaurant);
                        }else if("学校".equals(content)||"教育".equals(content)){
                            bitmap = BitmapDescriptorFactory
                                    .fromResource(R.drawable.school);
                        }else if("小卖部".equals(content)||"超市".equals(content)){
                            bitmap = BitmapDescriptorFactory
                                    .fromResource(R.drawable.supermarket);
                        } else if("交通工具".equals(content)||"地铁站".equals(content)||"地铁".equals(content)||"公交站".equals(content)||"公交".equals(content)){
                            bitmap = BitmapDescriptorFactory
                                    .fromResource(R.drawable.underground);
                        } else{
                            bitmap = BitmapDescriptorFactory
                                    .fromResource(R.drawable.poi);
                        }
                        //构建MarkerOption，用于在地图上添加Marker
                        OverlayOptions option = new MarkerOptions()
                                .position(position) //必传参数
                                .title("p"+i)
                                .icon(bitmap); //必传参数
                        //在地图上添加Marker，并显示
                        mBaiduMap.addOverlay(option);
                        List<Object> ob = new ArrayList<>();
                        ob.add(position);
                        ob.add(poi.get(i).address+poi.get(i).name);
                        posList.put("p"+i,ob);
                    }
                    poiOverlay.addToMap();
                    poiOverlay.zoomToSpan();
                    mBaiduMap.setOnMarkerClickListener((marker) -> {
                        String tag = marker.getTitle();
                        List<Object> ob = posList.get(tag);
                        if(ob==null) return true;
                        Button button = new Button(MapActivity.this);
                        button.setBackgroundResource(R.drawable.textview_yuanjiao_touming);
                        button.setText((String) ob.get(1));
                        //构造InfoWindow
                        //point 描述的位置点
                        //-100 InfoWindow相对于point在y轴的偏移量
                        InfoWindow mInfoWindow = new InfoWindow(button, (LatLng) ob.get(0), -100);
                        //使InfoWindow生效
                        mBaiduMap.showInfoWindow(mInfoWindow);
                        return true;
                    });
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }

        };
        //设置poi监听者该方法要先于检索方法searchNearby(PoiNearbySearchOption)前调用，否则会在某些场景出现拿不到回调结果的情况
        poiSearch.setOnGetPoiSearchResultListener(poiListener);
        //设置请求参数
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword(content)//检索关键字
                .location(center)//检索位置
                .pageNum(2)//分页编号，默认是0页
                .pageCapacity(10)//设置每页容量，默认10条
                .radius(5000);//附近检索半径 m
        //发起请求
        poiSearch.searchNearby(nearbySearchOption);
        //释放检索对象
        poiSearch.destroy();
        });
    }

    private void addOneMarker(LatLng poi) {
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ershou_flat);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(poi)
                .icon(bitmap);

        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

    }
}
