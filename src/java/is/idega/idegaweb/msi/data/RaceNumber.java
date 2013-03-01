package is.idega.idegaweb.msi.data;


import java.sql.Timestamp;
import com.idega.data.IDOEntity;

public interface RaceNumber extends IDOEntity {
	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#getRaceNumber
	 */
	public String getRaceNumber();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#getRaceType
	 */
	public RaceType getRaceType();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#getIsInUse
	 */
	public boolean getIsInUse();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#getIsApproved
	 */
	public boolean getIsApproved();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#getApplicationDate
	 */
	public Timestamp getApplicationDate();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#getApprovedDate
	 */
	public Timestamp getApprovedDate();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#setRaceNumber
	 */
	public void setRaceNumber(String raceNumber);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#setRaceType
	 */
	public void setRaceType(RaceType type);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#setIsInUse
	 */
	public void setIsInUse(boolean isInUse);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#setIsApproved
	 */
	public void setIsApproved(boolean isApproved);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#setApplicationDate
	 */
	public void setApplicationDate(Timestamp applicationDate);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceNumberBMPBean#setApprovedDate
	 */
	public void setApprovedDate(Timestamp approvedDate);
}