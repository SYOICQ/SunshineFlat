package Util;

import java.util.ArrayList;
import java.util.List;

import pojo.Flat;
import rx.Observable;
import rx.Subscriber;

public class ObserableUtils {

    //查询国内的房源数据
    public static Observable<List<Flat>> createGUoNei(String url,int flag){
        return Observable.create(new Observable.OnSubscribe<List<Flat>>() {
            @Override
            public void call(Subscriber<? super List<Flat>> subscriber) {
               List<Flat> result = new ArrayList<>();
                try {
                   if (flag == 0) {
                       result = CrawlerUtils.getLouPan(url);
                    }else if(flag==1){
                        result = CrawlerUtils.getErShouFang(url);
                    }else if(flag==2){
                        result = CrawlerUtils.getZuFang(url);
                    }
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    //查询国外的房源数据
    public static Observable<List<Flat>> createGUoWai(String url,int flag){
        return Observable.create(new Observable.OnSubscribe<List<Flat>>() {
            @Override
            public void call(Subscriber<? super List<Flat>> subscriber) {
                List<Flat> result = new ArrayList<>();
                try {
                    if (flag == 0) {
                        result = CrawlerUtils.getForeignFlat(url);
                    }else if(flag==1){

                    }else if(flag==2){

                    }
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    //分析国内的房源详细信息
    public static Observable<List<Object>> parseGuoNei(int flag,String detailUrl,String ProvinceName,String CurrentCityName,String addressPre){
        return Observable.create(new Observable.OnSubscribe<List<Object>>(){

            @Override
            public void call(Subscriber<? super List<Object>> subscriber) {
                List<Object> result = new ArrayList<>();
                try {
                    if (flag == 0) {
                        //新房
                       //result.add("新房");
                       result.add(CrawlerUtils.parseLouPan(detailUrl,"中国"+ProvinceName+CurrentCityName+addressPre));
                    }else if(flag==1){
                        //二手房
                        //result.add("二手房");
                        result.add(CrawlerUtils.parseErShouFang(detailUrl,"中国"+ProvinceName+CurrentCityName));
                    }else if(flag==2){
                        //租房
                        //result.add("租房");
                        result.add(CrawlerUtils.parseZufang(detailUrl,"中国"+ProvinceName+CurrentCityName+addressPre));
                    }
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    //分析国外的房源详细信息
    public static Observable<List<Object>> parseGuoWai(int flag,String detailUrl){
        return Observable.create(new Observable.OnSubscribe<List<Object>>(){

            @Override
            public void call(Subscriber<? super List<Object>> subscriber) {
                List<Object> result = new ArrayList<>();
                try {
                    if (flag == 0) {
                        //新房
                        //result.add("新房");
                        result.add(CrawlerUtils.parseGuoWaiLouPan(detailUrl));
                    }else if(flag==1){
                        //二手房

                    }else if(flag==2){
                        //租房

                    }
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }
}
