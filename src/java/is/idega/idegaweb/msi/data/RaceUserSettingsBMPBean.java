package is.idega.idegaweb.msi.data;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.user.data.User;

public class RaceUserSettingsBMPBean extends GenericEntity implements
		RaceUserSettings {

	private static final String ENTITY_NAME = "msi_user_settings";
	
	private static final String COLUMN_USER = "user_id";
	
	private static final String COLUMN_HOME_PAGE = "home_page";

	private static final String COLUMN_RACE_NUMBER_MX = "race_number_mx";

	private static final String COLUMN_RACE_NUMBER_SNOCROSS = "race_number_snocross";

	private static final String COLUMN_VEHICLE_TYPE = "vehicle_type_id_new";

	private static final String COLUMN_VEHICLE_SUBTYPE = "vehicle_subtype_id_new";
	
	private static final String COLUMN_ENGINE = "engine_id";
	
	private static final String COLUMN_ENGINE_CC = "engine_cc_id";
	
	private static final String COLUMN_MODEL = "model";
	
	private static final String COLUMN_TRANSPONDER = "transponder";
	
	private static final String COLUMN_TEAM = "team";
	
	private static final String COLUMN_SPONSOR = "sponsor";
	
	private static final String COLUMN_BODY_NUMBER = "body_number";
		
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());

		addOneToOneRelationship(COLUMN_USER, User.class);
		addAttribute(COLUMN_HOME_PAGE, "Home page", String.class);
		addOneToOneRelationship(COLUMN_RACE_NUMBER_MX, RaceNumber.class);
		addOneToOneRelationship(COLUMN_RACE_NUMBER_SNOCROSS, RaceNumber.class);
		addManyToOneRelationship(COLUMN_VEHICLE_TYPE, RaceVehicleType.class);
		addManyToOneRelationship(COLUMN_VEHICLE_SUBTYPE, RaceVehicleType.class);
		addAttribute(COLUMN_ENGINE, "Engine", String.class);
		addAttribute(COLUMN_ENGINE_CC, "CC", String.class);
		addAttribute(COLUMN_MODEL, "Model", String.class);
		addAttribute(COLUMN_TRANSPONDER, "Transponder", String.class);
		addAttribute(COLUMN_TEAM, "Team", String.class);
		addAttribute(COLUMN_SPONSOR, "Sponsor", String.class);
		addAttribute(COLUMN_BODY_NUMBER, "Body number", String.class);
	}

	//getters
	public User getUser() {
		return (User) getColumnValue(COLUMN_USER);
	}
	
	public String getHomePage() {
		return getStringColumnValue(COLUMN_HOME_PAGE);
	}
	
	public RaceNumber getRaceNumberMX() {
		return (RaceNumber) getColumnValue(COLUMN_RACE_NUMBER_MX);
	}

	public RaceNumber getRaceNumberSnocross() {
		return (RaceNumber) getColumnValue(COLUMN_RACE_NUMBER_SNOCROSS);
	}

	public RaceVehicleType getVehicleType() {
		return (RaceVehicleType) getColumnValue(COLUMN_VEHICLE_TYPE);
	}
	
	public RaceVehicleType getVehicleSubType() {
		return (RaceVehicleType) getColumnValue(COLUMN_VEHICLE_SUBTYPE);
	}
	
	public String getEngine() {
		return getStringColumnValue(COLUMN_ENGINE);
	}
	
	public String getEngineCC() {
		return getStringColumnValue(COLUMN_ENGINE_CC);
	}
	
	public String getModel() {
		return getStringColumnValue(COLUMN_MODEL);
	}
	
	public String getTransponderNumber() {
		return getStringColumnValue(COLUMN_TRANSPONDER);
	}

	public String getTeam() {
		return getStringColumnValue(COLUMN_TEAM);
	}
	
	public String getSponsor() {
		return getStringColumnValue(COLUMN_SPONSOR);
	}
	
	public String getBodyNumber() {
		return getStringColumnValue(COLUMN_BODY_NUMBER);
	}
	
	//setters
	public void setUser(User user) {
		setColumn(COLUMN_USER, user);
	}
	
	public void setHomePage(String homePage) {
		setColumn(COLUMN_HOME_PAGE, homePage);
	}
	
	public void setRaceNumberMX(RaceNumber raceNumber) {
		setColumn(COLUMN_RACE_NUMBER_MX, raceNumber);
	}

	public void setRaceNumberMX(int raceNumberID) {
		setColumn(COLUMN_RACE_NUMBER_MX, raceNumberID);
	}
	
	public void setRaceNumberSnocross(RaceNumber raceNumber) {
		setColumn(COLUMN_RACE_NUMBER_SNOCROSS, raceNumber);
	}

	public void setRaceNumberSnocross(int raceNumberID) {
		setColumn(COLUMN_RACE_NUMBER_SNOCROSS, raceNumberID);
	}

	public void setVehicleType(RaceVehicleType vehicleType) {
		setColumn(COLUMN_VEHICLE_TYPE, vehicleType);
	}
	
	public void setVehicleSubType(RaceVehicleType subType) {
		setColumn(COLUMN_VEHICLE_SUBTYPE, subType);
	}
	
	public void setEngine(String engine) {
		setColumn(COLUMN_ENGINE, engine);
	}
	
	public void setEngineCC(String cc) {
		setColumn(COLUMN_ENGINE_CC, cc);
	}
	
	public void setModel(String model) {
		setColumn(COLUMN_MODEL, model);
	}
	
	public void setTransponder(String transponder) {
		setColumn(COLUMN_TRANSPONDER, transponder);
	}
	
	public void setTeam(String team) {
		setColumn(COLUMN_TEAM, team);
	}
	
	public void setSponsor(String sponsor) {
		setColumn(COLUMN_SPONSOR, sponsor);
	}
	
	public void setBodyNumber(String bodyNumber) {
		setColumn(COLUMN_BODY_NUMBER, bodyNumber);
	}
	
	//ejb
	public Object ejbFindByUser(User user) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_USER), MatchCriteria.EQUALS, user));
		
		System.out.println("sql = " + query.toString());

		return idoFindOnePKByQuery(query);
	}
	
	public Object ejbFindByMXRaceNumber(RaceNumber number) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_RACE_NUMBER_MX), MatchCriteria.EQUALS, number));
		
		System.out.println("sql = " + query.toString());

		return idoFindOnePKByQuery(query);
	}

	public Object ejbFindBySnocrossRaceNumber(RaceNumber number) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_RACE_NUMBER_SNOCROSS), MatchCriteria.EQUALS, number));
		
		System.out.println("sql = " + query.toString());

		return idoFindOnePKByQuery(query);
	}
}