package is.idega.idegaweb.msi.data;


import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOFactory;
import com.idega.data.IDOStoreException;
import com.idega.util.StringUtil;

public class EventHomeImpl extends IDOFactory implements EventHome {

	private static final long serialVersionUID = -2001639907426771158L;

	public Class getEntityInterfaceClass() {
		return Event.class;
	}

	public Event create() {
		try {
			return (Event) super.createIDO();
		} catch (CreateException e) {
			getLog().log(Level.WARNING, "Failed to create entity, cause of: ", e);
		}

		return null;
	}

	public Event findByPrimaryKey(String pk) {
		try {
			return (Event) super.findByPrimaryKeyIDO(pk);
		} catch (FinderException e) {
			getLog().log(Level.WARNING, "Failed to get entity by primary key: " + pk);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.EventHome#findAll()
	 */
	@Override
	public Collection<Event> findAll() {
		EventBMPBean entity = (EventBMPBean) idoCheckOutPooledEntity();

		Collection<String> primaryKeys = entity.getValidEvents();
		try {
			return getEntityCollectionForPrimaryKeys(primaryKeys);
		} catch (FinderException e) {
			getLog().log(Level.WARNING, 
					"Failed to get entities by priamary keys: " + primaryKeys);
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.EventHome#getInvalidEvents()
	 */
	@Override
	public Collection<Event> getInvalidEvents() {
		Collection<String> primaryKeys = ((EventBMPBean) idoCheckOutPooledEntity())
				.getInvalidEvents();

		try {
			return getEntityCollectionForPrimaryKeys(primaryKeys);
		} catch (FinderException e) {
			getLog().log(Level.WARNING, "Failed to get entities by priamary keys: " + primaryKeys);
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.EventHome#update(java.lang.String, java.lang.Boolean)
	 */
	@Override
	public Event update(String name, Boolean valid, boolean publishEvent) {
		Event entity = findByPrimaryKey(name);
		if (entity == null) {
			entity = create();
		}

		if (!StringUtil.isEmpty(name)) {
			entity.setName(name);
		}

		if (valid != null) {
			entity.setValid(valid);
		}

		entity.setNotification(publishEvent);

		try {
			entity.store();
			return entity;
		} catch (IDOStoreException e) {
			getLog().log(Level.WARNING, "Failed to store entity, cause of: ", e);
			return null;
		}
	}
}