package is.idega.idegaweb.msi.data;


import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class EventHomeImpl extends IDOFactory implements EventHome {
	public Class getEntityInterfaceClass() {
		return Event.class;
	}

	private Logger getLogger(){
		return Logger.getLogger(EventHomeImpl.class.getName());
	}
	public Event create() throws CreateException {
		return (Event) super.createIDO();
	}

	public Event findByPrimaryKey(Object pk) throws FinderException {
		return (Event) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((EventBMPBean) entity).getValidEvents();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	public Collection getInvalidEvents(){
		IDOEntity entity = this.idoCheckOutPooledEntity();
		try {
			Collection ids = ((EventBMPBean) entity).getInvalidEvents();
			this.idoCheckInPooledEntity(entity);
			return this.getEntityCollectionForPrimaryKeys(ids);
		} catch (FinderException e) {
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed finding invalid events",e);
		}
		return Collections.EMPTY_LIST;
	}
}