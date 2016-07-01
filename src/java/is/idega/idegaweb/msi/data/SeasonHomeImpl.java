package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.data.IDOFactory;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.timer.DateUtil;

public class SeasonHomeImpl extends IDOFactory implements SeasonHome {

	private static final long serialVersionUID = -7724497645144572872L;

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
		return Season.class;
	}

	public Season create() {
		try {
			return (Season) super.createIDO();
		} catch (CreateException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to create new entity, cause of:", e);
		}

		return null;
	}

	public Season findByPrimaryKey(Object pk) {
		try {
			return (Season) super.findByPrimaryKeyIDO(pk);
		} catch (FinderException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get entity by priamary key:" + pk);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.SeasonHome#findByName(java.lang.String)
	 */
	@Override
	public Season findByName(String name) {
		if (name != null) {
			Collection<Group> seasonGroups = null;
			try {
				seasonGroups = getGroupHome().findGroupsByNameAndGroupTypes(
						name, 
						Arrays.asList(MSIConstants.GROUP_TYPE_SEASON), 
						Boolean.TRUE);
			} catch (FinderException e) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, 
						"Failed to find season by name: " + name);
			}

			if (!ListUtil.isEmpty(seasonGroups)) {
				try {
					return ConverterUtility.getInstance().convertGroupToSeason(
							seasonGroups.iterator().next());
				} catch (FinderException e) {
					Logger.getLogger(getClass().getName()).log(Level.WARNING, 
							"Failed to convert group to season, cause of: ", e);
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.SeasonHome#update(java.lang.Integer, java.lang.String, java.lang.String, java.sql.Timestamp, java.sql.Timestamp)
	 */
	@Override
	public Season update(
			Integer seasonId, 
			String seasonName, 
			String description,
			Timestamp begin, 
			Timestamp end) {
		Season season = findByPrimaryKey(seasonId);
		if (season == null) {
			Group seasonsGroup = null;
			try {
				seasonsGroup = getGroupHome().findByPrimaryKey(1430);
			} catch (FinderException e) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, 
						"Failed to get seasons group by id: " + 1430);
			}

			if (seasonsGroup != null) {
				Group seasonGroup = null;
				try {
					seasonGroup = getGroupBusiness().createGroupUnder(
							seasonName, 
							description, 
							MSIConstants.GROUP_TYPE_SEASON, 
							seasonsGroup);
				} catch (RemoteException | CreateException e) {
					Logger.getLogger(getClass().getName()).log(Level.WARNING, 
							"Failed to create season group, cause of: ", e);
				}

				if (seasonGroup != null) {
					try {
						season = ConverterUtility.getInstance()
								.convertGroupToSeason(seasonGroup);
					} catch (FinderException e) {
						Logger.getLogger(getClass().getName()).log(Level.WARNING, 
								"Failed to convert group " + seasonGroup.getPrimaryKey().toString() + 
								" to season, cause of: ", e);
					}
				}
			}
		}

		if (!StringUtil.isEmpty(seasonName)) {
			season.setName(seasonName);
		}

		if (!StringUtil.isEmpty(description)) {
			season.setDescription(description);
		}

		if (begin != null) {
			season.setSeasonBeginDate(begin);
		}

		if (end != null) {
			season.setSeasonEndDate(end);
		}

		try {
			season.store();
			return season;
		} catch (IDOStoreException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to save season by name: " + seasonName);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.SeasonHome#update(java.lang.Integer, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public Season update(
			Integer seasonId, 
			String seasonName, 
			String description,
			Date begin, 
			Date end) {
		return update(
				seasonId, 
				seasonName, 
				description, 
				begin != null ? new Timestamp(begin.getTime()) : (Timestamp) null, 
				end != null ? new Timestamp(end.getTime()) : (Timestamp) null);
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.SeasonHome#getSeason(java.util.Date)
	 */
	@Override
	public Season getSeason(Date date) {
		LocalDateTime dateTime = DateUtil.getDateTime(date);
		if (dateTime != null) {
			Season season = findByName(String.valueOf(dateTime.getYear()));
			if (season != null) {
				return season;
			}
			
			LocalDate beginDate = LocalDate.of(dateTime.getYear(), 1, 1);
			LocalDate endDate = LocalDate.of(dateTime.getYear(), 12, 31);
			return update(
					null, 
					String.valueOf(dateTime.getYear()), 
					null, 
					DateUtil.getDate(beginDate), 
					DateUtil.getDate(endDate));
		}
	
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.SeasonHome#getCurrentSeason()
	 */
	@Override
	public Season getCurrentSeason() {
		return getSeason(new Date(System.currentTimeMillis()));
	}
}