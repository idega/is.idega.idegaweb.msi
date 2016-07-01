package is.idega.idegaweb.msi.data.bean;

import is.idega.idegaweb.msi.util.MSIConstants;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.idega.user.data.bean.Group;

/**
 * 
 * <p>This class is used for nothing and it does nothing. 
 * Basically it is workaround of bad pattern management</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2016 June 10
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
@Entity
@DiscriminatorValue(MSIConstants.GROUP_TYPE_RACE_EVENT)
public class RaceEventGroup extends Group {
	private static final long serialVersionUID = -3578703262437128982L;
}