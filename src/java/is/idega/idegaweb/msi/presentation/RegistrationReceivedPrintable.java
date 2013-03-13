/*
 * Created on Aug 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.text.NumberFormat;
import java.util.Locale;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.PrintButton;
import com.idega.presentation.ui.Window;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;
import com.idega.util.LocaleUtil;


/**
 * @author birna
 *
 */
public class RegistrationReceivedPrintable extends Window {
	
	public RegistrationReceivedPrintable() {
		setResizable(true);
		setHeight(450);
		setWidth(650);
	}

	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		setTitle(iwrb.getLocalizedString("run_reg.receipt", "Receipt"));

		Table t = new Table(1, 2);
		t.setStyleAttribute("border-style:solid;border-color:#e1e1e1;border-width:1px;");
		t.setHeight(Table.HUNDRED_PERCENT);
		t.setVerticalAlignment(1, 1, Table.VERTICAL_ALIGN_TOP);
		t.setVerticalAlignment(1, 2, Table.VERTICAL_ALIGN_BOTTOM);

		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		int row = 1;
		
		Participant participant = (Participant) iwc.getSessionAttribute(Registration.SESSION_ATTRIBUTE_PARTICIPANT);
		double amount = ((Double) iwc.getSessionAttribute(Registration.SESSION_ATTRIBUTE_AMOUNT)).doubleValue();
		String cardNumber = (String) iwc.getSessionAttribute(Registration.SESSION_ATTRIBUTE_CARD_NUMBER);
		IWTimestamp stamp = (IWTimestamp) iwc.getSessionAttribute(Registration.SESSION_ATTRIBUTE_PAYMENT_DATE);

		table.add(getHeader(iwrb.getLocalizedString("run_reg.hello_participant", "Hello participant(s)")), 1, row++);
		table.setHeight(row++, 16);

		table.add(getText(iwrb.getLocalizedString("run_reg.payment received", "We have received payment for the following:")), 1, row++);
		table.setHeight(row++, 8);

		Table runnerTable = new Table(3, 4);
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.runner_name", "Runner name")), 1, 1);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.race", "Race")), 2, 1);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.event", "Event")), 3, 1);
		table.add(runnerTable, 1, row++);
		int runRow = 2;
		Group race = participant.getRaceGroup();
		RaceEvent event = participant.getRaceEvent();
		
		runnerTable.add(getText(participant.getUser().getName()), 1, runRow);
		runnerTable.add(getText(race.getName()), 2, runRow);
		runnerTable.add(getText(event.getEventID()), 3, runRow++);
		
		Table creditCardTable = new Table(2, 3);
		creditCardTable.add(getHeader(iwrb.getLocalizedString("run_reg.payment_received_timestamp", "Payment received") + ":"), 1, 1);
		creditCardTable.add(getText(stamp.getLocaleDateAndTime(iwc.getCurrentLocale(), IWTimestamp.SHORT, IWTimestamp.SHORT)), 2, 1);
		creditCardTable.add(getHeader(iwrb.getLocalizedString("run_reg.card_number", "Card number") + ":"), 1, 2);
		creditCardTable.add(getText(cardNumber), 2, 2);
		creditCardTable.add(getHeader(iwrb.getLocalizedString("run_reg.amount", "Amount") + ":"), 1, 3);
		creditCardTable.add(getText(formatAmount(iwc.getCurrentLocale(), (float) amount)), 2, 3);
		table.setHeight(row++, 16);
		table.add(creditCardTable, 1, row++);
		
		table.setHeight(row++, 16);
		table.add(getHeader(iwrb.getLocalizedString("run_reg.receipt_info_headline", "Receipt - Please Print It Out")), 1, row++);
		table.add(getText(iwrb.getLocalizedString("run_reg.receipt_info_headline_body", "This document is your receipt, please print this out.")), 1, row++);

		table.setHeight(row++, 16);
		table.add(getText(iwrb.getLocalizedString("run_reg.best_regards", "Best regards,")), 1, row++);
		table.add(getText(iwrb.getLocalizedString("run_reg.msi", "MSI")), 1, row++);
		table.add(getText("www.msisport.is"), 1, row++);
		
		table.setHeight(row++, 16);
		t.add(table, 1, 1);
		
		PrintButton print = new PrintButton(iwrb.getLocalizedString("print", "Print"));
		t.add(print, 1, 2);
		
		add(t);
	}
	
	private String formatAmount(Locale locale, float amount) {
		return NumberFormat.getInstance(locale).format(amount) + " " + (locale.equals(LocaleUtil.getIcelandicLocale()) ? "ISK" : "EUR");
	}
	
	private Text getHeader(String s) {
		Text text = new Text(s);
		text.setBold(true);
		
		return text;
	}
	
	private Text getText(String s) {
		return new Text(s);
	}
	
	public String getBundleIdentifier() {
		return MSIConstants.IW_BUNDLE_IDENTIFIER;
	}
}