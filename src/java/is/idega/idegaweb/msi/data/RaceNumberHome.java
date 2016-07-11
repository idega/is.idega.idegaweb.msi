package is.idega.idegaweb.msi.data;


import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.ejb.FinderException;

import com.idega.data.IDOHome;

public interface RaceNumberHome extends IDOHome {

	/**
	 * 
	 * @return created entity or <code>null</code> on failure;
	 */
	public RaceNumber create();

	/**
	 * 
	 * @param pk is {@link RaceNumber#getPrimaryKey()}, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	public RaceNumber findByPrimaryKey(Object pk);

	public Collection findAll() throws FinderException;

	public Collection<RaceNumber> findAllNotInUseByType(RaceType raceType)
			throws FinderException;
	public int countAllNotInUseByType(RaceType raceType);
	public int getMaxNumberByType(RaceType raceType);
	public Collection getMxInUseWithoutUser(int start, int max);
	public Collection getSnocrossInUseWithoutUser(int start, int max);

	/**
	 * 
	 * @param raceType to search by, not <code>null</code>;
	 * @return entities of {@link Collections#emptyList()} on failure;
	 */
	public Collection<RaceNumber> findAllByType(RaceType raceType);

	/**
	 * 
	 * @param raceNumber is {@link RaceNumber#getPrimaryKey()}, not <code>null</code>;
	 * @param raceType is {@link RaceType}, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	public RaceNumber findByRaceNumber(int raceNumber, RaceType raceType);

	/**
	 * 
	 * @param raceNumber is {@link RaceNumber#getPrimaryKey()}, not <code>null</code>;
	 * @param raceTypeId is {@link RaceType#getPrimaryKey()}, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	RaceNumber findByRaceNumber(Integer raceNumber, Integer raceTypeId);

	/**
	 * 
	 * @param primaryKey is {@link RaceNumber#getPrimaryKey()}, new is created when <code>null</code>;
	 * @param raceNumber is {@link RaceNumber#getRaceNumber()}, skipped if <code>null</code>;
	 * @param applicationDate is {@link RaceNumber#getApplicationDate()}, skipped if <code>null</code>;
	 * @param approvalDate is {@link RaceNumber#getApprovedDate()}, skipped if <code>null</code>;
	 * @param approved is {@link RaceNumber#getIsApproved()}, skipped if <code>null</code>;
	 * @param inUse is {@link RaceNumber#getIsInUse()}, skipped if <code>null</code>;
	 * @param raceTypeId is {@link RaceType#getPrimaryKey()}, skipped if <code>null</code>;
	 * @return created or updated entity or <code>null</code> on failure;
	 */
	RaceNumber update(Integer primaryKey, Integer raceNumber,
			Date applicationDate, Date approvalDate, Boolean approved,
			Boolean inUse, Integer raceTypeId);

	/**
	 * 
	 * @param primaryKey is {@link RaceNumber#getPrimaryKey()}, new is created when <code>null</code>;
	 * @param raceNumber is {@link RaceNumber#getRaceNumber()}, skipped if <code>null</code>;
	 * @param applicationDate is {@link RaceNumber#getApplicationDate()}, skipped if <code>null</code>;
	 * @param approvalDate is {@link RaceNumber#getApprovedDate()}, skipped if <code>null</code>;
	 * @param approved is {@link RaceNumber#getIsApproved()}, skipped if <code>null</code>;
	 * @param inUse is {@link RaceNumber#getIsInUse()}, skipped if <code>null</code>;
	 * @param raceType skipped if <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 */
	RaceNumber update(Integer primaryKey, Integer raceNumber,
			Date applicationDate, Date approvalDate, Boolean approved,
			Boolean inUse, RaceType raceType);
}