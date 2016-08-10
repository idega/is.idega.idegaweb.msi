package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.util.MSIConstants;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.idega.user.data.Group;
import com.idega.user.data.UserHome;
import com.idega.util.StringUtil;

public class ParticipantHomeImpl extends IDOFactory implements ParticipantHome {

	private static final long serialVersionUID = -6270406277569221655L;

	private RaceUserSettingsHome getRaceUserSettingsHome() {
		try {
			return (RaceUserSettingsHome) IDOLookup.getHome(RaceUserSettings.class);
		} catch (IDOLookupException e) {
			getLog().log(Level.WARNING, 
					"Failed to get " + RaceUserSettingsHome.class + 
					" cause of: ", e);
		}

		return null;
	}

	private UserHome getUserHome() {
		try {
			return (UserHome) IDOLookup.getHome(com.idega.user.data.User.class);
		} catch (IDOLookupException e) {
			getLog().log(Level.WARNING, 
					"Failed to get " + UserHome.class + 
					" cause of: ", e);
		}

		return null;
	}

	private SeasonHome getSeasonHome() {
		try {
			return (SeasonHome) IDOLookup.getHome(Season.class);
		} catch (IDOLookupException e) {
			getLog().log(Level.WARNING, 
					"Failed to get " + SeasonHome.class + 
					" cause of: ", e);
		}

		return null;
	}
	
	private RaceEventHome getRaceEventHome() {
		try {
			return (RaceEventHome) IDOLookup.getHome(RaceEvent.class);
		} catch (IDOLookupException e) {
			getLog().log(Level.WARNING, 
					"Failed to get " + RaceEventHome.class + 
					" cause of: ", e);
		}

		return null;
	}

	private RaceHome getRaceHome() {
		try {
			return (RaceHome) IDOLookup.getHome(Race.class);
		} catch (IDOLookupException e) {
			getLog().log(Level.WARNING, 
					"Failed to get " + RaceHome.class + 
					" cause of: ", e);
		}

		return null;
	}

