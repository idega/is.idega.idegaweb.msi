package is.idega.idegaweb.msi.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;
import com.idega.user.data.User;

public interface RaceUserSettingsHome extends IDOHome {
	public RaceUserSettings create() throws CreateException;

	public RaceUserSettings findByPrimaryKey(Object pk);

	public RaceUserSettings findByUser(User user) throws FinderException;

	public RaceUserSettings findByMXRaceNumber(RaceNumber number);

	public RaceUserSettings findBySnocrossRaceNumber(RaceNumber number)
			throws FinderException;
}