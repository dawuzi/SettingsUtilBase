package bgn.settings;

/**
* This wrapper class eases the implementation of settings util in projects 
* by presenting a generic settings util class that can be extended
* for grid settings scenarios. It changes the visibility to public for the 
* getSettingByName and the basic get setting value methods to ease extension
* 
* @author Okafor Ezewuzie Dawuzi
* @version 1.0
* @since 2013
* 
*/

public abstract class GridSettingsUtilBase<T> extends SettingsUtilBase<T> { 

	public GridSettingsUtilBase(boolean cacheSetting, Class<T> clazz,String... orderedGridUniqueSettingsFields) { 
		super(cacheSetting, clazz, orderedGridUniqueSettingsFields);
        
		if(orderedGridUniqueSettingsFields == null || orderedGridUniqueSettingsFields.length == 0){
			throw new IllegalArgumentException("You must specify an orderedGridUniqueSettingsFields. Use" +
					" the SimpleSettingsUtilBase instead if it is not needed");
		}
	}

	public GridSettingsUtilBase(Class<T> clazz,String... orderedGridUniqueSettingsFields) { 
		this(false, clazz, orderedGridUniqueSettingsFields);
	}

	protected abstract T getSettingByName(String settingName,Object... orderedGridUniqueSettingsValues) throws Exception;

    public T getByName(String settingName,Object... orderedGridUniqueSettingsValues){
    	return super.getByName(settingName, orderedGridUniqueSettingsValues); 
    }
	
	@Override
	public T getSettingByName(String settingName, String defaultValue,
			String defaultDescription, boolean createIfNotExist,
			Object... orderedGridUniqueSettingsValues) {
		// TODO Auto-generated method stub
		return super.getSettingByName(settingName, defaultValue, defaultDescription,
				createIfNotExist, orderedGridUniqueSettingsValues);
	}

	@Override
	public String getSettingValue(String settingName, String defaultValue,
			String defaultDescription, boolean createIfNotExist,
			Object... orderedGridUniqueSettingsValues) {
		// TODO Auto-generated method stub
		return super.getSettingValue(settingName, defaultValue, defaultDescription,
				createIfNotExist, orderedGridUniqueSettingsValues);
	}

	@Override
	public Integer getSettingIntValue(String settingName,
			Integer defaultValue, String defaultDescription,
			boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {
		// TODO Auto-generated method stub
		return super.getSettingIntValue(settingName, defaultValue, defaultDescription,
				createIfNotExist, orderedGridUniqueSettingsValues);
	}

	@Override
	public Long getSettingLongValue(String settingName, Long defaultValue,
			String defaultDescription, boolean createIfNotExist,
			Object... orderedGridUniqueSettingsValues) {
		// TODO Auto-generated method stub
		return super.getSettingLongValue(settingName, defaultValue, defaultDescription,
				createIfNotExist, orderedGridUniqueSettingsValues);
	}

	@Override
	public Float getSettingFloatValue(String settingName,
			Float defaultValue, String defaultDescription,
			boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {
		// TODO Auto-generated method stub
		return super.getSettingFloatValue(settingName, defaultValue,
				defaultDescription, createIfNotExist, orderedGridUniqueSettingsValues);
	}

	@Override
	public Boolean getSettingBooleanValue(String settingName,
			Boolean defaultValue, String defaultDescription,
			boolean createIfNotExist, Object... orderedGridUniqueSettingsValues) {
		// TODO Auto-generated method stub
		return super.getSettingBooleanValue(settingName, defaultValue,
				defaultDescription, createIfNotExist, orderedGridUniqueSettingsValues);
	}
	
	
}
