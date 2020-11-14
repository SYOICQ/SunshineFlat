package pojo;

import java.util.HashMap;
import java.util.List;

public class LoupanDetail {
    //标题
    private String title;
    //价格
    private Double price;
    //单价
    private Double price_perMi;
    //项目地址
    private String project_address;
    //最新开盘
    private String last_open_date;
    //经纪人
    private String agent;
    //电话
    private String phoneNumber;
    //详细的地址
    private String detail_address;
    //效果图
    private List<String> rendering_url;
    //实景图
    private List<String> vr_url;
    //样板间
    private List<String> sample_url;
    //小区配套
    private List<String> Village_set;
    //预售许可证
    private List<String> Presale_license;
    //开发商营业执照
    private List<String> Developer_business_license;
    //户型介绍
    private List<HashMap<String,String>> huxing_info;
    //规划信息
    private HashMap<String,Object> planning_info;
    //配套信息
    private HashMap<String,String> matching_info;
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
    public Double getPrice_perMi() {
        return price_perMi;
    }
    public void setPrice_perMi(Double price_perMi) {
        this.price_perMi = price_perMi;
    }
    public String getProject_address() {
        return project_address;
    }
    public void setProject_address(String project_address) {
        this.project_address = project_address;
    }
    public String getLast_open_date() {
        return last_open_date;
    }
    public void setLast_open_date(String last_open_date) {
        this.last_open_date = last_open_date;
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
    public String getDetail_address() {
        return detail_address;
    }
    public void setDetail_address(String detail_address) {
        this.detail_address = detail_address;
    }
    public List<String> getRendering_url() {
        return rendering_url;
    }
    public void setRendering_url(List<String> rendering_url) {
        this.rendering_url = rendering_url;
    }
    public List<String> getVr_url() {
        return vr_url;
    }
    public void setVr_url(List<String> vr_url) {
        this.vr_url = vr_url;
    }
    public List<String> getSample_url() {
        return sample_url;
    }
    public void setSample_url(List<String> sample_url) {
        this.sample_url = sample_url;
    }
    public List<String> getVillage_set() {
        return Village_set;
    }
    public void setVillage_set(List<String> village_set) {
        Village_set = village_set;
    }
    public List<String> getPresale_license() {
        return Presale_license;
    }
    public void setPresale_license(List<String> presale_license) {
        Presale_license = presale_license;
    }
    public List<String> getDeveloper_business_license() {
        return Developer_business_license;
    }
    public void setDeveloper_business_license(List<String> developer_business_license) {
        Developer_business_license = developer_business_license;
    }
    public List<HashMap<String, String>> getHuxing_info() {
        return huxing_info;
    }
    public void setHuxing_info(List<HashMap<String, String>> huxing_info) {
        this.huxing_info = huxing_info;
    }
    public HashMap<String, Object> getPlanning_info() {
        return planning_info;
    }
    public void setPlanning_info(HashMap<String, Object> planning_info) {
        this.planning_info = planning_info;
    }
    public HashMap<String, String> getMatching_info() {
        return matching_info;
    }
    public void setMatching_info(HashMap<String, String> matching_info) {
        this.matching_info = matching_info;
    }
    @Override
    public String toString() {
        return "LoupanDetail [title=" + title + ", price=" + price + ", price_perMi=" + price_perMi
                + ", project_address=" + project_address + ", last_open_date=" + last_open_date + ", agent=" + agent
                + ", phoneNumber=" + phoneNumber + ", detail_address=" + detail_address + ", rendering_url="
                + rendering_url + ", vr_url=" + vr_url + ", sample_url=" + sample_url + ", Village_set=" + Village_set
                + ", Presale_license=" + Presale_license + ", Developer_business_license=" + Developer_business_license
                + ", huxing_info=" + huxing_info + ", planning_info=" + planning_info + ", matching_info="
                + matching_info + "]";
    }


}
