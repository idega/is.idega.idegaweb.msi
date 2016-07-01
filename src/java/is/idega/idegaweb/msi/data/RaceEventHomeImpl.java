package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.user.data.User;
import com.idega.data.IDOFactory;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.data.UserHome;
import com.idega.util.ListUtil;

public class RaceEventHomeImpl extends IDOFactory implements RaceEventHome {

	private static final long serialVersionUID = 5478530439464633937L;

	private UserHome getUserHome() {
		try {
			return (UserHome) IDOLookup.getHome(User.class);
		} catch (IDOLookupException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get " + UserHome.class + " cause of: ", e);
		}

		return null;
	}
	
	private RaceHome getRaceHome() {
		try {
			return (RaceHome) IDOLookup.getHome(Race.class);
		} catch (IDOLookupException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get " + RaceHome.class + "cause of: ", e);
		}

		return null;
	}

	private EventHome getEventHome() {
		try {
			return (EventHome) IDOLookup.getHome(Event.class);
		} catch (IDOLookupException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get " + EventHome.class + " cause of: ", e);
		}

		return null;
	}

	private GroupHome getGroupHome() {
		try {
			return (GroupHome) IDOLookup.getHome(Group.class);
		} catch (IDOLookupException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get " + GroupHome.class + " cause of: ", e);
		}

		return null;
	}

	private GroupBusiness getGroupBusiness() {
		try {
			return IBOLookup.getServiceInstance(
					IWMainApplication.getDefaultIWApplicationContext(), 
					GroupBusiness.class);
		} catch (IBOLookupException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, 
					"Failed to get " + GroupBusiness.class + " cause of :", e);
		}

		return null;
	}

	public Class getEntityInterfaceClass() {
		return RaceEvent.class;
	}

	public RaceEvent create() {
		try {
			return (RaceEvent) super.createIDO();
		} catch (CreateException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to create entity, cause of: ", e);
		}

		return null;
	}

	public RaceEvent findByPrimaryKey(Object pk) {
		if (pk != null) {
			try {
				return (RaceEvent) super.findByPrimaryKeyIDO(pk);
			} catch (FinderException e) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, 
						"Failed to get entity, cause of: ", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceEventHome#findByRaceAndEvent(java.lang.Integer, java.lang.String)
	 */
	@Override
	public RaceEvent findByRaceAndEvent(Integer raceId, String eventId) {
		Collection<Group> raceEventGroups = null;
		try {
			raceEventGroups = getGroupHome().findGroupsContained(
					getGroupHome().findByPrimaryKey(raceId),
					Arrays.asList(MSIConstants.GROUP_TYPE_RACE_EVENT), 
					Boolean.TRUE);
		} catch (FinderException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get race events groups");
		}

		if (!ListUtil.isEmpty(raceEventGroups)) {
			for (Group raceEventGroup : raceEventGroups) {
				RaceEvent raceEvent = null;
				try {
					raceEvent = ConverterUtility.getInstance()
							.convertGroupToRaceEvent(raceEventGroup);
				} catch (FinderException e) {
					java.util.logging.Logger.getLogger(getClass().getName()).log(Level.WARNING, "", e);
				}

				if (raceEvent != null && raceEvent.getEventID().equals(eventId)) {
					return raceEvent;
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceEventHome#update(java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Float, java.lang.Float, java.lang.Integer, java.lang.Float, java.lang.Boolean)
	 */
	@Override
	public RaceEvent update(
			Integer raceEventId, 
			Integer raceId, 
			String eventId,
			Float registrationFee,
			Float lateRegistrationFee,
			Integer teamsAmount,
			Float timeTransmitterPrice,
			Boolean timeTransmitterRentEnabled, 
			boolean publishEvent) {
		RaceEvent raceEvent = findByPrimaryKey(raceEventId);
		if (raceEvent == null) {
			raceEvent = findByRaceAndEvent(raceId, eventId);
		}

		if (raceEvent == null) {
			Race race = getRaceHome().findByPrimaryKey(raceId);
			Event event = getEventHome().findByPrimaryKey(eventId);
			if (race != null && event != null) {
				Group raceEventGroup = null;
				try {
					raceEventGroup = getGroupBusiness().createGroupUnder(
							event.getName(), 
							event.getName(),
							MSIConstants.GROUP_TYPE_RACE_EVENT, 
							race);
				} catch (RemoteException | CreateException e) {
					Logger.getLogger(getClass().getName()).log(Level.WARNING, 
							"Failed to create race event group, cause of: ", e);
				}

				if (raceEventGroup != null) {
					try {
						raceEvent = ConverterUtility.getInstance()
								.convertGroupToRaceEvent(raceEventGroup);
					} catch (FinderException e) {
						Logger.getLogger(getClass().getName()).log(Level.WARNING, 
								"Failed to convert race event group to race event, cause of:", e);
					}
				}
			}
		}

		if (eventId != null) {
			raceEvent.setEventID(eventId.toString());
		}

		if (registrationFee != null) {
			raceEvent.setPrice(registrationFee);
		}

		if (lateRegistrationFee != null) {
			raceEvent.setPrice2(lateRegistrationFee);
		}

		if (teamsAmount != null) {
			raceEvent.setTeamCount(teamsAmount);
		}

		if (timeTransmitterRentEnabled != null) {
			raceEvent.setTimeTransmitterPriceOn(timeTransmitterRentEnabled);
		}

		if (timeTransmitterPrice != null) {
			raceEvent.setTimeTransmitterPrice(timeTransmitterPrice);
		}

		try {
			raceEvent.setNotification(publishEvent);
			raceEvent.store();
			return raceEvent;
		} catch (IDOStoreException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to save entity, cause of: ", e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceEventHome#addParticipant(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void addParticipant(Integer raceEventId, Integer userId) {
		if (raceEventId != null && userId != null) {
			RaceEvent raceEvent = findByPrimaryKey(raceEventId);
			if (raceEvent != null) {
				try {
					raceEvent.addGroup(getUserHome().findByPrimaryKey(userId));
				} catch (EJBException | FinderException e) {
					Logger.getLogger(getClass().getName()).log(Level.WARNING, 
							"Failed to get user by id: " + userId);
				}
			}
		}
	}
}