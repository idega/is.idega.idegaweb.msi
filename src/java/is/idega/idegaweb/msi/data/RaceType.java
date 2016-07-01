package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.events.RaceTypeUpdatedAction;

import com.idega.data.IDOEntity;

public interface RaceType extends IDOEntity {
	/**
	 * @see is.idega.idegaweb.msi.data.RaceTypeBMPBean#getRaceType
	 */
	public String getRaceType();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceTypeBMPBean#setRaceType
	 */
	public void setRaceType(String raceType);

	/**
	 * 
	 * @param publishEvent <code>true</code> if 
	 * new {@link RaceTypeUpdatedAction} should be emitted;
	 */
	void setNotification(boolean publishEvent);
}