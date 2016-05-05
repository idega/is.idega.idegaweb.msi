/*
 * $Id: IWBundleStarter.java,v 1.3 2008/02/01 17:55:32 palli Exp $
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
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.block.creditcard.data.KortathjonustanMerchant;
import com.idega.block.creditcard.data.KortathjonustanMerchantHome;
import com.idega.block.importer.data.ImportHandler;
import com.idega.block.importer.data.ImportHandlerHome;
import com.idega.business.IBOLookup;
import com.idega.core.business.ICApplicationBindingBusiness;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.EventTimer;
import com.idega.util.ListUtil;

import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceNumberHome;
import is.idega.idegaweb.msi.importer.RaceNumberImportFileHandler;
import is.idega.idegaweb.msi.listeners.GenerateNumbersListener;
import is.idega.idegaweb.msi.util.MSIConstants;


/**
 * Last modified: $Date: 2008/02/01 17:55:32 $ by $Author: palli $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public class IWBundleStarter implements IWBundleStartable {
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
	 */
	public void start(IWBundle starterBundle) {
		registerImporters(starterBundle);
		
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
		fixRaceNumbers(starterBundle);

	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
	}

	private Logger getLogger(){
		return Logger.getLogger(IWBundleStarter.class.getName());
	}
	protected void addStandardViews(IWMainApplication iwma){
		MSIViewManager manager = MSIViewManager.getInstance(iwma);
		manager.getMSIViewNode();
	}

	private void addListeners(IWBundle starterBundle){
		EventTimer timer = new EventTimer(EventTimer.THREAD_SLEEP_24_HOURS, MSIConstants.NUMBERS_GENAROTOR_LISTENER_EVENT);
		GenerateNumbersListener listener = new GenerateNumbersListener();
		timer.addActionListener(listener);
		timer.start();
	}
	
	private void fixRaceNumbers(IWBundle starterBundle){
		try{
			IWMainApplication iwma = starterBundle.getApplication();
			IWMainApplicationSettings settings = iwma.getSettings();
			if(settings.getBoolean("race_numbers_in_use_fixed", false)){
				return;
			}
			SimpleQuerier.executeUpdate("ALTER TABLE msi_race_number CHANGE COLUMN MSI_RACE_NUMBER_ID MSI_RACE_NUMBER_ID INT(11) NOT NULL AUTO_INCREMENT;", false);
			RaceNumberHome home = (RaceNumberHome) IDOLookup.getHome(RaceNumber.class);
			int start = 0;
			int max = 100;
			for(Collection numbers = home.getMxInUseWithoutUser(start, max);!ListUtil.isEmpty(numbers);
					start = start + max,
					numbers = home.getMxInUseWithoutUser(start, max)){
				for(Iterator iter = numbers.iterator();iter.hasNext();){
					RaceNumber raceNumber = (RaceNumber) iter.next();
					raceNumber.setApplicationDate(null);
					raceNumber.setApprovedDate(null);
					raceNumber.setIsInUse(false);
					raceNumber.setIsApproved(false);
					raceNumber.store();
				}
			}
			start = 0;
			for(Collection numbers = home.getSnocrossInUseWithoutUser(start, max);!ListUtil.isEmpty(numbers);
					start = start + max,
					numbers = home.getSnocrossInUseWithoutUser(start, max)){
				for(Iterator iter = numbers.iterator();iter.hasNext();){
					RaceNumber raceNumber = (RaceNumber) iter.next();
					raceNumber.setApplicationDate(null);
					raceNumber.setApprovedDate(null);
					raceNumber.setIsInUse(false);
					raceNumber.setIsApproved(false);
					raceNumber.store();
				}
			}
			settings.setProperty("race_numbers_in_use_fixed", Boolean.TRUE.toString());
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed fixing numbers", e);
		}
	}
	
	private void updateData() {
	}
	
	private void registerImporters(IWBundle bundle) {
		try {
			ImportHandlerHome home = (ImportHandlerHome) IDOLookup.getHome(ImportHandler.class);
			try {
				home.findByClassName(RaceNumberImportFileHandler.class.getName());
			} catch (FinderException fe) {
				try {
					ImportHandler handler = home.create();
					handler.setName("Race numbers");
					handler.setDescription("Race numbers import handler (excel files).");
					handler.setClassName(RaceNumberImportFileHandler.class.getName());
					handler.store();
				} catch (CreateException ce) {
					ce.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
