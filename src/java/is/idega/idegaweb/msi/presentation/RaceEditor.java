package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.data.Event;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.Season;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;

public class RaceEditor extends RaceBlock {
	
	private static final String PARAMETER_ACTION = "mmsi_prm_action";
	private static final String PARAMETER_SEASON_PK = "prm_season_pk";
	private static final String PARAMETER_RACE_PK = "prm_race_pk";
	protected static final String PARAMETER_RACE_NAME = "prm_race_name";
	protected static final String PARAMETER_RACE_DATE = "prm_race_date";
	protected static final String PARAMETER_LAST_REGISTRATION_DATE = "prm_last_registration_date";
	protected static final String PARAMETER_PRICE = "prm_price";
	protected static final String PARAMETER_CHIP_RENT = "prm_chip_rent";
	protected static final String PARAMETER_RACE_EVENTS = "prm_race_event";
	
	private static final int ACTION_VIEW = 1;
	private static final int ACTION_EDIT = 2;
	private static final int ACTION_NEW = 3;
	private static final int ACTION_SAVE_NEW = 4;
	private static final int ACTION_SAVE_EDIT = 5;
	

	public void main(IWContext iwc) throws Exception {
		init(iwc);
	}
	
	protected void init(IWContext iwc) throws Exception {
		switch (parseAction(iwc)) {
			case ACTION_VIEW:
				showList(iwc);
				break;

			case ACTION_NEW:
				showNewEditor(iwc);
				break;

			case ACTION_EDIT:
				showEditor(iwc);
				break;

			case ACTION_SAVE_NEW:
				saveNew(iwc);
				showList(iwc);
				break;

			case ACTION_SAVE_EDIT:
				saveEdit(iwc);
				showList(iwc);
				break;
		}
	}

