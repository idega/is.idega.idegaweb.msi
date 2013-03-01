package is.idega.idegaweb.msi.data;


import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class RaceVehicleTypeHomeImpl extends IDOFactory implements
		RaceVehicleTypeHome {
	public Class getEntityInterfaceClass() {
		return RaceVehicleType.class;
	}

	public RaceVehicleType create() throws CreateException {
		return (RaceVehicleType) super.createIDO();
	}

	public RaceVehicleType findByPrimaryKey(Object pk) throws FinderException {
		return (RaceVehicleType) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAllParents() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((RaceVehicleTypeBMPBean) entity).ejbFindAllParents();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findSubtypeByParent(RaceVehicleType parent)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((RaceVehicleTypeBMPBean) entity)
				.ejbFindSubtypeByParent(parent);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}