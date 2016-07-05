package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceNumberHome;
import is.idega.idegaweb.msi.data.RaceType;
import is.idega.idegaweb.msi.data.RaceTypeHome;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.faces.context.FacesContext;

import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
import com.idega.util.text.Name;

public class RaceNumberEditor extends RaceBlock {

	protected static final String PARAMETER_ACTION = "msi_prm_action";
	protected static final String PARAMETER_RACE_TYPE_PK = "prm_race_type_pk";
	protected static final String PARAMETER_RACE_NUMBER_PK = "prm_race_number_pk";

	protected static final String PARAMETER_USER_SSN = "prm_user";

	
	private static final int ACTION_VIEW = 1;
	private static final int ACTION_EDIT = 2;
	private static final int ACTION_SAVE = 3;

	private RaceNumberHome getRaceNumberHome() {
		try {
			return (RaceNumberHome) IDOLookup.getHome(RaceNumber.class);
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get " + RaceNumberHome.class + " cause of: ", e);
		}

		return null;
	}

	private RaceTypeHome getRaceTypeHome() {
		try {
			return (RaceTypeHome) IDOLookup.getHome(RaceType.class);
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get " + RaceTypeHome.class + " cause of: ", e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.presentation.Block#encodeBegin(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeBegin(FacesContext fc) throws IOException {
		super.encodeBegin(fc);

		if (getSportUnionBundle() != null) {
			ArrayList<String> styleSheets = new ArrayList<String>();
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/viewer.css"));
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/select.css"));
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/input.css"));
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/button.css"));
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/label.css"));
			styleSheets.add(getSportUnionBundle().getVirtualPathWithFileNameString("style/sport_union.css"));
			PresentationUtil.addStyleSheetsToHeader(
					IWContext.getIWContext(fc), styleSheets);
		}
	}

	public void main(IWContext iwc) throws Exception {
		init(iwc);
	}

	protected void init(IWContext iwc) throws Exception {
		switch (parseAction(iwc)) {
			case ACTION_VIEW:
				showList(iwc);
				break;
			case ACTION_EDIT:
				showEditor(iwc);
				break;
			case ACTION_SAVE:
				save(iwc);
				showList(iwc);
				break;
		}
	}

	private int parseAction(IWContext iwc) {
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			return Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		return ACTION_VIEW;
	}

	private DropdownMenu getRaceTypeDropdown(IWContext iwc) {
		DropdownMenu raceTypeDropDown = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_RACE_TYPE_PK)); 
		raceTypeDropDown.addMenuElement("", localize(
				"race_number_editor.select_race_type", 
				"Select race type"));
		raceTypeDropDown.setToSubmit();

		Collection<RaceType> raceTypes = getRaceBusiness(iwc).getRaceTypes();
		for (RaceType raceType: raceTypes) {
			raceTypeDropDown.addMenuElement(
					raceType.getPrimaryKey().toString(), 
					localize(raceType.getRaceType(), raceType.getRaceType()));
		}

		if (iwc.isParameterSet(PARAMETER_RACE_TYPE_PK)) {
			raceTypeDropDown.setSelectedElement(iwc.getParameter(PARAMETER_RACE_TYPE_PK));
		}

