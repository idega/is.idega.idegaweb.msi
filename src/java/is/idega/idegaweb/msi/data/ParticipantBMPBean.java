/*
 * Created on Jul 6, 2004
 */
package is.idega.idegaweb.msi.data;

import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 * Description: <br>
 * Copyright: Idega Software 2004 <br>
 * Company: Idega Software <br>
 * 
 * @author birna
 */
public class ParticipantBMPBean extends GenericEntity implements Participant {

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
	
	private static final String COLUMN_RENT_CHIP = "rent_chip";
	
	public ParticipantBMPBean() {
		super();
	}

	public ParticipantBMPBean(int id) throws SQLException {
		super(id);
	}

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
		addAttribute(COLUMN_RENT_CHIP, "Rent chip", Boolean.class);
	}

	public static String getEntityTableName() {
		return ENTITY_NAME;
	}

	public String getEntityName() {
		return getEntityTableName();
	}

	//GET
	public int getSeasonGroupID() {
		return getIntColumnValue(COLUMN_SEASON);
	}
	
	public Group getSeasonGroup() {
		return (Group) getColumnValue(COLUMN_SEASON);
	}

	public int getRaceGroupID() {
		return getIntColumnValue(COLUMN_RACE);
	}

	public Group getRaceGroup() {
		return (Group) getColumnValue(COLUMN_RACE);
	}

	public int getEventGroupID() {
		return getIntColumnValue(COLUMN_EVENT);
	}

	public RaceEvent getRaceEvent() {
		int distanceGroupId = getIntColumnValue(COLUMN_EVENT);
		RaceEventHome dHome;
		try {
			dHome = (RaceEventHome) getIDOHome(RaceEvent.class);
			return (RaceEvent) dHome.findByPrimaryKey(new Integer(distanceGroupId));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public User getUser() {
		return (User) getColumnValue(COLUMN_USER);
	}

	public int getUserID() {
		return getIntColumnValue(COLUMN_USER);
	}

	public String getChipNumber() {
		return getStringColumnValue(COLUMN_CHIP_NUMBER);
	}
	
	public String getRaceNumber() {
		return getStringColumnValue(COLUMN_RACE_NUMBER);
	}

	public RaceVehicleType getRaceVehicle() {
		return (RaceVehicleType) getColumnValue(COLUMN_RACE_VEHICLE);
	}

	public String getSponsors() {
		return getStringColumnValue(COLUMN_SPONSORS);
	}

	public String getPayMethod() {
		return getStringColumnValue(COLUMN_PAY_METHOD);
	}
	
	public String getPayedAmount() {
		return getStringColumnValue(COLUMN_AMOUNT_PAYED);
	}
	
	public boolean getRentChip() {
		return getBooleanColumnValue(COLUMN_RENT_CHIP);
	}

	//SET
	public void setSeasonGroupID(int seasonGroupID) {
		setColumn(COLUMN_SEASON, seasonGroupID);
	}

	public void setSeasonGroup(Group seasonGroup) {
		setColumn(COLUMN_SEASON, seasonGroup);
	}

	public void setRaceGroupID(int raceGroupID) {
		setColumn(COLUMN_RACE, raceGroupID);
	}

	public void setRaceGroup(Group raceGroup) {
		setColumn(COLUMN_RACE, raceGroup);
	}

	public void setEventGroupID(int eventGroupID) {
		setColumn(COLUMN_EVENT, eventGroupID);
	}

	public void setEventGroup(Group eventGroup) {
		setColumn(COLUMN_EVENT, eventGroup);
	}

	public void setUserID(int userID) {
		setColumn(COLUMN_USER, userID);
	}

	public void setUser(User user) {
		setColumn(COLUMN_USER, user);
	}

	public void setChipNumber(String chipNumber) {
		setColumn(COLUMN_CHIP_NUMBER, chipNumber);
	}
	
	public void setRaceNumber(String raceNumber) {
		setColumn(COLUMN_RACE_NUMBER, raceNumber);
	}

	public void setRaceVehicle(RaceVehicleType vehicle) {
		setColumn(COLUMN_RACE_VEHICLE, vehicle);
	}

	public void setSponsors(String sponsors) {
		setColumn(COLUMN_SPONSORS, sponsors);
	}

	public void setPayMethod(String payMethod) {
		setColumn(COLUMN_PAY_METHOD, payMethod);
	}
	
	public void setPayedAmount(String amount) {
		setColumn(COLUMN_AMOUNT_PAYED, amount);
	}
	
	public void setRentChip(boolean rentChip) {
		setColumn(COLUMN_RENT_CHIP, rentChip);
	}
	
	public Collection ejbFindAll() throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		
		return idoFindPKsBySQL(query.toString());
	}
	
	public Collection ejbFindAllByRace(Group race) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table, COLUMN_RACE, MatchCriteria.EQUALS, race));
		query.addOrder(table, COLUMN_EVENT, true);
		
		return idoFindPKsBySQL(query.toString());
	}

	
