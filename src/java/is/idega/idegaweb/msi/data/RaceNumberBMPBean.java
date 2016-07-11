package is.idega.idegaweb.msi.data;

import is.idega.idegaweb.msi.util.MSIConstants;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORelationshipException;
import com.idega.data.query.Column;
import com.idega.data.query.CountColumn;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;

public class RaceNumberBMPBean extends GenericEntity implements RaceNumber {

	private static final long serialVersionUID = 1921008429569608574L;

	private static final String ENTITY_NAME = "msi_race_number";

	private static final String COLUMN_RACE_NUMBER = "race_number";
	private static final String COLUMN_RACE_TYPE = "race_type";
	private static final String COLUMN_IN_USE = "in_use";
	private static final String COLUMN_APPROVED = "approved";
	private static final String COLUMN_APPLICATION_DATE = "application_date";
	private static final String COLUMN_APPROVED_DATE = "approved_date";

	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_RACE_NUMBER, "Race number", String.class);
		addManyToOneRelationship(COLUMN_RACE_TYPE, "Race type", RaceType.class);
		addAttribute(COLUMN_IN_USE, "In use", Boolean.class);
		addAttribute(COLUMN_APPROVED, "Approved", Boolean.class);
		addAttribute(COLUMN_APPLICATION_DATE, "Application date", Timestamp.class);
		addAttribute(COLUMN_APPROVED_DATE, "Approved date", Timestamp.class);
	}

	public void insertStartData() throws Exception {
		RaceTypeHome typeHome = (RaceTypeHome) IDOLookup.getHome(RaceType.class);
		RaceNumberHome home = (RaceNumberHome) IDOLookup.getHome(RaceNumber.class);

		Collection types = typeHome.findAll();
		if (types != null && !types.isEmpty()) {
			Iterator it = types.iterator();
			while (it.hasNext()) {
				RaceType type = (RaceType) it.next();
				for (int i = 0; i < 1000; i++) {
					RaceNumber number = home.create();
					number.setRaceNumber(Integer.toString(i));
					number.setRaceType(type);
					number.setIsApproved(false);
					number.setIsInUse(false);
					number.store();
				}
			}
		}
	}

	//getters
		public String getRaceNumber() {
		return getStringColumnValue(COLUMN_RACE_NUMBER);
	}
	
	public RaceType getRaceType() {
		return (RaceType) getColumnValue(COLUMN_RACE_TYPE);
	}

	public boolean getIsInUse() {
		return getBooleanColumnValue(COLUMN_IN_USE, false);
	}

	public boolean getIsApproved() {
		return getBooleanColumnValue(COLUMN_APPROVED, false);
	}
	
	public Timestamp getApplicationDate() {
		return getTimestampColumnValue(COLUMN_APPLICATION_DATE);
	}
	
	public Timestamp getApprovedDate() {
		return getTimestampColumnValue(COLUMN_APPROVED_DATE);
	}
	
	//setters	
	public void setRaceNumber(String raceNumber) {
		setColumn(COLUMN_RACE_NUMBER, raceNumber);
	}
	
	public void setRaceType(RaceType type) {
		setColumn(COLUMN_RACE_TYPE, type);
	}

	public void setIsInUse(boolean isInUse) {
		setColumn(COLUMN_IN_USE, isInUse);
	}

	public void setIsApproved(boolean isApproved) {
		setColumn(COLUMN_APPROVED, isApproved);
	}
	
	public void setApplicationDate(Timestamp applicationDate) {
		setColumn(COLUMN_APPLICATION_DATE, applicationDate);
	}
	
	public void setApprovedDate(Timestamp approvedDate) {
		setColumn(COLUMN_APPROVED_DATE, approvedDate);
	}
	
	//ejb
	public Collection ejbFindAll() throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		
		return idoFindPKsBySQL(query.toString());
	}

	public Collection ejbFindAllNotInUseByType(RaceType raceType) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_IN_USE), MatchCriteria.EQUALS, false));
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_RACE_TYPE), MatchCriteria.EQUALS, raceType));
					
		return idoFindPKsBySQL(query.toString());
	}
	
	public int countAllNotInUseByType(RaceType raceType) throws IDOException{
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new CountColumn("*"));
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_IN_USE), MatchCriteria.EQUALS, false));
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_RACE_TYPE), MatchCriteria.EQUALS, raceType));
					
		return idoGetNumberOfRecords(query);
	}
	public Object getMaxNumberByType(RaceType raceType) throws FinderException{
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_RACE_TYPE), MatchCriteria.EQUALS, raceType));
		query.addOrder(table, COLUMN_RACE_NUMBER, false);
		return idoFindOnePKByQuery(query);
	}
	public Collection getMxInUseWithoutUser(int start, int max) throws IDOLookupException, FinderException, IDORelationshipException{
		Table table = new Table(this);
		Table raceSettings = new Table(RaceUserSettings.class);
		Table raceType = new Table(RaceType.class);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addLeftJoin(table, getIDColumnName(),raceSettings, RaceUserSettingsBMPBean.COLUMN_RACE_NUMBER_MX);
		query.addJoin(table, raceType);
		query.addCriteria(new MatchCriteria(new Column(raceSettings, RaceUserSettingsBMPBean.COLUMN_RACE_NUMBER_MX), MatchCriteria.IS, MatchCriteria.NULL));
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_IN_USE), MatchCriteria.EQUALS, true));
		query.addCriteria(new MatchCriteria(new Column(raceType, RaceTypeBMPBean.COLUMN_RACE_TYPE), MatchCriteria.EQUALS, MSIConstants.RACE_TYPE_MX_AND_ENDURO));
		return idoFindPKsByQuery(query, max, start);
	}
	public Collection getSnocrossInUseWithoutUser(int start, int max) throws IDOLookupException, FinderException, IDORelationshipException{
		Table table = new Table(this);
		Table raceSettings = new Table(RaceUserSettings.class);
		Table raceType = new Table(RaceType.class);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addLeftJoin(table, getIDColumnName(),raceSettings, RaceUserSettingsBMPBean.COLUMN_RACE_NUMBER_SNOCROSS);
		query.addJoin(table, raceType);
		query.addCriteria(new MatchCriteria(new Column(raceSettings, RaceUserSettingsBMPBean.COLUMN_RACE_NUMBER_SNOCROSS), MatchCriteria.IS, MatchCriteria.NULL));
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_IN_USE), MatchCriteria.EQUALS, true));
		query.addCriteria(new MatchCriteria(new Column(raceType, RaceTypeBMPBean.COLUMN_RACE_TYPE), MatchCriteria.EQUALS, MSIConstants.RACE_TYPE_SNOCROSS));
		return idoFindPKsByQuery(query, max, start);
	}

	/**
	 * 
	 * @param raceType to find by, not <code>null</code>;
	 * @return {@link Collection} of {@link RaceNumber#getPrimaryKey()}s 
	 * or {@link Collections#emptyList()} on failure;
	 */
	public Collection<Integer> ejbFindAllByType(RaceType raceType) {
		Table table = new Table(this);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_RACE_TYPE), MatchCriteria.EQUALS, raceType));

		try {
			return idoFindPKsBySQL(query.toString());
		} catch (FinderException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get primary keys by query: " + query);
		}

		return Collections.emptyList();
	}

	/**
	 * 
	 * @param raceNumber is {@link RaceNumber#getRaceNumber()}, not <code>null</code>;
	 * @param raceTypeId is {@link RaceType#getRaceType()}, not <code>null</code>;
	 * @return {@link RaceNumber#getPrimaryKey()} or <code>null</code> on failure;
	 */
	public Integer ejbFindByRaceNumber(int raceNumber, int raceTypeId) {
		StringBuilder query = new StringBuilder();
		query.append(" SELECT mrn.msi_race_number_id FROM msi_race_number mrn");
		query.append(" WHERE mrn.race_number = ").append(raceNumber);
		query.append(" AND mrn.race_type = ").append(raceTypeId);

		try {
			return (Integer) idoFindOnePKBySQL(query.toString());
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, "Failed to get primary keys by: " + query);
		}

		return null;
	}
}