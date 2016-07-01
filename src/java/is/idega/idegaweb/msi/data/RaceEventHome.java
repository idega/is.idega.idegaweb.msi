package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.events.RaceEventUpdatedAction;

import com.idega.core.user.data.User;
import com.idega.data.IDOHome;

public interface RaceEventHome extends IDOHome {

	/**
	 * 
	 * @return entity or <code>null</code> on failure;
	 */
	public RaceEvent create();

	/**
	 * 
	 * @param pk is {@link RaceEvent#getPrimaryKey()}, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	public RaceEvent findByPrimaryKey(Object pk);

	/**
	 * 
	 * @param raceEventId is {@link RaceEvent#getPrimaryKey()}, new entity 
	 * is created if <code>null</code>;
	 * @param raceId is {@link Race#getPrimaryKey()}, mandatory on new entity creation;
	 * @param eventId is {@link Event#getPrimaryKey()}, mandatory on new entity creation;
	 * @param registrationFee is {@link RaceEvent#getPrice()}, skipped if <code>null</code>;
	 * @param lateRegistrationFee is {@link RaceEvent#getPrice2()}, skipped if <code>null</code>;
	 * @param teamsAmount
	 * @param timeTransmitterPrice
	 * @param timeTransmitterRentEnabled
	 * @param publishEvent true when there is a need to emit {@link RaceEventUpdatedAction}
	 * @return entity or <code>null</code> on failure;
	 */
	RaceEvent update(
			Integer raceEventId, 
			Integer raceId, 
			String eventId,
			Float registrationFee, 
			Float lateRegistrationFee,
			Integer teamsAmount, 
			Float timeTransmitterPrice,
			Boolean timeTransmitterRentEnabled, 
			boolean publishEvent);

	/**
	 * 
	 * @param raceEventId is {@link RaceEvent#getPrimaryKey()}, not <code>null</code>;
	 * @param userId is {@link User#getPrimaryKey()}, not <code>null</code>;
	 */
	void addParticipant(Integer raceEventId, Integer userId);

	/**
	 * 
	 * @param raceId is {@link Race#getPrimaryKey()}, not <code>null</code>;
	 * @param eventId is {@link Event#getName()}, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	RaceEvent findByRaceAndEvent(Integer raceId, String eventId);
}