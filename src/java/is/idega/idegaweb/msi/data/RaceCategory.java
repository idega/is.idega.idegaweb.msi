package is.idega.idegaweb.msi.data;


import com.idega.data.IDOEntity;

public interface RaceCategory extends IDOEntity {
	/**
	 * @see is.idega.idegaweb.msi.data.RaceCategoryBMPBean#getCategoryKey
	 */
	public String getCategoryKey();

	/**
	 * @see is.idega.idegaweb.msi.data.RaceCategoryBMPBean#setCategoryKey
	 */
	public void setCategoryKey(String key);
}