	private int parseAction(IWContext iwc) {
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			return Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		return ACTION_VIEW;
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
			races = selectedSeason.getChildGroups(types, true); 
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
			cell.add(new Text(localize("race_editor.race_price", "Price")));

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
					Link edit = new Link(getEditIcon(localize("edit", "Edit")));
					edit.addParameter(PARAMETER_SEASON_PK, iwc.getParameter(PARAMETER_SEASON_PK));
					edit.addParameter(PARAMETER_RACE_PK, race.getPrimaryKey().toString());
					edit.addParameter(PARAMETER_ACTION, ACTION_EDIT);
								
					cell = row.createCell();
					cell.add(new Text(race.getName()));
					cell = row.createCell();
					cell.add(new Text(new IWTimestamp(race.getRaceDate()).getDateString("dd.MM.yyyy")));
					cell = row.createCell();
					cell.add(new Text(new IWTimestamp(race.getLastRegistrationDate()).getDateString("dd.MM.yyyy")));
					cell = row.createCell();
					cell.add(new Text(Float.toString(race.getPrice())));
					cell = row.createCell();
					cell.add(new Text(Float.toString(race.getChipRent())));
					row.createCell().add(edit);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
				iRow++;
			}
		}
		form.add(table);
		form.add(new Break());
		if (iwc.isParameterSet(PARAMETER_SEASON_PK)) {
			SubmitButton newLink = (SubmitButton) getButton(new SubmitButton(localize("race_editor.new_race", "New race"), PARAMETER_ACTION, String.valueOf(ACTION_NEW)));
			form.add(newLink);
		}
		add(form);
	}

	public void showNewEditor(IWContext iwc) throws java.rmi.RemoteException {
		String seasonID = iwc.getParameter(PARAMETER_SEASON_PK);
		CreateRaceForm form = new CreateRaceForm(seasonID);
		add(form);
	}

	public void showEditor(IWContext iwc) throws java.rmi.RemoteException, FinderException {
		String raceID = iwc.getParameter(PARAMETER_RACE_PK);
		Form form = new Form();
		form.maintainParameter(PARAMETER_SEASON_PK);
		form.maintainParameter(PARAMETER_RACE_PK);
		TextInput race = new TextInput(PARAMETER_RACE_PK);
		DateInput raceDate = new DateInput(PARAMETER_RACE_DATE);
		DateInput lastRegistrationDate = new DateInput(PARAMETER_LAST_REGISTRATION_DATE);
		TextInput price = new TextInput(PARAMETER_PRICE);
		price.setAsFloat(localize("not_a_valid_float", "Not a valid number"));
		TextInput rent = new TextInput(PARAMETER_CHIP_RENT);
		rent.setAsFloat(localize("not_a_valid_float", "Not a valid number"));
		
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label label = new Label(localize("race_editor.race", "Race"), race);
		layer.add(label);
		layer.add(race);
		form.add(layer);
		form.add(new Break());
		
		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label();
		label.setLabel(localize("race_editor.race_date", "Race date"));
		layer.add(label);
		layer.add(raceDate);
		form.add(layer);
		form.add(new Break());
		
		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("race_editor.last_registration_date", "Last registration date"), lastRegistrationDate);
		layer.add(label);
		layer.add(lastRegistrationDate);
		form.add(layer);
		form.add(new Break());

		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("race_editor.price", "Verð"), price);
		layer.add(label);
		layer.add(price);
		form.add(layer);
		form.add(new Break());

		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("race_editor.chip_rent", "Chip rent"), rent);
		layer.add(label);
		layer.add(rent);
		form.add(layer);
		form.add(new Break());

		Collection events = getRaceBusiness(iwc).getEvents();
		
		if (events != null) {
			Iterator it = events.iterator();
			while (it.hasNext()) {
				Event event = (Event) it.next();
				CheckBox check = new CheckBox(PARAMETER_RACE_EVENTS, event.getName());
				layer = new Layer(Layer.DIV);
				layer.setStyleClass(STYLENAME_FORM_ELEMENT);
				label = new Label(event.getName(), check);
				layer.add(label);
				layer.add(check);
				form.add(layer);
				form.add(new Break());
			}
		}
		
		SubmitButton save = (SubmitButton) getButton(new SubmitButton(localize("save", "Save"), PARAMETER_ACTION, String.valueOf(ACTION_SAVE_EDIT)));
		SubmitButton cancel = (SubmitButton) getButton(new SubmitButton(localize("cancel", "Cancel"), PARAMETER_ACTION, String.valueOf(ACTION_VIEW)));

		form.add(save);
		form.add(cancel);
		
		if (raceID != null) {
			Group selectedGroupRace = getRaceBusiness(iwc).getSeasonByGroupId(Integer.valueOf(raceID.toString()));
			Race selectedRace = ConverterUtility.getInstance().convertGroupToRace(selectedGroupRace);
			race.setValue(selectedRace.getName());
			race.setDisabled(true);
			if (selectedRace.getRaceDate() != null) { 
				raceDate.setDate(new IWTimestamp(selectedRace.getRaceDate()).getDate());
			}
			if (selectedRace.getLastRegistrationDate() != null) { 
				lastRegistrationDate.setDate(new IWTimestamp(selectedRace.getLastRegistrationDate()).getDate());
			}
			
			price.setValue(Float.toString(selectedRace.getPrice()));
			rent.setValue(Float.toString(selectedRace.getChipRent()));
		}
		add(form);
	}
	
	public void saveNew(IWContext iwc) throws java.rmi.RemoteException {
		String seasonID = iwc.getParameter(PARAMETER_SEASON_PK);
		String name = iwc.getParameter(PARAMETER_RACE_NAME);
		String raceDate = iwc.getParameter(PARAMETER_RACE_DATE);
		String lastRegistration = iwc.getParameter(PARAMETER_LAST_REGISTRATION_DATE);
		String price = iwc.getParameter(PARAMETER_PRICE);
		String rent = iwc.getParameter(PARAMETER_CHIP_RENT);
		
		String events[] = iwc.getParameterValues(PARAMETER_RACE_EVENTS);
		
		Race race = getRaceBusiness(iwc).createRace(seasonID, name, raceDate, lastRegistration, price, rent);
		getRaceBusiness(iwc).addEventsToRace(race, events);		
	}
	
	private void saveEdit(IWContext iwc) throws java.rmi.RemoteException {
		String raceID = iwc.getParameter(PARAMETER_RACE_PK);
		Race race = null;
		if (raceID != null) {
			try {
				race = ConverterUtility.getInstance().convertGroupToRace(new Integer(raceID));
			} 
			catch (FinderException e){
				//no year found, nothing saved
			}
		}
		if (race != null) {
			String raceDate = iwc.getParameter(PARAMETER_RACE_DATE);
			String lastRegistration = iwc.getParameter(PARAMETER_LAST_REGISTRATION_DATE);
			String price = iwc.getParameter(PARAMETER_PRICE);
			String rent = iwc.getParameter(PARAMETER_CHIP_RENT);
			
			String events[] = iwc.getParameterValues(PARAMETER_RACE_EVENTS);

			
			getRaceBusiness(iwc).updateRace(race, raceDate, lastRegistration, price, rent);
			getRaceBusiness(iwc).addEventsToRace(race, events);		
			
			race.store();
		}
	}
}