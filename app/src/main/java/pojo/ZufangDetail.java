package pojo;

import java.util.HashMap;
import java.util.List;

public class ZufangDetail {
    //标题
    private String title;
    //价格
    private Double price;
    //地址
    private String address;
    //室
    private Integer room;
    //厅
    private Integer hall;
    //卫
    private Integer toilet;
    //面积
    private Double area;
    //朝向
    private String toward;
    //租赁方式
    private String rent_way;
    //经纪人
    private String agent;
    //电话
    private String phoneNumber;
    //基本信息
    private HashMap<String,String> base;
    //房源图片描述
    private List<String> imagUrl;
    //费用详情
    private HashMap<String,String> price_detail;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
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
    public Integer getToilet() {
        return toilet;
    }
    public void setToilet(Integer toilet) {
        this.toilet = toilet;
    }
    public Double getArea() {
        return area;
    }
    public void setArea(Double area) {
        this.area = area;
    }
    public String getToward() {
        return toward;
    }
    public void setToward(String toward) {
        this.toward = toward;
    }
    public String getRent_way() {
        return rent_way;
    }
    public void setRent_way(String rent_way) {
        this.rent_way = rent_way;
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
    public HashMap<String, String> getBase() {
        return base;
    }
    public void setBase(HashMap<String, String> base) {
        this.base = base;
    }
    public List<String> getImagUrl() {
        return imagUrl;
    }
    public void setImagUrl(List<String> imagUrl) {
        this.imagUrl = imagUrl;
    }

    public HashMap<String, String> getPrice_detail() {
        return price_detail;
    }
    public void setPrice_detail(HashMap<String, String> price_detail) {
        this.price_detail = price_detail;
    }
    @Override
    public String toString() {
        return "ZufangDetail [title=" + title + ", price=" + price + ", address=" + address + ", room=" + room
                + ", hall=" + hall + ", toilet=" + toilet + ", area=" + area + ", toward=" + toward + ", rent_way="
                + rent_way + ", agent=" + agent + ", phoneNumber=" + phoneNumber + ", base=" + base + ", imagUrl="
                + imagUrl + ", price_detail=" + price_detail + "]";
    }


}
