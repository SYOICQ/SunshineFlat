package Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suyong.sunshineflat.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import CustomView.StickyDecoration;
import Util.CrawlerUtils;
import Util.LoadingDialog;
import Util.MyThreadPool;
import adapter.CityAdapter;
import pojo.City;

public class ContentFragment extends Fragment {

    private String TAG = "ContentFragment";
    private CityAdapter cityAdapter;
    private int flag ;
    private static final String KEY = "title";
    private ProgressDialog LoadDialog ;
    private LinkedHashMap<String, LinkedHashMap<String, List<City>>> guonei_cityList = new LinkedHashMap<String,LinkedHashMap<String,List<City>>>();
    private LinkedHashMap<String, LinkedHashMap<String, List<City>>> guowai_cityList = new LinkedHashMap<String,LinkedHashMap<String,List<City>>>();
    private RecyclerView recyclerView;
    private List<LinkedHashMap<String, List<City>>> compare = new ArrayList<LinkedHashMap<String, List<City>>>();
    private List<City>dataList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contentfragment,container,false);
        recyclerView = view.findViewById(R.id.recyclerView);
        flag = getArguments().getInt(KEY);
        LoadDialog = LoadingDialog.createLoadingDialog(getActivity(),"提示","加载中...");
        Log.d("ContentFragment", "onCreateView"+flag);
        return view;
    }

    private void init() {
        if(flag==0&&guonei_cityList.size()==0){
            //国内
            searchCityList(0);
        }else if(flag==1&&guowai_cityList.size()==0){
            //国外
            searchCityList(1);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        Log.d("ContentFragment", "onActivityCreated"+flag);
           init();
    }

    /**
     * fragment静态传值
     */
    public static ContentFragment newInstance(int i){
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY,i);
        fragment.setArguments(bundle);
        return fragment;
    }



    public void searchCityList(int i) {
        Runnable runnable = ()->{
            getActivity().runOnUiThread(()-> LoadDialog.show());
            try {
                if(i==0) {
                    guonei_cityList = CrawlerUtils.getCityList(1);
                    Iterator it =  guonei_cityList.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry<String,LinkedHashMap<String, List<City>>> entry = (Map.Entry) it.next();
                        compare.add(entry.getValue());
                        for (Map.Entry<String, List<City>> stringListEntry : entry.getValue().entrySet()) {
                            List<City> a = stringListEntry.getValue();
                            for(City s:a) dataList.add(s);
                        }
                    }
                }else if(i==1) {
                    guowai_cityList = CrawlerUtils.getCityList(0);
                    Iterator it =  guowai_cityList.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry<String,LinkedHashMap<String, List<City>>> entry = (Map.Entry) it.next();
                        compare.add(entry.getValue());
                        for (Map.Entry<String, List<City>> stringListEntry : entry.getValue().entrySet()) {
                                List<City> a = stringListEntry.getValue();
                                for(City s:a) dataList.add(s);
                        }
                    }
                }

                getActivity().runOnUiThread(()-> {
                    recyclerView.addItemDecoration(new StickyDecoration(getActivity(), new StickyDecoration.OnGroupListener(){
                        @Override
                        public String getGroupName(int position) {
                            if(compare.size()==0) return null;
                            City name = dataList.get(position);
                            for(LinkedHashMap<String, List<City>> lis:compare){
                                for (Map.Entry<String, List<City>> enetry : lis.entrySet()) {
                                    for(City c:enetry.getValue()){
                                        if(c.equals(name)) return enetry.getKey();
                                    }
                                }
                            }
                            return null;
                        }
                    }));
                    cityAdapter = new CityAdapter(getActivity(),dataList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    cityAdapter.setOnClickListener((city)->{
                        if(flag==0){
                            //国内
                            boolean f =true;
                            Iterator it =  guonei_cityList.entrySet().iterator();
                            while(it.hasNext()&&f){
                                Map.Entry<String,LinkedHashMap<String, List<City>>> entry = (Map.Entry) it.next();
                                for (Map.Entry<String, List<City>> stringListEntry : entry.getValue().entrySet()) {
                                    if(!f) break;
                                    List<City> a = stringListEntry.getValue();
                                    for(City s:a) {
                                        if(s.getName().equals(city.getName())) {city.setCityName(stringListEntry.getKey());f=false;break;}
                                    }
                                }
                            }
                        }else if(flag==1){
                            //国外
                            boolean f =true;
                            Iterator it =  guowai_cityList.entrySet().iterator();
                            while(it.hasNext()&&f){
                                Map.Entry<String,LinkedHashMap<String, List<City>>> entry = (Map.Entry) it.next();
                                for (Map.Entry<String, List<City>> stringListEntry : entry.getValue().entrySet()) {
                                    if(!f) break;
                                    List<City> a = stringListEntry.getValue();
                                    for(City s:a) {
                                        if(s.getName().equals(city.getName())) {city.setCityName(stringListEntry.getKey());f=false;break;}
                                    }
                                }
                            }
                        }
                        Intent mIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", city);
                        mIntent.putExtras(bundle);
                        // 设置结果，并进行传送
                        getActivity().setResult(20, mIntent);
                        EventBus.getDefault().post("你需要更新房源信息");
                        getActivity().finish();
                    });
                    recyclerView.setAdapter(cityAdapter);
                    cityAdapter.notifyDataSetChanged();
                    LoadDialog.dismiss();
                });
            }catch(Exception e){
                e.printStackTrace();
                getActivity().runOnUiThread(()-> LoadDialog.dismiss());
            }
        };
        MyThreadPool.getInstance().submit(runnable);
    }


}
