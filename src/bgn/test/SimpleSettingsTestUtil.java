package bgn.test;

import bgn.settings.SimpleSettingsUtilBase;


public class SimpleSettingsTestUtil extends SimpleSettingsUtilBase<Setting>{

	Service service = Service.getInstance();
	
	public SimpleSettingsTestUtil(boolean cacheSetting) {
		super(cacheSetting, Setting.class);
	}

	@Override
	public Setting getSettingByName(String settingName) throws Exception {
		// TODO Auto-generated method stub
		return service.getSettingByName(settingName);
	}

	@Override
	protected boolean createSetting(Setting setting) throws Exception {
		// TODO Auto-generated method stub
		return service.createSetting(setting);
	}

	@Override
	protected boolean updateSetting(Setting setting) throws Exception {
		// TODO Auto-generated method stub
		return service.updateSetting(setting);
	}

}
