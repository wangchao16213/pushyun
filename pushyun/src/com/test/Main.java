package com.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.tools.ZipUtils;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> fileList =new ArrayList<String>();
		String buildPath="D:\\project\\myproject\\.metadata\\.plugins\\com.genuitec.eclipse.easie.tomcat.myeclipse\\tomcat\\webapps\\pushyun\\build";
		
		StrUtil.getFiles(buildPath+File.separator+"d776266a-c644-4eff-87db-2433225ad771", fileList);
		String[] files=new String[fileList.size()];
		for(int i=0;i<fileList.size();i++){
			File f=new File(fileList.get(i));
			files[i]="d776266a-c644-4eff-87db-2433225ad771"+File.separator+f.getName();
			System.out.println(f.getName());
		}
		String zipName=ZipUtils.zip(buildPath, files, DateUtil.getDate(new Date()));

	}

}
