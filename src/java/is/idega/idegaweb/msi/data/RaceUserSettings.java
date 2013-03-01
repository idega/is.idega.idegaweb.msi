package is.idega.idegaweb.msi.data;


import com.idega.user.data.User;
import com.idega.data.IDOEntity;

public interface RaceUserSettings extends IDOEntity {
	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getUser
	 */
	public User getUser();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getHomePage
	 */
	public String getHomePage();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getRaceNumberMX
	 */
	public RaceNumber getRaceNumberMX();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getRaceNumberSnocross
	 */
	public RaceNumber getRaceNumberSnocross();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getVehicleType
	 */
	public RaceVehicleType getVehicleType();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getVehicleSubType
	 */
	public RaceVehicleType getVehicleSubType();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getEngine
	 */
	public String getEngine();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getEngineCC
	 */
	public String getEngineCC();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getModel
	 */
	public String getModel();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getTransponderNumber
	 */
	public String getTransponderNumber();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getTeam
	 */
	public String getTeam();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getSponsor
	 */
	public String getSponsor();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#getBodyNumber
	 */
	public String getBodyNumber();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setUser
	 */
	public void setUser(User user);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setHomePage
	 */
	public void setHomePage(String homePage);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setRaceNumberMX
	 */
	public void setRaceNumberMX(RaceNumber raceNumber);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setRaceNumberMX
	 */
	public void setRaceNumberMX(int raceNumberID);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setRaceNumberSnocross
	 */
	public void setRaceNumberSnocross(RaceNumber raceNumber);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setRaceNumberSnocross
	 */
	public void setRaceNumberSnocross(int raceNumberID);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setVehicleType
	 */
	public void setVehicleType(RaceVehicleType vehicleType);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setVehicleSubType
	 */
	public void setVehicleSubType(RaceVehicleType subType);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setEngine
	 */
	public void setEngine(String engine);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setEngineCC
	 */
	public void setEngineCC(String cc);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setModel
	 */
	public void setModel(String model);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setTransponder
	 */
	public void setTransponder(String transponder);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setTeam
	 */
	public void setTeam(String team);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setSponsor
	 */
	public void setSponsor(String sponsor);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceUserSettingsBMPBean#setBodyNumber
	 */
	public void setBodyNumber(String bodyNumber);
}