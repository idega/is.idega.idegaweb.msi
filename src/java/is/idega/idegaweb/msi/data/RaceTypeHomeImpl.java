package is.idega.idegaweb.msi.data;


import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class RaceTypeHomeImpl extends IDOFactory implements RaceTypeHome {
	public Class getEntityInterfaceClass() {
		return RaceType.class;
	}

	public RaceType create() throws CreateException {
		return (RaceType) super.createIDO();
	}

	public RaceType findByPrimaryKey(Object pk) throws FinderException {
		return (RaceType) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((RaceTypeBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public RaceType findByRaceType(String raceType) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((RaceTypeBMPBean) entity).ejbFindByRaceType(raceType);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}