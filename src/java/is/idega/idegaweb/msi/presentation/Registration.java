/*
 * $Id: Registration.java,v 1.4 2008/05/21 09:04:17 palli Exp $
 * Created on May 16, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.business.PaymentInfo;
import is.idega.idegaweb.msi.business.RaceParticipantInfo;
import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.RaceCategory;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceType;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.data.Season;
import is.idega.idegaweb.msi.data.SeasonHome;
import is.idega.idegaweb.msi.service.PaymentsService;
import is.idega.idegaweb.msi.util.MSIConstants;
import is.idega.idegaweb.msi.util.MSIUtil;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ejb.FinderException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.block.creditcard.business.CreditCardAuthorizationException;
import com.idega.block.web2.business.Web2Business;
import com.idega.builder.business.BuilderLogic;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOFinderException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.facelets.ui.FaceletComponent;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.UnavailableIWContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SelectOption;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.servlet.filter.IWAuthenticator;
import com.idega.user.business.NoEmailFoundException;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.user.data.UserBMPBean;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.EmailValidator;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * Last modified: $Date: 2008/05/21 09:04:17 $ by $Author: palli $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.4 $
 */
public class Registration extends RaceBlock {

	protected static final String SESSION_ATTRIBUTE_PARTICIPANT = "sa_participant";
	public static final String SESSION_ATTRIBUTE_AMOUNT = "sa_amount";
	public static final String SESSION_ATTRIBUTE_CARD_NUMBER = "sa_card_number";
	public static final String SESSION_ATTRIBUTE_PAYMENT_DATE = "sa_payment_date";

	public static final String PARAMETER_ACTION = "prm_action",
			PARAMETER_FROM_ACTION = "prm_from_action";

	public static final String PARAMETER_RACE = "prm_race";
	public static final String PARAMETER_EVENT = "prm_event";
	private static final String PARAMETER_SEASON = "prm_season";

	private static final String PARAMETER_EMAIL = "prm_email";
	private static final String PARAMETER_HOME_PHONE = "prm_home_phone";
	private static final String PARAMETER_MOBILE_PHONE = "prm_mobile_phone";
	private static final String PARAMETER_AGREE = "prm_agree";

	private static final String PARAMETER_NAME_ON_CARD = "prm_name_on_card";
	private static final String PARAMETER_CARD_NUMBER = "prm_card_number";
	private static final String PARAMETER_EXPIRES_MONTH = "prm_expires_month";
	private static final String PARAMETER_EXPIRES_YEAR = "prm_expires_year";
	private static final String PARAMETER_CCV = "prm_ccv";
	public static final String PARAMETER_AMOUNT = "prm_amount";
	private static final String PARAMETER_CARD_HOLDER_EMAIL = "prm_card_holder_email";
	public static final String PARAMETER_REFERENCE_NUMBER = "prm_reference_number";

	public static final String PARAMETER_COMMENT = "prm_comment";
	public static final String PARAMETER_PARTNER1_NAME = "prm_partner1_name";
	public static final String PARAMETER_PARTNER2_NAME = "prm_partner2_name";
	public static final String PARAMETER_PARTNER1_PERSONAL_ID = "prm_partner1_personal_id";
	public static final String PARAMETER_PARTNER2_PERSONAL_ID = "prm_partner2_personal_id";
	private static final String PARAMETER_RENT_TIMETRANSMITTER = "prm_rent_tt";

	public static final int ACTION_STEP_PERSONALDETAILS = 1,
			ACTION_STEP_DISCLAIMER = 2, ACTION_STEP_PAYMENT_INFO = 3,
			ACTION_SAVE = 4, ACTION_CANCEL = 5;

	private RaceParticipantInfo raceParticipantInfo;

	private boolean membershipFee;

	@Autowired
	private Web2Business web2Business;

	public boolean isMembershipFeePayed() {
		return membershipFee;
	}

	public void setMembershipFeePayed(boolean membershipFee) {
		this.membershipFee = membershipFee;
	}

	protected SeasonHome getSeasonHome() {
		try {
			return (SeasonHome) IDOLookup.getHome(Season.class);
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING,
					"Failed to get " + SeasonHome.class + " cause of: ", e);
		}

