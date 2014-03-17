package is.idega.idegaweb.msi.business;


import javax.ejb.CreateException;
import java.rmi.RemoteException;
import com.idega.business.IBOHome;

public interface RaceBusinessHome extends IBOHome {
	public RaceBusiness create() throws CreateException, RemoteException;
}