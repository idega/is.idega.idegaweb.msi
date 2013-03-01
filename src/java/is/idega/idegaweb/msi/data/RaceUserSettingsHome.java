package is.idega.idegaweb.msi.data;


import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;
import com.idega.user.data.User;

public interface RaceUserSettingsHome extends IDOHome {
	public RaceUserSettings create() throws CreateException;

	public RaceUserSettings findByPrimaryKey(Object pk) throws FinderException;

	public RaceUserSettings findByUser(User user) throws FinderException;

	public RaceUserSettings findByMXRaceNumber(RaceNumber number)
			throws FinderException;

	public RaceUserSettings findBySnocrossRaceNumber(RaceNumber number)
			throws FinderException;
}