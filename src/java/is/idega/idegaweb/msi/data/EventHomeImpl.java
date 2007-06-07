package is.idega.idegaweb.msi.data;


import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class EventHomeImpl extends IDOFactory implements EventHome {
	public Class getEntityInterfaceClass() {
		return Event.class;
	}

	public Event create() throws CreateException {
		return (Event) super.createIDO();
	}

	public Event findByPrimaryKey(Object pk) throws FinderException {
		return (Event) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((EventBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}