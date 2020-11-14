package Util;

import android.text.TextUtils;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import pojo.City;
import pojo.ErshouDetail;
import pojo.Flat;
import pojo.ForeignFlatDetail;
import pojo.LoupanDetail;
import pojo.ZufangDetail;

public class CrawlerUtils {

    private String TAg="CrawlerUtils";

    /**
     * 获取国外房源
     * @param url
     * @return
     * @throws Exception
     */
    public static List<Flat> getForeignFlat(String url) throws Exception{
        List<Flat> result = new ArrayList<>();
        //获取页数
        int totalPage = 0;
        String base = url.replaceFirst("m/","m/newhomes/");
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        String s = document.select("div.page-box").attr("page-data");
        if(s==null||"".equals(s)) return result;
        String[] d = s.split(",");
        String tem = d[0];
        int pos = tem.indexOf(':');
        totalPage = Integer.parseInt(tem.substring(pos+1));
        if(totalPage==0) return result;
        if(totalPage>=30) {
            totalPage = 30;
        }
        //获取国外新房数据
        base = base+"/pg{page}";
        for(int i=1;i<=totalPage;i++) {
            String cityUrl = base.replace("{page}", String.valueOf(i));
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats= doc.select("div.list-wrap dl.clear");
            for(Element flat:flats) {
                Flat f = new Flat();
                String image = flat.select("img").attr("src");
                int index= url.indexOf("m/");
                String detailUrl = url.substring(0,index+1)+flat.select("a").attr("href");
                Elements flat2 = flat.select("dd");
                String title = flat2.select("h3").text();
                String price = flat2.select("div.list-price div.unit-price").text();
                String houseInfo= flat2.select("div.list-det").eq(1).text();
                String flood = flat2.select("div.list-det").eq(0).text();
                f.setImage(image);
                f.setDetailUrl(detailUrl);
                f.setTitle_text(title);
                f.setPrice(price);
                f.setHouseInfo(houseInfo);
                f.setFlood(flood);
                System.out.println(f);
                result.add(f);
            }
        }
        return result;
    }


    /**
     * 获取租房数据
     * @param url
     * @return
     * @throws Exception
     */
    public static List<Flat> getZuFang(String url) throws Exception{
        if(url.indexOf("{page}")!=-1){
            return getCustomZuFang(url);
        }
        if(url.indexOf("{toward}")!=-1){
            return getZuFang_Toward(url.replace("{toward}",""));
        }
        List<Flat> result = new ArrayList<>();
        //获取页数
        int totalPage = 0;
        String base = url+"/zufang";
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        String s= document.select("div.content__pg").attr("data-totalpage");
        if(s==null||"".equals(s)) return result;
        totalPage = Integer.parseInt(s);
        if(totalPage==0) return result;
        if(totalPage>=30) {
            totalPage = 30;
        }
        base = base+"/pg{page}";
        //获取房源信息
        for(int i =1;i<=totalPage;i++) {
            String cityUrl = base.replace("{page}", String.valueOf(i));
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats= doc.select("div.content__list div.content__list--item");
            for(Element flat:flats) {
                Flat f = new Flat();
                Elements flat1 = flat.select("a.content__list--item--aside");
                String image = flat1.select("img").attr("data-src");
                f.setImage(image);
                Elements flat2 = flat.select("div.content__list--item--main");
                String title = flat2.select(".content__list--item--title").text();
                String detailUrl = url + flat2.select("p.content__list--item--title a").attr("href");
                String houseInfo = flat2.select("p.content__list--item--des").text();
                String price = flat2.select("span.content__list--item-price").text();
                f.setTitle_text(title);
                f.setDetailUrl(detailUrl);
                f.setHouseInfo(houseInfo);
                f.setPrice(price);
                System.out.println(f);
                result.add(f);
            }
        }
        return result;
    }

    private static List<Flat> getZuFang_Toward(String url) throws Exception{
        int pos = url.indexOf("com/");
        String url1 = url.substring(0,pos+3);
        List<Flat> result = new ArrayList<>();
        //获取页数
        int totalPage = 0;
        String base = url.replace("{pg}","pg1");
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        String s= document.select("div.content__pg").attr("data-totalpage");
        if(s==null||"".equals(s)) return result;
        totalPage = Integer.parseInt(s);
        if(totalPage==0) return result;
        if(totalPage>=30) {
            totalPage = 30;
        }
        //获取房源信息
        for(int i =1;i<=totalPage;i++) {
            String cityUrl =  url.replace("{pg}","pg"+i);
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats= doc.select("div.content__list div.content__list--item");
            for(Element flat:flats) {
                Flat f = new Flat();
                Elements flat1 = flat.select("a.content__list--item--aside");
                String image = flat1.select("img").attr("data-src");
                f.setImage(image);
                Elements flat2 = flat.select("div.content__list--item--main");
                String title = flat2.select(".content__list--item--title").text();
                String detailUrl = url1 + flat2.select("p.content__list--item--title a").attr("href");
                String houseInfo = flat2.select("p.content__list--item--des").text();
                String price = flat2.select("span.content__list--item-price").text();
                f.setTitle_text(title);
                f.setDetailUrl(detailUrl);
                f.setHouseInfo(houseInfo);
                f.setPrice(price);
                System.out.println(f);
                result.add(f);
            }
        }
        return result;
    }

