/*
 * Created on Jul 6, 2004
 */
package is.idega.idegaweb.msi.data;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

import is.idega.idegaweb.msi.events.ParticipantUpdatedAction;

/**
 * Description: <br>
 * Copyright: Idega Software 2004 <br>
 * Company: Idega Software <br>
 *
 * @author birna
 */
public class ParticipantBMPBean extends GenericEntity implements Participant {

	private static final long serialVersionUID = -4127402857174589109L;

	private static final String ENTITY_NAME = "msi_participant";

	private static final String COLUMN_SEASON = "grp_season";
	private static final String COLUMN_RACE = "grp_race";
	private static final String COLUMN_EVENT = "grp_event";
	private static final String COLUMN_USER = "ic_user_id";

	private static final String COLUMN_CHIP_NUMBER = "chip_number";
	private static final String COLUMN_RACE_NUMBER = "race_number";
	private static final String COLUMN_RACE_VEHICLE = "race_vehicle_id";
	private static final String COLUMN_SPONSORS = "sponsors";
	private static final String COLUMN_PAY_METHOD = "pay_method";
	private static final String COLUMN_AMOUNT_PAYED = "amount_payed";

	//private static final String COLUMN_RENT_CHIP = "rent_chip";

	private static final String COLUMN_COMMENT = "comment";

	private static final String COLUMN_PARTNER1 = "partner1";
	private static final String COLUMN_PARTNER2 = "partner2";

	private static final String COLUMN_CREATED = "created_date";
	public static final String COLUMN_RENTS_TIME_TRANSMITTER = "RENTS_TIME_TRANSMITTER";

	private static final String COLUMN_PAYMENT_AUTH_CODE = "payment_auth_code";
	
	private static final String COLUMN_FIRST_PARTNER = "first_partner_id";
	private static final String COLUMN_SECOND_PARTNER = "second_partner_id";

	private boolean publishEvent = Boolean.TRUE;

	public ParticipantBMPBean() {
		super();
	}

