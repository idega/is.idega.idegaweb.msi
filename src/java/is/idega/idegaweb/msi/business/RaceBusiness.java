package is.idega.idegaweb.msi.business;


import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.ParticipantHome;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceNumberHome;
import is.idega.idegaweb.msi.data.RaceTypeHome;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.data.RaceUserSettingsHome;
import is.idega.idegaweb.msi.data.RaceVehicleTypeHome;
import is.idega.idegaweb.msi.data.Season;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.block.creditcard.business.CreditCardAuthorizationException;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOService;
import com.idega.core.location.data.Country;
import com.idega.data.IDOCreateException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

public interface RaceBusiness extends IBOService {
	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#createRace
	 */
	public Race createRace(String seasonID, String raceName, String raceDate,
			String lastRegistration, String lastRegistrationPrice1,
			String type, String category) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#updateRace
	 */
	public void updateRace(Race race, String raceDate, String lastRegistration,
			String lastRegistrationPrice1, String type, String category)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#updateRaceNumber
	 */
	public void updateRaceNumber(RaceNumber raceNumber, String userSSN)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#addEventsToRace
	 */
	public boolean addEventsToRace(Race race, String[] events, Map price,
			Map price2, Map teamCount) throws IBOLookupException,
			RemoteException, FinderException, RemoteException;

	public boolean addEventsToRace(Race race, String events[], Map price,
			Map price2, Map teamCount,Map timeTransmitterPrices) throws IBOLookupException, RemoteException,
			FinderException;
	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getEventsForRace
	 */
	public Map getEventsForRace(Race race) throws FinderException,
			IBOLookupException, RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#isRegisteredInRun
	 */
	public boolean isRegisteredInRun(int runID, int userID)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getEvents
	 */
	public Collection getEvents() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getRaceTypes
	 */
	public Collection getRaceTypes() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getRaceCategories
	 */
	public Collection getRaceCategories() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#createEvent
	 */
	public boolean createEvent(String name) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#isRegisteredInRun
	 */
	public boolean isRegisteredInRun(int runID, String personalID)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#saveParticipant
	 */
	public Participant saveParticipant(RaceParticipantInfo raceParticipantInfo,
			String email, String hiddenCardNumber, double amount,
			IWTimestamp date, Locale locale) throws IDOCreateException,
			RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#finishPayment
	 */
	public void finishPayment(String properties)
			throws CreditCardAuthorizationException, RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#authorizePayment
	 */
	public String authorizePayment(String nameOnCard, String cardNumber,
			String monthExpires, String yearExpires, String ccVerifyNumber,
			double amount, String currency, String referenceNumber)
			throws CreditCardAuthorizationException, RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getPriceForRunner
	 */
	public float getPriceForRunner(RaceParticipantInfo raceParticipantInfo)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getCreditCardImages
	 */
	public Collection getCreditCardImages() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getAvailableCardTypes
	 */
	public DropdownMenu getAvailableCardTypes(IWResourceBundle iwrb)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getSeasonByGroupId
	 */
	public Season getSeasonByGroupId(Integer groupId) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#sendMessage
	 */
	public void sendMessage(String email, String subject, String body)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getRuns
	 */
	public Collection getRuns() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getSeasons
	 */
	public Collection getSeasons() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getMXRaceNumbers
	 */
	public Collection getMXRaceNumbers() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getSnocrossRaceNumbers
	 */
	public Collection getSnocrossRaceNumbers() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getCountries
	 */
	public Collection getCountries() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getUserBiz
	 */
	public UserBusiness getUserBiz() throws IBOLookupException, RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getCountryByNationality
	 */
	public Country getCountryByNationality(Object nationality)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getParticipantByPrimaryKey
	 */
	public Participant getParticipantByPrimaryKey(int participantID)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getParticipantHome
	 */
	public ParticipantHome getParticipantHome() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getRaceUserSettingsHome
	 */
	public RaceUserSettingsHome getRaceUserSettingsHome()
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getRaceTypeHome
	 */
	public RaceTypeHome getRaceTypeHome() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getRaceNumberHome
	 */
	public RaceNumberHome getRaceNumberHome() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getRaceVehicleTypeHome
	 */
	public RaceVehicleTypeHome getRaceVehicleTypeHome() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getRaceUserSettings
	 */
	public RaceUserSettings getRaceUserSettings(User user)
			throws RemoteException;
}