package is.idega.idegaweb.msi.data;


import com.idega.user.data.Group;
import java.sql.Timestamp;
import com.idega.data.IDOEntity;

public interface Season extends IDOEntity, Group {
	/**
	 * @see is.idega.idegaweb.msi.data.SeasonBMPBean#getSeasonBeginDate
	 */
	public Timestamp getSeasonBeginDate();

	/**
	 * @see is.idega.idegaweb.msi.data.SeasonBMPBean#getSeasonEndDate
	 */
	public Timestamp getSeasonEndDate();

	/**
	 * @see is.idega.idegaweb.msi.data.SeasonBMPBean#setSeasonBeginDate
	 */
	public void setSeasonBeginDate(Timestamp date);

	/**
	 * @see is.idega.idegaweb.msi.data.SeasonBMPBean#setSeasonEndDate
	 */
	public void setSeasonEndDate(Timestamp date);
}