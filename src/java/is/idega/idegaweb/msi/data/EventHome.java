package is.idega.idegaweb.msi.data;


import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;

public interface EventHome extends IDOHome {
	public Event create() throws CreateException;

	public Event findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAll() throws FinderException;
	public Collection getInvalidEvents();
}