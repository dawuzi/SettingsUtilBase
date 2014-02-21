package bgn.settings;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bgn.exception.UtilException;


/**
* This class eases the implementation of settings util in projects 
* by presenting a generic settings util class that can be extended.
* <br>
* Extend {@link GridSettingsUtilBase} for grid settings based scenarios
* <br>
* Extend {@link SimpleSettingsUtilBase} for simple settings based scenarios
* 
* @author Okafor Ezewuzie Dawuzi
* @version 1.0
* @since 2013
* 
*/


public abstract class SettingsUtilBase<T> {

    @SuppressWarnings("rawtypes")
	private final Map<List,T> settingsCache = new HashMap<List, T>()  ;
    private final Logger log = LoggerFactory.getLogger(SettingsUtilBase.class);
    private final boolean cacheSetting ;
    private Class<T> clazz;
    private String[] orderedGridUniqueSettingsFields ;
    private final boolean showOnlyErrorLogs ;
    
    private final boolean ignoreDescription;
    
    private final String NAME;
    private final String VALUE;
    private final String DESCRIPTION;
    
    private final String TRUE;
    private final String FALSE;
    
    protected abstract T getSettingByName(String settingName, Object... orderedGridUniqueSettingsValues) throws Exception;
    protected abstract boolean createSetting(T setting) throws Exception;
    protected abstract boolean updateSetting(T setting) throws Exception;
    
    public SettingsUtilBase(boolean cacheSetting, Class<T> clazz, String... orderedGridUniqueSettingsFields){
    	this.cacheSetting = cacheSetting;
    	this.clazz = clazz;
    	this.orderedGridUniqueSettingsFields = orderedGridUniqueSettingsFields;
    	this.showOnlyErrorLogs = showOnlyErrorLogs();
    	
    	NAME = getName();
    	VALUE = getValue();
    	DESCRIPTION = getDescription();
    	
    	validateTrueAndFalseValues();
    	
    	TRUE = getTrueValue().trim().toUpperCase();
    	FALSE = getFalseValue().trim().toUpperCase();
    	
    	ignoreDescription = ignoreDescription();
    	
    	validateSettingObjectType();
    }

    private void validateTrueAndFalseValues() {
		String trueVal = getTrueValue();
		String falseVal = getFalseValue();
		
		if(trueVal == null || trueVal.trim().equals("")){
			throw new IllegalStateException("the returned value from the overridden getTrueValue() method is null or empty. Value found : "+trueVal);
		}
		if(falseVal == null || falseVal.trim().equals("")){
			throw new IllegalStateException("the returned value from the overridden getFalseValue() method is null or empty. Value found : "+falseVal);
		}
		if(trueVal.trim().equalsIgnoreCase(falseVal.trim())){
			throw new IllegalStateException("the returned values from the overridden getFalseValue() and getTrueValue() methods are both equal (ignoring case) to "+trueVal);
		}
	}
	/**
     * Override this method if you want to see only error messages 
     * 
     * @return a boolean
     */
    protected boolean showOnlyErrorLogs() {
		// TODO Auto-generated method stub
		return true;
	}
	private void validateSettingObjectType() {
    	
		if(clazz == null){
			throw new IllegalArgumentException("clazz argument is null");
		}
		
		T obj;

    	try {
    		obj = clazz.newInstance();
    	} catch (Exception e) {
    		throw new UtilException("Cannot create instance of "
    				+clazz.getCanonicalName()
    				+", Please ensure the Setting class has a no argument contructor", e);
    	}

    	verifyField(obj,NAME);
    	verifyField(obj,VALUE);
    	
    	if(!ignoreDescription){
    		verifyField(obj,DESCRIPTION);
    	}
    	List<String> verifiedOrderedGridUniqueSettingsFields = new ArrayList<String>();
    	
        if(orderedGridUniqueSettingsFields != null && orderedGridUniqueSettingsFields.length > 0){
        	for(String anOrderedGridUniqueSettingsField : orderedGridUniqueSettingsFields){
        		
        		
        		if(anOrderedGridUniqueSettingsField == null || anOrderedGridUniqueSettingsField.trim().equals("")){
        			throw new IllegalArgumentException("none of the orderedGridUniqueSettingsFields can" +
        					" be null or empty");
        		}
        		
        		if(verifiedOrderedGridUniqueSettingsFields.contains(anOrderedGridUniqueSettingsField.trim())){
        			throw new IllegalArgumentException("Multiple orderedGridUniqueSettingsFields : "+anOrderedGridUniqueSettingsField.trim());
        		}
        		
        		verifyField(obj,anOrderedGridUniqueSettingsField);
        		
        		verifiedOrderedGridUniqueSettingsFields.add(anOrderedGridUniqueSettingsField.trim());
        	}
        	
        }
    }

