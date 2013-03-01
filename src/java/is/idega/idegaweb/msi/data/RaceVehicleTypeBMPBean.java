package is.idega.idegaweb.msi.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;

public class RaceVehicleTypeBMPBean extends GenericEntity implements
		RaceVehicleType {
	
	private static final String ENTITY_NAME = "msi_vehicle_type";

	private static final String COLUMN_VEHICLE_TYPE_LOC_KEY = "localization_key";
	
	private static final String COLUMN_PARENT = "parent_id";
	
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_VEHICLE_TYPE_LOC_KEY, "Localization key", String.class);
		addManyToOneRelationship(COLUMN_PARENT, "Parent", RaceVehicleType.class);
	}
	
	public void insertStartData() throws Exception {
		RaceVehicleTypeHome home = (RaceVehicleTypeHome) IDOLookup.getHome(RaceVehicleType.class);

		RaceVehicleType type = home.create();
		type.setLocalizationKey("GasGas");
		type.store();
		
		type = home.create();
		type.setLocalizationKey("Husaberg");
		type.store();

		type = home.create();
		type.setLocalizationKey("Kawasaki");
		type.store();

		RaceVehicleType subType = home.create();
		subType.setLocalizationKey("KX");
		subType.setParent(type);
		subType.store();

		subType = home.create();
		subType.setLocalizationKey("KXF");
		subType.setParent(type);
		subType.store();

		type = home.create();
		type.setLocalizationKey("KTM");
		type.store();

		subType = home.create();
		subType.setLocalizationKey("EXC");
		subType.setParent(type);
		subType.store();
		
		subType = home.create();
		subType.setLocalizationKey("XC");
		subType.setParent(type);
		subType.store();

		subType = home.create();
		subType.setLocalizationKey("SX-F");
		subType.setParent(type);
		subType.store();

		subType = home.create();
		subType.setLocalizationKey("SX");
		subType.setParent(type);
		subType.store();

		type = home.create();
		type.setLocalizationKey("Honda");
		type.store();

		subType = home.create();
		subType.setLocalizationKey("CR");
		subType.setParent(type);
		subType.store();

		subType = home.create();
		subType.setLocalizationKey("CRF");
		subType.setParent(type);
		subType.store();

		subType = home.create();
		subType.setLocalizationKey("CRFX");
		subType.setParent(type);
		subType.store();

		type = home.create();
		type.setLocalizationKey("Suzuki");
		type.store();

		subType = home.create();
		subType.setLocalizationKey("RM");
		subType.setParent(type);
		subType.store();

		subType = home.create();
		subType.setLocalizationKey("RMZ");
		subType.setParent(type);
		subType.store();

		type = home.create();
		type.setLocalizationKey("TM");
		type.store();

		subType = home.create();
		subType.setLocalizationKey("Racing");
		subType.setParent(type);
		subType.store();

		type = home.create();
		type.setLocalizationKey("Yamaha");
		type.store();
		
		subType = home.create();
		subType.setLocalizationKey("YZ");
		subType.setParent(type);
		subType.store();
		
		subType = home.create();
		subType.setLocalizationKey("YZF");
		subType.setParent(type);
		subType.store();

		subType = home.create();
		subType.setLocalizationKey("WR");
		subType.setParent(type);
		subType.store();
	}
	
	//getters
	public String getLocalizationKey() {
		return getStringColumnValue(COLUMN_VEHICLE_TYPE_LOC_KEY);
	}
	
	public RaceVehicleType getParent() {
		return (RaceVehicleType) getColumnValue(COLUMN_PARENT);
	}
	
	//setters
	public void setLocalizationKey(String key) {
		setColumn(COLUMN_VEHICLE_TYPE_LOC_KEY, key);
	}
	
	public void setParent(RaceVehicleType parent) {
		setColumn(COLUMN_PARENT, parent);
	}
	
	//ejb
	public Collection ejbFindAllParents() throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_PARENT), false));
					
		System.out.println("sql = " + query.toString());
		
		return idoFindPKsBySQL(query.toString());
	}

	public Collection ejbFindSubtypeByParent(RaceVehicleType parent) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_PARENT), MatchCriteria.EQUALS, parent));
					
		System.out.println("sql = " + query.toString());
		
		return idoFindPKsBySQL(query.toString());
	}
}