    private static List<Flat> getCustomZuFang(String url) throws Exception{
        int pos = url.indexOf("com/");
        String url1 = url.substring(0,pos+3);
        List<Flat> result = new ArrayList<>();
        //获取页数
        int totalPage = 0;
        String base = url.replace("{page}","pg1");
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        String s= document.select("div.content__pg").attr("data-totalpage");
        if(s==null||"".equals(s)) return result;
        totalPage = Integer.parseInt(s);
        if(totalPage==0) return result;
        if(totalPage>=30) {
            totalPage = 30;
        }
        //获取房源信息
        for(int i =1;i<=totalPage;i++) {
            String cityUrl = url.replace("{page}","pg"+i);
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats= doc.select("div.content__list div.content__list--item");
            for(Element flat:flats) {
                Flat f = new Flat();
                Elements flat1 = flat.select("a.content__list--item--aside");
                String image = flat1.select("img").attr("data-src");
                f.setImage(image);
                Elements flat2 = flat.select("div.content__list--item--main");
                String title = flat2.select(".content__list--item--title").text();
                String detailUrl = url1 + flat2.select("p.content__list--item--title a").attr("href");
                String houseInfo = flat2.select("p.content__list--item--des").text();
                String price = flat2.select("span.content__list--item-price").text();
                f.setTitle_text(title);
                f.setDetailUrl(detailUrl);
                f.setHouseInfo(houseInfo);
                f.setPrice(price);
                System.out.println(f);
                result.add(f);
            }
        }
        return result;
    }

    /**
     * 获取新房数据
     * @param url
     * @return
     * @throws Exception
     */
    public static List<Flat> getLouPan(String url) throws Exception {
        if(url.indexOf("{area}")!=-1){
            return getCustomLouPanArea(url.replace("{area}",""));
        }
        if(url.indexOf("{page}")!=-1){
            return getCustomLouPan(url);
        }
        if(url.indexOf("{hall}")!=-1){
            return getCustomLouPan_Hall(url.replace("{hall}",""));
        }
        List<Flat> result = new ArrayList<>();
        //获取页数
        int totalPage = 0;
        String base = url+"/loupan";
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        String s= document.select(".se-part").eq(1).text();
        if(s==null||"".equals(s)) return result;
        String[]a = s.split(" ");
        totalPage = Integer.parseInt(a[a.length-1]);
        if(totalPage>=30) {
            totalPage = 30;
        }
        base = base+"/pg{page}";
        //获取房源信息
        for(int i =1;i<=totalPage;i++) {
            String cityUrl = base.replace("{page}", String.valueOf(i));
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats = doc.select("ul.resblock-list-wrapper li.resblock-list");
            for(Element flat:flats) {
                Flat f = new Flat();
                Elements flat1 = flat.select("a.resblock-img-wrapper img.lj-lazy");
                String image = flat1.attr("data-original");
                f.setImage(image);
                Elements flat2 = flat.select("div.resblock-desc-wrapper");
                Elements title= flat2.select("div.resblock-name a.name");
                String detailUrl  = url +title.attr("href");
                String title_text = title.text();
                String flood = flat2.select("a.resblock-location").text();
                String houseInfo = flat2.select("a.resblock-room").text();
                String followInfo = "详细请点击";
                String price = flat2.select("div.resblock-price div.main-price").text();
                f.setDetailUrl(detailUrl);
                f.setTitle_text(title_text);
                f.setFlood(flood);
                f.setHouseInfo(houseInfo);
                f.setFollowInfo(followInfo);
                f.setPrice(price);
                System.out.println(f);
                result.add(f);
            }
        }

        return result;
    }

    private static List<Flat> getCustomLouPanArea(String url) throws Exception{
        int pos = url.indexOf("com/");
        String url1 = url.substring(0,pos+3);
        List<Flat> result = new ArrayList<>();
        //获取页数
        int totalPage = 0;
        String base = url.replace("{page}","pg1");
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        String s= document.select(".se-part").eq(1).text();
        if(s==null||"".equals(s)) return result;
        String[]a = s.split(" ");
        totalPage = Integer.parseInt(a[a.length-1]);
        if(totalPage>=30) {
            totalPage = 30;
        }
        //获取房源信息
        for(int i =1;i<=totalPage;i++) {
            String cityUrl = url.replace("{page}", "pg"+i);
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats = doc.select("ul.resblock-list-wrapper li.resblock-list");
            for(Element flat:flats) {
                Flat f = new Flat();
                Elements flat1 = flat.select("a.resblock-img-wrapper img.lj-lazy");
                String image = flat1.attr("data-original");
                f.setImage(image);
                Elements flat2 = flat.select("div.resblock-desc-wrapper");
                Elements title= flat2.select("div.resblock-name a.name");
                String detailUrl  = url1 +title.attr("href");
                String title_text = title.text();
                String flood = flat2.select("a.resblock-location").text();
                String houseInfo = flat2.select("a.resblock-room").text();
                String followInfo = "详细请点击";
                String price = flat2.select("div.resblock-price div.main-price").text();
                f.setDetailUrl(detailUrl);
                f.setTitle_text(title_text);
                f.setFlood(flood);
                f.setHouseInfo(houseInfo);
                f.setFollowInfo(followInfo);
                f.setPrice(price);
                System.out.println(f);
                result.add(f);
            }
        }

        return result;
    }

