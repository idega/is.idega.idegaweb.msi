package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.events.RaceCategoryUpdatedAction;

import java.util.Collection;
import java.util.Collections;

import com.idega.data.IDOHome;

public interface RaceCategoryHome extends IDOHome {

	/**
	 * 
	 * @return created entity or <code>null</code> on failure;
	 */
	public RaceCategory create();

	/**
	 * 
	 * @param pk is {@link RaceCategory#getPrimaryKey()}, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	public RaceCategory findByPrimaryKey(Object pk);

	/**
	 * 
	 * @param name is {@link RaceCategory#getCategoryKey()}, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	RaceCategory findByName(String name);

	/**
	 * 
	 * @return all entities or {@link Collections#emptyList()} on failure;
	 */
	public Collection<RaceCategory> findAll();

	/**
	 * 
	 * @param primaryKey is {@link RaceCategory#getPrimaryKey()}, 
	 * new entity is created when <code>null</code>;
	 * @param localizationKey is {@link RaceCategory#getCategoryKey()}, 
	 * skipped if <code>null</code>;
	 * @param publishEvent is <code>true</code> 
	 * when {@link RaceCategoryUpdatedAction} should be thrown;
	 * @return entity or <code>null</code> on failure;
	 */
	RaceCategory update(Integer primaryKey, String localizationKey, boolean publishEvent);
}