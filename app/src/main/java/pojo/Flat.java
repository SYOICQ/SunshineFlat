package pojo;

public class Flat {

    private String price;
    private String image;
    private String detailUrl;
    private String title_text;
    private String flood;
    private String houseInfo;
    private String followInfo;
    public String getDetailUrl() {
        return detailUrl;
    }
    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
    public String getTitle_text() {
        return title_text;
    }
    public void setTitle_text(String title_text) {
        this.title_text = title_text;
    }
    public String getFlood() {
        return flood;
    }
    public void setFlood(String flood) {
        this.flood = flood;
    }
    public String getHouseInfo() {
        return houseInfo;
    }
    public void setHouseInfo(String houseInfo) {
        this.houseInfo = houseInfo;
    }
    public String getFollowInfo() {
        return followInfo;
    }
    public void setFollowInfo(String followInfo) {
        this.followInfo = followInfo;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    @Override
    public String toString() {
        return "Flat [price=" + price + ", image=" + image + ", detailUrl=" + detailUrl + ", title_text=" + title_text
                + ", flood=" + flood + ", houseInfo=" + houseInfo + ", followInfo=" + followInfo + "]";
    }
}
