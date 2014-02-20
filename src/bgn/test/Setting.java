package bgn.test;

public class Setting {
	
	private String name;
	private String value;
	private String description;
	private Company company;
	
	public Setting() {
		this.value = "value";
		this.description = "description";
	}
	public Setting(String name, String value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}
	public Setting(String name, String value, String description,
			Company company) {
		super();
		this.name = name;
		this.value = value;
		this.description = description;
		this.company = company;
	}
	public Setting(String name,Company company) {
		super();
		this.name = name;
		this.value = "value";
		this.description = "description";
		this.company = company;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null){
			return false;
		}
		
		if(this == obj){
			return true;
		}

		if (!(obj instanceof Setting))
		{
			return false;
		}		
		
		Setting that = (Setting) obj;
		
		return that.getName()!= null 
				&& this.getName() != null 
				&& this.getName().equalsIgnoreCase(that.getName());
	}
	@Override
	public int hashCode() {
		return 1;
	}
	@Override
	public String toString() {
		return super.toString()+" [name = "+name+", value = "+value+", description = "+description+", company = "+company+"]";
	}
	
	
}
