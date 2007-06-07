/*
 * Created on Jun 30, 2004
 */
package is.idega.idegaweb.msi.business;

import is.idega.block.family.business.FamilyLogic;
import is.idega.block.family.business.NoParentFound;
import is.idega.idegaweb.msi.data.Event;
import is.idega.idegaweb.msi.data.EventHome;
import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.ParticipantHome;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.Season;
import is.idega.idegaweb.msi.data.SeasonHome;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.transaction.UserTransaction;

import com.idega.block.creditcard.business.CreditCardAuthorizationException;
import com.idega.block.creditcard.business.CreditCardBusiness;
import com.idega.block.creditcard.business.CreditCardClient;
import com.idega.block.creditcard.data.CreditCardMerchant;
import com.idega.block.creditcard.data.KortathjonustanMerchant;
import com.idega.block.creditcard.data.KortathjonustanMerchantHome;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.core.contact.data.Email;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOCreateException;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.UnavailableIWContext;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Gender;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.text.Name;

/**
 * Description: Business bean (service) for run... <br>
 * Copyright: Idega Software 2004 <br>
 * Company: Idega Software <br>
 * 
 * @author birna
 */
public class RaceBusinessBean extends IBOServiceBean implements RaceBusiness {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3105168986587179336L;

	private final static String IW_BUNDLE_IDENTIFIER = MSIConstants.IW_BUNDLE_IDENTIFIER;

	private static String DEFAULT_SMTP_MAILSERVER = "mail.agurait.com";

	private static String PROP_SYSTEM_SMTP_MAILSERVER = "messagebox_smtp_mailserver";
	private static String PROP_CC_ADDRESS = "messagebox_cc_address";

	private static String PROP_MESSAGEBOX_FROM_ADDRESS = "messagebox_from_mailaddress";

	private static String DEFAULT_MESSAGEBOX_FROM_ADDRESS = "messagebox@idega.com";
	private static String DEFAULT_CC_ADDRESS = "hjordis@ibr.is";

	public Race createRace(String seasonID, String raceName, String raceDate,
			String lastRegistration, String price, String chipRent) {
		Race race = null;

		
		
		return race;
	}

	public void updateRace(Race race, String raceDate,
			String lastRegistration, String price, String chipRent) {
	}

	
	public boolean addEventsToRace(Race race, String events[]) {
		return true;
	}

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

