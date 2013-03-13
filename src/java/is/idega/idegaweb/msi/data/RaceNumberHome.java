package is.idega.idegaweb.msi.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface RaceNumberHome extends IDOHome {
	public RaceNumber create() throws CreateException;

	public RaceNumber findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAll() throws FinderException;

	public Collection findAllNotInUseByType(RaceType raceType)
			throws FinderException;

	public Collection findAllByType(RaceType raceType) throws FinderException;

	public RaceNumber findByRaceNumber(int raceNumber, RaceType raceType)
			throws FinderException;
}