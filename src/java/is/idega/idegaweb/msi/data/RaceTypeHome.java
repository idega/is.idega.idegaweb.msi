package is.idega.idegaweb.msi.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface RaceTypeHome extends IDOHome {
	public RaceType create() throws CreateException;

	public RaceType findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAll() throws FinderException;

	public RaceType findByRaceType(String raceType) throws FinderException;
}