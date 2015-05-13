package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.data.RaceUserSettings;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Text;
import com.idega.user.data.User;
import com.idega.util.ListUtil;

public class RaceParticipantList extends RaceBlock {
	
	public static final String PARAMETER_RACE = "prm_race";

	private Race race = null;

	public void main(IWContext iwc) throws Exception {
		try {
			if (iwc.isParameterSet(PARAMETER_RACE)) {
				race = ConverterUtility.getInstance().convertGroupToRace(
						new Integer(iwc.getParameter(PARAMETER_RACE)));
			}
		} catch (Exception e) {
		}

		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("raceElement");
		layer.setID("raceParticipants");

		Layer headerLayer = new Layer(Layer.DIV);
		headerLayer.setStyleClass("raceParticipantsHeader");
		layer.add(headerLayer);

		Layer headingLayer = new Layer(Layer.DIV);
		headingLayer.setStyleClass("raceParticipantHeading");
		headingLayer.add(new Text(getHeading()));
		headerLayer.add(headingLayer);

		layer.add(getRaceParticipantList(iwc));

		add(layer);
	}

	protected String getHeading() {
		return race != null ? race.getName() : "";
	}

	private Layer getRaceParticipantList(IWContext iwc) {
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("raceParticipantsEvents");
		layer.setID("raceParticipantsEvents");

		Map participants = getRaceParticipants(iwc);
		if (participants != null && !participants.isEmpty()) {
			Iterator it = participants.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				List eventParticipants = (List) participants.get(key);
				try {
					RaceEvent raceEvent = ConverterUtility.getInstance().convertGroupToRaceEvent(key);
					Layer subLayer = new Layer(Layer.DIV);
					subLayer.setStyleClass("raceParticipantsEvent");
					subLayer.setID("raceParticipantsEvent");
					layer.add(subLayer);

					Layer headerLayer = new Layer(Layer.DIV);
					headerLayer.setStyleClass("raceParticipantsEventHeader");
					subLayer.add(headerLayer);

					Layer headingLayer = new Layer(Layer.DIV);
					headingLayer.setStyleClass("raceParticipantsEventHeading");
					headingLayer.add(new Text(raceEvent.getName()));
					headerLayer.add(headingLayer);

					subLayer.add(getRaceParticipantListForEvent(iwc, eventParticipants, raceEvent));
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Error printing participants for " + key, e);
				}
			}
		}
		return layer;
	}

	private Table2 getRaceParticipantListForEvent(IWContext iwc, List eventParticipants, RaceEvent raceEvent) throws RemoteException {
		Table2 table = new Table2();
		table.setStyleClass("raceParticipantTable");
		table.setStyleClass("ruler");
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);

		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.setStyleClass("raceNumber");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_participant_list.race_number", "#")));

		cell = row.createHeaderCell();
		cell.setStyleClass("name");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_participant_list.name", "Name")));

		cell = row.createHeaderCell();
		cell.setStyleClass("raceVehicle");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_participant_list.race_vehicle", "Race vehicle")));

		cell = row.createHeaderCell();
		cell.setStyleClass("raceVehicleSubtype");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_participant_list.race_vehicle_subtype", "Subtype")));

		cell = row.createHeaderCell();
		cell.setStyleClass("raceEngineCC");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_participant_list.race_engine_CC", "Engine CC")));

		cell = row.createHeaderCell();
		cell.setStyleClass("raceEngine");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_participant_list.race_engine", "Engine")));

		cell = row.createHeaderCell();
		cell.setStyleClass("raceTeam");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_participant_list.race_team", "Team")));

		cell = row.createHeaderCell();
		cell.setStyleClass("raceSponsor");
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"race_participant_list.race_sponsor", "Sponsor")));

		group = table.createBodyRowGroup();
		int iRow = 1;

		if (!ListUtil.isEmpty(eventParticipants)) {
			Iterator iter = eventParticipants.iterator();
			while (iter.hasNext()) {
				Participant participant = (Participant) iter.next();
				if (participant == null) {
					getLogger().warning("Participant is unknown for race event: " + raceEvent);
					continue;
				}

				row = group.createRow();

				User user = null;
				RaceUserSettings settings = null;
				try {
					user = participant.getUser();
					settings = this.getRaceBusiness(iwc).getRaceUserSettings(user);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Error getting settings for participant " + participant + ", user: " + user + (user == null ? "" : "(ID: " + user.getId() + ")") + ". Race event: " + raceEvent, e);
				}
				if (user == null) {
					getLogger().warning("User can not be found for participant " + participant + ", race event: " + raceEvent);
				}
				if (settings == null) {
					getLogger().warning("Settings can not be found for participant " + participant + ", race event: " + raceEvent);
				}

				if (iRow == 1) {
					row.setStyleClass("firstRow");
				} else if (!iter.hasNext()) {
					row.setStyleClass("lastRow");
				}

				cell = row.createCell();
				cell.setStyleClass("firstColumn");
				cell.setStyleClass("raceNumber");
				cell.add(new Text(participant.getRaceNumber() != null ? participant.getRaceNumber() : ""));

				cell = row.createCell();
				cell.setStyleClass("name");
				cell.add(new Text(user != null ?user.getName() : ""));

				cell = row.createCell();
				cell.setStyleClass("raceVehicle");
				String raceVehicleString = "";
				if (settings != null && settings.getVehicleType() != null) {
					raceVehicleString = getResourceBundle().getLocalizedString(
							settings.getVehicleType().getLocalizationKey(),
							settings.getVehicleType().getLocalizationKey());
				}
				cell.add(new Text(raceVehicleString));

				cell = row.createCell();
				cell.setStyleClass("raceVehicleSubtype");
				String raceVehicleSubtypeString = "";
				if (settings != null && settings.getVehicleSubType() != null) {
					raceVehicleSubtypeString = getResourceBundle()
							.getLocalizedString(
									settings.getVehicleSubType()
											.getLocalizationKey(),
									settings.getVehicleSubType()
											.getLocalizationKey());
				}
				cell.add(new Text(raceVehicleSubtypeString));

				cell = row.createCell();
				cell.setStyleClass("raceEngineCC");
				cell.add(new Text(settings == null ? "" : settings.getEngineCC() != null ? settings.getEngineCC() : ""));

				cell = row.createCell();
				cell.setStyleClass("raceEngine");
				cell.add(new Text(settings == null ? "" : settings.getEngine() != null ? settings.getEngine() : ""));

				cell = row.createCell();
				cell.setStyleClass("raceTeam");
				cell.add(new Text(settings == null ? "" : settings.getTeam() != null ? settings.getTeam() : ""));

				StringBuffer sponsorString = new StringBuffer("");
				if (settings != null && settings.getSponsor() != null) {
					if (settings.getSponsor().length() > 30) {
						sponsorString.append(settings.getSponsor().substring(0, 30));
					} else {
						sponsorString.append(settings.getSponsor());
					}
				}
				cell = row.createCell();
				cell.setStyleClass("raceSponsor");
				cell.add(new Text(sponsorString.toString()));

				if (iRow % 2 == 0) {
					row.setStyleClass("evenRow");
				} else {
					row.setStyleClass("oddRow");
				}

				iRow++;
			}
		} else {
			getLogger().warning("Participants not provided");
		}

		return table;
	}

	protected Map getRaceParticipants(IWContext iwc) {
		Collection raceParticipants = null;
		try {
			raceParticipants = getRaceBusiness(iwc).getParticipantHome().findAllByRace(this.race);
		} catch (RemoteException e) {
		} catch (FinderException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (raceParticipants != null && !raceParticipants.isEmpty()) {
			Map raceEvents = new HashMap();
			Iterator it = raceParticipants.iterator();
			while (it.hasNext()) {
				Participant info = (Participant) it.next();
				List eventParticipants = null;
				if (raceEvents.containsKey(info.getRaceEvent().getPrimaryKey())) {
					eventParticipants = (List) raceEvents.get(info
							.getRaceEvent().getPrimaryKey());
				} else {
					eventParticipants = new ArrayList();
				}

				eventParticipants.add(info);
				raceEvents.put(info.getRaceEvent().getPrimaryKey(),
						eventParticipants);
			}

			return raceEvents;
		}

		return null;
	}
}