		return raceTypeDropDown;
	}

	protected void showList(IWContext iwc) throws RemoteException, FinderException {
		String styleSheets = getBundle().getVirtualPathWithFileNameString("msiEditor.css");
		if (!StringUtil.isEmpty(styleSheets)) {
			PresentationUtil.addStyleSheetToHeader(iwc, styleSheets);
		}

		Form form = new Form();

		Table2 table = new Table2();
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setStyleClass("msiracenumbereditor table");

		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = null;
		TableCell2 cell = null;

		if (iwc.isParameterSet(PARAMETER_RACE_TYPE_PK)) {
			row = group.createRow();
			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("race_number_editor.race_number", "Number")));

			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("race_number_editor.race_type", "Race type")));

			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("race_number_editor.application_date", "Application date")));

			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("race_number_editor.approved_date", "Approved date")));

			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("race_number_editor.user_ssn", "User ssn")));

			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("race_number_editor.user_name", "User name")));

			cell = row.createHeaderCell();
			cell.setStyleClass("text");
			cell.add(new Text(localize("edit", "Edit")));
		}

		group = table.createBodyRowGroup();
		int iRow = 1;

		Collection<RaceNumber> numbers = null;

		String raceTypeID = iwc.getParameter(PARAMETER_RACE_TYPE_PK);
		if (!StringUtil.isEmpty(raceTypeID)) {
			RaceType selectedRaceType = getRaceTypeHome().findByPrimaryKey(Integer.valueOf(raceTypeID));
			numbers = getRaceNumberHome().findAllByType(selectedRaceType); 
			if (ListUtil.isEmpty(numbers)) {
				for (int i = 0; i < 1000; i++) {
					RaceNumber number;
					try {
						number = getRaceNumberHome().create();
						number.setRaceNumber(Integer.toString(i));
						number.setRaceType(selectedRaceType);
						number.setIsApproved(false);
						number.setIsInUse(false);
						number.store();
					} catch (CreateException e) {
						e.printStackTrace();
					}
				}

				numbers = getRaceNumberHome().findAllByType(selectedRaceType);
			}
		}

		if (numbers != null) {
			for (RaceNumber number : numbers) {
				row = group.createRow();
				try {
					Link edit = new Link(getEditIcon(number.getRaceNumber()));
					edit.addParameter(PARAMETER_RACE_TYPE_PK, iwc.getParameter(PARAMETER_RACE_TYPE_PK));
					edit.addParameter(PARAMETER_RACE_NUMBER_PK, number.getPrimaryKey().toString());
					edit.addParameter(PARAMETER_ACTION, ACTION_EDIT);

					cell = row.createCell();
					cell.setStyleClass("text");
					cell.add(new Text(number.getRaceNumber()));

					cell = row.createCell();
					cell.setStyleClass("text");
					if (number.getRaceType() != null) {
						cell.add(new Text(localize(
								number.getRaceType().getRaceType(), 
								number.getRaceType().getRaceType())));
					} else {
						cell.add(new Text(""));						
					}

					cell = row.createCell();
					cell.setStyleClass("text");
					if (number.getApplicationDate() != null) {
						cell.add(new Text(new IWTimestamp(number.getApplicationDate()).getDateString("dd.MM.yyyy")));
					} else {
						cell.add(new Text(""));						
					}

					cell = row.createCell();
					cell.setStyleClass("text");
					if (number.getApprovedDate() != null) {
						cell.add(new Text(new IWTimestamp(number.getApprovedDate()).getDateString("dd.MM.yyyy")));						
					} else {
						cell.add(new Text(""));												
					}

					cell = row.createCell();
					cell.setStyleClass("text");
					RaceUserSettings settings = null;
					if (number.getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_SNOCROSS)) {
						try {
							settings = getRaceBusiness(iwc).getRaceUserSettingsHome().findBySnocrossRaceNumber(number);
						} catch (Exception e) {}
					} else if (number.getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_MX_AND_ENDURO)) {
						try {
							settings = getRaceBusiness(iwc).getRaceUserSettingsHome().findByMXRaceNumber(number);
						} catch (Exception e) {}
					}

					if (settings != null) {
						User user = settings.getUser();
						cell.add(new Text(user.getPersonalID()));
						cell = row.createCell();
						cell.add(new Text(new Name(
								user.getFirstName(), 
								user.getMiddleName(), 
								user.getLastName()).getName()));
					} else {
						cell.add(new Text(""));
						cell = row.createCell();
						cell.add(new Text(""));
					}

					row.createCell().add(edit);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}

				if (iRow % 2 == 0) {
					row.setStyleClass("tableRow even evenRow");
				} else {
					row.setStyleClass("tableRow odd oddRow");
				}

				iRow++;
			}
		}

		Label raceTypeDropdownLabel = new Label();
		raceTypeDropdownLabel.setLabel(getLocalizedString("race_editor.race_type", "Type", iwc));

		Layer raceTypeLayer = new Layer();
		raceTypeLayer.setStyleClass("formItem");
		raceTypeLayer.add(raceTypeDropdownLabel);
		raceTypeLayer.add(getRaceTypeDropdown(iwc));

		Layer filterLayer = new Layer();
		filterLayer.setStyleClass("formSection navigationLayer");
		filterLayer.add(raceTypeLayer);

		form.add(filterLayer);
		form.add(table);
		add(form);
	}

	public void showEditor(IWContext iwc) throws java.rmi.RemoteException, FinderException {
		String raceNumberID = iwc.getParameter(PARAMETER_RACE_NUMBER_PK);
		Form form = new Form();
		form.maintainParameter(PARAMETER_RACE_TYPE_PK);
		form.maintainParameter(PARAMETER_RACE_NUMBER_PK);
		
		RaceNumber eNumber = getRaceBusiness(iwc).getRaceNumberHome().findByPrimaryKey(Integer.valueOf(raceNumberID));
		
		TextInput number = new TextInput();
		number.setValue(eNumber.getRaceNumber());
		number.setDisabled(true);

		TextInput type = new TextInput();
		type.setValue(localize(eNumber.getRaceType().getRaceType(), eNumber.getRaceType().getRaceType()));
		type.setDisabled(true);

		RaceUserSettings settings = null;
		if (eNumber.getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_SNOCROSS)) {
			try {
				settings = getRaceBusiness(iwc).getRaceUserSettingsHome().findBySnocrossRaceNumber(eNumber);
			} catch (Exception e) {}
		} else if (eNumber.getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_MX_AND_ENDURO)) {
			try {
				settings = getRaceBusiness(iwc).getRaceUserSettingsHome().findByMXRaceNumber(eNumber);
			} catch (Exception e) {}						
		}
		
		TextInput user = new TextInput(PARAMETER_USER_SSN);
		if (settings != null) {
			user.setValue(settings.getUser().getPersonalID());
		}

		Layer formSectionLayer = new Layer();
		formSectionLayer.setStyleClass("formSection");
		
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		layer.setStyleClass("formItem");
		Label label = new Label(localize("race_number_editor.number", "Number"), number);
		layer.add(label);
		layer.add(number);
		formSectionLayer.add(layer);

		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		layer.setStyleClass("formItem");
		label = new Label(localize("race_number_editor.race_type", "Type"), type);
		layer.add(label);
		layer.add(type);
		formSectionLayer.add(layer);

		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		layer.setStyleClass("formItem");
		label = new Label(localize("race_number_editor.user_ssn", "User ssn"), user);
		layer.add(label);
		layer.add(user);
		formSectionLayer.add(layer);

		SubmitButton save = new SubmitButton(
				localize("save", "Save"), 
				PARAMETER_ACTION, 
				String.valueOf(ACTION_SAVE));
		save.setStyleClass("button");

		SubmitButton cancel = new SubmitButton(
				localize("cancel", "Cancel"), 
				PARAMETER_ACTION, 
				String.valueOf(ACTION_VIEW));
		cancel.setStyleClass("button");

		Layer buttonLayer = new Layer();
		buttonLayer.setStyleClass("buttonLayer");
		buttonLayer.add(save);
		buttonLayer.add(cancel);
		
		Layer formContent = new Layer();
		formContent.setStyleClass("formContents");
		formContent.add(formSectionLayer);
		formContent.add(buttonLayer);
		
		form.add(formContent);

		add(form);
	}
	
	private void save(IWContext iwc) throws java.rmi.RemoteException, FinderException {
		String raceNumberID = iwc.getParameter(PARAMETER_RACE_NUMBER_PK);
		RaceNumber number = getRaceBusiness(iwc).getRaceNumberHome().findByPrimaryKey(Integer.valueOf(raceNumberID));
		
		if (number != null) {
			String userSSN = iwc.getParameter(PARAMETER_USER_SSN);
			
			getRaceBusiness(iwc).updateRaceNumber(number, userSSN);
			
		}
	}
}