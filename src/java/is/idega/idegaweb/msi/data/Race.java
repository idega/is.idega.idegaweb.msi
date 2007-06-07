package is.idega.idegaweb.msi.data;


import com.idega.user.data.Group;
import java.sql.Timestamp;
import com.idega.data.IDOEntity;

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
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#getPrice
	 */
	public float getPrice();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#getChipRent
	 */
	public float getChipRent();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#setRaceDate
	 */
	public void setRaceDate(Timestamp date);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#setLastRegistrationDate
	 */
	public void setLastRegistrationDate(Timestamp date);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#setPrice
	 */
	public void setPrice(float price);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceBMPBean#setChipRent
	 */
	public void setChipRent(float rent);
}