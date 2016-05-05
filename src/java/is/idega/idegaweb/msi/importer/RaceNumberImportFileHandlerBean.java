package is.idega.idegaweb.msi.importer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.block.importer.business.ImportFileHandler;
import com.idega.block.importer.data.ImportFile;
import com.idega.business.IBOLookup;
import com.idega.business.IBOServiceBean;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

import is.idega.idegaweb.msi.business.RaceBusiness;
import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceType;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.util.MSIConstants;

public class RaceNumberImportFileHandlerBean extends IBOServiceBean implements RaceNumberImportFileHandler, ImportFileHandler {

	private static final long serialVersionUID = 8359598651557298882L;

	private ImportFile importFile = null;
	private List failedRecords;
	private List successRecords;

	public boolean handleRecords() throws RemoteException, RemoteException {
		failedRecords = new ArrayList();
		successRecords = new ArrayList();

		UserBusiness userBusiness = (UserBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), UserBusiness.class);
		RaceBusiness raceBusiness = (RaceBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), RaceBusiness.class);

		int counter = 0;
		String record;
		Map importedNumbers = new HashMap();
		IWTimestamp now = IWTimestamp.RightNow();
		while (!(record = (String) this.importFile.getNextRecord()).equals("")) {
			try {
				List values = this.importFile.getValuesFromRecordString(record);
				if (ListUtil.isEmpty(values) || values.size() < 5) {
					getLogger().warning("Invalid record: " + record + ", values: " + values);
					failedRecords.add(record);
					continue;
				}

				String number = (String) values.get(0);
				if (StringUtil.isEmpty(number)) {
					getLogger().warning("Number is unknown: " + record + ", values: " + values);
					failedRecords.add(record);
					continue;
				}

				String personalID = (String) values.get(1);
				if (StringUtil.isEmpty(personalID)) {
					getLogger().warning("Personal ID is unknown: " + record + ", values: " + values);
					failedRecords.add(record);
					continue;
				}
				User user = null;
				try {
					user = userBusiness.getUser(personalID);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (user == null) {
					getLogger().warning("User is unknown for personal ID: " + personalID + ", values: " + values);
					failedRecords.add(record);
					continue;
				}
				RaceUserSettings userSettings = null;
				try {
					userSettings = raceBusiness.getRaceUserSettings(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (userSettings == null) {
					getLogger().warning("User settings not foun for " + user + ", personal ID: " + personalID + ", values: " + values);
					failedRecords.add(record);
					continue;
				}

				String raceTypeValue = (String) values.get(4);
				if (StringUtil.isEmpty(raceTypeValue)) {
					getLogger().warning("Race type is unknown: " + record + ", values: " + values);
					failedRecords.add(record);
					continue;
				}
				RaceType raceType = null;
				try {
					raceType = raceBusiness.getRaceTypeHome().findByRaceType(raceTypeValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (raceType == null) {
					getLogger().warning("Race type is unknown for: " + raceTypeValue + ", values: " + values);
					failedRecords.add(record);
					continue;
				}

				Map numbers = (Map) importedNumbers.get(raceType);
				if (numbers == null) {
					numbers = new HashMap();
					importedNumbers.put(raceType, numbers);
				}

				counter++;
				RaceNumber raceNumber = MSIConstants.RACE_TYPE_MX_AND_ENDURO.equals(raceTypeValue) ? userSettings.getRaceNumberMX() : userSettings.getRaceNumberSnocross();
				if (raceNumber == null) {
					try {
						raceNumber = raceBusiness.getRaceNumberHome().findByRaceNumber(Integer.parseInt(number), raceType);
					} catch (Exception e) {}
					if (raceNumber == null) {
						raceNumber = raceBusiness.getRaceNumberHome().create();
						raceNumber.setRaceType(raceType);
						raceNumber.store();
						getLogger().info("Created new number (" + number + ", type: " + raceTypeValue + ")");
					}

					if (MSIConstants.RACE_TYPE_MX_AND_ENDURO.equals(raceTypeValue)) {
						userSettings.setRaceNumberMX(raceNumber);
					} else {
						userSettings.setRaceNumberSnocross(raceNumber);
					}
					userSettings.store();
					getLogger().info("Assigned new number (" + number + ", type: " + raceTypeValue + ") for " + user);
				}

				if (raceNumber.getApplicationDate() == null) {
					raceNumber.setApplicationDate(now.getTimestamp());
				}
				if (raceNumber.getApprovedDate() == null) {
					raceNumber.setApprovedDate(now.getTimestamp());
				}
				raceNumber.setRaceNumber(number);
				raceNumber.setIsApproved(true);
				raceNumber.setIsInUse(true);
				raceNumber.store();

				numbers.put(number, Boolean.TRUE);
				successRecords.add(record);
			} catch (Exception e) {
				counter--;
				failedRecords.add(record);
				getLogger().log(Level.WARNING, "Error importing record " + counter + ": '" + record + "'", e);
			}
		}

		//	Marking rest of numbers as not in use
		List keys = new ArrayList(importedNumbers.keySet());
		for (int i = 0; i < keys.size(); i++) {
			RaceType raceType = (RaceType) keys.get(i);
			Map numbers = (Map) importedNumbers.get(raceType);
			if (numbers == null || numbers.size() == 0) {
				getLogger().warning("No numbers imported for race type " + raceType.getRaceType());
				continue;
			}

			Collection raceNumbers = null;
			try {
				raceNumbers = raceBusiness.getRaceNumberHome().findAllByType(raceType);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (ListUtil.isEmpty(raceNumbers)) {
				getLogger().warning("No race numbers found in DB for race type " + raceType.getRaceType());
				continue;
			}

			for (Iterator iter = raceNumbers.iterator(); iter.hasNext();) {
				RaceNumber number = (RaceNumber) iter.next();
				if (numbers.containsKey(number.getRaceNumber())) {
					continue;
				}

				RaceUserSettings tmp = null;
				if (MSIConstants.RACE_TYPE_MX_AND_ENDURO.equals(raceType.getRaceType())) {
					try {
						tmp = raceBusiness.getRaceUserSettingsHome().findByMXRaceNumber(number);
					} catch (Exception e) {}
					if (tmp != null) {
						tmp.setRaceNumberMX(null);
					}
				} else {
					try {
						tmp = raceBusiness.getRaceUserSettingsHome().findBySnocrossRaceNumber(number);
					} catch (FinderException e) {}
					if (tmp != null) {
						tmp.setRaceNumberSnocross(null);
					}
				}
				if (tmp != null) {
					tmp.store();
				}

				number.setApplicationDate(null);
				number.setApprovedDate(null);
				number.setIsApproved(false);
				number.setIsInUse(false);
				number.store();
				getLogger().info("Marked number " + number.getRaceNumber() + " as not in use for race type " + raceType.getRaceType() + (tmp == null ? CoreConstants.EMPTY : ", racer: " + tmp.getUser()));
			}
		}

		getLogger().info(counter + " records imported");

		return true;
	}

	public void setImportFile(ImportFile file) throws RemoteException, RemoteException {
		this.importFile = file;
	}

	public void setRootGroup(Group rootGroup) throws RemoteException, RemoteException {
	}

	public List getFailedRecords() throws RemoteException, RemoteException {
		return failedRecords;
	}

	public List getSuccessRecords() throws RemoteException {
		return successRecords;
	}

}