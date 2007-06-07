package is.idega.idegaweb.msi.presentation;


import is.idega.idegaweb.msi.data.Event;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.util.Collection;
import java.util.Iterator;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

public class CreateRaceForm extends RaceBlock {
	
	private static final String PARAMETER_ACTION = "msi_prm_action";
	private static final String PARAMETER_SEASON_PK = "prm_season_pk";
	private static final String PARAMETER_GROUP_ID = "ic_group_id";
	private static final int ACTION_SAVE = 4;

	private static final String PARAMETER_RACE_NAME = RaceEditor.PARAMETER_RACE_NAME;
	private static final String PARAMETER_RACE_PRICE = RaceEditor.PARAMETER_PRICE;
	private static final String PARAMETER_RACE_CHIP_RENT = RaceEditor.PARAMETER_CHIP_RENT;
	private static final String PARAMETER_RACE_DATE = RaceEditor.PARAMETER_RACE_DATE;
	private static final String PARAMETER_RACE_LAST_REGISTRATION = RaceEditor.PARAMETER_LAST_REGISTRATION_DATE;
	private static final String PARAMETER_RACE_EVENTS = RaceEditor.PARAMETER_RACE_EVENTS;

	private String seasonID = null;
	
	public CreateRaceForm (String seasonID) {
		super();
		this.seasonID = seasonID;
	}
	
	private int parseAction(IWContext iwc) {
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			return Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		} else {
			return 0;
		}
	}
	
	public void main(IWContext iwc) throws Exception {
		
		switch (parseAction(iwc)) {
			case ACTION_SAVE: {
				String name = iwc.getParameter(PARAMETER_RACE_NAME);
				String raceDate = iwc.getParameter(PARAMETER_RACE_DATE);
				String lastRegistration = iwc.getParameter(PARAMETER_RACE_LAST_REGISTRATION);
				String price = iwc.getParameter(PARAMETER_RACE_PRICE);
				String rent = iwc.getParameter(PARAMETER_RACE_CHIP_RENT);
				
				String events[] = iwc.getParameterValues(PARAMETER_RACE_EVENTS);
				
				Race race = getRaceBusiness(iwc).createRace(seasonID, name, raceDate, lastRegistration, price, rent);
				getRaceBusiness(iwc).addEventsToRace(race, events);
			}
		}
		
		IWResourceBundle iwrb = getResourceBundle(iwc);

		Form form = new Form();
		form.maintainParameter(PARAMETER_SEASON_PK);
		form.maintainParameter(PARAMETER_GROUP_ID);

		Table table = new Table();
		table.setCellpadding(3);
		int row = 1;
		
		table.add(new Text(iwrb.getLocalizedString("create_race_form.race_name","Race name")+": "), 1, row);
		table.add(new TextInput(PARAMETER_RACE_NAME), 2, row++);
		table.setHeight(row++, 12);

		DateInput raceDate = new DateInput(PARAMETER_RACE_DATE);
		DateInput lastRegistrationDate = new DateInput(PARAMETER_RACE_LAST_REGISTRATION);
		TextInput price = new TextInput(PARAMETER_RACE_PRICE);
		price.setAsFloat(localize("not_a_valid_float", "Not a valid number"));
		TextInput rent = new TextInput(PARAMETER_RACE_CHIP_RENT);
		rent.setAsFloat(localize("not_a_valid_float", "Not a valid number"));

		
		table.add(new Text(iwrb.getLocalizedString("create_race_form.race_date", "Race date")), 1, row);
		table.add(raceDate, 2, row++);
		table.add(new Text(iwrb.getLocalizedString("create_race_form.race_last_registration", "Last registration")), 1, row);
		table.add(lastRegistrationDate, 2, row++);
		table.add(new Text(iwrb.getLocalizedString("create_race_form.race_price", "Price")), 1, row);
		table.add(price, 2, row++);
		table.add(new Text(iwrb.getLocalizedString("create_race_form.race_chip_rent", "Chip rent")), 1, row);
		table.add(rent, 2, row++);
		
		Collection events = getRaceBusiness(iwc).getEvents();
		
		if (events != null) {
			table.add(new Text(iwrb.getLocalizedString("create_race_form.events", "Events")), 1, row);

			Iterator it = events.iterator();
			while (it.hasNext()) {
				Event event = (Event) it.next();
				CheckBox check = new CheckBox(PARAMETER_RACE_EVENTS, event.getName());
				table.add(check, 2, row);
				table.add(Text.getBreak(), 2, row);
				table.add(event.getName(), 2, row++);
			}
		}
		
		form.add(table);
		
		SubmitButton create = new SubmitButton(iwrb.getLocalizedString("create_race_form.save", "Save"), PARAMETER_ACTION, String.valueOf(ACTION_SAVE));
		table.setHeight(row++, 12);
		table.add(create, 1, row);
		
		add(form);
	}

	public String getBundleIdentifier() {
		return MSIConstants.IW_BUNDLE_IDENTIFIER;
	}
}