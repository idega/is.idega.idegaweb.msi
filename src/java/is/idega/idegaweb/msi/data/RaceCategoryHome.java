package is.idega.idegaweb.msi.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface RaceCategoryHome extends IDOHome {
	public RaceCategory create() throws CreateException;

	public RaceCategory findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAll() throws FinderException;
}