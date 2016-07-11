package is.idega.idegaweb.msi.listeners;

import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceNumberHome;
import is.idega.idegaweb.msi.data.RaceType;
import is.idega.idegaweb.msi.data.RaceTypeHome;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.data.IDOLookup;
import com.idega.util.ListUtil;

public class GenerateNumbersListener implements ActionListener {

	private RaceTypeHome getRaceTypeHome() {
		try {
			return (RaceTypeHome) IDOLookup.getHome(RaceType.class);
		} catch (RemoteException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get " + RaceTypeHome.class + " cause of: ", e);
		}

		return null;
	}

	private RaceNumberHome getRaceNumberHome() {
		try {
			return (RaceNumberHome) IDOLookup.getHome(RaceNumber.class);
		} catch (RemoteException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get " + RaceNumberHome.class + " cause of: ", e);
		}

		return null;
	}

	public void actionPerformed(ActionEvent event) {
		if (!MSIConstants.NUMBERS_GENAROTOR_LISTENER_EVENT.equals(event.getActionCommand())) {
			return;
		}

		try {
			Collection<RaceType> types = getRaceTypeHome().findAll();
			if (ListUtil.isEmpty(types)){
				return;
			}

			for (RaceType type : types) {
				int count = getRaceNumberHome().countAllNotInUseByType(type);
				if(count > 200){
					continue;
				}

				getLogger().info("Generating numbers for " + type.getRaceType());
				int maxValue = getRaceNumberHome().getMaxNumberByType(type);
				for (int i = 0; i < 300; i++) {
					getRaceNumberHome().update(null, ++maxValue, null, null, 
							Boolean.FALSE, Boolean.FALSE, type);
				}
			}
			
		} catch(Exception e) {
			getLogger().log(Level.WARNING, "Error faled generating numbers for races", e);
		}
	}

	private Logger getLogger(){
		return Logger.getLogger(GenerateNumbersListener.class.getName());
	}

}
