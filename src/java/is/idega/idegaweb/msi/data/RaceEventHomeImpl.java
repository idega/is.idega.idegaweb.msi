package is.idega.idegaweb.msi.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;

public class RaceEventHomeImpl extends IDOFactory implements RaceEventHome {
	@Override
	public Class getEntityInterfaceClass() {
		return RaceEvent.class;
	}

	public RaceEvent create() throws CreateException {
		return (RaceEvent) super.createIDO();
	}

	public RaceEvent findByPrimaryKey(Object pk) throws FinderException {
		return (RaceEvent) super.findByPrimaryKeyIDO(pk);
	}
}