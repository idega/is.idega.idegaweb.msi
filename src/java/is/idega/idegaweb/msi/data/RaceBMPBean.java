/*
 * $Id: RaceBMPBean.java,v 1.1 2007/06/07 22:54:34 palli Exp $
 * Created on May 22, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.msi.data;

import java.sql.Timestamp;

import com.idega.user.data.Group;
import com.idega.user.data.GroupBMPBean;
import com.idega.util.IWTimestamp;


/**
 * Last modified: $Date: 2007/06/07 22:54:34 $ by $Author: palli $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class RaceBMPBean extends GroupBMPBean  implements Race, Group {
	private static final String METADATA_RACE_DATE = "run_date";
	
	private static final String METADATA_LAST_REGISTRATION_DATE = "last_registration_date";
	
	private static final String METADATA_PRICE = "price";
	
	private static final String METADATA_CHIP_RENT = "chip_rent";

	//getters
	public Timestamp getRaceDate() {
		String date = getMetaData(METADATA_RACE_DATE);
		if (date != null) {
			IWTimestamp stamp = new IWTimestamp(date);
			return stamp.getTimestamp();
		}
		return null;
	}

	public Timestamp getLastRegistrationDate() {
		String date = getMetaData(METADATA_LAST_REGISTRATION_DATE);
		if (date != null) {
			IWTimestamp stamp = new IWTimestamp(date);
			return stamp.getTimestamp();
		}
		return null;
	}
	
	public float getPrice() {
		String price = getMetaData(METADATA_PRICE);

		if (price != null) {
			return Float.parseFloat(price);
		}
	
		return 0;
	}

	public float getChipRent() {
		String rent = getMetaData(METADATA_CHIP_RENT);

		if (rent != null) {
			return Float.parseFloat(rent);
		}
	
		return 0;
	}

	//setters
	public void setRaceDate(Timestamp date) {
		IWTimestamp stamp = new IWTimestamp(date);
		setMetaData(METADATA_RACE_DATE, stamp.toSQLString(),
				"java.sql.Timestamp");
	}

	public void setLastRegistrationDate(Timestamp date) {
		IWTimestamp stamp = new IWTimestamp(date);
		setMetaData(METADATA_LAST_REGISTRATION_DATE, stamp.toSQLString(),
				"java.sql.Timestamp");
	}
	
	public void setPrice(float price) {
		setMetaData(METADATA_PRICE, String.valueOf(price), "java.lang.Float");
	}
	
	public void setChipRent(float rent) {
		setMetaData(METADATA_CHIP_RENT, String.valueOf(rent), "java.lang.Float");
	}
}