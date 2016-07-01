package is.idega.idegaweb.msi.data;

import is.idega.idegaweb.msi.events.EventUpdatedAction;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOStoreException;
import com.idega.data.query.Criteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.OR;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.util.expression.ELUtil;

public class EventBMPBean extends GenericEntity implements Event {

	private static final long serialVersionUID = -8577585923331427323L;

	private boolean publishEvent = Boolean.FALSE;

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

	/*
	 * (non-Javadoc)
	 * @see com.idega.data.GenericEntity#getIDColumnName()
	 */
	@Override
	public String getIDColumnName() {
		return COLUMN_EVENT_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.data.GenericEntity#getName()
	 */
	@Override
	public String getName() {
		return getStringColumnValue(COLUMN_EVENT_NAME);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.data.GenericEntity#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		setColumn(COLUMN_EVENT_NAME, name);
	}

	/**
	 * 
	 * @return {@link Collection} of {@link Event#getName()} of {@link Event}s
	 * or {@link Collections#emptyList()}
	 * on failure;
	 */
	public Collection<String> ejbFindAll() {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addOrder(table, COLUMN_EVENT_NAME, true);
		
		try {
			return idoFindPKsBySQL(query.toString());
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get primary keys by query: " + query);
		}

		return Collections.emptyList();
	}

	/**
	 * 
	 * @return {@link Collection} of {@link Event#getName()} of {@link Event}s
	 * which are {@link Event#isValid()} or {@link Collections#emptyList()}
	 * on failure;
	 */
	public Collection<String> getValidEvents() {
		SelectQuery query = idoSelectQuery();
		query.addCriteria(getValidCriteria());
		try {
			return idoFindPKsByQuery(query);
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get primary keys by query: " + query);
		}

		return Collections.emptyList();
	}

	/**
	 * 
	 * @return {@link Collection} of {@link Event#getName()} of {@link Event}s
	 * which are not {@link Event#isValid()} or {@link Collections#emptyList()}
	 * on failure;
	 */
	public Collection<String> getInvalidEvents() {
		SelectQuery query = idoSelectQuery();
		query.addCriteria( new MatchCriteria(idoQueryTable(), 
				COLUMN_EVENT_VALID, MatchCriteria.EQUALS, false));

		try {
			return idoFindPKsByQuery(query);
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get primary keys by query: " + query);
		}

		return Collections.emptyList();
	}

	private Criteria getValidCriteria() {
		MatchCriteria c1 = new MatchCriteria(idoQueryTable(), COLUMN_EVENT_VALID, MatchCriteria.EQUALS, true);
		MatchCriteria c2 = new MatchCriteria(idoQueryTable(), COLUMN_EVENT_VALID, MatchCriteria.IS, MatchCriteria.NULL);
		return new OR(c1, c2);
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.Event#isValid()
	 */
	@Override
	public boolean isValid(){
		return getBooleanColumnValue(COLUMN_EVENT_VALID,true);
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.Event#setValid(boolean)
	 */
	@Override
	public void setValid(boolean valid){
		setColumn(COLUMN_EVENT_VALID, valid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.data.GenericEntity#store()
	 */
	@Override
	public void store() throws IDOStoreException {
		super.store();

		if (this.publishEvent) {
			EventUpdatedAction event = new EventUpdatedAction(this);
			ELUtil.getInstance().publishEvent(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.data.GenericEntity#remove()
	 */
	@Override
	public void remove() throws RemoveException {
		EventUpdatedAction event = new EventUpdatedAction(this);
		event.setRemoved(Boolean.TRUE);

		super.remove();

		if (this.publishEvent) {
			ELUtil.getInstance().publishEvent(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.Event#setNotification(boolean)
	 */
	@Override
	public void setNotification(boolean publishEvent) {
		this.publishEvent = publishEvent;
	}
}