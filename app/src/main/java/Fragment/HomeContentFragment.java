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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.suyong.sunshineflat.R;
import com.zxl.library.DropDownMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Interf.MyItemClickListener;
import PullDownOptionViews.AreaView;
import PullDownOptionViews.HallView;
import PullDownOptionViews.PriceView;
import PullDownOptionViews.TowardView;
import Util.LoadingDialog;
import Util.ObserableUtils;
import Util.SharedPrefrenceUtils;
import activity.FlatDetailActivity;
import adapter.FlatAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pojo.Flat;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeContentFragment extends Fragment implements MyItemClickListener {

    @BindView(R.id.lay_fragment_FlatEmpty)
    LinearLayout empty_view;
    @BindView(R.id.drop_down)
    DropDownMenu mDropDownMenu;




    private String IsGuoNei="";
    private String TAG = "HomeContentFragment";
    private int flag ;
    private static final String KEY = "title";
    private ProgressDialog LoadDialog ;
    private RecyclerView recyclerView;
    private Unbinder unbinder;
    private boolean refreshFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contentfragment,container,false);
        recyclerView = view.findViewById(R.id.recyclerView);
        flag = getArguments().getInt(KEY);
        LoadDialog = LoadingDialog.createLoadingDialog(getActivity(),"提示","加载中...");
        unbinder = ButterKnife.bind(this, view);
        onRefresh();
        EventBus.getDefault().register(this);
        initView();
        Log.d(TAG, "onCreateView"+flag);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void onRefresh() {
        //更新房源信息
        String url = SharedPrefrenceUtils.get(getContext(),"CurrentCity");
        String isGuoNei = SharedPrefrenceUtils.get(getContext(),"flag");
        if(url==null) return;
        searchData(url,isGuoNei);
    }

    private void searchData(String url, String isGuoNei) {
        IsGuoNei = isGuoNei;
        if("国内".equals(isGuoNei)){
            if(!LoadDialog.isShowing()) {
                LoadDialog.show();
            }
            //Schedulers.io() 网络访问用这个 但这个好像是按顺序执行 所以采用newThread 实现多线程
            ObserableUtils.createGUoNei(url,flag).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Flat>>(){
                @Override
                public void onCompleted() {
                    LoadDialog.dismiss();
                }
                @Override
                public void onError(Throwable e) {
                    if(LoadDialog.isShowing()) {
                        LoadDialog.dismiss();
                    }
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(getActivity(),"出错啦！",Toast.LENGTH_SHORT);
                }
                @Override
                public void onNext(List<Flat> flats) {
                    if(flats.size()>0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        empty_view.setVisibility(View.GONE);
                        FlatAdapter adapter = new FlatAdapter(getContext(),flats);
                        adapter.setOnclicklistener((flat) ->{
                            String CurrentProvince = SharedPrefrenceUtils.get(getContext(),"CurrentCountry");
                            String CurrentCityName = SharedPrefrenceUtils.get(getContext(),"CurrentCityName");
                            String detailUrl = flat.getDetailUrl();
                            Intent mIntent = new Intent(getActivity(), FlatDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("picUrl",flat.getImage());
                            bundle.putString("LouPanName",flat.getTitle_text());
                            bundle.putInt("flag",flag);
                            bundle.putString("isGuoNei",isGuoNei);
                            bundle.putString("CurrentProvince",CurrentProvince+"省");
                            bundle.putString("CurrentCityName",CurrentCityName+"市");
                            bundle.putString("detailUrl",detailUrl);
                            if (flag == 0) {
                                //新房
                                bundle.putString("addressPre",flat.getFlood());
                            }else if(flag==1){
                                //二手房
                                bundle.putString("addressPre","");
                            }else if(flag==2){
                                //租房
                                int pos = flat.getHouseInfo().indexOf("/");
                                bundle.putString("addressPre",flat.getHouseInfo().substring(0,pos));
                            }
                            mIntent.putExtra("Message",bundle);
                            startActivity(mIntent);
                        });
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                    }else{
                        recyclerView.setVisibility(View.GONE);
                        empty_view.setVisibility(View.VISIBLE);
                    }
                }
            });
        }else if("国外".equals(isGuoNei)) {
            if(!LoadDialog.isShowing()) {
                LoadDialog.show();
            }
            ObserableUtils.createGUoWai(url,flag).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Flat>>(){
                @Override
                public void onCompleted() {
                    LoadDialog.dismiss();
                }
                @Override
                public void onError(Throwable e) {
                    if(LoadDialog.isShowing()) {
                        LoadDialog.dismiss();
                    }
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(getActivity(),"出错啦！",Toast.LENGTH_SHORT);
                }
                @Override
                public void onNext(List<Flat> flats) {
                    if(flats.size()>0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        empty_view.setVisibility(View.GONE);
                        FlatAdapter adapter = new FlatAdapter(getContext(), flats);
                        adapter.setOnclicklistener((flat) ->{
                            String CurrentProvince = SharedPrefrenceUtils.get(getContext(),"CurrentCountry");
                            String CurrentCityName = SharedPrefrenceUtils.get(getContext(),"CurrentCityName");
                            String detailUrl = flat.getDetailUrl();
                            Intent mIntent = new Intent(getActivity(), FlatDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("picUrl",flat.getImage());
                            bundle.putInt("flag",flag);
                            bundle.putString("isGuoNei",isGuoNei);
                            bundle.putString("LouPanName",flat.getTitle_text());
                            bundle.putString("CurrentProvince",CurrentProvince);
                            bundle.putString("CurrentCityName",CurrentCityName);
                            bundle.putString("detailUrl",detailUrl);
                            mIntent.putExtra("Message",bundle);
                            startActivity(mIntent);
                        });
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                    }else{
                        recyclerView.setVisibility(View.GONE);
                        empty_view.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }



    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        Log.d(TAG, "onActivityCreated"+flag);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getFragmentMsg(String msg){
        String[] custom = msg.split(":");
        if("你需要更新房源信息".equals(msg)){
            refreshFlag = true;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        IsGuoNei = SharedPrefrenceUtils.get(getContext(),"flag");
        if(refreshFlag) {
            onRefresh();
            refreshFlag = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:"+refreshFlag);
    }

    /**
     * fragment静态传值
     */
    public static HomeContentFragment newInstance(int i){
        HomeContentFragment fragment = new HomeContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY,i);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
         List <String>headers = new ArrayList<>();
         List<HashMap<String, Object>> viewDatas = new ArrayList<>();

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(DropDownMenu.KEY, DropDownMenu.TYPE_CUSTOM);
            PriceView priceView = new PriceView(getActivity());
            View view;
            if (flag == 2) {
                view = priceView.getPriceView("元");
            } else {
                view = priceView.getPriceView("万元");
            }
            priceView.setListener(this);
            map.put(DropDownMenu.VALUE, view);
            viewDatas.add(map);
            headers.add("价格");

        if(flag!=2) {
            headers.add("面积");
            HashMap<String, Object> map1 = new HashMap<String, Object>();
            map1.put(DropDownMenu.KEY, DropDownMenu.TYPE_CUSTOM);
            AreaView areaView = new AreaView(getActivity());
            areaView.setListener(this);
            map1.put(DropDownMenu.VALUE, areaView.getAreaView());
            viewDatas.add(map1);
        }

        if(flag!=2) {
            HashMap<String, Object> map2 = new HashMap<String, Object>();
            map2.put(DropDownMenu.KEY, DropDownMenu.TYPE_CUSTOM);
            HallView hallView = new HallView(getActivity());
            hallView.setListener(this);
            map2.put(DropDownMenu.VALUE, hallView.getHallView());
            viewDatas.add(map2);
            headers.add("户型");
        }
            if (flag != 0) {
                HashMap<String, Object> map3 = new HashMap<String, Object>();
                map3.put(DropDownMenu.KEY, DropDownMenu.TYPE_CUSTOM);
                TowardView towardView = new TowardView(getActivity());
                towardView.setListener(this);
                map3.put(DropDownMenu.VALUE, towardView.getTowardView());
                viewDatas.add(map3);
                headers.add("朝向");
            }

            /**
             * Dropdownmenu下面的主体部分
             * */
            View contentView = getLayoutInflater().inflate(R.layout.contentview, null);
            mDropDownMenu.setDropDownMenu(headers, viewDatas, contentView);
    }


    @Override
    public void onItemClick(View view, int postion, String string) {
        if("国外".equals(IsGuoNei)){Toast.makeText(getActivity(),"国外房源暂不支持筛选!",Toast.LENGTH_SHORT).show();return;}
        switch (postion){
            case 1:
                mDropDownMenu.closeMenu();
                Log.d("HomeContentFragment", string);
                DealWithPriceSelect(string);
                break;
            case 2:
                mDropDownMenu.closeMenu();
                Log.d("HomeContentFragment", string);
                DealWithAreaSelect(string);
                break;
            case 3:
                mDropDownMenu.closeMenu();
                Log.d("HomeContentFragment", string);
                DealWithHallSelect(string);
                break;
            case 4:
                mDropDownMenu.closeMenu();
                Log.d("HomeContentFragment", string);
                DealWithTowardSelect(string);
                break;
            default:
                break;
        }
    }

    private void DealWithTowardSelect(String string) {
        String url = SharedPrefrenceUtils.get(getContext(),"CurrentCity");
        if(url==null){Toast.makeText(getActivity(),"请先选择城市哦!",Toast.LENGTH_SHORT).show();return;}
        if(flag==1){
            //yikan
            String str="";
            if(string.indexOf("朝东")!=-1) str+="f1";
            if(string.indexOf("朝南")!=-1) str+="f2";
            if(string.indexOf("朝西")!=-1) str+="f3";
            if(string.indexOf("朝北")!=-1) str+="f4";
            if(string.indexOf("南北")!=-1) str+="f5";
            String new_url = url+"/ershoufang/{pg}"+str+"{toward}";
            searchData(new_url,IsGuoNei);
        }else if(flag==2){
            //yikan
            String str="";
            if(string.indexOf("朝东")!=-1) str+="f100500000001";
            if(string.indexOf("朝南")!=-1) str+="f100500000003";
            if(string.indexOf("朝西")!=-1) str+="f100500000005";
            if(string.indexOf("朝北")!=-1) str+="f100500000007";
            if(string.indexOf("南北")!=-1) str+="f100500000009";
            String new_url = url+"/zufang/{pg}"+str+"{toward}";
            searchData(new_url,IsGuoNei);
        }
    }

    private void DealWithHallSelect(String string) {
        String url = SharedPrefrenceUtils.get(getContext(),"CurrentCity");
        if(url==null){Toast.makeText(getActivity(),"请先选择城市哦!",Toast.LENGTH_SHORT).show();return;}
        if(flag==0){
            //yikan
            String new_url = url+"/loupan/{pg}"+string+"{hall}";
            searchData(new_url,IsGuoNei);
        }else if(flag==1){
            //yikan
            String new_url = url+"/ershoufang/{pg}"+string+"{hall}";
            searchData(new_url,IsGuoNei);
        }
    }

    //处理面积
    private void DealWithAreaSelect(String string) {
        String area[] = string.split("-");
        String url = SharedPrefrenceUtils.get(getContext(),"CurrentCity");
        if(url==null){Toast.makeText(getActivity(),"请先选择城市哦!",Toast.LENGTH_SHORT).show();return;}
        if(flag==0){
            //yikan
             String new_url = url+"/loupan/"+"bba"+area[0]+"eba"+area[1]+"{page}{area}";
            searchData(new_url,IsGuoNei);
        }else if(flag==1){
            //yikan
            String new_url = url+"/ershoufang/"+"{page}ba"+area[0]+"ea"+area[1];
            searchData(new_url,IsGuoNei);
        }
    }

    //处理价格
    private void DealWithPriceSelect(String string) {
        String price[] = string.split("-");
        String url = SharedPrefrenceUtils.get(getContext(),"CurrentCity");
        if(url==null){Toast.makeText(getActivity(),"请先选择城市哦!",Toast.LENGTH_SHORT).show();return;}
        if(flag==0){
            //yikan
            String new_url = url+"/loupan/"+"bp"+price[0]+"ep"+price[1]+"{page}";
            searchData(new_url,IsGuoNei);
        }else if(flag==1){
            //yikan
            String new_url = url+"/ershoufang/"+"{page}bp"+price[0]+"ep"+price[1];
            searchData(new_url,IsGuoNei);
        }else if(flag==2){
            //yikan
            String new_url = url+"/zufang/"+"{page}brp"+price[0]+"erp"+price[1];
            searchData(new_url,IsGuoNei);
        }

    }


}
