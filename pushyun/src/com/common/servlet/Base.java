package com.common.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bean.ManageUser;
import com.common.comm.Constants;
import com.common.comm.UserSession;
import com.common.config.SpringConfig;
import com.common.service.BaseService;


public abstract class Base extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	private String logonjsp="index.jsp";
	
	private final Logger logger = Logger.getLogger(Base.class.getName());
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		getMethod(request,response);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");  
		response.setCharacterEncoding("utf-8");  
//		String contextRoot = request.getContextPath();
		getMethod(request,response);
	}
	

	
	
	private void getMethod(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		String action = request.getParameter("action");
		Method method=null;
		try {
			if(StringUtils.isBlank(action)){
				action="run";//默认执行方法
			}
			if(action.equalsIgnoreCase("init")){
				action="run";
			}
			method = this.getClass().getDeclaredMethod(action,HttpServletRequest.class,HttpServletResponse.class);
		}  catch (Exception e) {
			logger.error("", e);
		}
		try {
			method.invoke(this,new Object[]{request,response});
		}  catch (Exception e) {
			logger.error(action+"|"+this.getClass().getName(), e);
		}
	}
	

	protected ManageUser getManageUser(HttpServletRequest request){
		HttpSession session =request.getSession();
		Object object=session.getAttribute(Constants.SESSION_USER_CODE);
		if(object ==null){
			ManageUser manageUser=(ManageUser) getBaseService(ManageUser.class).findByProperty("username", "admin");
			return manageUser;
		}
		return ((UserSession)object).getUsers();
	}
	
	
	@SuppressWarnings("unchecked")
	protected void saveLog(HttpServletRequest request, HttpServletResponse response){

	}
	
	
	@SuppressWarnings("unchecked")
	protected String getParameter(HttpServletRequest request){
		StringBuffer sb=new StringBuffer();
		Enumeration enumeration=request.getParameterNames();   
        while(enumeration.hasMoreElements()){   
              String   paramName=(String)enumeration.nextElement();                       
              String[] values=request.getParameterValues(paramName);   
              for(int i=0;i<values.length;i++){   
                   sb.append(paramName);
                   sb.append("=");
                   sb.append(values[i]);
                   sb.append("&");
             }  
        }
        return sb.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	protected BaseService getBaseService(Class beanClass){
		return (BaseService) SpringConfig.getInstance().getService(beanClass);
	}
	
	
	@SuppressWarnings("unchecked")
	protected Object getService(Class beanClass){
		return  SpringConfig.getInstance().getService(beanClass);
	}
	
	protected String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}
	
	protected void createFile(String content,String fullpath){
		FileOutputStream fop = null;  
		File file;  
	    try {  
	            file = new File(fullpath);  
	            fop = new FileOutputStream(file);  
	            if (!file.exists()) {  
	                file.createNewFile();  
	            }  
	            byte[] contentInBytes = content.getBytes();  
	            fop.write(contentInBytes);  
	            fop.flush(); 
	     } catch (IOException e) {  
	            e.printStackTrace();
	     } finally {  
	            try {  
	                if (fop != null) {  
	                    fop.close();  
	                }  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	     }  
	}
	
	
	public abstract void run(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException;
	
	protected void sendWeb(Object o, String contentType, HttpServletRequest request,HttpServletResponse response) {
		PrintWriter out = null;
		try {
			response.setContentType(contentType);
			out = response.getWriter();
			out.print(o);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
