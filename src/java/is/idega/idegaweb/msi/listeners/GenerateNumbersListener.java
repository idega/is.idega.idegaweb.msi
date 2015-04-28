package is.idega.idegaweb.msi.listeners;

import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceNumberHome;
import is.idega.idegaweb.msi.data.RaceType;
import is.idega.idegaweb.msi.data.RaceTypeHome;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.data.IDOLookup;

public class GenerateNumbersListener implements ActionListener {

	public void actionPerformed(ActionEvent event) {
		if (!MSIConstants.NUMBERS_GENAROTOR_LISTENER_EVENT.equals(event.getActionCommand())) {
			return;
		}
		try {
			RaceTypeHome typeHome = (RaceTypeHome) IDOLookup.getHome(RaceType.class);
			RaceNumberHome home = (RaceNumberHome) IDOLookup.getHome(RaceNumber.class);
			Collection types = typeHome.findAll();
			if((types == null)|| (types.size() < 1)){
				return;
			}
			Iterator it = types.iterator();
			while (it.hasNext()) {
				RaceType type = (RaceType) it.next();
				int count = home.countAllNotInUseByType(type);
				if(count > 200){
					continue;
				}
				getLogger().info("Generating numbers for " + type.getRaceType());
				int maxValue = home.getMaxNumberByType(type);
				for (int i = 0; i < 300; i++) {
					RaceNumber number = home.create();
					number.setRaceNumber(Integer.toString(++maxValue));
					number.setRaceType(type);
					number.setIsApproved(false);
					number.setIsInUse(false);
					number.store();
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
