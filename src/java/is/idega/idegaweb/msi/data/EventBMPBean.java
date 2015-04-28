package is.idega.idegaweb.msi.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.query.Criteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.OR;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;

public class EventBMPBean extends GenericEntity implements Event {

	private static final String ENTITY_NAME = "msi_event";

	private static final String COLUMN_EVENT_NAME = "name";
	public static final String COLUMN_EVENT_VALID = "IS_VALID";

	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void initializeAttributes() {
		addAttribute(COLUMN_EVENT_NAME, "name", String.class, 1000);
		setAsPrimaryKey(COLUMN_EVENT_NAME, true);
		addAttribute(COLUMN_EVENT_VALID, COLUMN_EVENT_VALID, Boolean.class);
	}

	public Class getPrimaryKeyClass() {
		return String.class;
	}

	public String getIDColumnName() {
		return COLUMN_EVENT_NAME;
	}

	//getters
	public String getName() {
		return getStringColumnValue(COLUMN_EVENT_NAME);
	}
	
	//setters
	public void setName(String name) {
		setColumn(COLUMN_EVENT_NAME, name);
	}
	
	public Collection ejbFindAll() throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addOrder(table, COLUMN_EVENT_NAME, true);
		
		return idoFindPKsBySQL(query.toString());
	}
	
	public Collection getValidEvents() throws FinderException{
		SelectQuery query = idoSelectQuery();
		query.addCriteria(getValidCriteria());
		return idoFindPKsByQuery(query);
	}
	
	public Collection getInvalidEvents() throws FinderException{
		SelectQuery query = idoSelectQuery();
		query.addCriteria( new MatchCriteria(idoQueryTable(), COLUMN_EVENT_VALID, MatchCriteria.EQUALS, false));
		return idoFindPKsByQuery(query);
	}
	private Criteria getValidCriteria() {
		MatchCriteria c1 = new MatchCriteria(idoQueryTable(), COLUMN_EVENT_VALID, MatchCriteria.EQUALS, true);
		MatchCriteria c2 = new MatchCriteria(idoQueryTable(), COLUMN_EVENT_VALID, MatchCriteria.IS, MatchCriteria.NULL);
		return new OR(c1, c2);
	}
	public void setDefaultValues() {
		// TODO Auto-generated method stub
		super.setDefaultValues();
	}
	
	public boolean isValid(){
		return getBooleanColumnValue(COLUMN_EVENT_VALID,true);
	}
	
	public void setValid(boolean valid){
		setColumn(COLUMN_EVENT_VALID, valid);
	}
}