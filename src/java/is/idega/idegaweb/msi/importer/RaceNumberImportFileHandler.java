package is.idega.idegaweb.msi.importer;

import java.rmi.RemoteException;
import java.util.List;

import com.idega.block.importer.business.ImportFileHandler;
import com.idega.block.importer.data.ImportFile;
import com.idega.business.IBOService;
import com.idega.user.data.Group;

public interface RaceNumberImportFileHandler extends IBOService, ImportFileHandler {

	/**
	 * @see is.idega.idegaweb.egov.parking.business.importer.TicketOfficerImportFileHandlerBean#handleRecords
	 */
	public boolean handleRecords() throws RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.parking.business.importer.TicketOfficerImportFileHandlerBean#setImportFile
	 */
	public void setImportFile(ImportFile file) throws RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.parking.business.importer.TicketOfficerImportFileHandlerBean#setRootGroup
	 */
	public void setRootGroup(Group rootGroup) throws RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.parking.business.importer.TicketOfficerImportFileHandlerBean#getFailedRecords
	 */
	public List getFailedRecords() throws RemoteException, RemoteException;
	
}