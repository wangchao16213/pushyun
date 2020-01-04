package com.common.tools;

import java.util.Map;

import com.common.comm.Constants;
import com.common.type.EnumMessage;





public class EnumUtil {

    /**
     * 获取value返回枚举对象
     * @param value
     * @param clazz
     * */
    public static <T extends  EnumMessage>  T getEnumObject(String code,Class<T> clazz){
        return (T)Constants.ENUM_MAP.get(clazz).get(code);
    }
    
    public static <T extends  EnumMessage>  Map<String, EnumMessage> getEnumValues(Class<T> clazz){
        return (Map<String, EnumMessage>)Constants.ENUM_MAP.get(clazz);
    }
    
    
    public static void main(String[] args) {
//        System.out.println(EnumUtil.getEnumObject("00", TAllUrlMIsad.class).getDisplay());
//        Map<String, EnumMessage> map= EnumUtil.getEnumValues(TAllUrlMIsad.class);
//        for(String s:map.keySet()){
//        	 System.out.println(map.get(s).getDisplay());
//        }
    }
    
    
}
