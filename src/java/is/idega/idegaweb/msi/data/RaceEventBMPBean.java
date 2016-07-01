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

import is.idega.idegaweb.msi.events.RaceEventUpdatedAction;

import java.util.logging.Level;

import javax.ejb.RemoveException;

import com.idega.user.data.Group;
import com.idega.user.data.GroupBMPBean;
import com.idega.util.expression.ELUtil;


/**
 * Last modified: $Date: 2007/06/11 12:14:19 $ by $Author: palli $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class RaceEventBMPBean extends GroupBMPBean  implements Group, RaceEvent{

	private static final long serialVersionUID = 6322170965139587414L;

	private boolean publishEvent;
	
	private static final String METADATA_EVENT_ID = "event_id";

	private static final String METADATA_PRICE = "price";

	private static final String METADATA_PRICE2 = "price2";

	public static final String METADATA_TIME_TRANSMITER_PRICE = "TIME_TRANSMITTER_PRICE";
	public static final String METADATA_TIME_TRANSMITER_PRICE_ON = "TIME_TRANSMITTER_PRICE_ON";

	private static final String METADATA_TEAM_COUNT = "team_count";

	//getters
	public String getEventID() {
		return getMetaData(METADATA_EVENT_ID);
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

	public float getTimeTransmitterPrice() {
		try{
			String price = getMetaData(METADATA_TIME_TRANSMITER_PRICE);
			if (price != null) {
				return Float.parseFloat(price);
			}
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed getting time transmitter price", e);
		}

		return 0;
	}

	public void setTimeTransmitterPrice(float price) {
		setMetaData(METADATA_TIME_TRANSMITER_PRICE, String.valueOf(price), "java.lang.Float");
	}

	public boolean isTimeTransmitterPriceOn() {
		try{
			String price = getMetaData(METADATA_TIME_TRANSMITER_PRICE_ON);
			return "Y".equals(price);
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed getting time transmitter price on", e);
		}

		return false;
	}

	public void setTimeTransmitterPriceOn(boolean price) {
		setMetaData(METADATA_TIME_TRANSMITER_PRICE_ON, price ? "Y" : "N", "java.lang.String");
	}

	public int getTeamCount() {
		String teamCount= getMetaData(METADATA_TEAM_COUNT);

		if (teamCount != null && !"".equals(teamCount.trim())) {
			try {
				return Integer.parseInt(teamCount);
			} catch (NumberFormatException e) {
				return 1;
			}
		}

		return 1;
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

	public void setTeamCount(int teamCount) {
		setMetaData(METADATA_TEAM_COUNT, String.valueOf(teamCount), "java.lang.Integer");
	}

	public String toString() {
		return "Name: " + getName() + ", ID: " + getId();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.data.GroupBMPBean#store()
	 */
	@Override
	public void store() {
		super.store();

		if (this.publishEvent) {
			RaceEventUpdatedAction event = new RaceEventUpdatedAction(this);
			ELUtil.getInstance().publishEvent(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.data.GenericEntity#remove()
	 */
	@Override
	public void remove() throws RemoveException {
		RaceEventUpdatedAction event = new RaceEventUpdatedAction(this);
		event.setRemoved(Boolean.TRUE);
		super.remove();

		if (this.publishEvent) {
			ELUtil.getInstance().publishEvent(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceEvent#setNotification(boolean)
	 */
	@Override
	public void setNotification(boolean publishEvent) {
		this.publishEvent = publishEvent;
	}
}