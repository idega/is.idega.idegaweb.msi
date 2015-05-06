/*
 * $Id: RaceReportWriter.java,v 1.3 2008/05/21 09:04:17 palli Exp $ Created on Jan 25, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to license terms.
 */
package is.idega.idegaweb.msi.business;

import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.presentation.RaceBlock;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.DownloadWriter;
import com.idega.io.MediaWritable;
import com.idega.io.MemoryFileBuffer;
import com.idega.io.MemoryInputStream;
import com.idega.io.MemoryOutputStream;
import com.idega.presentation.IWContext;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.StringHandler;

public class RaceReportWriter extends DownloadWriter implements MediaWritable {

	public static final String PARAMETER_RACE_ID = "prm_race_id";
	public static final String PARAMETER_SEASON_ID = "prm_season_id";

	private MemoryFileBuffer buffer = null;

	private Locale locale;

	private IWResourceBundle iwrb;

	public String getMimeType() {
		if (this.buffer != null) {
			return this.buffer.getMimeType();
		} else {
			return super.getMimeType();
		}
	}

	public void init(HttpServletRequest req, IWContext iwc) {
		try {
			this.locale = iwc.getApplicationSettings().getApplicationLocale();
			this.iwrb = iwc.getIWMainApplication()
					.getBundle(RaceBlock.IW_BUNDLE_IDENTIFIER)
					.getResourceBundle(this.locale);

			if (req.getParameter(PARAMETER_RACE_ID) != null
					|| req.getParameter(PARAMETER_SEASON_ID) != null) {
				String raceID = req.getParameter(PARAMETER_RACE_ID);
				int groupID = -1;
				String season = null;
				Collection raceParticipants = null;
				boolean allRaces = false;

				if (raceID != null) {
					groupID = Integer.parseInt(raceID);
					raceParticipants = this
							.getRaceBusiness(iwc)
							.getParticipantHome()
							.findAllByRace(
									ConverterUtility.getInstance()
											.convertRaceToGroup(
													new Integer(groupID)));
				} else {
					season = req.getParameter(PARAMETER_SEASON_ID);
					String[] types = { MSIConstants.GROUP_TYPE_RACE };
					Collection races = ConverterUtility.getInstance()
							.convertSeasonToGroup(getRaceBusiness(iwc).getSeasonByGroupId(Integer.valueOf(season)))
							.getChildGroups(types, true);
					Iterator it = races.iterator();
					raceParticipants = new ArrayList();
					while (it.hasNext()) {
						Race race = ConverterUtility.getInstance()
								.convertGroupToRace((Group) it.next());
						raceParticipants
								.addAll(this
										.getRaceBusiness(iwc)
										.getParticipantHome()
										.findAllByRace(
												ConverterUtility
														.getInstance()
														.convertRaceToGroup(
																(Integer) (race
																		.getPrimaryKey()))));
					}
					allRaces = true;
				}

				if (allRaces) {
					this.buffer = writeAllRaceXLS(raceParticipants, Integer.valueOf(season), iwc);
					setAsDownload(iwc, "race_report.xls",
							this.buffer.length());

				} else {
					if (raceParticipants != null && !raceParticipants.isEmpty()) {
						Map raceEvents = new HashMap();
						Iterator it = raceParticipants.iterator();
						while (it.hasNext()) {
							Participant info = (Participant) it.next();
							List eventParticipants = null;
							if (raceEvents.containsKey(info.getRaceEvent()
									.getPrimaryKey())) {
								eventParticipants = (List) raceEvents.get(info
										.getRaceEvent().getPrimaryKey());
							} else {
								eventParticipants = new ArrayList();
							}

							eventParticipants.add(info);
							raceEvents.put(info.getRaceEvent().getPrimaryKey(),
									eventParticipants);
						}

						this.buffer = writeXLS(raceEvents, iwc);
						setAsDownload(iwc, "race_report.xls",
								this.buffer.length());
					}else{
						this.buffer = writeXLSNothingFound();
						setAsDownload(iwc, "race_report.xls",
								this.buffer.length());
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MemoryFileBuffer writeAllRaceXLS(Collection participants, Integer year,
			IWContext iwc) throws Exception {
		MemoryFileBuffer buffer = new MemoryFileBuffer();
		MemoryOutputStream mos = new MemoryOutputStream(buffer);
		if (!participants.isEmpty()) {
			HSSFWorkbook wb = new HSSFWorkbook();

//			Styles
			HSSFCellStyle headerStyle = getHeaderStyle(wb);
			HSSFCellStyle timeTransmitterStyle = getTimeTransmitterStyle(wb);
			HSSFCellStyle normalStyle = getNormalStyle(wb);
			
			HSSFSheet sheet = wb.createSheet(year.toString());
			int cellRow = 0;
			int column = 0;
			HSSFRow row = sheet.createRow(cellRow++);
			
			HSSFCell cell = row.createCell(column++);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(this.iwrb.getLocalizedString(
					"race_report.race", "Race"));
			cell = row.createCell(column++);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(this.iwrb.getLocalizedString(
					"race_report.race_date", "Race date"));

			column = createHeaderRow(row, headerStyle,column);
			

			String yesString = iwrb.getLocalizedString("yes", "Yes");
			String noString = iwrb.getLocalizedString("no", "No");
			Iterator iter = participants.iterator();
			while (iter.hasNext()) {
				column = 0;
				Participant info = (Participant) iter.next();
				row = sheet.createRow(cellRow++);
				RaceEvent raceEvent = info.getRaceEvent();
				Race race = ConverterUtility.getInstance().convertGroupToRace(info.getRaceGroup());
				boolean hasTimeTransmitter = info.isRentsTimeTransmitter();
				
				cell = row.createCell(column++);
				cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(race.getName() == null ? "" : race.getName());

				cell = row.createCell(column++);
				cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(race.getRaceDate() == null ? "" :new IWTimestamp(race.getRaceDate())
				.getDateString("dd.MM.yyyy"));
				
				column = createParticipantRow(row, timeTransmitterStyle, normalStyle, info, iwc, yesString, noString, raceEvent,column);
			}
			autosizeColumns(sheet,column);

			wb.write(mos);
		}
		buffer.setMimeType("application/x-msexcel");
		return buffer;
	}

	private HSSFCellStyle getHeaderStyle(HSSFWorkbook wb){
		HSSFFont headerFont = wb.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setFontHeightInPoints((short) 12);
		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFont(headerFont);
		return headerStyle;
	}
	private HSSFCellStyle getTimeTransmitterStyle(HSSFWorkbook wb){
//		HSSFFont timeTransmitterFont = wb.createFont();
//		timeTransmitterFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//		timeTransmitterFont.setColor(new HSSFColor.GREEN().getIndex());
//		timeTransmitterFont.setFontHeightInPoints((short) 12);
//		HSSFCellStyle timeTransmitterStyle = wb.createCellStyle();
//		timeTransmitterStyle.setFont(timeTransmitterFont);
//		return timeTransmitterStyle;
		return getNormalStyle(wb);
	}
	private HSSFCellStyle getNormalStyle(HSSFWorkbook wb){
		HSSFFont normalFont = wb.createFont();
		normalFont.setFontHeightInPoints((short) 12);
		HSSFCellStyle normalStyle = wb.createCellStyle();
		normalStyle.setFont(normalFont);
		return normalStyle;
	}
	private int createHeaderRow(HSSFRow row,HSSFCellStyle headerStyle,int column){
		
		HSSFCell cell = row.createCell( column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.race_number", "Race number"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.first_name", "First name"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.last_name", "Last name"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.personal_id", "Personal ID"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.race_vehicle", "Race vehicle"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.transmitter_number", "Transmitter number"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.race_class", "Class"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.car_registration", "Car registration"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.transponder1", "Transponder1"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.transponder3", "Time transmitter"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.nationality", "Nationality"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.engine", "Engine"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.type", "Type"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.cc", "CC"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.sub_type", "Sub type"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.club", "Club"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.sponsor", "Sponsor"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.team", "Team"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.comment", "Comment"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.partner1", "Partner 1"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.partner2", "Partner 2"));
		cell = row.createCell(column++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(this.iwrb.getLocalizedString(
				"race_report.created", "Date of entry"));
		return column;
	}
	private int createParticipantRow(HSSFRow row,HSSFCellStyle timeTransmitterStyle,HSSFCellStyle normalStyle,Participant info,IWContext iwc,String yesString,String noString,RaceEvent raceEvent,int column) throws RemoteException{

		boolean hasTimeTransmitter = info.isRentsTimeTransmitter();
		
		User racer = info.getUser();
		RaceUserSettings settings = this.getRaceBusiness(iwc).getRaceUserSettings(
				racer);

		HSSFCell cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(info.getRaceNumber() == null ? "" : info
				.getRaceNumber());

		StringBuffer first = new StringBuffer(racer.getFirstName());
		if (racer.getMiddleName() != null) {
			first.append(" ");
			first.append(racer.getMiddleName());
		}

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(first.toString() == null ? "" : first
				.toString());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(racer.getLastName() == null ? "" : racer
				.getLastName());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(racer.getPersonalID() == null ? ""
				: racer.getPersonalID());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(info.getRaceVehicle() == null ? "" : info
				.getRaceVehicle().getLocalizationKey());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(info.getChipNumber() == null ? "" : info
				.getChipNumber());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(raceEvent.getName() == null ? ""
				: raceEvent.getName());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(settings == null
				|| settings.getBodyNumber() == null ? "" : settings
				.getBodyNumber());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(settings == null
				|| settings.getTransponderNumber() == null ? ""
				: settings.getTransponderNumber());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(info.isRentsTimeTransmitter() ? yesString : noString);
		

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue("ISL");

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(settings == null
				|| settings.getEngine() == null ? "" : settings
				.getEngine());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(settings == null
				|| settings.getVehicleType() == null ? ""
				: settings.getVehicleType().getLocalizationKey());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(settings == null
				|| settings.getEngineCC() == null ? "" : settings
				.getEngineCC());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(settings == null
				|| settings.getVehicleSubType() == null ? ""
				: settings.getVehicleSubType().getLocalizationKey());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue("");

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(info.getSponsors() == null ? "" : info
				.getSponsors());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(settings == null
				|| settings.getTeam() == null ? "" : settings
				.getTeam());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(info == null || info.getComment() == null ? ""
				: info.getComment());
		
		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(info == null || info.getPartner1() == null ? ""
				: info.getPartner1());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(info == null || info.getPartner2() == null ? ""
				: info.getPartner2());

		cell = row.createCell(column++);
		cell.setCellStyle(hasTimeTransmitter ? timeTransmitterStyle : normalStyle);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(info == null
				|| info.getCreatedDate() == null ? ""
				: new IWTimestamp(info.getCreatedDate())
						.getDateString("dd.MM.yyyy hh:mm:ss"));
		return column;

	}
	private void autosizeColumns(HSSFSheet sheet,int columns){
		for (int i = 0;i < columns;i++) {
			sheet.autoSizeColumn(i);
		}
	}
	public MemoryFileBuffer writeXLS(Map raceEvents, IWContext iwc)
			throws Exception {
		MemoryFileBuffer buffer = new MemoryFileBuffer();
		MemoryOutputStream mos = new MemoryOutputStream(buffer);
		if (!raceEvents.isEmpty()) {
			HSSFWorkbook wb = new HSSFWorkbook();
//			Styles
			HSSFCellStyle headerStyle = getHeaderStyle(wb);
			HSSFCellStyle timeTransmitterStyle = getTimeTransmitterStyle(wb);
			HSSFCellStyle normalStyle = getNormalStyle(wb);

			String yesString = iwrb.getLocalizedString("yes", "Yes");
			String noString = iwrb.getLocalizedString("no", "No");
			Iterator it = raceEvents.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				List eventParticipant = (List) raceEvents.get(key);
				RaceEvent raceEvent = ConverterUtility.getInstance()
						.convertGroupToRaceEvent(key);

				HSSFSheet sheet = wb.createSheet(StringHandler.shortenToLength(StringHandler.stripNonRomanCharacters(raceEvent.getName(), new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '-'}), 30));
				int cellRow = 0;
				int columns = 0;
				HSSFRow row = sheet.createRow(cellRow++);
				createHeaderRow(row, headerStyle,columns);
				Iterator iter = eventParticipant.iterator();
				while (iter.hasNext()) {
					Participant info = (Participant) iter.next();
					row = sheet.createRow(cellRow++);
					columns = createParticipantRow(row, timeTransmitterStyle, normalStyle, info, iwc, yesString, noString, raceEvent,0);

				}
				autosizeColumns(sheet,columns);
			}

			wb.write(mos);
		}
		buffer.setMimeType("application/x-msexcel");
		return buffer;
	}
	
	public MemoryFileBuffer writeXLSNothingFound()
	throws Exception {
		MemoryFileBuffer buffer = new MemoryFileBuffer();
		MemoryOutputStream mos = new MemoryOutputStream(buffer);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(this.iwrb.getLocalizedString("race_report.nothing_found", "Nothing found"));
		wb.write(mos);
		buffer.setMimeType("application/x-msexcel");
		return buffer;
	}

	public void writeTo(OutputStream out) throws IOException {
		if (this.buffer != null) {
			MemoryInputStream mis = new MemoryInputStream(this.buffer);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while (mis.available() > 0) {
				baos.write(mis.read());
			}
			baos.writeTo(out);
		} else {
			System.err.println("buffer is null");
		}
	}

	protected RaceBusiness getRaceBusiness(IWApplicationContext iwc)
			throws RemoteException {
		return (RaceBusiness) IBOLookup.getServiceInstance(iwc,
				RaceBusiness.class);
	}
}