package bgn.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bgn.settings.util.SUBUtils;

public class EnumFields {

    private final Logger log = LoggerFactory.getLogger(EnumFields.class);
	private Class<?> enumClass; 
	private String defaultValueFieldName = "defaultValue";
	private String defaultDescriptionFieldName = "defaultDescription";
	private String createIfNotExistFieldName = "createIfNotExist";
	
	public EnumFields(Class<?> enumClass, String defaultValueFieldName,
			String defaultDescriptionFieldName, String createIfNotExistFieldName) {
		this.enumClass = enumClass;
		this.defaultValueFieldName = defaultValueFieldName;
		this.defaultDescriptionFieldName = defaultDescriptionFieldName;
		this.createIfNotExistFieldName = createIfNotExistFieldName;
		validate();
	}

	public EnumFields(Class<?> enumClass) {
		this.enumClass = enumClass;
		validate();
	}
	private void validate(){
		if(enumClass == null){
			throw new IllegalArgumentException("The clazz cannot be null");
		}
		if(!enumClass.isEnum()){
			throw new IllegalArgumentException("The clazz must be an enum class");
		}
		Object[] enumConstants = enumClass.getEnumConstants();
		if(enumConstants == null){
			throw new IllegalArgumentException("The clazz must be an enum class");
		}
		if(enumConstants.length == 0){
			log.warn("This enum class "+enumClass.getName()+" does not have any enum elements");
		}else{
			Object object = enumConstants[0];
			
			SUBUtils.getProperty(enumClass, object, defaultValueFieldName);
			SUBUtils.getProperty(enumClass, object, createIfNotExistFieldName);
			
//			the test for defaultDescriptionFieldName would be done in the settingUtilBase class depending on whether description is ignored or not
		}
		
	}

	public String getDefaultValueFieldName() {
		return defaultValueFieldName;
	}

	public String getDefaultDescriptionFieldName() {
		return defaultDescriptionFieldName;
	}

	public String getCreateIfNotExistFieldName() {
		return createIfNotExistFieldName;
	}

	public Class<?> getEnumClass() {
		return enumClass;
	}
}
