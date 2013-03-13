package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.data.RaceNumber;
import is.idega.idegaweb.msi.data.RaceType;
import is.idega.idegaweb.msi.data.RaceUserSettings;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.text.Name;

public class RaceNumberEditor extends RaceBlock {

	protected static final String PARAMETER_ACTION = "msi_prm_action";
	protected static final String PARAMETER_RACE_TYPE_PK = "prm_race_type_pk";
	protected static final String PARAMETER_RACE_NUMBER_PK = "prm_race_number_pk";

	protected static final String PARAMETER_USER_SSN = "prm_user";

	
	private static final int ACTION_VIEW = 1;
	private static final int ACTION_EDIT = 2;
	private static final int ACTION_SAVE = 3;

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
	
	protected void showList(IWContext iwc) throws RemoteException, FinderException {
		

		this.getParentPage().addStyleSheetURL(getBundle().getVirtualPathWithFileNameString("msiEditor.css"));
		Form form = new Form();
				
		Table2 table = new Table2();
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setStyleClass("msiracenumbereditor");
				
		Collection raceTypes = getRaceBusiness(iwc).getRaceTypes();
		Iterator raceTypeIt = raceTypes.iterator();
		DropdownMenu raceTypeDropDown = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_RACE_TYPE_PK)); 
		raceTypeDropDown.addMenuElement("", localize("race_number_editor.select_race_type","Select race type"));
		while (raceTypeIt.hasNext()) {
			RaceType raceType = (RaceType)raceTypeIt.next();
			raceTypeDropDown.addMenuElement(raceType.getPrimaryKey().toString(), localize(raceType.getRaceType(),raceType.getRaceType()));
		}
		raceTypeDropDown.setToSubmit();
		
		Collection numbers = null;
		if (iwc.isParameterSet(PARAMETER_RACE_TYPE_PK)) {
			String raceTypeID = iwc.getParameter(PARAMETER_RACE_TYPE_PK);
			raceTypeDropDown.setSelectedElement(raceTypeID);
			RaceType selectedRaceType = getRaceBusiness(iwc).getRaceTypeHome().findByPrimaryKey(Integer.valueOf(raceTypeID));
			numbers = getRaceBusiness(iwc).getRaceNumberHome().findAllByType(selectedRaceType); 
			if (numbers == null || numbers.isEmpty()) {
				for (int i = 0; i < 1000; i++) {
					RaceNumber number;
					try {
						number = getRaceBusiness(iwc).getRaceNumberHome().create();
						number.setRaceNumber(Integer.toString(i));
						number.setRaceType(selectedRaceType);
						number.setIsApproved(false);
						number.setIsInUse(false);
						number.store();
					} catch (CreateException e) {
						e.printStackTrace();
					}
				}
				
				numbers = getRaceBusiness(iwc).getRaceNumberHome().findAllByType(selectedRaceType);
			}
		}
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
		cell.add(raceTypeDropDown);
		group.createRow().createCell().setHeight("20");
		
		if (iwc.isParameterSet(PARAMETER_RACE_TYPE_PK)) {
			row = group.createRow();
			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_number_editor.race_number", "Number")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_number_editor.race_type", "Race type")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_number_editor.application_date", "Application date")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_number_editor.approved_date", "Approved date")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_number_editor.user_ssn", "User ssn")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_number_editor.user_name", "User name")));
		}
				
		group = table.createBodyRowGroup();
		int iRow = 1;
		
		if (numbers != null) {
			Iterator iter = numbers.iterator();
			RaceNumber number;
			
			while (iter.hasNext()) {
				row = group.createRow();
				number = (RaceNumber) iter.next();
				try {
					Link edit = new Link(getEditIcon(number.getRaceNumber()));
					edit.addParameter(PARAMETER_RACE_TYPE_PK, iwc.getParameter(PARAMETER_RACE_TYPE_PK));
					edit.addParameter(PARAMETER_RACE_NUMBER_PK, number.getPrimaryKey().toString());
					edit.addParameter(PARAMETER_ACTION, ACTION_EDIT);
								
					cell = row.createCell();
					cell.add(new Text(number.getRaceNumber()));
					cell = row.createCell();
					if (number.getRaceType() != null) {
						cell.add(new Text(localize(number.getRaceType().getRaceType(), number.getRaceType().getRaceType())));
					} else {
						cell.add(new Text(""));						
					}
					cell = row.createCell();
					if (number.getApplicationDate() != null) {
						cell.add(new Text(new IWTimestamp(number.getApplicationDate()).getDateString("dd.MM.yyyy")));
					} else {
						cell.add(new Text(""));						
					}
					cell = row.createCell();
					if (number.getApprovedDate() != null) {
						cell.add(new Text(new IWTimestamp(number.getApprovedDate()).getDateString("dd.MM.yyyy")));						
					} else {
						cell.add(new Text(""));												
					}
					cell = row.createCell();
					RaceUserSettings settings = null;
					if (number.getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_SNOCROSS)) {
						try {
							settings = getRaceBusiness(iwc).getRaceUserSettingsHome().findBySnocrossRaceNumber(number);
						} catch (Exception e) {
						}
					} else if (number.getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_MX_AND_ENDURO)) {
						try {
							settings = getRaceBusiness(iwc).getRaceUserSettingsHome().findByMXRaceNumber(number);
						} catch (Exception e) {
						}						
					}
					if (settings != null) {
						User user = settings.getUser();
						cell.add(new Text(user.getPersonalID()));
						cell = row.createCell();
						cell.add(new Text(new Name(user.getFirstName(), user.getMiddleName(), user.getLastName()).getName()));
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
					row.setStyleClass("evenRow");
				} else {
					row.setStyleClass("oddRow");
				}
				iRow++;
			}
		}
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
			} catch (Exception e) {
			}
		} else if (eNumber.getRaceType().getRaceType().equals(MSIConstants.RACE_TYPE_MX_AND_ENDURO)) {
			try {
				settings = getRaceBusiness(iwc).getRaceUserSettingsHome().findByMXRaceNumber(eNumber);
			} catch (Exception e) {
			}						
		}
		
		TextInput user = new TextInput(PARAMETER_USER_SSN);
		if (settings != null) {
			user.setValue(settings.getUser().getPersonalID());
		}
		
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label label = new Label(localize("race_number_editor.number", "Number"), number);
		layer.add(label);
		layer.add(number);
		form.add(layer);
		form.add(new Break());

		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("race_number_editor.race_type", "Type"), type);
		layer.add(label);
		layer.add(type);
		form.add(layer);
		form.add(new Break());

		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("race_number_editor.user_ssn", "User ssn"), user);
		layer.add(label);
		layer.add(user);
		form.add(layer);
		form.add(new Break());
				
		SubmitButton save = (SubmitButton) getButton(new SubmitButton(localize("save", "Save"), PARAMETER_ACTION, String.valueOf(ACTION_SAVE)));


		SubmitButton cancel = (SubmitButton) getButton(new SubmitButton(localize("cancel", "Cancel"), PARAMETER_ACTION, String.valueOf(ACTION_VIEW)));

		form.add(save);
		form.add(cancel);

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