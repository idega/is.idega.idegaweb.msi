package is.idega.idegaweb.msi.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface EventHome extends IDOHome {
	public Event create() throws CreateException;

	public Event findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAll() throws FinderException;
}