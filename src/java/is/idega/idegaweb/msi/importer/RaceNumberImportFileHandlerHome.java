package is.idega.idegaweb.msi.importer;

import java.rmi.RemoteException;

import javax.ejb.CreateException;

import com.idega.business.IBOHome;

public interface RaceNumberImportFileHandlerHome extends IBOHome {

	public RaceNumberImportFileHandler create() throws CreateException, RemoteException;
	
}