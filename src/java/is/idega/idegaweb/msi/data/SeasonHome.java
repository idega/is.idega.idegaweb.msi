package is.idega.idegaweb.msi.data;


import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface SeasonHome extends IDOHome {
	public Season create() throws CreateException;

	public Season findByPrimaryKey(Object pk) throws FinderException;
}