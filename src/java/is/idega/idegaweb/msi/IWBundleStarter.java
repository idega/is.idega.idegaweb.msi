/*
 * $Id: IWBundleStarter.java,v 1.2 2009/01/06 04:06:48 valdas Exp $
 * Created on May 23, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.msi;

import java.io.IOException;
import java.rmi.RemoteException;

import is.idega.idegaweb.msi.util.MSIConstants;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.block.creditcard.data.KortathjonustanMerchant;
import com.idega.block.creditcard.data.KortathjonustanMerchantHome;
import com.idega.business.IBOLookup;
import com.idega.core.business.ICApplicationBindingBusiness;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplication;


/**
 * Last modified: $Date: 2009/01/06 04:06:48 $ by $Author: valdas $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class IWBundleStarter implements IWBundleStartable {
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
	 */
	public void start(IWBundle starterBundle) {
		updateData();
		addStandardViews(starterBundle.getApplication());
		IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
		String merchantPK = null;
		ICApplicationBindingBusiness abb = null;
		try {
			abb= (ICApplicationBindingBusiness) IBOLookup.getServiceInstance(iwac, ICApplicationBindingBusiness.class);
			merchantPK = abb.get(MSIConstants.PROPERTY_MERCHANT_PK);
		
			if (merchantPK == null) {
				merchantPK = starterBundle.getProperty(MSIConstants.PROPERTY_MERCHANT_PK);
			}
			
			try {
				KortathjonustanMerchant merchant = null;
				if (merchantPK == null) {
					merchant = ((KortathjonustanMerchantHome) IDOLookup.getHome(KortathjonustanMerchant.class)).create();
				}
				else {
					merchant = ((KortathjonustanMerchantHome) IDOLookup.getHome(KortathjonustanMerchant.class)).findByPrimaryKey(new Integer(merchantPK));
				}
				
				merchant.setName("MSI.is");
				merchant.store();
				starterBundle.setProperty(MSIConstants.PROPERTY_MERCHANT_PK, merchant.getPrimaryKey().toString());

				abb.put(MSIConstants.PROPERTY_MERCHANT_PK, merchant.getPrimaryKey().toString());
			}
			catch (IDOLookupException ile) {
				ile.printStackTrace();
			}
			catch (FinderException fe) {
				fe.printStackTrace();
			}
			catch (CreateException ce) {
				ce.printStackTrace();
			}	
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
	}

	protected void addStandardViews(IWMainApplication iwma){
		MSIViewManager manager = MSIViewManager.getInstance(iwma);
		manager.getMSIViewNode();
	}

	private void updateData() {
//		GlobalIncludeManager includeManager = GlobalIncludeManager.getInstance();
//		includeManager.addBundleStyleSheet(RaceBlock.IW_BUNDLE_IDENTIFIER, "/style/marathon.css");
	}
}
