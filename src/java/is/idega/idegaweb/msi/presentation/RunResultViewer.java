/*
 * Created on Jul 8, 2004
 */
package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.business.RaceBusiness;
import is.idega.idegaweb.msi.business.RunGroup;
import is.idega.idegaweb.msi.business.RunGroupComparator;
import is.idega.idegaweb.msi.business.RunGroupMap;
import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.data.Season;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.location.data.Country;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.util.SelectorUtility;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.Counter;
import com.idega.util.IWTimestamp;

/**
 * Show the results of a run. This block can show results for a specific run,
 * year and run group
 * 
 * @author birna
 */
public class RunResultViewer extends Block {

	private static final String STYLENAME_INTERFACE = "interface";
	private static final String STYLENAME_GROUP_ROW = "groupRow";
	private static final String STYLENAME_HEADER_ROW = "headerRow";
	private static final String STYLENAME_LIST_ROW = "listRow";

	private static final String DEFAULT_INTERFACE_STYLE = "font-family:Verdana,Arial,Helvetica,sans-serif;font-size:8px;font-weight:bold;border-width:1px;border-color:#000000;border-style:solid;";
	private static final String DEFAULT_GROUP_ROW_STYLE = "font-family:Arial,Helvetica,sans-serif;font-size:10px;font-weight:bold;padding:4px;background-color:#ACACAC;color:#FFFFFF";
	private static final String DEFAULT_HEADER_ROW_STYLE = "font-family:Arial,Helvetica,sans-serif;font-size:12px;font-weight:bold;padding:2px;";
	private static final String DEFAULT_LIST_ROW_STYLE = "font-family:Arial,Helvetica,sans-serif;font-size:10px;padding:2px;";

	private static final String DARK_COLOR = "#E9E9E9";
	private static final String LIGHT_COLOR = "#FFFFFF";

	private int COLUMN_COUNT = 13;

	private RaceEvent distance;
	private Season year;

	Collator _collator;
	private IWContext _iwc;
	private IWResourceBundle iwrb;

	private RaceBusiness _runBiz;
	private GroupBusiness _groupBiz;
	private UserBusiness _userBiz;

	private String runPK;
	private Race run;
	private int sortBy = MSIConstants.RYSDD_TOTAL;

	private SelectorUtility util;
	private HashMap countryMap = new HashMap();

