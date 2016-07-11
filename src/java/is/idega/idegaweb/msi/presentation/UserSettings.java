package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.business.RaceBusiness;
import is.idega.idegaweb.msi.business.RaceParticipantInfo;
import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.data.RaceVehicleType;

import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.core.builder.data.ICPage;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.PostalCode;
import com.idega.presentation.ExceptionWrapper;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Span;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.business.UserBusiness;
import com.idega.util.IWTimestamp;

/**
 * @author Pall Helgason
 * @version 1.0
 */
public class UserSettings extends RaceBlock {

	private final static int ACTION_VIEW_FORM = 1;
	private final static int ACTION_FORM_SUBMIT = 2;

	private final static String PARAMETER_FORM_SUBMIT = "us_sbmt";

	private final static String PARAMETER_HOME_PAGE = "us_home_page";
	private final static String PARAMETER_VEHICLE_TYPE = "us_vehicle_type";
	private final static String PARAMETER_VEHICLE_SUBTYPE = "us_vehicle_subtype";
	private final static String PARAMETER_ENGINE = "us_engine";
	private final static String PARAMETER_ENGINE_CC = "us_engine_cc";
	private final static String PARAMETER_MODEL = "us_model";
	private final static String PARAMETER_RACE_NUMBER_MX = "us_race_number_mx";
	private final static String PARAMETER_RACE_NUMBER_SNOCROSS = "us_race_number_snocross";
	private final static String PARAMETER_TRANSPONDER = "us_transponder";
	private final static String PARAMETER_TEAM = "us_team";
	private final static String PARAMETER_SPONSOR = "us_sponsor";
	private final static String PARAMETER_BODY_NUMBER = "us_body_number";


	private final static String KEY_PREFIX = "msi.";
	private final static String KEY_NAME = KEY_PREFIX + "name";
	private final static String KEY_SSN = KEY_PREFIX + "ssn";
	private final static String KEY_ADDRESS = KEY_PREFIX + "address";
	private final static String KEY_PO = KEY_PREFIX + "po";
	private final static String KEY_CLUB = KEY_PREFIX + "club";
	
	private final static String KEY_EMAIL = KEY_PREFIX + "email";
	private final static String KEY_UPDATE = KEY_PREFIX + "update";
	private final static String KEY_PHONE_HOME = KEY_PREFIX + "phone_home";
	private final static String KEY_PHONE_MOBILE = KEY_PREFIX + "phone_mobile";
	private final static String KEY_PHONE_WORK = KEY_PREFIX + "phone_work";

	private final static String KEY_HOME_PAGE = KEY_PREFIX + "home_page";
	private final static String KEY_RACE_NUMBER_MX = KEY_PREFIX + "race_number_mx";
	private final static String KEY_RACE_NUMBER_SNOCROSS = KEY_PREFIX + "race_number_snocross";
	private final static String KEY_VEHICLE_TYPE = KEY_PREFIX + "vehicle_type";
	private final static String KEY_VEHICLE_SUBTYPE = KEY_PREFIX + "vehicle_subtype";
	private final static String KEY_ENGINE = KEY_PREFIX + "engine";
	private final static String KEY_ENGINE_CC = KEY_PREFIX + "cc";
	private final static String KEY_MODEL = KEY_PREFIX + "model";

	private final static String KEY_TRANSPONDER = KEY_PREFIX + "transponder";
	private final static String KEY_TEAM = KEY_PREFIX + "team";
	private final static String KEY_SPONSOR = KEY_PREFIX + "sponsor";
	private final static String KEY_BODY_NUMBER = KEY_PREFIX + "body_number";

	private final static String KEY_PREFERENCES_SAVED = KEY_PREFIX + "preferenced_saved";

