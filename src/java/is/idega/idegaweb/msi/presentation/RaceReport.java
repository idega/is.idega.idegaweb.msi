package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.business.RaceReportWriter;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.Season;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.presentation.IWContext;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.DownloadLink;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;

public class RaceReport extends RaceBlock {
	
	protected static final String PARAMETER_SEASON_PK = "prm_season_pk";
	
	public void main(IWContext iwc) throws Exception {
		showList(iwc);
	}
	
	public void showList(IWContext iwc) throws RemoteException, FinderException {
		Form form = new Form();
				
		Table2 table = new Table2();
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);
				
		Collection seasons = getRaceBusiness(iwc).getSeasons();
		Iterator seasonIt = seasons.iterator();
		DropdownMenu seasonDropDown = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_SEASON_PK)); 
		seasonDropDown.addMenuElement("", localize("race_editor.select_season","Select season"));
		while (seasonIt.hasNext()) {
			Group season = (Group)seasonIt.next();
			seasonDropDown.addMenuElement(season.getPrimaryKey().toString(), localize(season.getName(),season.getName()));
		}
		seasonDropDown.setToSubmit();
		
		Collection races = null;
		if (iwc.isParameterSet(PARAMETER_SEASON_PK)) {
			String seasonID = iwc.getParameter(PARAMETER_SEASON_PK);
			seasonDropDown.setSelectedElement(seasonID);
			Season selectedSeason = getRaceBusiness(iwc).getSeasonByGroupId(Integer.valueOf(seasonID));
		    String[] types = {MSIConstants.GROUP_TYPE_RACE};
			races = ConverterUtility.getInstance().convertSeasonToGroup(selectedSeason).getChildGroups(types, true); 
		}
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
		cell.add(seasonDropDown);
		group.createRow().createCell().setHeight("20");
		
		if (iwc.isParameterSet(PARAMETER_SEASON_PK)) {
			row = group.createRow();
			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_editor.race_name", "Name")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_editor.race_date", "Date")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_editor.race_last_registration", "Last registration")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_editor.race_chip_rent", "Chip rent")));
		}
				
		group = table.createBodyRowGroup();
		int iRow = 1;
		
		if (races != null) {
			Iterator iter = races.iterator();
			Race race;
			
			while (iter.hasNext()) {
				row = group.createRow();
				race = ConverterUtility.getInstance().convertGroupToRace((Group) iter.next());
				try {								
					cell = row.createCell();
					cell.add(new Text(race.getName()));
					cell = row.createCell();
					cell.add(new Text(new IWTimestamp(race.getRaceDate()).getDateString("dd.MM.yyyy")));
					cell = row.createCell();
					cell.add(new Text(new IWTimestamp(race.getLastRegistrationDate()).getDateString("dd.MM.yyyy")));
					cell = row.createCell();
					cell.add(new Text(Float.toString(race.getChipRent())));
					row.createCell().add(getXLSLink(race));
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
				iRow++;
			}
		}
		form.add(table);

		add(form);
	}
	
	protected Link getXLSLink(Race race) throws RemoteException {
		DownloadLink link = new DownloadLink(this.getResourceBundle().getImage("xls.gif"));
		link.setTarget(Link.TARGET_NEW_WINDOW);
		link.setMediaWriterClass(RaceReportWriter.class);
		link.addParameter(RaceReportWriter.PARAMETER_RACE_ID, race.getPrimaryKey().toString());

		return link;
	}

}