	public Class getEntityInterfaceClass() {
		return Participant.class;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.ParticipantHome#create()
	 */
	public Participant create() {
		try {
			return (Participant) super.createIDO();
		} catch (CreateException e) {
			getLog().log(Level.WARNING, "Failed to create participant, cause of:", e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.ParticipantHome#findByPrimaryKey(java.lang.Object)
	 */
	public Participant findByPrimaryKey(Object pk) {
		if (pk != null) {
			try {
				return (Participant) super.findByPrimaryKeyIDO(pk);
			} catch (FinderException e) {
				getLog().log(Level.WARNING, "Failed to find participant by id: " + pk);
			}
		}

		return null;
	}

	public Collection<Participant> findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Integer> ids = ((ParticipantBMPBean) entity).ejbFindAll();
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection<Participant> findAllByRace(Group race) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Integer> ids = ((ParticipantBMPBean) entity).ejbFindAllByRace(race);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.ParticipantHome#findAll(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Collection<Participant> findAll(Integer raceId, Integer userId) {
		ParticipantBMPBean entity = (ParticipantBMPBean) idoCheckOutPooledEntity();
		Collection<Integer> ids = entity.ejbFindAll(raceId, userId);
		try {
			return this.getEntityCollectionForPrimaryKeys(ids);
		} catch (FinderException e) {
			getLog().log(Level.WARNING, "Failed to get entities by primary keys: " + ids);
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.ParticipantHome#update(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, is.idega.idegaweb.msi.data.RaceVehicleType, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.lang.Boolean, boolean)
	 */
	@Override
	public Participant update(Integer id, 
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
			boolean publishEvent) {
		Participant participant = findByPrimaryKey(id);
		if (participant == null) {
			participant = create();
		}

		if (seasonId != null) {
			participant.setSeasonGroupID(seasonId);
		}

		if (raceId != null) {
			participant.setRaceGroupID(raceId);
		}

		if (raceEventId != null) {
			participant.setEventGroupID(raceEventId);
			RaceEvent raceEvent = getRaceEventHome().findByPrimaryKey(raceEventId);
			if (raceEvent != null) {
				if (raceEvent.getTeamCount() > 1 && !StringUtil.isEmpty(firstPartner)) {
					participant.setPartner1(firstPartner);
				}

				if (raceEvent.getTeamCount() > 2 && !StringUtil.isEmpty(secondPartner)) {
					participant.setPartner2(secondPartner);
				}
			}
		}

		if (userId != null) {
			participant.setUserID(userId);
		}

		if (vehicle != null) {
			participant.setRaceVehicle(vehicle);
		}

		if (!StringUtil.isEmpty(raceNumber)) {
			participant.setRaceNumber(raceNumber);
		}

		if (!StringUtil.isEmpty(chipNumber)) {
			participant.setChipNumber(chipNumber);
		}

		if (!StringUtil.isEmpty(comment)) {
			participant.setComment(comment);
		}

		if (!StringUtil.isEmpty(sponsorName)) {
			participant.setSponsors(sponsorName);
		}

		if (!StringUtil.isEmpty(payedAmount) && Integer.valueOf(payedAmount) > 0) {
			participant.setPayedAmount(payedAmount);
		}

		if (paymentDate != null) {
			participant.setCreatedDate(new Timestamp(paymentDate.getTime()));
		}

		if (!StringUtil.isEmpty(paymentMethod)) {
			participant.setPayMethod(paymentMethod);
		}

		if (isTimeTransmitterRented != null) {
			participant.setRentsTimeTransmitter(isTimeTransmitterRented);
		}

		participant.setNotification(publishEvent);

		try {
			participant.store();
			return participant;
		} catch (IDOStoreException e) {
			getLog().log(Level.WARNING, "Failed to save participant, cause of:", e);;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.ParticipantHome#update(java.lang.Integer, java.lang.Integer, java.lang.Integer, boolean)
	 */
	@Override
	public Collection<Participant> update(Integer raceId, Integer userId, boolean publishEvent) {
		Integer seasonId  = null;
		RaceVehicleType vehicle = null;
		String sponsorName = null;
		String raceTypeName = null;
		String raceNumber = null;
		String chipNumber = null;

		Race race = getRaceHome().findByPrimaryKey(raceId);
		if (race != null) {
			RaceType raceType = race.getRaceType();
			if (raceType != null) {
				raceTypeName = raceType.getRaceType();
			}
			
			Season season = getSeasonHome().getSeason(race.getRaceDate());
			if (season != null) {
				seasonId = Integer.valueOf(season.getPrimaryKey().toString());
			}

			RaceUserSettings userSettings = null;
			try {
				userSettings = getRaceUserSettingsHome().findByUser(
						getUserHome().findByPrimaryKey(userId));
			} catch (FinderException e) {}

			if (userSettings != null) {
				vehicle = userSettings.getVehicleType();
				sponsorName = userSettings.getSponsor();
				chipNumber = userSettings.getTransponderNumber();

				if (MSIConstants.RACE_TYPE_MX_AND_ENDURO.equals(raceTypeName)) {
					RaceNumber mx = userSettings.getRaceNumberMX();
					if (mx != null) {
						raceNumber = mx.getRaceNumber();
					}
				}

				if (MSIConstants.RACE_TYPE_SNOCROSS.equals(raceTypeName)) {
					RaceNumber snocross = userSettings.getRaceNumberSnocross();
					if (snocross != null) {
						raceNumber = snocross.getRaceNumber();
					}
				}
			}
		}

		ArrayList<Participant> updatedParticipants = new ArrayList<Participant>();

		Collection<Participant> participants = findAll(raceId, userId);
		for (Participant participant: participants) {
			Participant updatedParticipant = update(
					(Integer) participant.getPrimaryKey(),
					seasonId, raceId,
					(Integer) participant.getRaceEvent().getPrimaryKey(),
					userId, null, null, vehicle, chipNumber, raceNumber,
					null, sponsorName, null, null, null, null,
					publishEvent);
			if (updatedParticipant != null) {
				updatedParticipants.add(updatedParticipant);
			}
		}

		return updatedParticipants;
	}
}