package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.events.RaceCategoryUpdatedAction;

import com.idega.data.IDOEntity;

public interface RaceCategory extends IDOEntity {
	/**
	 * @see is.idega.idegaweb.msi.data.RaceCategoryBMPBean#getCategoryKey
	 */
	public String getCategoryKey();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceCategoryBMPBean#setCategoryKey
	 */
	public void setCategoryKey(String key);

	/**
	 * 
	 * @param publishEvent is <code>true</code> 
	 * when {@link RaceCategoryUpdatedAction} should be emitted
	 */
	void setNotification(boolean publishEvent);
}