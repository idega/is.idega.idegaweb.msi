package is.idega.idegaweb.msi.data;


import java.sql.Timestamp;

import com.idega.data.IDOEntity;
import com.idega.user.data.Group;
import com.idega.user.data.User;

public interface Participant extends IDOEntity {

	public void setRentsTimeTransmitter(boolean rents);
	public boolean isRentsTimeTransmitter();
	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getSeasonGroupID
	 */
	public int getSeasonGroupID();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getSeasonGroup
	 */
	public Group getSeasonGroup();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getRaceGroupID
	 */
	public int getRaceGroupID();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getRaceGroup
	 */
	public Group getRaceGroup();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getEventGroupID
	 */
	public int getEventGroupID();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getRaceEvent
	 */
	public RaceEvent getRaceEvent();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getUser
	 */
	public User getUser();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getUserID
	 */
	public int getUserID();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getChipNumber
	 */
	public String getChipNumber();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getRaceNumber
	 */
	public String getRaceNumber();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getRaceVehicle
	 */
	public RaceVehicleType getRaceVehicle();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getSponsors
	 */
	public String getSponsors();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getPayMethod
	 */
	public String getPayMethod();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getPayedAmount
	 */
	public String getPayedAmount();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getComment
	 */
	public String getComment();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getPartner1
	 */
	public String getPartner1();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getPartner2
	 */
	public String getPartner2();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getCreatedDate
	 */
	public Timestamp getCreatedDate();
	
	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getFirstPartner
	 */
	public User getFirstPartner();
	
	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#getSecondPartner
	 */
	public User getSecondPartner();

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setSeasonGroupID
	 */
	public void setSeasonGroupID(int seasonGroupID);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setSeasonGroup
	 */
	public void setSeasonGroup(Group seasonGroup);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setRaceGroupID
	 */
	public void setRaceGroupID(int raceGroupID);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setRaceGroup
	 */
	public void setRaceGroup(Group raceGroup);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setEventGroupID
	 */
	public void setEventGroupID(int eventGroupID);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setEventGroup
	 */
	public void setEventGroup(Group eventGroup);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setUserID
	 */
	public void setUserID(int userID);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setUser
	 */
	public void setUser(User user);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setChipNumber
	 */
	public void setChipNumber(String chipNumber);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setRaceNumber
	 */
	public void setRaceNumber(String raceNumber);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setRaceVehicle
	 */
	public void setRaceVehicle(RaceVehicleType vehicle);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setSponsors
	 */
	public void setSponsors(String sponsors);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setPayMethod
	 */
	public void setPayMethod(String payMethod);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setPayedAmount
	 */
	public void setPayedAmount(String amount);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setComment
	 */
	public void setComment(String comment);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setPartner1
	 */
	public void setPartner1(String partner1);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setPartner2
	 */
	public void setPartner2(String partner2);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setCreatedDate
	 */
	public void setCreatedDate(Timestamp created);

	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setFirstPartner
	 */
	public void setFirstPartner(User firstPartner);
	
	/**
	 * @see is.idega.idegaweb.msi.data.ParticipantBMPBean#setSecondPartner
	 */
	public void setSecondPartner(User secondPartner);
	
	/**
	 *
	 * @param publishEvent <code>true</code> when update event should be published
	 */
	void setNotification(boolean publishEvent);

	String getVehicle();

	public String getPaymentAuthCode();

	public void setPaymentAuthCode(String paymentAuthCode);

}