    private static List<Flat> getCustomLouPan_Hall(String url) throws Exception{
        int pos = url.indexOf("com/");
        String url1 = url.substring(0,pos+3);
        List<Flat> result = new ArrayList<>();
        //获取页数
        int totalPage = 0;
        String base = url.replace("{pg}","pg1");
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        String s= document.select(".se-part").eq(1).text();
        if(s==null||"".equals(s)) return result;
        String[]a = s.split(" ");
        totalPage = Integer.parseInt(a[a.length-1]);
        if(totalPage>=30) {
            totalPage = 30;
        }
        //获取房源信息
        for(int i =1;i<=totalPage;i++) {
            String cityUrl = url.replace("{pg}", "pg"+i);
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats = doc.select("ul.resblock-list-wrapper li.resblock-list");
            for(Element flat:flats) {
                Flat f = new Flat();
                Elements flat1 = flat.select("a.resblock-img-wrapper img.lj-lazy");
                String image = flat1.attr("data-original");
                f.setImage(image);
                Elements flat2 = flat.select("div.resblock-desc-wrapper");
                Elements title= flat2.select("div.resblock-name a.name");
                String detailUrl  = url1 +title.attr("href");
                String title_text = title.text();
                String flood = flat2.select("a.resblock-location").text();
                String houseInfo = flat2.select("a.resblock-room").text();
                String followInfo = "详细请点击";
                String price = flat2.select("div.resblock-price div.main-price").text();
                f.setDetailUrl(detailUrl);
                f.setTitle_text(title_text);
                f.setFlood(flood);
                f.setHouseInfo(houseInfo);
                f.setFollowInfo(followInfo);
                f.setPrice(price);
                System.out.println(f);
                result.add(f);
            }
        }

        return result;
    }

    private static List<Flat> getCustomLouPan(String url) throws Exception{
        List<Flat> result = new ArrayList<>();
        int pos = url.indexOf("com/");
         String url1 = url.substring(0,pos+3);
        //获取页数
        int totalPage = 0;
        String base = url.replace("{page}","pg1");
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        String s= document.select(".se-part").eq(1).text();
        if(s==null||"".equals(s)) return result;
        String[]a = s.split(" ");
        totalPage = Integer.parseInt(a[a.length-1]);
        if(totalPage>=30) {
            totalPage = 30;
        }
        //获取房源信息
        for(int i =1;i<=totalPage;i++) {
            String cityUrl = url.replace("{page}", "pg"+i);
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats = doc.select("ul.resblock-list-wrapper li.resblock-list");
            for(Element flat:flats) {
                Flat f = new Flat();
                Elements flat1 = flat.select("a.resblock-img-wrapper img.lj-lazy");
                String image = flat1.attr("data-original");
                f.setImage(image);
                Elements flat2 = flat.select("div.resblock-desc-wrapper");
                Elements title= flat2.select("div.resblock-name a.name");
                String detailUrl  = url1 +title.attr("href");
                String title_text = title.text();
                String flood = flat2.select("a.resblock-location").text();
                String houseInfo = flat2.select("a.resblock-room").text();
                String followInfo = "详细请点击";
                String price = flat2.select("div.resblock-price div.main-price").text();
                f.setDetailUrl(detailUrl);
                f.setTitle_text(title_text);
                f.setFlood(flood);
                f.setHouseInfo(houseInfo);
                f.setFollowInfo(followInfo);
                f.setPrice(price);
                System.out.println(f);
                result.add(f);
            }
        }

        return result;
    }


    /**
     * 获取二手房数据
     * @param url
     * @throws Exception
     */
    public static List<Flat> getErShouFang(String url) throws Exception {
        if(url.indexOf("{page}")!=-1){
            return getCustomErShouFang(url);
        }
        if(url.indexOf("{hall}")!=-1){
            return getErShouFang_Hall(url.replace("{hall}",""));
        }
        if(url.indexOf("{toward}")!=-1){
            return getErShouFang_Toward(url.replace("{toward}",""));
        }
        List<Flat> result = new ArrayList<>();
        String base = url+"/ershoufang";
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        int totalPage = 0;
        //获取总页数
        String s = document.select(".page-box").attr("page-data");
        if("".equals(s)||s==null) return result;
        String[] d = s.split(",");
        String tem = d[0];
        int pos = tem.indexOf(':');
        totalPage = Integer.parseInt(tem.substring(pos+1));
        if(totalPage>=30) {
            totalPage=30;
        }
        System.out.println(totalPage);
        //获取房源信息
        base = base+"/pg{page}";
        for(int i =1;i<=totalPage;i++) {
            String cityUrl = base.replace("{page}", String.valueOf(i));
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats = doc.select(".sellListContent li.clear");
            for(Element flat:flats) {
                Flat f = new Flat();
                Elements flat1 = flat.select("a.img img.lj-lazy");
                String image = flat1.attr("data-original");
                f.setImage(image);
                Elements flat2 = flat.select("div.info");
                Elements title= flat2.select(".title a");
                String detailUrl  = title.attr("href");
                String title_text = title.text();
                Elements address = flat2.select("div.address");
                String flood = address.select("div.flood").text();
                String houseInfo = address.select("div.houseInfo").text();
                String followInfo = address.select("div.followInfo").text();
                String price = address.select("div.priceInfo div.unitPrice").text();
                f.setDetailUrl(detailUrl);
                f.setTitle_text(title_text);
                f.setFlood(flood);
                f.setHouseInfo(houseInfo);
                f.setFollowInfo(followInfo);
                f.setPrice(price);
                System.out.println(f);
                result.add(f);
            }
        }
        return result;
    }

