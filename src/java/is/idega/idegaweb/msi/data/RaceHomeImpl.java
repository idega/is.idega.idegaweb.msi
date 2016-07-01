package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.data.IDOFactory;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.util.StringUtil;

public class RaceHomeImpl extends IDOFactory implements RaceHome {

	private static final long serialVersionUID = -5862995410588381220L;

	private GroupBusiness groupBusiness;

	private GroupBusiness getGroupBusiness() {
		if (this.groupBusiness == null) {
			try {
				this.groupBusiness = IBOLookup.getServiceInstance(
						IWMainApplication.getDefaultIWApplicationContext(), 
						GroupBusiness.class);
			} catch (IBOLookupException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, 
						"Failed to get " + GroupBusiness.class + " cause of :", e);
			}
		}

		return this.groupBusiness;
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

	public Class getEntityInterfaceClass() {
		return Race.class;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceHome#create()
	 */
	@Override
	public Race create() {
		try {
			return (Race) super.createIDO();
		} catch (CreateException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to create entity, cause of: ", e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceHome#findByPrimaryKey(java.lang.Object)
	 */
	@Override
	public Race findByPrimaryKey(Object pk) {
		if (pk != null) {
			try {
				return (Race) super.findByPrimaryKeyIDO(pk);
			} catch (FinderException e) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, 
						"Failed to get entity by primary key: " + pk);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceHome#update(java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.sql.Timestamp, java.sql.Timestamp, java.sql.Timestamp, java.lang.String, java.lang.String, java.lang.Float)
	 */
	@Override
	public Race update(
			Integer primaryKey, 
			Integer seasonId, 
			String name,
			String description,
			Timestamp date,
			Timestamp lastRegistrationDate, 
			Timestamp lastPayedRegistrationDate, 
			String type, 
			String category,
			Float chipRentPrice, boolean publishEvent) {
		Race race = findByPrimaryKey(primaryKey);
		if (race == null) {
			Group seasonGroup = null;
			try {
				seasonGroup = getGroupHome().findByPrimaryKey(seasonId);
			} catch (FinderException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, "Failed to get season group, cause of: ", e);
			}

			if (seasonGroup != null) {
				Group gRace = null;
				try {
					gRace = getGroupBusiness().createGroupUnder(
							name, 
							description != null && description.length() < 255 ? description : name,
							MSIConstants.GROUP_TYPE_RACE, 
							seasonGroup);
				} catch (RemoteException | CreateException e) {
					Logger.getLogger(getClass().getName()).log(
							Level.WARNING, 
							"Failed to create new race group, cause of:", e);
				}

				if (gRace != null) {
					try {
						race = ConverterUtility.getInstance().convertGroupToRace(gRace);
					} catch (FinderException e) {
						Logger.getLogger(getClass().getName()).log(Level.WARNING, 
								"Failed to convert race group to race object, cause of: ", e);
					}
				}
			}
		}

		if (!StringUtil.isEmpty(name)) {
			race.setName(name);
		}

		if (!StringUtil.isEmpty(description)) {
			race.setDescription(description != null && description.length() < 255 ? description : name);
		}

		if (date != null) {
			race.setRaceDate(date);
		}

		if (lastRegistrationDate != null) {
			race.setLastRegistrationDate(lastRegistrationDate);
		}

		if (lastPayedRegistrationDate != null) {
			race.setLastRegistrationDatePrice1(lastPayedRegistrationDate);
		}

		if (type != null) {
			race.setRaceType(type);
		}

		if (category != null) {
			race.setRaceCategory(category);
		}

		if (chipRentPrice != null) {
			race.setChipRent(chipRentPrice);
		}

		race.setNotification(publishEvent);

		try {
			race.store();
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to save " + Race.class + " cause of: ", e);
		}

		return race;
	}

	public void remove(Race race) {}
}