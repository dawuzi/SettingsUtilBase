package bgn.settings;

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

	public SimpleSettingsUtilBase(Class<T> clazz) {
		super(false, clazz);
	}
	
	public SimpleSettingsUtilBase(boolean cacheSetting, Class<T> clazz) {
		super(cacheSetting, clazz);
	}

    public abstract T getSettingByName(String settingName) throws Exception;
	
	@Override
	protected T getSettingByName(String settingName,
			Object... orderedGridUniqueSettingsValues) throws Exception {
		// TODO Auto-generated method stub
		return getSettingByName(settingName);
	}
	
	public T getSettingByName(String settingName, String defaultValue,
			String defaultDescription, boolean createIfNotExist) {
		// TODO Auto-generated method stub
		return super.getSettingByName(settingName, defaultValue, defaultDescription,
				createIfNotExist);
	}

	public String getSettingValue(String settingName, String defaultValue,
			String defaultDescription, boolean createIfNotExist) {
		// TODO Auto-generated method stub
		return super.getSettingValue(settingName, defaultValue, defaultDescription,createIfNotExist);
	}

	public Integer getSettingIntValue(String settingName, Integer defaultValue,
			String defaultDescription, boolean createIfNotExist) {
		// TODO Auto-generated method stub
		return super.getSettingIntValue(settingName, defaultValue, defaultDescription,createIfNotExist);
	}

	public Long getSettingLongValue(String settingName, Long defaultValue,
			String defaultDescription, boolean createIfNotExist) {
		// TODO Auto-generated method stub
		return super.getSettingLongValue(settingName, defaultValue, defaultDescription,createIfNotExist);
	}

	public Float getSettingFloatValue(String settingName, Float defaultValue,
			String defaultDescription, boolean createIfNotExist) {
		// TODO Auto-generated method stub
		return super.getSettingFloatValue(settingName, defaultValue,defaultDescription, createIfNotExist);
	}
	
	public Boolean getSettingBooleanValue(String settingName,
			Boolean defaultValue, String defaultDescription,
			boolean createIfNotExist) {
		// TODO Auto-generated method stub
		return super.getSettingBooleanValue(settingName, defaultValue,
				defaultDescription, createIfNotExist);
	}

}
