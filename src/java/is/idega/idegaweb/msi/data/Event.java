package is.idega.idegaweb.msi.data;


import com.idega.data.IDOEntity;

public interface Event extends IDOEntity {
	/**
	 * @see is.idega.idegaweb.msi.data.EventBMPBean#getPrimaryKeyClass
	 */
	public Class getPrimaryKeyClass();

	/**
	 * @see is.idega.idegaweb.msi.data.EventBMPBean#getName
	 */
	public String getName();

	/**
	 * @see is.idega.idegaweb.msi.data.EventBMPBean#setName
	 */
	public void setName(String name);
	
	public boolean isValid();
	
	public void setValid(boolean valid);
}