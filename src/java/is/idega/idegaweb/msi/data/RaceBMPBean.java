/*
 * $Id: RaceBMPBean.java,v 1.4 2008/05/21 09:04:17 palli Exp $
 * Created on May 22, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.msi.data;

import is.idega.idegaweb.msi.events.RaceUpdatedAction;

import java.sql.Timestamp;

import javax.ejb.RemoveException;

import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.user.data.Group;
import com.idega.user.data.GroupBMPBean;
import com.idega.util.IWTimestamp;
import com.idega.util.expression.ELUtil;


/**
 * Last modified: $Date: 2008/05/21 09:04:17 $ by $Author: palli $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.4 $
 */
public class RaceBMPBean extends GroupBMPBean  implements Race, Group {

	private static final long serialVersionUID = -4425187386593352783L;

	private boolean publishEvent = Boolean.FALSE;

	private static final String METADATA_RACE_DATE = "run_date";
	
	private static final String METADATA_LAST_REGISTRATION_DATE = "last_registration_date";

	private static final String METADATA_LAST_REGISTRATION_DATE_PRICE1 = "last_registration_date_price1";
	
	private static final String METADATA_CHIP_RENT = "chip_rent";
	
	private static final String METADATA_RACE_CATEGORY = "race_category";

	private static final String METADATA_RACE_TYPE = "race_type";
	
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

	public Timestamp getLastRegistrationDatePrice1() {
		String date = getMetaData(METADATA_LAST_REGISTRATION_DATE_PRICE1);
		if (date != null) {
			IWTimestamp stamp = new IWTimestamp(date);
			return stamp.getTimestamp();
		}
		return null;
	}

	public float getChipRent() {
		String rent = getMetaData(METADATA_CHIP_RENT);

		if (rent != null) {
			return Float.parseFloat(rent);
		}
	
		return 0;
	}
	
	public RaceCategory getRaceCategory() {
		String raceCategoryID = getMetaData(METADATA_RACE_CATEGORY);
		if (raceCategoryID != null) {
			try {
				RaceCategory category = ((RaceCategoryHome) IDOLookup.getHome(RaceCategory.class)).findByPrimaryKey(new Integer(raceCategoryID));
				return category;
			} catch (IDOLookupException e) {
			} catch (NumberFormatException e) {
			}
		}
			
		return null;
	}

	public RaceType getRaceType() {
		String raceTypeID = getMetaData(METADATA_RACE_TYPE);
		if (raceTypeID != null) {
			try {
				RaceType type = ((RaceTypeHome) IDOLookup.getHome(RaceType.class)).findByPrimaryKey(new Integer(raceTypeID));
				return type;
			} catch (IDOLookupException e) {
			} catch (NumberFormatException e) {
			}
		}
			
		return null;
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

	public void setLastRegistrationDatePrice1(Timestamp date) {
		IWTimestamp stamp = new IWTimestamp(date);
		setMetaData(METADATA_LAST_REGISTRATION_DATE_PRICE1, stamp.toSQLString(),
				"java.sql.Timestamp");
	}

	public void setChipRent(float rent) {
		setMetaData(METADATA_CHIP_RENT, String.valueOf(rent), "java.lang.Float");
	}
	
	public void setRaceCategory(RaceCategory raceCategory) {
		if (raceCategory != null) {
			setRaceCategory(raceCategory.getPrimaryKey().toString());
		}
	}
	
	public void setRaceCategory(String raceCategoryID) {
		setMetaData(METADATA_RACE_CATEGORY, raceCategoryID, "java.lang.String");
	}
	
	public void setRaceType(RaceType raceType) {
		if (raceType != null) {
			setRaceType(raceType.getPrimaryKey().toString());
		}
	}
	
	public void setRaceType(String raceTypeID) {
		setMetaData(METADATA_RACE_TYPE, raceTypeID, "java.lang.String");
	}

	/* 
	 * (non-Javadoc)
	 * @see com.idega.user.data.GroupBMPBean#store()
	 */
	@Override
	public void store() {
		super.store();

		if (this.publishEvent) {
			RaceUpdatedAction event = new RaceUpdatedAction(this);
			ELUtil.getInstance().publishEvent(event);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see com.idega.data.GenericEntity#remove()
	 */
	@Override
	public void remove() throws RemoveException {
		RaceUpdatedAction event = new RaceUpdatedAction(this);
		event.setRemoved(Boolean.TRUE);

		super.remove();
		if (this.publishEvent) {
			ELUtil.getInstance().publishEvent(event);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.Race#setNotification(boolean)
	 */
	@Override
	public void setNotification(boolean publishEvent) {
		this.publishEvent = publishEvent;
	}
}