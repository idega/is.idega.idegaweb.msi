package is.idega.idegaweb.msi.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class RaceBusinessHomeImpl extends IBOHomeImpl implements
		RaceBusinessHome {
	public Class getBeanInterfaceClass() {
		return RaceBusiness.class;
	}

	public RaceBusiness create() throws CreateException {
		return (RaceBusiness) super.createIBO();
	}
}