package is.idega.idegaweb.msi.data;


import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface RaceHome extends IDOHome {
	public Race create() throws CreateException;

	public Race findByPrimaryKey(Object pk) throws FinderException;
}