/*
 * Created on Jun 30, 2004
 */
package is.idega.idegaweb.msi.business;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.mail.MessagingException;
import javax.transaction.UserTransaction;

import com.idega.block.creditcard.business.CreditCardAuthorizationException;
import com.idega.block.creditcard.business.CreditCardBusiness;
import com.idega.block.creditcard.business.CreditCardClient;
import com.idega.block.creditcard.data.CreditCardMerchant;
import com.idega.block.creditcard.data.KortathjonustanMerchant;
import com.idega.block.creditcard.data.KortathjonustanMerchantHome;
import com.idega.block.trade.data.CreditCardInformation;
import com.idega.block.trade.data.CreditCardInformationHome;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.core.contact.data.Email;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.messaging.MessagingSettings;
import com.idega.data.IDOCreateException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.ArrayUtil;
import com.idega.util.EmailValidator;
import com.idega.util.IWTimestamp;
import com.idega.util.SendMail;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;

import is.idega.idegaweb.msi.bean.TimeTransmitterRentProperties;
import is.idega.idegaweb.msi.data.Event;
import is.idega.idegaweb.msi.data.EventHome;
import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.ParticipantHome;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.RaceCategory;
import is.idega.idegaweb.msi.data.RaceCategoryHome;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.data.RaceEventHome;
import is.idega.idegaweb.msi.data.RaceHome;
import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceNumberHome;
import is.idega.idegaweb.msi.data.RaceType;
import is.idega.idegaweb.msi.data.RaceTypeHome;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.data.RaceUserSettingsHome;
import is.idega.idegaweb.msi.data.RaceVehicleType;
import is.idega.idegaweb.msi.data.RaceVehicleTypeHome;
import is.idega.idegaweb.msi.data.Season;
import is.idega.idegaweb.msi.data.SeasonHome;
import is.idega.idegaweb.msi.util.MSIConstants;

/**
 * Description: Business bean (service) for run... <br>
 * Copyright: Idega Software 2004 <br>
 * Company: Idega Software <br>
 *
 * @author birna
 */
public class RaceBusinessBean extends IBOServiceBean implements RaceBusiness {

	private static final long serialVersionUID = 3105168986587179336L;

	private final static String IW_BUNDLE_IDENTIFIER = MSIConstants.IW_BUNDLE_IDENTIFIER;

	public static final String PROPERTY_INFO_PK = "cc_info_pk";

	private EventHome getEventHome() {
		try {
			return (EventHome) IDOLookup.getHome(Event.class);
		} catch (IDOLookupException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING,
					"Failed to get " + EventHome.class + "cause of: ", e);
		}

