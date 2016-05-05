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

	public static final String COLUMN_RACE_NUMBER_MX = "race_number_mx";

	public static final String COLUMN_RACE_NUMBER_SNOCROSS = "race_number_snocross";

	private static final String COLUMN_VEHICLE_TYPE = "vehicle_type_id_new";

	private static final String COLUMN_VEHICLE_SUBTYPE = "vehicle_subtype_id_new";

	private static final String COLUMN_ENGINE = "engine_id";

	private static final String COLUMN_ENGINE_CC = "engine_cc_id";

	private static final String COLUMN_MODEL = "model";

	private static final String COLUMN_TRANSPONDER = "transponder";

	private static final String COLUMN_TEAM = "team";

	private static final String COLUMN_SPONSOR = "sponsor";

	private static final String COLUMN_BODY_NUMBER = "body_number";

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	@Override
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
	@Override
	public User getUser() {
		return (User) getColumnValue(COLUMN_USER);
	}

	@Override
	public String getHomePage() {
		return getStringColumnValue(COLUMN_HOME_PAGE);
	}

	@Override
	public RaceNumber getRaceNumberMX() {
		return (RaceNumber) getColumnValue(COLUMN_RACE_NUMBER_MX);
	}

	@Override
	public RaceNumber getRaceNumberSnocross() {
		return (RaceNumber) getColumnValue(COLUMN_RACE_NUMBER_SNOCROSS);
	}

	@Override
	public RaceVehicleType getVehicleType() {
		return (RaceVehicleType) getColumnValue(COLUMN_VEHICLE_TYPE);
	}

	@Override
	public RaceVehicleType getVehicleSubType() {
		return (RaceVehicleType) getColumnValue(COLUMN_VEHICLE_SUBTYPE);
	}

	@Override
	public String getEngine() {
		return getStringColumnValue(COLUMN_ENGINE);
	}

	@Override
	public String getEngineCC() {
		return getStringColumnValue(COLUMN_ENGINE_CC);
	}

	@Override
	public String getModel() {
		return getStringColumnValue(COLUMN_MODEL);
	}

	@Override
	public String getTransponderNumber() {
		return getStringColumnValue(COLUMN_TRANSPONDER);
	}

	@Override
	public String getTeam() {
		return getStringColumnValue(COLUMN_TEAM);
	}

	@Override
	public String getSponsor() {
		return getStringColumnValue(COLUMN_SPONSOR);
	}

	@Override
	public String getBodyNumber() {
		return getStringColumnValue(COLUMN_BODY_NUMBER);
	}

	//setters
	@Override
	public void setUser(User user) {
		setColumn(COLUMN_USER, user);
	}

	@Override
	public void setHomePage(String homePage) {
		setColumn(COLUMN_HOME_PAGE, homePage);
	}

	@Override
	public void setRaceNumberMX(RaceNumber raceNumber) {
		setColumn(COLUMN_RACE_NUMBER_MX, raceNumber);
	}

	@Override
	public void setRaceNumberMX(int raceNumberID) {
		setColumn(COLUMN_RACE_NUMBER_MX, raceNumberID);
	}

	@Override
	public void setRaceNumberSnocross(RaceNumber raceNumber) {
		setColumn(COLUMN_RACE_NUMBER_SNOCROSS, raceNumber);
	}

	@Override
	public void setRaceNumberSnocross(int raceNumberID) {
		setColumn(COLUMN_RACE_NUMBER_SNOCROSS, raceNumberID);
	}

	@Override
	public void setVehicleType(RaceVehicleType vehicleType) {
		setColumn(COLUMN_VEHICLE_TYPE, vehicleType);
	}

	@Override
	public void setVehicleSubType(RaceVehicleType subType) {
		setColumn(COLUMN_VEHICLE_SUBTYPE, subType);
	}

	@Override
	public void setEngine(String engine) {
		setColumn(COLUMN_ENGINE, engine);
	}

	@Override
	public void setEngineCC(String cc) {
		setColumn(COLUMN_ENGINE_CC, cc);
	}

	@Override
	public void setModel(String model) {
		setColumn(COLUMN_MODEL, model);
	}

	@Override
	public void setTransponder(String transponder) {
		setColumn(COLUMN_TRANSPONDER, transponder);
	}

	@Override
	public void setTeam(String team) {
		setColumn(COLUMN_TEAM, team);
	}

	@Override
	public void setSponsor(String sponsor) {
		setColumn(COLUMN_SPONSOR, sponsor);
	}

	@Override
	public void setBodyNumber(String bodyNumber) {
		setColumn(COLUMN_BODY_NUMBER, bodyNumber);
	}

	//ejb
	public Object ejbFindByUser(User user) throws FinderException {
		Table table = new Table(this);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());

		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_USER), MatchCriteria.EQUALS, user));

		return idoFindOnePKByQuery(query);
	}

	public Object ejbFindByMXRaceNumber(RaceNumber number) throws FinderException {
		Table table = new Table(this);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());

		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_RACE_NUMBER_MX), MatchCriteria.EQUALS, number));

		return idoFindOnePKByQuery(query);
	}

	public Object ejbFindBySnocrossRaceNumber(RaceNumber number) throws FinderException {
		Table table = new Table(this);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());

		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_RACE_NUMBER_SNOCROSS), MatchCriteria.EQUALS, number));

		return idoFindOnePKByQuery(query);
	}
}