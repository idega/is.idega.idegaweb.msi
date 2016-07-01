package is.idega.idegaweb.msi.data;

import is.idega.idegaweb.msi.events.RaceCategoryUpdatedAction;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOStoreException;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class RaceCategoryBMPBean extends GenericEntity implements RaceCategory {

	private static final long serialVersionUID = 4830920477452826594L;

	private static final String ENTITY_NAME = "msi_race_category";

	private static final String COLUMN_CATEGORY_KEY = "category_key";

	private boolean publishEvent = Boolean.FALSE;
	
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

	public Collection<String> ejbFindAll() {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addOrder(table, COLUMN_CATEGORY_KEY, true);
		
		try {
			return idoFindPKsBySQL(query.toString());
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get primary keys by query: " + query.toString());
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.data.GenericEntity#store()
	 */
	@Override
	public void store() throws IDOStoreException {
		super.store();

		if (this.publishEvent) {
			RaceCategoryUpdatedAction event = new RaceCategoryUpdatedAction(this);
			ELUtil.getInstance().publishEvent(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.data.GenericEntity#remove()
	 */
	@Override
	public void remove() throws RemoveException {	
		RaceCategoryUpdatedAction event = new RaceCategoryUpdatedAction(this);
		event.setRemoved(Boolean.TRUE);

		super.remove();

		if (this.publishEvent) {
			ELUtil.getInstance().publishEvent(event);
		}
	}

	/**
	 * 
	 * @param name is {@link RaceCategory#getCategoryKey()}, not <code>null</code>;
	 * @return {@link RaceCategory#getPrimaryKey()} or <code>null</code> on failure;
	 */
	public Integer ebjFindByName(String name) {
		if (!StringUtil.isEmpty(name)) {
			StringBuilder query = new StringBuilder();
			query.append("SELECT mrc.MSI_RACE_CATEGORY_ID FROM msi_race_category mrc ");
			query.append("WHERE mrc.CATEGORY_KEY = '").append(name).append("'");

			try {
				return (Integer) idoFindOnePKBySQL(query.toString());
			} catch (FinderException e) {
				getLogger().log(Level.WARNING, 
						"Failed to execute query: '" + query.toString() + "'");
			}
		}

		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceCategory#setNotification(boolean)
	 */
	@Override
	public void setNotification(boolean publishEvent) {
		this.publishEvent = publishEvent;
	}
}