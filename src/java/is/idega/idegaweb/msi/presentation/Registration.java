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
import is.idega.idegaweb.msi.business.RaceParticipantInfo;
import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.block.creditcard.business.CreditCardAuthorizationException;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOCreateException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
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
import com.idega.user.business.NoEmailFoundException;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

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

	private static final String PARAMETER_ACTION = "prm_action";
	private static final String PARAMETER_FROM_ACTION = "prm_from_action";

	public static final String PARAMETER_RACE = "prm_race";
	private static final String PARAMETER_EVENT = "prm_event";

	private static final String PARAMETER_EMAIL = "prm_email";
	private static final String PARAMETER_HOME_PHONE = "prm_home_phone";
	private static final String PARAMETER_MOBILE_PHONE = "prm_mobile_phone";
	private static final String PARAMETER_AGREE = "prm_agree";

	private static final String PARAMETER_NAME_ON_CARD = "prm_name_on_card";
	private static final String PARAMETER_CARD_NUMBER = "prm_card_number";
	private static final String PARAMETER_EXPIRES_MONTH = "prm_expires_month";
	private static final String PARAMETER_EXPIRES_YEAR = "prm_expires_year";
	private static final String PARAMETER_CCV = "prm_ccv";
	private static final String PARAMETER_AMOUNT = "prm_amount";
	private static final String PARAMETER_CARD_HOLDER_EMAIL = "prm_card_holder_email";
	private static final String PARAMETER_REFERENCE_NUMBER = "prm_reference_number";
	
	private static final String PARAMETER_COMMENT = "prm_comment";
	private static final String PARAMETER_PARTNER1 = "prm_partner1";
	private static final String PARAMETER_PARTNER2 = "prm_partner2";

	private static final int ACTION_STEP_PERSONALDETAILS = 1;
	private static final int ACTION_STEP_DISCLAIMER = 2;
	private static final int ACTION_STEP_PAYMENT_INFO = 3;
	private static final int ACTION_SAVE = 4;
	private static final int ACTION_CANCEL = 5;

	private RaceParticipantInfo raceParticipantInfo;

	public void main(IWContext iwc) throws Exception {
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
			save(iwc, true);
			break;
		case ACTION_CANCEL:
			cancel(iwc);
			break;
		}
	}

	private void stepPersonalDetails(IWContext iwc) throws RemoteException, FinderException {
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

		table.add(getPhasesTable(ACTION_STEP_PERSONALDETAILS, ACTION_SAVE,
				"race_reg.registration", "Registration"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize(
				"race_reg.information_text_step_1", "Information text 1...")),
				1, row++);
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

		DropdownMenu eventsDropdown = (DropdownMenu) getStyledInterface(new DropdownMenu(
				PARAMETER_EVENT));
		eventsDropdown.setAsNotEmpty(localize("race_reg.must_select_distance",
				"You have to select a distance"));
		eventsDropdown.addMenuElement(-1, localize("race_reg.select_distance","Please select distance"));
		Map events = getRaceBusiness(iwc).getEventsForRace(raceParticipantInfo.getRace());
		Iterator it = events.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			RaceEvent event = (RaceEvent) events.get(key);
			eventsDropdown.addOption(new SelectOption(event.getEventID(), event.getPrimaryKey().toString()));
		}

		Text redStar = getHeader("*");
		redStar.setFontColor("#ff0000");

		choiceTable.add(getHeader(localize("race_reg.race_name", "Race name")),
				1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(
				getHeader(localize("race_reg.event_name", "Event name")), 3,
				iRow);
		choiceTable.add(redStar, 3, iRow++);
		if (this.raceParticipantInfo.getRace() != null) {
			choiceTable.add(this.raceParticipantInfo.getRace().getName(), 1, iRow);
		}
		choiceTable.add(eventsDropdown, 3, iRow++);

		choiceTable.setHeight(iRow++, 12);

		Text nameField = (Text) getText("");
		nameField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getUser() != null) {
			nameField.setText(this.raceParticipantInfo.getUser().getName());
		}

		Text emailField = (Text) getText("");
		emailField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getEmail() != null) {
			emailField.setText(this.raceParticipantInfo.getEmail());
			this.add(new HiddenInput(PARAMETER_EMAIL, this.raceParticipantInfo.getEmail()));
		} else if (this.raceParticipantInfo.getUser() != null) {
			try {
				Email mail = getUserBusiness(iwc).getUsersMainEmail(
						this.raceParticipantInfo.getUser());
				emailField.setText(mail.getEmailAddress());
				this.add(new HiddenInput(PARAMETER_EMAIL, mail.getEmailAddress()));
			} catch (NoEmailFoundException nefe) {
			}
		}

		choiceTable.add(getHeader(localize("race_reg.participant_name", "Name")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable
				.add(getHeader(localize("race_reg.email", "Email")), 3, iRow);
		choiceTable.add(redStar, 3, iRow++);
		choiceTable.add(nameField, 1, iRow);
		choiceTable.add(emailField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text ssnISField = (Text) getText("");
		if (this.raceParticipantInfo.getUser() != null) {
			ssnISField.setText(this.raceParticipantInfo.getUser().getPersonalID());
		}

		Text telField = (Text) getText("");
		telField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getHomePhone() != null) {
			telField.setText(this.raceParticipantInfo.getHomePhone());
			this.add(new HiddenInput(PARAMETER_HOME_PHONE, this.raceParticipantInfo.getHomePhone()));
		} else if (this.raceParticipantInfo.getUser() != null) {
			try {
				Phone phone = getUserBusiness(iwc).getUsersHomePhone(
						this.raceParticipantInfo.getUser());
				telField.setText(phone.getNumber());
				this.add(new HiddenInput(PARAMETER_HOME_PHONE, phone.getNumber()));
			} catch (NoPhoneFoundException nefe) {
				// No phone registered...
			}
		}

		choiceTable.add(getHeader(localize("race_reg.ssn", "SSN")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable
				.add(getHeader(localize("race_reg.telephone", "Telephone")), 3, iRow++);
		choiceTable.add(ssnISField, 1, iRow);
		choiceTable.add(telField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text addressField = (Text) getText("");
		addressField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getUser() != null) {
			Address address = getUserBusiness(iwc).getUsersMainAddress(
					this.raceParticipantInfo.getUser());
			if (address != null) {
				addressField.setText(address.getStreetAddress());
			}
		}

		Text mobileField = (Text) getText("");
		mobileField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getMobilePhone() != null) {
			mobileField.setText(this.raceParticipantInfo.getMobilePhone());
			this.add(new HiddenInput(PARAMETER_MOBILE_PHONE, this.raceParticipantInfo.getMobilePhone()));
		} else if (this.raceParticipantInfo.getUser() != null) {
			try {
				Phone phone = getUserBusiness(iwc).getUsersMobilePhone(
						this.raceParticipantInfo.getUser());
				mobileField.setText(phone.getNumber());
				this.add(new HiddenInput(PARAMETER_MOBILE_PHONE, phone.getNumber()));
			} catch (NoPhoneFoundException nefe) {
			}
		}

		choiceTable.add(getHeader(localize("race_reg.address", "Address")), 1,
				iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize(MSIConstants.RR_MOBILE,
				"Mobile Phone")),
				3, iRow++);
		choiceTable.add(addressField, 1, iRow);
		choiceTable.add(mobileField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text postalField = (Text) getText("");
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

		Text raceNumberField = (Text) getText("");
		if (this.raceParticipantInfo.getRaceNumber() != null) {
			raceNumberField.setText(this.raceParticipantInfo.getRaceNumber());
		}
		
		choiceTable.add(getHeader(localize(MSIConstants.RR_POSTAL,
				"Postal Code")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize("race_reg.race_number",
				"Race number")), 3, iRow);
		choiceTable.add(redStar, 3, iRow++);
		choiceTable.add(postalField, 1, iRow);
		choiceTable.add(raceNumberField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);


		Text raceVehicleField = (Text) getText("");
		if (this.raceParticipantInfo.getRaceVehicle() != null) {
			raceVehicleField.setText(this.raceParticipantInfo.getRaceVehicle().getLocalizationKey());
		}

		Text raceVehicleSubtypeField = (Text) getText("");
		raceVehicleSubtypeField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getRaceVehicleSubtype() != null) {
			raceVehicleSubtypeField.setText(this.raceParticipantInfo.getRaceVehicleSubtype().getLocalizationKey());
		}

		choiceTable.add(getHeader(localize("race_reg.race_vehicle",
				"Race vehicle")), 1, iRow);
		choiceTable.add(getHeader(localize("race_reg.race_vehicle_subtype",
				"Race vehicle subtype")), 3, iRow++);
		choiceTable.add(raceVehicleField, 1, iRow);
		choiceTable.add(raceVehicleSubtypeField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text engineField = (Text) getText("");
		if (this.raceParticipantInfo.getEngine() != null) {
			engineField.setText(this.raceParticipantInfo.getEngine());
		}

		Text engineCCField = (Text) getText("");
		engineCCField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getEngineCC() != null) {
			engineCCField.setText(this.raceParticipantInfo.getEngineCC());
		}

		choiceTable.add(getHeader(localize("race_reg.engine",
				"Engine")), 1, iRow);
		choiceTable.add(getHeader(localize("race_reg.engineCC",
				"Engine CC")), 3, iRow++);
		choiceTable.add(engineField, 1, iRow);
		choiceTable.add(engineCCField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		Text bodyNumberField = (Text) getText("");
		if (this.raceParticipantInfo.getBodyNumber() != null) {
			bodyNumberField.setText(this.raceParticipantInfo.getBodyNumber());
		}

		Text modelField = (Text) getText("");
		modelField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getModel() != null) {
			modelField.setText(this.raceParticipantInfo.getModel());
		}

		choiceTable.add(getHeader(localize("race_reg.body_number",
				"Body number")), 1, iRow);
		choiceTable.add(getHeader(localize("race_reg.model",
				"Model")), 3, iRow++);
		choiceTable.add(bodyNumberField, 1, iRow);
		choiceTable.add(modelField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);
		
		//new		
		Text chipField = (Text) getText("");
		chipField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getChipNumber() != null) {
			chipField.setText(this.raceParticipantInfo.getChipNumber());
		}

		choiceTable.add(getHeader(localize("race_reg.chip_number",
				"Chip number")), 1, iRow++);
		choiceTable.add(chipField, 1, iRow++);
		choiceTable.setHeight(iRow++, 3);
		
		Text teamField = (Text) getText("");
		if (this.raceParticipantInfo.getTeam() != null) {
			teamField.setText(this.raceParticipantInfo.getTeam());
		}

		Text sponsorsField = (Text) getText("");
		sponsorsField.setWidth(Table.HUNDRED_PERCENT);
		if (this.raceParticipantInfo.getSponsors() != null) {
			sponsorsField.setText(this.raceParticipantInfo.getSponsors());
		}

		choiceTable.add(getHeader(localize("race_reg.team",
			"Team")), 1, iRow);
		choiceTable.add(getHeader(localize("race_reg.sponsors",
				"Sponsors")), 3, iRow++);
		choiceTable.add(teamField, 1, iRow);
		choiceTable.add(sponsorsField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);
		
		/*TextArea commentField = (TextArea) getStyledInterface(new TextArea(
				PARAMETER_COMMENT));
		commentField.setWidth(Table.HUNDRED_PERCENT);
		commentField.setMaximumCharacters(1000);*/
		TextInput partner1Field = new TextInput(PARAMETER_PARTNER1);
		if (this.raceParticipantInfo.getPartner1() != null) {
			partner1Field.setContent(this.raceParticipantInfo.getPartner1());
		}

		TextInput partner2Field = new TextInput(PARAMETER_PARTNER2);
		if (this.raceParticipantInfo.getPartner2() != null) {
			partner2Field.setContent(this.raceParticipantInfo.getPartner2());
		}

		choiceTable.mergeCells(1, iRow, 4, iRow);
		choiceTable.add(getHeader(localize("race_reg.comment",
				"Comment")), 1, iRow++);
		choiceTable.add(getHeader(localize("race_reg.partner1",
				"Partner1")), 1, iRow);
		choiceTable.mergeCells(2, iRow, 4, iRow);
		choiceTable.add(partner1Field, 1, iRow++);
		choiceTable.setHeight(iRow++, 3);

		choiceTable.add(getHeader(localize("race_reg.partner2",
				"Partner2")), 1, iRow);
		choiceTable.mergeCells(2, iRow, 4, iRow);
		choiceTable.add(partner2Field, 1, iRow++);
		choiceTable.setHeight(iRow++, 3);

		boolean canRegister = canRegister(iwc);
		
		if (canRegister) {
			SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize(
					"next", "Next")));
			next.setValueOnClick(PARAMETER_ACTION, String
					.valueOf(ACTION_STEP_DISCLAIMER));
	
			table.setHeight(row++, 18);
			table.add(next, 1, row);
			table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);
		} else {
			table.setHeight(row++, 18);
			table.add(getHeader(localize("race_reg.cant_register",
				"Can't register in a national tournament without a valid race number")), 1, row);
			table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);			
		}
		add(form);
	}

	private boolean canRegister(IWContext iwc) {
		if (iwc.isSuperAdmin()) {
			return true;
		}
		
		if (this.raceParticipantInfo.getRace().getRaceCategory().getCategoryKey().equals(MSIConstants.ICELANDIC_CHAMPIONSHIP)) {
			if (this.raceParticipantInfo.getRaceNumber() == null) {
				return false;
			}
			
			try {
				RaceNumber raceNumber = this.getRaceBusiness(iwc).getRaceNumberHome().findByRaceNumber(Integer.parseInt(this.raceParticipantInfo.getRaceNumber()), this.raceParticipantInfo.getRace().getRaceType());
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

		table.add(getPhasesTable(ACTION_STEP_DISCLAIMER, ACTION_SAVE,
				"race_reg.consent", "Consent"), 1, row++);
		table.setHeight(row++, 18);

		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize(
				"next", "Next")));
		next.setValueOnClick(PARAMETER_ACTION, String
				.valueOf(ACTION_STEP_PAYMENT_INFO));
		if (!this.raceParticipantInfo.isAgree()) {
			next.setDisabled(true);
		}

		CheckBox agree = getCheckBox(PARAMETER_AGREE, Boolean.TRUE.toString());
		agree.setToEnableWhenChecked(next);
		agree.setToDisableWhenUnchecked(next);
		agree.setChecked(this.raceParticipantInfo.isAgree());

		table.add(getText(localize("race_reg.information_text_step_3",
				"Information text 3...")), 1, row++);
		table.setHeight(row++, 6);
		table.add(agree, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(getHeader(localize("race_reg.agree_terms", "Yes, I agree")),
				1, row++);

		SubmitButton previous = (SubmitButton) getButton(new SubmitButton(
				localize("previous", "Previous")));
		previous.setValueOnClick(PARAMETER_ACTION, String
				.valueOf(ACTION_STEP_PERSONALDETAILS));

		table.setHeight(row++, 18);
		table.add(previous, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}

	private void stepPaymentInfo(IWContext iwc) throws RemoteException {
		Form form = new Form();
		form.addParameter(PARAMETER_ACTION, "-1");

		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(getPhasesTable(ACTION_STEP_PAYMENT_INFO, ACTION_SAVE,
				"race_reg.payment_info", "Payment info"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize(
				"race_reg.information_text_step_4", "Information text 4...")),
				1, row++);
		table.setHeight(row++, 18);

		Table runnerTable = new Table();
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.setCellspacing(0);
		runnerTable.add(getHeader(localize("race_reg.participant_name",
				"Participant name")), 1, 1);
		runnerTable.add(getHeader(localize("race_reg.race", "Race")), 2, 1);
		runnerTable.add(getHeader(localize("race_reg.event", "Event")), 3, 1);
		runnerTable.add(getHeader(localize("race_reg.price", "Price")), 4, 1);
		table.add(runnerTable, 1, row++);
		table.setHeight(row++, 18);
		int runRow = 2;

		float totalAmount = 0;
		if (this.raceParticipantInfo.getUser() != null) {
			runnerTable.add(getText(this.raceParticipantInfo.getUser().getName()), 1, runRow);
		} else {
			runnerTable.add(getText(""), 1, runRow);
		}
		runnerTable.add(getText(this.raceParticipantInfo.getRace().getName()), 2, runRow);
		runnerTable.add(getText(this.raceParticipantInfo.getEvent().getEventID()), 3, runRow);
		float runPrice = getRaceBusiness(iwc).getPriceForRunner(this.raceParticipantInfo);
		totalAmount += runPrice;
		runnerTable.add(getText(formatAmount(runPrice)), 4, runRow++);

		this.raceParticipantInfo.setAmount(runPrice);

			runnerTable.add(new HiddenInput(PARAMETER_REFERENCE_NUMBER,
					this.raceParticipantInfo.getUser().getPersonalID().replaceAll("-", "")));

		if (totalAmount == 0) {
			save(iwc, false);
			return;
		}

		runnerTable.setHeight(runRow++, 12);
		runnerTable.add(getHeader(localize("race_reg.total_amount",
				"Total amount")), 1, runRow);
		runnerTable.add(getHeader(formatAmount(totalAmount)), 4, runRow);
		runnerTable.setColumnAlignment(4, Table.HORIZONTAL_ALIGN_RIGHT);

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

		creditCardTable.add(
				getHeader(localize("race_reg.credit_card_information",
						"Credit card information")), 1, creditRow);
		creditCardTable.add(getRaceBusiness(iwc).getAvailableCardTypes(
				this.getResourceBundle()), 3, creditRow);
		creditCardTable.add(Text.getNonBrakingSpace(), 3, creditRow);
		Collection images = getRaceBusiness(iwc).getCreditCardImages();
		if (images != null) {
			Iterator iterator = images.iterator();
			while (iterator.hasNext()) {
				Image image = (Image) iterator.next();
				creditCardTable.add(image, 3, creditRow);
				if (iterator.hasNext()) {
					creditCardTable
							.add(Text.getNonBrakingSpace(), 3, creditRow);
				}
			}
		}
		creditCardTable.setHeight(creditRow++, 12);

		TextInput nameField = (TextInput) getStyledInterface(new TextInput(
				PARAMETER_NAME_ON_CARD));
		nameField.setAutoComplete(false);
		nameField.setAsNotEmpty(localize(
				"race_reg.must_supply_card_holder_name",
				"You must supply card holder name"));
		nameField.keepStatusOnAction(true);

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

		IWTimestamp stamp = new IWTimestamp();
		DropdownMenu month = (DropdownMenu) getStyledInterface(new DropdownMenu(
				PARAMETER_EXPIRES_MONTH));
		for (int a = 1; a <= 12; a++) {
			month.addMenuElement(a < 10 ? "0" + a : String.valueOf(a),
					a < 10 ? "0" + a : String.valueOf(a));
		}
		month.keepStatusOnAction(true);
		month.addFirstOption(new SelectOption(localize("race_reg.select_month",""), "-1"));
		
		DropdownMenu year = (DropdownMenu) getStyledInterface(new DropdownMenu(
				PARAMETER_EXPIRES_YEAR));
		for (int a = stamp.getYear(); a <= stamp.getYear() + 8; a++) {
			year.addMenuElement(String.valueOf(a).substring(2), String
					.valueOf(a));
		}
		year.keepStatusOnAction(true);
		year.addFirstOption(new SelectOption(localize("race_reg.select_year",""), "-1"));

		creditCardTable.add(getHeader(localize("race_reg.card_holder",
				"Card holder")), 1, creditRow);
		creditCardTable.add(getHeader(localize("race_reg.card_number",
				"Card number")), 3, creditRow++);
		creditCardTable.add(nameField, 1, creditRow);
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
			cardNumber.setMininumLength(4,
					localize("race_reg.not_valid_card_number",
							"Not a valid card number"));
			cardNumber.setAsIntegers(localize("race_reg.not_valid_card_number",
					"Not a valid card number"));
			cardNumber.setAsNotEmpty(localize(
					"race_reg.must_supply_card_number",
					"You must enter the credit card number"));
			cardNumber.keepStatusOnAction(true);
			cardNumber.setAutoComplete(false);

			creditCardTable.add(cardNumber, 3, creditRow);
			if (a != 4) {
				creditCardTable.add(Text.getNonBrakingSpace(), 3, creditRow);
			}
		}
		creditRow++;
		creditCardTable.setHeight(creditRow++, 3);

		creditCardTable.add(getHeader(localize("race_reg.card_expires",
				"Card expires")), 1, creditRow);
		creditCardTable.add(getHeader(localize("race_reg.ccv_number",
				"CCV number")), 3, creditRow++);
		creditCardTable.add(month, 1, creditRow);
		creditCardTable.add(getText("/"), 1, creditRow);
		creditCardTable.add(year, 1, creditRow);
		creditCardTable.add(ccv, 3, creditRow++);

		TextInput emailField = (TextInput) getStyledInterface(new TextInput(
				PARAMETER_CARD_HOLDER_EMAIL));
		emailField.setAsEmail(localize("race_reg.email_err_msg",
				"Not a valid email address"));
		emailField.keepStatusOnAction(true);

		creditCardTable.setHeight(creditRow++, 3);
		creditCardTable.mergeCells(3, creditRow, 3, creditRow + 1);
		creditCardTable
				.add(
						getText(localize(
								"race_reg.ccv_explanation_text",
								"A CCV number is a three digit number located on the back of all major credit cards.")),
						3, creditRow);
		creditCardTable.add(getHeader(localize("race_reg.card_holder_email",
				"Cardholder email")), 1, creditRow++);
		creditCardTable.add(emailField, 1, creditRow++);
		creditCardTable.add(new HiddenInput(PARAMETER_AMOUNT, String
				.valueOf(totalAmount)));
		creditCardTable.setHeight(creditRow++, 18);

		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize(
				"race_reg.pay", "Pay")));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_SAVE));

		SubmitButton previous = (SubmitButton) getButton(new SubmitButton(
				localize("previous", "Previous")));
		previous.setValueOnClick(PARAMETER_ACTION, String
				.valueOf(ACTION_STEP_DISCLAIMER));
		table.setHeight(row++, 18);
		table.add(previous, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(next, 1, row);
		form.setToDisableOnSubmit(next, true);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}

	private String formatAmount(float amount) {
		return NumberFormat.getInstance().format(amount) + " ISK";
	}

	private void save(IWContext iwc, boolean doPayment) throws RemoteException {
		try {
			String nameOnCard = null;
			String cardNumber = null;
			String hiddenCardNumber = "XXXX-XXXX-XXXX-XXXX";
			String email = this.raceParticipantInfo.getEmail();
			String expiresMonth = null;
			String expiresYear = null;
			String ccVerifyNumber = null;
			String referenceNumber = null;
			double amount = 0;
			IWTimestamp paymentStamp = new IWTimestamp();

			IWBundle iwb = getBundle(iwc);
			boolean disablePaymentProcess = "true".equalsIgnoreCase(iwb
					.getProperty("disable_payment_authorization_process",
							"false"));
			if (doPayment && disablePaymentProcess) {
				doPayment = false;
			}

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
				email = iwc.getParameter(PARAMETER_CARD_HOLDER_EMAIL);
				amount = Double.parseDouble(iwc.getParameter(PARAMETER_AMOUNT));
				referenceNumber = iwc.getParameter(PARAMETER_REFERENCE_NUMBER);
			}

			String properties = null;
			if (doPayment) {
				properties = getRaceBusiness(iwc).authorizePayment(nameOnCard,
						cardNumber, expiresMonth, expiresYear, ccVerifyNumber,
						amount, "ISK", referenceNumber);
			}
			Participant participant = getRaceBusiness(iwc).saveParticipant(
					this.raceParticipantInfo, email, hiddenCardNumber, amount, paymentStamp,
					iwc.getCurrentLocale());
			if (doPayment) {
				getRaceBusiness(iwc).finishPayment(properties);
			}
			iwc.removeSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT_INFO);

			showReceipt(iwc, participant, amount, hiddenCardNumber,
					paymentStamp, doPayment);
		} catch (IDOCreateException ice) {
			getParentPage()
					.setAlertOnLoad(
							localize(
									"race_reg.save_failed",
									"There was an error when trying to finish registration.  Please contact the msisport.is office."));
			ice.printStackTrace();
			stepPaymentInfo(iwc);
		} catch (CreditCardAuthorizationException ccae) {
			IWResourceBundle creditCardBundle = iwc.getIWMainApplication()
					.getBundle("com.idega.block.creditcard").getResourceBundle(
							iwc.getCurrentLocale());
			getParentPage().setAlertOnLoad(
					ccae.getLocalizedMessage(creditCardBundle));
			ccae.printStackTrace();
			stepPaymentInfo(iwc);
		}
	}

	private void showReceipt(IWContext iwc, Participant participant, double amount,
			String cardNumber, IWTimestamp paymentStamp, boolean doPayment) {
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		int row = 1;
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_AMOUNT, new Double(amount));
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_CARD_NUMBER, cardNumber);
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_PAYMENT_DATE, paymentStamp);
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT, participant);

		table.add(getPhasesTable(ACTION_SAVE, ACTION_SAVE, "race_reg.receipt",
				"Receipt"), 1, row++);
		table.setHeight(row++, 18);

		table.add(getHeader(localize("race_reg.hello_participant",
				"Hello participant(s)")), 1, row++);
		table.setHeight(row++, 16);

		table.add(getText(localize("race_reg.payment_received",
				"We have received payment for the following:")), 1, row++);
		table.setHeight(row++, 8);

		Table runnerTable = new Table(3, 4);
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.add(getHeader(localize("race_reg.runner_name",
				"Runner name")), 1, 1);
		runnerTable.add(getHeader(localize("race_reg.race", "Race")), 2, 1);
		runnerTable.add(getHeader(localize("race_reg.event", "Event")),
				3, 1);
		table.add(runnerTable, 1, row++);
		int runRow = 2;
		Group race = participant.getRaceGroup();
		RaceEvent event = participant.getRaceEvent();

		runnerTable
				.add(getText(participant.getUser().getName()), 1, runRow);
		runnerTable.add(getText(race.getName()), 2,
				runRow);
		runnerTable.add(getText(event.getEventID()), 3, runRow++);

		if (doPayment) {
			Table creditCardTable = new Table(2, 3);
			creditCardTable.add(getHeader(localize(
					"race_reg.payment_received_timestamp", "Payment received")
					+ ":"), 1, 1);
			creditCardTable.add(
					getText(paymentStamp.getLocaleDateAndTime(iwc
							.getCurrentLocale(), IWTimestamp.SHORT,
							IWTimestamp.SHORT)), 2, 1);
			creditCardTable.add(getHeader(localize("race_reg.card_number",
					"Card number")
					+ ":"), 1, 2);
			creditCardTable.add(getText(cardNumber), 2, 2);
			creditCardTable.add(getHeader(localize("race_reg.amount", "Amount")
					+ ":"), 1, 3);
			creditCardTable.add(getText(formatAmount((float) amount)), 2, 3);
			table.setHeight(row++, 16);
			table.add(creditCardTable, 1, row++);
		}

		table.setHeight(row++, 16);
		table.add(getHeader(localize("race_reg.receipt_info_headline",
				"Receipt - Please Print It Out")), 1, row++);
		table
				.add(
						getText(localize(
								"race_reg.receipt_info_headline_body",
								"This document is your receipt, please print this out.")),
						1, row++);

		table.setHeight(row++, 16);
		table.add(getText(localize("race_reg.best_regards", "Best regards,")),
				1, row++);
		table.add(getText(localize("race_reg.msi",
				"MSI")), 1, row++);
		table.add(getText("www.msisport.is"), 1, row++);

		table.setHeight(row++, 16);

		Link print = new Link(localize("print", "Print"));
		print.setPublicWindowToOpen(RegistrationReceivedPrintable.class);
		table.add(print, 1, row);

		add(table);
	}

	private void cancel(IWContext iwc) {
		removeRaceParticipantInfo(iwc);
	}

	private void collectValues(IWContext iwc) throws RemoteException,
			NumberFormatException, FinderException {
		User user = iwc.getCurrentUser();
		raceParticipantInfo = getRaceParticipantInfo(iwc);
		if (raceParticipantInfo == null) {
			raceParticipantInfo = new RaceParticipantInfo();
		}
		
		raceParticipantInfo.setUser(user);

		if (iwc.isParameterSet(PARAMETER_RACE)) {
			raceParticipantInfo.setRace(ConverterUtility.getInstance().convertGroupToRace(
					new Integer(iwc.getParameter(PARAMETER_RACE))));
		}

		fillParticipantInfoWithUserSettings(iwc);

		if (iwc.isParameterSet(PARAMETER_EVENT)) {
			raceParticipantInfo.setEvent(ConverterUtility.getInstance()
					.convertGroupToRaceEvent(new Integer(iwc.getParameter(PARAMETER_EVENT))));
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

		if (iwc.isParameterSet(PARAMETER_PARTNER1)) {
			raceParticipantInfo.setPartner1(iwc.getParameter(PARAMETER_PARTNER1));
		}

		if (iwc.isParameterSet(PARAMETER_PARTNER2)) {
			raceParticipantInfo.setPartner2(iwc.getParameter(PARAMETER_PARTNER2));
		}

		saveRaceParticipantInfo(iwc, raceParticipantInfo);
	}

	private void fillParticipantInfoWithUserSettings(IWContext iwc) {
		try {
			RaceUserSettings settings = this.getRaceBusiness(iwc).getRaceUserSettings(this.raceParticipantInfo.getUser());
			if (settings != null) {
				if (this.raceParticipantInfo.getRace() != null) {
					if (this.raceParticipantInfo.getRace().getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_MX_AND_ENDURO)) {
						if (settings.getRaceNumberMX() != null && settings.getRaceNumberMX().getApprovedDate() != null) {
							this.raceParticipantInfo.setRaceNumber(settings.getRaceNumberMX().getRaceNumber());
						}
					} else if (this.raceParticipantInfo.getRace().getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_SNOCROSS)) {
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
		} catch (RemoteException e) {
		}
	}

	private int parseAction(IWContext iwc) throws RemoteException,
			NumberFormatException, FinderException {
		int action = ACTION_STEP_PERSONALDETAILS;

		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		
		collectValues(iwc);

		if (action == ACTION_STEP_DISCLAIMER) {
			if (this.raceParticipantInfo.getEvent().getTeamCount() > 1) {
				if (this.raceParticipantInfo.getPartner1() == null || "".equals(this.raceParticipantInfo.getPartner1().trim())) {
					action = ACTION_STEP_PERSONALDETAILS;
				}
				
				if (this.raceParticipantInfo.getEvent().getTeamCount() > 2) {
					if (this.raceParticipantInfo.getPartner2() == null || "".equals(this.raceParticipantInfo.getPartner2().trim())) {
						action = ACTION_STEP_PERSONALDETAILS;
					}	
				}				
			}
		}
		
		return action;
	}
	
	private RaceParticipantInfo getRaceParticipantInfo(IWContext iwc) {
		return (RaceParticipantInfo) iwc.getSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT_INFO);
	}
	
	private void saveRaceParticipantInfo(IWContext iwc, RaceParticipantInfo raceParticipantInfo) {
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT_INFO, raceParticipantInfo);
	}
	
	private void removeRaceParticipantInfo(IWContext iwc) {
		iwc.removeSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT_INFO);
	}
}