	public boolean isRegisteredInRun(String year, Group run, User user) {
		try {
			Group runYear = null;
			String[] types = { MSIConstants.GROUP_TYPE_SEASON };
			Collection years = getGroupBiz().getChildGroups(run, types, true);
			Iterator iter = years.iterator();
			while (iter.hasNext()) {
				Group yearGroup = (Group) iter.next();
				if (yearGroup.getName().equals(year)) {
					runYear = yearGroup;
					break;
				}
			}

			if (runYear == null) {
				return false;
			}

			((ParticipantHome) IDOLookup.getHome(Participant.class))
					.findByUserAndRun(user, run, runYear);
			return true;
		} catch (FinderException fe) {
			return false;
		} catch (RemoteException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	public boolean doesGroupExist(Object distancePK, String groupName) {
		try {
			return ((ParticipantHome) IDOLookup.getHome(Participant.class))
					.getCountByDistanceAndGroupName(distancePK, groupName) > 0;
		} catch (IDOException ie) {
			ie.printStackTrace();
			return false;
		} catch (IDOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	public Collection getEvents() {
		try {
			return ((EventHome) IDOLookup.getHome(Event.class)).findAll();
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean createEvent(String name) {
		try {
			Event event = ((EventHome) IDOLookup.getHome(Event.class)).create();
			event.setName(name);
			event.store();
			
			return true;
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
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

	/**
	 * saves information on the run for the specific user puts user in the right
	 * group
	 */
	public void saveRun(int userID, String run, String distance, String year,
			String nationality, String tshirt, String chipOwnershipStatus,
			String chipNumber, String groupName, String bestTime,
			String goalTime, Locale locale) {
		Group groupRun = null;
		Group disGroup = null;
		int ageGenderGroupID = -1;
		User user = null;
		try {
			groupRun = getGroupBiz().getGroupByGroupID(Integer.parseInt(run));
		} catch (IBOLookupException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (UnavailableIWContext e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		String distanceType = null;
		try {
			user = getUserBiz().getUser(userID);
			if (distance != null && !distance.equals("")) {
				int disGroupID = Integer.parseInt(distance);

				try {
					disGroup = getGroupBiz().getGroupByGroupID(disGroupID);
					distanceType = disGroup.getName();
				} catch (UnavailableIWContext e1) {
					e1.printStackTrace();
				} catch (FinderException e1) {
					e1.printStackTrace();
				}
			}
			ParticipantHome runHome = (ParticipantHome) getIDOHome(Participant.class);
			Participant r = runHome.create();
			r.setUserID(userID);
			r.setRunTypeGroupID(Integer.parseInt(run));
			r.setRunDistanceGroupID(Integer.parseInt(distance));
			r.setRunYearGroupID(Integer.parseInt(year));
			if (ageGenderGroupID != -1) {
				r.setRunGroupGroupID(ageGenderGroupID);
			}
			r.setShirtSize(tshirt);
			r.setChipOwnershipStatus(chipOwnershipStatus);
			r.setChipNumber(chipNumber);
			r.setRunGroupName(groupName);
			r.setUserNationality(nationality);
			if (bestTime != null && !bestTime.equals("")) {
				r.setBestTime(bestTime);
			}
			if (goalTime != null && !goalTime.equals("")) {
				r.setGoalTime(goalTime);
			}
			r.store();

			Email email = getUserBiz().getUserMail(user);
			if (groupRun != null && user != null && email != null
					&& email.getEmailAddress() != null) {
				IWResourceBundle iwrb = getIWApplicationContext()
						.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER)
						.getResourceBundle(locale);
				Object[] args = {
						user.getName(),
						iwrb.getLocalizedString(groupRun.getName(), groupRun
								.getName()),
						iwrb.getLocalizedString(disGroup.getName(), disGroup
								.getName()),
						iwrb.getLocalizedString(tshirt, tshirt) };
				String subject = iwrb.getLocalizedString(
						"registration_received_subject_mail",
						"Your registration has been received.");
				String body = MessageFormat.format(iwrb.getLocalizedString(
						"registration_received_body_mail",
						"Your registration has been received."), args);
				sendMessage(email.getEmailAddress(), subject, body);
			}
		} catch (RemoteException rme) {
		} catch (CreateException cre) {
		}
	}

	public Collection saveParticipants(Collection runners, String email,
			String hiddenCardNumber, double amount, IWTimestamp date,
			Locale locale) throws IDOCreateException {
		Collection participants = new ArrayList();

		UserTransaction trans = getSessionContext().getUserTransaction();
		try {
			trans.begin();
			Iterator iter = runners.iterator();
			while (iter.hasNext()) {
				Runner runner = (Runner) iter.next();
				User user = runner.getUser();
				if (user == null) {
				}

				Group yearGroup = (Group) runner.getDistance().getParentNode();
				Group run = runner.getRun();
				Group distance = runner.getDistance();

				try {
					ParticipantHome runHome = (ParticipantHome) getIDOHome(Participant.class);
					Participant participant = runHome.create();
					participant.setUser(user);
					participant.setRunTypeGroup(run);
					participant.setRunDistanceGroup(distance);
					participant.setRunYearGroup(yearGroup);
					participant.setMaySponsorContact(runner
							.isMaySponsorContactRunner());
					if (runner.getAmount() > 0) {
						participant.setPayedAmount(String.valueOf(runner
								.getAmount()));
					}

					participant.setShirtSize(runner.getShirtSize());
					if (runner.isOwnChip()) {
						participant
								.setChipOwnershipStatus(MSIConstants.CHIP_OWN);
					} else if (runner.isRentChip()) {
						participant
								.setChipOwnershipStatus(MSIConstants.CHIP_RENT);
					} else if (runner.isBuyChip()) {
						participant
								.setChipOwnershipStatus(MSIConstants.CHIP_BUY);
					}
					participant.setChipNumber(runner.getChipNumber());
					participant.setUserNationality(runner.getNationality()
							.getName());
					participant.setTransportOrdered(String.valueOf(runner
							.isTransportOrdered()));
					participant.store();
					participants.add(participant);

					getUserBiz().updateUserHomePhone(user,
							runner.getHomePhone());
					getUserBiz().updateUserMobilePhone(user,
							runner.getMobilePhone());
					getUserBiz().updateUserMail(user, runner.getEmail());

					if (runner.getEmail() != null) {
						IWResourceBundle iwrb = getIWApplicationContext()
								.getIWMainApplication().getBundle(
										MSIConstants.IW_BUNDLE_IDENTIFIER)
								.getResourceBundle(locale);
						String distanceString = iwrb.getLocalizedString(
								distance.getName(), distance.getName());
						if (runner.isTransportOrdered()) {
							distanceString = distanceString
									+ " ("
									+ iwrb.getLocalizedString(
											"run_reg.with_bus_trip",
											"with bus trip") + ")";
						}
						Object[] args = {
								user.getName(),
								iwrb.getLocalizedString(run.getName(), run
										.getName()),
								distanceString,
								iwrb.getLocalizedString("shirt_size."
										+ runner.getShirtSize(), runner
										.getShirtSize()),
								String.valueOf(participant
										.getParticipantNumber()) };
						String subject = iwrb.getLocalizedString(
								"registration_received_subject_mail",
								"Your registration has been received.");
						String body = MessageFormat
								.format(
										iwrb
												.getLocalizedString(
														"registration_received_body_mail",
														"Your registration has been received."),
										args);
						sendMessage(runner.getEmail(), subject, body);
					}
				} catch (CreateException ce) {
					ce.printStackTrace();
				} catch (RemoteException re) {
					throw new IBORuntimeException(re);
				}
			}

			if (email != null) {
				IWResourceBundle iwrb = getIWApplicationContext()
						.getIWMainApplication().getBundle(
								MSIConstants.IW_BUNDLE_IDENTIFIER)
						.getResourceBundle(locale);
				Object[] args = {
						hiddenCardNumber,
						String.valueOf(amount),
						date.getLocaleDateAndTime(locale, IWTimestamp.SHORT,
								IWTimestamp.SHORT) };
				String subject = iwrb.getLocalizedString(
						"receipt_subject_mail",
						"Your receipt for registration on Marathon.is");
				String body = MessageFormat.format(iwrb.getLocalizedString(
						"receipt_body_mail",
						"Your registration has been received."), args);
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

		return participants;
	}

	public void addParticipantsToGroup(String[] participants,
			String[] bestTimes, String[] estimatedTimes, String groupName) {
		try {
			ParticipantHome runHome = (ParticipantHome) getIDOHome(Participant.class);

			for (int i = 0; i < participants.length; i++) {
				try {
					Participant participant = runHome
							.findByPrimaryKey(new Integer(participants[i]));
					participant.setBestTime(bestTimes[i]);
					participant.setGoalTime(estimatedTimes[i]);
					participant.setRunGroupName(groupName);
					participant.store();
				} catch (FinderException fe) {
					fe.printStackTrace();
				}
			}
		} catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	public void finishPayment(String properties)
			throws CreditCardAuthorizationException {
		try {
			CreditCardClient client = getCreditCardBusiness()
					.getCreditCardClient(getCreditCardMerchant());
			client.finishTransaction(properties);
		} catch (CreditCardAuthorizationException ccae) {
			throw ccae;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new CreditCardAuthorizationException(
					"Online payment failed. Unknown error.");
		}
	}

	public String authorizePayment(String nameOnCard, String cardNumber,
			String monthExpires, String yearExpires, String ccVerifyNumber,
			double amount, String currency, String referenceNumber)
			throws CreditCardAuthorizationException {
		try {
			CreditCardClient client = getCreditCardBusiness()
					.getCreditCardClient(getCreditCardMerchant());
			return client.creditcardAuthorization(nameOnCard, cardNumber,
					monthExpires, yearExpires, ccVerifyNumber, amount,
					currency, referenceNumber);
		} catch (CreditCardAuthorizationException ccae) {
			throw ccae;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new CreditCardAuthorizationException(
					"Online payment failed. Unknown error.");
		}
	}

	public float getPriceForRunner(Runner runner, Locale locale,
			float chipDiscount, float chipPrice) {
		if (runner.getUser() != null) {
			int groupID = Integer.parseInt(getIWApplicationContext()
					.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER)
					.getProperty(MSIConstants.PROPERTY_STAFF_GROUP_ID, "-1"));
			if (groupID != -1) {
				try {
					if (getUserBiz().isMemberOfGroup(groupID, runner.getUser())) {
						return 0;
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		float runnerPrice = 0.0f;// runner.getDistance().getPrice(locale);

		if (runner.isOwnChip() || runner.isBuyChip()) {
			runnerPrice = runnerPrice - chipDiscount;
			if (runner.isBuyChip()) {
				runnerPrice += chipPrice;
			}
		}
		return runnerPrice;
	}

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
		String merchantPK = getIWApplicationContext().getIWMainApplication()
				.getBundle(MSIConstants.IW_BUNDLE_IDENTIFIER).getProperty(
						MSIConstants.PROPERTY_MERCHANT_PK);
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

	public void savePayment(int userID, int distanceID, String payMethod,
			String amount) {
		try {
			ParticipantHome runHome = (ParticipantHome) getIDOHome(Participant.class);
			Participant run = runHome.findByUserIDandDistanceID(userID,
					distanceID);
			if (run != null) {
				run.setPayMethod(payMethod);
				run.setPayedAmount(amount);
				run.store();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
	}

	public void savePaymentByUserID(int userID, String payMethod, String amount) {
		try {
			try {
				ParticipantHome runHome = (ParticipantHome) getIDOHome(Participant.class);
				Collection runObjs = runHome.findByUserID(userID);
				if (runObjs != null) {
					Iterator runIt = runObjs.iterator();
					while (runIt.hasNext()) {
						Participant run = (Participant) runIt.next();
						if (run != null) {
							run.setPayMethod(payMethod);
							run.setPayedAmount(amount);
							run.store();
						}
					}

				}
			} catch (IDOStoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (FinderException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public Season getSeasonByGroupId(Integer groupId) {
		try {
			SeasonHome seasonHome = (SeasonHome) getIDOHome(Season.class);
			return seasonHome.findByPrimaryKey(groupId);
		} catch (RemoteException e) {
			log(e);
		} catch (FinderException e) {
			log(e);
		}
		return null;
	}

	public void sendMessage(String email, String subject, String body) {

		boolean sendEmail = true;
		String sSendEmail = this.getIWMainApplication().getBundle(
				IW_BUNDLE_IDENTIFIER).getProperty(
				MSIConstants.PROPERTY_SEND_EMAILS);
		if ("no".equalsIgnoreCase(sSendEmail)) {
			sendEmail = false;
		}

		if (sendEmail) {
			String mailServer = DEFAULT_SMTP_MAILSERVER;
			String fromAddress = DEFAULT_MESSAGEBOX_FROM_ADDRESS;
			String cc = DEFAULT_CC_ADDRESS;
			try {
				IWBundle iwb = getIWApplicationContext().getIWMainApplication()
						.getBundle(IW_BUNDLE_IDENTIFIER);
				mailServer = iwb.getProperty(PROP_SYSTEM_SMTP_MAILSERVER,
						DEFAULT_SMTP_MAILSERVER);
				fromAddress = iwb.getProperty(PROP_MESSAGEBOX_FROM_ADDRESS,
						DEFAULT_MESSAGEBOX_FROM_ADDRESS);
				cc = iwb.getProperty(PROP_CC_ADDRESS, DEFAULT_CC_ADDRESS);
			} catch (Exception e) {
				System.err
						.println("MessageBusinessBean: Error getting mail property from bundle");
				e.printStackTrace();
			}

			cc = "";

			try {
				com.idega.util.SendMail.send(fromAddress, email.trim(), cc, "",
						mailServer, subject, body);
			} catch (javax.mail.MessagingException me) {
				System.err.println("Error sending mail to address: " + email
						+ " Message was: " + me.getMessage());
			}
		}
	}

	public Collection getRuns() {
		Collection runs = null;
		String[] type = { MSIConstants.GROUP_TYPE_RACE };
		try {
			runs = getGroupBiz().getGroups(type, true);
		} catch (Exception e) {
			runs = null;
		}
		return runs;
	}

	public Collection getSeasons() {
		Collection season = null;
		String[] type = { MSIConstants.GROUP_TYPE_SEASON };
		try {
			season = getGroupBiz().getGroups(type, true);
		} catch (Exception e) {
			season = null;
		}
		return season;

	}

	/**
	 * Gets all countries. This method is for example used when displaying a
	 * dropdown menu of all countries
	 * 
	 * @return Colleciton of all countries
	 */
	public Collection getCountries() {
		Collection countries = null;
		try {
			CountryHome countryHome = (CountryHome) getIDOHome(Country.class);
			countries = new ArrayList(countryHome.findAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return countries;
	}

	private GroupBusiness getGroupBiz() throws IBOLookupException {
		GroupBusiness business = (GroupBusiness) IBOLookup.getServiceInstance(
				getIWApplicationContext(), GroupBusiness.class);
		return business;
	}

	private CreditCardBusiness getCreditCardBusiness() {
		try {
			return (CreditCardBusiness) IBOLookup.getServiceInstance(
					getIWApplicationContext(), CreditCardBusiness.class);
		} catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	public UserBusiness getUserBiz() throws IBOLookupException {
		UserBusiness business = (UserBusiness) IBOLookup.getServiceInstance(
				getIWApplicationContext(), UserBusiness.class);
		return business;
	}

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

	public Participant getParticipantByPrimaryKey(int participantID) {
		Participant participant = null;
		try {
			ParticipantHome runHome = (ParticipantHome) getIDOHome(Participant.class);
			participant = runHome.findByPrimaryKey(new Integer(participantID));
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (FinderException e1) {
			e1.printStackTrace();
		}
		return participant;
	}
}