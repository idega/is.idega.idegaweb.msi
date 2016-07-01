package is.idega.idegaweb.msi.data;


import is.idega.idegaweb.msi.events.RaceUpdatedAction;

import java.sql.Timestamp;

import com.idega.data.IDOHome;

public interface RaceHome extends IDOHome {

	/**
	 * 
	 * @return entity or <code>null</code> on failure;
	 */
	public Race create();

	/**
	 * 
	 * @param pk is {@link Race#getPrimaryKey()}, not <code>null</code>
	 * @return entity or <code>null</code> on failure;
	 */
	public Race findByPrimaryKey(Object pk);

	/**
	 * 
	 * <p>Creates/updates entity</p>
	 * @param primaryKey is {@link Race#getPrimaryKey()}, 
	 * new one is created if <code>null</code>
	 * @param seasonId is {@link Season#getPrimaryKey()}, 
	 * not <code>null</code> if new entity is created;
	 * @param name is {@link Race#getName()}, skipped if <code>null</code>;
	 * @param description is {@link Race#getDescription()}, skipped if <code>null</code>;
	 * @param date is {@link Race#getRaceDate()}, skipped if <code>null</code>;
	 * @param lastRegistrationDate is {@link Race#getLastRegistrationDate()}, skipped if <code>null</code>;
	 * @param lastPayedRegistrationDate is {@link Race#getLastRegistrationDatePrice1()}, skipped if <code>null</code>;
	 * @param type is {@link RaceType#getPrimaryKey()}, skipped if <code>null</code>;
	 * @param category is {@link RaceCategory#getCategoryKey()}, skipped if <code>null</code>;
	 * @param chipRentPrice is {@link Race#getChipRent()}, skipped if <code>null</code>;
	 * @param publishEvent true, when there is new to create {@link RaceUpdatedAction}
	 * @return entity or <code>null</code> on failure;
	 */
	Race update(
			Integer primaryKey,
			Integer seasonId,
			String name,
			String description,
			Timestamp date,
			Timestamp lastRegistrationDate,
			Timestamp lastPayedRegistrationDate,
			String type,
			String category,
			Float chipRentPrice,
			boolean publishEvent);
}