/*
 * $Id: RaceOverview.java,v 1.7 2008/05/21 09:04:17 palli Exp $
 * Created on Sep 25, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.business.RaceDateComparator;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.Season;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;

/**
 * Last modified: $Date: 2008/05/21 09:04:17 $ by $Author: palli $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.7 $
 */
public class RaceOverview extends RaceBlock {

	private boolean showParticipantsLink = false;
	
	private ICPage registrationPage = null;

	private ICPage participantsListPage = null;

	public void main(IWContext iwc) throws Exception {
		/*
		 * if (!iwc.isLoggedOn()) { add(new Text("No user logged on..."));
		 * return; }
		 */

		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("raceElement");
		layer.setID("races");

		Layer headerLayer = new Layer(Layer.DIV);
		headerLayer.setStyleClass("raceHeader");
		layer.add(headerLayer);

		Layer headingLayer = new Layer(Layer.DIV);
		headingLayer.setStyleClass("raceHeading");
		headingLayer.add(new Text(getHeading()));
		headerLayer.add(headingLayer);

		layer.add(getRaceTable(iwc));

		add(layer);
	}

	protected String getHeading() {
		return getResourceBundle().getLocalizedString("race_overview.heading",
				"Races");
	}

	private Table2 getRaceTable(IWContext iwc) throws RemoteException {
		Table2 table = new Table2();
		table.setStyleClass("raceTable");
		table.setStyleClass("ruler");
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);

		boolean isLoggedOn = iwc.isLoggedOn();

		List races = getRaces(iwc);

		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.setStyleClass("raceName");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_overview.race_name", "Name")));

		cell = row.createHeaderCell();
		cell.setStyleClass("raceType");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_overview.race_type", "Type")));

		cell = row.createHeaderCell();
		cell.setStyleClass("raceCategory");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_overview.race_category", "Category")));

		
		cell = row.createHeaderCell();
		cell.setStyleClass("raceDate");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_overview.race_date", "Date")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("raceLastRegistrationDate");
		if (!isLoggedOn && !showParticipantsLink) {
			cell.setStyleClass("lastColumn");				
		}
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_overview.race_last_registration_price1", "Last registration price1")));


		if (showParticipantsLink) {
			cell = row.createHeaderCell();
			if (!isLoggedOn) {
				cell.setStyleClass("lastColumn");
			}
			cell.setStyleClass("raceParticipants");
			cell.add(new Text(getResourceBundle().getLocalizedString(
					"race_overview.race_participants", "Participants")));
		}

		if (isLoggedOn) {
			cell = row.createHeaderCell();
			cell.setStyleClass("lastColumn");
			cell.setStyleClass("raceRegister");
			cell.add(new Text(getResourceBundle().getLocalizedString(
					"race_overview.race_register", "Register")));
		}

		group = table.createBodyRowGroup();
		int iRow = 1;

