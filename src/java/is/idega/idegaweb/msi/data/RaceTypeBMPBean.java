package is.idega.idegaweb.msi.data;

import is.idega.idegaweb.msi.util.MSIConstants;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;

public class RaceTypeBMPBean extends GenericEntity implements RaceType {

	private static final String ENTITY_NAME = "msi_race_type";
	private static final String COLUMN_RACE_TYPE = "race_type";
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_RACE_TYPE, "Race type", String.class);
	}

	public void insertStartData() throws Exception {
	    RaceTypeHome home = (RaceTypeHome) IDOLookup.getHome(RaceType.class);

	    RaceType type = home.create();
	    type.setRaceType(MSIConstants.RACE_TYPE_MX_AND_ENDURO);
	    type.store();
	    
	    type= home.create();
	    type.setRaceType(MSIConstants.RACE_TYPE_SNOCROSS);
	    type.store();
	}

	// getters
	public String getRaceType() {
		return getStringColumnValue(COLUMN_RACE_TYPE);
	}
	
	//setters
	public void setRaceType(String raceType) {
		setColumn(COLUMN_RACE_TYPE, raceType);
	}
	
	//ejb
	public Collection ejbFindAll() throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addOrder(table, COLUMN_RACE_TYPE, true);
		
		return idoFindPKsBySQL(query.toString());
	}
	
	public Object ejbFindByRaceType(String raceType) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());

		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_RACE_TYPE), MatchCriteria.EQUALS, raceType));
		
		System.out.println("sql = " + query.toString());
		
		return idoFindOnePKByQuery(query);
	}
}