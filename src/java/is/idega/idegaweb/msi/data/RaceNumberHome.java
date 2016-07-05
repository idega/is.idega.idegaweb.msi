package is.idega.idegaweb.msi.data;


import java.util.Collection;
import java.util.Collections;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;

public interface RaceNumberHome extends IDOHome {
	public RaceNumber create() throws CreateException;

	public RaceNumber findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAll() throws FinderException;

	public Collection findAllNotInUseByType(RaceType raceType)
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

	public RaceNumber findByRaceNumber(int raceNumber, RaceType raceType)
			throws FinderException;
}