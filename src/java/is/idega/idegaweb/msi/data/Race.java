package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.events.RaceUpdatedAction;

import java.sql.Timestamp;

import com.idega.data.IDOEntity;
import com.idega.user.data.Group;

public interface Race extends IDOEntity, Group {
	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#getRaceDate
	 */
	public Timestamp getRaceDate();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#getLastRegistrationDate
	 */
	public Timestamp getLastRegistrationDate();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#getLastRegistrationDatePrice1
	 */
	public Timestamp getLastRegistrationDatePrice1();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#getChipRent
	 */
	public float getChipRent();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#getRaceCategory
	 */
	public RaceCategory getRaceCategory();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#getRaceType
	 */
	public RaceType getRaceType();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#setRaceDate
	 */
	public void setRaceDate(Timestamp date);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#setLastRegistrationDate
	 */
	public void setLastRegistrationDate(Timestamp date);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#setLastRegistrationDatePrice1
	 */
	public void setLastRegistrationDatePrice1(Timestamp date);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#setChipRent
	 */
	public void setChipRent(float rent);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#setRaceCategory
	 */
	public void setRaceCategory(RaceCategory raceCategory);

	/**
	 * 
	 * @param raceCategoryID is {@link RaceCategory#getPrimaryKey()}
	 */
	public void setRaceCategory(String raceCategoryID);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#setRaceType
	 */
	public void setRaceType(RaceType raceType);

	/**
	 * 
	 * @param raceTypeID is {@link RaceType#getPrimaryKey()}
	 */
	public void setRaceType(String raceTypeID);

	/**
	 *  
	 * @param publishEvent <code>true</code> if {@link RaceUpdatedAction} 
	 * should be emitted or <code>false</code>;
	 */
	public void setNotification(boolean publishEvent);
}