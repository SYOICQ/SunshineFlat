package pojo;

public class Apply {
    private String phone;
    private String reson;
    private String pic;
    private Integer adminFlag;
    private Integer lawerFlag;
    private Integer agentFlag;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReson() {
        return reson;
    }

    public void setReson(String reson) {
        this.reson = reson;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Integer getAdminFlag() {
        return adminFlag;
    }

    public void setAdminFlag(Integer adminFlag) {
        this.adminFlag = adminFlag;
    }

    public Integer getLawerFlag() {
        return lawerFlag;
    }

    public void setLawerFlag(Integer lawerFlag) {
        this.lawerFlag = lawerFlag;
    }

    public Integer getAgentFlag() {
        return agentFlag;
    }

    public void setAgentFlag(Integer agentFlag) {
        this.agentFlag = agentFlag;
    }

    @Override
    public String toString() {
        return "Apply{" +
                "phone='" + phone + '\'' +
                ", reson='" + reson + '\'' +
                ", pic='" + pic + '\'' +
                ", adminFlag=" + adminFlag +
                ", lawerFlag=" + lawerFlag +
                ", agentFlag=" + agentFlag +
                '}';
    }
}