    protected String getDescription() {
		return "description";
	}
    protected String getValue() {
		return "value";
	}
    protected String getName() {
		return "name";
	}
	
    private void verifyField(T obj, String field) {
		getProperty(obj, field);
		setProperty(obj, field, null);
	}
	
	private Object getProperty(Object setting, String fieldName){
		try {
			Method method = new PropertyDescriptor(fieldName, clazz).getReadMethod();
			return method.invoke(setting);
		} catch (Exception e) {
			// TODO Auto-generated catch block
    		throw new UtilException("cannot find getter for field -"+fieldName+"- for class "+clazz.getCanonicalName(), e);
		}
	}
	private void setProperty(Object setting, String fieldName, Object value){
		try {
			Method method = new PropertyDescriptor(fieldName, clazz).getWriteMethod();
			method.invoke(setting, value);
		} catch (Exception e) {
    		throw new UtilException("cannot find setter for field -"+fieldName+"- for class "+clazz.getCanonicalName(), e);
		} 
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List getList(Object settingName, Object... objs) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException{
		List list = new ArrayList();
		
		list.add(BeanUtils.cloneBean(settingName));
		
		for(Object o : objs){
			if(o != null){
				list.add(BeanUtils.cloneBean(o));
			}
			else{
				list.add(o);
			}
		}
		
		return list;
	}
	
	protected T getSettingByName(String settingName, String defaultValue, 
			String defaultDescription, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues){
        
        if(settingName.trim().equals("")){
            throw new IllegalArgumentException("setting name is empty");
        }
        
        if(orderedGridUniqueSettingsValues != null && orderedGridUniqueSettingsValues.length > 0){
        	
        	if(orderedGridUniqueSettingsFields == null){
                throw new IllegalArgumentException("you cannot pass in an orderedGridUniqueSettingsValues" +
                		" when there are no orderedGridUniqueSettingsFields in the constructor." +
                		" Pass in the corresponding strings of orderedGridUniqueSettingsFields" +
                		" in the constuctor.");
        	}
        	
        	if(orderedGridUniqueSettingsFields.length != orderedGridUniqueSettingsValues.length){
        		throw new IllegalArgumentException("The orderedGridUniqueSettingsFields and " +
        				"orderedGridUniqueSettingsValues dont match. They need to match in both " +
        				"order and length. Null values can be used for fields you wish to be null");
        	}
        }
        
        try{
        
	        settingName = settingName.trim();
	
	        T setting = null;
	        
	        if(cacheSetting){
		        setting = settingsCache.get(getList(settingName, orderedGridUniqueSettingsValues));
		        
		        if(setting != null){
	            	if(!showOnlyErrorLogs){
			            log.debug("returning setting with name -"
			                    +settingName+"- from settingCache");
	            	}
		            return setting;
		        }
	        }
	        
	        try {
	            setting = getSettingByName(settingName,orderedGridUniqueSettingsValues);
	        } catch (Exception ex) {
	            log.error("Exception", ex);
	        }
	
	        if (setting != null) {
	            
	        	if(cacheSetting){
	        	
		            Object prevSetting = settingsCache.put(getList(settingName, orderedGridUniqueSettingsValues), setting);
		            
		            if(prevSetting != null){
		            	if(!showOnlyErrorLogs){
			                log.warn("A previous setting with name -"
			                        +settingName+"- was removed from the settingCache");
		                }
		            }else{
		            	if(!showOnlyErrorLogs){
		            		log.debug("Adding an already existing setting with name -"
		            				+settingName+"- to settingCache, value = -"+getProperty(setting,VALUE)+"-");
		            	}
		            }
	        	}
	            return setting;
	            
	        } 
	        else if (createIfNotExist) {
	        	setting = clazz.newInstance();
	            
	            setProperty(setting, NAME, settingName);
	            
	            if (defaultValue != null) {
	                setProperty(setting, VALUE, defaultValue);
	            } else {
	                setProperty(setting, VALUE, "");
	            }
	
	            if(!ignoreDescription){
		            if (defaultDescription != null) {
		                setProperty(setting, DESCRIPTION, defaultDescription);
		            } else {
		                setProperty(setting, DESCRIPTION, "");
		            }
	            }
	            
	            if(orderedGridUniqueSettingsValues != null 
	            		&& orderedGridUniqueSettingsValues.length > 0){
	            	for(int x = 0; x < orderedGridUniqueSettingsValues.length; x++){
	            		setProperty(setting, orderedGridUniqueSettingsFields[x], orderedGridUniqueSettingsValues[x]);
	            	}
	            }
	
	            createSetting(setting);
	            
	            Object prevSetting = settingsCache.put(getList(settingName, orderedGridUniqueSettingsValues), setting);
	            
	            if(prevSetting != null){
	            	if(!showOnlyErrorLogs){
		                log.warn("A previous setting with name -"
		                        +settingName+"- was removed from the settingCache");
	            	}
	            }else{
	            	if(!showOnlyErrorLogs){
		                log.debug("Adding a new setting with name -"
		                        +settingName+"- to settingCache, value = -"+getProperty(setting,VALUE)+"-");
	            	}
	            }
	
	            return setting;
	        }
	        
        	if(!showOnlyErrorLogs){
		        log.debug("setting with name -"
		                +settingName
		                +"- does not exist in DB and it is not to be created if it does not exist");
	        }
	        
	        return null;
        }
        catch(Exception e){
        	throw new UtilException("error getting setting by name",e);
        }
    }
    
	protected String getSettingValue(String settingName, String defaultValue, 
    		String defaultDescription, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {

    	try{
	        T setting = getSettingByName(settingName, defaultValue, defaultDescription, createIfNotExist, orderedGridUniqueSettingsValues);
	
	        if (setting != null) {
	        	
	        	String value = (String) getProperty(setting, VALUE);
	        	
	            if (value != null && !value.trim().equals("")) {
	                return value;
	            } else {
	                if (defaultValue != null) {
	                	setProperty(setting, VALUE, defaultValue);
	                } else {
	                	setProperty(setting, VALUE, "");
	                }
	
	                if(!ignoreDescription){
		                if (defaultDescription != null) {
		                	setProperty(setting, DESCRIPTION, defaultDescription);
		                } else {
		                	setProperty(setting, DESCRIPTION, "");
		                }
	                }
	                
	                updateSetting(setting);
	                return value;
	            }
	        } 
	        else {
	            return defaultValue;
	        }
	        
    	}catch(Exception e){
    		throw new UtilException("Error getting setting value",e);
    	}
    }

	protected Integer getSettingIntValue(String settingName, Integer defaultValue, 
    		String defaultDescription, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {

        String defValInUse = null ;
        
        if(defaultValue != null){
            defValInUse = String.valueOf(defaultValue);
        }
        
        String val = getSettingValue(settingName, defValInUse, defaultDescription, createIfNotExist);
        if (val != null){
            try {
                return Integer.parseInt(val);
            }
            catch(NumberFormatException nfe){
                boolean update = updateNumberSetting(settingName, 
                        defValInUse, defaultDescription, createIfNotExist);
            	if(!showOnlyErrorLogs){
            		log.debug("update = "+update);
            	}
                return defaultValue;
            }
        }
        else{
            return defaultValue;
        }
    }    
    
	protected Long getSettingLongValue(String settingName, Long defaultValue, 
    		String defaultDescription, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {

        String defValInUse = null ;
        
        if(defaultValue != null){
            defValInUse = String.valueOf(defaultValue);
        }
        
        String val = getSettingValue(settingName, defValInUse, defaultDescription, createIfNotExist);
        if (val != null){
            try {
                return Long.parseLong(val);
            }
            catch(NumberFormatException nfe){
                
                boolean update = updateNumberSetting(settingName, 
                        defValInUse, defaultDescription, createIfNotExist);
            	if(!showOnlyErrorLogs){
            		log.debug("update = "+update);
            	}
                return defaultValue;
            }
        }
        else{
            return defaultValue;
        }
    }    

	protected Float getSettingFloatValue(String settingName, Float defaultValue, 
    		String defaultDescription, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {

        String defValInUse = null ;
        
        if(defaultValue != null){
            defValInUse = String.valueOf(defaultValue);
        }
        
        String val = getSettingValue(settingName, defValInUse, defaultDescription, createIfNotExist);
        if (val != null){
            try {
                return Float.parseFloat(val);
            }
            catch(NumberFormatException nfe){
                
                boolean update = updateNumberSetting(settingName, 
                        defValInUse, defaultDescription, createIfNotExist);
                
            	if(!showOnlyErrorLogs){
            		log.debug("update = "+update);
            	}
                return defaultValue;
            }
        }
        else{
            return defaultValue;
        }
    }    
    
    private boolean updateNumberSetting(String settingName, String defValInUse, 
    		String defaultDescription, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {
        
        try {

            if (!createIfNotExist) {
            	if(!showOnlyErrorLogs){
            		log.debug("createIfNotExist = false for setting name " 
            				+ settingName + " so setting will not be updated");
            	}
                return false;
            }

            T setting = getSettingByName(settingName,
                    defValInUse, defaultDescription, createIfNotExist);

            if (setting != null) {

                setProperty(setting, VALUE, defValInUse);
                
            	if(!showOnlyErrorLogs){
            		log.debug("update set value = " + getProperty(setting, VALUE));
            	}
            	
                updateSetting(setting);
            } else {
            	
            	if(!showOnlyErrorLogs){
	                log.error("settings name "
	                        + settingName
	                        + ", wasnt created and createIfNotExist is true");
                }
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Exception", e);
            return false;
        }
    }
    
    protected Boolean getSettingBooleanValue(String settingName, Boolean defaultValue, 
    		String defaultDescription, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues){

    	try{
	        String defValInUse = null ;
	        
	        if(defaultValue != null){
	        	if(defaultValue){
	        		defValInUse = TRUE;
	        	}else{
	        		defValInUse = FALSE;
	        	}
	        }
	        
	        String val = getSettingValue(settingName, defValInUse, defaultDescription, createIfNotExist);
	        
	        if (val != null){
	            
            	if(!showOnlyErrorLogs){
            		log.debug("val = -"+val+"-");
            	}
            	
	            val = val.trim().toUpperCase();
	            
	            if(val.equals(TRUE) || val.equals(FALSE)){
	            	if(!showOnlyErrorLogs){
	            		log.debug("valid boolean value");
	                }
	            	if(val.equals(TRUE)){
	            		return Boolean.TRUE;
	            	}else{
	            		return Boolean.FALSE;
	                }
	            }
	            else if (createIfNotExist) {
	//                    lets update it to either true or false

	            	if(!showOnlyErrorLogs){
	            		log.debug("updating setting to something that makes sense as a boolean value");
	            	}
	            	
	            	T setting = getSettingByName(settingName, defValInUse, defaultDescription, createIfNotExist);

            		String updatedValue;
	            	
	            	if (setting != null) {
	            		
	                    if (defaultValue != null) {
	                        setProperty(setting, VALUE, defValInUse);
	                        updatedValue = defValInUse;
	                    } else {
	                        setProperty(setting, VALUE, FALSE);
	                        updatedValue = FALSE;
	                    }
	                    
		            	if(!showOnlyErrorLogs){
		            		log.debug("update set value = "+getProperty(setting, VALUE));
		            	}
		            	
	                    updateSetting(setting);
	                } else {
	                    log.error("settings name "
	                            + settingName
	                            + ", wasnt created and createIfNotExist is true");
	                    
	                    return defaultValue;
	                }
	
	            	if(updatedValue.equalsIgnoreCase(TRUE)){
	            		return Boolean.TRUE;
	            	}
	            	else{
	            		return Boolean.FALSE;
	            	}
	            	
	            }
	            else{
	                return defaultValue;
	            }
	            
	        }
	        else{
            	if(!showOnlyErrorLogs){
            		log.debug("val is null");
	            }
	            return defaultValue;
	        }
    	}catch(Exception e){
    		throw new UtilException("error getting setting boolean value", e);
    	}
    }
    
    protected String getTrueValue(){
    	return "TRUE";
    }
    
    protected String getFalseValue(){
    	return "FALSE";
    }
    
    protected boolean ignoreDescription(){
    	return false;
    }
}
