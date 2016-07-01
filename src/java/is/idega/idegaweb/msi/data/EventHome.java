package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.events.EventUpdatedAction;

import java.util.Collection;
import java.util.Collections;

import com.idega.data.IDOHome;

public interface EventHome extends IDOHome {
	public Event create();

	public Event findByPrimaryKey(String pk);

	/**
	 * 
	 * @return {@link Collection} of {@link Event}s
	 * which are {@link Event#isValid()} or {@link Collections#emptyList()}
	 * on failure;
	 */
	public Collection<Event> findAll();

	/**
	 * 
	 * @return {@link Collection} of {@link Event}s
	 * which are not {@link Event#isValid()} or {@link Collections#emptyList()}
	 * on failure;
	 */
	public Collection<Event> getInvalidEvents();

	/**
	 * 
	 * @param name is {@link Event#getName()}, not <code>null</code>;
	 * @param valid
	 * @param publishEvent is <code>true</code> 
	 * when {@link EventUpdatedAction} is required
	 * @return entity or <code>null</code> on failure;
	 */
	Event update(String name, Boolean valid, boolean publishEvent);
}