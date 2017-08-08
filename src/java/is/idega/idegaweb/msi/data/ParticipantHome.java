package is.idega.idegaweb.msi.data;


import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;
import com.idega.user.data.Group;
import com.idega.user.data.bean.User;

import is.idega.idegaweb.msi.events.ParticipantUpdatedAction;

public interface ParticipantHome extends IDOHome {
	public Participant create() throws CreateException;

	public Participant findByPrimaryKey(Object pk) throws FinderException;

	public Collection<Participant> findAll() throws FinderException;

	public Collection<Participant> findAllByRace(Group race) throws FinderException;

	/**
	 *
	 * @param id is {@link Participant#getPrimaryKey()}
	 * @param seasonId is {@link Season#getPrimaryKey()}
	 * @param raceId is {@link Race#getPrimaryKey()}
	 * @param raceEventId is {@link RaceEvent#getPrimaryKey()}
	 * @param userId is {@link User#getId()}, not <code>null</code>;
	 * @param vehicle
	 * @param sponsorName
	 * @param paymentMethod
	 * @param payedAmount
	 * @param chipNumber
	 * @param raceNumber is {@link RaceNumber#getRaceNumber()}
	 * @param firstPartner is {@link User#getName()} or {@link User#getPersonalID()}
	 * @param secondPartner is {@link User#getName()} or {@link User#getPersonalID()}
	 * @param comment
	 * @param paymentDate
	 * @param isTimeTransmitterRented
	 * @param publishEvent <code>true</code> if new {@link ParticipantUpdatedAction}
	 * should be thrown
	 * @return updated/created entity or <code>null</code> on failure;
	 */
	Participant update(
			Integer id,
			Integer seasonId,
			Integer raceId,
			Integer raceEventId,
			Integer userId,
			String firstPartner,
			String secondPartner,
			RaceVehicleType vehicle,
			String chipNumber,
			String raceNumber,
			String comment,
			String sponsorName,
			String paymentMethod,
			String payedAmount,
			Date paymentDate,
			Boolean isTimeTransmitterRented,
			boolean publishEvent);

	Participant update(Integer id,
			Integer seasonId,
			Integer raceId,
			Integer raceEventId,
			Integer userId,
			String partner1,
			String partner2,
			RaceVehicleType vehicle,
			String chipNumber,
			String raceNumber,
			String comment,
			String sponsorName,
			String paymentMethod,
			String payedAmount,
			Date paymentDate,
			Boolean isTimeTransmitterRented,
			boolean publishEvent,
			com.idega.user.data.User firstPartner,
			com.idega.user.data.User secondPartner);
	
	/**
	 *
	 * @param raceId is {@link Race#getPrimaryKey()}
	 * @param userId is {@link User#getId()}, not <code>null</code>;
	 * @param publishEvent <code>true</code> if new {@link ParticipantUpdatedAction}
	 * should be thrown
	 * @return updated/created entity or <code>null</code> on failure;
	 */
	Collection<Participant> update(Integer raceId, Integer userId, boolean publishEvent);

	/**
	 *
	 * @param raceId is {@link Race#getPrimaryKey()}
	 * @param userId is {@link User#getId()}, not <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	Collection<Participant> findAll(Integer raceId, Integer userId);

	Collection<Participant> findByDates(String from, String to);
	
	Collection<Participant> findAll(String userId, String from, String to, String authCode);
	
}