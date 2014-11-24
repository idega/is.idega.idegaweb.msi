package is.idega.idegaweb.msi.presentation;


import is.idega.idegaweb.msi.MsiConstants;
import is.idega.idegaweb.msi.data.Event;
import is.idega.idegaweb.msi.data.EventHome;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.block.web2.business.Web2Business;
import com.idega.business.IBOLookup;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
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
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.PresentationUtil;

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
		table.setCellpadding(0);
		table.setCellspacing(0);
				
		Collection events = getRaceBusiness(iwc).getEvents();
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.add(new Text(localize("event_editor.existing_events", "Existing events")));
		row.createHeaderCell();
		cell = row.createHeaderCell();
		cell.add(new Text(localize("event_editor.unactive_events", "Unactive events")));
		
		group = table.createBodyRowGroup();
		row = group.createRow();
		cell = row.createCell();
		Layer activeEvents = new Layer("select");
		cell.add(activeEvents);
		activeEvents.setStyleClass("events-selector active-events");
		activeEvents.setMarkupAttribute("multiple", "multiple");
		
		cell = row.createCell();
		Layer disable = new Layer("input");
		cell.add(disable);
		disable.setMarkupAttribute("type", "button");
		disable.setMarkupAttribute("value", ">>");
		disable.setStyleClass("action disable");
		Layer enable = new Layer("input");
		cell.add(enable);
		enable.setMarkupAttribute("type", "button");
		enable.setStyleClass("action enable");
		enable.setMarkupAttribute("value", "<<");
		
		cell = row.createCell();
		Layer inactiveEvents = new Layer("select");
		cell.add(inactiveEvents);
		inactiveEvents.setStyleClass("events-selector inactive-events");
		inactiveEvents.setMarkupAttribute("multiple", "multiple");
		for (Iterator iter = events.iterator();iter.hasNext();) {
			Event event = (Event) iter.next();
			Layer option = new Layer("option");
			activeEvents.add(option);
			option.setMarkupAttribute("value", event.getPrimaryKey().toString());
			row = group.createRow();
			option.add(event.getName());
		}
		EventHome eventHome = (EventHome) IDOLookup.getHome(Event.class);
		
		events = eventHome.getInvalidEvents();
		for (Iterator iter = events.iterator();iter.hasNext();) {
			Event event = (Event) iter.next();
			Layer option = new Layer("option");
			inactiveEvents.add(option);
			option.setMarkupAttribute("value", event.getPrimaryKey().toString());
			row = group.createRow();
			option.add(event.getName());
		}
		
		form.add(table);
		form.add(new Break());
		SubmitButton newLink = (SubmitButton) getButton(new SubmitButton(localize("event_editor.new_event", "New event"), PARAMETER_ACTION, String.valueOf(ACTION_NEW)));
		form.add(newLink);
		add(form);
		addFiles(iwc);
		StringBuilder script = new StringBuilder("jQuery(document).ready(function(){jQuery('#").append(table.getId()).append("').eventsEditor();});");
		Layer scriptTag = new Layer("script");
		form.add(scriptTag);
		scriptTag.add(script.toString());
	}
	
	private void addFiles(IWContext iwc){
		IWMainApplication iwma = iwc.getApplicationContext().getIWMainApplication();
		IWBundle iwb = iwma.getBundle(MsiConstants.IW_BUNDLE_IDENTIFIER);
		List files = new ArrayList();
		files.add(CoreConstants.DWR_ENGINE_SCRIPT);
		files.add(CoreConstants.DWR_UTIL_SCRIPT);
		files.add(CoreUtil.getCoreBundle().getVirtualPathWithFileNameString("iw_core.js"));
		Web2Business web2;
		try {
			web2 = (Web2Business) IBOLookup.getServiceInstance(iwc, Web2Business.class);
			files.add(web2.getBundleURIToJQueryLib("1.7"));
		} catch (Exception e) {
			this.getLogger().log(Level.WARNING, "Failed getting script files", e);
		}
		files.add(iwb.getVirtualPathWithFileNameString("javascript/events-editor.js"));
		files.add("/dwr/interface/RaceBusiness.js");
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, files);
		files = new ArrayList();
		files.add(iwb.getVirtualPathWithFileNameString("style/events-editor.css"));
		PresentationUtil.addStyleSheetsToHeader(iwc, files);
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