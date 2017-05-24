package com.binggou.cloud.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Date;


public class ProjectClean implements Serializable {
    private Integer id;

    /**
     * 项目编号-抓取项目的编号
     */
    private String projectnumber;
    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    /**
     * 云并购系统项目编号【年份、日期、流水号、类型、来源  】共14位数字构成
     目前类型编号暂定：
     01	央企产权
     02	国有产权
     03	实物资产
     04	诉讼资产
     05	增资
     06	融资
     07	投资意向
     08	非国有产权
     目前来源编号可以确定的有：
     001	北京产权交易所
     002	上海联合产权交易所
     003	重庆联合产权交易所
     004	天津产权交易所
     */
    private String yunprojectnumber;
    private String motorVehicle;
    private String immovables;
    private String mechanicalEquipment;
    private String payWay;		// 交纳方式
    private String payDate;		// 交纳时间
    private Double payMoney;

    public Double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Double payMoney) {
        this.payMoney = payMoney;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getMotorVehicle() {
        return motorVehicle;
    }

    public void setMotorVehicle(String motorVehicle) {
        this.motorVehicle = motorVehicle;
    }

    public String getImmovables() {
        return immovables;
    }

    public void setImmovables(String immovables) {
        this.immovables = immovables;
    }

    public String getMechanicalEquipment() {
        return mechanicalEquipment;
    }

    public void setMechanicalEquipment(String mechanicalEquipment) {
        this.mechanicalEquipment = mechanicalEquipment;
    }

    /**
     * 标的名称
     */
    private String projectname;

    /**
     * 挂牌起始日期
     */
    private Date begindate;

    /**
     * 挂牌期满日期
     */
    private Date enddate;

    /**
     * 挂牌公告期(endDate-beginDate-假日)
     */
    private Integer noticeperiod;

    /**
     * 有效期(endDate-beginDate)
     */
    private Integer validperiod;

    /**
     * 标的企业名称
     */
    private String companyname;

    /**
     * 国家
     */
    private String country = "中国";

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 地区
     */
    private String area;

    /**
     * 抓取项目的地区
     */
    private String sourcearea;

    /**
     * 挂牌价格
     */
    private Double projectprice;

    /**
     * 标的所属一级行业
     */
    private String firstcategory;

    /**
     * 二级行业
     */
    private String secondcategory;

    /**
     * 抓取项目的原始行业
     */
    private String sourcecategory;

    /**
     * 项目来源 0 云并购 ，1：北京产权交易所 ，2 ：上海联合产权交易所， 3：重庆产权交易所 4：天津产权交易所默认0
     */
    private Byte datafrom;

    /**
     * 内部审议情况
     */
    private String internalreview;

    /**
     * 决议类型
     */
    private String resolutiontype;

    /**
     * 交易方式
     */
    private String transactionway;

    /**
     * 交纳金额
     */
    private Double paymoney;

    /**
     * 交纳方式
     */
    private String payway;

    /**
     * 交纳时间
     */
    private String paydate;

    /**
     * 数据写入时间
     */
    private Date createdate;

    /**
     * 标的企业基本情况 JSON格式
     {

     "zhuCeDi": "注册地",

     "chengLiShiJian": "成立时间",

     "zhuCeZiBen": "注册资本",

     "jingYingFanWei": "经营范围",
     "zhiGongRenShu": "职工人数",
     "shiFouHanYouGuoYouHuaBoTuDi": "是否含有国有划拨土地"

     }
     */
    private String enterpriseinfo;

    /**
     * 股权结构 JSON格式
     {

     "shiFouFangQiYouXianGouMaiQuan": "标的企业原股东是否放弃行使优先购买权",
     "guDongLiBIao":[

     {"guDongMingCheng":"股东名称",                 "guDongChiGuBiLi":"股东持股比例"}

     ]

     }
     */
    private String stockstructure;

    /**
     * 审计报告 JSON格式
     {
     "shenJiJiGou": "审计机构",

     "shenJiBaoGao":[

     {"nianDu":"年度",
     "yingYeShouRu":"营业收入",
     "yingYeLiRun":"营业利润",
     "jingLiRun":"净利润",

     "ziChanZongJi":"资产总计",
     "fuZhanZongJi":"负债总计",
     "suoYouZheQuanYi":"所有者权益"
     }

     ]

     */
    private String auditreport;

    /**
     * 财务报表 JSON格式
     [

     {"baoBiaoRiQi":"报表日期",
     "yingYeShouRu":"营业收入",
     "yingYeLiRun":"营业利润",
     "jingLiRun":"净利润",

     "ziChanZongJi":"资产总计",
     "fuZhanZongJi":"负债总计",
     "suoYouZheQuanYi":"所有者权益"
     }

     ]


     */
    private String financereport;

    /**
     * 重要信息披露 JSON格式
     {
     "qiTaPiLuNeiRong": "其他披露内容",
     "guanLiCengCanYuShouRangYiXiang":"管理层拟参与受让意向"

     }
     */
    private String imporinfordisclosure;

    /**
     * 转让方基本情况JSON格式
     {

     "zhuanRangFangMingCheng": "转让方名称",
     "chiGuBiLi":"持有产(股)权比例",
     "zhuanRangGuBiLi":"拟转让产(股)权比例"

     }
     */
    private String transferinfo;

    public String getRightCategory() {
        return rightCategory;
    }

    public void setRightCategory(String rightCategory) {
        this.rightCategory = rightCategory;
    }

    private String rightCategory;
    private String assetType;
    private String transferorName;

    /**
     * 交易条件JSON格式
     {
     "jiaKuanZhiFuFangShi": "价款支付方式",
     "qiTaTiaoJian":"与转让相关其他条件"

     }
     */
    private String transactionterm;

    private static final long serialVersionUID = 1L;
    /**
     * 企业简况
     */
    private JSONObject qiyejiankuang;

    /**
     * 标的所属行业
     */
    //private String category;

    /**
     * 所在地区
     */
    private String region;

    /**
     * 四、交易条件与受让方资格条件
     */
    private JSONObject fourJiaoYiTiaoJian;

    /**
     * 三、转让方简况
     */
    private JSONObject zhuanRangFangJianJie;

    /**
     * 三、转让方简况--北京
     */
    private JSONArray zhuanRangFangJianJieArr;

    /**
     * 五、挂牌信息
     */
    private JSONObject guaPaiXinXi;

    /**
     * 项目的来源 ，例 ：上海联合产权交易所：央企股权项目
     */
    private String laiyuan;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectnumber() {
        return projectnumber;
    }

    public void setProjectnumber(String projectnumber) {
        this.projectnumber = projectnumber == null ? null : projectnumber.trim();
    }

    public String getYunprojectnumber() {
        return yunprojectnumber;
    }

    public void setYunprojectnumber(String yunprojectnumber) {
        this.yunprojectnumber = yunprojectnumber == null ? null : yunprojectnumber.trim();
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname == null ? null : projectname.trim();
    }

    public Date getBegindate() {
        return begindate;
    }

    public void setBegindate(Date begindate) {
        this.begindate = begindate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public Integer getNoticeperiod() {
        return noticeperiod;
    }

    public void setNoticeperiod(Integer noticeperiod) {
        this.noticeperiod = noticeperiod;
    }

    public Integer getValidperiod() {
        return validperiod;
    }

    public void setValidperiod(Integer validperiod) {
        this.validperiod = validperiod;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname == null ? null : companyname.trim();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    public String getSourcearea() {
        return sourcearea;
    }

    public void setSourcearea(String sourcearea) {
        this.sourcearea = sourcearea == null ? null : sourcearea.trim();
    }

    public Double getProjectprice() {
        return projectprice;
    }

    public void setProjectprice(Double projectprice) {
        this.projectprice = projectprice;
    }

    public String getFirstcategory() {
        return firstcategory;
    }

    public void setFirstcategory(String firstcategory) {
        this.firstcategory = firstcategory == null ? null : firstcategory.trim();
    }

    public String getSecondcategory() {
        return secondcategory;
    }

    public void setSecondcategory(String secondcategory) {
        this.secondcategory = secondcategory == null ? null : secondcategory.trim();
    }

    public String getSourcecategory() {
        return sourcecategory;
    }

    public void setSourcecategory(String sourcecategory) {
        this.sourcecategory = sourcecategory == null ? null : sourcecategory.trim();
    }

    public Byte getDatafrom() {
        return datafrom;
    }

    public void setDatafrom(Byte datafrom) {
        this.datafrom = datafrom;
    }

    public String getInternalreview() {
        return internalreview;
    }

    public void setInternalreview(String internalreview) {
        this.internalreview = internalreview == null ? null : internalreview.trim();
    }

    public String getResolutiontype() {
        return resolutiontype;
    }

    public void setResolutiontype(String resolutiontype) {
        this.resolutiontype = resolutiontype == null ? null : resolutiontype.trim();
    }

    public String getTransactionway() {
        return transactionway;
    }

    public void setTransactionway(String transactionway) {
        this.transactionway = transactionway == null ? null : transactionway.trim();
    }

    public Double getPaymoney() {
        return paymoney;
    }

    public void setPaymoney(Double paymoney) {
        this.paymoney = paymoney;
    }

    public String getPayway() {
        return payway;
    }

    public void setPayway(String payway) {
        this.payway = payway == null ? null : payway.trim();
    }

    public String getPaydate() {
        return paydate;
    }

    public void setPaydate(String paydate) {
        this.paydate = paydate;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getEnterpriseinfo() {
        return enterpriseinfo;
    }

    public void setEnterpriseinfo(String enterpriseinfo) {
        this.enterpriseinfo = enterpriseinfo == null ? null : enterpriseinfo.trim();
    }

    public String getStockstructure() {
        return stockstructure;
    }

    public void setStockstructure(String stockstructure) {
        this.stockstructure = stockstructure == null ? null : stockstructure.trim();
    }

    public String getAuditreport() {
        return auditreport;
    }

    public void setAuditreport(String auditreport) {
        this.auditreport = auditreport == null ? null : auditreport.trim();
    }

    public String getFinancereport() {
        return financereport;
    }

    public void setFinancereport(String financereport) {
        this.financereport = financereport == null ? null : financereport.trim();
    }

    public String getImporinfordisclosure() {
        return imporinfordisclosure;
    }

    public void setImporinfordisclosure(String imporinfordisclosure) {
        this.imporinfordisclosure = imporinfordisclosure == null ? null : imporinfordisclosure.trim();
    }

    public String getTransferinfo() {
        return transferinfo;
    }

    public void setTransferinfo(String transferinfo) {
        this.transferinfo = transferinfo == null ? null : transferinfo.trim();
    }

    public String getTransactionterm() {
        return transactionterm;
    }

    public void setTransactionterm(String transactionterm) {
        this.transactionterm = transactionterm == null ? null : transactionterm.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", projectnumber=").append(projectnumber);
        sb.append(", yunprojectnumber=").append(yunprojectnumber);
        sb.append(", projectname=").append(projectname);
        sb.append(", begindate=").append(begindate);
        sb.append(", enddate=").append(enddate);
        sb.append(", noticeperiod=").append(noticeperiod);
        sb.append(", validperiod=").append(validperiod);
        sb.append(", companyname=").append(companyname);
        sb.append(", country=").append(country);
        sb.append(", province=").append(province);
        sb.append(", city=").append(city);
        sb.append(", area=").append(area);
        sb.append(", sourcearea=").append(sourcearea);
        sb.append(", projectprice=").append(projectprice);
        sb.append(", firstcategory=").append(firstcategory);
        sb.append(", secondcategory=").append(secondcategory);
        sb.append(", sourcecategory=").append(sourcecategory);
        sb.append(", datafrom=").append(datafrom);
        sb.append(", internalreview=").append(internalreview);
        sb.append(", resolutiontype=").append(resolutiontype);
        sb.append(", transactionway=").append(transactionway);
        sb.append(", paymoney=").append(paymoney);
        sb.append(", payway=").append(payway);
        sb.append(", paydate=").append(paydate);
        sb.append(", createdate=").append(createdate);
        sb.append(", enterpriseinfo=").append(enterpriseinfo);
        sb.append(", stockstructure=").append(stockstructure);
        sb.append(", auditreport=").append(auditreport);
        sb.append(", financereport=").append(financereport);
        sb.append(", imporinfordisclosure=").append(imporinfordisclosure);
        sb.append(", transferinfo=").append(transferinfo);
        sb.append(", transactionterm=").append(transactionterm);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public JSONObject getQiyejiankuang() {
        return qiyejiankuang;
    }

    public void setQiyejiankuang(JSONObject qiyejiankuang) {
        this.qiyejiankuang = qiyejiankuang;
    }

	/*public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}*/

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public JSONObject getFourJiaoYiTiaoJian() {
        return fourJiaoYiTiaoJian;
    }

    public void setFourJiaoYiTiaoJian(JSONObject fourJiaoYiTiaoJian) {
        this.fourJiaoYiTiaoJian = fourJiaoYiTiaoJian;
    }

    public JSONObject getZhuanRangFangJianJie() {
        return zhuanRangFangJianJie;
    }

    public void setZhuanRangFangJianJie(JSONObject zhuanRangFangJianJie) {
        this.zhuanRangFangJianJie = zhuanRangFangJianJie;
    }

    public JSONObject getGuaPaiXinXi() {
        return guaPaiXinXi;
    }

    public void setGuaPaiXinXi(JSONObject guaPaiXinXi) {
        this.guaPaiXinXi = guaPaiXinXi;
    }

    public String getLaiyuan() {
        return laiyuan;
    }

    public void setLaiyuan(String laiyuan) {
        this.laiyuan = laiyuan;
    }

    public JSONArray getZhuanRangFangJianJieArr() {
        return zhuanRangFangJianJieArr;
    }

    public void setZhuanRangFangJianJieArr(JSONArray zhuanRangFangJianJieArr) {
        this.zhuanRangFangJianJieArr = zhuanRangFangJianJieArr;
    }

    //上海
    private JSONObject jidongche;
    //不动产
    private JSONObject budongchan;
    private JSONObject jixieshebei;
    private JSONObject baozhengjinsheding;
    private JSONObject zhongyaoxinxipilu;
    private JSONObject zhuyaocaiwuzhibiao;
    private JSONArray fujian;
    //北京资产不动产
    private JSONArray budongchanArray;
    private JSONArray jixieshebeiArray;
    private JSONArray jidongcheArray;

    public JSONArray getJidongcheArray() {
        return jidongcheArray;
    }

    public void setJidongcheArray(JSONArray jidongcheArray) {
        this.jidongcheArray = jidongcheArray;
    }

    public JSONArray getJixieshebeiArray() {
        return jixieshebeiArray;
    }

    public void setJixieshebeiArray(JSONArray jixieshebeiArray) {
        this.jixieshebeiArray = jixieshebeiArray;
    }

    //其它披露内容
    private String qtplnr;

    public String getQtplnr() {
        return qtplnr;
    }

    public void setQtplnr(String qtplnr) {
        this.qtplnr = qtplnr;
    }

    public JSONArray getBudongchanArray() {
        return budongchanArray;
    }

    public void setBudongchanArray(JSONArray budongchanArray) {
        this.budongchanArray = budongchanArray;
    }

    public JSONArray getFujian() {
        return fujian;
    }

    public void setFujian(JSONArray fujian) {
        this.fujian = fujian;
    }

    public JSONObject getJidongche() {
        return jidongche;
    }

    public void setJidongche(JSONObject jidongche) {
        this.jidongche = jidongche;
    }

    public JSONObject getBudongchan() {
        return budongchan;
    }

    public void setBudongchan(JSONObject budongchan) {
        this.budongchan = budongchan;
    }

    public JSONObject getJixieshebei() {
        return jixieshebei;
    }

    public void setJixieshebei(JSONObject jixieshebei) {
        this.jixieshebei = jixieshebei;
    }

    public JSONObject getBaozhengjinsheding() {
        return baozhengjinsheding;
    }

    public void setBaozhengjinsheding(JSONObject baozhengjinsheding) {
        this.baozhengjinsheding = baozhengjinsheding;
    }

    public JSONObject getZhongyaoxinxipilu() {
        return zhongyaoxinxipilu;
    }

    public void setZhongyaoxinxipilu(JSONObject zhongyaoxinxipilu) {
        this.zhongyaoxinxipilu = zhongyaoxinxipilu;
    }

    public JSONObject getZhuyaocaiwuzhibiao() {
        return zhuyaocaiwuzhibiao;
    }

    public void setZhuyaocaiwuzhibiao(JSONObject zhuyaocaiwuzhibiao) {
        this.zhuyaocaiwuzhibiao = zhuyaocaiwuzhibiao;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getTransferorName() {
        return transferorName;
    }

    public void setTransferorName(String transferorName) {
        this.transferorName = transferorName;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}