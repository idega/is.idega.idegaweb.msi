package is.idega.idegaweb.msi.data;


import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class RaceNumberHomeImpl extends IDOFactory implements RaceNumberHome {
	public Class getEntityInterfaceClass() {
		return RaceNumber.class;
	}

	public RaceNumber create() throws CreateException {
		return (RaceNumber) super.createIDO();
	}

	public RaceNumber findByPrimaryKey(Object pk) throws FinderException {
		return (RaceNumber) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((RaceNumberBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllNotInUseByType(RaceType raceType)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((RaceNumberBMPBean) entity)
				.ejbFindAllNotInUseByType(raceType);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllByType(RaceType raceType) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((RaceNumberBMPBean) entity)
				.ejbFindAllByType(raceType);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public RaceNumber findByRaceNumber(int raceNumber, RaceType raceType)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((RaceNumberBMPBean) entity).ejbFindByRaceNumber(
				raceNumber, raceType);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}