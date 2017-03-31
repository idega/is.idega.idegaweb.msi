package is.idega.idegaweb.msi.business;


import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.block.creditcard.business.CreditCardAuthorizationException;
import com.idega.block.creditcard.business.CreditCardClient;
import com.idega.builder.bean.AdvancedProperty;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOService;
import com.idega.core.location.data.Country;
import com.idega.data.IDOCreateException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

import is.idega.idegaweb.msi.bean.TimeTransmitterRentProperties;
import is.idega.idegaweb.msi.data.Event;
import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.ParticipantHome;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.RaceCategory;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceNumberHome;
import is.idega.idegaweb.msi.data.RaceType;
import is.idega.idegaweb.msi.data.RaceTypeHome;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.data.RaceUserSettingsHome;
import is.idega.idegaweb.msi.data.RaceVehicleTypeHome;
import is.idega.idegaweb.msi.data.Season;

public interface RaceBusiness extends IBOService {

	Race createRace(
			String seasonID,
			String raceName,
			String raceDate,
			String lastRegistration,
			String lastRegistrationPrice1,
			String type,
			String category);

	void updateRace(
			Race race,
			String raceDate,
			String lastRegistration,
			String lastRegistrationPrice1,
			String type,
			String category);

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#updateRaceNumber
	 */
	public void updateRaceNumber(RaceNumber raceNumber, String userSSN)
			throws RemoteException;

	boolean addEventsToRace(
			Race race,
			String[] events,
			Map<String, String> price,
			Map<String, String> price2,
			Map<String, String> teamCount);

	boolean addEventsToRace(
			Race race,
			String events[],
			Map<String, String> price,
			Map<String, String> price2,
			Map<String, String> teamCount,
			Map<String, TimeTransmitterRentProperties> timeTransmitterPrices);
	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getEventsForRace
	 */
	public Map<String, RaceEvent> getEventsForRace(Race race) throws FinderException,
			IBOLookupException, RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#isRegisteredInRun
	 */
	public boolean isRegisteredInRun(int runID, int userID)
			throws RemoteException;

	Collection<Event> getEvents();

	Collection<RaceType> getRaceTypes();

	Collection<RaceCategory> getRaceCategories();

	boolean createEvent(String name);

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#isRegisteredInRun
	 */
	public boolean isRegisteredInRun(int runID, String personalID)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#saveParticipant
	 */
	public Participant saveParticipant(
			RaceParticipantInfo raceParticipantInfo,
			String email,
			String hiddenCardNumber,
			double amount,
			IWTimestamp date,
			Locale locale,
			String paymentAuthCode
	) throws IDOCreateException, RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#finishPayment
	 */
	public String finishPayment(String properties) throws CreditCardAuthorizationException, RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#authorizePayment
	 */
	public String authorizePayment(String nameOnCard, String cardNumber,
			String monthExpires, String yearExpires, String ccVerifyNumber,
			double amount, String currency, String referenceNumber)
			throws CreditCardAuthorizationException, RemoteException;

	public float getEventPriceForRunner(RaceParticipantInfo raceParticipantInfo);
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

	Collection<Group> getRuns();
	Collection<Group> getSeasons();
	public Collection<RaceNumber> getMXRaceNumbers();
	public Collection<RaceNumber> getSnocrossRaceNumbers();

	Collection<Country> getCountries();

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

	ParticipantHome getParticipantHome();

	RaceUserSettingsHome getRaceUserSettingsHome();

	RaceTypeHome getRaceTypeHome();

	RaceNumberHome getRaceNumberHome();

	RaceVehicleTypeHome getRaceVehicleTypeHome();

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getRaceUserSettings
	 */
	public RaceUserSettings getRaceUserSettings(User user)
			throws RemoteException;

	public List enableEvents(List ids);

	public List disableEvents(List ids);

	public Float getTransmitterPrices(String raceId, String eventId);
	public AdvancedProperty getTransmitterPricesAndTeamInfo(String raceId, String eventId);

	public CreditCardClient getCreditCardClient() throws Exception;

}