		return null;
	}

	@Autowired
	private PaymentsService paymentsService;

	private PaymentsService getPaymentsService() {
		if (this.paymentsService == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.paymentsService;
	}

	@Override
	protected IWBundle getBundle() {
		IWMainApplication application = IWMainApplication
				.getDefaultIWMainApplication();
		if (application != null) {
			return application.getBundle(MSIConstants.IW_BUNDLE_IDENTIFIER);
		}

		return null;
	}

	@Override
	protected IWBundle getSportUnionBundle() {
		IWMainApplication application = IWMainApplication
				.getDefaultIWMainApplication();
		if (application != null) {
			return application.getBundle("com.idega.sport.union");
		}

		return null;
	}

	private IWBundle getCoreBundle() {
		return CoreUtil.getCoreBundle();
	}

	private Web2Business getWeb2Business() {
		if (this.web2Business == null) {
			this.web2Business = ELUtil.getInstance().getBean(
					Web2Business.SPRING_BEAN_IDENTIFIER);
		}

		return this.web2Business;
	}

	private IWMainApplicationSettings getSettings() {
		IWMainApplication iwma = IWMainApplication
				.getDefaultIWMainApplication();
		if (iwma != null) {
			return iwma.getSettings();
		}

		return null;
	}

	protected String getApplicationProperty(String key, String value) {
		IWMainApplicationSettings settings = getSettings();
		if (settings != null) {
			return settings.getProperty(key, value);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.idega.presentation.Block#encodeBegin(javax.faces.context.FacesContext
	 * )
	 */
	@Override
	public void encodeBegin(FacesContext fc) throws IOException {
		super.encodeBegin(fc);

		IWContext iwc = IWContext.getIWContext(fc);
		ArrayList<String> scripts = new ArrayList<String>();
		scripts.add(getCoreBundle().getVirtualPathWithFileNameString(
				"iw_core.js"));
		scripts.add(getWeb2Business().getBundleURIToJQueryLib());
		scripts.add(CoreConstants.DWR_ENGINE_SCRIPT);
		scripts.add("/dwr/interface/RaceBusiness.js");
		scripts.add(getBundle().getVirtualPathWithFileNameString(
				"javascript/race-registration.js"));
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, scripts);

		ArrayList<String> styles = new ArrayList<String>();
		if (getSportUnionBundle() != null) {
			styles.add(getSportUnionBundle().getVirtualPathWithFileNameString(
					"style/label.css"));
			styles.add(getSportUnionBundle().getVirtualPathWithFileNameString(
					"style/select.css"));
			styles.add(getSportUnionBundle().getVirtualPathWithFileNameString(
					"style/button.css"));
			styles.add(getSportUnionBundle().getVirtualPathWithFileNameString(
					"style/textarea.css"));
			styles.add(getBundle().getVirtualPathWithFileNameString(
					"style/race-registration.css"));
			PresentationUtil.addStyleSheetsToHeader(iwc, styles);
		}
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		if (!iwc.isLoggedOn()) {
			String registrationLink = iwc.getRequestURI();
			if (iwc.isParameterSet(PARAMETER_RACE)) {
				registrationLink = registrationLink + CoreConstants.QMARK
						+ PARAMETER_RACE + CoreConstants.EQ
						+ iwc.getParameter(PARAMETER_RACE);
			}
			String loginPageUrl = BuilderLogic.getInstance()
					.getFullPageUrlByPageType(iwc, "msi_login", false);
			try {
				String redirectUrl = loginPageUrl + CoreConstants.QMARK
						+ IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON
						+ CoreConstants.EQ + registrationLink;
				getResponse().sendRedirect(redirectUrl);
			} catch (UnavailableIWContext e) {
				getLogger().warning("" + e);
			} catch (IOException e) {
				getLogger().warning("" + e);
			}
		} else {
			switch (parseAction(iwc)) {
			case ACTION_STEP_PERSONALDETAILS:
				stepPersonalDetails(iwc);
				break;
			case ACTION_STEP_DISCLAIMER:
				stepDisclaimer(iwc);
				break;
			case ACTION_STEP_PAYMENT_INFO:
				stepPaymentInfo(iwc);
				break;
			case ACTION_SAVE:
				save(iwc, null, this.raceParticipantInfo, true);
				break;
			case ACTION_CANCEL:
				cancel(iwc);
				break;
			}
		}
	}

	private DropdownMenu getSeasonsMenu(IWContext iwc) {
		DropdownMenu seasonsDropdown = (DropdownMenu) getStyledInterface(new DropdownMenu(
				PARAMETER_SEASON));
		/*
		 * seasonsDropdown.setAsNotEmpty(localize( "race_editor.select_season",
		 * "Select season"));
		 */
		Collection<Season> seasons = getSeasonHome().findAll();
		for (Season season : seasons) {
			seasonsDropdown.addOption(new SelectOption(season.getName(), season
					.getPrimaryKey().toString()));
		}

		Season season = getSeasonHome().getCurrentSeason();
		if (season != null) {
			seasonsDropdown.setSelectedElement(season.getPrimaryKey()
					.toString());
		}

		seasonsDropdown.setDisabled(!iwc.getIWMainApplication().getSettings()
				.getBoolean("msi.check_membership_registration", false));

		return seasonsDropdown;
	}

	private DropdownMenu getEventsMenu(IWContext iwc, Form form) {
		DropdownMenu eventsDropdown = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_EVENT));
		eventsDropdown.setAsNotEmpty(localize("race_reg.must_select_distance", "You have to select a distance"));
		eventsDropdown.addMenuElement(-1, localize("race_reg.select_distance", "Please select distance"));
		Map<String, RaceEvent> events = null;
		
		try {
			events = getRaceBusiness(iwc).getEventsForRace(raceParticipantInfo.getRace());
		} catch (RemoteException | FinderException e) {
			getLogger().log(Level.WARNING, "Failed to get events for race, cause of: ", e);
		}

		for (String key : events.keySet()) {
			RaceEvent event = events.get(key);
			String pk = event.getPrimaryKey().toString();
			eventsDropdown.addOption(new SelectOption(event.getEventID(), pk));
		}

		return eventsDropdown;
	}

	private Text getRaceName() {
		if (this.raceParticipantInfo != null) {
			Race race = this.raceParticipantInfo.getRace();
			if (race != null) {
				return new Text(race.getName());
			}
		}

		return new Text("-");
	}

	public IWBundle getBundle(FacesContext ctx, String bundleIdentifier) {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(ctx);
		return iwma.getBundle(bundleIdentifier);
	}

	protected static IWMainApplication getIWMainApplication(FacesContext context) {
		return IWMainApplication.getIWMainApplication(context);
	}

	protected boolean canShowPartners(IWContext iwc) {
		return true;
	}

	private void stepPersonalDetails(IWContext iwc) throws RemoteException,
			FinderException {
		Form form = new Form();
		form.maintainParameter(PARAMETER_RACE);
		form.addParameter(PARAMETER_ACTION, "-1");
		form.addParameter(PARAMETER_FROM_ACTION, ACTION_STEP_PERSONALDETAILS);

		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		/*
		 * table.add(getPhasesTable(ACTION_STEP_PERSONALDETAILS, ACTION_SAVE,
		 * "race_reg.registration", "Registration"), 1, row++);
		 */
		table.add(
				getPhasesTable(ACTION_STEP_PERSONALDETAILS, ACTION_SAVE,
						CoreConstants.EMPTY, CoreConstants.EMPTY), 1, row++);
		table.setHeight(row++, 12);

		table.add(
				getInformationTable(localize(
						"race_reg.information_text_step_1",
						"Information text 1...")), 1, row++);
		table.setHeight(row++, 18);

		Table choiceTable = new Table();
		choiceTable.setColumns(3);
		choiceTable.setCellpadding(2);
		choiceTable.setCellspacing(0);
		choiceTable.setWidth(1, "50%");
		choiceTable.setWidth(2, 12);
		choiceTable.setWidth(3, "50%");
		choiceTable.setWidth(Table.HUNDRED_PERCENT);
		table.add(choiceTable, 1, row++);
		int iRow = 1;

		Text redStar = getHeader("*");
		redStar.setFontColor("#ff0000");

		/*
		 * Tournament name and event name
		 */
		choiceTable.add(getHeader(localize("race_reg.race_name", "Race name")),
				1, iRow);
		choiceTable.add(redStar, 1, iRow);

		choiceTable.add(
				getHeader(localize("race_reg.event_name", "Event name")), 3,
				iRow);
		choiceTable.add(redStar, 3, iRow);

		iRow++;

		choiceTable.add(getRaceName(), 1, iRow);
		choiceTable.add(getEventsMenu(iwc, form), 3, iRow);

		iRow++;

		/*
		 * Union and season payment
		 */
		choiceTable.add(getHeader(localize("msi.club", "Club")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);

		if (!isMembershipFeePayed()) {
			choiceTable.add(
					getHeader(localize("msi_membership_fee", "Seasons")), 3,
					iRow);
			// choiceTable.add(redStar, 3, iRow);
		}

		iRow++;

		UIComponent facelet = getIWMainApplication(iwc).createComponent(
				FaceletComponent.COMPONENT_TYPE);
		if (facelet instanceof FaceletComponent) {
			((FaceletComponent) facelet).setFaceletURI(getBundle(iwc,
					"com.idega.sport.union").getFaceletURI(
					"club/main_union_editor.xhtml"));
		}

		choiceTable.add(facelet, 1, iRow);

		if (!isMembershipFeePayed()) {
			choiceTable.add(getSeasonsMenu(iwc), 3, iRow);
			choiceTable.add(Text.getNonBrakingSpace(), 3, iRow);
			choiceTable.add(":", 3, iRow);
			choiceTable.add(Text.getNonBrakingSpace(), 3, iRow);
			choiceTable.add(
					getApplicationProperty(MSIConstants.PROPERTY_SEASON_PRICE,
							"5000"), 3, iRow);
			choiceTable.add(Text.getNonBrakingSpace(), 3, iRow);
			choiceTable.add("ISK", 3, iRow);
			Layer explanation = new Layer("span");
			explanation.setStyleClass("dropdownExplanationText");
			explanation
					.add(new Text(
							localize(
									"seasons_menu_explanation",
									"If you have not paid membership fees in your company, you can pay for the year by checking here. MSI does so up to the club.")));
			choiceTable.add(explanation, 3, iRow);
		}

		iRow++;

		choiceTable.setHeight(iRow++, 12);

		Layer useTT = new Layer();
		choiceTable.add(useTT, 3, iRow++);
		// choiceTable.mergeCells(1, iRow-1, 3, iRow-1);
		CheckBox isRentTimeTransmitter = new CheckBox(
				PARAMETER_RENT_TIMETRANSMITTER);

		Text isRentTimeTransmitterLabel = getHeader(localize(
				"race_reg.rent_time_transmitter2", "Rent time transmitter"));
		useTT.add(isRentTimeTransmitterLabel);
		useTT.add(isRentTimeTransmitter);

		Layer priceText = new Layer("label");
		useTT.add(priceText);
		// priceText.setStyleAttribute("display:inline;display:inline-block;width:20px;overflow:visible;");

		Layer priceTextContainer = new Layer();
		priceText.add(priceTextContainer);
		priceTextContainer.add(localize("race_reg.rent_price", "Price")
				+ CoreConstants.SPACE);

		Layer priceValue = new Layer("span");
		priceTextContainer.add(priceValue);
		priceValue.setStyleClass("tt-price");
		priceTextContainer.add(" kr. ");
		priceTextContainer
				.add(localize("race_reg.check_if_want_rent_timetransmitter",
						"Check here if you want to rent a time transmitter sent from MSI"));
		priceTextContainer.setStyleAttribute("width:450px;");

		Layer script = new Layer("script");
		useTT.add(script);
		StringBuilder actions = new StringBuilder(
				"jQuery(document).ready(function(){")
				.append("RaceRegistrationHelper.ttPricesActions(null);})");
		script.add(actions.toString());

		Text nameField = getText("");
		nameField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getUser() != null) {
			nameField.setText(this.raceParticipantInfo.getUser().getName());
		}

		Text emailField = getText("");
		emailField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getEmail() != null) {
			emailField.setText(this.raceParticipantInfo.getEmail());
			choiceTable.add(new HiddenInput(PARAMETER_EMAIL,
					this.raceParticipantInfo.getEmail()), 1, 1);
		} else if (this.raceParticipantInfo.getUser() != null) {
			try {
				Email mail = getUserBusiness(iwc).getUsersMainEmail(
						this.raceParticipantInfo.getUser());
				emailField.setText(mail.getEmailAddress());
				choiceTable
						.add(new HiddenInput(PARAMETER_EMAIL, mail
								.getEmailAddress()), 1, 1);
			} catch (NoEmailFoundException nefe) {
			}
		}

		choiceTable.add(
				getHeader(localize("race_reg.participant_name", "Name")), 1,
				iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable
		.add(getHeader(localize("race_reg.email", "Email")), 3, iRow);
		choiceTable.add(redStar, 3, iRow++);
		choiceTable.add(nameField, 1, iRow);
		choiceTable.add(emailField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text ssnISField = getText("");
		if (this.raceParticipantInfo.getUser() != null) {
			ssnISField.setText(this.raceParticipantInfo.getUser()
					.getPersonalID());
		}

		Text telField = getText("");
		telField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getHomePhone() != null) {
			telField.setText(this.raceParticipantInfo.getHomePhone());
			choiceTable.add(new HiddenInput(PARAMETER_HOME_PHONE,
					this.raceParticipantInfo.getHomePhone()), 1, 1);
		} else if (this.raceParticipantInfo.getUser() != null) {
			try {
				Phone phone = getUserBusiness(iwc).getUsersHomePhone(
						this.raceParticipantInfo.getUser());
				telField.setText(phone.getNumber());
				choiceTable
						.add(new HiddenInput(PARAMETER_HOME_PHONE, phone
								.getNumber()), 1, 1);
			} catch (NoPhoneFoundException nefe) {
				// No phone registered...
			}
		}

		choiceTable.add(getHeader(localize("race_reg.ssn", "SSN")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize("race_reg.telephone", "Telephone")),
				3, iRow++);
		choiceTable.add(ssnISField, 1, iRow);
		choiceTable.add(telField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text addressField = getText("");
		addressField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getUser() != null) {
			Address address = getUserBusiness(iwc).getUsersMainAddress(
					this.raceParticipantInfo.getUser());
			if (address != null) {
				addressField.setText(address.getStreetAddress());
			}
		}

		Text mobileField = getText("");
		mobileField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getMobilePhone() != null) {
			mobileField.setText(this.raceParticipantInfo.getMobilePhone());
			choiceTable.add(new HiddenInput(PARAMETER_MOBILE_PHONE,
					this.raceParticipantInfo.getMobilePhone()), 1, 1);
		} else if (this.raceParticipantInfo.getUser() != null) {
			try {
				Phone phone = getUserBusiness(iwc).getUsersMobilePhone(
						this.raceParticipantInfo.getUser());
				mobileField.setText(phone.getNumber());
				choiceTable.add(
						new HiddenInput(PARAMETER_MOBILE_PHONE, phone
								.getNumber()), 1, 1);
			} catch (NoPhoneFoundException nefe) {
			}
		}

		choiceTable.add(getHeader(localize("race_reg.address", "Address")), 1,
				iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(
				getHeader(localize(MSIConstants.RR_MOBILE, "Mobile Phone")), 3,
				iRow++);
		choiceTable.add(addressField, 1, iRow);
		choiceTable.add(mobileField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text postalField = getText("");
		if (this.raceParticipantInfo.getUser() != null) {
			Address address = getUserBusiness(iwc).getUsersMainAddress(
					this.raceParticipantInfo.getUser());
			if (address != null) {
				PostalCode postal = address.getPostalCode();
				if (postal != null) {
					postalField.setText(postal.getPostalCode());
				}
			}
		}

		Text raceNumberField = getText("");
		if (this.raceParticipantInfo.getRaceNumber() != null) {
			raceNumberField.setText(this.raceParticipantInfo.getRaceNumber());
		}

		choiceTable.add(
				getHeader(localize(MSIConstants.RR_POSTAL, "Postal Code")), 1,
				iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(
				getHeader(localize("race_reg.race_number", "Race number")), 3,
				iRow);
		choiceTable.add(redStar, 3, iRow++);
		choiceTable.add(postalField, 1, iRow);
		choiceTable.add(raceNumberField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text raceVehicleField = getText("");
		if (this.raceParticipantInfo.getRaceVehicle() != null) {
			raceVehicleField.setText(this.raceParticipantInfo.getRaceVehicle()
					.getLocalizationKey());
		}

		Text raceVehicleSubtypeField = getText("");
		raceVehicleSubtypeField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getRaceVehicleSubtype() != null) {
			raceVehicleSubtypeField.setText(this.raceParticipantInfo
					.getRaceVehicleSubtype().getLocalizationKey());
		}

		choiceTable.add(
				getHeader(localize("race_reg.race_vehicle", "Race vehicle")),
				1, iRow);
		choiceTable.add(
				getHeader(localize("race_reg.race_vehicle_subtype",
						"Race vehicle subtype")), 3, iRow++);
		choiceTable.add(raceVehicleField, 1, iRow);
		choiceTable.add(raceVehicleSubtypeField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text engineField = getText("");
		if (this.raceParticipantInfo.getEngine() != null) {
			engineField.setText(this.raceParticipantInfo.getEngine());
		}

		Text engineCCField = getText("");
		engineCCField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getEngineCC() != null) {
			engineCCField.setText(this.raceParticipantInfo.getEngineCC());
		}

		choiceTable.add(getHeader(localize("race_reg.engine", "Engine")), 1,
				iRow);
		choiceTable.add(getHeader(localize("race_reg.engineCC", "Engine CC")),
				3, iRow++);
		choiceTable.add(engineField, 1, iRow);
		choiceTable.add(engineCCField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text bodyNumberField = getText("");
		if (this.raceParticipantInfo.getBodyNumber() != null) {
			bodyNumberField.setText(this.raceParticipantInfo.getBodyNumber());
		}

		Text modelField = getText("");
		modelField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getModel() != null) {
			modelField.setText(this.raceParticipantInfo.getModel());
		}

		choiceTable.add(
				getHeader(localize("race_reg.body_number", "Body number")), 1,
				iRow);
		choiceTable.add(getHeader(localize("race_reg.model", "Model")), 3,
				iRow++);
		choiceTable.add(bodyNumberField, 1, iRow);
		choiceTable.add(modelField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		// new
		Text chipField = getText("");
		chipField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getChipNumber() != null) {
			chipField.setText(this.raceParticipantInfo.getChipNumber());
		}

		choiceTable.add(
				getHeader(localize("race_reg.chip_number", "Chip number")), 1,
				iRow++);
		choiceTable.add(chipField, 1, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text teamField = getText("");
		if (this.raceParticipantInfo.getTeam() != null) {
			teamField.setText(this.raceParticipantInfo.getTeam());
		}

		Text sponsorsField = getText("");
		sponsorsField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getSponsors() != null) {
			sponsorsField.setText(this.raceParticipantInfo.getSponsors());
		}

		choiceTable.add(getHeader(localize("race_reg.team", "Team")), 1, iRow);
		choiceTable.add(getHeader(localize("race_reg.sponsors", "Sponsors")),
				3, iRow++);
		choiceTable.add(teamField, 1, iRow);
		choiceTable.add(sponsorsField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);
		
		TextInput commentField = (TextInput) getStyledInterface(new TextInput(PARAMETER_COMMENT));
		commentField.setMaxlength(1000);
		
		boolean showPartners = canShowPartners(iwc);
		
		TextInput partner1NameField = new TextInput(PARAMETER_PARTNER1_NAME);
		TextInput partner1PersonalIdField = new TextInput(PARAMETER_PARTNER1_PERSONAL_ID);
		
		partner1NameField.setStyleClass("partner-one-name");
		partner1PersonalIdField.setStyleClass("partner-one-personal-id");
		
		partner1NameField.setPlaceholder(localize("race_reg.participant_name", "Name"));
		partner1PersonalIdField.setPlaceholder(localize("race_reg.ssn", "SSN"));
		
		User firstPartner = this.raceParticipantInfo.getFirstPartner();
		if (firstPartner != null) {
			partner1NameField.setContent(firstPartner.getName());
			partner1PersonalIdField.setContent(firstPartner.getPersonalID());
		}
		
		TextInput partner2NameField = new TextInput(PARAMETER_PARTNER2_NAME);
		TextInput partner2PersonalIdField = new TextInput(PARAMETER_PARTNER2_PERSONAL_ID);
		
		partner2NameField.setStyleClass("partner-two-name");
		partner2PersonalIdField.setStyleClass("partner-two-personal-id");
		
		partner2NameField.setPlaceholder(localize("race_reg.participant_name", "Name"));
		partner2PersonalIdField.setPlaceholder(localize("race_reg.ssn", "SSN"));
		
		User secondPartner = this.raceParticipantInfo.getSecondPartner();
		if (secondPartner != null) {
			partner2NameField.setContent(secondPartner.getName());
			partner2PersonalIdField.setContent(secondPartner.getPersonalID());
		}
		
		if (!showPartners) {
			partner1NameField.setStyleAttribute("display", "none");
			partner1PersonalIdField.setStyleAttribute("display", "none");
			partner2NameField.setStyleAttribute("display", "none");
			partner2PersonalIdField.setStyleAttribute("display", "none");
		}
		
		choiceTable.mergeCells(1, iRow, 4, iRow);
		choiceTable.add(getHeader(localize("race_reg.comment", "Comment")), 1, iRow++);
		choiceTable.add(commentField, 1, iRow++);
		
		choiceTable.setHeight(iRow++, 3);
		choiceTable.add(getHeader(localize("race_reg.teammates", "Teammates")), 1, iRow++);
		choiceTable.add(getHeader(localize("race_reg.partner1", "Partner1")), 1, iRow);
		choiceTable.mergeCells(2, iRow, 4, iRow);
		choiceTable.add(partner1NameField, 1, iRow++);
		choiceTable.add(partner1PersonalIdField, 2, iRow - 1);
		if (!showPartners) {
			choiceTable.setStyle(1, iRow - 1, "display", "none");
		}
		choiceTable.setHeight(iRow++, 3);

		choiceTable.add(getHeader(localize("race_reg.partner2", "Partner2")), 1, iRow);
		choiceTable.mergeCells(2, iRow, 4, iRow);
		choiceTable.add(partner2NameField, 1, iRow++);
		choiceTable.add(partner2PersonalIdField, 2, iRow - 1);
		if (!showPartners) {
			choiceTable.setStyle(1, iRow - 1, "display", "none");
		}
		choiceTable.setHeight(iRow++, 3);

		boolean canRegister = canRegister(iwc);

		if (canRegister) {
			SubmitButton next = (SubmitButton) getButton(new SubmitButton(
					localize("next", "Next")));
			next.setValueOnClick(PARAMETER_ACTION,
					String.valueOf(ACTION_STEP_DISCLAIMER));

			table.setHeight(row++, 18);
			table.add(next, 1, row);
			table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);
		} else {
			table.setHeight(row++, 18);
			table.add(
					getHeader(localize("race_reg.cant_register",
							"Can't register in a national tournament without a valid race number")),
					1, row);
			table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);
		}
		add(form);
	}

	private RaceCategory getRaceCategory(RaceParticipantInfo info) {
		Race race = getRace(info);
		if (race != null) {
			return race.getRaceCategory();
		}

		return null;
	}

	private String getRaceCategoryKey(RaceParticipantInfo info) {
		RaceCategory raceCategory = getRaceCategory(info);
		if (raceCategory != null) {
			return raceCategory.getCategoryKey();
		}

		return null;
	}

	private boolean canRegister(IWContext iwc) {
		if (iwc.isSuperAdmin()) {
			return true;
		}

		if (MSIConstants.ICELANDIC_CHAMPIONSHIP
				.equals(getRaceCategoryKey(this.raceParticipantInfo))) {
			if (this.raceParticipantInfo.getRaceNumber() == null) {
				return false;
			}

			try {
				RaceNumber raceNumber = this
						.getRaceBusiness(iwc)
						.getRaceNumberHome()
						.findByRaceNumber(
								Integer.parseInt(this.raceParticipantInfo
										.getRaceNumber()),
								this.raceParticipantInfo.getRace()
										.getRaceType());
				if (!raceNumber.getIsApproved()) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}

	private void stepDisclaimer(IWContext iwc) {
		Form form = new Form();
		form.addParameter(PARAMETER_ACTION, "-1");
		form.addParameter(PARAMETER_FROM_ACTION, ACTION_STEP_DISCLAIMER);

		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(
				getPhasesTable(ACTION_STEP_DISCLAIMER, ACTION_SAVE,
						"race_reg.consent", "Consent"), 1, row++);
		table.setHeight(row++, 18);

		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize(
				"next", "Next")));
		next.setValueOnClick(PARAMETER_ACTION,
				String.valueOf(ACTION_STEP_PAYMENT_INFO));
		if (!this.raceParticipantInfo.isAgree()) {
			next.setDisabled(true);
		}

		CheckBox agree = getCheckBox(PARAMETER_AGREE, Boolean.TRUE.toString());
		agree.setToEnableWhenChecked(next);
		agree.setToDisableWhenUnchecked(next);
		agree.setChecked(this.raceParticipantInfo.isAgree());

		table.add(
				getText(localize("race_reg.information_text_step_3",
						"Information text 3...")), 1, row++);
		table.setHeight(row++, 6);
		table.add(agree, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(getHeader(localize("race_reg.agree_terms", "Yes, I agree")),
				1, row++);

		SubmitButton previous = (SubmitButton) getButton(new SubmitButton(
				localize("previous", "Previous")));
		previous.setValueOnClick(PARAMETER_ACTION,
				String.valueOf(ACTION_STEP_PERSONALDETAILS));

		table.setHeight(row++, 18);
		table.add(previous, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}

	protected TextInput getCCVInput() {
		TextInput ccv = (TextInput) getStyledInterface(new TextInput(
				PARAMETER_CCV));
		ccv.setLength(3);
		ccv.setMaxlength(3);
		ccv.setAsIntegers(localize("race_reg.not_valid_ccv",
				"Not a valid CCV number"));
		ccv.setAsNotEmpty(localize("race_reg.must_supply_ccv",
				"You must enter the CCV number"));
		ccv.keepStatusOnAction(true);
		ccv.setAutoComplete(false);
		return ccv;
	}

	protected DropdownMenu getExpirationMonthMenu() {
		DropdownMenu month = (DropdownMenu) getStyledInterface(new DropdownMenu(
				PARAMETER_EXPIRES_MONTH));
		for (int a = 1; a <= 12; a++) {
			month.addMenuElement(a < 10 ? "0" + a : String.valueOf(a),
					a < 10 ? "0" + a : String.valueOf(a));
		}
		month.keepStatusOnAction(true);
		month.addFirstOption(new SelectOption(localize("race_reg.select_month",
				""), "-1"));
		return month;
	}

	protected DropdownMenu getExpirationYearMenu() {
		IWTimestamp stamp = new IWTimestamp();

		DropdownMenu year = (DropdownMenu) getStyledInterface(new DropdownMenu(
				PARAMETER_EXPIRES_YEAR));
		for (int a = stamp.getYear(); a <= stamp.getYear() + 8; a++) {
			year.addMenuElement(String.valueOf(a).substring(2),
					String.valueOf(a));
		}
		year.keepStatusOnAction(true);
		year.addFirstOption(new SelectOption(localize("race_reg.select_year",
				""), "-1"));
		return year;
	}

	protected TextInput getCardHolderNameInput() {
		TextInput nameField = (TextInput) getStyledInterface(new TextInput(
				PARAMETER_NAME_ON_CARD));
		nameField.setAutoComplete(false);
		nameField.setAsNotEmpty(localize(
				"race_reg.must_supply_card_holder_name",
				"You must supply card holder name"));
		nameField.keepStatusOnAction(true);
		return nameField;
	}

	protected TextInput getCardHolderEmailInput() {
		TextInput emailField = (TextInput) getStyledInterface(new TextInput(
				PARAMETER_CARD_HOLDER_EMAIL));
		emailField.setAsEmail(localize("race_reg.email_err_msg",
				"Not a valid email address"));
		emailField.keepStatusOnAction(true);
		return emailField;
	}

	protected Layer getCreditCardNumberInput() {
		Layer layer = new Layer(Layer.DIV);

		TextInput lastNumber = null;
		for (int a = 1; a <= 4; a++) {
			TextInput cardNumber = (TextInput) getStyledInterface(new TextInput(
					PARAMETER_CARD_NUMBER + "_" + a));
			if (a < 4) {
				cardNumber.setLength(4);
				cardNumber.setMaxlength(4);
				if (a > 1) {
					lastNumber.setNextInput(cardNumber);
				}

				lastNumber = cardNumber;
			} else {
				cardNumber.setLength(4);
				cardNumber.setMaxlength(7);
				lastNumber.setNextInput(cardNumber);
			}

			cardNumber.setMininumLength(
					4,
					localize("race_reg.not_valid_card_number",
							"Not a valid card number"));
			cardNumber.setAsIntegers(localize("race_reg.not_valid_card_number",
					"Not a valid card number"));
			cardNumber.setAsNotEmpty(localize(
					"race_reg.must_supply_card_number",
					"You must enter the credit card number"));
			cardNumber.keepStatusOnAction(true);
			cardNumber.setAutoComplete(false);

			layer.add(cardNumber);
			if (a != 4) {
				layer.add(Text.getNonBrakingSpace());
			}
		}

		return layer;
	}

	protected void stepPaymentInfo(IWContext iwc) throws Exception {
		Form form = new Form();
		form.addParameter(PARAMETER_ACTION, "-1");

		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setStyleClass("paymentTable");
		form.add(table);
		int row = 1;

		table.add(
				getPhasesTable(ACTION_STEP_PAYMENT_INFO, ACTION_SAVE,
						"race_reg.payment_info", "Payment info"), 1, row++);
		table.setHeight(row++, 12);

		table.add(
				getInformationTable(localize(
						"race_reg.information_text_step_4",
						"Information text 4...")), 1, row++);

		List<PaymentInfo> unpaidEntries = null;
		if (this.raceParticipantInfo.getUser() != null) {
			unpaidEntries = getUnpaidEntries(this.raceParticipantInfo.getUser()
					.getPrimaryKey().toString());
		}

		table.setHeight(row++, 18);

		float totalAmount = 0;
		float unpaidAmount = 0;
		
		if (!ListUtil.isEmpty(unpaidEntries)) {
			Table infoTable = getInformationTable(localize(
					"race_reg.missing_payment_text_1",
					"Missing payment text 1..."));
			infoTable.setStyleClass("borderlessTable");
			table.add(infoTable, 1, row++);
			
			Table unpaidEntriesTable = new Table();
			int entryRow = 1;
			int index = 0;
			for (PaymentInfo paymentInfo : unpaidEntries) {
				index++;
				unpaidEntriesTable.mergeCells(1, entryRow, 3, entryRow);
				unpaidEntriesTable.add(getText(index + CoreConstants.DOT + CoreConstants.SPACE + paymentInfo.toString()), 1, entryRow);
				unpaidEntriesTable.add(getText(formatAmount(paymentInfo.getPrice())), 4, entryRow++);
				totalAmount += paymentInfo.getPrice();
				unpaidAmount += paymentInfo.getPrice();
			}
			
			unpaidEntriesTable.setWidth(Table.HUNDRED_PERCENT);
			unpaidEntriesTable.setColumnStyle(1, "white-space", "nowrap");
			unpaidEntriesTable.setColumnAlignment(1, Table.HORIZONTAL_ALIGN_LEFT);
			unpaidEntriesTable.setColumnAlignment(4, Table.HORIZONTAL_ALIGN_RIGHT);
			
			table.add(unpaidEntriesTable, 1, row++);
		}
		
		/*
		 * Price table
		 */
		Table runnerTable = new Table();
		
		runnerTable.add(
				getHeader(localize("race_reg.participant_name",
						"Participant name")), 1, 1);
		runnerTable.add(getHeader(localize("race_reg.race", "Race")), 2, 1);
		runnerTable.add(getHeader(localize("race_reg.event", "Event")), 3, 1);
		runnerTable.add(getHeader(localize("race_reg.price", "Price")), 4, 1);
		table.add(runnerTable, 1, row++);
		table.setHeight(row++, 18);
		int runRow = 2;

		if (this.raceParticipantInfo.getUser() != null) {
			runnerTable.add(getText(this.raceParticipantInfo.getUser()
					.getName()), 1, runRow);
		} else {
			runnerTable.add(getText(""), 1, runRow);
		}
		runnerTable.add(getText(this.raceParticipantInfo.getRace().getName()),
				2, runRow);
		runnerTable.add(getText(this.raceParticipantInfo.getEvent()
				.getEventID()), 3, runRow);
		float runPrice = getRaceBusiness(iwc).getEventPriceForRunner(
				this.raceParticipantInfo);

		totalAmount += runPrice;
		runnerTable.add(getText(formatAmount(runPrice)), 4, runRow++);

		if (unpaidEntries == null) {
			getLogger().log(
					Level.WARNING,
					"Error getting unpaid entries for user "
							+ (this.raceParticipantInfo.getUser() == null ? null
									: this.raceParticipantInfo.getUser()
									.getPrimaryKey().toString()));
		}

		if (this.raceParticipantInfo.isRentTimeTransmitter()) {
			float ttPrice = raceParticipantInfo.getEvent()
					.getTimeTransmitterPrice();
			if (ttPrice < 0) {
				ttPrice = 0;
			}

			runnerTable.add(
					getText(localize("race_reg.rent_time_transmitter2",
							"Rent time transmitter")), 3, runRow);
			runnerTable.add(getText(formatAmount(ttPrice)), 4, runRow);
			runRow++;
			totalAmount += ttPrice;
		}

		if (!ListUtil.isEmpty(unpaidEntries)) {
			Table infoTable = getInformationTable(localize(
					"race_reg.missing_payment_text_2",
					"Missing payment text 2..."));
			infoTable.setStyleClass("borderlessTable");
			table.add(infoTable, 1, row++);
		}

		/*
		 * Season
		 */
		if (iwc.getIWMainApplication().getSettings()
				.getBoolean("msi.check_membership_registration", false)) {
			if (!isMembershipFeePayed()) {
				String priceString = getApplicationProperty(
						MSIConstants.PROPERTY_SEASON_PRICE, "5000");
				float seasonPrice = Float.valueOf(priceString);
				if (seasonPrice < 0) {
					seasonPrice = 0;
				}

				runnerTable.add(
						getText(localize("season_editor.season", "Season")), 3,
						runRow);
				runnerTable.add(getText(formatAmount(seasonPrice)), 4, runRow);
				runRow++;
				totalAmount += seasonPrice;
				this.raceParticipantInfo.setSeasonPrice(seasonPrice);
			}
		}

		this.raceParticipantInfo.setUnpaidAmount(unpaidAmount);
		this.raceParticipantInfo.setAmount(runPrice);

		runnerTable.add(new HiddenInput(PARAMETER_REFERENCE_NUMBER,
				this.raceParticipantInfo.getUser().getPersonalID()
						.replaceAll("-", "")));

		if (totalAmount == 0) {
			save(iwc, null, this.raceParticipantInfo, false);
			return;
		}

		/*
		 * Total amount
		 */
		runnerTable.setHeight(runRow++, 12);
		runnerTable.add(
				getHeader(localize("race_reg.total_amount", "Total amount")),
				1, runRow);
		runnerTable.add(getHeader(formatAmount(totalAmount)), 4, runRow);
		runnerTable.setColumnAlignment(4, Table.HORIZONTAL_ALIGN_RIGHT);

		/*
		 * Credit card table
		 */
		Table creditCardTable = new Table();
		creditCardTable.setWidth(Table.HUNDRED_PERCENT);
		creditCardTable.setWidth(1, "50%");
		creditCardTable.setWidth(3, "50%");
		creditCardTable.setWidth(2, 12);
		creditCardTable.setColumns(3);
		creditCardTable.setCellspacing(0);
		creditCardTable.setCellpadding(0);
		table.setTopCellBorder(1, row, 1, "#D7D7D7", "solid");
		table.setCellpaddingBottom(1, row++, 6);
		table.add(creditCardTable, 1, row++);
		int creditRow = 1;

		/*
		 * Credit card header
		 */
		creditCardTable.add(
				getHeader(localize("race_reg.credit_card_information",
						"Credit card information")), 1, creditRow);

		/*
		 * Credit card types
		 */
		creditCardTable.add(
				getRaceBusiness(iwc).getAvailableCardTypes(
						this.getResourceBundle()), 3, creditRow);
		creditCardTable.add(Text.getNonBrakingSpace(), 3, creditRow);
		Collection<Image> images = getRaceBusiness(iwc).getCreditCardImages();
		if (images != null) {
			Iterator<Image> iterator = images.iterator();
			while (iterator.hasNext()) {
				creditCardTable.add(iterator.next(), 3, creditRow);
				if (iterator.hasNext()) {
					creditCardTable
							.add(Text.getNonBrakingSpace(), 3, creditRow);
				}
			}
		}
		creditCardTable.setHeight(creditRow++, 12);

		/*
		 * Card holder and card number
		 */
		creditCardTable.add(
				getHeader(localize("race_reg.card_holder", "Card holder")), 1,
				creditRow);
		creditCardTable.add(
				getHeader(localize("race_reg.card_number", "Card number")), 3,
				creditRow++);

		creditCardTable.add(getCardHolderNameInput(), 1, creditRow);
		creditCardTable.add(getCreditCardNumberInput(), 3, creditRow);
		creditRow++;

		creditCardTable.setHeight(creditRow++, 3);

		/*
		 * Card expiration date and card CCV
		 */
		creditCardTable.add(
				getHeader(localize("race_reg.card_expires", "Card expires")),
				1, creditRow);
		creditCardTable.add(
				getHeader(localize("race_reg.ccv_number", "CCV number")), 3,
				creditRow++);
		creditCardTable.add(getExpirationMonthMenu(), 1, creditRow);
		creditCardTable.add(getText("/"), 1, creditRow);
		creditCardTable.add(getExpirationYearMenu(), 1, creditRow);
		creditCardTable.add(getCCVInput(), 3, creditRow++);

		creditCardTable.setHeight(creditRow++, 3);
		creditCardTable.mergeCells(3, creditRow, 3, creditRow + 1);
		creditCardTable
				.add(getText(localize(
						"race_reg.ccv_explanation_text",
						"A CCV number is a three digit number located on the back of all major credit cards.")),
						3, creditRow);

		/*
		 * Card holder email
		 */
		creditCardTable.add(
				getHeader(localize("race_reg.card_holder_email",
						"Cardholder email")), 1, creditRow++);
		creditCardTable.add(getCardHolderEmailInput(), 1, creditRow++);

		creditCardTable.add(new HiddenInput(PARAMETER_AMOUNT, String
				.valueOf(totalAmount)));
		creditCardTable.setHeight(creditRow++, 18);

		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize(
				"race_reg.pay", "Pay")));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_SAVE));

		SubmitButton previous = (SubmitButton) getButton(new SubmitButton(
				localize("previous", "Previous")));
		previous.setValueOnClick(PARAMETER_ACTION,
				String.valueOf(ACTION_STEP_DISCLAIMER));
		table.setHeight(row++, 18);
		table.add(previous, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(next, 1, row);
		form.setToDisableOnSubmit(next, true);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}

	protected String formatAmount(float amount) {
		return NumberFormat.getInstance().format(amount) + " ISK";
	}

	protected void save(IWContext iwc, Participant participant,
			RaceParticipantInfo raceParticipantInfo, boolean doPayment)
			throws RemoteException {
		String nameOnCard = null;
		double amount = 0;
		try {
			String cardNumber = null;
			String hiddenCardNumber = "XXXX-XXXX-XXXX-XXXX";

			String participantEmail = raceParticipantInfo.getEmail();
			String providedEmail = iwc.getParameter(PARAMETER_CARD_HOLDER_EMAIL);
			providedEmail = providedEmail == null ? null : providedEmail.trim();
			String email = providedEmail;
			if (!EmailValidator.getInstance().isValid(email)) {
				email = participantEmail;
			}

			String expiresMonth = null;
			String expiresYear = null;
			String ccVerifyNumber = null;
			String referenceNumber = null;
			IWTimestamp paymentStamp = new IWTimestamp();

			List<PaymentInfo> unpaidEntries = getUnpaidEntries(raceParticipantInfo.getUser().getPrimaryKey().toString());
			
			if (doPayment) {
				nameOnCard = iwc.getParameter(PARAMETER_NAME_ON_CARD);
				cardNumber = "";
				for (int i = 1; i <= 4; i++) {
					cardNumber += iwc.getParameter(PARAMETER_CARD_NUMBER + "_"
							+ i);
				}
				hiddenCardNumber = "XXXX-XXXX-XXXX-"
						+ iwc.getParameter(PARAMETER_CARD_NUMBER + "_" + 4);
				expiresMonth = iwc.getParameter(PARAMETER_EXPIRES_MONTH);
				expiresYear = iwc.getParameter(PARAMETER_EXPIRES_YEAR);
				ccVerifyNumber = iwc.getParameter(PARAMETER_CCV);
				email = EmailValidator.getInstance().isValid(email) ? email
						: iwc.getParameter(PARAMETER_CARD_HOLDER_EMAIL);
				amount = getRaceBusiness(iwc).getPriceForRunner(
						raceParticipantInfo);
				amount = amount + raceParticipantInfo.getSeasonPrice();
				amount = amount + raceParticipantInfo.getUnpaidAmount();
				referenceNumber = iwc.getParameter(PARAMETER_REFERENCE_NUMBER);
			}

			String properties = null;
			if (doPayment) {
				properties = getRaceBusiness(iwc).authorizePayment(nameOnCard,
						cardNumber, expiresMonth, expiresYear, ccVerifyNumber,
						amount, "ISK", referenceNumber);
			}

			if (participant == null) {
				participant = getRaceBusiness(iwc).saveParticipant(
						raceParticipantInfo, email, hiddenCardNumber, amount,
						paymentStamp, iwc.getCurrentLocale(), null);
			}
			if (doPayment) {
				String authCode = getRaceBusiness(iwc)
						.finishPayment(properties);
				if (!isMembershipFeePayed()) {
					setMembershipFeePayed(Boolean.TRUE);
				}
				if (participant != null) {
					participant.setPaymentAuthCode(authCode);
					participant.store();
				}
				
				if (!ListUtil.isEmpty(unpaidEntries)) {
					for (PaymentInfo unpaidEntry : unpaidEntries) {
						String participantId = unpaidEntry.getParticipantId();
						Participant unpaidParticipant = getRaceBusiness(iwc).getParticipantHome().findByPrimaryKey(participantId);
						if (unpaidParticipant != null && !StringUtil.isEmpty(authCode)) {
							unpaidParticipant.setPaymentAuthCode(authCode);
							unpaidParticipant.store();
						}
					}
				}
			}

			iwc.removeSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT_INFO);

			if (participant != null) {
				User user = participant.getUser();
				UserBusiness userBusiness = getUserBusiness(iwc);
				userBusiness.updateUserMail(user, email);
			}

			showReceipt(iwc, participant, amount, hiddenCardNumber,
					paymentStamp, doPayment);
		} catch (Exception e) {
			String error = "Failed to execute payment for " + nameOnCard
					+ ", amount: " + amount;
			getLogger().log(Level.WARNING, error, e);
			CoreUtil.sendExceptionNotification(error, e);

			String message = localize(
					"race_reg.save_failed",
					"There was an error when trying to finish registration. Please contact the msisport.is office.");
			if (e instanceof CreditCardAuthorizationException) {
				IWResourceBundle creditCardBundle = iwc.getIWMainApplication()
						.getBundle("com.idega.block.creditcard")
						.getResourceBundle(iwc.getCurrentLocale());
				message = ((CreditCardAuthorizationException) e)
						.getLocalizedMessage(creditCardBundle);
			}

			Page page = getParentPage();
			if (page != null) {
				page.setOnLoad("alert('" + message + "')");
			}
			try {
				stepPaymentInfo(iwc);
			} catch (Exception ee) {
				getLogger()
						.log(Level.WARNING, "Error showing payment info", ee);
			}

			CoreUtil.clearAllCaches();
		}
	}

	private void showReceipt(IWContext iwc, Participant participant,
			double amount, String cardNumber, IWTimestamp paymentStamp,
			boolean doPayment) {
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setStyleClass("receiptTable");
		int row = 1;
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_AMOUNT, new Double(amount));
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_CARD_NUMBER, cardNumber);
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_PAYMENT_DATE, paymentStamp);
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT, participant);

		table.add(
				getPhasesTable(ACTION_SAVE, ACTION_SAVE, "race_reg.receipt",
						"Receipt"), 1, row++);
		table.setHeight(row++, 18);

		table.add(
				getHeader(localize("race_reg.hello_participant",
						"Hello participant(s)")), 1, row++);
		table.setHeight(row++, 16);

		table.add(
				getText(localize("race_reg.payment_received",
						"We have received payment for the following:")), 1,
				row++);
		table.setHeight(row++, 8);

		Table runnerTable = new Table(3, 4);
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.add(
				getHeader(localize("race_reg.runner_name", "Runner name")), 1,
				1);
		runnerTable.add(getHeader(localize("race_reg.race", "Race")), 2, 1);
		runnerTable.add(getHeader(localize("race_reg.event", "Event")), 3, 1);
		table.add(runnerTable, 1, row++);
		int runRow = 2;
		Group race = participant.getRaceGroup();
		RaceEvent event = participant.getRaceEvent();

		runnerTable.add(getText(participant.getUser().getName()), 1, runRow);
		runnerTable.add(getText(race.getName()), 2, runRow);
		runnerTable.add(getText(event.getEventID()), 3, runRow++);

		if (doPayment) {
			Table creditCardTable = new Table(2, 3);
			creditCardTable.add(
					getHeader(localize("race_reg.payment_received_timestamp",
							"Payment received") + ":"), 1, 1);
			creditCardTable.add(getText(paymentStamp.getLocaleDateAndTime(
					iwc.getCurrentLocale(), IWTimestamp.SHORT,
					IWTimestamp.SHORT)), 2, 1);
			creditCardTable.add(
					getHeader(localize("race_reg.card_number", "Card number")
							+ ":"), 1, 2);
			creditCardTable.add(getText(cardNumber), 2, 2);
			creditCardTable.add(getHeader(localize("race_reg.amount", "Amount")
					+ ":"), 1, 3);
			creditCardTable.add(getText(formatAmount((float) amount)), 2, 3);
			table.setHeight(row++, 16);
			table.add(creditCardTable, 1, row++);
		}

		table.setHeight(row++, 16);
		table.add(
				getHeader(localize("race_reg.receipt_info_headline",
						"Receipt - Please Print It Out")), 1, row++);
		table.add(
				getText(localize("race_reg.receipt_info_headline_body",
						"This document is your receipt, please print this out.")),
				1, row++);

		table.setHeight(row++, 16);
		table.add(getText(localize("race_reg.best_regards", "Best regards,")),
				1, row++);
		table.add(getText(localize("race_reg.msi", "MSI")), 1, row++);
		table.add(getText("www.msisport.is"), 1, row++);

		table.setHeight(row++, 16);

		Link print = new Link(localize("print", "Print"));
		print.setStyleClass("msi_InterfaceButton");
		print.setPublicWindowToOpen(RegistrationReceivedPrintable.class);
		table.add(print, 1, row);

		add(table);
	}

	private void cancel(IWContext iwc) {
		removeRaceParticipantInfo(iwc);
	}

	private Race getRace(RaceParticipantInfo info) {
		if (info != null) {
			return info.getRace();
		}

		return null;
	}

	private RaceType getRaceType(RaceParticipantInfo info) {
		Race race = getRace(info);
		if (race != null) {
			return race.getRaceType();
		}

		return null;
	}

	protected String getRaceTypeKey(RaceParticipantInfo info) {
		RaceType raceType = getRaceType(info);
		if (raceType != null) {
			return raceType.getRaceType();
		}

		return null;
	}

	private int parseAction(IWContext iwc) throws Exception {
		int action = ACTION_STEP_PERSONALDETAILS;

		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}

		User user = iwc.getCurrentUser();
		raceParticipantInfo = (RaceParticipantInfo) iwc.getSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT_INFO);
		if (raceParticipantInfo == null) {
			raceParticipantInfo = new RaceParticipantInfo();
		}

		if (iwc.isParameterSet(PARAMETER_RACE)) {
			try {
				raceParticipantInfo.setRace(ConverterUtility.getInstance().convertGroupToRace(new Integer(iwc.getParameter(PARAMETER_RACE))));
			} catch (Exception e) {
				getLogger().warning("Error converting group to race: " + iwc.getParameter(PARAMETER_RACE));
			}
		}

		this.raceParticipantInfo.setUser(user);

		try {
			RaceUserSettings settings = this.getRaceBusiness(iwc).getRaceUserSettings(user);
			if (settings != null) {
				if (this.raceParticipantInfo.getRace() != null) {
					if (MSIConstants.RACE_TYPE_MX_AND_ENDURO.equals(getRaceTypeKey(this.raceParticipantInfo))) {
						if (settings.getRaceNumberMX() != null && settings.getRaceNumberMX().getApprovedDate() != null) {
							this.raceParticipantInfo.setRaceNumber(settings.getRaceNumberMX().getRaceNumber());
						}
					} else if (MSIConstants.RACE_TYPE_SNOCROSS.equals(getRaceTypeKey(this.raceParticipantInfo))) {
						if (settings.getRaceNumberSnocross() != null && settings.getRaceNumberSnocross().getApprovedDate() != null) {
							this.raceParticipantInfo.setRaceNumber(settings.getRaceNumberSnocross().getRaceNumber());
						}
					}
				}

				this.raceParticipantInfo.setRaceVehicle(settings.getVehicleType());
				this.raceParticipantInfo.setSponsors(settings.getSponsor());

				if (settings.getTransponderNumber() != null && !"".equals(settings.getTransponderNumber())) {
					this.raceParticipantInfo.setChipNumber(settings.getTransponderNumber());
					this.raceParticipantInfo.setOwnChip(true);
				}

				this.raceParticipantInfo.setRaceVehicleSubtype(settings.getVehicleSubType());
				this.raceParticipantInfo.setEngine(settings.getEngine());
				this.raceParticipantInfo.setEngineCC(settings.getEngineCC());
				this.raceParticipantInfo.setBodyNumber(settings.getBodyNumber());
				this.raceParticipantInfo.setModel(settings.getModel());
				this.raceParticipantInfo.setTeam(settings.getTeam());
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting setting for " + user, e);
		}

		if (iwc.isParameterSet(PARAMETER_EVENT)) {
			raceParticipantInfo.setEvent(ConverterUtility.getInstance().convertGroupToRaceEvent(new Integer(iwc.getParameter(PARAMETER_EVENT))));
			if (iwc.isParameterSet(PARAMETER_RENT_TIMETRANSMITTER)) {
				raceParticipantInfo.setRentTimeTransmitter(true);
			} else {
				raceParticipantInfo.setRentTimeTransmitter(false);
			}
		}

		if (iwc.isParameterSet(PARAMETER_EMAIL)) {
			raceParticipantInfo.setEmail(iwc.getParameter(PARAMETER_EMAIL));
		}

		if (iwc.isParameterSet(PARAMETER_HOME_PHONE)) {
			raceParticipantInfo.setHomePhone(iwc.getParameter(PARAMETER_HOME_PHONE));
		}

		if (iwc.isParameterSet(PARAMETER_MOBILE_PHONE)) {
			raceParticipantInfo.setMobilePhone(iwc.getParameter(PARAMETER_MOBILE_PHONE));
		}

		if (iwc.isParameterSet(PARAMETER_AGREE)) {
			raceParticipantInfo.setAgree(true);
		}

		if (iwc.isParameterSet(PARAMETER_COMMENT)) {
			raceParticipantInfo.setComment(iwc.getParameter(PARAMETER_COMMENT));
		}

		if (iwc.isParameterSet(PARAMETER_PARTNER1_PERSONAL_ID)) {
			String personalId = iwc.getParameter(PARAMETER_PARTNER1_PERSONAL_ID);
			User partner = getOrCreatePartner(
					iwc,
					personalId,
					iwc.isParameterSet(PARAMETER_PARTNER1_NAME) ? iwc
							.getParameter(PARAMETER_PARTNER1_NAME) : null);
			if (partner != null) {
				raceParticipantInfo.setFirstPartner(partner);
			}
		}
		
		if (iwc.isParameterSet(PARAMETER_PARTNER2_PERSONAL_ID)) {
			String personalId = iwc.getParameter(PARAMETER_PARTNER2_PERSONAL_ID);
			User partner = getOrCreatePartner(
					iwc,
					personalId,
					iwc.isParameterSet(PARAMETER_PARTNER2_NAME) ? iwc
							.getParameter(PARAMETER_PARTNER2_NAME) : null);
			if (partner != null) {
				raceParticipantInfo.setSecondPartner(partner);
			}
		}
		
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT_INFO, raceParticipantInfo);

		if (action == ACTION_STEP_DISCLAIMER) {
			int teamCount = this.raceParticipantInfo.getEvent().getTeamCount();

			if (teamCount > 1 && canShowPartners(iwc)) {
				
				if (this.raceParticipantInfo.getFirstPartner() == null) {
					action = ACTION_STEP_PERSONALDETAILS;
				}

				if (teamCount > 2) {
					if (this.raceParticipantInfo.getSecondPartner() == null) {
						action = ACTION_STEP_PERSONALDETAILS;
					}
				}
			}
		}

		return action;
	}

	private void removeRaceParticipantInfo(IWContext iwc) {
		iwc.removeSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT_INFO);
	}

	protected User getOrCreatePartner(IWContext iwc, String partnerPersonalId, String partnerNames) {
		String personalId = StringHandler.replace(partnerPersonalId, CoreConstants.MINUS, CoreConstants.EMPTY);
		User partner = null;
		
		try {
			partner = getUserBusiness(iwc).getUser(personalId);
		} catch (IDOFinderException e) {
			getLogger().log(Level.INFO, "No user found with personal ID: " + personalId);
		} catch (RemoteException | FinderException e) {
			getLogger().log(Level.WARNING, "Failed to get UserBusiness, cause of:", e);
		}
		
		if (partner != null) {
			return partner;
		} else {
			String[] names = MSIUtil.getParsedNames(partnerNames);
			partner = new UserBMPBean();
			partner.setPersonalID(personalId);
			if (names != null) {
				partner.setFirstName(names[0]);
				partner.setMiddleName(names[1]);
				partner.setLastName(names[2]);
			}
			
			return partner;
		}
		
	}
	
	private List<PaymentInfo> getUnpaidEntries(String userId) {
		return getPaymentsService().getUnpaidEntries(
				userId,
				getApplicationProperty(
						MSIConstants.PROPERTY_UNPAID_PERIOD_START_DATE,
						MSIConstants.UNPAID_PERIOD_START_DATE),
				getApplicationProperty(
						MSIConstants.PROPERTY_UNPAID_PERIOD_END_DATE,
						MSIConstants.UNPAID_PERIOD_END_DATE));
	}
	
}