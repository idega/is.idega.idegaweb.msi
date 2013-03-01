package is.idega.idegaweb.msi.data;


import com.idega.user.data.Group;
import com.idega.data.IDOEntity;

public interface RaceEvent extends IDOEntity, Group {
	/**
	 * @see is.idega.idegaweb.msi.data.RaceEventBMPBean#getEventID
	 */
	public String getEventID();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceEventBMPBean#getPrice
	 */
	public float getPrice();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceEventBMPBean#getPrice2
	 */
	public float getPrice2();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceEventBMPBean#setEventID
	 */
	public void setEventID(String id);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceEventBMPBean#setPrice
	 */
	public void setPrice(float price);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceEventBMPBean#setPrice2
	 */
	public void setPrice2(float price);
}