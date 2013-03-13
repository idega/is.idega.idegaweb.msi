/*
 * $Id: RaceEventBMPBean.java,v 1.1 2007/06/11 12:14:19 palli Exp $
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
 * Last modified: $Date: 2007/06/11 12:14:19 $ by $Author: palli $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class RaceEventBMPBean extends GroupBMPBean  implements Group, RaceEvent{

	private static final String METADATA_EVENT_ID = "event_id";
	
	private static final String METADATA_PRICE = "price";

	private static final String METADATA_PRICE2 = "price2";
	
	//getters
	public String getEventID() {
		String id = getMetaData(METADATA_EVENT_ID);

		return id;
	}
	
	public float getPrice() {
		String price = getMetaData(METADATA_PRICE);

		if (price != null) {
			return Float.parseFloat(price);
		}
	
		return 0;
	}

	public float getPrice2() {
		String price = getMetaData(METADATA_PRICE2);

		if (price != null) {
			return Float.parseFloat(price);
		}
	
		return 0;
	}

	//setters
	public void setEventID(String id) {
		setMetaData(METADATA_EVENT_ID, id, "java.lang.String");
	}

	public void setPrice(float price) {
		setMetaData(METADATA_PRICE, String.valueOf(price), "java.lang.Float");
	}
	
	public void setPrice2(float price) {
		setMetaData(METADATA_PRICE2, String.valueOf(price), "java.lang.Float");
	}
}