    private static List<Flat> getErShouFang_Toward(String url) throws  Exception{
        List<Flat> result = new ArrayList<>();
        String base = url.replace("{pg}","pg1");
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        int totalPage = 0;
        //获取总页数
        String s = document.select(".page-box").attr("page-data");
        if("".equals(s)||s==null) return result;
        String[] d = s.split(",");
        String tem = d[0];
        int pos = tem.indexOf(':');
        totalPage = Integer.parseInt(tem.substring(pos+1));
        if(totalPage>=30) {
            totalPage=30;
        }
        System.out.println(totalPage);
        //获取房源信息
        for(int i =1;i<=totalPage;i++) {
            String cityUrl =  url.replace("{pg}","pg"+i);
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats = doc.select(".sellListContent li.clear");
            for(Element flat:flats) {
                Flat f = new Flat();
                Elements flat1 = flat.select("a.img img.lj-lazy");
                String image = flat1.attr("data-original");
                f.setImage(image);
                Elements flat2 = flat.select("div.info");
                Elements title= flat2.select(".title a");
                String detailUrl  = title.attr("href");
                String title_text = title.text();
                Elements address = flat2.select("div.address");
                String flood = address.select("div.flood").text();
                String houseInfo = address.select("div.houseInfo").text();
                String followInfo = address.select("div.followInfo").text();
                String price = address.select("div.priceInfo div.unitPrice").text();
                f.setDetailUrl(detailUrl);
                f.setTitle_text(title_text);
                f.setFlood(flood);
                f.setHouseInfo(houseInfo);
                f.setFollowInfo(followInfo);
                f.setPrice(price);
                System.out.println(f);
                result.add(f);
            }
        }
        return result;
    }

    private static List<Flat> getErShouFang_Hall(String url) throws Exception{
        List<Flat> result = new ArrayList<>();
        String base = url.replace("{pg}","pg1");
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        int totalPage = 0;
        //获取总页数
        String s = document.select(".page-box").attr("page-data");
        if("".equals(s)||s==null) return result;
        String[] d = s.split(",");
        String tem = d[0];
        int pos = tem.indexOf(':');
        totalPage = Integer.parseInt(tem.substring(pos+1));
        if(totalPage>=30) {
            totalPage=30;
        }
        System.out.println(totalPage);
        //获取房源信息
        base = base+"/pg{page}";
        for(int i =1;i<=totalPage;i++) {
            String cityUrl = base.replace("{page}", String.valueOf(i));
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats = doc.select(".sellListContent li.clear");
            for(Element flat:flats) {
                Flat f = new Flat();
                Elements flat1 = flat.select("a.img img.lj-lazy");
                String image = flat1.attr("data-original");
                f.setImage(image);
                Elements flat2 = flat.select("div.info");
                Elements title= flat2.select(".title a");
                String detailUrl  = title.attr("href");
                String title_text = title.text();
                Elements address = flat2.select("div.address");
                String flood = address.select("div.flood").text();
                String houseInfo = address.select("div.houseInfo").text();
                String followInfo = address.select("div.followInfo").text();
                String price = address.select("div.priceInfo div.unitPrice").text();
                f.setDetailUrl(detailUrl);
                f.setTitle_text(title_text);
                f.setFlood(flood);
                f.setHouseInfo(houseInfo);
                f.setFollowInfo(followInfo);
                f.setPrice(price);
                System.out.println(f);
                result.add(f);
            }
        }
        return result;
    }

    private static List<Flat> getCustomErShouFang(String url) throws Exception{
        List<Flat> result = new ArrayList<>();
        String base = url.replace("{page}","pg1");
        String str = doGet(base);
        if(str==null) return result;
        Document document = Jsoup.parse(str);
        int totalPage = 0;
        //获取总页数
        String s = document.select(".page-box").attr("page-data");
        if("".equals(s)||s==null) return result;
        String[] d = s.split(",");
        String tem = d[0];
        int pos = tem.indexOf(':');
        totalPage = Integer.parseInt(tem.substring(pos+1));
        if(totalPage>=30) {
            totalPage=30;
        }
        System.out.println(totalPage);
        //获取房源信息

        for(int i =1;i<=totalPage;i++) {
            String cityUrl = url.replace("{page}", "pg"+i);
            String s1 = doGet(cityUrl);
            Document doc= Jsoup.parse(s1);
            Elements flats = doc.select(".sellListContent li.clear");
            for(Element flat:flats) {
                Flat f = new Flat();
                Elements flat1 = flat.select("a.img img.lj-lazy");
                String image = flat1.attr("data-original");
                f.setImage(image);
                Elements flat2 = flat.select("div.info");
                Elements title= flat2.select(".title a");
                String detailUrl  = title.attr("href");
                String title_text = title.text();
                Elements address = flat2.select("div.address");
                String flood = address.select("div.flood").text();
                String houseInfo = address.select("div.houseInfo").text();
                String followInfo = address.select("div.followInfo").text();
                String price = address.select("div.priceInfo div.unitPrice").text();
                f.setDetailUrl(detailUrl);
                f.setTitle_text(title_text);
                f.setFlood(flood);
                f.setHouseInfo(houseInfo);
                f.setFollowInfo(followInfo);
                f.setPrice(price);
                System.out.println(f);
                result.add(f);
            }
        }
        return result;
    }


