package is.idega.idegaweb.msi.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.user.data.User;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class RaceUserSettingsHomeImpl extends IDOFactory implements
		RaceUserSettingsHome {
	public Class getEntityInterfaceClass() {
		return RaceUserSettings.class;
	}

	public RaceUserSettings create() throws CreateException {
		return (RaceUserSettings) super.createIDO();
	}

	public RaceUserSettings findByPrimaryKey(Object pk) throws FinderException {
		return (RaceUserSettings) super.findByPrimaryKeyIDO(pk);
	}

	public RaceUserSettings findByUser(User user) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((RaceUserSettingsBMPBean) entity).ejbFindByUser(user);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public RaceUserSettings findByMXRaceNumber(RaceNumber number)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((RaceUserSettingsBMPBean) entity)
				.ejbFindByMXRaceNumber(number);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public RaceUserSettings findBySnocrossRaceNumber(RaceNumber number)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((RaceUserSettingsBMPBean) entity)
				.ejbFindBySnocrossRaceNumber(number);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}