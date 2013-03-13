package is.idega.idegaweb.msi.data;


import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class RaceCategoryHomeImpl extends IDOFactory implements
		RaceCategoryHome {
	public Class getEntityInterfaceClass() {
		return RaceCategory.class;
	}

	public RaceCategory create() throws CreateException {
		return (RaceCategory) super.createIDO();
	}

	public RaceCategory findByPrimaryKey(Object pk) throws FinderException {
		return (RaceCategory) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((RaceCategoryBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}