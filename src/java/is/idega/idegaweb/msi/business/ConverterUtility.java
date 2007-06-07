/*
 * $Id: ConverterUtility.java,v 1.1 2007/06/07 22:54:35 palli Exp $
 * Created on May 22, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.msi.business;

import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.data.RaceEventHome;
import is.idega.idegaweb.msi.data.RaceHome;
import is.idega.idegaweb.msi.data.Season;
import is.idega.idegaweb.msi.data.SeasonHome;

import javax.ejb.FinderException;

import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.user.data.Group;


/**
 * Last modified: $Date: 2007/06/07 22:54:35 $ by $Author: palli $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class ConverterUtility {

	private static ConverterUtility util = null;
	
	private ConverterUtility() {
	}
	
	public static ConverterUtility getInstance() {
		if (util == null) {
			util = new ConverterUtility();
		}
		return util;
	}

	public Race convertGroupToRace(Group group) throws FinderException {
		return convertGroupToRace(group.getPrimaryKey());
	}
	
	public Race convertGroupToRace(Object groupPK) throws FinderException {
		try {
			RaceHome home = (RaceHome) IDOLookup.getHome(Race.class);
			return (Race) home.findByPrimaryKey(groupPK);
		}
		catch (IDOLookupException ile) {
			throw new FinderException(ile.getMessage());
		}
	}

	public RaceEvent convertGroupToRaceEvent(Group group) throws FinderException {
		return convertGroupToRaceEvent(group.getPrimaryKey());
	}
	
	public RaceEvent convertGroupToRaceEvent(Object groupPK) throws FinderException {
		try {
			RaceEventHome home = (RaceEventHome) IDOLookup.getHome(RaceEvent.class);
			return (RaceEvent) home.findByPrimaryKey(groupPK);
		}
		catch (IDOLookupException ile) {
			throw new FinderException(ile.getMessage());
		}
	}

	public Season convertGroupToSeason(Group group) throws FinderException {
		return convertGroupToSeason(group.getPrimaryKey());
	}
	
	public Season convertGroupToSeason(Object groupPK) throws FinderException {
		try {
			SeasonHome home = (SeasonHome) IDOLookup.getHome(Season.class);
			return (Season) home.findByPrimaryKey(groupPK);
		}
		catch (IDOLookupException ile) {
			throw new FinderException(ile.getMessage());
		}
	}
}