/*
 * $Id: RaceReportWriter.java,v 1.3 2008/05/21 09:04:17 palli Exp $ Created on Jan 25, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to license terms.
 */
package is.idega.idegaweb.msi.business;


import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.presentation.RaceBlock;

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
import com.idega.user.data.User;
import com.idega.util.StringHandler;

public class RaceReportWriter extends DownloadWriter implements MediaWritable {
		
		public static final String PARAMETER_RACE_ID = "prm_race_id";
	
		private MemoryFileBuffer buffer = null;

		private Locale locale;

		private IWResourceBundle iwrb;

		public String getMimeType() {
			if (this.buffer != null) {
				return this.buffer.getMimeType();
			}
			else {
				return super.getMimeType();
			}
		}

		public void init(HttpServletRequest req, IWContext iwc) {
			try {
				this.locale = iwc.getApplicationSettings().getApplicationLocale();
				this.iwrb = iwc.getIWMainApplication().getBundle(RaceBlock.IW_BUNDLE_IDENTIFIER).getResourceBundle(this.locale);

				if (req.getParameter(PARAMETER_RACE_ID) != null) {
					String raceID = req.getParameter(PARAMETER_RACE_ID);
					int groupID = -1;
					if (raceID != null) {
						groupID = Integer.parseInt(raceID);
					}

					Collection raceParticipants = null;
					raceParticipants = this.getRaceBusiness(iwc).getParticipantHome().findAllByRace(ConverterUtility.getInstance().convertRaceToGroup(new Integer(groupID)));

					if (raceParticipants != null && !raceParticipants.isEmpty()) {
						Map raceEvents = new HashMap();
						Iterator it = raceParticipants.iterator();
						while (it.hasNext()) {
							Participant info = (Participant) it.next();
							List eventParticipants = null;
							if (raceEvents.containsKey(info.getRaceEvent().getPrimaryKey())) {
								eventParticipants = (List) raceEvents.get(info.getRaceEvent().getPrimaryKey());
							} else {
								eventParticipants = new ArrayList();
							}
							
							eventParticipants.add(info);
							raceEvents.put(info.getRaceEvent().getPrimaryKey(), eventParticipants);
						}
						
						
						this.buffer = writeXLS(raceEvents, iwc);
						setAsDownload(iwc, "race_report.xls", this.buffer.length());
					}

				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		public MemoryFileBuffer writeXLS(Map raceEvents, IWContext iwc) throws Exception {
			MemoryFileBuffer buffer = new MemoryFileBuffer();
			MemoryOutputStream mos = new MemoryOutputStream(buffer);
			if (!raceEvents.isEmpty()) {
				HSSFWorkbook wb = new HSSFWorkbook();
				//HSSFSheet sheet = wb.createSheet(StringHandler.shortenToLength(this.schoolName, 30));

				Iterator it = raceEvents.keySet().iterator();
				while (it.hasNext()) {
					Object key = it.next();
					List eventParticipant = (List) raceEvents.get(key);
					RaceEvent raceEvent = ConverterUtility.getInstance().convertGroupToRaceEvent(key);
					
					HSSFSheet sheet = wb.createSheet(StringHandler.shortenToLength(raceEvent.getName(), 30));
					int cellRow = 0;
					HSSFRow row = sheet.createRow(cellRow++);
					int column = 0;

					HSSFCell cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.race_number", "Race number"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.first_name", "First name"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.last_name", "Last name"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.personal_id", "Personal ID"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.race_vehicle", "Race vehicle"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.transmitter_number", "Transmitter number"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.race_class", "Class"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.car_registration", "Car registration"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.transponder1", "Transponder1"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.transponder2", "Transponder2"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.nationality", "Nationality"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.engine", "Engine"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.type", "Type"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.cc", "CC"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.sub_type", "Sub type"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.club", "Club"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.sponsor", "Sponsor"));
					cell = row.createCell((short) column++);
					cell.setCellValue(this.iwrb.getLocalizedString("race_report.team", "Team"));

					User racer;
					Participant info;
					RaceUserSettings settings;
					
					Iterator iter = eventParticipant.iterator();
					while (iter.hasNext()) {
						column = 0;
						row = sheet.createRow(cellRow++);
						info = (Participant) iter.next();

						racer = info.getUser();
						settings = this.getRaceBusiness(iwc).getRaceUserSettings(racer);

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(info.getRaceNumber() == null ? "" : info.getRaceNumber());
						
						StringBuffer first = new StringBuffer(racer.getFirstName());
						if (racer.getMiddleName() != null) {
							first.append(" ");
							first.append(racer.getMiddleName());
						}
						
						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(first.toString() == null ? "" : first.toString());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(racer.getLastName() == null ? "" : racer.getLastName());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(racer.getPersonalID() == null ? "" : racer.getPersonalID());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(info.getRaceVehicle() == null ? "" : info.getRaceVehicle().getLocalizationKey());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(info.getChipNumber() == null ? "" : info.getChipNumber());
						
						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(raceEvent.getName() == null ? "" : raceEvent.getName());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(settings == null || settings.getBodyNumber() == null ? "" : settings.getBodyNumber());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(settings == null || settings.getTransponderNumber() == null ? "" : settings.getTransponderNumber());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue("");

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue("ISL");

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(settings == null || settings.getEngine() == null ? "" : settings.getEngine());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(settings == null || settings.getVehicleType() == null ? "" : settings.getVehicleType().getLocalizationKey());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(settings == null || settings.getEngineCC() == null ? "" : settings.getEngineCC());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(settings == null || settings.getVehicleSubType() == null ? "" : settings.getVehicleSubType().getLocalizationKey());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue("");

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(info.getSponsors() == null ? "" : info.getSponsors());

						cell = row.createCell((short) column++);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(settings == null || settings.getTeam() == null ? "" : settings.getTeam());
					}
				}
				
				wb.write(mos);
			}
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
		}
		else {
			System.err.println("buffer is null");
		}
	}

	protected RaceBusiness getRaceBusiness(IWApplicationContext iwc) throws RemoteException {
		return (RaceBusiness) IBOLookup.getServiceInstance(iwc, RaceBusiness.class);
	}
}