		return null;
	}

	private RaceHome getRaceHome() {
		try {
			return (RaceHome) IDOLookup.getHome(Race.class);
		} catch (IDOLookupException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING,
					"Failed to get " + RaceHome.class + "cause of: ", e);
		}

		return null;
	}

	private RaceEventHome getRaceEventHome() {
		try {
			return (RaceEventHome) IDOLookup.getHome(RaceEvent.class);
		} catch (IDOLookupException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING,
					"Failed to get " + RaceEventHome.class + "cause of: ", e);
		}

		return null;
	}

	private SeasonHome getSeasonHome() {
		try {
			return (SeasonHome) IDOLookup.getHome(Season.class);
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING,
					"Failed to get " + SeasonHome.class + " cause of: ", e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.business.RaceBusiness#createRace(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Race createRace(
			String seasonID,
			String raceName,
			String raceDate,
			String lastRegistration,
			String lastRegistrationPrice1,
			String type,
			String category) {
		return getRaceHome().update(
				null,
				Integer.valueOf(seasonID),
				raceName,
				raceName,
				new IWTimestamp(raceDate).getTimestamp(),
				new IWTimestamp(lastRegistration).getTimestamp(),
				new IWTimestamp(lastRegistrationPrice1).getTimestamp(),
				type,
				category,
				null, false);
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.business.RaceBusiness#updateRace(is.idega.idegaweb.msi.data.Race, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateRace(
			Race race,
			String raceDate,
			String lastRegistration,
			String lastRegistrationPrice1,
			String type,
			String category) {
		getRaceHome().update((Integer) race.getPrimaryKey(),
				null, null, null,
				new IWTimestamp(raceDate).getTimestamp(),
				new IWTimestamp(lastRegistration).getTimestamp(),
				new IWTimestamp(lastRegistrationPrice1).getTimestamp(),
				type, category, null, true);
	}

	@Override
	public void updateRaceNumber(RaceNumber raceNumber, String userSSN) {
		RaceUserSettings settings = null;
		if (raceNumber.getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_SNOCROSS)) {
			try {
				settings = getRaceUserSettingsHome().findBySnocrossRaceNumber(raceNumber);
				settings.setRaceNumberSnocross(null);
				settings.store();
			} catch (Exception e) {}
		} else if (raceNumber.getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_MX_AND_ENDURO)) {
			try {
				settings = getRaceUserSettingsHome().findByMXRaceNumber(raceNumber);
				settings.setRaceNumberMX(null);
				settings.store();
			} catch (Exception e) {}
		}

		if (userSSN == null || "".equals(userSSN)) {
			if (raceNumber.getApplicationDate() != null && settings != null) {
				try {
					Email mail = getUserBiz().getUserMail(settings.getUser());
					sendMessage(
							mail.getEmailAddress(),
							this.getBundle().getLocalizedString("number_mail_rejection_subject", "Application rejected"),
							this.getBundle().getLocalizedString("number_mail_rejection_body", "Your application for a race number has been rejected rejected"));
				} catch (IBOLookupException e) {
				} catch (RemoteException e) {}
			}

			raceNumber.setApplicationDate(null);
			raceNumber.setApprovedDate(null);
			raceNumber.setIsApproved(false);
			raceNumber.setIsInUse(false);
			raceNumber.store();
		} else {
			try {
				User user = this.getUserBiz().getUserHome().findByPersonalID(userSSN);

				try {
					try {
						settings = getRaceUserSettingsHome().findByUser(user);
					} catch (FinderException e) {
						settings = null;
					}

					if (settings == null) {
						settings = getRaceUserSettingsHome().create();
						settings.setUser(user);
					}

					if (raceNumber.getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_SNOCROSS)) {
						settings.setRaceNumberSnocross(raceNumber);
					} else if (raceNumber.getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_MX_AND_ENDURO)) {
						settings.setRaceNumberMX(raceNumber);
					}

					settings.store();
				} catch (Exception e) {}

				getRaceNumberHome().update(
						(Integer) raceNumber.getPrimaryKey(),
						null,
						null,
						IWTimestamp.getTimestampRightNow(),
						Boolean.TRUE,
						Boolean.TRUE,
						(Integer) null);
			} catch (IBOLookupException e) {
			} catch (RemoteException e) {
			} catch (FinderException e) {
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.business.RaceBusiness#addEventsToRace(is.idega.idegaweb.msi.data.Race, java.lang.String[], java.util.Map, java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	public boolean addEventsToRace(
			Race race,
			String events[],
			Map<String, String> price,
			Map<String, String> price2,
			Map<String, String> teamCount,
			Map<String, TimeTransmitterRentProperties> timeTransmitterPrices) {
		if (race != null && !ArrayUtil.isEmpty(events)) {
			for (String eventId : events) {
				TimeTransmitterRentProperties properties = null;
				if (timeTransmitterPrices != null) {
					properties = timeTransmitterPrices.get(eventId);
				}

				if (getRaceEventHome().update(
						null,
						(Integer) race.getPrimaryKey(),
						eventId,
						getEventPrice(price, eventId),
						getEventPrice(price2, eventId),
						getTeamCount(teamCount, eventId),
						properties != null ? Float.parseFloat(properties.getPrice()) : null,
						properties != null ? properties.isRentOn() : null, true) == null) {
					return Boolean.FALSE;
				}
			}
		}

		return Boolean.TRUE;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.business.RaceBusiness#addEventsToRace(is.idega.idegaweb.msi.data.Race, java.lang.String[], java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	public boolean addEventsToRace(
			Race race,
			String events[],
			Map<String, String> price,
			Map<String, String> price2,
			Map<String, String> teamCount) {
		return addEventsToRace(race, events, price, price2, teamCount, null);
	}

	private float getEventPrice(Map<String, String> prices, String key) {
		if (!MapUtil.isEmpty(prices) && prices.containsKey(key)) {
			String value = prices.get(key);
			if (!StringUtil.isEmpty(value)) {
				try {
					return Float.parseFloat(value);
				} catch (NumberFormatException e) {
					return 0.0f;
				}
			}
		}

		return 0.0f;
	}

	private int getTeamCount(Map<String, String> teamCount, String key) {
		if (teamCount.containsKey(key)) {
			String value = teamCount.get(key);
			if (!StringUtil.isEmpty(value)) {
				try {
					return Integer.parseInt(value);
				} catch (NumberFormatException e) {
					return 0;
				}
			}
		}

		return 0;
	}

	@Override
	public Map<String, RaceEvent> getEventsForRace(Race race) throws FinderException, IBOLookupException, RemoteException {
		Group gRace = ConverterUtility.getInstance().convertRaceToGroup(race);

		String[] types = { MSIConstants.GROUP_TYPE_RACE_EVENT };
		Collection<Group> events = getGroupBiz().getChildGroups(gRace, types, true);
		if (events != null) {
			Map<String, RaceEvent> eventIDList = new HashMap<String, RaceEvent>();

			Iterator<Group> it = events.iterator();
			while (it.hasNext()) {
				RaceEvent event = ConverterUtility.getInstance().convertGroupToRaceEvent(it.next());
				eventIDList.put(event.getEventID(), event);
			}

			return eventIDList;
		}

		return null;
	}

	@Override
	public boolean isRegisteredInRun(int runID, int userID) {
		try {
			User user = getUserBiz().getUserHome().findByPrimaryKey(
					new Integer(userID));

			return getUserBiz().isMemberOfGroup(runID, user);
		} catch (RemoteException re) {
			log(re);
		} catch (FinderException fe) {
			// User does not exist in database...
		}
		return false;
	}

	@Override
	public Collection<Event> getEvents() {
		return getEventHome().findAll();
	}

	@Override
	public Collection<RaceType> getRaceTypes() {
		return getRaceTypeHome().findAll();
	}

	@Override
	public Collection<RaceCategory> getRaceCategories() {
		try {
			return ((RaceCategoryHome) IDOLookup.getHome(RaceCategory.class)).findAll();
		} catch (IDOLookupException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean createEvent(String name) {
		return getEventHome().update(name, Boolean.TRUE, Boolean.TRUE) != null;
	}

	@Override
	public boolean isRegisteredInRun(int runID, String personalID) {
		try {
			User user = getUserBiz().getUserHome().findByPersonalID(personalID);

			return getUserBiz().isMemberOfGroup(runID, user);
		} catch (RemoteException re) {
			log(re);
		} catch (FinderException fe) {
			// User does not exist in database...
		}
		return false;
	}


	@Override
	public Participant saveParticipant(
			RaceParticipantInfo raceParticipantInfo,
			String email,
			String hiddenCardNumber,
			double amount,
			IWTimestamp date,
			Locale locale,
			String paymentAuthCode
	) throws IDOCreateException {
		Participant retParticipant = null;

		UserTransaction trans = getSessionContext().getUserTransaction();
		try {
			trans.begin();

			User user = raceParticipantInfo.getUser();

			RaceEvent event = raceParticipantInfo.getEvent();
			event.addGroup(user);
			Race race = raceParticipantInfo.getRace();

			try {
				ParticipantHome runHome = (ParticipantHome) getIDOHome(Participant.class);
				Participant participant = runHome.create();
				participant.setUser(user);
				participant.setRaceGroup(ConverterUtility.getInstance().convertRaceToGroup(race));
				participant.setEventGroupID(((Integer)event.getPrimaryKey()).intValue());
				if (raceParticipantInfo.getAmount() > 0) {
					participant.setPayedAmount(String.valueOf(raceParticipantInfo.getAmount()));
				}

				participant.setChipNumber(raceParticipantInfo.getChipNumber());
				participant.setRaceNumber(raceParticipantInfo.getRaceNumber());
				participant.setRaceVehicle(raceParticipantInfo.getRaceVehicle());
				participant.setSponsors(raceParticipantInfo.getSponsors());
				participant.setComment(raceParticipantInfo.getComment());
				participant.setRentsTimeTransmitter(raceParticipantInfo.isRentTimeTransmitter());
				if (participant.getRaceEvent().getTeamCount() > 1) {
					participant.setPartner1(raceParticipantInfo.getPartner1());
					if (participant.getRaceEvent().getTeamCount() > 2) {
						participant.setPartner2(raceParticipantInfo.getPartner2());
					}
				}
				participant.setCreatedDate(date.getTimestamp());
				participant.setPaymentAuthCode(paymentAuthCode);
				participant.store();
				retParticipant = participant;

				getUserBiz().updateUserHomePhone(user, raceParticipantInfo.getHomePhone());
				getUserBiz().updateUserMobilePhone(user, raceParticipantInfo.getMobilePhone());
				if (!StringUtil.isEmpty(email)) {
					raceParticipantInfo.setEmail(email);
				}
				getUserBiz().updateUserMail(user, raceParticipantInfo.getEmail());

				if (raceParticipantInfo.getEmail() != null) {
					IWResourceBundle iwrb = getIWApplicationContext().getIWMainApplication().getBundle(MSIConstants.IW_BUNDLE_IDENTIFIER).getResourceBundle(locale);
					Object[] args = {
							user.getName(),
							race.getName(),
							event.getEventID()
					};
					String subject = iwrb.getLocalizedString(
							"registration_received_subject_mail",
							"Your registration has been received."
					);
					String body = MessageFormat.format(iwrb.getLocalizedString("registration_received_body_mail", "Your registration has been received."), args);
					sendMessage(raceParticipantInfo.getEmail(), subject, body);
				}
			} catch (CreateException ce) {
				ce.printStackTrace();
			} catch (RemoteException re) {
				throw new IBORuntimeException(re);
			}

			if (email != null) {
				IWResourceBundle iwrb = getIWApplicationContext().getIWMainApplication().getBundle(MSIConstants.IW_BUNDLE_IDENTIFIER).getResourceBundle(locale);
				Object[] args = {
						hiddenCardNumber,
						String.valueOf(amount),
						date.getLocaleDateAndTime(locale, IWTimestamp.SHORT, IWTimestamp.SHORT)
				};
				String subject = iwrb.getLocalizedString(
						"receipt_subject_mail",
						"Your receipt for registration on msisport.is"
				);
				String body = MessageFormat.format(
						iwrb.getLocalizedString("receipt_body_mail", "Your registration has been received."),
						args
				);
				sendMessage(email, subject, body);
			}
			trans.commit();
		} catch (Exception ex) {
			try {
				trans.rollback();
			} catch (javax.transaction.SystemException e) {
				throw new IDOCreateException(e.getMessage());
			}
			ex.printStackTrace();
			throw new IDOCreateException(ex);
		}

		return retParticipant;
	}

	@Override
	public CreditCardClient getCreditCardClient() throws Exception {
		return getCreditCardBusiness().getCreditCardClient(getCreditCardMerchant());
	}

	@Override
	public String finishPayment(String properties) throws CreditCardAuthorizationException {
		try {
			CreditCardClient client = getCreditCardBusiness().getCreditCardClient(getCreditCardMerchant());
			String authCode = client.finishTransaction(properties);
			return authCode;
		} catch (CreditCardAuthorizationException ccae) {
			getLogger().log(Level.WARNING, "Error finishing payment: " + properties, ccae);
			throw ccae;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error finishing payment: " + properties, e);
			throw new CreditCardAuthorizationException("Online payment failed. Unknown error.");
		}
	}

	@Override
	public String authorizePayment(
			String nameOnCard,
			String cardNumber,
			String monthExpires,
			String yearExpires,
			String ccVerifyNumber,
			double amount,
			String currency,
			String referenceNumber
		) throws CreditCardAuthorizationException {
		try {
			CreditCardClient client = getCreditCardBusiness().getCreditCardClient(getCreditCardMerchant());
			return client.creditcardAuthorization(
					nameOnCard,
					cardNumber,
					monthExpires,
					yearExpires,
					ccVerifyNumber,
					amount,
					currency,
					referenceNumber
			);
		} catch (CreditCardAuthorizationException ccae) {
			getLogger().log(Level.WARNING, "Error authorizing payment", ccae);
			throw ccae;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error authorizing payment", e);
			throw new CreditCardAuthorizationException("Online payment failed. Unknown error.");
		}
	}

	@Override
	public float getEventPriceForRunner(RaceParticipantInfo raceParticipantInfo){
		IWTimestamp now = IWTimestamp.RightNow();
		float racePrice = 0.0f;

		IWTimestamp lastReg = new IWTimestamp(raceParticipantInfo.getRace().getLastRegistrationDatePrice1());
		lastReg.addDays(1);

		if (now.isLaterThan(lastReg)) {
			racePrice += raceParticipantInfo.getEvent().getPrice2();
		} else {
			racePrice += raceParticipantInfo.getEvent().getPrice();
		}

		return racePrice;
	}

	@Override
	public float getPriceForRunner(RaceParticipantInfo raceParticipantInfo) {
		float racePrice = getEventPriceForRunner(raceParticipantInfo);
		if(raceParticipantInfo.isRentTimeTransmitter()){
			float ttPrice = raceParticipantInfo.getEvent().getTimeTransmitterPrice();
			if(ttPrice > 0){
				racePrice += ttPrice;
			}
		}
		return racePrice;
	}

	@Override
	public Collection getCreditCardImages() {
		try {
			return getCreditCardBusiness().getCreditCardTypeImages(
					getCreditCardBusiness().getCreditCardClient(
							getCreditCardMerchant()));
		} catch (FinderException fe) {
			fe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList();
	}

	private CreditCardMerchant getCreditCardMerchant() throws FinderException {
		String infoPK = getIWApplicationContext().getIWMainApplication()
				.getBundle(IW_BUNDLE_IDENTIFIER).getProperty(PROPERTY_INFO_PK);
		if (infoPK != null) {
			try {
				CreditCardInformationHome ccInfoHome = (CreditCardInformationHome) IDOLookup
						.getHome(CreditCardInformation.class);
				CreditCardInformation ccInfo = ccInfoHome
						.findByPrimaryKey(new Integer(infoPK));
				CreditCardMerchant merchant = getCreditCardBusiness()
						.getCreditCardMerchant(ccInfo);
				return merchant;
			} catch (IDOLookupException e) {
				throw new IBORuntimeException(e);
			}
		} else {
			String merchantPK = getIWApplicationContext()
					.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER)
					.getProperty(MSIConstants.PROPERTY_MERCHANT_PK);
			if (merchantPK != null) {
				try {
					return ((KortathjonustanMerchantHome) IDOLookup
							.getHome(KortathjonustanMerchant.class))
							.findByPrimaryKey(new Integer(merchantPK));
				} catch (IDOLookupException ile) {
					throw new IBORuntimeException(ile);
				}
			}
			return null;
		}
	}

	@Override
	public DropdownMenu getAvailableCardTypes(IWResourceBundle iwrb) {
		try {
			CreditCardMerchant merchant = getCreditCardMerchant();
			if (merchant != null) {
				return getCreditCardBusiness().getCreditCardTypes(
						getCreditCardBusiness().getCreditCardClient(merchant),
						iwrb, "CARD_TYPE");
			}
		} catch (FinderException fe) {
			fe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new DropdownMenu();
	}

	@Override
	public Season getSeasonByGroupId(Integer groupId) {
		try {
			SeasonHome seasonHome = (SeasonHome) getIDOHome(Season.class);
			return seasonHome.findByPrimaryKey(groupId);
		} catch (RemoteException e) {
			log(e);
		}

		return null;
	}

	@Override
	public void sendMessage(String email, String subject, String body) {
		boolean sendEmail = StringUtil.isEmpty(email) ? false : true;
		if (!sendEmail) {
			getLogger().warning("Receiver not provided for message: subject: " + subject + ", text: " + body);
			return;
		}

		email = email.trim();
		sendEmail = EmailValidator.getInstance().isValid(email);
		if (!sendEmail) {
			getLogger().warning("Invalid receiver '" + email + "' of message: subject: " + subject + ", text: " + body);
			return;
		}

		String fromAddress = "msi@msisport.is";
		String bcc = null;
		try {
			fromAddress = getApplicationProperty(MessagingSettings.PROP_MESSAGEBOX_FROM_ADDRESS, fromAddress);
			bcc = getApplicationProperty(MessagingSettings.PROP_SYSTEM_BCC_RECEIVER, bcc);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting mail property from bundle " + IW_BUNDLE_IDENTIFIER, e);
		}

		try {
			SendMail.send(fromAddress, email, null, bcc, null, subject, body);
		} catch (MessagingException me) {
			getLogger().log(Level.WARNING, "Error sending mail to address: " + email + ". Message was: " + me.getMessage(), me);
		}
	}

	@Override
	public Collection<Group> getRuns() {
		String[] type = { MSIConstants.GROUP_TYPE_RACE };
		try {
			return getGroupBiz().getGroups(type, true);
		} catch (Exception e) {}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.business.RaceBusiness#getSeasons()
	 */
	@Override
	public Collection<Group> getSeasons() {
		return getSeasonHome().findAllGroups();
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.business.RaceBusiness#getMXRaceNumbers()
	 */
	@Override
	public Collection<RaceNumber> getMXRaceNumbers() {
		try {
			return getRaceNumberHome().findAllNotInUseByType(
					getRaceTypeHome().findByRaceType(
							MSIConstants.RACE_TYPE_MX_AND_ENDURO));
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, "Failed to get MX numbers");
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.business.RaceBusiness#getSnocrossRaceNumbers()
	 */
	@Override
	public Collection<RaceNumber> getSnocrossRaceNumbers() {
		try {
			return getRaceNumberHome().findAllNotInUseByType(
					getRaceTypeHome().findByRaceType(
							MSIConstants.RACE_TYPE_SNOCROSS));
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, "Failed to get MX numbers");
		}

		return Collections.emptyList();
	}


	/**
	 * Gets all countries. This method is for example used when displaying a
	 * dropdown menu of all countries
	 *
	 * @return Colleciton of all countries
	 */
	@Override
	public Collection<Country> getCountries() {
		Collection<Country> countries = null;
		try {
			CountryHome countryHome = (CountryHome) getIDOHome(Country.class);
			countries = new ArrayList<Country>(countryHome.findAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return countries;
	}

	private GroupBusiness getGroupBiz() throws IBOLookupException {
		return IBOLookup.getServiceInstance(
				getIWApplicationContext(),
				GroupBusiness.class);
	}

	private CreditCardBusiness getCreditCardBusiness() {
		try {
			return IBOLookup.getServiceInstance(
					getIWApplicationContext(), CreditCardBusiness.class);
		} catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	@Override
	public UserBusiness getUserBiz() throws IBOLookupException {
		return IBOLookup.getServiceInstance(
				getIWApplicationContext(),
				UserBusiness.class);
	}

	@Override
	public Country getCountryByNationality(Object nationality) {
		Country country = null;
		try {
			CountryHome home = (CountryHome) getIDOHome(Country.class);
			try {
				int countryPK = Integer.parseInt(nationality.toString());
				country = home.findByPrimaryKey(new Integer(countryPK));
			} catch (NumberFormatException nfe) {
				country = home.findByIsoAbbreviation(nationality.toString());
			}
		} catch (FinderException fe) {
			// log(fe);
		} catch (RemoteException re) {
			// log(re);
		}
		return country;
	}

	@Override
	public Participant getParticipantByPrimaryKey(int participantID) {
		try {
			return getParticipantHome().findByPrimaryKey(participantID);
		} catch (FinderException e1) {
			e1.printStackTrace();
		}

		return null;
	}

	@Override
	public ParticipantHome getParticipantHome() {
		try {
			return (ParticipantHome) getIDOHome(Participant.class);
		} catch (RemoteException e) {
			getLogger().log(Level.WARNING,
					"Failed to get " + ParticipantHome.class + " cause of: ", e);
		}

		return null;
	}

	@Override
	public RaceUserSettingsHome getRaceUserSettingsHome() {
		try {
			return (RaceUserSettingsHome) getIDOHome(RaceUserSettings.class);
		} catch (RemoteException e) {
			getLogger().log(Level.WARNING,
					"Failed to get " + RaceUserSettingsHome.class + " cause of: ", e);
		}

		return null;
	}

	@Override
	public RaceTypeHome getRaceTypeHome() {
		try {
			return (RaceTypeHome) getIDOHome(RaceType.class);
		} catch (RemoteException e) {
			getLogger().log(Level.WARNING,
					"Failed to get " + RaceTypeHome.class + " cause of: ", e);
		}

		return null;
	}

	@Override
	public RaceNumberHome getRaceNumberHome() {
		try {
			return (RaceNumberHome) getIDOHome(RaceNumber.class);
		} catch (RemoteException e) {
			getLogger().log(Level.WARNING,
					"Failed to get " + RaceNumberHome.class + " cause of: ", e);
		}

		return null;
	}

	@Override
	public RaceVehicleTypeHome getRaceVehicleTypeHome() {
		try {
			return (RaceVehicleTypeHome) getIDOHome(RaceVehicleType.class);
		} catch (RemoteException e) {
			getLogger().log(Level.WARNING,
					"Failed to get " + RaceVehicleTypeHome.class + " cause of: ", e);
		}

		return null;
	}

	@Override
	public RaceUserSettings getRaceUserSettings(User user) {
		if (user == null) {
			return null;
		}
		RaceUserSettings settings = null;
		RaceUserSettingsHome home = getRaceUserSettingsHome();
		if (home != null) {
			try {
				settings = home.findByUser(user);
			} catch (FinderException e) {
			}
		}

		return settings;
	}

	@Override
	public List enableEvents(List ids) {
		try{
			ArrayList enabled = new ArrayList(ids.size());
			for(Iterator iter = ids.iterator();iter.hasNext();){
				Object id = iter.next();
				try{
					Event event = getEventHome().findByPrimaryKey(id.toString());
					event.setValid(true);
					event.store();
					enabled.add(id);
				}catch (Exception e) {
					getLogger().log(Level.WARNING, "Failed enabling event: '"+id+"'", e);
				}
			}
			return enabled;
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed enabling events: '"+ids+"'", e);
		}
		return Collections.emptyList();
	}

	@Override
	public List disableEvents(List ids) {
		try{
			ArrayList disabled = new ArrayList(ids.size());
			for(Iterator iter = ids.iterator();iter.hasNext();){
				Object id = iter.next();
				try{
					Event event = getEventHome().findByPrimaryKey(id.toString());
					event.setValid(false);
					event.store();
					disabled.add(id);
				}catch (Exception e) {
					getLogger().log(Level.WARNING, "Failed disabling event: '"+id+"'", e);
				}
			}
			return disabled;
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed disabling events: '"+ids+"'", e);
		}
		return Collections.emptyList();
	}

	@Override
	public Float getTransmitterPrices(String raceId, String eventId) {
		if (StringUtil.isEmpty(raceId)) {
			getLogger().warning("Event ID is not provided");
			return null;
		}
		if (StringUtil.isEmpty(eventId)) {
			getLogger().warning("Event ID is not provided");
			return null;
		}

		try {
			Race race = ((is.idega.idegaweb.msi.data.RaceHome) IDOLookup.getHome(Race.class)).findByPrimaryKey(raceId);
			Map<String, RaceEvent> raceEvents = getEventsForRace(race);
			if (raceEvents == null || raceEvents.isEmpty()) {
				getLogger().warning("No events found for race " + raceId);
				return null;
			}

			RaceEvent raceEvent = null;
			for (Iterator<RaceEvent> it = raceEvents.values().iterator(); (it.hasNext() && raceEvent == null);) {
				raceEvent = it.next();
				if (!eventId.equals(raceEvent.getId())) {
					raceEvent = null;
				}
			}
			if (raceEvent == null) {
				getLogger().warning("Event (ID: " + eventId + ") not found for race " + raceId + " in " + raceEvents);
				return null;
			}
			if (!raceEvent.isTimeTransmitterPriceOn()) {
				getLogger().info("Event (ID: " + eventId + ") does offer to rent transmitters for race " + raceId);
				return null;
			}

			Float price = Float.valueOf(raceEvent.getTimeTransmitterPrice());
			getLogger().info("Got price " + price + " for event (ID: " + eventId + ") and race " + raceId);
			return price;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting transmitter price for race: " + raceId + " and event: " + eventId, e);
		}

		return null;
	}
}