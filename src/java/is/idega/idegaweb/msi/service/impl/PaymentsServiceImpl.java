package is.idega.idegaweb.msi.service.impl;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.creditcard.business.CreditCardClient;
import com.idega.core.business.DefaultSpringBean;
import com.idega.core.contact.data.Email;
import com.idega.presentation.IWContext;
import com.idega.user.data.User;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.EmailValidator;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.LocaleUtil;
import com.idega.util.SendMail;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

import is.idega.idegaweb.msi.business.RaceBusiness;
import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.presentation.ParticipantPayment;
import is.idega.idegaweb.msi.service.PaymentsService;

@Service(PaymentsServiceImpl.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@RemoteProxy(creator=SpringCreator.class, creatorParams={
		@Param(name="beanName", value=PaymentsServiceImpl.BEAN_NAME),
		@Param(name="javascript", value=PaymentsServiceImpl.DWR_OBJECT)
	}, name=PaymentsServiceImpl.DWR_OBJECT)
public class PaymentsServiceImpl extends DefaultSpringBean implements PaymentsService {

	static final String BEAN_NAME = "msiPaymentsService",
						DWR_OBJECT = "MSIPaymentsService";

	@Override
	@RemoteMethod
	public boolean refundCard(String cardnumber, String monthExpires, String yearExpires, String ccVerifyNumber, double amount, String parentDataPK, String extraField) {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc != null && iwc.isLoggedOn() && iwc.isSuperAdmin()) {
			return refund(cardnumber, monthExpires, yearExpires, ccVerifyNumber, amount, "ISK", parentDataPK, extraField);
		}
		return false;
	}

	@Override
	public boolean refund(String cardnumber, String monthExpires, String yearExpires, String ccVerifyNumber, double amount, String currency, String parentDataPK, String extraField) {
		try {
			RaceBusiness raceBusiness = getServiceInstance(RaceBusiness.class);
			CreditCardClient creditCardClient = raceBusiness.getCreditCardClient();
			String refund = creditCardClient.doRefund(cardnumber, monthExpires, yearExpires, ccVerifyNumber, amount, currency, parentDataPK, extraField);
			return !StringUtil.isEmpty(refund);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error refunding " + cardnumber, e);
		}
		return false;
	}

	@Override
	@RemoteMethod
	public boolean sendEmailsAboutPayments(String participantId, String emailTo, String link, String dateFrom, String dateTo) {
		IWContext iwc = CoreUtil.getIWContext();
		if ((iwc == null || !iwc.isLoggedOn()) && !iwc.isSuperAdmin()) {
			return false;
		}

		try {
			RaceBusiness raceBusiness = getServiceInstance(RaceBusiness.class);
			Collection<Participant> participants = raceBusiness.getParticipantHome().findByDates(dateFrom, dateTo);
			if (ListUtil.isEmpty(participants)) {
				return false;
			}

			Map<User, List<Participant>> groupedParticipants = new HashMap<>();
			for (Participant participant: participants) {
				if (participant == null || !StringUtil.isEmpty(participant.getPaymentAuthCode())) {
					continue;
				}

				if (participantId != null && !participantId.equals(participant.getPrimaryKey().toString())) {
					getLogger().info("Skipping " + participant);
					continue;
				}

				User user = participant.getUser();
				if (user == null) {
					getLogger().warning("User unknown for " + participant);
					continue;
				}

				List<Participant> userParticiations = groupedParticipants.get(user);
				if (userParticiations == null) {
					userParticiations = new ArrayList<>();
					groupedParticipants.put(user, userParticiations);
				}
				userParticiations.add(participant);
			}

			Locale icelandic = LocaleUtil.getIcelandicLocale();

//			IWTimestamp now = IWTimestamp.RightNow();
//			IWTimestamp eighteeneYearAgo = new IWTimestamp();
//			eighteeneYearAgo.setYear(now.getYear() - 18);
//			UserBusiness userBusiness = getServiceInstance(UserBusiness.class);
//			for (User user: groupedParticipants.keySet()) {
//				IWTimestamp dateOfBirth = new IWTimestamp(userBusiness.getUserDateOfBirthFromPersonalId(user.getPersonalID()));
//				if (eighteeneYearAgo.isEarlierThan(dateOfBirth)) {
//					System.out.println(user.getName() + " " + user.getPersonalID() + " " + dateOfBirth);
//				}
//			}

			String subject = "Mistök í kortagreiðslum til MSÍ: {0}";
			String text = "Sæll {0},\n\nÍ sumar tók MSÍ í notkun nýtt tölvukerfi fyrir m.a. skráningu í mót og greiðslu mótsgjalda (www.msisport.is). Kerfið hefur líka verið notað til að greiða fyrir miða á árshátíð MSÍ. Vegna tæknilegra mistaka var um tíma tekið við skráningu og greiðslu án þess að greiðslan væri tekin af kreditkorti viðkomandi. Þú varst með slíka skráningu sem er í raun ógreidd. Færslan/færslurnar sem um ræðir eru:\n\n{1}\nVinsamlega smelltu á hlekkinn hér að framan til að ganga frá greiðslu.\n\nVið biðjumst velvirðingar á þessum óþægindum. Ef þú óskar eftir nánari útskýringum þá er þér velkomið að hringja í okkur í síma 554 7557.\n\nKveðja,\n\nGunnar Páll Þórisson\nIOS hugbúnaður ehf.\nwww.idega.is / sími 554 7557";
			EmailValidator validator = EmailValidator.getInstance();
			for (User user: groupedParticipants.keySet()) {
				try {
					Email email = null;
					try {
						email = user.getUsersEmail();
					} catch (Exception e) {}
					String emailAddress = email == null ? null : email.getEmailAddress();
					if (!StringUtil.isEmpty(emailAddress)) {
						emailAddress = StringHandler.replace(emailAddress, CoreConstants.SEMICOLON, CoreConstants.COMMA);
					}
					String[] emails = emailAddress == null ? null : emailAddress.split(CoreConstants.COMMA);
					if (ArrayUtil.isEmpty(emails)) {
						getLogger().warning(user + " (personal ID: " + user.getPersonalID() + ") does not have email");
						continue;
					}
					StringBuilder receiverMails = new StringBuilder();
					for (String userEmail: emails) {
						if (validator.isValid(emailAddress)) {
							receiverMails.append(userEmail).append(CoreConstants.COMMA);
						} else {
							getLogger().warning(user + " (personal ID: " + user.getPersonalID() + ") has invalid email: '" + userEmail + "'");
						}
					}
					String to = receiverMails.toString();
					if (to.endsWith(CoreConstants.COMMA)) {
						to = to.substring(0, to.length() - 1);
					}
					if (StringUtil.isEmpty(to)) {
						getLogger().warning(user + " (personal ID: " + user.getPersonalID() + ") does not have email");
						continue;
					}

					int index = 1;
					StringBuilder records = new StringBuilder();
					List<String> dates = new ArrayList<>();
					List<Participant> userParticiations = groupedParticipants.get(user);
					for (Iterator<Participant> participationsIter = userParticiations.iterator(); participationsIter.hasNext();) {
						Participant participant = participationsIter.next();
						String tournament = participant.getRaceGroup().getName();
						String group = participant.getRaceEvent().getName();
						IWTimestamp date = new IWTimestamp(participant.getCreatedDate());
						String localizedDate = date.getLocaleDateAndTime(icelandic, DateFormat.MEDIUM, DateFormat.SHORT);
						dates.add(localizedDate);

						records.append(index).append(". ").append(tournament).append(", ").append(group).append(" ")
						.append(localizedDate).append(" ")
						.append(link).append("?").append(ParticipantPayment.PARAMETER_PARITICIPANT_ID).append("=")
						.append(participant.getPrimaryKey().toString()).append("\n");
						index++;
					}

					StringBuilder subjectDates = new StringBuilder();
					for (Iterator<String> datesIter = dates.iterator(); datesIter.hasNext();) {
						subjectDates.append(datesIter.next());
						if (datesIter.hasNext()) {
							subjectDates.append(", ");
						}
					}
					Object[] subjectParams = new Object[] {subjectDates.toString()};

					Object[] textParams = new Object[] {
						user.getName(),
						records.toString()
					};

					getLogger().info("Sending email to " + to);
					SendMail.send(
							"gunnar@idega.is",
							StringUtil.isEmpty(emailTo) ? to : emailTo,
							null,
							"valdas@idega.com",
							"gunnar@idega.is",
							null,
							MessageFormat.format(subject, subjectParams),
							MessageFormat.format(text, textParams),
							null,
							null,
							false,
							false
					);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Error sending email about payment to participant " + user, e);
				}
			}
			return true;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error sending emails about payments from " + dateFrom + " to " + dateTo, e);
		}
		return false;
	}

}