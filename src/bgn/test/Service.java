package bgn.test;


import java.util.HashMap;
import java.util.Map;

public class Service {
	
	private static Service service;
	private static Map<String, Setting> localDB = new HashMap<String, Setting>();
	
	public static Service getInstance(){
		if(service == null){
			service = new Service();
		}
		return service;
	}
	
	private Service (){
	}
	
	public Setting getSettingByName(String settingName){
		return localDB.get(settingName.trim());
	}

	public boolean createSetting(Setting setting){
		
		validateSetting(setting);
		
		String settingName = setting.getName().trim();
		
		if(getSettingByName(settingName) != null){
			throw new IllegalArgumentException("setting with name already exists");
		}
		localDB.put(settingName, setting);
		
		return true;
	}
	
	public boolean updateSetting(Setting setting){
		validateSetting(setting);
		return true;
	}
	
	public void showAllRecords(){
		if(localDB.isEmpty()){
			System.out.println("no settings in db");
			return;
		}
		System.out.println("showing all settings in db");
		for(String str : localDB.keySet()){
			System.out.println("setting name = "+str+", Setting = "+localDB.get(str));
		}
	}
	
	public void validateSetting(Setting setting){
		
		if(setting == null){
			throw new IllegalArgumentException("setting is null");
		}
		if(setting.getName() == null || setting.getName().trim().equals("")){
			throw new IllegalArgumentException("setting name is null or empty");
		}
	}
}
