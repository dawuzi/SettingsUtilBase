package bgn.test;

public class Company {
	private String name;
	
	
	public Company(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null){
			return false;
		}
		
		if(this == obj){
			return true;
		}

		if (!(obj instanceof Company))
		{
			return false;
		}		
		
		Company that = (Company) obj;
		
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
		return "name = "+name;
	}
}
