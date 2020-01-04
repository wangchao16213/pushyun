package com.module.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessRule;
import com.bean.BusinessRuleDetail;
import com.bean.BusinessTask;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.servlet.Base;
import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.type.BusinessChannelCmdType;
import com.common.type.BusinessChannelOnlinestate;
import com.common.type.BusinessChannelState;
import com.common.type.BusinessRuleDetailState;
import com.common.type.BusinessRuleState;
import com.common.type.BusinessTaskState;
import com.common.type.BusinessTaskType;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.module.business.service.ChannelCmdService;
import com.module.business.service.RuleService;
import com.module.data.service.ChannelLogService;
import com.spacesat.util.json.JSONArray;
import com.spacesat.util.json.JSONException;
import com.spacesat.util.json.JSONObject;

public class GetControlServlet extends Base{
	
	
	private static final long serialVersionUID = 1L;

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pointid=request.getParameter("pointid");
		String sign=request.getParameter("sign");
		String version=request.getParameter("version");
		JSONObject jsonObject=new JSONObject();
		if(StringUtils.isBlank(sign)){
			try {
				jsonObject.put("code",-1);
				jsonObject.put("msg", "sign is empty");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(null, 
					getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(),jsonObject.toString());
			this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
			return ;
		}
		sign=StrUtil.getMacBySetFormat(sign);
		BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findByProperty("sign", sign);
		if(businessChannel==null){
			businessChannel=new BusinessChannel();
			businessChannel.setCode(sign);
			businessChannel.setName(sign);
			businessChannel.setSign(sign);
			businessChannel.setCreatetime(new Date());
			businessChannel.setCreateuserid(getManageUser(request).getId());
			businessChannel.setVersion(version);
			businessChannel.setUpdateuserid(businessChannel.getCreateuserid());
			businessChannel.setUpdatetime(businessChannel.getCreatetime());
			businessChannel.setOnlinestate(BusinessChannelOnlinestate.online.getCode());
			businessChannel.setState(BusinessChannelState.normal.getCode());
			businessChannel.setRemark("GetControlServlet自动新增");
			getBaseService(BusinessChannel.class).saveObject(businessChannel);
			try {
				jsonObject.put("code",-1);
				jsonObject.put("msg", "lack of information");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(businessChannel, 
					getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(), 
					jsonObject.toString());
			this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
			return ;
		}else{
			if(businessChannel.getOnlinestate().equals(BusinessChannelOnlinestate.offline.getCode())){
				businessChannel.setUpdateuserid(getManageUser(request).getId());
				businessChannel.setUpdatetime(new Date());
				businessChannel.setOnlinestate(BusinessChannelOnlinestate.online.getCode());
				getBaseService(BusinessChannel.class).updateObject(businessChannel,true);
			}else{
				double iResult=DateUtil.minuSecond(businessChannel.getUpdatetime(), new Date());
				if(iResult>=1800){
					businessChannel.setUpdateuserid(getManageUser(request).getId());
					businessChannel.setUpdatetime(new Date());
					businessChannel.setOnlinestate(BusinessChannelOnlinestate.online.getCode());
					getBaseService(BusinessChannel.class).updateObject(businessChannel,true);
				}
			}
		}
		String result="";
		if(StringUtils.isNotBlank(businessChannel.getFileaddress())){
			String rootPath = "build";
			String buildPath = this.getServletContext().getRealPath(rootPath);// 生成路径
			String filePath=buildPath+businessChannel.getFileaddress().replaceAll(rootPath, "");
			File pathFile = new File(filePath);
    		if (!pathFile.exists()) {
    			saveTask(businessChannel);
    		}else{
    			result=StrUtil.readTxtFile(filePath);
    		}
		}else{
			saveTask(businessChannel);
		}
		if(StringUtils.isBlank(result)){
			try {
				jsonObject.put("code",0);
				jsonObject.put("msg", "rule is empty");
				JSONArray dataArray=new JSONArray();
				jsonObject.put("data", dataArray);
				jsonObject.put("num", 0);
				String cmd=((ChannelCmdService)getService(ChannelCmdService.class)).getChannelCmd(businessChannel,BusinessChannelCmdType.other.getCode());
				if(StringUtils.isNotBlank(cmd)){
					jsonObject.put("cmd", cmd);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result=jsonObject.toString();
		}
		((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(businessChannel, 
				getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(), "");
		this.sendWeb(result, "application/json;charset=utf-8", request, response);
		return ;
	}
	
	private void saveTask(BusinessChannel businessChannel){
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("type", BusinessTaskType.api.getCode()));
		expList.add(Restrictions.or(Restrictions.eq("state", BusinessTaskState.wait.getCode()), 
				Restrictions.eq("state", BusinessTaskState.run.getCode())));
		expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
		List<BaseRecord> taskList=getBaseService(BusinessTask.class).findList(expList, orders, 0, 9999, null);
		if(taskList==null
				||taskList.size()==0){
			BusinessTask businessTask=new BusinessTask();
			businessTask.setBusinessChannel(businessChannel);
			businessTask.setCreatetime(new Date());
			businessTask.setUpdateuserid(businessTask.getCreateuserid());
			businessTask.setName(BusinessTaskType.api.getDisplay());
			businessTask.setState(BusinessTaskState.wait.getCode());
			businessTask.setTasktime(new Date());
			businessTask.setType(BusinessTaskType.api.getCode());
			businessTask.setUpdatetime(new Date());
			getBaseService(BusinessTask.class).saveObject(businessTask);
		}
	}
	


}
