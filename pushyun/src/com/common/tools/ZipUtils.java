package com.common.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.UUID;

import org.apache.tools.zip.*;

public class ZipUtils {
    @SuppressWarnings("unchecked")
	public static void unZip(String zipfile, String destDir) {
        byte b[] = new byte [1024];
        int length;
  
        ZipFile zipFile;
        try {
            zipFile = new ZipFile( new File(zipfile));
            Enumeration enumeration = zipFile.getEntries();
            ZipEntry zipEntry = null ;
  
            while (enumeration.hasMoreElements()) {
               zipEntry = (ZipEntry) enumeration.nextElement();
               File loadFile = new File(destDir +File.separator+zipEntry.getName());
               try{
                   if (zipEntry.isDirectory()) {
                       // 这段都可以不要，因为每次都貌似从最底层开始遍历的
                	   zipEntry.setUnixMode(755);//解决linux乱码 
                       loadFile.mkdirs();
                   } else {
                	   zipEntry.setUnixMode(644);//解决linux乱码  
                       if (!loadFile.getParentFile().exists()){
                    	   loadFile.getParentFile().mkdirs();
                       }
                       OutputStream outputStream = new FileOutputStream(loadFile);
                     //  outputStream .setEncoding("GBK");//解决linux乱码  
                       InputStream inputStream = zipFile.getInputStream(zipEntry);
      
                       while ((length = inputStream.read(b)) > 0){
                    	   outputStream.write(b, 0, length);
                       }
                       outputStream.close();
                       inputStream.close();
                   }
               }catch (Exception e) {
				// TODO: handle exception
               }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
     }
    
    public static String zip(String destPath, String inputFileName, String zipName){
        String zipFileName = zipName+"_"+UUID.randomUUID()+".zip";
        String realPath = StrUtil.getStringWithFileSeparator(destPath + File.separator+ zipFileName);
        startZip(realPath, new File(StrUtil.getStringWithFileSeparator(destPath + File.separator + inputFileName)));
        return zipFileName;
    }
    
    
    public static String zip(String destPath, String[] inputFileName, String zipName){
    	 String zipFileName = zipName+"_"+UUID.randomUUID()+".zip";
    	 String realPath = StrUtil.getStringWithFileSeparator(destPath + File.separator+ zipFileName);
    	 startZip(realPath, destPath,inputFileName);
    	 return zipFileName;
    }
    
    private static void startZip(String zipFileName,String destPath,String[] inputFileName) {
    	 ZipOutputStream out = null;
         FileInputStream in=null;
         try{
         	out=new ZipOutputStream(new FileOutputStream(zipFileName));
         	out.setEncoding("GBK");
         	for(String fileName:inputFileName){
         		File f=	new File(StrUtil.getStringWithFileSeparator(destPath + File.separator+ fileName));
             	if (!f.isFile()) {
             		continue;
             	}
             	out.putNextEntry(new ZipEntry(f.getName()));
             	in = new FileInputStream(f);
             	int b;
             	while ((b = in.read()) != -1) {
             		out.write(b);
             	}
             	f.delete();
         	}
         }catch (Exception e) {
 			
 		}finally{
 			if(out!=null){
 				try {
 					out.close();
 				} catch (IOException e) {
 					e.printStackTrace();
 				}
 			}
 			if(in!=null){
 				try {
 					in.close();
 				} catch (IOException e) {
 					e.printStackTrace();
 				}
 			}
 		}  
    }
   
    private static void startZip(String zipFileName,File inputFile) {
        ZipOutputStream out = null;
        FileInputStream in=null;
        try{
        	out=new ZipOutputStream(new FileOutputStream(zipFileName));
        	out.setEncoding("GBK");
        	if (inputFile.isDirectory()) {
            	File [] files = inputFile.listFiles();
            	try {
    				out.putNextEntry(new ZipEntry(zipFileName+"/"));
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
            	for (File file : files) {
            		if (!file.isFile()) {
            			continue;
            		}
            		out.putNextEntry(new ZipEntry(zipFileName+file.getName()));
            		in = new FileInputStream(file);
            		int b;
            		while ((b = in.read()) != -1) {
            			out.write(b);
            		}
            		file.delete();
            	}
            } else {
            	out.putNextEntry(new ZipEntry(inputFile.getName()));
            	in = new FileInputStream(inputFile);
            	int b;
        		while ((b = in.read()) != -1) {
        			out.write(b);
        		}
        		inputFile.delete();
            }
        }catch (Exception e) {
			
		}finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}  
    }
   

}