	private final static String DEFAULT_NAME = "Name";
	private final static String DEFAULT_SSN = "SSN";
	private final static String DEFAULT_ADDRESS = "Address";
	private final static String DEFAULT_PO = "Postal code";
	private final static String DEFAULT_CLUB = "Club";
	private final static String DEFAULT_EMAIL = "Email";
	private final static String DEFAULT_UPDATE = "Update";
	private final static String DEFAULT_PHONE_HOME = "Phone (home)";
	private final static String DEFAULT_PHONE_MOBILE = "Phone (mobile)";
	private final static String DEFAULT_PHONE_WORK = "Phone (work)";

	private final static String DEFAULT_HOME_PAGE = "Home page";
	private final static String DEFAULT_RACE_NUBMER_MX = "Race number MX";
	private final static String DEFAULT_RACE_NUBMER_SNOCROSS = "Race number Snocross";
	private final static String DEFAULT_VEHICLE_TYPE = "Vehicle type";
	private final static String DEFAULT_VEHICLE_SUBTYPE = "Vehicle subtype";
	private final static String DEFAULT_ENGINE = "Engine";
	private final static String DEFAULT_ENGINE_CC = "CC";
	private final static String DEFAULT_MODEL = "Model";

	private final static String DEFAULT_TRANSPONDER = "Transponder";
	private final static String DEFAULT_TEAM = "Team";
	private final static String DEFAULT_SPONSOR = "Sponsor";
	private final static String DEFAULT_BODY_NUMBER = "Body number";

	private final static String DEFAULT_PREFERENCES_SAVED = "Your preferences has been saved.";

	private RaceParticipantInfo info = null;


	public UserSettings() {
	}

	public void main(IWContext iwc) {
		if (!iwc.isLoggedOn()) {
			return;
		}
		
		info = getRaceParticipantInfo(iwc);
		info.setUser(iwc.getCurrentUser());
		
		try {
			int action = parseAction(iwc);
			switch (action) {
				case ACTION_VIEW_FORM:
					viewForm(iwc);
					break;
				case ACTION_FORM_SUBMIT:
					updatePreferences(iwc);
					break;
			}
		}
		catch (Exception e) {
			super.add(new ExceptionWrapper(e, this));
		}
	}

	private int parseAction(final IWContext iwc) {
		int action = ACTION_VIEW_FORM;
		if (iwc.isParameterSet(PARAMETER_FORM_SUBMIT)) {
			String value = iwc.getParameter(PARAMETER_FORM_SUBMIT);
			if (value.equals(Boolean.TRUE.toString())) {
				action = ACTION_FORM_SUBMIT;
			}
		}
		return action;
	}

