package is.idega.idegaweb.msi.importer;

import javax.ejb.CreateException;

import com.idega.business.IBOHomeImpl;

public class RaceNumberImportFileHandlerHomeImpl extends IBOHomeImpl implements RaceNumberImportFileHandlerHome {

	private static final long serialVersionUID = 7881750711518396563L;

	public Class getBeanInterfaceClass() {
		return RaceNumberImportFileHandler.class;
	}

	public RaceNumberImportFileHandler create() throws CreateException {
		return (RaceNumberImportFileHandler) super.createIBO();
	}

}