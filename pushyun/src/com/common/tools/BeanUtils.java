package com.common.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 工具类，通过反射机制对对象赋值
 * @author 
 */
public class BeanUtils {
	protected static final Log logger = LogFactory.getLog(BeanUtils.class);

	private BeanUtils() {
	}
	
	/**
	 * 获取对象的DeclaredField.
	 * 
	 * @throws NoSuchFieldException
	 *             如果没有该Field时抛出.
	 */
	public static Field getDeclaredField(Object object, String propertyName)
			throws NoSuchFieldException {
		//Field 提供有关类或接口的单个字段的信息，以及对它的动态访问权限。反射的字段可能是一个类（静态）字段或实例字段。
		return getDeclaredField(object.getClass(), propertyName);
	}

	/**
	 * 获取对象的DeclaredField.
	 * 
	 * @throws NoSuchFieldException
	 *             如果没有该Field时抛出.
	 */
	public static Field getDeclaredField(Class clazz, String propertyName)
			throws NoSuchFieldException {
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(propertyName);
			} catch (NoSuchFieldException e) {

			}
		}
		throw new NoSuchFieldException("No such field: " + clazz.getName()
				+ '.' + propertyName);
	}

	/**
	 * 强制获取对象变量值
	 * 
	 * @param object
	 * @param propertyName
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static Object forceGetProperty(Object object, String propertyName)
			throws NoSuchFieldException {

		Field field = getDeclaredField(object, propertyName);

		boolean accessible = field.isAccessible();
		field.setAccessible(true);

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			logger.info("error wont' happen");
		}
		field.setAccessible(accessible);
		return result;
	}

	/**
	 * 强制设置对象变量值
	 * 
	 * @param object
	 * @param propertyName
	 * @param newValue
	 * @throws NoSuchFieldException
	 */
	public static void forceSetProperty(Object object, String propertyName,
			Object newValue) throws NoSuchFieldException {
		Field field = getDeclaredField(object, propertyName);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(object, newValue);
		} catch (IllegalAccessException e) {
		
		}
		field.setAccessible(accessible);
	}

	/**
	 * 按Field类型取得Field列表
	 * 
	 * @param object
	 * @param type
	 * @return
	 */
	public static List<Field> getFieldsByType(Object object, Class type) {
		List<Field> list = new ArrayList<Field>();
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getType().isAssignableFrom(type)) {
				list.add(field);
			}
		}
		return list;
	}

	/**
	 * 按FiledName获得Field的类型.
	 */
	public static Class getPropertyType(Class type, String name)
			throws NoSuchFieldException {
		return getDeclaredField(type, name).getType();
	}
	
    // 取出Mongo中的属性值，为bean赋值
    public static <T> void setProperty(T bean, String varName, T object) {
        varName = varName.substring(0, 1).toUpperCase() + varName.substring(1);
        try {
            String type = object.getClass().getName();
            // 类型为String
            if (type.equals("java.lang.String")) {
                Method m = bean.getClass().getMethod("get" + varName);
                String value = (String) m.invoke(bean);
                if (value == null) {
                    m = bean.getClass()
                            .getMethod("set" + varName, String.class);
                    m.invoke(bean, object);
                }
            }
            // 类型为Integer
            if (type.equals("java.lang.Integer")) {
                Method m = bean.getClass().getMethod("get" + varName);
                String value = (String) m.invoke(bean);
                if (value == null) {
                    m = bean.getClass().getMethod("set" + varName,
                            Integer.class);
                    m.invoke(bean, object);
                }
            }
            // 类型为Boolean
            if (type.equals("java.lang.Boolean")) {
                Method m = bean.getClass().getMethod("get" + varName);
                String value = (String) m.invoke(bean);
                if (value == null) {
                    m = bean.getClass().getMethod("set" + varName,
                            Boolean.class);
                    m.invoke(bean, object);
                }
            }
            if (type.equals("java.util.Date")) {
                Method m = bean.getClass().getMethod("get" + varName);
                String value = (String) m.invoke(bean);
                if (value == null) {
                    m = bean.getClass().getMethod("set" + varName,
                            Date.class);
                    m.invoke(bean, object);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
