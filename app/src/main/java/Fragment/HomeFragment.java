package Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suyong.sunshineflat.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Util.SharedPrefrenceUtils;
import Util.StatusBarUtils;
import activity.SelectCity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pojo.City;

public class HomeFragment extends BaseFragment {


    private List<String> listTitles;
    private List<Fragment> fragments;
    private ProgressDialog LoadDialog ;
    private City currentCity = null;
    private int requestCityCode = 0;
    private Unbinder unbinder;
    @BindView(R.id.place_for_select)
    TextView place_for_select;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.vp_view)
    ViewPager mViewPager;
    @BindView(R.id.custom_search)
    TextView custom_search;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StatusBarUtils.setStatusBarMode(getActivity(),true,android.R.color.white);
        View view=inflater .inflate(R.layout.home_fragment ,container,false) ;
        unbinder = ButterKnife.bind(this, view);
        //LoadDialog = LoadingDialog.createLoadingDialog(getActivity(),"提示","搜索城市房源信息...");
        initEvent();
        initData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefreshView();
    }

    //更新界面
    private void onRefreshView() {
        String name = SharedPrefrenceUtils.get(getContext(),"CurrentCityName");
        if(name == null) return;
        ((TextView)place_for_select.findViewById(R.id.place_for_select)).setText(name);
    }

    //注册事件
    private void initEvent() {
        place_for_select.setOnClickListener((v)->{
            startActivityForResult(new Intent(getActivity(), SelectCity.class), requestCityCode);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case 0:
                if(resultCode==20){
                    currentCity=(City)intent.getSerializableExtra("user");
                    HashMap<String,String>data = new HashMap<>();
                    data.put("CurrentCity",currentCity.getUrl());
                    data.put("CurrentCityName",currentCity.getName());
                    data.put("CurrentCountry",currentCity.getCityName());
                    String f="";
                    if(currentCity.getFlag()){
                        f="国内";
                    }else{
                        f="国外";
                    }
                    data.put("flag",f);
                    SharedPrefrenceUtils.insert(getContext(),data);
                    onRefreshView();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode,resultCode,intent);
    }

    private void initData() {
        listTitles = new ArrayList<>();
        fragments = new ArrayList<>();
        listTitles.add("新房");
        listTitles.add("二手房");
        listTitles.add("租房");
        for (int i = 0; i < listTitles.size(); i++) {
            HomeContentFragment fragment = HomeContentFragment.newInstance(i);
            fragments.add(fragment);
        }
        //mTabLayout.setTabMode(TabLayout.SCROLL_AXIS_HORIZONTAL);//设置tab模式，当前为系统默认模式
        for (int i=0;i<listTitles.size();i++){
            mTabLayout.addTab(mTabLayout.newTab().setText(listTitles.get(i)));//添加tab选项
        }

        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
            //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
            @Override
            public CharSequence getPageTitle(int position) {
                return listTitles.get(position);
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }
}
