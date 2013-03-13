package is.idega.idegaweb.msi.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface RaceVehicleTypeHome extends IDOHome {
	public RaceVehicleType create() throws CreateException;

	public RaceVehicleType findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAllParents() throws FinderException;

	public Collection findSubtypeByParent(RaceVehicleType parent)
			throws FinderException;
}