/*	public int ejbHomeGetNextAvailableParticipantNumber(Object distancePK, int min, int max) throws IDOException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new MaxColumn(getColumnNameParticipantNumber()));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunDistanceGroupID(), MatchCriteria.EQUALS, distancePK));
		query.addCriteria(new MatchCriteria(table, getColumnNameParticipantNumber(), MatchCriteria.GREATEREQUAL, min));
		query.addCriteria(new MatchCriteria(table, getColumnNameParticipantNumber(), MatchCriteria.LESSEQUAL, max));
		
		return idoGetNumberOfRecords(query.toString());
	}
	
	public int ejbHomeGetNumberOfParticipantsByDistance(Object distancePK, int min, int max) throws IDOException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new CountColumn(getColumnNameParticipantNumber()));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunDistanceGroupID(), MatchCriteria.EQUALS, distancePK));
		query.addCriteria(new MatchCriteria(table, getColumnNameParticipantNumber(), MatchCriteria.GREATEREQUAL, min));
		query.addCriteria(new MatchCriteria(table, getColumnNameParticipantNumber(), MatchCriteria.LESSEQUAL, max));
		
		return idoGetNumberOfRecords(query.toString());
	}
	
	public int ejbHomeGetCountByDistanceAndNumber(Object distancePK, int number) throws IDOException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new CountColumn(getColumnNameParticipantNumber()));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunDistanceGroupID(), MatchCriteria.EQUALS, distancePK));
		query.addCriteria(new MatchCriteria(table, getColumnNameParticipantNumber(), MatchCriteria.EQUALS, number));
		
		return idoGetNumberOfRecords(query.toString());
	}
	
	public int ejbHomeGetCountByDistanceAndGroupName(Object distancePK, String groupName) throws IDOException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn(table));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunDistanceGroupID(), MatchCriteria.EQUALS, distancePK));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunGroupName(), MatchCriteria.EQUALS, groupName));
		
		return idoGetNumberOfRecords(query);
	}
	
	public Collection ejbFindAllByDistanceAndGroup(Group distance, Group runGroup) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunDistanceGroupID(), MatchCriteria.EQUALS, distance));
		if (runGroup != null) {
			query.addCriteria(new MatchCriteria(table, getColumnNameRunGroupGroupID(), MatchCriteria.EQUALS, runGroup));
		}
		query.addOrder(table, getColumnNameRunTime(), true);
		
		return idoFindPKsBySQL(query.toString());
	}
	
	public Integer ejbFindByUserIDandDistanceID(int userID, int distanceID) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(getColumnNameUserID(),userID);
		query.appendAndEquals(getColumnNameRunDistanceGroupID(),distanceID);
		return (Integer) super.idoFindOnePKByQuery(query);
	}
	
	public Integer ejbFindByDistanceAndParticpantNumber(Object distancePK, int participantNumber) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(getColumnNameRunDistanceGroupID(), distancePK);
		query.appendAndEquals(getColumnNameParticipantNumber(), participantNumber);
		return (Integer) super.idoFindOnePKByQuery(query);
	}
	
	public Integer ejbFindByUserAndRun(User user, Group run, Group year) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(getColumnNameUserID(),user);
		query.appendAndEquals(getColumnNameRunTypeGroupID(),run);
		query.appendAndEquals(getColumnNameRunYearGroupID(),year);
		return (Integer) super.idoFindOnePKByQuery(query);
	}
	
	public Collection ejbFindByUserAndParentGroup(int userID, int runGroupID, int yearGroupID, int distanceGroupID) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(getColumnNameUserID(),userID);
		query.appendAndEquals(getColumnNameRunTypeGroupID(),runGroupID);
//		query.appendAndEquals(getColumnNameRunYearGroupID(),yearGroupID);
		query.appendAndEquals(getColumnNameRunDistanceGroupID(),distanceGroupID);
		return super.idoFindPKsByQuery(query);
	
	}
	public Collection ejbFindByUserID(int userID) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(getColumnNameUserID(),userID);
		return super.idoFindPKsByQuery(query);
	}
	
	public Collection ejbFindAllWithoutChipNumber(int distanceIDtoIgnore) throws FinderException {
		IDOQuery query = idoQueryGetSelect();
		query.appendWhere().append("("+getColumnNameChipNumber()).appendIsNull().appendOr().append(getColumnNameChipNumber()).append("= '')");
		if (distanceIDtoIgnore != -1) {
			query.appendAnd().append(getColumnNameRunDistanceGroupID()).appendNOTEqual().append(distanceIDtoIgnore);
		}
		return super.idoFindPKsByQuery(query);		
	}
	
	public Collection ejbFindAllByRunGroupIdAndYearGroupId(int runId, int yearId) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		//query.appendWhereEquals(getColumnNameUserID(),user);
		query.appendWhereEquals(getColumnNameRunTypeGroupID(),runId);
		query.appendAndEquals(getColumnNameRunYearGroupID(),yearId);
		return super.idoFindPKsByQuery(query);
	}
	
	public Collection ejbFindAllByRunGroupIdAndYear(int runId, int year) throws FinderException{
		
		GroupHome groupHome;
		try {
			groupHome = (GroupHome) getIDOHome(Group.class);
		} catch (IDOLookupException e) {
			throw new RuntimeException(e);
		}
		Group runGroup = groupHome.findByPrimaryKey(new Integer(runId));
		Collection childrenGroups = runGroup.getChildren();
		Integer yearId = new Integer(-1);
		for (Iterator iter = childrenGroups.iterator(); iter.hasNext();) {
			Group childGroup = (Group) iter.next();
			if(childGroup.getName().equals(Integer.toString(year))){
				yearId = (Integer) childGroup.getPrimaryKey();
			}
		}
		
		return ejbFindAllByRunGroupIdAndYearGroupId(runId,yearId.intValue());
	}*/
}