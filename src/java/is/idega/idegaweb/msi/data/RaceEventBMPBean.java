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

import java.util.logging.Level;

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

	public static final String METADATA_TIME_TRANSMITER_PRICE = "TIME_TRANSMITTER_PRICE";
	public static final String METADATA_TIME_TRANSMITER_PRICE_ON = "TIME_TRANSMITTER_PRICE_ON";

	//private static final String METADATA_HAS_CHIP = "has_chip";

	//private static final String METADATA_CHIP_PRICE = "chip_price";

	private static final String METADATA_TEAM_COUNT = "team_count";

	//getters
	@Override
	public String getEventID() {
		String id = getMetaData(METADATA_EVENT_ID);

		return id;
	}

	@Override
	public float getPrice() {
		String price = getMetaData(METADATA_PRICE);

		if (price != null) {
			return Float.parseFloat(price);
		}

		return 0;
	}

	@Override
	public float getPrice2() {
		String price = getMetaData(METADATA_PRICE2);

		if (price != null) {
			return Float.parseFloat(price);
		}

		return 0;
	}
	@Override
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
	@Override
	public void setTimeTransmitterPrice(float price) {
		setMetaData(METADATA_TIME_TRANSMITER_PRICE, String.valueOf(price), "java.lang.Float");
	}

	@Override
	public boolean isTimeTransmitterPriceOn() {
		try{
			String price = getMetaData(METADATA_TIME_TRANSMITER_PRICE_ON);
			return "Y".equals(price);
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed getting time transmitter price on", e);
		}

		return false;
	}
	@Override
	public void setTimeTransmitterPriceOn(boolean price) {
		setMetaData(METADATA_TIME_TRANSMITER_PRICE_ON, price ? "Y" : "N", "java.lang.String");
	}

/*	public boolean getHasChip() {
		String hasChip = getMetaData(METADATA_HAS_CHIP);

		if (hasChip != null) {
			return Boolean.parseBoolean(hasChip);
		}

		return false;
	}

	public float getChipPrice() {
		String price = getMetaData(METADATA_CHIP_PRICE);

		if (price != null) {
			return Float.parseFloat(price);
		}

		return 0;
	}*/

	@Override
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
	@Override
	public void setEventID(String id) {
		setMetaData(METADATA_EVENT_ID, id, "java.lang.String");
	}

	@Override
	public void setPrice(float price) {
		setMetaData(METADATA_PRICE, String.valueOf(price), "java.lang.Float");
	}

	@Override
	public void setPrice2(float price) {
		setMetaData(METADATA_PRICE2, String.valueOf(price), "java.lang.Float");
	}

/*	public void setHasChip(boolean hasChip) {
		setMetaData(METADATA_HAS_CHIP, Boolean.valueOf(hasChip).toString(), "java.lang.Boolean");
	}


	public void setChipPrice(float price) {
		setMetaData(METADATA_CHIP_PRICE, String.valueOf(price), "java.lang.Float");
	}*/

	@Override
	public void setTeamCount(int teamCount) {
		setMetaData(METADATA_TEAM_COUNT, String.valueOf(teamCount), "java.lang.Integer");
	}

	@Override
	public String toString() {
		return "Name: " + getName() + ", ID: " + getId();
	}
}