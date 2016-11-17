package is.idega.idegaweb.msi.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneBMPBean;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.User;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.business.RaceParticipantInfo;
import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.util.MSIConstants;

public class ParticipantPayment extends Registration {

	public static final String PARAMETER_PARITICIPANT_ID = "prm_participant_id";

	private Integer participantId;

	private Integer getParticipantId(IWContext iwc) {
		if (participantId != null) {
			return participantId;
		}

		if (!iwc.isParameterSet(PARAMETER_PARITICIPANT_ID)) {
			getLogger().warning("Parameter " + PARAMETER_PARITICIPANT_ID + " is not provided!");
			return null;
		}

		String id = iwc.getParameter(PARAMETER_PARITICIPANT_ID);
		if (StringHandler.isNumeric(id)) {
			participantId = Integer.valueOf(id);
		}
		return participantId;
	}

	private RaceParticipantInfo raceParticipantInfo;

	private Participant getParticipant(IWContext iwc) throws Exception {
		Integer participantId = getParticipantId(iwc);
		return participantId == null ? null : getRaceBusiness(iwc).getParticipantByPrimaryKey(participantId);
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		switch (parseAction(iwc)) {
		case Registration.ACTION_STEP_PAYMENT_INFO:
			stepPaymentInfo(iwc);
			break;
		case Registration.ACTION_SAVE:
			try {
				Participant participant = getParticipant(iwc);
				save(iwc, participant, getRaceParticipantInfo(iwc, participant), true);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error saving payment", e);
				stepPaymentInfo(iwc);
			}
			break;
		}
	}

	protected RaceParticipantInfo getRaceParticipantInfo(IWContext iwc, Participant participant) throws Exception {
		RaceParticipantInfo raceParticipantInfo = (RaceParticipantInfo) iwc.getSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANT_INFO);
		if (raceParticipantInfo == null) {
			raceParticipantInfo = new RaceParticipantInfo();
		}
		raceParticipantInfo.setSeasonPrice(0);

		try {
			raceParticipantInfo.setRace(ConverterUtility.getInstance().convertGroupToRace(participant.getRaceGroupID()));
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}

		User user = participant.getUser();
		raceParticipantInfo.setUser(user);

		try {
			RaceUserSettings settings = this.getRaceBusiness(iwc).getRaceUserSettings(user);
			if (settings != null) {
				if (raceParticipantInfo.getRace() != null) {
					if (MSIConstants.RACE_TYPE_MX_AND_ENDURO.equals(getRaceTypeKey(raceParticipantInfo))) {
						if (settings.getRaceNumberMX() != null && settings.getRaceNumberMX().getApprovedDate() != null) {
							raceParticipantInfo.setRaceNumber(settings.getRaceNumberMX().getRaceNumber());
						}
					} else if (MSIConstants.RACE_TYPE_SNOCROSS.equals(getRaceTypeKey(raceParticipantInfo))) {
						if (settings.getRaceNumberSnocross() != null && settings.getRaceNumberSnocross().getApprovedDate() != null) {
							raceParticipantInfo.setRaceNumber(settings.getRaceNumberSnocross().getRaceNumber());
						}
					}
				}

				raceParticipantInfo.setRaceVehicle(settings.getVehicleType());
				raceParticipantInfo.setSponsors(settings.getSponsor());

				if (settings.getTransponderNumber() != null && !"".equals(settings.getTransponderNumber())) {
					raceParticipantInfo.setChipNumber(settings.getTransponderNumber());
					raceParticipantInfo.setOwnChip(true);
				}

				raceParticipantInfo.setRaceVehicleSubtype(settings.getVehicleSubType());
				raceParticipantInfo.setEngine(settings.getEngine());
				raceParticipantInfo.setEngineCC(settings.getEngineCC());
				raceParticipantInfo.setBodyNumber(settings.getBodyNumber());
				raceParticipantInfo.setModel(settings.getModel());
				raceParticipantInfo.setTeam(settings.getTeam());
			}
		} catch (RemoteException e) {}

