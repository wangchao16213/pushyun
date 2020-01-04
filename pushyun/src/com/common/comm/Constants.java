package com.common.comm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.tools.PackageUtil;
import com.common.type.EnumMessage;

/**
 * 
 * <br/>使用的常量<br/>
 * @author 
 * @see 
 */
public class Constants {
	public static final String VSERION = "0.1";

	public static final String CHARSET = "UTF-8";

	public static final boolean CACHE_KEY = true;
	
	/**
	 * 系统错误提示
	 */
	public static final String ERROR = "严重错误";

	// 程序处理状态
	/**
	 * 0表示出现错误,并且是不可逆转的
	 */
	public static final int STATE_OPERATOR_LOST = 0;

	/**
	 * 1表示成功返回
	 */
	public static final int STATE_OPERATOR_SUCC = 1;

	/**
	 * 2表示等待返回
	 */
	public static final int STATE_OPERATOR_WAIT = 2;

	/**
	 * 2表示程序等待返回
	 */
	public static final String STATE_PROGRAM_WAIT = "2";

	/**
	 * 0表示程序错误
	 */
	public static final String STATE_PROGRAM_ERROR = "0";

	/**
	 * 1表示程序正确
	 */
	public static final String STATE_PROGRAM_SUCC = "1";

	/**
	 * 3表示程序等待也许的情况
	 */
	public static final String STATE_PROGRAM_MAYBE = "3";

	public static final String TERMINAL_MODEM = "modem";

	public static final String SMS_TYPE_EncUcs2 = "EncUcs2";

	public static final String SMS_TYPE_Enc7Bit = "Enc7Bit";

	public static final String SMS_TYPE_Enc8Bit = "Enc8Bit";
	
	public static final String LOGIN_VALIDATE_CODE = "loginvalidatecode";

	public static final String SESSION_ADMIN_CODE = "sessionadmincode";

	public static final String SESSION_USER_CODE = "sessionusercode";
	
	public static final String SESSION_USER_URL = "sessionuserurl";

	public static final String OERATION_NONE = "NONE";

	public static final String OERATION_CREATE = "CREATE";

	public static final String OERATION_UPDATE = "UPDATE";
	
	public static final String OERATION_ALL_UPDATE = "ALLUPDATE";

	public static final String OERATION_DELETE = "DELETE";

	public static final String OERATION_SEARCH = "SEARCH";
	
	public static final String OERATION_PASSWORD= "password";
	
	public static final String OERATION_OTHER= "OTHER";
	
	public static final String OERATION_EXP= "EXP";
	
	public static final String OERATION_IMP= "IMP";
	
	public static final String OERATION_IMP_ERROR= "IMPERROR";

	
	public static final String OERATION_IMP_SERVER= "IMPSERVER";
	
	public static final String OERATION_COPY= "COPY";
	
	
	public static final String TASK_JMS_PUSHCOUNT_QUEUE_NAME = "TASK_JMS_PUSHCOUNT_QUEUE_NAME";
	
	
	 /**
	     * 枚举类对应的包路径
	       */
	 public final static String PACKAGE_NAME = "com.common.type";
	 /**
	   * 枚举接口类全路径
	*/
	public final static String ENUM_MESSAGE_PATH=PACKAGE_NAME+".EnumMessage";

	/**
	  * 枚举类对应的全路径集合
	*/
	public static final List<String> ENUM_OBJECT_PATH = PackageUtil.getPackageClasses(PACKAGE_NAME, true);
 
    /**
	  * 存放单个枚举对象 map常量定义
	 */
	private static Map<String, EnumMessage> SINGLE_ENUM_MAP = null;
	/**
	* 所有枚举对象的 map
	*/
	public static final Map<Class, Map<String, EnumMessage>> ENUM_MAP = initialEnumMap(true);
	
    /**静态初始化块*/
    static {

    }

    /**
     * 加载所有枚举对象数据
     * @param  isFouceCheck 是否强制校验枚举是否实现了EnumMessage接口
     *
     * */
    private static Map<Class, Map<String, EnumMessage>> initialEnumMap(boolean isFouceCheck){
        Map<Class, Map<String, EnumMessage>> ENUM_MAP = new HashMap<Class, Map<String, EnumMessage>>();
        try {
            for (String classname : ENUM_OBJECT_PATH) {
                Class<?> cls = null;
                cls = Class.forName(classname);
                Class <?>[]iter=cls.getInterfaces();
                boolean flag=false;
                if(isFouceCheck){
                    for(Class cz:iter){
                        if(cz.getName().equals(ENUM_MESSAGE_PATH)){
                            flag=true;
                            break;
                        }
                    }
                }
                if(flag==isFouceCheck){
                     SINGLE_ENUM_MAP = new HashMap<String, EnumMessage>();
                    initialSingleEnumMap(cls);
                    ENUM_MAP.put(cls, SINGLE_ENUM_MAP);
                }

            }
        } catch (Exception e) {
           
        }
        return ENUM_MAP;
    }

    /**
     * 加载每个枚举对象数据
     * */
    private static void  initialSingleEnumMap(Class<?> cls )throws Exception{
        Method method = cls.getMethod("values");
        EnumMessage inter[] = (EnumMessage[]) method.invoke(null, null);
        for (EnumMessage enumMessage : inter) {
            SINGLE_ENUM_MAP.put(enumMessage.getCode(), enumMessage);
        }
    }

}
