package pojo;

import java.util.HashMap;
import java.util.List;

public class ForeignFlatDetail {
    //标题
    private String title;
    //详细地址
    private String detail_address;
    //金额列表
    private List<Double> price;
    //户型列表
    private List<String> huxingText;
    //建筑面积
    private List<String> area;
    //最新开盘
    private String last_open_date;
    //户型详细列表
    private List<HashMap<String,String>> huxing_detail;
    //图片列表
    private List<String>img_url;
    //项目详情
    private HashMap<String,String>project_detail_info;
    //经纪人
    private String agent;
    //电话号码
    private String puhoneNumber;
    //核心卖点
    private HashMap<String,String> selling_point;



    public HashMap<String, String> getSelling_point() {
        return selling_point;
    }
    public void setSelling_point(HashMap<String, String> selling_point) {
        this.selling_point = selling_point;
    }
    public String getAgent() {
        return agent;
    }
    public void setAgent(String agent) {
        this.agent = agent;
    }
    public String getPuhoneNumber() {
        return puhoneNumber;
    }
    public void setPuhoneNumber(String puhoneNumber) {
        this.puhoneNumber = puhoneNumber;
    }
    public HashMap<String, String> getProject_detail_info() {
        return project_detail_info;
    }
    public void setProject_detail_info(HashMap<String, String> project_detail_info) {
        this.project_detail_info = project_detail_info;
    }
    public List<String> getImg_url() {
        return img_url;
    }
    public void setImg_url(List<String> img_url) {
        this.img_url = img_url;
    }
    public List<HashMap<String, String>> getHuxing_detail() {
        return huxing_detail;
    }
    public void setHuxing_detail(List<HashMap<String, String>> huxing_detail) {
        this.huxing_detail = huxing_detail;
    }
    public String getLast_open_date() {
        return last_open_date;
    }
    public void setLast_open_date(String last_open_date) {
        this.last_open_date = last_open_date;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDetail_address() {
        return detail_address;
    }
    public void setDetail_address(String detail_address) {
        this.detail_address = detail_address;
    }
    public List<Double> getPrice() {
        return price;
    }
    public void setPrice(List<Double> price) {
        this.price = price;
    }

    public List<String> getHuxingText() {
        return huxingText;
    }
    public void setHuxingText(List<String> huxingText) {
        this.huxingText = huxingText;
    }
    public List<String> getArea() {
        return area;
    }
    public void setArea(List<String> area) {
        this.area = area;
    }
    @Override
    public String toString() {
        return "ForeignFlatDetail [title=" + title + ", detail_address=" + detail_address + ", price=" + price
                + ", huxingText=" + huxingText + ", area=" + area + ", last_open_date=" + last_open_date
                + ", huxing_detail=" + huxing_detail + ", img_url=" + img_url + ", project_detail_info="
                + project_detail_info + ", agent=" + agent + ", puhoneNumber=" + puhoneNumber + ", selling_point="
                + selling_point + "]";
    }

}
