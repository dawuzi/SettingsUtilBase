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
		
		if(SUBUtils.isAnyArgumentNull(defaultDescriptionFieldName, defaultValueFieldName, createIfNotExistFieldName)){
			throw new IllegalArgumentException("Neither defaultDescriptionFieldName, defaultValueFieldName, " +
					"createIfNotExistFieldName arguments can be null or empty. defaultDescriptionFieldName : -"+defaultDescriptionFieldName+"-, " +
					"defaultValueFieldName : -"+defaultValueFieldName+"-, createIfNotExistFieldName : -"+createIfNotExistFieldName+"-");
		}
		
//		defaultDescriptionFieldName is nullable
		if(defaultDescriptionFieldName != null){
			defaultDescriptionFieldName = defaultDescriptionFieldName.trim();
		}
		defaultValueFieldName = defaultValueFieldName.trim();
		createIfNotExistFieldName = createIfNotExistFieldName.trim();
		
		
		if(defaultValueFieldName.equals(defaultDescriptionFieldName)){
			throw new IllegalArgumentException("The defaultDescriptionFieldName and defaultValueFieldName arguments are equal");
		}
		
		if(createIfNotExistFieldName.equals(defaultDescriptionFieldName)){
			throw new IllegalArgumentException("The defaultDescriptionFieldName and createIfNotExistFieldName arguments are equal");
		}
		
		if(defaultValueFieldName.equals(createIfNotExistFieldName)){
			throw new IllegalArgumentException("The defaultValueFieldName and createIfNotExistFieldName arguments are equal");
		}
		
		if(enumConstants.length == 0){
			log.warn("This enum class "+enumClass.getName()+" does not have any enum elements");
		}else{
			
			for(Object anEnum : enumConstants){
				Object defaultValue = SUBUtils.getProperty(enumClass, anEnum, defaultValueFieldName);
				Boolean createIfNotExist = (Boolean) SUBUtils.getProperty(enumClass, anEnum, createIfNotExistFieldName);
				if(createIfNotExist == null){
					throw new IllegalArgumentException("None of the enum constants should have a null createIfNotExist value. Error enum constant : "+anEnum.toString());
				}
				if(createIfNotExist && (defaultValue == null)){
					throw new IllegalArgumentException("If createIfNotExist is true then a default value must be specified. Error enum constant : "+anEnum.toString());
				}
			}
			
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
