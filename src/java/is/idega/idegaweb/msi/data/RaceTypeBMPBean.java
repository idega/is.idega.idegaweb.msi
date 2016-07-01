package is.idega.idegaweb.msi.data;

import is.idega.idegaweb.msi.events.RaceTypeUpdatedAction;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.data.IDOStoreException;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.util.expression.ELUtil;

public class RaceTypeBMPBean extends GenericEntity implements RaceType {

	private static final long serialVersionUID = -5765075306043919292L;

	private boolean publishEvent = Boolean.FALSE;
	
	private static final String ENTITY_NAME = "msi_race_type";
	public static final String COLUMN_RACE_TYPE = "race_type";
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
	public Collection<Integer> ejbFindAll() {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addOrder(table, COLUMN_RACE_TYPE, true);
		
		try {
			return idoFindPKsBySQL(query.toString());
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get primary keys by query: " + query);
		}

		return Collections.emptyList();
	}
	
	public Object ejbFindByRaceType(String raceType) {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());

		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_RACE_TYPE), MatchCriteria.EQUALS, raceType));
		
		try {
			return idoFindOnePKByQuery(query);
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get primary key by query: " + query);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceType#setNotification(boolean)
	 */
	@Override
	public void setNotification(boolean publishEvent) {
		this.publishEvent = publishEvent;	
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.data.GenericEntity#store()
	 */
	@Override
	public void store() throws IDOStoreException {
		super.store();

		if (this.publishEvent) {
			RaceTypeUpdatedAction event = new RaceTypeUpdatedAction(this);
			ELUtil.getInstance().publishEvent(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.data.GenericEntity#remove()
	 */
	@Override
	public void remove() throws RemoveException {
		RaceTypeUpdatedAction event = new RaceTypeUpdatedAction(this);
		event.setRemoved(Boolean.TRUE);

		super.remove();

		if (this.publishEvent) {
			ELUtil.getInstance().publishEvent(event);
		}
	}
}