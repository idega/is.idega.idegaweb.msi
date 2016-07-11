package is.idega.idegaweb.msi.data;


import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;
import com.idega.user.data.User;

public class RaceUserSettingsHomeImpl extends IDOFactory implements
		RaceUserSettingsHome {
	public Class getEntityInterfaceClass() {
		return RaceUserSettings.class;
	}

	public RaceUserSettings create() throws CreateException {
		return (RaceUserSettings) super.createIDO();
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsHome#findByPrimaryKey(java.lang.Object)
	 */
	@Override
	public RaceUserSettings findByPrimaryKey(Object pk) {
		if (pk != null) {
			try {
				return (RaceUserSettings) super.findByPrimaryKeyIDO(pk);
			} catch (FinderException e) {
				getLog().log(Level.WARNING, "Failed to get entity by primary key: " + pk);
			}
		}

		return null;
	}

	public RaceUserSettings findByUser(User user) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((RaceUserSettingsBMPBean) entity).ejbFindByUser(user);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsHome#findByMXRaceNumber(is.idega.idegaweb.msi.data.RaceNumber)
	 */
	@Override
	public RaceUserSettings findByMXRaceNumber(RaceNumber number) {
		RaceUserSettingsBMPBean entity = (RaceUserSettingsBMPBean) idoCheckOutPooledEntity();
		return findByPrimaryKey(entity.ejbFindByMXRaceNumber(number));
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