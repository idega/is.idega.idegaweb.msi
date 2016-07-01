package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.events.RaceTypeUpdatedAction;

import java.util.Collection;
import java.util.Collections;

import com.idega.data.IDOHome;

public interface RaceTypeHome extends IDOHome {

	/**
	 * 
	 * @return created entity or <code>null</code> on failure;
	 */
	RaceType create();

	/**
	 * 
	 * @param pk is {@link RaceType#getPrimaryKey()}, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	RaceType findByPrimaryKey(Object pk);

	/**
	 * 
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	Collection<RaceType> findAll();

	/**
	 * 
	 * @param raceType is {@link RaceType#getRaceType()}, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	RaceType findByRaceType(String raceType);

	/**
	 * 
	 * @param primaryKey is {@link RaceType#getPrimaryKey()}, 
	 * new entity is created when <code>null</code>;
	 * @param raceType is {@link RaceType#getRaceType()}, 
	 * skipped if <code>null</code>;
	 * @param notify <code>true</code> if {@link RaceTypeUpdatedAction} should 
	 * be emitted, <code>false</code> otherwise;
	 * @return entity or <code>null</code> on failure;
	 */
	RaceType update(Integer primaryKey, String raceType, boolean notify);
}