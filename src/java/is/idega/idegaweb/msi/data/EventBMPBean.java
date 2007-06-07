package is.idega.idegaweb.msi.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;

public class EventBMPBean extends GenericEntity implements Event {

	private static final String ENTITY_NAME = "msi_event";

	private static final String COLUMN_EVENT_NAME = "name";

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	@Override
	public void initializeAttributes() {
		addAttribute(COLUMN_EVENT_NAME, "name", String.class, 1000);
		setAsPrimaryKey(COLUMN_EVENT_NAME, true);
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
}