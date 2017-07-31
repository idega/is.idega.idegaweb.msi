package is.idega.idegaweb.msi.service;

import is.idega.idegaweb.msi.business.PaymentInfo;

import java.util.List;

public interface PaymentsService {

	public boolean refundCard(String cardnumber, String monthExpires, String yearExpires, String ccVerifyNumber, double amount, String parentDataPK, String extraField);

	public boolean refund(String cardnumber, String monthExpires, String yearExpires, String ccVerifyNumber, double amount, String currency, String parentDataPK, String extraField);

	public boolean sendEmailsAboutPayments(String participantId, String emailTo, String link, String from, String to, boolean reminder);

	public List<PaymentInfo> getUnpaidEntries(String userId, String dateFrom, String dateTo);
	
}