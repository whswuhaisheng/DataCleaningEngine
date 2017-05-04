package com.binggou.cloud.business.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.binggou.cloud.business.interfaces.IProjectCleanService;
import com.binggou.cloud.common.utils.DateUtils;
import com.binggou.cloud.common.utils.PropertyUtil;
import com.binggou.cloud.model.Category;
import com.binggou.cloud.model.City;
import com.binggou.cloud.model.ProjectClean;
import com.binggou.cloud.model.Province;
import com.binggou.cloud.persistence.annotation.CategoryDao;
import com.binggou.cloud.persistence.annotation.CityDao;
import com.binggou.cloud.persistence.annotation.ProjectCleanDao;
import com.binggou.cloud.persistence.annotation.ProvinceDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;


@Service("dataCleanService")
public class ProjectCleanServiceImpl implements IProjectCleanService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ProjectCleanDao projectCleanDao;

	@Autowired
	private ProvinceDao provinceDao;

	@Autowired
	private CityDao cityDao;

	@Autowired
	private CategoryDao categoryDao;

	Logger logger = Logger.getLogger(ProjectCleanServiceImpl.class);

	List<Province> provinceList = null;

	List<City> cityList = null;

	@Override
	public void dataCleaning() {
		int cleanSuccess = 0;
		int cleanError = 0;
		int cleanRepeat = 0;
		int moreZhuanRangFang = 0;
		long count = getMongoDataCount("crawler");
		if(count <= 0)return;
		int cycleCount = 0;	//循环次数
		if(count%100 > 0){
			cycleCount = (int)(count/100) +1;
		}else{
			cycleCount = (int)(count/100);
		}

		List<String> propertyList = PropertyUtil.getPropertyNames();//获取一级节点
        if(propertyList == null) return;
        String projectnumber = null;
    	ProjectClean projectClean = null;
    	provinceList = provinceDao.selectAllProvince();
    	cityList = cityDao.selectAllCity();

		for (int i = 0; i < cycleCount; i++) {
			List<JSONObject> list = getMongoData(0, 100, "crawler");//因为每处理一条，删除一条，所以这里写的是固定值
			if(list == null || list.size() <=0) break;
	    	for (JSONObject jsonObject : list) {
				//获取数据类型,通过数据类型获取模版
				String dataType = jsonObject.getString("来源");
				if(dataType.equals("北京资产")||dataType.equals("上海资产")){
					projectnumber = jsonObject.getJSONObject("基本信息").getString("项目编号");
					projectnumber = projectnumber.substring(projectnumber.lastIndexOf("：")+1, projectnumber.length()-1);
				} else{
					projectnumber = jsonObject.getString("项目编号");
					if(projectnumber != null && !"".equals(projectnumber) && projectnumber.contains("监测编号：")){
						projectnumber = projectnumber.substring(projectnumber.lastIndexOf("：")+1, projectnumber.length()-1);
					}
				}
	    		projectClean = projectCleanDao.selectByProjectNumber(projectnumber);
	    		if(projectClean != null){
	    			cleanRepeat++;
	    			mongoTemplate.remove(new Query(Criteria.where("项目编号").is(jsonObject.getString("项目编号"))), "crawler"); //此处删掉mongodb中的数据
	    			logger.warn("MySQL DB exist projectnumber="+jsonObject.getString("项目编号"));
	    			continue;
	    		}
	    		projectClean = new ProjectClean();
	    		projectClean.setProjectnumber(projectnumber);
	    		projectClean.setCreatedate(new Date());

	    		try {
	    			initProjectCleanFirst(projectClean, jsonObject, propertyList);
	    			if(projectClean.getZhuanRangFangJianJieArr() != null && projectClean.getZhuanRangFangJianJieArr().size() > 3){
	    				moreZhuanRangFang++;
	    				logger.warn("moreZhuanRangFang:"+jsonObject.toJSONString());
	    				mongoTemplate.remove(new Query(Criteria.where("项目编号").is(jsonObject.getString("项目编号"))), "crawler");
	    				continue;
	    			}
					initProjectCleanSecond(projectClean);
					projectCleanDao.insert(projectClean);//保存项目基本信息
					mongoTemplate.remove(new Query(Criteria.where("项目编号").is(jsonObject.getString("项目编号"))), "crawler"); //此处删掉mongodb中的数据
					cleanSuccess++;
				} catch (Exception e) {
					cleanError++;
					e.printStackTrace();
					logger.error("have error projectnumber ="+projectnumber);
					logger.warn(jsonObject.toJSONString());
				}

			}
		}
		logger.info("count="+count);
		logger.info("cleanSuccess="+cleanSuccess);
		logger.info("cleanError="+cleanError);
		logger.info("cleanRepeat="+cleanRepeat);
		logger.info("moreZhuanRangFang="+moreZhuanRangFang);
	}


	/**
	 * 从ProjectClean类中解析一级节点
	 * @param projectClean
	 */
	private void initProjectCleanSecond(ProjectClean projectClean) throws Exception{
		projectClean.setYunprojectnumber(initType(projectClean.getLaiyuan()));
		if(projectClean.getBegindate() != null && projectClean.getEnddate() != null){
			projectClean.setNoticeperiod(DateUtils.getDutyDays(DateUtils.formatDate(projectClean.getBegindate(), null), DateUtils.formatDate(projectClean.getEnddate(),null)));
			projectClean.setValidperiod(new Double(DateUtils.getDistanceOfTwoDate(projectClean.getBegindate(),projectClean.getEnddate())).intValue());
		}

		String zhucedizhi = null;
		String region = projectClean.getRegion();//标的所在地
		if(projectClean.getDatafrom()==101){//北京
			JSONObject jsonObject = projectClean.getQiyejiankuang().getJSONObject("标的企业基本情况");
			projectClean.setCompanyname(projectClean.getQiyejiankuang().getString("标的企业名称")==null?projectClean.getQiyejiankuang().getJSONObject("标的企业基本情况").getString("标的企业名称"):projectClean.getQiyejiankuang().getString("标的企业名称"));
			zhucedizhi = jsonObject.getString("注册地（住所）");
			projectClean.setInternalreview(projectClean.getQiyejiankuang().getString("内部审议情况"));

			JSONArray zhuanRangFangJianJieArr = projectClean.getZhuanRangFangJianJieArr();
			JSONObject zhuanRangFangJianJieJson = null;
			JSONObject zhuanrangfagn = null;// zhuanRangFangJianJieArr.getJSONObject(0).getJSONObject("基本情况");
			if(zhuanRangFangJianJieArr != null){
				zhuanRangFangJianJieJson = zhuanRangFangJianJieArr.getJSONObject(1);
				zhuanrangfagn = zhuanRangFangJianJieArr.getJSONObject(0).getJSONObject("基本情况");
				if(zhuanrangfagn == null) zhuanrangfagn = zhuanRangFangJianJieArr.getJSONObject(0).getJSONObject("转让方基本情况");
			}else{
				zhuanRangFangJianJieJson = projectClean.getZhuanRangFangJianJie();
				zhuanrangfagn = zhuanRangFangJianJieJson.getJSONObject("转让方基本情况");
			}
			projectClean.setResolutiontype(zhuanRangFangJianJieJson.getJSONObject("产权转让行为批准情况").getString("转让方决策文件类型") != null?
					zhuanRangFangJianJieJson.getJSONObject("产权转让行为批准情况").getString("转让方决策文件类型") : zhuanRangFangJianJieJson.getJSONObject("产权转让行为批准情况").getString("决议类型"));
			projectClean.setTransactionway(projectClean.getGuaPaiXinXi().getString("竞价方式"));

			JSONObject baoZhengJinSheDing = projectClean.getFourJiaoYiTiaoJian().getJSONObject("保证金设定");
			String jiaonajine = baoZhengJinSheDing.getString("交纳金额");
			if(jiaonajine != null && !"".equals(jiaonajine)){
				if(jiaonajine.contains("交纳")){
					projectClean.setPaymoney(Double.valueOf(jiaonajine.substring(jiaonajine.indexOf("交纳")+2, jiaonajine.indexOf("万元")).trim()));
				}else{
					jiaonajine = jiaonajine.replace("万元", "");
					if(jiaonajine != null && !"".equals(jiaonajine)){
						projectClean.setPaymoney(Double.valueOf(jiaonajine.trim()));
					}
				}

				projectClean.setPaydate(baoZhengJinSheDing.getString("交纳时间"));
				projectClean.setPayway(baoZhengJinSheDing.getString("交纳方式"));
			}
			JSONObject enterpriseInfo = new JSONObject();//标的企业基本情况
			enterpriseInfo.put("zhuCeDi", jsonObject.getString("注册地（住所）"));
			enterpriseInfo.put("chengLiShiJian", jsonObject.getString("成立时间")==null?jsonObject.getString("成立日期"):jsonObject.getString("成立时间"));
			enterpriseInfo.put("zhuCeZiBen", jsonObject.getString("注册资本（万元）"));
			enterpriseInfo.put("jingYingFanWei", jsonObject.getString("经营范围"));
			enterpriseInfo.put("zhiGongRenShu", jsonObject.getString("职工人数"));
			enterpriseInfo.put("shiFouHanYouGuoYouHuaBoTuDi", jsonObject.getString("是否含有国有划拨土地"));
			projectClean.setEnterpriseinfo(enterpriseInfo.toString());

			//十大股东
			JSONObject guquanjiegou = projectClean.getQiyejiankuang().getJSONObject("标的企业股权结构");
			JSONObject stockStructure = new JSONObject();
			if(jsonObject.getString("其他股东是否放弃优先受让权") != null && !"".equals(jsonObject.getString("其他股东是否放弃优先受让权"))){
				stockStructure.put("shiFouFangQiYouXianGouMaiQuan",jsonObject.getString("其他股东是否放弃优先受让权"));
			}else{
				stockStructure.put("shiFouFangQiYouXianGouMaiQuan", guquanjiegou.getString("标的企业原股东是否放弃行使优先购买权")!=null?guquanjiegou.getString("标的企业原股东是否放弃行使优先购买权"):guquanjiegou.getString("其他股东是否放弃优先受让权"));
			}
			JSONArray tenArray = new JSONArray();
			JSONArray tenArraySource = guquanjiegou.getJSONArray("前十位股东");
			if(tenArraySource == null) tenArraySource = guquanjiegou.getJSONArray("前十位股东名称");
			for (int i = 0; i < tenArraySource.size(); i++) {
				JSONObject object = tenArraySource.getJSONObject(i);
				if(object.getString("股东名称") == null || "".equals(object.getString("股东名称"))) continue;
				JSONObject nObject = new JSONObject();
				nObject.put("guDongMingCheng", object.getString("股东名称"));
				if(object.getString("持股比例") != null){
					nObject.put("guDongChiGuBiLi", object.getString("持股比例"));
				}else{
					nObject.put("guDongChiGuBiLi", object.getString("持有比例（%）"));
				}
				tenArray.add(nObject);
			}
			stockStructure.put("guDongLiBIao", tenArray);
			projectClean.setStockstructure(stockStructure.toString());

			//审计报告 -年报
			JSONObject zhuyaocaiwuzhibiao = projectClean.getQiyejiankuang().getJSONObject("主要财务指标（单位：万元）");
			if(zhuyaocaiwuzhibiao == null) zhuyaocaiwuzhibiao = projectClean.getQiyejiankuang().getJSONObject("主要财务指标（万元）");
			JSONObject niandushuju2015 = zhuyaocaiwuzhibiao.getJSONObject("2015年度审计报告数据")==null ? zhuyaocaiwuzhibiao.getJSONObject("以下数据出自企业年度审计报告"):zhuyaocaiwuzhibiao.getJSONObject("2015年度审计报告数据");
			JSONObject auditReport = new JSONObject();
			if(niandushuju2015 != null){
				JSONArray shenJiBaoGao = new JSONArray();
				if(niandushuju2015.getJSONObject("2014年度") != null){
					beiJinNianDuShenJi(auditReport, niandushuju2015.getJSONObject("2014年度"), shenJiBaoGao, "2014年度");
				}
				if(niandushuju2015.getJSONObject("2015年度") != null){
					beiJinNianDuShenJi(auditReport, niandushuju2015.getJSONObject("2015年度"), shenJiBaoGao, "2015年度");
				}
				if(niandushuju2015.getJSONObject("2014年度") == null && niandushuju2015.getJSONObject("2015年度") == null){
					beiJinNianDuShenJi(auditReport, niandushuju2015, shenJiBaoGao, "2015年度");
				}
				auditReport.put("shenJiBaoGao", shenJiBaoGao);
			}
			projectClean.setAuditreport(auditReport.toString());
			//财务报表
			JSONArray financereport = new JSONArray();
			//JSONObject caiwuBaoBiaoSource = zhuyaocaiwuzhibiao.getJSONObject("以下数据出自企业财务报表");
			if(zhuyaocaiwuzhibiao != null){
				Set<String> keyset = zhuyaocaiwuzhibiao.keySet();
				for (String string : keyset) {
					if(!string.contains("年度审计报告")){
						JSONObject caiwuBaoBiao = new JSONObject();
						if(string.equals("以下数据出自企业财务报表")){
							caiwuBaoBiao.put("baoBiaoRiQi", zhuyaocaiwuzhibiao.getJSONObject(string).getString("报表日期"));
						}else{
							caiwuBaoBiao.put("baoBiaoRiQi", string.substring(0, string.length()-5).replace("年", "-").replace("月", "-"));
						}
						caiwuBaoBiao.put("baoBiaoLeiXing", zhuyaocaiwuzhibiao.getJSONObject(string).getString("报表类型")==null?"":zhuyaocaiwuzhibiao.getJSONObject(string).getString("报表类型"));
						caiwuBaoBiao.put("yingYeShouRu", zhuyaocaiwuzhibiao.getJSONObject(string).getString("营业收入") == null ? "" : zhuyaocaiwuzhibiao.getJSONObject(string).getString("营业收入"));
						caiwuBaoBiao.put("yingYeLiRun", zhuyaocaiwuzhibiao.getJSONObject(string).getString("营业利润") == null ? "" : zhuyaocaiwuzhibiao.getJSONObject(string).getString("营业利润"));
						caiwuBaoBiao.put("jingLiRun", zhuyaocaiwuzhibiao.getJSONObject(string).getString("净利润") == null ? "" : zhuyaocaiwuzhibiao.getJSONObject(string).getString("净利润"));
						caiwuBaoBiao.put("ziChanZongJi", zhuyaocaiwuzhibiao.getJSONObject(string).getString("资产总计") == null ? "" : zhuyaocaiwuzhibiao.getJSONObject(string).getString("资产总计"));
						caiwuBaoBiao.put("fuZhanZongJi", zhuyaocaiwuzhibiao.getJSONObject(string).getString("负债总计") == null ? "" : zhuyaocaiwuzhibiao.getJSONObject(string).getString("负债总计"));
						caiwuBaoBiao.put("suoYouZheQuanYi", zhuyaocaiwuzhibiao.getJSONObject(string).getString("所有者权益") == null ? "" : zhuyaocaiwuzhibiao.getJSONObject(string).getString("所有者权益"));
						financereport.add(caiwuBaoBiao);
					}
				}

			}
			projectClean.setFinancereport(financereport.toString());

			//重要信息披露
			JSONObject imporInforDisclosure = new JSONObject();
			JSONObject zhongyaoxinxi = projectClean.getQiyejiankuang();
			if(zhongyaoxinxi != null){
				imporInforDisclosure.put("qiTaPiLuNeiRong", zhongyaoxinxi.getString("其他披露的内容")==null?zhongyaoxinxi.getJSONObject("重要信息披露").getString("其他披露内容"):zhongyaoxinxi.getString("其他披露的内容"));
				imporInforDisclosure.put("guanLiCengCanYuShouRangYiXiang", zhongyaoxinxi.getJSONObject("标的企业基本情况").getString("企业管理层是否参与受让")==null?
						zhongyaoxinxi.getJSONObject("重要信息披露").getString("管理层拟参与受让意向"):zhongyaoxinxi.getJSONObject("标的企业基本情况").getString("企业管理层是否参与受让"));
			}
			projectClean.setImporinfordisclosure(imporInforDisclosure.toString());

			//转让方基本情况JSON格式
			//JSONObject zhuanrangfagn = zhuanRangFangJianJieArr.getJSONObject(0).getJSONObject("基本情况");
			if(zhuanrangfagn != null){
				JSONObject transferInfo = new JSONObject();
				transferInfo.put("zhuanRangFangMingCheng", zhuanrangfagn.getString("转让方名称"));
				transferInfo.put("zhuanRangGuBiLi", zhuanrangfagn.getString("持有产(股)权比例"));
				transferInfo.put("zhuanRangGuBiLi", zhuanrangfagn.getString("拟转让产(股)权比例"));
				projectClean.setTransferinfo(transferInfo.toString());
			}

			//交易条件JSON格式
			JSONObject jiaoyitiaojian = projectClean.getFourJiaoYiTiaoJian().getJSONObject("交易条件");
			if(jiaoyitiaojian != null){
				JSONObject transactionTerm = new JSONObject();
				transactionTerm.put("jiaKuanZhiFuFangShi", jiaoyitiaojian.getString("价款支付方式"));
				transactionTerm.put("qiTaTiaoJian", jiaoyitiaojian.getString("与转让相关的其他条件")==null ? jiaoyitiaojian.getString("与转让相关其他条件"):jiaoyitiaojian.getString("与转让相关的其他条件"));
				projectClean.setTransactionterm(transactionTerm.toString());
			}

		}else if(projectClean.getDatafrom()==102){//上海
			JSONObject jsonObject = projectClean.getQiyejiankuang().getJSONObject("标的企业基本情况");
			projectClean.setCompanyname(jsonObject.getString("标的企业名称"));
			zhucedizhi = jsonObject.getString("注册地(地址)");
			projectClean.setInternalreview(projectClean.getQiyejiankuang().getJSONObject("资产评估情况（万元）").getString("内部审议情况"));
			//上海没有决议类型

			projectClean.setTransactionway(projectClean.getGuaPaiXinXi().getJSONObject("挂牌信息").getString("交易方式"));

			JSONObject baoZhengJinSheDing = projectClean.getFourJiaoYiTiaoJian().getJSONObject("保证金设定");
			String jiaonajine = baoZhengJinSheDing.getString("交纳金额");
			if(jiaonajine != null){
				jiaonajine = jiaonajine.replace("（万元）", "");
				if(jiaonajine != null && !"".equals(jiaonajine)){
					projectClean.setPaymoney(Double.valueOf(jiaonajine.trim()));
				}
				projectClean.setPaydate(baoZhengJinSheDing.getString("交纳时间"));
				projectClean.setPayway(projectClean.getFourJiaoYiTiaoJian().getJSONObject("交易条件").getString("价款支付方式"));
			}
			JSONObject enterpriseInfo = new JSONObject();//标的企业基本情况
			enterpriseInfo.put("zhuCeDi", jsonObject.getString("注册地(地址)"));
			enterpriseInfo.put("chengLiShiJian", jsonObject.getString("成立时间"));
			enterpriseInfo.put("zhuCeZiBen", jsonObject.getString("注册资本"));
			enterpriseInfo.put("jingYingFanWei", jsonObject.getString("经营范围"));
			enterpriseInfo.put("zhiGongRenShu", jsonObject.getString("职工人数"));
			enterpriseInfo.put("shiFouHanYouGuoYouHuaBoTuDi", jsonObject.getString("是否含有国有划拨土地"));
			projectClean.setEnterpriseinfo(enterpriseInfo.toString());

			//十大股东
			JSONObject guquanjiegou = projectClean.getQiyejiankuang().getJSONObject("标的企业股权结构");
			JSONObject stockStructure = new JSONObject();
			stockStructure.put("shiFouFangQiYouXianGouMaiQuan", guquanjiegou.getString("老股东是否放弃行使优先购买权"));
			JSONArray tenArray = new JSONArray();
			JSONArray tenArraySource = guquanjiegou.getJSONArray("十大股东");
			for (int i = 0; i < tenArraySource.size(); i++) {
				JSONObject object = tenArraySource.getJSONObject(i);
				if(object.getString("股东名称") == null || "".equals(object.getString("股东名称"))) continue;
				JSONObject nObject = new JSONObject();
				nObject.put("guDongMingCheng", object.getString("股东名称"));
				nObject.put("guDongChiGuBiLi", object.getString("持股比例"));
				tenArray.add(nObject);
			}
			stockStructure.put("guDongLiBIao", tenArray);
			projectClean.setStockstructure(stockStructure.toString());

			//审计报告 -年报
			JSONObject zhuyaocaiwuzhibiao = projectClean.getQiyejiankuang().getJSONObject("主要财务指标（万元）");
			JSONArray niandushuju = zhuyaocaiwuzhibiao.getJSONArray("年度审计报告");
			JSONObject auditReport = new JSONObject();
			if(niandushuju != null && niandushuju.size() > 0){
				auditReport.put("shenJiJiGou", niandushuju.getJSONObject(0).getString("审计机构"));
				JSONArray shenJiBaoGao = new JSONArray();
				for (int i = 0; i < niandushuju.size(); i++) {
					JSONObject object = niandushuju.getJSONObject(i);
					JSONObject json = new JSONObject();
					json.put("nianDu", object.getString("年度"));
					json.put("yingYeShouRu", object.getString("营业收入（万元）"));
					json.put("yingYeLiRun", object.getString("营业利润（万元）"));
					json.put("jingLiRun", object.getString("净利润（万元）"));
					json.put("ziChanZongJi", object.getString("资产总计（万元）"));
					json.put("fuZhanZongJi", object.getString("负债总计（万元）"));
					json.put("suoYouZheQuanYi", object.getString("所有者权益（万元）"));
					shenJiBaoGao.add(json);
				}

				auditReport.put("shenJiBaoGao", shenJiBaoGao);
			}
			projectClean.setAuditreport(auditReport.toString());

			//财务报表
			JSONArray financereport = new JSONArray();
			JSONObject caiwuBaoBiaoSource = zhuyaocaiwuzhibiao.getJSONObject("企业财务报告");
			if(caiwuBaoBiaoSource != null){
				JSONObject caiwuBaoBiao = new JSONObject();
				caiwuBaoBiao.put("baoBiaoLeiXing", caiwuBaoBiaoSource.getString("报表类型")==null?"":caiwuBaoBiaoSource.getString("报表类型"));
				caiwuBaoBiao.put("baoBiaoRiQi", caiwuBaoBiaoSource.getString("报表日期"));
				caiwuBaoBiao.put("yingYeShouRu", caiwuBaoBiaoSource.getString("营业收入（万元）"));
				caiwuBaoBiao.put("yingYeLiRun", caiwuBaoBiaoSource.getString("营业利润（万元）"));
				caiwuBaoBiao.put("jingLiRun", caiwuBaoBiaoSource.getString("净利润（万元）"));
				caiwuBaoBiao.put("ziChanZongJi", caiwuBaoBiaoSource.getString("资产总计（万元）"));
				caiwuBaoBiao.put("fuZhanZongJi", caiwuBaoBiaoSource.getString("负债总计（万元）"));
				caiwuBaoBiao.put("suoYouZheQuanYi", caiwuBaoBiaoSource.getString("所有者权益（万元）"));
				financereport.add(caiwuBaoBiao);
			}
			projectClean.setFinancereport(financereport.toString());

			//重要信息披露
			JSONObject imporInforDisclosure = new JSONObject();
			JSONObject zhongyaoxinxi = projectClean.getQiyejiankuang().getJSONObject("重要信息披露");
			if(zhongyaoxinxi != null){
				imporInforDisclosure.put("qiTaPiLuNeiRong", zhongyaoxinxi.getString("其他披露内容"));
				imporInforDisclosure.put("guanLiCengCanYuShouRangYiXiang", zhongyaoxinxi.getString("管理层拟参与受让意向"));
			}
			projectClean.setImporinfordisclosure(imporInforDisclosure.toString());

			//转让方基本情况JSON格式
			JSONObject zhuanrangfang = projectClean.getZhuanRangFangJianJie().getJSONObject("转让方基本情况");
			if(zhuanrangfang != null){
				JSONObject transferInfo = new JSONObject();
				transferInfo.put("zhuanRangFangMingCheng", zhuanrangfang.getString("转让方名称"));
				transferInfo.put("zhuanRangGuBiLi", zhuanrangfang.getString("持有产(股)权比例"));
				transferInfo.put("zhuanRangGuBiLi", zhuanrangfang.getString("拟转让产(股)权比例"));
				projectClean.setTransferinfo(transferInfo.toString());
			}

			//交易条件JSON格式
			JSONObject jiaoyitiaojian = projectClean.getFourJiaoYiTiaoJian().getJSONObject("交易条件");
			if(jiaoyitiaojian != null){
				JSONObject transactionTerm = new JSONObject();
				transactionTerm.put("jiaKuanZhiFuFangShi", jiaoyitiaojian.getString("价款支付方式"));
				transactionTerm.put("qiTaTiaoJian", jiaoyitiaojian.getString("与转让相关其他条件"));
				projectClean.setTransactionterm(transactionTerm.toString());
			}

		}else if(projectClean.getDatafrom()==103){//重庆  标的企业情况
			JSONObject jsonObject = projectClean.getQiyejiankuang().getJSONObject("标的企业情况");
			projectClean.setCompanyname(jsonObject.getString("标的企业名称"));
			zhucedizhi = jsonObject.getString("注册地（地址）");
			projectClean.setInternalreview(projectClean.getQiyejiankuang().getJSONObject("资产评估情况").getString("内部审议情况"));
			JSONObject zhuanRangFangJianJieJson = projectClean.getZhuanRangFangJianJie();
			if(zhuanRangFangJianJieJson.getJSONObject("产权转让行为批准情况") != null){
				projectClean.setResolutiontype(zhuanRangFangJianJieJson.getJSONObject("产权转让行为批准情况").getString("决议类型"));
			}else{
				JSONArray jsonArray = zhuanRangFangJianJieJson.getJSONArray("转让方基本情况");
				if(jsonArray != null){
					projectClean.setResolutiontype(jsonArray.getJSONObject(0).getString("决议类型"));
				}
			}
			projectClean.setTransactionway(projectClean.getGuaPaiXinXi().getString("交易方式"));

			JSONObject baoZhengJinSheDing = projectClean.getFourJiaoYiTiaoJian().getJSONObject("保证金设定");
			String jiaonajine = baoZhengJinSheDing.getString("交纳金额");
			if(jiaonajine != null){
				jiaonajine = jiaonajine.replace("万元", "");
				if(jiaonajine != null && !"".equals(jiaonajine)){
					projectClean.setPaymoney(Double.valueOf(jiaonajine.trim()));
				}
				projectClean.setPaydate(baoZhengJinSheDing.getString("交纳时间"));
				projectClean.setPayway(baoZhengJinSheDing.getString("交纳方式"));
			}

			JSONObject enterpriseInfo = new JSONObject();//标的企业基本情况
			enterpriseInfo.put("zhuCeDi", jsonObject.getString("注册地（地址）"));
			enterpriseInfo.put("chengLiShiJian", jsonObject.getString("成立时间"));
			enterpriseInfo.put("zhuCeZiBen", jsonObject.getString("注册资本"));
			enterpriseInfo.put("jingYingFanWei", jsonObject.getString("经营范围"));
			enterpriseInfo.put("zhiGongRenShu", jsonObject.getString("职工人数"));
			enterpriseInfo.put("shiFouHanYouGuoYouHuaBoTuDi", jsonObject.getString("是否含有国有划拨土地"));
			projectClean.setEnterpriseinfo(enterpriseInfo.toString());

			JSONObject guquanjiegou = projectClean.getQiyejiankuang().getJSONObject("标的企业股权结构");
			JSONObject stockStructure = new JSONObject();
			stockStructure.put("shiFouFangQiYouXianGouMaiQuan", guquanjiegou.getString("老股东是否放弃行使优先购买权"));
			JSONArray tenArray = new JSONArray();
			JSONArray tenArraySource = guquanjiegou.getJSONArray("十大股东");
			for (int i = 0; i < tenArraySource.size(); i++) {
				JSONObject object = tenArraySource.getJSONObject(i);
				if(object.getString("前十位股东名称") == null || "".equals(object.getString("前十位股东名称"))) continue;
				JSONObject nObject = new JSONObject();
				nObject.put("guDongMingCheng", object.getString("前十位股东名称"));
				nObject.put("guDongChiGuBiLi", object.getString("持股比例"));
				tenArray.add(nObject);
			}
			stockStructure.put("guDongLiBIao", tenArray);
			projectClean.setStockstructure(stockStructure.toString());

			//审计报告 -年报
			JSONObject zhuyaocaiwuzhibiao = projectClean.getQiyejiankuang().getJSONObject("主要财务指标");
			JSONObject niandushuju2015 = zhuyaocaiwuzhibiao.getJSONObject("以下数据出自年度审计报告").getJSONObject("2015（年度）");
			JSONObject auditReport = new JSONObject();
			if(niandushuju2015 != null){
				auditReport.put("shenJiJiGou", niandushuju2015.getString("审计机构"));
				JSONArray shenJiBaoGao = new JSONArray();
				JSONObject json2015 = new JSONObject();
				json2015.put("nianDu", "2015年度");
				json2015.put("yingYeShouRu", niandushuju2015.getString("营业收入"));
				json2015.put("yingYeLiRun", niandushuju2015.getString("营业利润"));
				json2015.put("jingLiRun", niandushuju2015.getString("净利润"));
				json2015.put("ziChanZongJi", niandushuju2015.getString("资产总计"));
				json2015.put("fuZhanZongJi", niandushuju2015.getString("负债总计"));
				json2015.put("suoYouZheQuanYi", niandushuju2015.getString("所有者权益"));
				shenJiBaoGao.add(json2015);
				auditReport.put("shenJiBaoGao", shenJiBaoGao);
			}
			projectClean.setAuditreport(auditReport.toString());

			//财务报表
			JSONArray financereport = new JSONArray();
			JSONObject caiwuBaoBiaoSource = zhuyaocaiwuzhibiao.getJSONObject("以下数据出自企业财务报表").getJSONObject("报表日期");
			if(caiwuBaoBiaoSource != null){
				JSONObject caiwuBaoBiao = new JSONObject();
				caiwuBaoBiao.put("baoBiaoLeiXing", caiwuBaoBiaoSource.getString("报表类型")==null?"":caiwuBaoBiaoSource.getString("报表类型"));
				caiwuBaoBiao.put("baoBiaoRiQi", caiwuBaoBiaoSource.getString("报表日期").replace("万元", ""));
				caiwuBaoBiao.put("yingYeShouRu", caiwuBaoBiaoSource.getString("营业收入").replace("万元", ""));
				caiwuBaoBiao.put("yingYeLiRun", caiwuBaoBiaoSource.getString("营业利润").replace("万元", ""));
				caiwuBaoBiao.put("jingLiRun", caiwuBaoBiaoSource.getString("净利润").replace("万元", ""));
				caiwuBaoBiao.put("ziChanZongJi", caiwuBaoBiaoSource.getString("资产总计").replace("万元", ""));
				caiwuBaoBiao.put("fuZhanZongJi", caiwuBaoBiaoSource.getString("负债总计").replace("万元", ""));
				caiwuBaoBiao.put("suoYouZheQuanYi", caiwuBaoBiaoSource.getString("所有者权益")==null?caiwuBaoBiaoSource.getString("报所有者权益").replace("万元", ""):caiwuBaoBiaoSource.getString("所有者权益").replace("万元", ""));
				financereport.add(caiwuBaoBiao);
			}
			projectClean.setFinancereport(financereport.toString());

			//重要信息披露
			JSONObject imporInforDisclosure = new JSONObject();
			JSONObject zhongyaoxinxi = projectClean.getQiyejiankuang().getJSONObject("重要信息披露");
			if(zhongyaoxinxi != null){
				imporInforDisclosure.put("qiTaPiLuNeiRong", zhongyaoxinxi.getString("其他披露内容"));
				imporInforDisclosure.put("guanLiCengCanYuShouRangYiXiang", zhongyaoxinxi.getString("管理层拟参与受让意向"));
			}
			projectClean.setImporinfordisclosure(imporInforDisclosure.toString());

			//转让方基本情况JSON格式
			JSONArray zhuanrangfagn = projectClean.getZhuanRangFangJianJie().getJSONArray("转让方基本情况");
			if(zhuanrangfagn != null && zhuanrangfagn.size() > 0){
				JSONObject transferInfo = new JSONObject();
				transferInfo.put("zhuanRangFangMingCheng", zhuanrangfagn.getJSONObject(0).getString("转让方名称"));
				transferInfo.put("zhuanRangGuBiLi", zhuanrangfagn.getJSONObject(0).getString("持有产(股)权比例(%)"));
				transferInfo.put("zhuanRangGuBiLi", zhuanrangfagn.getJSONObject(0).getString("拟转让产(股)权比例(%)"));
				projectClean.setTransferinfo(transferInfo.toString());
			}

			//交易条件JSON格式
			JSONObject jiaoyitiaojian = projectClean.getFourJiaoYiTiaoJian().getJSONObject("交易条件");
			if(jiaoyitiaojian != null){
				JSONObject transactionTerm = new JSONObject();
				transactionTerm.put("jiaKuanZhiFuFangShi", jiaoyitiaojian.getString("价款支付方式"));
				transactionTerm.put("qiTaTiaoJian", jiaoyitiaojian.getString("与转让相关其他条件"));
				projectClean.setTransactionterm(transactionTerm.toString());
			}

		}else if(projectClean.getDatafrom()==104){//天津
			JSONObject jsonObject = projectClean.getQiyejiankuang().getJSONObject("标的企业基本情况");
			projectClean.setCompanyname(jsonObject.getString("标的企业名称"));
			zhucedizhi = jsonObject.getString("注册地（住所）");
			projectClean.setInternalreview(projectClean.getQiyejiankuang().getString("内部审议情况"));
			JSONObject zhuanRangFangJianJieJson = projectClean.getZhuanRangFangJianJie();
			projectClean.setResolutiontype(zhuanRangFangJianJieJson.getJSONObject("转让方1").getJSONObject("产权转让行为批准情况").getString("决议类型"));
			projectClean.setTransactionway(projectClean.getGuaPaiXinXi().getString("交易方式"));
			JSONObject baoZhengJinSheDing = projectClean.getFourJiaoYiTiaoJian().getJSONObject("保证金设定");
			String jiaonajine = baoZhengJinSheDing.getString("交纳金额");
			if(jiaonajine != null){
				jiaonajine = jiaonajine.substring(jiaonajine.lastIndexOf("[")+2,jiaonajine.lastIndexOf("]"));
				if(jiaonajine != null && !"".equals(jiaonajine)){
					projectClean.setPaymoney(Double.valueOf(jiaonajine));
				}
				projectClean.setPaydate(baoZhengJinSheDing.getString("交纳时间"));
				projectClean.setPayway(baoZhengJinSheDing.getString("交纳方式"));
			}

			JSONObject enterpriseInfo = new JSONObject();//标的企业基本情况
			enterpriseInfo.put("zhuCeDi", jsonObject.getString("注册地（住所）"));
			enterpriseInfo.put("chengLiShiJian", jsonObject.getString("成立时间"));
			enterpriseInfo.put("zhuCeZiBen", jsonObject.getString("注册资本（万元）"));
			enterpriseInfo.put("jingYingFanWei", jsonObject.getString("经营范围"));
			enterpriseInfo.put("zhiGongRenShu", jsonObject.getString("职工人数"));
			enterpriseInfo.put("shiFouHanYouGuoYouHuaBoTuDi", jsonObject.getString("是否含有国有划拨土地"));
			projectClean.setEnterpriseinfo(enterpriseInfo.toString());

			JSONObject guquanjiegou = projectClean.getQiyejiankuang().getJSONObject("标的企业股权结构");
			JSONObject stockStructure = new JSONObject();
			stockStructure.put("shiFouFangQiYouXianGouMaiQuan", guquanjiegou.getString("标的企业原股东是否放弃行使优先购买权")==null?guquanjiegou.getString("原股东是否放弃行使优先购买权"):"");
			JSONArray tenArray = new JSONArray();
			JSONArray tenArraySource = guquanjiegou.getJSONArray("十大股东");
			for (int i = 0; i < tenArraySource.size(); i++) {
				JSONObject object = tenArraySource.getJSONObject(i);
				if(object.getString("前十位股东名称") == null || "".equals(object.getString("前十位股东名称"))) continue;
				JSONObject nObject = new JSONObject();
				nObject.put("guDongMingCheng", object.getString("前十位股东名称"));
				nObject.put("guDongChiGuBiLi", object.getString("持股比例"));
				tenArray.add(nObject);
			}
			stockStructure.put("guDongLiBIao", tenArray);
			projectClean.setStockstructure(stockStructure.toString());

			//审计报告 -年报
			JSONObject zhuyaocaiwuzhibiao = projectClean.getQiyejiankuang().getJSONObject("主要财务指标（万元）");
			JSONArray niandushuju = zhuyaocaiwuzhibiao.getJSONObject("以下数据出自年度审计报告").getJSONArray("年");
			JSONObject auditReport = new JSONObject();
			auditReport.put("shenJiJiGou", zhuyaocaiwuzhibiao.getJSONObject("以下数据出自年度审计报告").getString("审计机构"));
			JSONArray shenJiBaoGao = new JSONArray();
			tianJinNianDuShenJi(niandushuju,shenJiBaoGao,"年");
			niandushuju = zhuyaocaiwuzhibiao.getJSONObject("以下数据出自年度审计报告").getJSONArray("2015年");
			tianJinNianDuShenJi(niandushuju,shenJiBaoGao,"2015年");
			auditReport.put("shenJiBaoGao", shenJiBaoGao);
			projectClean.setAuditreport(auditReport.toString());

			//财务报表
			JSONArray financereport = new JSONArray();
			JSONObject caiwuBaoBiaoSource = zhuyaocaiwuzhibiao.getJSONObject("以下数据出自企业财务报表");
			if(caiwuBaoBiaoSource != null){
				JSONObject caiwuBaoBiao = new JSONObject();
				caiwuBaoBiao.put("baoBiaoLeiXing", caiwuBaoBiaoSource.getString("报表类型")==null?"":caiwuBaoBiaoSource.getString("报表类型"));
				caiwuBaoBiao.put("baoBiaoRiQi", caiwuBaoBiaoSource.getString("报表日期"));
				caiwuBaoBiao.put("yingYeShouRu", caiwuBaoBiaoSource.getString("营业收入"));
				caiwuBaoBiao.put("yingYeLiRun", caiwuBaoBiaoSource.getString("营业利润"));
				caiwuBaoBiao.put("jingLiRun", caiwuBaoBiaoSource.getString("净利润"));
				caiwuBaoBiao.put("ziChanZongJi", caiwuBaoBiaoSource.getString("资产总计"));
				caiwuBaoBiao.put("fuZhanZongJi", caiwuBaoBiaoSource.getString("负债总计"));
				caiwuBaoBiao.put("suoYouZheQuanYi", caiwuBaoBiaoSource.getString("所有者权益"));
				financereport.add(caiwuBaoBiao);
			}
			projectClean.setFinancereport(financereport.toString());

			//重要信息披露
			JSONObject imporInforDisclosure = new JSONObject();
			JSONObject zhongyaoxinxi = projectClean.getQiyejiankuang().getJSONObject("重要信息披露");
			if(zhongyaoxinxi != null){
				imporInforDisclosure.put("qiTaPiLuNeiRong", zhongyaoxinxi.getString("其他披露内容"));
				imporInforDisclosure.put("guanLiCengCanYuShouRangYiXiang", zhongyaoxinxi.getString("管理层拟参与受让意向"));
			}
			projectClean.setImporinfordisclosure(imporInforDisclosure.toString());

			//转让方基本情况JSON格式
			JSONObject zhuanrangfang1 = projectClean.getZhuanRangFangJianJie().getJSONObject("转让方1");
			if(zhuanrangfang1 != null){
				JSONObject json = zhuanrangfang1.getJSONObject("转让方基本情况");
				JSONObject transferInfo = new JSONObject();
				transferInfo.put("zhuanRangFangMingCheng", json.getString("转让方名称"));
				transferInfo.put("zhuanRangGuBiLi", json.getString("持有产(股)权比例"));
				transferInfo.put("zhuanRangGuBiLi", json.getString("拟转让产(股)权比例"));
				projectClean.setTransferinfo(transferInfo.toString());
			}

			//交易条件JSON格式
			JSONObject jiaoyitiaojian = projectClean.getFourJiaoYiTiaoJian().getJSONObject("交易条件");
			if(jiaoyitiaojian != null){
				JSONObject transactionTerm = new JSONObject();
				transactionTerm.put("jiaKuanZhiFuFangShi", jiaoyitiaojian.getString("价款支付方式"));
				transactionTerm.put("qiTaTiaoJian", jiaoyitiaojian.getString("与转让相关其他条件"));
				projectClean.setTransactionterm(transactionTerm.toString());
			}
		}
		projectClean.setSourcearea(zhucedizhi);
		for (Province province : provinceList) {
			if(region.contains(province.getProvincename()) || region.contains(province.getProvincename().substring(0, province.getProvincename().length()-1))){
				projectClean.setProvince(province.getProvincename());
				break;
			}
		}
		if(zhucedizhi != null){
			Integer provinceid = null;
			for (City city : cityList) {
				if(zhucedizhi.contains(city.getCityname()) || zhucedizhi.contains(city.getCityname().substring(0, city.getCityname().length()-1))){
					projectClean.setCity(city.getCityname());
					provinceid = city.getProvinceid();
					break;
				}
			}
			if(projectClean.getProvince() == null){
				for (Province province : provinceList) {
					if(zhucedizhi.contains(province.getProvincename())){
						projectClean.setProvince(province.getProvincename());
						break;
					}
				}
			}
			if(projectClean.getProvince() == null && provinceid != null){
				Province province = provinceDao.selectByPrimaryKey(provinceid);
				projectClean.setProvince(province.getProvincename());
			}

		}

		Category category = categoryDao.selectByCategoryName(projectClean.getSourcecategory());//查询二级行业
		if(category != null){
			projectClean.setSecondcategory(projectClean.getSourcecategory());
			projectClean.setFirstcategory(categoryDao.selectByPrimaryKey(category.getFathercatid()).getCategoryname());
		}else{
			category = categoryDao.selectFirstByCategoryName(projectClean.getSourcecategory());//查询一级行业
			if(category != null) projectClean.setFirstcategory(projectClean.getSourcecategory());
		}
	}

	/**
	 * 从mongoDB中获取数据
	 * @return
	 */
	private List<JSONObject> getMongoData(int skip, int limit, String tableName) {
		Query query = new Query();
		query.with(new Sort(new Order(Direction.ASC, "抓取时间")));
		query.skip(skip).limit(limit);
		return mongoTemplate.find(query, JSONObject.class, tableName);//crawler
	}

	/**
	 * 从mongoDB中获取数据总条数
	 * @return
	 */
	private long getMongoDataCount(String tableName) {
		return mongoTemplate.count(new Query(),  tableName);//crawler
	}

	/**
	 * 给ProjectClean对象赋值,主要是把一级节点取出来
	 * @param jsonObject
	 * @param propertyList
	 */
	private void initProjectCleanFirst(ProjectClean projectClean, JSONObject jsonObject, List<String> propertyList) {
		for (String str : propertyList) {
			//System.out.println(str);
			String s = PropertyUtil.getStringProperty(str);
			if (s.equals("jiaoyitiaojian")) {
				if (jsonObject.getJSONObject(str) != null) {
					projectClean.setFourJiaoYiTiaoJian(jsonObject.getJSONObject(str));
				} else {
					projectClean.setFourJiaoYiTiaoJian(jsonObject.getJSONObject("三：交易条件与受让方资格条件"));//重庆
				}

			} else if (s.equals("biaodisuoshuhangye")) {
				if (jsonObject.getString(str) != null) {
					projectClean.setSourcecategory(jsonObject.getString(str));
				} else {//北京 所属行业
					projectClean.setSourcecategory(jsonObject.getString("所属行业"));
				}

			} else if (s.equals("guapaijiage")) {
				if (jsonObject.getString(str) != null) {
					try {
						projectClean.setProjectprice(Double.valueOf(jsonObject.getString(str).replace("（万元）", "").replace("(万元)", "").replace("万元", "")));
					} catch (NumberFormatException e) {
						projectClean.setProjectprice(null);
					}
				} else {
					projectClean.setProjectprice(Double.valueOf(jsonObject.getString("转让底价").replace("万元", "")));//北京
				}

			} else if (s.equals("suozaidiqu")) {
				if (jsonObject.getString(str) != null) {
					projectClean.setRegion(jsonObject.getString(str));
				} else {//北京  所在地区
					projectClean.setRegion(jsonObject.getString("所在地区"));
				}

			} else if (s.equals("guapaiqishiriqi")) {
				if (jsonObject.getString(str) != null) {
					projectClean.setBegindate(DateUtils.parseDate(jsonObject.getString(str)));
				} else {
					projectClean.setBegindate(DateUtils.parseDate(jsonObject.getString("信息披露起始日期")));//北京
				}

			} else if (s.equals("qiyejiankuang")) {
				if (jsonObject.getJSONObject(str) != null) {
					projectClean.setQiyejiankuang(jsonObject.getJSONObject(str)); //二、标的企业简况  ---、天津、上海
				} else if (jsonObject.getJSONObject("二：标的企业简况") != null) { //重庆
					projectClean.setQiyejiankuang(jsonObject.getJSONObject("二：标的企业简况"));
				} else {
					projectClean.setQiyejiankuang(jsonObject.getJSONObject("二、转让标的基本情况"));//北京
				}

			} else if (s.equals("biaodimingcheng")) {
				if (jsonObject.getString(str) != null) {
					projectClean.setProjectname(jsonObject.getString(str));
				} else if (jsonObject.getString("转让标的名称") != null) {
					projectClean.setProjectname(jsonObject.getString("转让标的名称")); //重庆是转让标的名称
				} else {
					projectClean.setProjectname(jsonObject.getString("项目名称")); //北京
				}

			} else if (s.equals("zhuanrangfangjianjie")) {
				if (jsonObject.getString(str) != null && !"".equals(jsonObject.getString(str))) {
					if (jsonObject.getString(str).startsWith("{")) {
						if (jsonObject.getJSONObject(str) != null) {
							projectClean.setZhuanRangFangJianJie(jsonObject.getJSONObject(str));//三、转让方简况
						}
					} else if (jsonObject.getString(str).startsWith("[")) {
						if (jsonObject.getJSONArray(str) != null) {//北京
							projectClean.setZhuanRangFangJianJieArr(jsonObject.getJSONArray(str));
						}
					}
				} else if (jsonObject.getString("四：转让方简况") != null && !"".equals(jsonObject.getString("四：转让方简况"))) {
					projectClean.setZhuanRangFangJianJie(jsonObject.getJSONObject("四：转让方简况"));//重庆  四：转让方简况
				} else if (jsonObject.getJSONArray("三、转让方基本情况") != null) {//北京
					projectClean.setZhuanRangFangJianJieArr(jsonObject.getJSONArray("三、转让方基本情况"));
				}


			} else if (s.equals("guapaixinxi")) {
				if (jsonObject.getJSONObject(str) != null) {
					projectClean.setGuaPaiXinXi(jsonObject.getJSONObject(str));//五、挂牌信息
				} else if (jsonObject.getJSONObject("六、竞价方式") != null) {
					projectClean.setGuaPaiXinXi(jsonObject.getJSONObject("六、竞价方式"));//北京
				} else {
					projectClean.setGuaPaiXinXi(jsonObject.getJSONObject("五：挂牌信息"));//重庆
				}

			} else if (s.equals("guapaiqimanriqi")) {
				if (jsonObject.getString(str) != null) {
					projectClean.setEnddate(DateUtils.parseDate(jsonObject.getString(str)));
				} else {
					projectClean.setEnddate(DateUtils.parseDate(jsonObject.getString("信息披露结束日期")));//北京
				}

			} else if (s.equals("laiyuan")) {
				projectClean.setDatafrom(initSource(jsonObject.getString(str)));
				projectClean.setLaiyuan(jsonObject.getString(str));

			} else {
			}
    	}
	}

	private byte initSource(String string) {
		byte source = 0;
		if(string.startsWith("北京")){
			source = 101;
		}else if(string.startsWith("上海")){
			source = 102;
		}else if(string.startsWith("重庆")){
			source = 103;
		}else if(string.startsWith("天津")){
			source = 104;
		}
		return source;
	}


	/*项目
	 * 天津产权交易中心：国有股权转让
	 * @param laiyuan
	 * @return
	 */
	private String initType(String laiyuan){
		String type = "01";
		String leixing = laiyuan.split("：")[1];
		switch (leixing) {
		case "央企产权项目":
			type = "09";
			break;
		case "市属产权项目":
			type = "10";
			break;
		case "国有资产股权":
			type = "10";
			break;
		case "央企股权项目":
			type = "01";
			break;
		case "国有股权转让":
			type = "02";
			break;
		case "央企产权":
			type = "09";
			break;
		case "市属产权":
			type = "10";
			break;

		default:
			break;
		}

		return type;
	}

	/**
	 * 天津年度审计报告
	 * @param niandushuju
	 * @param shenJiBaoGao
	 * @param niandu
	 */
	private void tianJinNianDuShenJi(JSONArray niandushuju,JSONArray shenJiBaoGao,String niandu){
		if(niandushuju != null && niandushuju.size() > 0){
			for (int i = 0; i < niandushuju.size(); i++) {
				JSONObject object = niandushuju.getJSONObject(i);
				JSONObject json = new JSONObject();
				json.put("nianDu", niandu);
				json.put("yingYeShouRu", object.getString("营业收入").replace("万元", ""));
				json.put("yingYeLiRun", object.getString("营业利润").replace("万元", ""));
				json.put("jingLiRun", object.getString("净利润").replace("万元", ""));
				json.put("ziChanZongJi", object.getString("资产总计").replace("万元", ""));
				json.put("fuZhanZongJi", object.getString("负债总计").replace("万元", ""));
				json.put("suoYouZheQuanYi", object.getString("所有者权益").replace("万元", ""));
				shenJiBaoGao.add(json);
			}
		}
	}

	/**
	 * 北京年度审计报告
	 * @param auditReport
	 * @param niandushuju2015
	 * @param shenJiBaoGao
	 */
	private void beiJinNianDuShenJi(JSONObject auditReport, JSONObject niandushuju2015, JSONArray shenJiBaoGao, String niandu){
		auditReport.put("shenJiJiGou", niandushuju2015.getString("审计机构") == null ? "" : niandushuju2015.getString("审计机构"));
		JSONObject json2015 = new JSONObject();
		json2015.put("nianDu", niandu);
		json2015.put("yingYeShouRu", niandushuju2015.getString("营业收入") == null ? "" : niandushuju2015.getString("营业收入"));
		json2015.put("yingYeLiRun", niandushuju2015.getString("营业利润") == null ? "" : niandushuju2015.getString("营业利润"));
		json2015.put("jingLiRun", niandushuju2015.getString("净利润") == null ? "" : niandushuju2015.getString("净利润"));
		json2015.put("ziChanZongJi", niandushuju2015.getString("资产总计") == null ? "" : niandushuju2015.getString("资产总计"));
		json2015.put("fuZhanZongJi", niandushuju2015.getString("负债总计") == null ? "" : niandushuju2015.getString("负债总计"));
		json2015.put("suoYouZheQuanYi", niandushuju2015.getString("所有者权益") == null ? "" : niandushuju2015.getString("所有者权益"));
		shenJiBaoGao.add(json2015);
	}
}
