package is.idega.idegaweb.msi.presentation;


import is.idega.idegaweb.msi.data.Event;

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
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

public class EventEditor extends RaceBlock {
	
	private static final String PARAMETER_ACTION = "msi_prm_action";
	private static final String PARAMETER_EVENT = "prm_event";

	private static final int ACTION_VIEW = 1;
	private static final int ACTION_NEW = 2;
	private static final int ACTION_SAVE = 3;
	
	public void main(IWContext iwc) throws Exception {
		init(iwc);
	}
	
	protected void init(IWContext iwc) throws Exception {
		switch (parseAction(iwc)) {
			case ACTION_VIEW:
				showList(iwc);
				break;

			case ACTION_NEW:
				showEditor(iwc);
				break;

			case ACTION_SAVE:
				save(iwc);
				showList(iwc);
				break;
		}
	}

	public void showList(IWContext iwc) throws RemoteException, FinderException {
		Form form = new Form();
		Table2 table = new Table2();
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);
				
		Collection events = getRaceBusiness(iwc).getEvents();
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
		cell.add(new Text(localize("event_editor.existing_events", "Existing events")));
		group = table.createBodyRowGroup();
		int iRow = 1;
		
		if (events != null) {
			Iterator iter = events.iterator();
			Event event;
			while (iter.hasNext()) {
				row = group.createRow();
				event = (Event) iter.next();
				cell = row.createCell();
				cell.add(new Text(event.getName()));
				iRow++;
			}
		}
		form.add(table);
		form.add(new Break());
		SubmitButton newLink = (SubmitButton) getButton(new SubmitButton(localize("event_editor.new_event", "New event"), PARAMETER_ACTION, String.valueOf(ACTION_NEW)));
		form.add(newLink);
		add(form);
	}
	
	public void showEditor(IWContext iwc) throws java.rmi.RemoteException {
		Form form = new Form();
		TextInput event = new TextInput(PARAMETER_EVENT);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label label = new Label(localize("event_editor.event", "Event"), event);
		layer.add(label);
		layer.add(event);
		form.add(layer);
		form.add(new Break());


		SubmitButton save = (SubmitButton) getButton(new SubmitButton(localize("save", "Save"), PARAMETER_ACTION, String.valueOf(ACTION_SAVE)));
		SubmitButton cancel = (SubmitButton) getButton(new SubmitButton(localize("cancel", "Cancel"), PARAMETER_ACTION, String.valueOf(ACTION_VIEW)));

		form.add(save);
		form.add(cancel);
		add(form);
	}

	public void save(IWContext iwc) throws java.rmi.RemoteException, CreateException, FinderException {
		String eventName = iwc.getParameter(PARAMETER_EVENT);
		
		getRaceBusiness(iwc).createEvent(eventName);
	}
	
	private int parseAction(IWContext iwc) {
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			return Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		return ACTION_VIEW;
	}	
}