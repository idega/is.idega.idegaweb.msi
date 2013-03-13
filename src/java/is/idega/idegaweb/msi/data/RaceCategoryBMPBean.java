package is.idega.idegaweb.msi.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;

public class RaceCategoryBMPBean extends GenericEntity implements RaceCategory {

	private static final String ENTITY_NAME = "msi_race_category";
	
	private static final String COLUMN_CATEGORY_KEY = "category_key";
	
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_CATEGORY_KEY, "Category key", String.class);
	}

	//getters
	public String getCategoryKey() {
		return getStringColumnValue(COLUMN_CATEGORY_KEY);
	}
	
	//setters
	public void setCategoryKey(String key) {
		setColumn(COLUMN_CATEGORY_KEY, key);
	}
	
	//ejb
	public Collection ejbFindAll() throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addOrder(table, COLUMN_CATEGORY_KEY, true);
		
		return idoFindPKsBySQL(query.toString());
	}
}