package bgn.settings.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bgn.exception.UtilException;

public class SUBUtils {
	
	private final static Logger log = LoggerFactory.getLogger(SUBUtils.class);
    static final String GET = "get";
    static final String IS = "is";
	
	public static Object getProperty(Class<?> clazz, Object object, String fieldName){
		try {
//			the getter method was obtained manually to accommodate for the settings enum which ideally should have no setters for its field
//			since it values ought to be constants
			Method method = getGetterMethod(clazz, fieldName); 
			return method.invoke(object);
		} catch (Exception e) {
    		throw new UtilException("cannot find getter for field -"+fieldName+"- for class "+clazz.getCanonicalName(), e);
		}
	}
	
	public static Method getGetterMethod(Class<?> clazz, String fieldName)throws NoSuchMethodException, SecurityException  {
				
		String methodSuffix = makeFirstLetterCapital(fieldName.trim()); 
		
		String isGetterMethodName = IS + methodSuffix; 
		
		Method isGetterMethod = null;
		try {
			isGetterMethod = clazz.getMethod(isGetterMethodName);
			if(isGetterMethod != null){ 
				
//				is prefixed defined getter methods are used with only boolean return types and thus the following validation
				
				if(isGetterMethod.getReturnType().equals(boolean.class) 
						||  isGetterMethod.getReturnType().equals(Boolean.class)){
					return isGetterMethod;
				}
				else{
					throw new UtilException("The getter method "+isGetterMethodName+" does not return a boolean type"); 
				}
			}
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}  
		
//		no IS prefixed defined getter. Hence return GET prefixed defined getter if it exists.
//		No try catch to allow the throwing of a NoSuchMethodException if it doesnt exists 
//		or SecurityException if that is private
		
		String getGetterMethodName = GET + methodSuffix;
		
		return clazz.getMethod(getGetterMethodName);
		
	}

	public static void setProperty(Class<?> clazz, Object object, String fieldName, Object value){
		try {
			Method method = new PropertyDescriptor(fieldName, clazz).getWriteMethod();
			method.invoke(object, value);
		} catch (Exception e) {
    		throw new UtilException("cannot find setter for field -"+fieldName+"- for class "+clazz.getCanonicalName(), e);
		} 
	}
	
    public static boolean isAnyArgumentNull(Object... objs) {
        Object obj;
        String str;

        if (objs.length == 0) {
            log.debug("no argument was passed");
            return true;
        }

        for (int x = 0; x < objs.length; x++) {
            obj = objs[x];
            if (obj == null) {
                log.debug("argument " + (x + 1) + " is null");
                return true;
            } else {
                if (obj.getClass().equals(String.class)) {
                    str = (String) obj;
                    if (str.trim().equals("")) {
                        log.debug("String argument " + (x + 1) + " is empty");
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static String makeFirstLetterCapital(String str) {
      if ((str == null) || (str.length() == 0)) {
        return str;
      }
      return str.substring(0, 1).toUpperCase(Locale.ENGLISH) + str.substring(1);
    }
}
