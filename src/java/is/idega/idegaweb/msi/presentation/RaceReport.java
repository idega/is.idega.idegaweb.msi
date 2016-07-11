package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.business.RaceReportWriter;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.Season;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;
import javax.faces.context.FacesContext;

import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.DownloadLink;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;
import com.idega.util.PresentationUtil;

public class RaceReport extends RaceBlock {
	
	protected static final String PARAMETER_SEASON_PK = "prm_season_pk";

	/*
	 * (non-Javadoc)
	 * @see com.idega.presentation.Block#encodeBegin(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeBegin(FacesContext fc) throws IOException {
		super.encodeBegin(fc);

		if (getSportUnionBundle() != null) {
			ArrayList<String> styleSheets = new ArrayList<String>();
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/viewer.css"));
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/select.css"));
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/input.css"));
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/button.css"));
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/label.css"));
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/sport_union.css"));
			PresentationUtil.addStyleSheetsToHeader(
					IWContext.getIWContext(fc), styleSheets);
		}
	}
	
	public void main(IWContext iwc) throws Exception {
		showList(iwc);
	}
	
	public void showList(IWContext iwc) throws RemoteException, FinderException {
		Form form = new Form();
				
		Table2 table = new Table2();
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setStyleClass("table");
				
		Collection<Group> seasons = getRaceBusiness(iwc).getSeasons();
		Iterator<Group> seasonIt = seasons.iterator();
		DropdownMenu seasonDropDown = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_SEASON_PK)); 
		seasonDropDown.addMenuElement("", localize("race_editor.select_season","Select season"));
		while (seasonIt.hasNext()) {
			Group season = (Group)seasonIt.next();
			seasonDropDown.addMenuElement(season.getPrimaryKey().toString(), localize(season.getName(),season.getName()));
		}
		seasonDropDown.setToSubmit();
		
		Collection<Group> races = null;
		Season selectedSeason = null;
		if (iwc.isParameterSet(PARAMETER_SEASON_PK)) {
			String seasonID = iwc.getParameter(PARAMETER_SEASON_PK);
			seasonDropDown.setSelectedElement(seasonID);
			selectedSeason = getRaceBusiness(iwc).getSeasonByGroupId(Integer.valueOf(seasonID));
		    String[] types = {MSIConstants.GROUP_TYPE_RACE};
			races = ConverterUtility.getInstance().convertSeasonToGroup(selectedSeason).getChildGroups(types, true); 
		}
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = null;
		
		if (iwc.isParameterSet(PARAMETER_SEASON_PK)) {
			row = group.createRow();
			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("race_editor.race_name", "Name")));

			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("race_editor.race_date", "Date")));

			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("race_editor.race_last_registration", "Last registration")));

			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("race_editor.race_chip_rent", "Chip rent")));

			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("edit", "Edit")));
		}
				
		group = table.createBodyRowGroup();
		row = group.createRow();
		cell = row.createCell();
		cell.setStyleClass("text");
		cell.add(new Text(localize("race_editor.all_races", "All races")));
		cell = row.createCell();
		cell.setStyleClass("text");
		cell.add(new Text(""));
		cell = row.createCell();
		cell.setStyleClass("text");
		cell.add(new Text(""));
		cell = row.createCell();
		cell.setStyleClass("text");
		cell.add(new Text(""));
		cell = row.createCell();
		cell.setStyleClass("text");
		cell.add(getXLSLink(null, selectedSeason));
		
		if (races != null) {
			Iterator<Group> iter = races.iterator();
			Race race;

			int iRow = 1;
			while (iter.hasNext()) {
				row = group.createRow();
				race = ConverterUtility.getInstance().convertGroupToRace((Group) iter.next());
				try {								
					cell = row.createCell();
					cell.setStyleClass("text");
					cell.add(new Text(race.getName()));
					cell = row.createCell();
					cell.setStyleClass("text");
					cell.add(new Text(new IWTimestamp(race.getRaceDate()).getDateString("dd.MM.yyyy")));
					cell = row.createCell();
					cell.setStyleClass("text");
					cell.add(new Text(new IWTimestamp(race.getLastRegistrationDate()).getDateString("dd.MM.yyyy")));
					cell = row.createCell();
					cell.setStyleClass("text");
					cell.add(new Text(Float.toString(race.getChipRent())));
					cell = row.createCell();
					cell.setStyleClass("text");
					cell.add(getXLSLink(race, null));
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				if (iRow++ % 2 == 0) {
					row.setStyleClass("tableRow even evenRow");
				} else {
					row.setStyleClass("tableRow odd oddRow");
				}
			}
		}

		Label raceTypeDropdownLabel = new Label();
		raceTypeDropdownLabel.setLabel(getLocalizedString("msi_seasons", "Season", iwc));

		Layer raceTypeLayer = new Layer();
		raceTypeLayer.setStyleClass("formItem");
		raceTypeLayer.add(raceTypeDropdownLabel);
		raceTypeLayer.add(seasonDropDown);

		Layer filterLayer = new Layer();
		filterLayer.setStyleClass("formSection navigationLayer");
		filterLayer.add(raceTypeLayer);

		form.add(filterLayer);

		form.add(table);

		add(form);
	}
	
	protected Link getXLSLink(Race race, Season selectedSeason) throws RemoteException {
		DownloadLink link = new DownloadLink(this.getResourceBundle().getImage("xls.gif"));
		link.setTarget(Link.TARGET_NEW_WINDOW);
		link.setMediaWriterClass(RaceReportWriter.class);
		if (race != null) {
			link.addParameter(RaceReportWriter.PARAMETER_RACE_ID, race.getPrimaryKey().toString());
		}
		if (selectedSeason != null) {
			link.addParameter(RaceReportWriter.PARAMETER_SEASON_ID, selectedSeason.getPrimaryKey().toString());			
		}

		return link;
	}

}