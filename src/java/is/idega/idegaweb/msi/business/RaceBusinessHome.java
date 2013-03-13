package is.idega.idegaweb.msi.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface RaceBusinessHome extends IBOHome {
	public RaceBusiness create() throws CreateException, RemoteException;
}