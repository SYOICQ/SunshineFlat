package pojo;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class City implements Serializable {
    /**
     * true:国内
     * false:国外
     */
    private boolean flag;
    //省
    private String cityName;
    //市
    private String name;
    private String url;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "City [name=" + name + ", url=" + url + "]";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        City c = (City)obj;
        if(name.equals(c.getName())&&url.equals(c.getUrl())) return true;
        return false;
    }
}
