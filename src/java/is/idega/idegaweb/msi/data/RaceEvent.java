package is.idega.idegaweb.msi.data;


import com.idega.user.data.Group;
import com.idega.data.IDOEntity;

public interface RaceEvent extends IDOEntity, Group {
	/**
	 * @see is.idega.idegaweb.msi.data.RaceEventBMPBean#getEventID
	 */
	public int getEventID();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceEventBMPBean#setEventID
	 */
	public void setEventID(int id);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceEventBMPBean#setEventID
	 */
	public void setEventID(Integer id);
}