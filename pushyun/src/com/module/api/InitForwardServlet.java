package com.module.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.bean.BusinessPushserver;
import com.common.servlet.Base;
import com.common.tools.StrUtil;
import com.spacesat.util.json.JSONException;
import com.spacesat.util.json.JSONObject;

public class InitForwardServlet extends Base{

	
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
				jsonObject.put("msg", "lack of information");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
			return ;
		}
		sign=StrUtil.getMacBySetFormat(sign);
		BusinessPushserver businessPushserver=(BusinessPushserver) getBaseService(BusinessPushserver.class).findByProperty("sign", sign);
		if(businessPushserver==null){
			try {
				jsonObject.put("code",-1);
				jsonObject.put("msg", "lack of information");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
			return ;
		}
		try {
			jsonObject.put("code",0);
			jsonObject.put("msg", "succ");
			JSONObject dataObject=new JSONObject();
			dataObject.put("routermac", businessPushserver.getRoutermac());
			dataObject.put("server", businessPushserver.getServeraddress());
			JSONObject if_ether_senderObject=new JSONObject();
			if_ether_senderObject.put("mac", businessPushserver.getSendermac());
			if_ether_senderObject.put("name", businessPushserver.getSendername());
			dataObject.put("if_ether_sender", if_ether_senderObject);
			jsonObject.put("data", dataObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
		return ;
		
	}

}