		Iterator iter = races.iterator();
		while (iter.hasNext()) {
			row = group.createRow();
			Race race = (Race) iter.next();
			if (iRow == 1) {
				row.setStyleClass("firstRow");
			} else if (!iter.hasNext()) {
				row.setStyleClass("lastRow");
			}

			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.setStyleClass("raceName");
			cell.add(new Text(race.getName()));

			cell = row.createCell();
			cell.setStyleClass("raceType");
			String raceTypeString = "";
			if (race.getRaceType() != null) {
				raceTypeString = getResourceBundle().getLocalizedString(race.getRaceType().getRaceType(), race.getRaceType().getRaceType());
			}
			cell.add(new Text(raceTypeString));

			cell = row.createCell();
			cell.setStyleClass("raceCategory");
			String raceCategoryString = "";
			if (race.getRaceCategory() != null) {
				raceCategoryString = getResourceBundle().getLocalizedString(race.getRaceCategory().getCategoryKey(), race.getRaceCategory().getCategoryKey());
			}
			cell.add(new Text(raceCategoryString));

			
			cell = row.createCell();
			cell.setStyleClass("raceDate");
			String raceDateString = "";
			if (race.getRaceDate() != null) {
				raceDateString = new IWTimestamp(race.getRaceDate()).getDateString("dd.MM.yyyy");
			}
			cell.add(new Text(raceDateString));

			cell = row.createCell();
			cell.setStyleClass("raceLastRegistrationDate");
			String registrationDateString = "";
			if (race.getLastRegistrationDatePrice1() != null) {
				registrationDateString = new IWTimestamp(race.getLastRegistrationDatePrice1()).getDateString("dd.MM.yyyy");
			}
			if (!isLoggedOn && !showParticipantsLink) {
				cell.setStyleClass("lastColumn");				
			}
			cell.add(new Text(registrationDateString));
			
			if (showParticipantsLink) {
				cell = row.createCell();
				if (!isLoggedOn) {
					cell.setStyleClass("lastColumn");
				}
				cell.setStyleClass("raceParticipants");

				boolean showlink = true;
				
				/*IWTimestamp now = IWTimestamp.RightNow();
				if (race.getLastRegistrationDatePrice1() != null) {
					IWTimestamp lastRegisrationDate = new IWTimestamp(race.getLastRegistrationDatePrice1());
					
					lastRegisrationDate.addDays(1);
					
					if (now.isLaterThan(lastRegisrationDate)) {
						showlink = true;
					}					
				}*/

				if (showlink && this.participantsListPage != null) {
					Link link = new Link(getBundle(iwc).getImage("shared/list.gif", getResourceBundle().getLocalizedString("race_overview.participants_list", "Participants list")));
					link.setStyleClass("register");
					link.setToolTip(getResourceBundle().getLocalizedString("race_overview.participants_list", "Participants list"));
					
					link.setParameter(RaceParticipantList.PARAMETER_RACE, race.getPrimaryKey().toString());
					
					link.setPage(this.participantsListPage);
					cell.add(link);
				} else {
					cell.add(new Text(""));					
				}
			}

			if (isLoggedOn) {
				cell = row.createCell();
				cell.setStyleClass("lastColumn");
				cell.setStyleClass("raceRegister");
				
				boolean showlink = true;
				
				IWTimestamp now = IWTimestamp.RightNow();
				if (race.getLastRegistrationDate() == null) {
					showlink = false;
				} else {
					IWTimestamp lastRegisrationDate = new IWTimestamp(race.getLastRegistrationDate());
					
					lastRegisrationDate.addDays(1);
					
					if (now.isLaterThan(lastRegisrationDate)) {
						showlink = false;
					}					
				}
				
				if (iwc.isSuperAdmin()) {
					showlink = true;
				}
				
				if (this.registrationPage != null && showlink) {
					Link link = new Link(getBundle(iwc).getImage("shared/register.gif", getResourceBundle().getLocalizedString("race_overview.register", "Register")));
					link.setStyleClass("register");
					link.setToolTip(getResourceBundle().getLocalizedString("race_overview.register", "Register"));
					
					link.setParameter(Registration.PARAMETER_RACE, race.getPrimaryKey().toString());
					
					link.setPage(this.registrationPage);
					cell.add(link);
				} else {
					cell.add(new Text(""));
				}
			}

			if (iRow % 2 == 0) {
				row.setStyleClass("evenRow");
			} else {
				row.setStyleClass("oddRow");
			}

			iRow++;
		}

		return table;
	}

	protected List getRaces(IWContext iwc) {
		List races = new ArrayList();

		try {
			Collection seasons = getRaceBusiness(iwc).getSeasons();
			Group currentSeason = null;
			IWTimestamp now = IWTimestamp.RightNow();
			if (seasons != null) {
				Iterator it = seasons.iterator();
				while (it.hasNext() && currentSeason == null) {
					Group gSeason = (Group) it.next();
					Season season = ConverterUtility.getInstance()
							.convertGroupToSeason(gSeason);
					IWTimestamp from = new IWTimestamp(season
							.getSeasonBeginDate());
					IWTimestamp to = new IWTimestamp(season.getSeasonEndDate());
					if (now.isBetween(from, to)) {
						currentSeason = gSeason;
					}
				}
			}

			if (currentSeason != null) {
				String[] types = { MSIConstants.GROUP_TYPE_RACE };
				Collection gRaces = getGroupBusiness(iwc).getChildGroups(
						currentSeason, types, true);
				if (gRaces != null) {
					Iterator it = gRaces.iterator();
					while (it.hasNext()) {
						Group gRace = (Group) it.next();
						Race race = ConverterUtility.getInstance()
								.convertGroupToRace(gRace);
						races.add(race);
					}
				}
			}
		} catch (RemoteException re) {
			log(re);
			return new ArrayList();
		} catch (FinderException e) {
			log(e);
		}

		RaceDateComparator comparator = new RaceDateComparator();
		Collections.sort(races, comparator);
		
		return races;
	}
	
	public void setShowParticipantList(boolean showList) {
		this.showParticipantsLink = showList;
	}
	
	public boolean getShowParticipantList() {
		return this.showParticipantsLink;
	}
	
	public void setRegistrationPage(ICPage page) {
		this.registrationPage = page;
	}
	
	public ICPage getRegistrationPage() {
		return this.registrationPage;
	}
	
	public void setParticipantsLinkPage(ICPage page) {
		this.participantsListPage = page;
	}
	
	public ICPage getParticipantsLinkPage() {
		return this.participantsListPage;
	}
}