package is.idega.idegaweb.msi.data;


import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.idega.data.IDOHome;
import com.idega.user.data.Group;

public interface SeasonHome extends IDOHome {

	/**
	 * 
	 * @return created entity or <code>null</code> on failure;
	 */
	public Season create();

	/**
	 * 
	 * @param pk is {@link Season#getPrimaryKey()}, not <code>null</code>;
	 * @return entity or <code>null</code> failure;
	 */
	public Season findByPrimaryKey(Object pk);

	/**
	 * 
	 * @param date to get {@link Season} for, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	Season getSeason(Date date);
	
	/**
	 * 
	 * @return entity for current date;
	 */
	Season getCurrentSeason();

	/**
	 * 
	 * @param seasonId is {@link Season#getPrimaryKey()}. 
	 * When it is <code>null</code> then new entity is created;
	 * @param seasonName is {@link Season#getName()}, skipped if <code>null</code>;
	 * @param description is {@link Season#getDescription()}, skipped if <code>null</code>;
	 * @param begin is {@link Season#getSeasonBeginDate()}, skipped if <code>null</code>;
	 * @param end is {@link Season#getSeasonEndDate()}, skipped if <code>null</code>;
	 * @return created/updated entity or <code>null</code> on failure.
	 */
	Season update(
			Integer seasonId, 
			String seasonName, 
			String description,
			Timestamp begin, 
			Timestamp end);

	/**
	 * 
	 * @param name is {@link Season#getName()}, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	Season findByName(String name);

	/**
	 * 
	 * @param seasonId is {@link Season#getPrimaryKey()}. 
	 * When it is <code>null</code> then new entity is created;
	 * @param seasonName is {@link Season#getName()}, skipped if <code>null</code>;
	 * @param description is {@link Season#getDescription()}, skipped if <code>null</code>;
	 * @param begin is {@link Season#getSeasonBeginDate()}, skipped if <code>null</code>;
	 * @param end is {@link Season#getSeasonEndDate()}, skipped if <code>null</code>;
	 * @return created/updated entity or <code>null</code> on failure.
	 */
	Season update(
			Integer seasonId, 
			String seasonName, 
			String description,
			Date begin,
			Date end);

	/**
	 * 
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	Collection<Season> findAll();

	/**
	 * 
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	Collection<Group> findAllGroups();
}