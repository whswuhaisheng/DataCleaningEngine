package com.binggou.cloud.model;

import java.io.Serializable;
import java.util.Date;

public class Category implements Serializable {
    /**
     * 类别ID
     */
    private Integer id;

    private String categoryname;

    private String encategoryname;

    private Integer fathercatid;

    private String fathercatname;

    private Integer isorder;

    /**
     * 0未启用1启用
     */
    private Integer isuse;

    private String remark;

    private Date addtime;

    private String adduser;

    private Date updatetime;

    private String updateuser;

    private Integer templateid;

    private String originalpicurl;

    private String bigpicurl;

    private String centerpicurl;

    private String smallpicurl;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname == null ? null : categoryname.trim();
    }

    public String getEncategoryname() {
        return encategoryname;
    }

    public void setEncategoryname(String encategoryname) {
        this.encategoryname = encategoryname == null ? null : encategoryname.trim();
    }

    public Integer getFathercatid() {
        return fathercatid;
    }

    public void setFathercatid(Integer fathercatid) {
        this.fathercatid = fathercatid;
    }

    public String getFathercatname() {
        return fathercatname;
    }

    public void setFathercatname(String fathercatname) {
        this.fathercatname = fathercatname == null ? null : fathercatname.trim();
    }

    public Integer getIsorder() {
        return isorder;
    }

    public void setIsorder(Integer isorder) {
        this.isorder = isorder;
    }

    public Integer getIsuse() {
        return isuse;
    }

    public void setIsuse(Integer isuse) {
        this.isuse = isuse;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public String getAdduser() {
        return adduser;
    }

    public void setAdduser(String adduser) {
        this.adduser = adduser == null ? null : adduser.trim();
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getUpdateuser() {
        return updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser == null ? null : updateuser.trim();
    }

    public Integer getTemplateid() {
        return templateid;
    }

    public void setTemplateid(Integer templateid) {
        this.templateid = templateid;
    }

    public String getOriginalpicurl() {
        return originalpicurl;
    }

    public void setOriginalpicurl(String originalpicurl) {
        this.originalpicurl = originalpicurl == null ? null : originalpicurl.trim();
    }

    public String getBigpicurl() {
        return bigpicurl;
    }

    public void setBigpicurl(String bigpicurl) {
        this.bigpicurl = bigpicurl == null ? null : bigpicurl.trim();
    }

    public String getCenterpicurl() {
        return centerpicurl;
    }

    public void setCenterpicurl(String centerpicurl) {
        this.centerpicurl = centerpicurl == null ? null : centerpicurl.trim();
    }

    public String getSmallpicurl() {
        return smallpicurl;
    }

    public void setSmallpicurl(String smallpicurl) {
        this.smallpicurl = smallpicurl == null ? null : smallpicurl.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", categoryname=").append(categoryname);
        sb.append(", encategoryname=").append(encategoryname);
        sb.append(", fathercatid=").append(fathercatid);
        sb.append(", fathercatname=").append(fathercatname);
        sb.append(", isorder=").append(isorder);
        sb.append(", isuse=").append(isuse);
        sb.append(", remark=").append(remark);
        sb.append(", addtime=").append(addtime);
        sb.append(", adduser=").append(adduser);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", updateuser=").append(updateuser);
        sb.append(", templateid=").append(templateid);
        sb.append(", originalpicurl=").append(originalpicurl);
        sb.append(", bigpicurl=").append(bigpicurl);
        sb.append(", centerpicurl=").append(centerpicurl);
        sb.append(", smallpicurl=").append(smallpicurl);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}