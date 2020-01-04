package com.module.data.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessRule;
import com.bean.BusinessRuleDetail;
import com.bean.DataStatistics;
import com.bean.LogRuleStatistics;
import com.bean.LogSnifferStatistics;
import com.common.comm.Constants;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.tools.DateUtil;
import com.common.type.BusinessChannelOnlinestate;
import com.common.type.DataStatisticsState;
import com.common.type.LogRuleStatisticsState;
import com.common.type.LogSnifferStatisticsState;
import com.module.data.service.DataStatisticsService;
import com.spacesat.util.json.JSONArray;
import com.spacesat.util.json.JSONObject;

public class DataStatisticsServiceImpl implements DataStatisticsService{

	@Override
	public int saveDataStatistics(JSONObject dataObject) {
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		BaseService dataStatisticsService=(BaseService) SpringConfig.getInstance().getService(DataStatistics.class);
		BaseService logRuleStatisticsService=(BaseService) SpringConfig.getInstance().getService(LogRuleStatistics.class);
		BaseService businessRuleDetailService=(BaseService) SpringConfig.getInstance().getService(BusinessRuleDetail.class);
		BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		BaseService logSnifferStatisticsService=(BaseService) SpringConfig.getInstance().getService(LogSnifferStatistics.class);
		int state=Constants.STATE_OPERATOR_LOST;
		try{
			JSONArray dataArray=dataObject.getJSONArray("data");
			Date d=new Date();
			String businessChannelId="";
			for(int i=0;i<dataArray.length();i++){
				JSONObject object=dataArray.getJSONObject(i);
				String objectid=object.getString("objectid");
				String accountid=object.getString("accountid");
				int count=object.getInt("count");
				BusinessRule businessRule=(BusinessRule) businessRuleService.findById(objectid);
				if(businessRule==null){
					continue;
				}
				if(StringUtils.isBlank(businessChannelId)){
					businessChannelId=businessRule.getBusinessChannel().getId();
				}
				List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
				expList.add(Restrictions.eq("sdate", DateUtil.getDate(d)));
				expList.add(Restrictions.eq("BusinessRule.id", objectid));
				List<BaseRecord> dataStatisticsList=dataStatisticsService.findList(expList, null, 0, 1, null);
				if(dataStatisticsList==null
						||dataStatisticsList.size()==0){
					DataStatistics dataStatistics=new DataStatistics();
					dataStatistics.setBusinessChannel(businessRule.getBusinessChannel());
					dataStatistics.setBusinessRule(businessRule);
					dataStatistics.setCreatetime(d);
					dataStatistics.setExact(businessRule.getExact());
					dataStatistics.setFuzzy(businessRule.getFuzzy());
					dataStatistics.setSdate(DateUtil.getDate(d));
					dataStatistics.setState(DataStatisticsState.normal.getCode());
					dataStatistics.setCount(count);
					dataStatistics.setUpdatetime(dataStatistics.getCreatetime());
					state=dataStatisticsService.saveObject(dataStatistics);
				}else{
					DataStatistics dataStatistics=(DataStatistics) dataStatisticsList.get(0);
					dataStatistics.setCount(dataStatistics.getCount()+count);
					dataStatistics.setUpdatetime(d);
					state=dataStatisticsService.updateObject(dataStatistics,true);
				}
//				if(businessRule.getNum()==null){
//					businessRule.setNum(count);
//				}else{
//					businessRule.setNum(businessRule.getNum()+count);
//				}
//				businessRule.setUpdatetime(d);
//				getBaseService(BusinessRule.class).updateObject(businessRule,true);
				BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail) businessRuleDetailService.findById(accountid);
				if(businessRuleDetail!=null){
//					if(businessRuleDetail.getNum()==null){
//						businessRuleDetail.setNum(count);
//					}else{
//						businessRuleDetail.setNum(businessRuleDetail.getNum()+count);
//					}
//					businessRuleDetail.setUpdatetime(d);
//					getBaseService(BusinessRuleDetail.class).updateObject(businessRuleDetail,true);
//					LogRuleStatistics logRuleStatistics=new LogRuleStatistics();
//					logRuleStatistics.setBusinessChannel(businessRule.getBusinessChannel());
//					logRuleStatistics.setBusinessRule(businessRule);
//					logRuleStatistics.setBusinessRuleDetail(businessRuleDetail);
//					logRuleStatistics.setCreatetime(d);
//					logRuleStatistics.setNum(count);
//					logRuleStatistics.setSdate(DateUtil.getDate(d));
//					logRuleStatistics.setState(LogRuleStatisticsState.normal.getCode());
//					logRuleStatistics.setUpdatetime(logRuleStatistics.getCreatetime());
//					logRuleStatistics.setUpdateuserid(logRuleStatistics.getCreateuserid());
//					state=logRuleStatisticsService.saveObject(logRuleStatistics);
				}
			}
			if(!dataObject.has("interfaces")){
				return state;
			}
			JSONArray interfacesArray=dataObject.getJSONArray("interfaces");
			long totalmatchnum=0;
			long totalnum=0;
			float totaltraffic=0;
			BusinessChannel businessChannel=(BusinessChannel) businessChannelService.findById(businessChannelId);
			for(int i=0;i<interfacesArray.length();i++){
				JSONObject object=interfacesArray.getJSONObject(i);
				String if_name=object.getString("if_name");
				String timestamp=object.getString("timestamp");
				String match_rule_packet_count=object.getString("match_rule_packet_count");
				String total_packet_count=object.getString("total_packet_count");
				String traffic=object.getString("traffic");
				try{
					totalnum=totalnum+Long.valueOf(total_packet_count);
				}catch (Exception e) {
					e.printStackTrace();
				}
				try{
					totalmatchnum=totalmatchnum+Long.valueOf(match_rule_packet_count);
				}catch (Exception e) {
					e.printStackTrace();
				}
				totaltraffic=totaltraffic+Float.parseFloat(traffic);
				LogSnifferStatistics logSnifferStatistics=new LogSnifferStatistics();
				logSnifferStatistics.setBusinessChannel(businessChannel);
				logSnifferStatistics.setCreatetime(d);
				logSnifferStatistics.setMatchrulenum(Long.valueOf(match_rule_packet_count));
				logSnifferStatistics.setReporttime(DateUtil.stringToDate(timestamp));
				logSnifferStatistics.setSdate(DateUtil.getDate(DateUtil.stringToDate(timestamp)));
				logSnifferStatistics.setSniffername(if_name);
				logSnifferStatistics.setState(LogSnifferStatisticsState.normal.getCode());
				logSnifferStatistics.setTotalnum(totalnum);
				logSnifferStatistics.setTraffic(Float.valueOf(traffic));
				logSnifferStatistics.setUpdatetime(logSnifferStatistics.getCreatetime());
				state=logSnifferStatisticsService.saveObject(logSnifferStatistics);
			}
			businessChannel=(BusinessChannel) businessChannelService.findById(businessChannelId);
			businessChannel.setTraffic(totaltraffic);
			businessChannel.setTotalnum(totalnum);
			businessChannel.setMatchrulenum(totalmatchnum);
			businessChannel.setUpdatetime(d);
			businessChannel.setOnlinestate(BusinessChannelOnlinestate.online.getCode());
			state=businessChannelService.updateObject(businessChannel,true);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

}