	public void main(IWContext iwc) throws Exception {
		this._iwc = iwc;
		this.iwrb = getResourceBundle(iwc);
		this._collator = Collator.getInstance(iwc.getCurrentLocale());
		this.util = new SelectorUtility();

		if (this.runPK != null) {
			this.run = ConverterUtility.getInstance().convertGroupToRace(
					new Integer(this.runPK));
		}

		if (this.run == null) {
			add("No run set...");
			return;
		}

		if (iwc.isParameterSet(MSIConstants.PARAMETER_SORT_BY)) {
			this.sortBy = Integer.parseInt(iwc
					.getParameter(MSIConstants.PARAMETER_SORT_BY));
		}

		if (iwc.isParameterSet(MSIConstants.GROUP_TYPE_SEASON)) {
			try {
				this.year = ConverterUtility
						.getInstance()
						.convertGroupToSeason(
								new Integer(
										iwc
												.getParameter(MSIConstants.GROUP_TYPE_SEASON)));
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}

		if (iwc.isParameterSet(MSIConstants.GROUP_TYPE_RACE_EVENT)) {
			try {
				this.distance = ConverterUtility
						.getInstance()
						.convertGroupToRaceEvent(
								new Integer(
										iwc
												.getParameter(MSIConstants.GROUP_TYPE_RACE_EVENT)));
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}

		Form form = null;// (Form)
							// iwc.getApplicationAttribute("run_result_cache_" +
							// runPK + "_" + (year != null ?
							// year.getPrimaryKey() + "_" : "") + (distance !=
							// null ? distance.getPrimaryKey() + "_" : "") +
							// sortBy);

		if (form == null) {
			form = new Form();
			Table table = new Table();
			table.setCellpadding(0);
			table.setCellspacing(0);
			table.setColumns(this.COLUMN_COUNT);
			table.setWidth(Table.HUNDRED_PERCENT);
			int row = 1;

			table.setCellpaddingLeft(1, row, 16);
			table.mergeCells(1, row, table.getColumns(), row);

			table.add(getYearsDropdown(this.run), 1, row);
			table.add(Text.getNonBrakingSpace(), 1, row);
			table.add(getDistanceDropdown(), 1, row);
			table.add(Text.getNonBrakingSpace(), 1, row);
			table.add(getSortDropdown(), 1, row++);
			table.setHeight(row++, 12);

			if (this.distance != null) {
				row = insertHeadersIntoTable(table, row);

				switch (this.sortBy) {
				case MSIConstants.RYSDD_TOTAL:
					getTotalResults(table, row);
					break;
				case MSIConstants.RYSDD_GROUPS:
					getGroupResults(table, row);
					break;
				case MSIConstants.RYSDD_GROUPS_COMP:
					getGroupCompetitionResults(table, row);
					break;
				}
			}

			for (int a = 2; a < this.COLUMN_COUNT; a = a + 2) {
				table.setWidth(a, 2);
				table.setColumnColor(a, "#FFFFFF");
			}

			form.add(table);
			// iwc.setApplicationAttribute("run_result_cache_" + runPK + "_" +
			// (year != null ? year.getPrimaryKey() + "_" : "") + (distance !=
			// null ? distance.getPrimaryKey() + "_" : "") + sortBy, form);
		}
		add(form);
	}

	private void getTotalResults(Table table, int row) {
		try {
			Collection runs = null;//getRunBiz().getRunnersByDistance(this.distance,
					//null);
			row = insertRunGroupIntoTable(table, row,
					"results.all_participants");

			Iterator runIter = runs.iterator();
			int num = 1;
			while (runIter.hasNext()) {
				Participant run = (Participant) runIter.next();
				if (run.getRunTime() != -1) {
					row = insertRunIntoTable(table, row, run, num, num);
					num++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getGroupResults(Table table, int row) {
		try {
			List runGroups = new ArrayList(getGroupBiz().getChildGroups(
					this.distance));
			sortRunnerGroups(runGroups);
			Iterator runGroupIter = runGroups.iterator();

			while (runGroupIter.hasNext()) {
				Group runGroup = (Group) runGroupIter.next();

				Collection runners = null;//getRunBiz().getRunnersByDistance(
						//this.distance, runGroup);
				if (runners.size() > 0) {
					row = insertRunGroupIntoTable(table, row, "group_"
							+ runGroup.getName());
					Iterator runIter = runners.iterator();
					int num = 1;
					while (runIter.hasNext()) {
						Participant run = (Participant) runIter.next();
						if (run.getRunTime() > 0) {
							row = insertRunIntoTable(table, row, run, num, num);
							num++;
						}

						if (!runIter.hasNext()) {
							table.setHeight(row++, 2);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getGroupCompetitionResults(Table table, int row) {
		try {
			List runs = null;//new ArrayList(getRunBiz().getRunnersByDistance(
					//this.distance, null));

			Map runGroups = new HashMap();
			RunGroupMap map = new RunGroupMap();

			Participant runner;
			RunGroup runnerGroup;
			Iterator iterator = runs.iterator();
			while (iterator.hasNext()) {
				runner = (Participant) iterator.next();
				if (runner.getRunGroupName() != null
						&& runner.getRunGroupName().trim().length() > 0) {
					runnerGroup = (RunGroup) runGroups.get(runner
							.getRunGroupName());
					if (runnerGroup == null) {
						runnerGroup = new RunGroup(runner.getRunGroupName());
						runGroups.put(runner.getRunGroupName(), runnerGroup);
					}

					map.put(runnerGroup, runner);
				}
			}

			List groupList = new ArrayList(map.keySet());
			Collections.sort(groupList, new RunGroupComparator(map));

			iterator = groupList.iterator();
			Participant run;
			int num = 1;
			int count = 0;
			Iterator runIter;
			Collection runnersInRunGroup;
			RunGroup runGroup;
			while (iterator.hasNext()) {
				runGroup = (RunGroup) iterator.next();
				runnersInRunGroup = map.getCollection(runGroup);
				if (runnersInRunGroup.size() >= 3) {
					row = insertRunGroupIntoTable(table, row, runGroup
							.getGroupName()
							+ " - " + runGroup.getCounter().toString());

					runIter = runnersInRunGroup.iterator();
					num = 1;
					count = 0;
					while (runIter.hasNext()) {
						run = (Participant) runIter.next();
						num = runs.indexOf(run);
						row = insertRunIntoTable(table, row, run, num,
								count + 1);
						count++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DropdownMenu getYearsDropdown(Group run) throws RemoteException {
		DropdownMenu years = (DropdownMenu) this.util
				.getSelectorFromIDOEntities(new DropdownMenu(
						MSIConstants.GROUP_TYPE_SEASON), run.getChildren(),
						"getName", this.iwrb);
		years.addMenuElementFirst("", "");
		years.setToSubmit();
		years.keepStatusOnAction();
		return years;
	}

	private DropdownMenu getDistanceDropdown() throws RemoteException {
		DropdownMenu distanceMenu = new DropdownMenu(
				MSIConstants.GROUP_TYPE_RACE_EVENT);
		distanceMenu.setToSubmit();
		distanceMenu.keepStatusOnAction();
		distanceMenu.addMenuElementFirst("", "");

		Integer threeKM = new Integer(this.iwrb.getIWBundleParent()
				.getProperty("3_km_id", "126"));
		// Integer sevenKM = new
		// Integer(iwrb.getIWBundleParent().getProperty("7_km_id", "113"));

		/*if (this.year != null) {
			List distances = getRunBiz().getDistancesMap(this.run,
					this.year.getName());
			Iterator iter = distances.iterator();
			while (iter.hasNext()) {
				Group distance = (Group) iter.next();
				if (!distance.getPrimaryKey().equals(threeKM)) {
					distanceMenu.addMenuElement(distance.getPrimaryKey()
							.toString(), this.iwrb.getLocalizedString(distance
							.getName(), distance.getName()));
				}
			}
		}*/

		return distanceMenu;
	}

	private DropdownMenu getSortDropdown() throws RemoteException {
		DropdownMenu sort = new DropdownMenu(MSIConstants.PARAMETER_SORT_BY);
		sort.addMenuElement(MSIConstants.RYSDD_TOTAL, this.iwrb
				.getLocalizedString(MSIConstants.PARAMETER_TOTAL,
						"Total result list"));
		sort.addMenuElement(MSIConstants.RYSDD_GROUPS, this.iwrb
				.getLocalizedString(MSIConstants.PARAMETER_GROUPS, "Groups"));
		sort.addMenuElement(MSIConstants.RYSDD_GROUPS_COMP, this.iwrb
				.getLocalizedString(MSIConstants.PARAMETER_GROUPS_COMPETITION,
						"Group competition"));
		sort.setToSubmit();
		sort.keepStatusOnAction();

		return sort;
	}

	/**
	 * Sorts groups based on their names
	 * 
	 * @param groups
	 *            The list of groups to sort
	 */
	private void sortRunnerGroups(List groups) {
		Collections.sort(groups, new Comparator() {

			public int compare(Object arg0, Object arg1) {
				Group g0 = (Group) arg0;
				Group g1 = (Group) arg1;
				return RunResultViewer.this._collator.compare(g0.getName(), g1
						.getName());
			}
		});
	}

	private int insertRunIntoTable(Table table, int row, Participant run,
			int num, int participantRow) {
		int column = 1;

		table.add(getRunnerRowText(Integer.toString(num)), column, row);
		table.setStyleClass(column, row, getStyleName(STYLENAME_LIST_ROW));
		table.setAlignment(column++, row, Table.HORIZONTAL_ALIGN_CENTER);

		column++;

		String runTime = getTimeStringFromRunTime(run.getRunTime());
		table.add(getRunnerRowText(runTime), column, row);
		table.setStyleClass(column, row, getStyleName(STYLENAME_LIST_ROW));
		table.setAlignment(column++, row, Table.HORIZONTAL_ALIGN_CENTER);

		column++;

		String chipTime = run.getChipTime() != -1 ? getTimeStringFromRunTime(run
				.getChipTime())
				: "-";
		table.add(getRunnerRowText(chipTime), column, row);
		table.setStyleClass(column, row, getStyleName(STYLENAME_LIST_ROW));
		table.setAlignment(column++, row, Table.HORIZONTAL_ALIGN_CENTER);

		column++;

		try {
			User user = getUserBiz().getUser(run.getUserID());
			table.add(getRunnerRowText(user.getName()), column, row);
			table
					.setStyleClass(column++, row,
							getStyleName(STYLENAME_LIST_ROW));

			column++;

			IWTimestamp birthDate = new IWTimestamp(user.getDateOfBirth());
			table.add(getRunnerRowText(Integer.toString(birthDate.getYear())),
					column, row);
			table.setStyleClass(column, row, getStyleName(STYLENAME_LIST_ROW));
			table.setAlignment(column++, row, Table.HORIZONTAL_ALIGN_CENTER);

			column++;
		} catch (RemoteException re) {
			log(re);
		}

		Country country = null;
		try {
			String userNationality = run.getUserNationality();
			if (this.countryMap.containsKey(userNationality)) {
				country = (Country) this.countryMap.get(userNationality);
			} else {
				country = getRunBiz().getCountryByNationality(userNationality);
				this.countryMap.put(userNationality, country);
			}
		} catch (RemoteException re) {
			log(re);
		}
		if (country != null) {
			table.add(getRunnerRowText(country.getName()), column, row);
		} else {
			table.add(getRunnerRowText(run.getUserNationality()), column, row);
		}
		table.setStyleClass(column, row, getStyleName(STYLENAME_LIST_ROW));
		table.setAlignment(column++, row, Table.HORIZONTAL_ALIGN_CENTER);

		column++;

		table.add(getRunnerRowText(run.getRunGroupName() != null ? run
				.getRunGroupName() : ""), column, row);
		table.setStyleClass(column, row, getStyleName(STYLENAME_LIST_ROW));

		if (participantRow % 2 == 0) {
			table.setRowColor(row, LIGHT_COLOR);
		} else {
			table.setRowColor(row, DARK_COLOR);
		}

		return ++row;
	}

	private int insertHeadersIntoTable(Table table, int row) {
		int column = 1;

		table.add(getRunnerRowText(this.iwrb.getLocalizedString(
				"results.number", "Nr.")), column, row);
		table.setStyleClass(column, row, getStyleName(STYLENAME_HEADER_ROW));
		table.setAlignment(column++, row, Table.HORIZONTAL_ALIGN_CENTER);
		column++;

		table.add(getRunnerRowText(this.iwrb.getLocalizedString(
				"results.run_time", "Run time")), column, row);
		table.setStyleClass(column, row, getStyleName(STYLENAME_HEADER_ROW));
		table.setAlignment(column++, row, Table.HORIZONTAL_ALIGN_CENTER);
		column++;

		table.add(getRunnerRowText(this.iwrb.getLocalizedString(
				"results.chip_time", "Chip time")), column, row);
		table.setStyleClass(column, row, getStyleName(STYLENAME_HEADER_ROW));
		table.setAlignment(column++, row, Table.HORIZONTAL_ALIGN_CENTER);
		column++;

		table.add(getRunnerRowText(this.iwrb.getLocalizedString("results.name",
				"Name")), column, row);
		table.setStyleClass(column++, row, getStyleName(STYLENAME_HEADER_ROW));
		column++;

		table.add(getRunnerRowText(this.iwrb.getLocalizedString(
				"results.birth_year", "Year")), column, row);
		table.setStyleClass(column, row, getStyleName(STYLENAME_HEADER_ROW));
		table.setAlignment(column++, row, Table.HORIZONTAL_ALIGN_CENTER);
		column++;

		table.add(getRunnerRowText(this.iwrb.getLocalizedString(
				"results.country", "Country")), column, row);
		table.setStyleClass(column, row, getStyleName(STYLENAME_HEADER_ROW));
		table.setAlignment(column++, row, Table.HORIZONTAL_ALIGN_CENTER);
		column++;

		table.add(getRunnerRowText(this.iwrb.getLocalizedString(
				"results.group", "Group")), column, row);
		table.setStyleClass(column, row, getStyleName(STYLENAME_HEADER_ROW));
		table.setHeight(++row, 2);

		return ++row;
	}

	private String getTimeStringFromRunTime(int seconds) {
		Counter counter = new Counter();
		counter.addSeconds(seconds);
		return counter.toString();
	}

	private Text getRunnerRowText(String text) {
		Text t = new Text(text);
		return t;
	}

	private int insertRunGroupIntoTable(Table table, int row, String groupName) {
		table.mergeCells(1, row, this.COLUMN_COUNT, row);
		table.setStyleClass(1, row, getStyleName(STYLENAME_GROUP_ROW));
		table.add(getRunnerRowText(groupName), 1, row++);
		table.setHeight(row, 2);
		return ++row;
	}

	private RaceBusiness getRunBiz() {
		if (this._runBiz == null) {
			try {
				this._runBiz = (RaceBusiness) IBOLookup.getServiceInstance(
						this._iwc, RaceBusiness.class);
			} catch (IBOLookupException e) {
				e.printStackTrace();
			}
		}
		return this._runBiz;
	}

	private GroupBusiness getGroupBiz() {
		if (this._groupBiz == null) {
			try {
				this._groupBiz = (GroupBusiness) IBOLookup.getServiceInstance(
						this._iwc, GroupBusiness.class);
			} catch (IBOLookupException e) {
				e.printStackTrace();
			}
		}
		return this._groupBiz;
	}

	private UserBusiness getUserBiz() {
		if (this._userBiz == null) {
			try {
				this._userBiz = (UserBusiness) IBOLookup.getServiceInstance(
						this._iwc, UserBusiness.class);
			} catch (IBOLookupException e) {
				e.printStackTrace();
			}
		}
		return this._userBiz;
	}

	public String getBundleIdentifier() {
		return MSIConstants.IW_BUNDLE_IDENTIFIER;
	}

	/**
	 * @param run
	 *            The run to set.
	 */
	public void setRun(String runPK) {
		this.runPK = runPK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.Block#getStyleNames()
	 */
	public Map getStyleNames() {
		Map map = new HashMap();
		map.put(STYLENAME_GROUP_ROW, DEFAULT_GROUP_ROW_STYLE);
		map.put(STYLENAME_HEADER_ROW, DEFAULT_HEADER_ROW_STYLE);
		map.put(STYLENAME_INTERFACE, DEFAULT_INTERFACE_STYLE);
		map.put(STYLENAME_LIST_ROW, DEFAULT_LIST_ROW_STYLE);

		return map;
	}
}