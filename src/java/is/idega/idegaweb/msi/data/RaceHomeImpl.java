package is.idega.idegaweb.msi.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;

public class RaceHomeImpl extends IDOFactory implements RaceHome {
	public Class getEntityInterfaceClass() {
		return Race.class;
	}

	public Race create() throws CreateException {
		return (Race) super.createIDO();
	}

	public Race findByPrimaryKey(Object pk) throws FinderException {
		return (Race) super.findByPrimaryKeyIDO(pk);
	}
}