    /**
     * 获取全球房源城市的列表
     * 0:国外
     * 1:国内
     * @param f
     * @throws Exception
     */
    public static LinkedHashMap<String,LinkedHashMap<String,List<City>>> getCityList(int f) throws Exception {
        String html = doGet("https://www.ke.com/city");
        if(html==null) return null;
        Document document = Jsoup.parse(html);
        //爬取城市列表
        LinkedHashMap<String,LinkedHashMap<String,List<City>>> result = new LinkedHashMap<String,LinkedHashMap<String,List<City>>>();
        Elements c;
        if(f==0) {
            c = document.select(".city_list_section").eq(1);
        }else {
            c = document.select(".city_list_section").eq(0);
        }

        Elements c1 = c.select("ul li.city_list_li");
        //获取首字母的个数
        for(Element element:c1) {
            String flag = element.select(".city_firstletter").text();
            Elements cities = element.select(".city_province");
            //获取每个首字母的主要城市
            LinkedHashMap<String,List<City>> main_city = new LinkedHashMap<>();
            for(Element ci:cities) {
                //获取主城市名称
                String mainCity = ci.select(".city_province .city_list_tit").text();
                Elements secondCity = ci.select(".CLICKDATA");
                //
                List<City> detail_city = new ArrayList<City>();
                //获取每个首字母下的城市
                for(Element sec:secondCity) {
                    City cit = new City();
                    cit.setName(sec.text());
                    cit.setUrl("https:"+sec.select("a").attr("href"));
                    if(f==0) {
                      cit.setFlag(false);
                    }else {
                       cit.setFlag(true);
                    }
                    detail_city.add(cit);
                }
                main_city.put(mainCity, detail_city);
            }
            result.put(flag,main_city);
        }
        return result;
    }

    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager(){

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }


    public static String doGet(String url) throws Exception{
        //CloseableHttpClient httpClient = HttpClients.createDefault();
       SSLContext sslcontext =
                SSLContextBuilder.create().loadTrustMaterial(TrustAllStrategy.INSTANCE).build();

        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext, NoopHostnameVerifier.INSTANCE ))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0"); // 设置请求头消息User-Agent
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if(response.getCode()==200) {
                String content = EntityUtils.toString(response.getEntity(),"UTF-8");
                System.out.println("访问成功!");
                return content;
            }else {
                System.out.println("访问失败!");
            }
        }finally {
            if(response!=null) {
                response.close();
            }
            httpClient.close();
        }
        return null;
    }

    /**
     * 解析国外新房信息(loupan）
     * @param url
     * @return
     * @throws Exception
     */
    public static ForeignFlatDetail parseGuoWaiLouPan(String url) throws Exception{
        String str = doGet(url);
        Document document = Jsoup.parse(str);
        ForeignFlatDetail flatDetail = new ForeignFlatDetail();
        Elements title_root = document.select("div.title-box");
        String title = title_root.select("h1").text();
        String detail_address = title_root.select("div.title-box span.location").text().trim().replace("•", "") + title;
        flatDetail.setTitle(title);
        flatDetail.setDetail_address(detail_address);
        //价格列表
        List<Double> priceList = new ArrayList<>();
        String price1 = document.select("div.price span.rmb-price").text();
        int pos = price1.indexOf("万");
        String price = price1.substring(0,pos);
        String[] s3 = price.split("-");
        for(String t:s3) {
            priceList.add(Double.valueOf(t));
        }
        flatDetail.setPrice(priceList);
        //户型列表
        String[] s = document.select("div.house-info p.item-text").text().trim().split(" ");
        List<String> huxing = new ArrayList<>();
        String[] s1 = s[0].split("/");
        for(String t:s1) huxing.add(t);
        flatDetail.setHuxingText(huxing);
        //建筑面积
        List<String> area = new ArrayList<>();

        int in = s[1].indexOf("m²");
        String[] s2 = s[1].substring(0,in).split("-");
        for(String t:s2) area.add(t);
        flatDetail.setArea(area);
        //开盘日期
        String date1 = document.select("p.latest-opening span.content").text();
        String date = document.select("p.latest-opening span.content a").text();
        //Date date = new SimpleDateFormat("yyyy-MM-dd").parse(date1.replace(d1,"").replace("/", "-"));
        flatDetail.setLast_open_date(date);
        //户型详细信息
        List<HashMap<String,String>> huxing_detail = new ArrayList<>();
        String huxingdetail_url = url+"#housetype";
        String huxingdetail_str = doGet(huxingdetail_url);
        Document huxingdetail_document = Jsoup.parse(huxingdetail_str);
        Elements elements = huxingdetail_document.select("div.housetype-box");
        for(Element e:elements) {
            HashMap<String,String> data = new HashMap<>();
            data.put("img",e.select("div.housetype-pic img").attr("src"));
            data.put("title",e.select("div.housetype-info span.housetype-title").text());
            String pri = e.select("div.housetype-info span.rmb-price").text();
            int d = pri.indexOf("万");
            data.put("price", pri.substring(0,d));
            String ar = e.select("div.housetype-info span.indoor-area").text();
            int f = ar.indexOf("㎡");
            data.put("area", ar.substring(0, f));
            huxing_detail.add(data);
        }
        flatDetail.setHuxing_detail(huxing_detail);
        //获取房源图片
        List<String>img_url = new ArrayList<>();
        elements = document.select("div.picbox").eq(0).select("ul.picbox_layout-preview-box li");
        for(Element e:elements) {
            img_url.add(e.select("img").attr("src"));
        }
        flatDetail.setImg_url(img_url);
        //项目详情
        HashMap<String,String>project_detail_info = new HashMap<>();
        elements = document.select("div.box-l div.left-container div.baseinfo.introduction div.intro-content-item");
        for(Element e:elements) {
            String lag = e.select("span.subtitle_left_gray").text();
            String val = e.select("span.subcontent_right").text();
            if("楼盘当地名称".equals(lag)) {
                project_detail_info.put("local_name", val);
            }else if("楼盘中文名称".equals(lag)) {
                project_detail_info.put("chinese_name", val);
            }else if("国家-城市".equals(lag)) {
                project_detail_info.put("position", val);
            }else if("邮编".equals(lag)) {
                project_detail_info.put("zip_code", val);
            }else if("占地面积".equals(lag)) {
                project_detail_info.put("area", val);
            }else if("产权年限".equals(lag)) {
                project_detail_info.put("own_year", val);
            }else if("规划户数".equals(lag)) {
                project_detail_info.put("flat_number", val);
            }else if("供暖方式".equals(lag)) {
                project_detail_info.put("heating_way", val);
            }else if("物业公司".equals(lag)) {
                project_detail_info.put("property_company", val);
            }else if("房产税".equals(lag)) {
                project_detail_info.put("property_tax", val);
            }else if("物业费".equals(lag)) {
                project_detail_info.put("management_price", val);
            }else if("车位数量".equals(lag)) {
                project_detail_info.put("car_num", val);
            }

        }
        flatDetail.setProject_detail_info(project_detail_info);
        flatDetail.setAgent(document.select("div.agent-info-text div.name span.cn-name").eq(0).text());
        flatDetail.setPuhoneNumber(document.select("div.agent-info-box div.agent-info-text div.phone").eq(0).text());
        //核心卖点
        HashMap<String,String> selling_point = new HashMap<>();
        String[] fag = document.select("div.maidian-tab-container").text().trim().split(" ");
        elements = document.select("div.maidian-content");
        int k=0;
        for(Element e:elements) {
            selling_point.put(fag[k++], e.text().trim());
        }
        flatDetail.setSelling_point(selling_point);
        System.out.println(flatDetail);
        return flatDetail;
    }


    /**
     * 解析新房详情页面(国内)
     * @param url
     * @param addressPre  这个需要传入当前国家、省、市地址
     * @return
     * @throws Exception
     */
    public static LoupanDetail parseLouPan(String url, String addressPre)throws Exception{
        String str = doGet(url);
        Document document = Jsoup.parse(str);
        LoupanDetail flatDetail = new LoupanDetail();
        String title = document.select("h2.DATA-PROJECT-NAME").text();
        flatDetail.setTitle(title);
        Double price =0.0;
        Double price_perMi =0.0;
        Elements elements = document.select("div.top-info div.price").eq(0).select("span");
        if(elements.size()<=1) {
            price = 0.0;
            price_perMi =0.0;
        }else if(elements.size()==3) {
            String fag = elements.eq(2).text().trim();
            if("元/平(单价)".equals(fag)) {
                price_perMi = Double.valueOf(elements.eq(1).text());
                price = 0.0;
            }else if("万/套(总价)".equals(fag)){
                price_perMi = 0.0;
                price  = Double.valueOf(elements.eq(1).text());
            }
        }else if(elements.size()==5){
            price_perMi = Double.valueOf(elements.eq(1).text());
            price = Double.valueOf(elements.eq(3).text());
        }
        flatDetail.setPrice(price);
        flatDetail.setPrice_perMi(price_perMi);
        //项目地址
        String project_address = document.select("ul.info-list span.content").eq(0).text();
        flatDetail.setProject_address(project_address);
        //开盘日期
        String last_open_date = document.select("li.open-date-wrap span.content").eq(0).text().replace(".", "-");
       // Date date = new SimpleDateFormat("yyyy-MM-dd").parse(last_open_date);
        flatDetail.setLast_open_date(last_open_date);
        //经纪人
        //String agent = document.select("div.ke-agent-sj-info span.ke-agent-sj-name").text();
        flatDetail.setAgent("未知");
        //电话
        String phoneNumber = document.select("div.share span").text().trim();
        flatDetail.setPhoneNumber(phoneNumber.split("：")[1]);
        //详细地址
        flatDetail.setDetail_address(addressPre);
        //爬取图片
        String pic_url = url+"xiangce/";
        String detail_pic_url = doGet(pic_url);
        Document pic_document = Jsoup.parse(detail_pic_url);
        List<String> rendering_url = new ArrayList<>();
        List<String> vr_url = new ArrayList<>();
        List<String> sample_url = new ArrayList<>();
        List<String> Village_set= new ArrayList<>();
        List<String> Presale_license= new ArrayList<>();
        List<String> Developer_business_license= new ArrayList<>();
        elements = pic_document.select("div.main-wrap div.tab-group");
        for(Element e:elements) {
            String label1 = e.select("h4").text();
            int index = label1.indexOf("（");
            String label = label1.substring(0,index);
            Elements tab = e.select("ul.tab-list li.item");
            if("效果图".equals(label)) {
                System.out.println(label+tab.size());
                for(Element s:tab) {
                    if(rendering_url.size()>10) {break;}
                    rendering_url.add(s.select("img").attr("src"));
                }
                flatDetail.setRendering_url(rendering_url);
            }else if("实景图".equals(label)) {
                System.out.println(label+tab.size());
                for(Element s:tab) {
                    if(vr_url.size()>10) {break;}
                    vr_url.add(s.select("img").attr("src"));
                }
                flatDetail.setVr_url(vr_url);
            }else if("样板间".equals(label)) {
                System.out.println(label+tab.size());
                for(Element s:tab) {
                    if(sample_url.size()>10) {break;}
                    sample_url.add(s.select("img").attr("src"));
                }
                flatDetail.setSample_url(sample_url);
            }else if("小区配套".equals(label)) {
                System.out.println(label+tab.size());
                for(Element s:tab) {
                    if(Village_set.size()>10) {break;}
                    Village_set.add(s.select("img").attr("src"));
                }
                flatDetail.setVillage_set(Village_set);
            }else if("预售许可证".equals(label)) {
                System.out.println(label+tab.size());
                for(Element s:tab) {
                    if(Presale_license.size()>10) {break;}
                    Presale_license.add(s.select("img").attr("src"));
                }
                flatDetail.setPresale_license(Presale_license);
            }else if("开发商营业执照".equals(label)) {
                System.out.println(label+tab.size());
                for(Element s:tab) {
                    if(Developer_business_license.size()>10) {break;}
                    Developer_business_license.add(s.select("img").attr("src"));
                }
                flatDetail.setDeveloper_business_license(Developer_business_license);
            }
        }
        //爬取户型图
        List<HashMap<String,String>> huxing_info = new ArrayList<>();
        String huxingUrl = url+"huxingtu/";
        String detail_huxingUrl = doGet(huxingUrl);
        Document huxingUrl_document = Jsoup.parse(detail_huxingUrl);
        elements = huxingUrl_document.select("div.item-list-wrap li.huxing-item");
        for(Element e:elements) {
            HashMap<String,String> h = new HashMap<>();
            String lab = e.select("a.thumb").text();
            String imgUrl = e.select("img").attr("src");
            h.put("title",lab);
            h.put("imgUrl",imgUrl);
            if(!TextUtils.isEmpty(e.select("span.price i").text())) {
                h.put("price",e.select("span.price i").text());
            }else {
                h.put("price","价格待定");
            }
            h.put("status",e.select(".status").text());
            Elements s = e.select("div.info li");
            h.put("type", s.eq(0).text().split("：")[1]);
            h.put("area", s.eq(1).text().split("：")[1]);
            huxing_info.add(h);
        }
        flatDetail.setHuxing_info(huxing_info);
        //详情地址
        HashMap<String,Object> planning_info = new HashMap<>();
        HashMap<String,String> matching_info = new HashMap<>();
        String detail_url = url+"xiangqing/";
        String detail_str = doGet(detail_url);
        Document detail_document = Jsoup.parse(detail_str);
        elements = detail_document.select("div.big-left ul.x-box");
        Elements guihua = elements.eq(1);
        Elements peitao = elements.eq(2);
        String[] col = new String[]{"建筑类型"," 绿化率","占地面积","容积率","建筑面积"," 物业类型","规划户数","产权年限"," "};
        int k = 0;
        for(Element s:guihua.select("li")) {
            if(" ".equals(col[k])) break;
            planning_info.put(col[k++], s.select("span.label-val").text());
        }
        flatDetail.setPlanning_info(planning_info);
        k=0;
        String[] col1 = new String[]{"物业公司"," 车位配比","物业费"," 供暖方式","供水方式","  供电方式"," 车位"," "};
        for(Element s:peitao.select("li")) {
            if(" ".equals(col1[k])) break;
            matching_info.put(col1[k++], s.select("span.label-val").text());
        }
        flatDetail.setMatching_info(matching_info);
        System.out.println(flatDetail);
        return flatDetail;
    }

    /**
     * 解析租房详情页面
     * @param url
     * @param addressPre 这个需要传入当前国家、省、市、县 （租房页面没有详细地址,所以在上一个页面时就需要拼接详细地址）
     * @return
     * @throws Exception
     */
    public static ZufangDetail parseZufang(String url, String addressPre)throws Exception{
        List<ZufangDetail> result = new ArrayList<>();
        String str = doGet(url);
        Document document = Jsoup.parse(str);
        ZufangDetail flatDetail = new ZufangDetail();
        String title = document.select("p.content__title").text();
        flatDetail.setTitle(title);
        Double price = Double.valueOf(document.select("div.content__aside--title span").eq(0).text());
        flatDetail.setPrice(price);
        String address = addressPre;
        flatDetail.setAddress(address);
        Elements elements = document.select("ul.content__aside__list li");
        for(Element e:elements) {
            String label = e.select("span").text();
            String content = "";
            if("租赁方式：".equals(label)) {
                content = e.text().replace(label, "");
                flatDetail.setRent_way(content);
            }else if("房屋类型：".equals(label)) {
                String[] d = e.text().replace(label, "").split(" ");
                int pos = d[1].indexOf("㎡");
                Double area = Double.valueOf(d[1].substring(0,pos));
                flatDetail.setArea(area);
                flatDetail.setRoom(Integer.parseInt(d[0].charAt(0)+""));
                flatDetail.setHall(Integer.parseInt(d[0].charAt(2)+""));
                flatDetail.setToilet(Integer.parseInt(d[0].charAt(4)+""));
            }else if("朝向楼层：".equals(label)) {
                content = e.text().replace(label, "");
                flatDetail.setToward(content.split(" ")[0]);
            }
        }
        String agent = document.select(".ke-agent-sj-sdk div.content__aside__list--title span.contact_name").eq(0).text();
        flatDetail.setAgent(agent);
        String phoneNumber = document.select("div.phone__hover--wrapper p").eq(0).text();
        flatDetail.setPhoneNumber(phoneNumber);
        //基本信息
        HashMap<String,String> base = new HashMap<>();
        elements = document.select("#info ul").eq(0).select("li");
        for(Element e:elements) {
            String[] d = e.text().split("：");
            if(d.length<=1) continue;
            base.put(d[0], d[1]);
        }
        flatDetail.setBase(base);
        //房源图片描述
        List<String> imagUrl = new ArrayList<>();
        elements = document.select("ul.piclist img");
        for(Element e:elements) {
            imagUrl.add(e.attr("src"));
        }
        flatDetail.setImagUrl(imagUrl);
        //费用详情
        HashMap<String,String> price_detail = new HashMap<>();
        elements = document.select("div.table_wrapper .table_title li");
        List<String>danwei = new ArrayList<>();
        for(Element e:elements) {
            danwei.add(e.text());
        }
        String[] value = document.select("div.table_wrapper ul.table_row").text().split(" ");
        for(int i =0;i<danwei.size();i++) {
            price_detail.put(danwei.get(i),value[i]);
        }
        flatDetail.setPrice_detail(price_detail);
        System.out.println(flatDetail);
        return flatDetail;
    }



    /**
     * 解析二手房详情页面
     * @param url
     * @param addressPre  传入当前的国家、省
     * @return
     * @throws Exception
     */
    public static ErshouDetail parseErShouFang(String url, String addressPre) throws Exception{
        String str = doGet(url);
        Document document = Jsoup.parse(str);
        ErshouDetail flatDetail = new ErshouDetail();
        String data_vr = document.select(".appLink").attr("href");
        String title = document.select(".title h1").text();
        Elements elements = document.select(".content .price");
        String totalPrice = elements.select(".total").text();
        Double price_perMi = Double.valueOf(elements.select(".text .unitPriceValue").text());
        String roomMainInfo = document.select(".room .mainInfo").text();
        String room = roomMainInfo.charAt(0)+"";
        String hall = roomMainInfo.charAt(2)+"";
        String toward = document.select(".type .mainInfo").text();
        String area1 = document.select(".area .mainInfo").text();
        int pos = area1.indexOf("平");
        String area = area1.substring(0,pos);
        String agent = document.select(".ke-agent-sj-fr a.ke-agent-sj-name").text();
        String phoneNumber = document.select(".ke-agent-sj-sdk-f-0 .ke-agent-sj-phone").text();
        String villiage_name = document.select(".communityName a.info").text();
        String villiage_address = addressPre+" "+document.select(".areaName .info").text()+" "+villiage_name;
        elements = document.select(".content-wrapper div.list div");
        //处理图片
        HashMap<String,String> picMap = new HashMap<>();
        for(Element e:elements) {
            String t = e.text();
            if(TextUtils.isEmpty(t)) continue;
            String u = e.select("img").attr("src");
            picMap.put(t,u);
        }
        //房型的每个控件的大小
        List<String> des = new ArrayList<>();
        elements = document.select("div.des .row");
        for(Element e:elements) {
            des.add(e.text());
        }
        //特色
        HashMap<String,String> features = new HashMap<>();
        elements = document.select(".introContent div.clear");
        for(Element e:elements) {
            features.put(e.select("div.name").text(),e.select("div.content").text());
        }
        //基本属性
        HashMap<String,String> base = new HashMap<>();
        elements = document.select("div.base li");
        for(Element e:elements) {
            String u = e.text();
            String label = e.select(".label").text();
            String content = u.replace(label,"");
            base.put(label,content);
        }
        //交易属性
        HashMap<String,String> transaction = new HashMap<>();
        elements = document.select("div.transaction li");
        for(Element e:elements) {
            String u = e.text();
            String label = e.select(".label").text();
            String content = u.replace(label,"");
            transaction.put(label,content);
        }
        flatDetail.setTitle(title);
        flatDetail.setData_vr(data_vr);
        flatDetail.setTotalPrice(Double.valueOf(totalPrice));
        flatDetail.setPrice_perMi(price_perMi);
        flatDetail.setRoom(Integer.parseInt(room));
        flatDetail.setHall(Integer.parseInt(hall));
        flatDetail.setToward(toward);
        flatDetail.setArea(Double.valueOf(area));
        flatDetail.setAgent(agent);
        flatDetail.setPhoneNumber(phoneNumber);
        flatDetail.setVillage_name(villiage_name);
        flatDetail.setVillage_address(villiage_address);
        flatDetail.setImageUrl(picMap);
        flatDetail.setDes(des);
        flatDetail.setFeatures(features);
        flatDetail.setBase(base);
        flatDetail.setTransaction(transaction);
        System.out.println(flatDetail);
        return flatDetail;
    }
}
