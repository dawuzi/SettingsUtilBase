package bgn.settings.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import bgn.exception.UtilException;

public class SUBUtils {
	public static Object getProperty(Class<?> clazz, Object object, String fieldName){
		try {
			Method method = new PropertyDescriptor(fieldName, clazz).getReadMethod();
			return method.invoke(object);
		} catch (Exception e) {
    		throw new UtilException("cannot find getter for field -"+fieldName+"- for class "+clazz.getCanonicalName(), e);
		}
	}
	
	public static void setProperty(Class<?> clazz, Object object, String fieldName, Object value){
		try {
			Method method = new PropertyDescriptor(fieldName, clazz).getWriteMethod();
			method.invoke(object, value);
		} catch (Exception e) {
    		throw new UtilException("cannot find setter for field -"+fieldName+"- for class "+clazz.getCanonicalName(), e);
		} 
	}
}