	private RaceParticipantInfo getRaceParticipantInfo(IWContext iwc) {
		RaceParticipantInfo tmp = (RaceParticipantInfo) iwc.getSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT_INFO);
		if (tmp == null) {
			tmp = new RaceParticipantInfo();
			iwc.setSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT_INFO, tmp);
		}
		
		return tmp;
	}
	
	private void viewForm(IWContext iwc) throws java.rmi.RemoteException {
		Form form = new Form();
		form.setMultiPart();
		form.addParameter(PARAMETER_FORM_SUBMIT, Boolean.FALSE.toString());
		form.setID("citizenAccountPreferences");
		form.setStyleClass("citizenForm");

		Layer header = new Layer(Layer.DIV);
		header.setStyleClass("header");
		form.add(header);

		Heading1 heading = new Heading1(this.getResourceBundle(iwc).getLocalizedString("citizen_preferences", "Citizen preferences"));
		header.add(heading);

		Layer contents = new Layer(Layer.DIV);
		contents.setStyleClass("formContents");
		form.add(contents);

		Layer section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		contents.add(section);

		UserBusiness ub = getUserBusiness(iwc);
		RaceBusiness raceBusiness = getRaceBusiness(iwc);

		Email mail = ub.getUserMail(this.info.getUser());
		Phone homePhone = null;
		try {
			homePhone = ub.getUsersHomePhone(this.info.getUser());
		}
		catch (NoPhoneFoundException e) {
			e.printStackTrace();
		}
		Phone mobilePhone = null;
		try {
			mobilePhone = ub.getUsersMobilePhone(this.info.getUser());
		}
		catch (NoPhoneFoundException e) {
			e.printStackTrace();
		}
		Phone workPhone = null;
		try {
			workPhone = ub.getUsersWorkPhone(this.info.getUser());
		}
		catch (NoPhoneFoundException e) {
			e.printStackTrace();
		}
		Address address = getCoAddress(iwc);
		if (address == null) {
			address = getMainAddress(iwc);

		}
		PostalCode postal = null;
		if (address != null) {
			postal = address.getPostalCode();
		}

		RaceUserSettings settings = raceBusiness.getRaceUserSettings(info.getUser());
		
		String homePage = settings != null ? settings.getHomePage() : null;
		RaceNumber raceNumberMX = settings != null ? settings.getRaceNumberMX() : null;
		RaceNumber raceNumberSnocross = settings != null ? settings.getRaceNumberSnocross() : null;
		RaceVehicleType vehicleType = settings != null ? settings.getVehicleType() : null;
		RaceVehicleType vehicleSubType = settings != null ? settings.getVehicleSubType() : null;
		String engine = settings != null ? settings.getEngine() : null;
		String engineCC = settings != null ? settings.getEngineCC() : null;
		String model = settings != null ? settings.getModel() : null;
		String transponder = settings != null ? settings.getTransponderNumber() : null;
		String team = settings != null ? settings.getTeam() : null;
		String sponsor = settings != null ? settings.getSponsor() : null;
		String bodyNumber = settings != null ? settings.getBodyNumber() : null;

		TextInput tiHomePage = new TextInput(PARAMETER_HOME_PAGE);
		if (homePage != null) {
			tiHomePage.setContent(homePage);
		}

		boolean mxSelected = false;
		PresentationObject tiRaceNumberMX = null;//new TextInput(PARAMETER_RACE_NUMBER_MX);
		if (raceNumberMX != null) {
			tiRaceNumberMX = new TextInput();
			((TextInput)tiRaceNumberMX).setContent(raceNumberMX.getRaceNumber());
			((TextInput)tiRaceNumberMX).setDisabled(true);
			mxSelected = true;
		} else {
			Collection raceNumbers = getRaceBusiness(iwc).getMXRaceNumbers();
			tiRaceNumberMX = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_RACE_NUMBER_MX)); 
			((DropdownMenu)tiRaceNumberMX).addMenuElement("", localize("race_editor.select_race_number","Select race number"));
			if (raceNumbers != null) {
				Iterator it = raceNumbers.iterator();
				while (it.hasNext()) {
					RaceNumber number = (RaceNumber) it.next();
					((DropdownMenu)tiRaceNumberMX).addMenuElement(number.getPrimaryKey().toString(), number.getRaceNumber());
				}
			}
		}

		boolean snocrossSelected = false;
		PresentationObject tiRaceNumberSnocross = null;//new TextInput(PARAMETER_RACE_NUMBER_SNOCROSS);
		if (raceNumberSnocross != null) {
			tiRaceNumberSnocross = new TextInput();
			((TextInput)tiRaceNumberSnocross).setContent(raceNumberSnocross.getRaceNumber());
			((TextInput)tiRaceNumberSnocross).setDisabled(true);
			snocrossSelected = true;
		} else {
			Collection raceNumbers = getRaceBusiness(iwc).getSnocrossRaceNumbers();
			tiRaceNumberSnocross = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_RACE_NUMBER_SNOCROSS)); 
			((DropdownMenu)tiRaceNumberSnocross).addMenuElement("", localize("race_editor.select_race_number","Select race number"));

			if (raceNumbers != null) {
				Iterator it = raceNumbers.iterator();
				while (it.hasNext()) {
					RaceNumber number = (RaceNumber) it.next();
					((DropdownMenu)tiRaceNumberSnocross).addMenuElement(number.getPrimaryKey().toString(), number.getRaceNumber());
				}
			}
		}

		Collection vehicleTypes = null;
		try {
			vehicleTypes = getRaceBusiness(iwc).getRaceVehicleTypeHome().findAllParents();
		} catch (FinderException e) {
			e.printStackTrace();
		} 
		
		DropdownMenu tiVehicleType = new DropdownMenu(PARAMETER_VEHICLE_TYPE);
		tiVehicleType.addMenuElement("", localize("race_editor.select_vehicle_type","Select vehicle type"));
		if (vehicleTypes != null && !vehicleTypes.isEmpty()) {
			Iterator it = vehicleTypes.iterator();
			while (it.hasNext()) {
				RaceVehicleType type = (RaceVehicleType) it.next();
				tiVehicleType.addMenuElement(type.getPrimaryKey().toString(), localize(type.getLocalizationKey(), type.getLocalizationKey()));
			}
			
			tiVehicleType.setToSubmit();
		}
		
		RaceVehicleType selectedType = null;
		if (iwc.isParameterSet(PARAMETER_VEHICLE_TYPE)) {
			try {
				selectedType = getRaceBusiness(iwc).getRaceVehicleTypeHome().findByPrimaryKey(new Integer(iwc.getParameter(PARAMETER_VEHICLE_TYPE)));
			} catch (NumberFormatException e) {
			} catch (FinderException e) {
			}
		}
		else if (vehicleType != null) {
			selectedType = vehicleType;
		}

		DropdownMenu tiVehicleSubtype = new DropdownMenu(PARAMETER_VEHICLE_SUBTYPE);
		if (selectedType != null) {
			tiVehicleType.setSelectedElement(selectedType.getPrimaryKey().toString());
			
			Collection subTypes = null;
			try {
				subTypes = getRaceBusiness(iwc).getRaceVehicleTypeHome().findSubtypeByParent(selectedType);
			} catch (FinderException e) {
			}
			
			tiVehicleSubtype.addMenuElement("", localize("race_editor.select_vehicle_subtype","Select vehicle subtype"));
			if (subTypes != null && !subTypes.isEmpty()) {
				Iterator it = subTypes.iterator();
				while (it.hasNext()) {
					RaceVehicleType type = (RaceVehicleType) it.next();
					tiVehicleSubtype.addMenuElement(type.getPrimaryKey().toString(), localize(type.getLocalizationKey(), type.getLocalizationKey()));
				}
			}
		}
		
		if (vehicleSubType != null) {
			tiVehicleSubtype.setSelectedElement(vehicleSubType.getPrimaryKey().toString());
		}

		DropdownMenu tiEngine = new DropdownMenu(PARAMETER_ENGINE);
		tiEngine.addMenuElement("", localize("race_editor.select_engine_size","Select engine size"));
		tiEngine.addMenuElement("2T", "2T");
		tiEngine.addMenuElement("4T", "4T");
		if (engine != null) {
			tiEngine.setSelectedElement(engine);
		}

		TextInput tiCC = new TextInput(PARAMETER_ENGINE_CC);
		if (engineCC != null) {
			tiCC.setContent(engineCC);
		}

		TextInput tiModel = new TextInput(PARAMETER_MODEL);
		if (model != null) {
			tiModel.setContent(model);
		}

		TextInput tiTransponder = new TextInput(PARAMETER_TRANSPONDER);
		if (transponder != null) {
			tiTransponder.setContent(transponder);
		}
		
		TextInput tiTeam = new TextInput(PARAMETER_TEAM);
		if (team != null) {
			tiTeam.setContent(team);
		}
		
		TextInput tiSponsor = new TextInput(PARAMETER_SPONSOR);
		if (sponsor != null) {
			tiSponsor.setContent(sponsor);
		}
		
		TextInput tiBodyNumber = new TextInput(PARAMETER_BODY_NUMBER);
		if (bodyNumber != null) {
			tiBodyNumber.setContent(bodyNumber);
		}
		
		Layer formItem;
		Label label;

		Layer layer = new Layer(Layer.DIV);
		layer.setID("personalInfo");
		section.add(layer);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label();
		label.setLabel(this.getResourceBundle(iwc).getLocalizedString(KEY_NAME, DEFAULT_NAME));
		formItem.add(label);
		if (info.getUser() != null) {
			formItem.add(new Span(new Text(info.getUser().getName())));
		} 
		else {
			formItem.add(new Span(new Text("")));			
		}
		layer.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label();
		label.setLabel(this.getResourceBundle(iwc).getLocalizedString(KEY_SSN, DEFAULT_SSN));
		formItem.add(label);
		if (info.getUser() != null) {
			formItem.add(new Span(new Text(info.getUser().getPersonalID())));
		} 
		else {
			formItem.add(new Span(new Text("")));
		}
		layer.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label();
		label.setLabel(this.getResourceBundle(iwc).getLocalizedString(KEY_ADDRESS, DEFAULT_ADDRESS));
		formItem.add(label);
		if (address != null) {
			formItem.add(new Span(new Text(address.getStreetAddress())));
		} 
		else {
			formItem.add(new Span(new Text("")));
		}
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label();
		label.setLabel(this.getResourceBundle(iwc).getLocalizedString(KEY_PO, DEFAULT_PO));
		formItem.add(label);
		if (postal != null) {
			formItem.add(new Span(new Text(postal.getPostalAddress())));
		} 
		else {
			formItem.add(new Span(new Text("")));
		}
		layer.add(formItem);
		formItem = new Layer(Layer.DIV);

		formItem.setStyleClass("formItem");
		label = new Label();
		label.setLabel(this.getResourceBundle(iwc).getLocalizedString(KEY_CLUB, DEFAULT_CLUB));
		formItem.add(label);
		formItem.add(new Span(new Text("")));
		layer.add(formItem);
		
		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");

		layer = new Layer(Layer.DIV);
		layer.setID("contactMethods");
		section.add(layer);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label();
		label.setLabel(this.getResourceBundle(iwc).getLocalizedString(KEY_EMAIL, DEFAULT_EMAIL));
		formItem.add(label);
		formItem.setStyleAttribute("word-wrap:break-word;");
		if (mail != null && mail.getEmailAddress() != null) {
			formItem.add(new Span(new Text(mail.getEmailAddress())));
		} 
		else {
			formItem.add(new Span(new Text("")));
		}		
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label();
		label.setLabel(this.getResourceBundle(iwc).getLocalizedString(KEY_PHONE_HOME, DEFAULT_PHONE_HOME));
		formItem.add(label);
		if (homePhone != null && homePhone.getNumber() != null) {
			formItem.add(new Span(new Text(homePhone.getNumber())));
		} 
		else {
			formItem.add(new Span(new Text("")));
		}
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label();
		label.setLabel(this.getResourceBundle(iwc).getLocalizedString(KEY_PHONE_MOBILE, DEFAULT_PHONE_MOBILE));
		formItem.add(label);
		if (mobilePhone != null && mobilePhone.getNumber() != null) {
			formItem.add(new Span(new Text(mobilePhone.getNumber())));
		} 
		else {
			formItem.add(new Span(new Text("")));
		}			
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label();
		label.setLabel(this.getResourceBundle(iwc).getLocalizedString(KEY_PHONE_WORK, DEFAULT_PHONE_WORK));
		formItem.add(label);
		if (workPhone != null && workPhone.getNumber() != null) {
			formItem.add(new Span(new Text(workPhone.getNumber())));
		} 
		else {
			formItem.add(new Span(new Text("")));
		}			
			
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_HOME_PAGE, DEFAULT_HOME_PAGE), tiHomePage);
		formItem.add(label);
		formItem.add(tiHomePage);
		layer.add(formItem);

		section.add(clearLayer);

		layer = new Layer(Layer.DIV);
		layer.setID("raceNumber");
		section.add(layer);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		if (mxSelected) {
			label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_RACE_NUMBER_MX, DEFAULT_RACE_NUBMER_MX), (TextInput)tiRaceNumberMX);
		} else {
			label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_RACE_NUMBER_MX, DEFAULT_RACE_NUBMER_MX), (DropdownMenu)tiRaceNumberMX);			
		}
		formItem.add(label);
		formItem.add(tiRaceNumberMX);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		if (snocrossSelected) {
			label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_RACE_NUMBER_SNOCROSS, DEFAULT_RACE_NUBMER_SNOCROSS), (TextInput)tiRaceNumberSnocross);			
		} else {
			label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_RACE_NUMBER_SNOCROSS, DEFAULT_RACE_NUBMER_SNOCROSS), (DropdownMenu)tiRaceNumberSnocross);
		}
		formItem.add(label);
		formItem.add(tiRaceNumberSnocross);
		layer.add(formItem);

		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItemAside");
		label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_TRANSPONDER, DEFAULT_TRANSPONDER), tiTransponder);
		formItem.add(label);
		formItem.add(tiTransponder);
		layer.add(formItem);

		layer = new Layer(Layer.DIV);
		layer.setID("vehicleInfo");
		section.add(layer);
				
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_VEHICLE_TYPE, DEFAULT_VEHICLE_TYPE), tiVehicleType);
		formItem.add(label);
		formItem.add(tiVehicleType);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_VEHICLE_SUBTYPE, DEFAULT_VEHICLE_SUBTYPE), tiVehicleSubtype);
		formItem.add(label);
		formItem.add(tiVehicleSubtype);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_ENGINE_CC, DEFAULT_ENGINE_CC), tiCC);
		formItem.add(label);
		formItem.add(tiCC);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_ENGINE, DEFAULT_ENGINE), tiEngine);
		formItem.add(label);
		formItem.add(tiEngine);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItemAside");
		label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_BODY_NUMBER, DEFAULT_BODY_NUMBER), tiBodyNumber);
		formItem.add(label);
		formItem.add(tiBodyNumber);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_MODEL, DEFAULT_MODEL), tiModel);
		formItem.add(label);
		formItem.add(tiModel);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_TEAM, DEFAULT_TEAM), tiTeam);
		formItem.add(label);
		formItem.add(tiTeam);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.getResourceBundle(iwc).getLocalizedString(KEY_SPONSOR, DEFAULT_SPONSOR), tiSponsor);
		formItem.add(label);
		formItem.add(tiSponsor);
		layer.add(formItem);

		section.add(clearLayer);

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		contents.add(buttonLayer);

		/*Layer span = new Layer(Layer.SPAN);
		span.add(new Text(this.getResourceBundle(iwc).getLocalizedString(KEY_UPDATE, DEFAULT_UPDATE)));
		Link send = new Link(span);
		send.setToFormSubmit(form);*/
		
		SubmitButton submit = (SubmitButton) getButton(new SubmitButton(localize(
				KEY_UPDATE, DEFAULT_UPDATE)));
		submit
				.setValueOnClick(PARAMETER_FORM_SUBMIT, Boolean.TRUE.toString());
		
		buttonLayer.add(submit);

		add(form);
	}

	private void updatePreferences(IWContext iwc) throws Exception {
		String homePage = iwc.getParameter(PARAMETER_HOME_PAGE);
		String raceNumberMX = iwc.getParameter(PARAMETER_RACE_NUMBER_MX);
		String raceNumberSnocross = iwc.getParameter(PARAMETER_RACE_NUMBER_SNOCROSS);
		String vehicleType = iwc.getParameter(PARAMETER_VEHICLE_TYPE);
		String vehicleSubType = iwc.getParameter(PARAMETER_VEHICLE_SUBTYPE);
		String engine = iwc.getParameter(PARAMETER_ENGINE);
		String engineCC = iwc.getParameter(PARAMETER_ENGINE_CC);
		String model = iwc.getParameter(PARAMETER_MODEL);
		String transponder = iwc.getParameter(PARAMETER_TRANSPONDER);
		String team = iwc.getParameter(PARAMETER_TEAM);
		String sponsor = iwc.getParameter(PARAMETER_SPONSOR);
		String bodyNumber = iwc.getParameter(PARAMETER_BODY_NUMBER);

		RaceBusiness raceBusiness = getRaceBusiness(iwc);
		UserBusiness ub = getUserBusiness(iwc);
		
		if (raceBusiness != null) {
			System.out.println("Trying to find settings for user " + iwc.getCurrentUser().getName());
			RaceUserSettings settings = raceBusiness.getRaceUserSettings(iwc.getCurrentUser());
			if (settings == null) {
				System.out.println("no settings found");
			} else {
				System.out.println("settings found");
			}
			if (settings == null) {
				System.out.println("creating new settings entry");
				settings = raceBusiness.getRaceUserSettingsHome().create();
				settings.setUser(iwc.getCurrentUser());
			}

			settings.setHomePage(homePage);
			if (!StringUtil.isEmpty(raceNumberMX)) {
				RaceNumber number = getRaceNumberHome().findByPrimaryKey(Integer.parseInt(raceNumberMX));
				number = getRaceNumberHome().update(Integer.parseInt(raceNumberMX), 
						null, 
						new Timestamp(System.currentTimeMillis()), 
						Integer.valueOf(number.getRaceNumber()) >= 100 ? new Timestamp(System.currentTimeMillis()) : null, 
						Integer.valueOf(number.getRaceNumber()) >= 100, 
						Boolean.TRUE,
						getRaceTypeHome().findByRaceType(MSIConstants.RACE_TYPE_MX_AND_ENDURO));

				try {
					settings.setRaceNumberMX(number);
					
					StringBuffer name = new StringBuffer("");
					if (settings.getUser().getFirstName() != null) {
						name.append(settings.getUser().getFirstName());
						name.append(" ");
					}
					if (settings.getUser().getMiddleName() != null) {
						name.append(settings.getUser().getMiddleName());
						name.append(" ");
					}
					if (settings.getUser().getLastName() != null) {
						name.append(settings.getUser().getLastName());
					}
					
					StringBuffer text = new StringBuffer();
					text.append(name.toString());
					text.append(", ");
					text.append(settings.getUser().getPersonalID());
					text.append(" ");
					text.append(this.getResourceBundle(iwc).getLocalizedString("mail_text_has_applied_for", "has applied for the number"));
					text.append(" ");
					text.append(number.getRaceNumber());
					text.append(" ");
					text.append(this.getResourceBundle(iwc).getLocalizedString("mail_text_race type", "in the race type"));
					text.append(" ");
					text.append(this.getResourceBundle(iwc).getLocalizedString(number.getRaceType().getRaceType(), number.getRaceType().getRaceType()));
					
					String subject = this.getResourceBundle(iwc).getLocalizedString("mail_subject", "Application for race number");
					
					String to = iwc.getApplicationSettings().getProperty("mail_to", "numer@msisport.is");
					
					raceBusiness.sendMessage(to, subject, text.toString());
				}
				catch (Exception e) {
					e.printStackTrace();
				}				
			}

			if (!StringUtil.isEmpty(raceNumberSnocross)) {
				RaceNumber number = getRaceNumberHome().findByPrimaryKey(Integer.parseInt(raceNumberSnocross));
				number = getRaceNumberHome().update(Integer.parseInt(raceNumberSnocross), 
						null, 
						new Timestamp(System.currentTimeMillis()), 
						Integer.valueOf(number.getRaceNumber()) >= 100 ? new Timestamp(System.currentTimeMillis()) : null, 
						Integer.valueOf(number.getRaceNumber()) >= 100, 
						Boolean.TRUE, 
						getRaceTypeHome().findByRaceType(MSIConstants.RACE_TYPE_SNOCROSS));

				try {
					settings.setRaceNumberSnocross(number);

					StringBuffer name = new StringBuffer("");
					if (settings.getUser().getFirstName() != null) {
						name.append(settings.getUser().getFirstName());
						name.append(" ");
					}
					if (settings.getUser().getMiddleName() != null) {
						name.append(settings.getUser().getMiddleName());
						name.append(" ");
					}
					if (settings.getUser().getLastName() != null) {
						name.append(settings.getUser().getLastName());
					}

					
					StringBuffer text = new StringBuffer();
					text.append(name.toString());
					text.append(", ");
					text.append(settings.getUser().getPersonalID());
					text.append(" ");
					text.append(this.getResourceBundle(iwc).getLocalizedString("mail_text_has_applied_for", "has applied for the number"));
					text.append(" ");
					text.append(number.getRaceNumber());
					text.append(" ");
					text.append(this.getResourceBundle(iwc).getLocalizedString("mail_text_race type", "in the race type"));
					text.append(" ");
					text.append(this.getResourceBundle(iwc).getLocalizedString(number.getRaceType().getRaceType(), number.getRaceType().getRaceType()));
					
					String subject = this.getResourceBundle(iwc).getLocalizedString("mail_subject", "Application for race number");
					
					String to = iwc.getApplicationSettings().getProperty("mail_to", "numer@msisport.is");
					
					raceBusiness.sendMessage(to, subject, text.toString());
				}
				catch (Exception e) {
					e.printStackTrace();
				}				
			}
			
			RaceVehicleType type = null;
			try {
				type = getRaceBusiness(iwc).getRaceVehicleTypeHome().findByPrimaryKey(new Integer(vehicleType));
			}
			catch (Exception e){
			}
			RaceVehicleType subtype = null;
			try {
				subtype = getRaceBusiness(iwc).getRaceVehicleTypeHome().findByPrimaryKey(new Integer(vehicleSubType));
			} catch (Exception e) {
			}
			
			
			settings.setVehicleType(type);
			settings.setVehicleSubType(subtype);
			settings.setEngine(engine);
			settings.setEngineCC(engineCC);
			settings.setModel(model);
			settings.setTransponder(transponder);
			settings.setTeam(team);
			settings.setSponsor(sponsor);
			settings.setBodyNumber(bodyNumber);
			settings.store();
		}
		
		Layer header = new Layer(Layer.DIV);
		header.setStyleClass("header");
		add(header);

		Heading1 heading = new Heading1(this.getResourceBundle(iwc).getLocalizedString("citizen_preferences", "Citizen preferences"));
		header.add(heading);

		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("receipt");

		heading = new Heading1(this.getResourceBundle(iwc).getLocalizedString(KEY_PREFERENCES_SAVED, DEFAULT_PREFERENCES_SAVED));
		layer.add(heading);

		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(this.getResourceBundle(iwc).getLocalizedString(KEY_PREFERENCES_SAVED + "_text", DEFAULT_PREFERENCES_SAVED + " info")));
		layer.add(paragraph);

		try {
			ICPage page = ub.getHomePageForUser(this.info.getUser());
			paragraph.add(new Break(2));

			Layer span = new Layer(Layer.SPAN);
			span.add(new Text(this.getResourceBundle(iwc).getLocalizedString("my_page", "My page")));
			Link link = new Link(span);
			link.setStyleClass("homeLink");
			link.setPage(page);
			paragraph.add(link);
		}
		catch (FinderException fe) {
			// No homepage found...
		}

		add(layer);
	}

	private Address getMainAddress(IWContext iwc) {
		try {
			UserBusiness ub = (UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class);

			return ub.getUsersMainAddress(info.getUser());
		}
		catch (Exception e) {
			super.add(new ExceptionWrapper(e, this));
		}
		return null;
	}
	
	private Address getCoAddress(IWContext iwc) {
		try {
			UserBusiness ub = (UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class);

			return ub.getUsersCoAddress(info.getUser());
		}
		catch (Exception e) {
			super.add(new ExceptionWrapper(e, this));
		}
		return null;
	}

}