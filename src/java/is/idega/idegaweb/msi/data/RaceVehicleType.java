package is.idega.idegaweb.msi.data;


import com.idega.data.IDOEntity;

public interface RaceVehicleType extends IDOEntity {
	/**
	 * @see is.idega.idegaweb.msi.data.RaceVehicleTypeBMPBean#getLocalizationKey
	 */
	public String getLocalizationKey();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceVehicleTypeBMPBean#getParent
	 */
	public RaceVehicleType getParent();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceVehicleTypeBMPBean#setLocalizationKey
	 */
	public void setLocalizationKey(String key);

	/**
	 * @see is.idega.idegaweb.msi.data.RaceVehicleTypeBMPBean#setParent
	 */
	public void setParent(RaceVehicleType parent);
}