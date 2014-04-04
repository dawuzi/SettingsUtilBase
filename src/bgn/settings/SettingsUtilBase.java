package bgn.settings;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bgn.exception.UtilException;
import bgn.settings.util.SUBUtils;


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
    protected final boolean cacheSetting ;
    protected final Class<T> clazz;
    protected final String[] orderedGridUniqueSettingsFields ;
    protected final boolean showOnlyErrorLogs ;
    
    protected final boolean ignoreDescription;
    
    protected final String NAME;
    protected final String VALUE;
    protected final String DESCRIPTION;
    
    protected final String TRUE;
    protected final String FALSE;
    
    private final List<EnumFields> allowedEnumFields;
    private final List<Class<?>> allowedEnumClasses;
    
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
    	
    	allowedEnumFields = getAllowedSettingsEnum();
    	allowedEnumClasses = getEnumClasses(allowedEnumFields);
    	validateAllowedEnumFields();
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
		return SUBUtils.getProperty(clazz, setting, fieldName);
	}
	private void setProperty(Object setting, String fieldName, Object value){
		SUBUtils.setProperty(clazz, setting, fieldName, value);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List getList(Object settingName, Object... objs) 
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException{
		
		List list = new ArrayList();

		list.add(BeanUtils.cloneBean(settingName));

		if(objs != null){
			for(Object o : objs){
				if(o != null){
					list.add(BeanUtils.cloneBean(o));
				}
				else{
					list.add(o);
				}
			}
		}
		return list;
	}

	protected T getSettingByName(String settingName, String defaultValue, 
			String defaultDescription, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues){
        
        if(settingName.trim().equals("")){
            throw new IllegalArgumentException("setting name is empty");
        }
        
        validateOrderedGridUniqueSettingsValues(orderedGridUniqueSettingsValues);
        
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
	            
	        	if(cacheSetting){
	            
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
        
        String val = getSettingValue(settingName, defValInUse, defaultDescription, createIfNotExist, orderedGridUniqueSettingsValues);
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
        
        String val = getSettingValue(settingName, defValInUse, defaultDescription, createIfNotExist, orderedGridUniqueSettingsValues);
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
        
        String val = getSettingValue(settingName, defValInUse, defaultDescription, createIfNotExist, orderedGridUniqueSettingsValues);
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
	        
	        String val = getSettingValue(settingName, defValInUse, defaultDescription, createIfNotExist, orderedGridUniqueSettingsValues);
	        
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
    
    public boolean create(T setting){
    	try {
			
    		boolean create = createSetting(setting);
    		
    		if(cacheSetting){
    			cacheSetting(setting);
    		}
    		
    		return create;
		} catch (Exception e) {
    		throw new UtilException("error creating setting", e);
		}
    }
    
    public boolean update(T setting){
    	try {
			
    		boolean update = updateSetting(setting);
    		
    		if(cacheSetting){
    			cacheSetting(setting);
    		}
    		
    		return update;
		} catch (Exception e) {
    		throw new UtilException("error updating setting", e);
		}
    }
    
    protected T getByName(String settingName, Object... orderedGridUniqueSettingsValues){

    	try{
    		
    		validateOrderedGridUniqueSettingsValues(orderedGridUniqueSettingsValues);
    		
    		T setting;
    		
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
    		
	        setting = getSettingByName(settingName, orderedGridUniqueSettingsValues);
    		
    		if(cacheSetting){
    			cacheSetting(setting);
    		}
    		
    		return setting;
    		
    	} catch (Exception e) {
    		throw new UtilException("error getting by setting", e);
    	}

    }

    private void cacheSetting(T setting) 
    		throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException{

    	if(setting == null){
        	if(!showOnlyErrorLogs){
                log.warn("null argument for cacheSetting method for setting class : "+clazz.getName());
        	}
    		return;
    	}
    	
    	String settingName = (String) getProperty(setting, NAME);

    	settingName = settingName.trim();


    	int lengthOfOrderedGridUniqueSettingsFields = 0;

		if(orderedGridUniqueSettingsFields != null){
			lengthOfOrderedGridUniqueSettingsFields = orderedGridUniqueSettingsFields.length;
		}

		Object[] orderedGridUniqueSettingsValues = new Object[lengthOfOrderedGridUniqueSettingsFields];
		
    	for(int x=0; x<lengthOfOrderedGridUniqueSettingsFields; x++){
    		
    		String field = orderedGridUniqueSettingsFields[x];
    		
    		Object fieldVal = getProperty(setting, field);
    		
    		orderedGridUniqueSettingsValues[x] = fieldVal;
    	}
    	
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
    }
    
    public void validateOrderedGridUniqueSettingsValues(Object... orderedGridUniqueSettingsValues){
    	
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
        else{
        	if(orderedGridUniqueSettingsFields != null && orderedGridUniqueSettingsFields.length > 0){
                throw new IllegalArgumentException("you cannot pass in an orderedGridUniqueSettingsValues" +
                		" when there are no orderedGridUniqueSettingsFields in the constructor." +
                		" Pass in the corresponding strings of orderedGridUniqueSettingsFields" +
                		" in the constuctor.");
        	}
        }
    
    }
    
    protected  List<EnumFields> getAllowedSettingsEnum(){
    	return null;
    }
    
	private SettingsReplica getSettingsReplica(Enum<?> enumObject, Object value, Boolean createIfNotExist) {
		Class<?> enumClazz = enumObject.getClass();
		EnumFields enumField = getEnumField(enumClazz);
		if(value == null){
			value =  (String) SUBUtils.getProperty(enumClazz, enumObject, enumField.getDefaultValueFieldName());
		}
		if(createIfNotExist == null){
			createIfNotExist = (Boolean) SUBUtils.getProperty(enumClazz, enumObject, enumField.getCreateIfNotExistFieldName());
		}
		String description = null;
		if(!ignoreDescription){
			description = (String) SUBUtils.getProperty(enumClazz, enumObject, enumField.getDefaultDescriptionFieldName());
		}
		String name = enumObject.name();
		
		return new SettingsReplica(name, value.toString(), description, createIfNotExist);
	}
    
    private void validateAllowedEnumFields() {
    	
    	if(allowedEnumClasses == null || allowedEnumClasses.isEmpty()){
    		return;
    	}
    	
    	if(ignoreDescription()){
    		return;
    	}
    	
//    	validate the earlier skipped description field now
    	for(Class<?> clazz : allowedEnumClasses){
    		
    		Object[] enumConstants = clazz.getEnumConstants();
    		
    		EnumFields enumField = getEnumField(clazz);
    		
    		if(enumConstants.length == 0){
    			if(!showOnlyErrorLogs){
        			log.warn("This enum class "+clazz.getName()+" does not have any enum elements");
    			}
    		}else{
    			Object object = enumConstants[0];
    			SUBUtils.getProperty(clazz, object, enumField.getDefaultDescriptionFieldName());
    		}    		
    	}
    	
	}
	
    private List<Class<?>> getEnumClasses(List<EnumFields> allowedEnumFields) {
		if(allowedEnumFields == null || allowedEnumFields.isEmpty()){
			if(!showOnlyErrorLogs){
				log.debug("allowedEnumFields is null or empty : "+allowedEnumFields+", for settings class "+clazz.getName());
			}
			return null;
		}
		
		List<Class<?>> enumClasses = new ArrayList<Class<?>>();
		
		for(EnumFields anEnumField : allowedEnumFields){
			
			if(!enumClasses.contains(anEnumField.getEnumClass())){
				enumClasses.add(anEnumField.getEnumClass());
			}
			else{
				throw new IllegalArgumentException("Duplicate enum class found among the declared allowed enum fields : "
						+anEnumField.getEnumClass().getName());
			}
		}
		
		return enumClasses;
	}
	
	private EnumFields getEnumField(Class<?> enumClass){
		
		if(allowedEnumFields == null || allowedEnumFields.isEmpty()){
			throw new IllegalArgumentException("No enumFields declared in the overridden getAllowedSettingsEnum() method");
		}
		
		for(EnumFields enumField : allowedEnumFields){
			if(enumClass.equals(enumField.getEnumClass())){
				return enumField;
			}
		}
		
		throw new IllegalArgumentException("enum class not found among the list of the declared allowable Enum Fields. Please add the enum class " +
				"to one of the enum fields returned in the overriden method getAllowedSettingsEnum()");
	}
    
	private void validateEnumObject(Enum<?> enumObject){
    	if(allowedEnumClasses == null || allowedEnumClasses.isEmpty()){
			throw new UtilException("No allowed EnumFields declared. Override the getAllowedSettingsEnum() method and " +
					"set this class as the enumClass field of one of the returned EnumFields in the list");
    	}
		if(!allowedEnumClasses.contains(enumObject.getClass())){
			throw new UtilException("The object's class must be set as the enumClass field of one of the returned EnumFields in the list");
		}
	}
	
	protected T getSettingByName(Enum<?> enumObject, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, null, null);
		return doGetSettingByName(settingsReplica, orderedGridUniqueSettingsValues); 
	}

	protected T getSettingByName(Enum<?> enumObject, String defaultValue, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, defaultValue, createIfNotExist);
		return doGetSettingByName(settingsReplica, orderedGridUniqueSettingsValues); 
	}
	
	private T doGetSettingByName(SettingsReplica settingsReplica, Object... orderedGridUniqueSettingsValues) {
		return getSettingByName(settingsReplica.name, settingsReplica.value, settingsReplica.description, settingsReplica.createIfNotExist, orderedGridUniqueSettingsValues);
	}

	protected String getSettingValue(Enum<?> enumObject, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, null, null);
		return doGetSettingValue(settingsReplica, orderedGridUniqueSettingsValues); 
	}

	protected String getSettingValue(Enum<?> enumObject, String defaultValue, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, defaultValue, createIfNotExist);
		return doGetSettingValue(settingsReplica, orderedGridUniqueSettingsValues); 
	}
	
	private String doGetSettingValue(SettingsReplica settingsReplica, Object... orderedGridUniqueSettingsValues) {
		return getSettingValue(settingsReplica.name, settingsReplica.value, settingsReplica.description, settingsReplica.createIfNotExist, orderedGridUniqueSettingsValues);
	}

	protected Integer getSettingIntValue(Enum<?> enumObject, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, null, null);
		return doGetSettingIntValue(settingsReplica, orderedGridUniqueSettingsValues); 
	}

	protected Integer getSettingIntValue(Enum<?> enumObject, Integer defaultValue, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, defaultValue, createIfNotExist);
		return doGetSettingIntValue(settingsReplica, orderedGridUniqueSettingsValues); 
	}
	
	private Integer doGetSettingIntValue(SettingsReplica settingsReplica, Object... orderedGridUniqueSettingsValues) {
		Integer defaultValue = null;
		try {
			if(settingsReplica.value != null){
				defaultValue = Integer.valueOf(settingsReplica.value);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return getSettingIntValue(settingsReplica.name, defaultValue, settingsReplica.description, settingsReplica.createIfNotExist, orderedGridUniqueSettingsValues);
	}

	protected Long getSettingLongValue(Enum<?> enumObject, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, null, null);
		return doGetSettingLongValue(settingsReplica, orderedGridUniqueSettingsValues); 
	}

	protected Long getSettingLongValue(Enum<?> enumObject, Long defaultValue, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, defaultValue, createIfNotExist);
		return doGetSettingLongValue(settingsReplica, orderedGridUniqueSettingsValues); 
	}
	
	private Long doGetSettingLongValue(SettingsReplica settingsReplica, Object... orderedGridUniqueSettingsValues) {
		Long defaultValue = null;
		try {
			if(settingsReplica.value != null){
				defaultValue = Long.valueOf(settingsReplica.value);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return getSettingLongValue(settingsReplica.name, defaultValue, settingsReplica.description, settingsReplica.createIfNotExist, orderedGridUniqueSettingsValues);
	}

	protected Float getSettingFloatValue(Enum<?> enumObject, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, null, null);
		return dogetSettingFloatValue(settingsReplica, orderedGridUniqueSettingsValues); 
	}

	protected Float getSettingFloatValue(Enum<?> enumObject, Float defaultValue, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, defaultValue, createIfNotExist);
		return dogetSettingFloatValue(settingsReplica, orderedGridUniqueSettingsValues); 
	}
	
	private Float dogetSettingFloatValue(SettingsReplica settingsReplica, Object... orderedGridUniqueSettingsValues) {
		Float defaultValue = null;
		try {
			if(settingsReplica.value != null){
				defaultValue = Float.valueOf(settingsReplica.value);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return getSettingFloatValue(settingsReplica.name, defaultValue, settingsReplica.description, settingsReplica.createIfNotExist, orderedGridUniqueSettingsValues);
	}

	protected Boolean getSettingBooleanValue(Enum<?> enumObject, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, null, null);
		return dogetSettingBooleanValue(settingsReplica, orderedGridUniqueSettingsValues); 
	}

	protected Boolean getSettingBooleanValue(Enum<?> enumObject, Boolean defaultValue, boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {
		validateEnumObject(enumObject); 
		SettingsReplica settingsReplica = getSettingsReplica(enumObject, defaultValue, createIfNotExist);
		return dogetSettingBooleanValue(settingsReplica, orderedGridUniqueSettingsValues); 
	}
	
	private Boolean dogetSettingBooleanValue(SettingsReplica settingsReplica, Object... orderedGridUniqueSettingsValues) {
		Boolean defaultValue = null;
		try {
			if(settingsReplica.value != null){
//				The actual overridden true value in use is better than assuming the extending class used the default
				if(settingsReplica.value.trim().equalsIgnoreCase(TRUE)){ 
					defaultValue = Boolean.TRUE;
				}
				else{
					defaultValue = Boolean.FALSE;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return getSettingBooleanValue(settingsReplica.name, defaultValue, settingsReplica.description, settingsReplica.createIfNotExist, orderedGridUniqueSettingsValues);
	}
	
    private static class SettingsReplica{
    	private String name;
    	private String value;
    	private String description;
    	private boolean createIfNotExist;
    	
		public SettingsReplica(String name, String value, String description,boolean createIfNotExist) {
			this.name = name;
			this.value = value;
			this.description = description;
			this.createIfNotExist = createIfNotExist;
		}
    }
    
}