		raceParticipantInfo.setEvent(ConverterUtility.getInstance().convertGroupToRaceEvent(participant.getEventGroupID()));
		raceParticipantInfo.setRentTimeTransmitter(participant.isRentsTimeTransmitter());

		Email email = null;
		try {
			email = user.getUsersEmail();
			if (email != null) {
				raceParticipantInfo.setEmail(email.getEmailAddress());
			}
		} catch (Exception e) {}

		Collection<Phone> phones = null;
		try {
			phones = user.getPhones(String.valueOf(PhoneBMPBean.getHomeNumberID()));
			if (!ListUtil.isEmpty(phones)) {
				raceParticipantInfo.setHomePhone(phones.iterator().next().getNumber());
			}
		} catch (Exception e) {}

		phones = null;
		try {
			phones = user.getPhones(String.valueOf(PhoneBMPBean.getMobileNumberID()));
			if (!ListUtil.isEmpty(phones)) {
				raceParticipantInfo.setMobilePhone(phones.iterator().next().getNumber());
			}
		} catch (Exception e) {}

		raceParticipantInfo.setAgree(true);

		if (iwc.isParameterSet(PARAMETER_COMMENT)) {
			raceParticipantInfo.setComment(iwc.getParameter(PARAMETER_COMMENT));
		}

		if (iwc.isParameterSet(PARAMETER_PARTNER1)) {
			raceParticipantInfo.setPartner1(iwc.getParameter(PARAMETER_PARTNER1));
		}

		if (iwc.isParameterSet(PARAMETER_PARTNER2)) {
			raceParticipantInfo.setPartner2(iwc.getParameter(PARAMETER_PARTNER2));
		}

