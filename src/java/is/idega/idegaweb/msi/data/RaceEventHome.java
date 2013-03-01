package is.idega.idegaweb.msi.data;


import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface RaceEventHome extends IDOHome {
	public RaceEvent create() throws CreateException;

	public RaceEvent findByPrimaryKey(Object pk) throws FinderException;
}