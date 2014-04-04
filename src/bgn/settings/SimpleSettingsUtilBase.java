package bgn.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* This wrapper class eases the implementation of settings util in projects 
* by presenting a generic settings util class that can be extended
* for non grid settings scenarios.
* 
* @author Okafor Ezewuzie Dawuzi
* @version 1.0
* @since 2013
* 
*/


public abstract class SimpleSettingsUtilBase<T> extends SettingsUtilBase<T> {

    @SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(SimpleSettingsUtilBase.class);
	
	public SimpleSettingsUtilBase(Class<T> clazz) {
		super(false, clazz);
	}
	
	public SimpleSettingsUtilBase(boolean cacheSetting, Class<T> clazz) {
		super(cacheSetting, clazz);
	}

    protected abstract T getSettingByName(String settingName) throws Exception;
	
	@Override
	protected T getSettingByName(String settingName, Object... orderedGridUniqueSettingsValues) throws Exception {
		return getSettingByName(settingName);
	}

    public T getByName(String settingName){
    	return super.getByName(settingName); 
    }
	
	public T getSettingByName(String settingName, String defaultValue, String defaultDescription, boolean createIfNotExist) {
		return super.getSettingByName(settingName, defaultValue, defaultDescription, createIfNotExist);
	}

	public String getSettingValue(String settingName, String defaultValue, String defaultDescription, boolean createIfNotExist) {
		return super.getSettingValue(settingName, defaultValue, defaultDescription,createIfNotExist);
	}

	public Integer getSettingIntValue(String settingName, Integer defaultValue, String defaultDescription, boolean createIfNotExist) {
		return super.getSettingIntValue(settingName, defaultValue, defaultDescription,createIfNotExist);
	}

	public Long getSettingLongValue(String settingName, Long defaultValue, String defaultDescription, boolean createIfNotExist) {
		return super.getSettingLongValue(settingName, defaultValue, defaultDescription,createIfNotExist);
	}

	public Float getSettingFloatValue(String settingName, Float defaultValue, String defaultDescription, boolean createIfNotExist) {
		return super.getSettingFloatValue(settingName, defaultValue,defaultDescription, createIfNotExist);
	}
	
	public Boolean getSettingBooleanValue(String settingName, Boolean defaultValue, String defaultDescription, boolean createIfNotExist) {
		return super.getSettingBooleanValue(settingName, defaultValue, defaultDescription, createIfNotExist);
	}

	public T getSettingByName(Enum<?> enumObject) {
		return super.getSettingByName(enumObject);
	}

	protected T getSettingByName(Enum<?> enumObject, String defaultValue, boolean createIfNotExist) {
		return super.getSettingByName(enumObject, defaultValue,createIfNotExist);
	}

	protected String getSettingValue(Enum<?> enumObject) {
		return super.getSettingValue(enumObject);
	}

	protected String getSettingValue(Enum<?> enumObject, String defaultValue, boolean createIfNotExist) {
		return super.getSettingValue(enumObject, defaultValue,createIfNotExist);
	}
	
	protected Integer getSettingIntValue(Enum<?> enumObject) {
		return super.getSettingIntValue(enumObject);
	}

	protected Integer getSettingIntValue(Enum<?> enumObject, Integer defaultValue, boolean createIfNotExist) {
		return super.getSettingIntValue(enumObject, defaultValue,createIfNotExist);
	}

	protected Long getSettingLongValue(Enum<?> enumObject) {
		return super.getSettingLongValue(enumObject);
	}

	protected Long getSettingLongValue(Enum<?> enumObject, Long defaultValue, boolean createIfNotExist) {
		return super.getSettingLongValue(enumObject, defaultValue,createIfNotExist);
	}

	protected Float getSettingFloatValue(Enum<?> enumObject) {
		return super.getSettingFloatValue(enumObject);
	}

	protected Float getSettingFloatValue(Enum<?> enumObject, Float defaultValue, boolean createIfNotExist) {
		return super.getSettingFloatValue(enumObject, defaultValue,createIfNotExist);
	}
	
	protected Boolean getSettingBooleanValue(Enum<?> enumObject) {
		return super.getSettingBooleanValue(enumObject);
	}

	protected Boolean getSettingBooleanValue(Enum<?> enumObject, Boolean defaultValue, boolean createIfNotExist) {
		return super.getSettingBooleanValue(enumObject, defaultValue,createIfNotExist);
	}
	
    
}