		return raceParticipantInfo;
	}

	@Override
	protected void stepPaymentInfo(IWContext iwc) throws Exception {
		Form form = new Form();
		form.addParameter(PARAMETER_ACTION, "-1");

		Participant participant = getParticipant(iwc);
		if (participant == null) {
			return;
		}
		if (!StringUtil.isEmpty(participant.getPaymentAuthCode())) {
			return;
		}

		raceParticipantInfo = getRaceParticipantInfo(iwc, participant);
		form.addParameter(PARAMETER_PARITICIPANT_ID, getParticipantId(iwc));

		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setStyleClass("paymentTable");
		form.add(table);
		int row = 1;

		/*
		 * Price table
		 */
		Table runnerTable = new Table();
		runnerTable.add(getHeader(localize("race_reg.participant_name", "Participant name")), 1, 1);
		runnerTable.add(getHeader(localize("race_reg.race", "Race")), 2, 1);
		runnerTable.add(getHeader(localize("race_reg.event", "Event")), 3, 1);
		runnerTable.add(getHeader(localize("race_reg.price", "Price")), 4, 1);
		table.add(runnerTable, 1, row++);
		table.setHeight(row++, 18);
		int runRow = 2;

		if (this.raceParticipantInfo.getUser() != null) {
			runnerTable.add(getText(this.raceParticipantInfo.getUser().getName()), 1, runRow);
		} else {
			runnerTable.add(getText(""), 1, runRow);
		}

		runnerTable.add(getText(this.raceParticipantInfo.getRace().getName()), 2, runRow);
		runnerTable.add(getText(this.raceParticipantInfo.getEvent().getEventID()), 3, runRow);
		float runPrice = Float.parseFloat(participant.getPayedAmount());

		float totalAmount = runPrice;
		runnerTable.add(getText(formatAmount(runPrice)), 4, runRow++);

		if (participant.isRentsTimeTransmitter()) {
			float ttPrice = raceParticipantInfo.getEvent().getTimeTransmitterPrice();
			if (ttPrice < 0) {
				ttPrice = 0;
			}

			runnerTable.add(getText(localize("race_reg.rent_time_transmitter2", "Rent time transmitter")),3,runRow);
			runnerTable.add(getText(formatAmount(ttPrice)),4,runRow);
			runRow++;
			totalAmount += ttPrice;
		}

		this.raceParticipantInfo.setAmount(runPrice);

		runnerTable.add(new HiddenInput(PARAMETER_REFERENCE_NUMBER, this.raceParticipantInfo.getUser().getPersonalID().replaceAll("-", "")));

		/*
		 * Total amount
		 */
		runnerTable.setHeight(runRow++, 12);
		runnerTable.add(getHeader(localize("race_reg.total_amount", "Total amount")), 1, runRow);
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
		creditCardTable.add(getHeader(localize("race_reg.credit_card_information", "Credit card information")), 1, creditRow);

		/*
		 * Credit card types
		 */
		creditCardTable.add(getRaceBusiness(iwc).getAvailableCardTypes(this.getResourceBundle()), 3, creditRow);
		creditCardTable.add(Text.getNonBrakingSpace(), 3, creditRow);
		Collection<Image> images = getRaceBusiness(iwc).getCreditCardImages();
		if (images != null) {
			Iterator<Image> iterator = images.iterator();
			while (iterator.hasNext()) {
				creditCardTable.add(iterator.next(), 3, creditRow);
				if (iterator.hasNext()) {
					creditCardTable.add(Text.getNonBrakingSpace(), 3, creditRow);
				}
			}
		}
		creditCardTable.setHeight(creditRow++, 12);

		/*
		 * Card holder and card number
		 */
		creditCardTable.add(getHeader(localize("race_reg.card_holder", "Card holder")), 1, creditRow);
		creditCardTable.add(getHeader(localize("race_reg.card_number", "Card number")), 3, creditRow++);

		TextInput cardHolderNameInput = getCardHolderNameInput();
		cardHolderNameInput.setValue(participant.getUser().getName());
		creditCardTable.add(cardHolderNameInput, 1, creditRow);
		creditCardTable.add(getCreditCardNumberInput(), 3, creditRow);
		creditRow++;

		creditCardTable.setHeight(creditRow++, 3);

		/*
		 * Card expiration date and card CCV
		 */
		creditCardTable.add(getHeader(localize("race_reg.card_expires", "Card expires")), 1, creditRow);
		creditCardTable.add(getHeader(localize("race_reg.ccv_number", "CCV number")), 3, creditRow++);
		creditCardTable.add(getExpirationMonthMenu(), 1, creditRow);
		creditCardTable.add(getText("/"), 1, creditRow);
		creditCardTable.add(getExpirationYearMenu(), 1, creditRow);
		creditCardTable.add(getCCVInput(), 3, creditRow++);

		creditCardTable.setHeight(creditRow++, 3);
		creditCardTable.mergeCells(3, creditRow, 3, creditRow + 1);
		creditCardTable.add(getText(localize("race_reg.ccv_explanation_text", "A CCV number is a three digit number located on the back of all major credit cards.")), 3, creditRow);

		/*
		 * Card holder email
		 */
		creditCardTable.add(getHeader(localize("race_reg.card_holder_email", "Cardholder email")), 1, creditRow++);
		TextInput cardHolderEmailInput = getCardHolderEmailInput();
		try {
			Email email = participant.getUser().getUsersEmail();
			if (email != null) {
				cardHolderEmailInput.setValue(email.getEmailAddress());
			}
		} catch (Exception e) {}
		creditCardTable.add(cardHolderEmailInput, 1, creditRow++);

		creditCardTable.add(new HiddenInput(PARAMETER_AMOUNT, String.valueOf(totalAmount)));
		creditCardTable.setHeight(creditRow++, 18);

		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("race_reg.pay", "Pay")));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_SAVE));

		table.setHeight(row++, 18);
		table.add(next, 1, row);
		form.setToDisableOnSubmit(next, true);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}

	private int parseAction(IWContext iwc) throws Exception {
		int action = Registration.ACTION_STEP_PAYMENT_INFO;

		if (iwc.isParameterSet(Registration.PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(Registration.PARAMETER_ACTION));
		}

		return action;
	}

}