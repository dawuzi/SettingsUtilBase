package bgn.test;

import javax.swing.JOptionPane;

public class Test {
	
	static Service service = Service.getInstance();
	
	public static void main(String[] args) throws Exception {
		test();
	}

	public static void test() throws Exception{
		
		service.showAllRecords();
		
		SimpleSettingsTestUtil settingUtil = new SimpleSettingsTestUtil(true);
		
		Boolean settingVal = settingUtil.getSettingBooleanValue("Test_Setting_Name", false, "test", true);
		
		service.showAllRecords();

		JOptionPane.showMessageDialog(null, "settingVal = "+settingVal);

		settingVal = settingUtil.getSettingBooleanValue("Test_Setting_Name", false, "test", true);

		JOptionPane.showMessageDialog(null, "settingVal = "+settingVal);

		service.showAllRecords();
		
		Setting setting = settingUtil.getByName("Test_Setting_Name");
		
		setting.setValue("true");
		
		service.showAllRecords();
		
		settingUtil.createSetting(setting); 
		
		settingUtil.getSettingByName("");
		
		settingUtil.getByName("");
	}

}
