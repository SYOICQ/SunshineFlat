package pojo;

public class User {
    private String phone;
    private String nickname;
    private String password;
    private String pic;
    private Integer adminFlag;
    private Integer lawerFlag;
    private Integer agentFlag;
    private String lawerId;
    private String lawerpwd;
    private String agentId;
    private String agentpwd;

    public String getLawerId() {
        return lawerId;
    }

    public void setLawerId(String lawerId) {
        this.lawerId = lawerId;
    }

    public String getLawerpwd() {
        return lawerpwd;
    }

    public void setLawerpwd(String lawerpwd) {
        this.lawerpwd = lawerpwd;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentpwd() {
        return agentpwd;
    }

    public void setAgentpwd(String agentpwd) {
        this.agentpwd = agentpwd;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public String toString() {
        return "User{" +
                "phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", pic='" + pic + '\'' +
                ", adminFlag=" + adminFlag +
                ", lawerFlag=" + lawerFlag +
                ", agentFlag=" + agentFlag +
                ", lawerId='" + lawerId + '\'' +
                ", lawerpwd='" + lawerpwd + '\'' +
                ", agentId='" + agentId + '\'' +
                ", agentpwd='" + agentpwd + '\'' +
                '}';
    }
}
