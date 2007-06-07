package is.idega.idegaweb.msi.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;

public class SeasonHomeImpl extends IDOFactory implements SeasonHome {
	@Override
	public Class getEntityInterfaceClass() {
		return Season.class;
	}

	public Season create() throws CreateException {
		return (Season) super.createIDO();
	}

	public Season findByPrimaryKey(Object pk) throws FinderException {
		return (Season) super.findByPrimaryKeyIDO(pk);
	}
}