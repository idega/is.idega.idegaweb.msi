/*
 * $Id: RaceEventBMPBean.java,v 1.1 2007/06/07 22:54:33 palli Exp $
 * Created on May 22, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.msi.data;

import com.idega.user.data.Group;
import com.idega.user.data.GroupBMPBean;


/**
 * Last modified: $Date: 2007/06/07 22:54:33 $ by $Author: palli $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class RaceEventBMPBean extends GroupBMPBean  implements Group, RaceEvent{

	private static final String METADATA_EVENT_ID = "event_id";

	public int getEventID() {
		String id = getMetaData(METADATA_EVENT_ID);

		if (id != null) {
			return Integer.parseInt(id);
		}
	
		return 0;
	}
	
	public void setEventID(int id) {
		setMetaData(METADATA_EVENT_ID, String.valueOf(id), "java.lang.Integer");
	}
	
	public void setEventID(Integer id) {
		setMetaData(METADATA_EVENT_ID, id.toString(), "java.lang.Integer");
	}
}