	public ParticipantBMPBean(int id) throws SQLException {
		super(id);
	}

	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());

		addManyToOneRelationship(COLUMN_SEASON, "Season", Group.class);
		addManyToOneRelationship(COLUMN_RACE, "Race", Group.class);
		addManyToOneRelationship(COLUMN_EVENT, "Event", Group.class);
		addManyToOneRelationship(COLUMN_USER, "User ID", User.class);

		addAttribute(COLUMN_CHIP_NUMBER, "Chip Number", true, true, String.class);
		addManyToOneRelationship(COLUMN_RACE_VEHICLE, RaceVehicleType.class);
		addAttribute(COLUMN_RACE_NUMBER, "Race vehicle", true, true, String.class);
		addAttribute(COLUMN_SPONSORS, "Sponsors", true, true, String.class);
		addAttribute(COLUMN_PAY_METHOD, "Pay method", true, true, String.class);
		addAttribute(COLUMN_AMOUNT_PAYED, "Amount payed", true, true, String.class);
		//addAttribute(COLUMN_RENT_CHIP, "Rent chip", Boolean.class);
		addAttribute(COLUMN_COMMENT, "Comment", String.class, 1000);

		addAttribute(COLUMN_PARTNER1, "Partner 1", String.class);
		addAttribute(COLUMN_PARTNER2, "Partner 2", String.class);
		addAttribute(COLUMN_RENTS_TIME_TRANSMITTER, COLUMN_RENTS_TIME_TRANSMITTER, Boolean.class);

		addAttribute(COLUMN_CREATED, "Created date", Timestamp.class);
		addAttribute(COLUMN_PAYMENT_AUTH_CODE, "Payment authorization code", String.class);
		
		addOneToOneRelationship(COLUMN_FIRST_PARTNER, User.class);
		addOneToOneRelationship(COLUMN_SECOND_PARTNER, User.class);
	}

	@Override
	public void setRentsTimeTransmitter(boolean rents){
		setColumn(COLUMN_RENTS_TIME_TRANSMITTER, rents);
	}

	@Override
	public boolean isRentsTimeTransmitter(){
		return getBooleanColumnValue(COLUMN_RENTS_TIME_TRANSMITTER);
	}

	public static String getEntityTableName() {
		return ENTITY_NAME;
	}


	@Override
	public String getEntityName() {
		return getEntityTableName();
	}

	//GET

	@Override
	public int getSeasonGroupID() {
		return getIntColumnValue(COLUMN_SEASON);
	}


	@Override
	public Group getSeasonGroup() {
		return (Group) getColumnValue(COLUMN_SEASON);
	}


	@Override
	public int getRaceGroupID() {
		return getIntColumnValue(COLUMN_RACE);
	}


	@Override
	public Group getRaceGroup() {
		return (Group) getColumnValue(COLUMN_RACE);
	}


	@Override
	public int getEventGroupID() {
		return getIntColumnValue(COLUMN_EVENT);
	}


	@Override
	public RaceEvent getRaceEvent() {
		int distanceGroupId = getIntColumnValue(COLUMN_EVENT);
		RaceEventHome dHome;
		try {
			dHome = (RaceEventHome) getIDOHome(RaceEvent.class);
			return dHome.findByPrimaryKey(new Integer(distanceGroupId));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public User getUser() {
		return (User) getColumnValue(COLUMN_USER);
	}


	@Override
	public int getUserID() {
		return getIntColumnValue(COLUMN_USER);
	}


	@Override
	public String getChipNumber() {
		return getStringColumnValue(COLUMN_CHIP_NUMBER);
	}


	@Override
	public String getRaceNumber() {
		return getStringColumnValue(COLUMN_RACE_NUMBER);
	}


	@Override
	public RaceVehicleType getRaceVehicle() {
		return (RaceVehicleType) getColumnValue(COLUMN_RACE_VEHICLE);
	}

	@Override
	public String getVehicle() {
		RaceUserSettingsHome home = null;
		try {
			home = (RaceUserSettingsHome) IDOLookup.getHome(RaceUserSettings.class);
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING, "Failed to get " + RaceUserSettingsHome.class + " cause of: ", e);
		}

		if (home != null) {
			RaceUserSettings settings = null;
			try {
				settings = home.findByUser(getUser());
			} catch (FinderException e) {
				getLogger().log(Level.WARNING, "Failed to get " + RaceUserSettings.class);
			}

			if (settings != null) {
				StringBuilder sb = new StringBuilder();
				if (settings.getVehicleType() != null) {
					sb.append(settings.getVehicleType().getLocalizationKey());
					sb.append(CoreConstants.SPACE);
				}

				if (settings.getEngineCC() != null) {
					sb.append(settings.getEngineCC());
				}

				if (settings.getVehicleSubType() != null) {
					sb.append(settings.getVehicleSubType().getLocalizationKey());
					sb.append(CoreConstants.SPACE);
				}

				return sb.toString();
			}
		}

		return null;
	}

	@Override
	public String getSponsors() {
		return getStringColumnValue(COLUMN_SPONSORS);
	}


	@Override
	public String getPayMethod() {
		return getStringColumnValue(COLUMN_PAY_METHOD);
	}


	@Override
	public String getPayedAmount() {
		return getStringColumnValue(COLUMN_AMOUNT_PAYED);
	}

/*	public boolean getRentChip() {
		return getBooleanColumnValue(COLUMN_RENT_CHIP);
	}*/


	@Override
	public String getComment() {
		return getStringColumnValue(COLUMN_COMMENT);
	}


	@Override
	public String getPartner1() {
		return getStringColumnValue(COLUMN_PARTNER1);
	}


	@Override
	public String getPartner2() {
		return getStringColumnValue(COLUMN_PARTNER2);
	}


	@Override
	public Timestamp getCreatedDate() {
		return getTimestampColumnValue(COLUMN_CREATED);
	}
	
	@Override
	public User getFirstPartner() {
		return (User) getColumnValue(COLUMN_FIRST_PARTNER);
	}
	
	@Override
	public User getSecondPartner() {
		return (User) getColumnValue(COLUMN_SECOND_PARTNER);
	}
	
	//SET

	@Override
	public void setSeasonGroupID(int seasonGroupID) {
		setColumn(COLUMN_SEASON, seasonGroupID);
	}


	@Override
	public void setSeasonGroup(Group seasonGroup) {
		setColumn(COLUMN_SEASON, seasonGroup);
	}


	@Override
	public void setRaceGroupID(int raceGroupID) {
		setColumn(COLUMN_RACE, raceGroupID);
	}


	@Override
	public void setRaceGroup(Group raceGroup) {
		setColumn(COLUMN_RACE, raceGroup);
	}


	@Override
	public void setEventGroupID(int eventGroupID) {
		setColumn(COLUMN_EVENT, eventGroupID);
	}


	@Override
	public void setEventGroup(Group eventGroup) {
		setColumn(COLUMN_EVENT, eventGroup);
	}


	@Override
	public void setUserID(int userID) {
		setColumn(COLUMN_USER, userID);
	}


	@Override
	public void setUser(User user) {
		setColumn(COLUMN_USER, user);
	}


	@Override
	public void setChipNumber(String chipNumber) {
		setColumn(COLUMN_CHIP_NUMBER, chipNumber);
	}


	@Override
	public void setRaceNumber(String raceNumber) {
		setColumn(COLUMN_RACE_NUMBER, raceNumber);
	}


	@Override
	public void setRaceVehicle(RaceVehicleType vehicle) {
		setColumn(COLUMN_RACE_VEHICLE, vehicle);
	}


	@Override
	public void setSponsors(String sponsors) {
		setColumn(COLUMN_SPONSORS, sponsors);
	}


	@Override
	public void setPayMethod(String payMethod) {
		setColumn(COLUMN_PAY_METHOD, payMethod);
	}


	@Override
	public void setPayedAmount(String amount) {
		setColumn(COLUMN_AMOUNT_PAYED, amount);
	}

	/*public void setRentChip(boolean rentChip) {
		setColumn(COLUMN_RENT_CHIP, rentChip);
	}*/


	@Override
	public void setComment(String comment) {
		setColumn(COLUMN_COMMENT, comment);
	}


	@Override
	public void setPartner1(String partner1) {
		setColumn(COLUMN_PARTNER1, partner1);
	}


	@Override
	public void setPartner2(String partner2) {
		setColumn(COLUMN_PARTNER2, partner2);
	}


	@Override
	public void setCreatedDate(Timestamp created) {
		setColumn(COLUMN_CREATED, created);
	}

	@Override
	public void setFirstPartner(User firstPartner) {
		setColumn(COLUMN_FIRST_PARTNER, firstPartner);
	}
	
	@Override
	public void setSecondPartner(User secondPartner) {
		setColumn(COLUMN_SECOND_PARTNER, secondPartner);
	}
	
	public Collection<Integer> ejbFindAll() throws FinderException {
		Table table = new Table(this);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());

		return idoFindPKsBySQL(query.toString());
	}

	public Collection<Integer> ejbFindAllByRace(Group race) throws FinderException {
		Table table = new Table(this);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table, COLUMN_RACE, MatchCriteria.EQUALS, race));
		query.addOrder(table, COLUMN_EVENT, true);

		return idoFindPKsBySQL(query.toString());
	}


	@Override
	public String toString() {
		return "ID: " + getPrimaryKey();
	}

	@Override
	public void setNotification(boolean publishEvent) {
		this.publishEvent = publishEvent;
	}

	@Override
	public void store() throws IDOStoreException {
		super.store();

		if (this.publishEvent) {
			ParticipantUpdatedAction event = new ParticipantUpdatedAction(this);
			ELUtil.getInstance().publishEvent(event);
		}
	}

	@Override
	public void remove() throws RemoveException {
		ParticipantUpdatedAction event = new ParticipantUpdatedAction(this);
		event.setRemoved(Boolean.TRUE);

		super.remove();

		if (this.publishEvent) {
			ELUtil.getInstance().publishEvent(event);
		}
	}

	public Collection<Integer> ejbFindAll(Integer raceId, Integer userId) {
		if (raceId == null || userId == null) {
			return Collections.emptyList();
		}

		StringBuilder query = new StringBuilder("SELECT p.MSI_PARTICIPANT_ID ");
		query.append("FROM MSI_PARTICIPANT p ");

		if (raceId != null) {
			query.append("WHERE p.GRP_RACE = ").append(raceId).append(" ");
		}

		if (userId != null) {
			if (query.indexOf("WHERE") > 0) {
				query.append("AND ");
			} else {
				query.append("WHERE ");
			}

			query.append("p.IC_USER_ID = ").append(userId);
		}

		try {
			return idoFindPKsBySQL(query.toString());
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, "Failed to get primary keys by query: " + query.toString());
		}

		return Collections.emptyList();
	}

	public Collection<Integer> ejbFindByDates(String from, String to) {
		if (from == null && to == null) {
			return Collections.emptyList();
		}

		StringBuilder query = new StringBuilder("SELECT p.MSI_PARTICIPANT_ID ");
		query.append("FROM MSI_PARTICIPANT p ");

		if (from != null) {
			query.append("WHERE p.").append(COLUMN_CREATED).append(" > '").append(from).append("' ");
		}
		if (to != null) {
			query.append(from == null ? " where " : "and ").append("p.").append(COLUMN_CREATED).append(" < '").append(to).append("'");
		}

		try {
			return idoFindPKsBySQL(query.toString());
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, "Failed to get primary keys by query: " + query.toString());
		}

		return Collections.emptyList();
	}
	
	public Collection<Integer> ejbFindAll(String userId, String from, String to, String authCode) {
		if (StringUtil.isEmpty(from) && StringUtil.isEmpty(to)) {
			return Collections.emptyList();
		}

		StringBuilder query = new StringBuilder("SELECT p.MSI_PARTICIPANT_ID ");
		query.append("FROM MSI_PARTICIPANT p ");

		if (!StringUtil.isEmpty(from)) {
			query.append("WHERE p.").append(COLUMN_CREATED).append(" > '").append(from).append("' ");
		}
		
		if (!StringUtil.isEmpty(to)) {
			query.append(StringUtil.isEmpty(from) ? " WHERE " : "AND ").append("p.").append(COLUMN_CREATED).append(" < '").append(to).append("'");
		}
		
		if (!StringUtil.isEmpty(userId)) {
			query.append(" AND ").append("p.").append(COLUMN_USER).append(" = ").append(userId);
		}
		
		if (StringUtil.isEmpty(authCode)) {
			query.append(" AND ").append("p.").append(COLUMN_PAYMENT_AUTH_CODE).append(" IS NULL ");
		} else {
			query.append(" AND ").append("p.").append(COLUMN_PAYMENT_AUTH_CODE).append(" = '").append(authCode).append("'");;
		}

		try {
			return idoFindPKsBySQL(query.toString());
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, "Failed to get primary keys by query: " + query.toString());
		}

		return Collections.emptyList();
	}

	@Override
	public String getPaymentAuthCode() {
		return getStringColumnValue(COLUMN_PAYMENT_AUTH_CODE);
	}

	@Override
	public void setPaymentAuthCode(String paymentAuthCode) {
		setValue(COLUMN_PAYMENT_AUTH_CODE, paymentAuthCode);
	}
	
}