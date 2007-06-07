package is.idega.idegaweb.msi.business;


import is.idega.idegaweb.msi.data.Race;
import com.idega.block.creditcard.business.CreditCardAuthorizationException;
import com.idega.user.data.User;
import java.rmi.RemoteException;
import com.idega.data.IDOCreateException;
import com.idega.user.data.Group;
import java.util.Locale;
import is.idega.idegaweb.msi.data.Participant;
import java.util.Collection;
import com.idega.util.IWTimestamp;
import com.idega.business.IBOService;
import is.idega.idegaweb.msi.data.Season;
import com.idega.user.business.UserBusiness;
import com.idega.business.IBOLookupException;
import com.idega.core.location.data.Country;

public interface RaceBusiness extends IBOService {
	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#createRace
	 */
	public Race createRace(String seasonID, String raceName, String raceDate,
			String lastRegistration, String price, String chipRent)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#updateRace
	 */
	public void updateRace(Race race, String raceDate, String lastRegistration,
			String price, String chipRent) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#addEventsToRace
	 */
	public boolean addEventsToRace(Race race, String[] events)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#isRegisteredInRun
	 */
	public boolean isRegisteredInRun(int runID, int userID)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#isRegisteredInRun
	 */
	public boolean isRegisteredInRun(String year, Group run, User user)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#doesGroupExist
	 */
	public boolean doesGroupExist(Object distancePK, String groupName)
			throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getEvents
	 */
	public Collection getEvents() throws RemoteException;

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
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#saveRun
	 */
	public void saveRun(int userID, String run, String distance, String year,
			String nationality, String tshirt, String chipOwnershipStatus,
			String chipNumber, String groupName, String bestTime,
			String goalTime, Locale locale) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#saveParticipants
	 */
	public Collection saveParticipants(Collection runners, String email,
			String hiddenCardNumber, double amount, IWTimestamp date,
			Locale locale) throws IDOCreateException, RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#addParticipantsToGroup
	 */
	public void addParticipantsToGroup(String[] participants,
			String[] bestTimes, String[] estimatedTimes, String groupName)
			throws RemoteException;

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
	public float getPriceForRunner(Runner runner, Locale locale,
			float chipDiscount, float chipPrice) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#getCreditCardImages
	 */
	public Collection getCreditCardImages() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#savePayment
	 */
	public void savePayment(int userID, int distanceID, String payMethod,
			String amount) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.msi.business.RaceBusinessBean#savePaymentByUserID
	 */
	public void savePaymentByUserID(int userID, String payMethod, String amount)
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
}