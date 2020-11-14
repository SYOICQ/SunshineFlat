package pojo;

import java.util.HashMap;
import java.util.List;

public class ErshouDetail {
    //vr链接
    private String data_vr;
    //标题
    private String title;
    //总价格
    private Double totalPrice;
    //每平米的价格
    private Double price_perMi;
    //室
    private Integer room;
    //厅
    private Integer hall;
    //朝向
    private String toward;
    //面积
    private Double area;
    //小区名称
    private String village_name;
    //小区地址
    private String village_address;
    //经纪人
    private String agent;
    //电话
    private String phoneNumber;
    //图片列表
    private HashMap<String,String> imageUrl;
    //房屋每个空间大小的具体描述
    private List<String> des;
    //房屋的特色
    private HashMap<String,String> features;
    //基本属性
    private HashMap<String,String> base;
    //交易属性
    private HashMap<String,String> transaction;


    public String getToward() {
        return toward;
    }
    public void setToward(String toward) {
        this.toward = toward;
    }

    public Double getPrice_perMi() {
        return price_perMi;
    }
    public void setPrice_perMi(Double price_perMi) {
        this.price_perMi = price_perMi;
    }
    public String getData_vr() {
        return data_vr;
    }
    public void setData_vr(String data_vr) {
        this.data_vr = data_vr;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public Integer getRoom() {
        return room;
    }
    public void setRoom(Integer room) {
        this.room = room;
    }
    public Integer getHall() {
        return hall;
    }
    public void setHall(Integer hall) {
        this.hall = hall;
    }
    public Double getArea() {
        return area;
    }
    public void setArea(Double area) {
        this.area = area;
    }
    public String getVillage_name() {
        return village_name;
    }
    public void setVillage_name(String village_name) {
        this.village_name = village_name;
    }
    public String getVillage_address() {
        return village_address;
    }
    public void setVillage_address(String village_address) {
        this.village_address = village_address;
    }
    public String getAgent() {
        return agent;
    }
    public void setAgent(String agent) {
        this.agent = agent;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public HashMap<String, String> getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(HashMap<String, String> imageUrl) {
        this.imageUrl = imageUrl;
    }
    public List<String> getDes() {
        return des;
    }
    public void setDes(List<String> des) {
        this.des = des;
    }
    public HashMap<String, String> getFeatures() {
        return features;
    }
    public void setFeatures(HashMap<String, String> features) {
        this.features = features;
    }
    public HashMap<String, String> getBase() {
        return base;
    }
    public void setBase(HashMap<String, String> base) {
        this.base = base;
    }
    public HashMap<String, String> getTransaction() {
        return transaction;
    }
    public void setTransaction(HashMap<String, String> transaction) {
        this.transaction = transaction;
    }
    @Override
    public String toString() {
        return "FlatDetail [data_vr=" + data_vr + ", title=" + title + ", totalPrice=" + totalPrice + ", price_perMi="
                + price_perMi + ", room=" + room + ", hall=" + hall + ", toward=" + toward + ", area=" + area
                + ", village_name=" + village_name + ", village_address=" + village_address + ", agent=" + agent
                + ", phoneNumber=" + phoneNumber + ", imageUrl=" + imageUrl + ", des=" + des + ", features=" + features
                + ", base=" + base + ", transaction=" + transaction + "]";
    }
}
