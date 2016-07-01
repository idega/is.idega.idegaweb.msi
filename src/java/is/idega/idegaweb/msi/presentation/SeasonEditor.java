package is.idega.idegaweb.msi.presentation;


import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.data.Season;
import is.idega.idegaweb.msi.data.SeasonHome;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;

public class SeasonEditor extends RaceBlock {
	
	private static final String PARAMETER_ACTION = "msi_prm_action";
	private static final String PARAMETER_SEASON = "prm_season";
	private static final String PARAMETER_SEASON_START = "prm_season_start";
	private static final String PARAMETER_SEASON_END = "prm_season_end";

	private static final int ACTION_VIEW = 1;
	private static final int ACTION_NEW = 2;
	private static final int ACTION_SAVE = 3;

	private SeasonHome getSeasonHome() {
		try {
			return (SeasonHome) IDOLookup.getHome(Season.class);
		} catch (IDOLookupException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, 
					"Failed to get " + SeasonHome.class + " cause of: ", e);
		}

		return null;
	}
	
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
				
		Collection seasons = getRaceBusiness(iwc).getSeasons();
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
		cell.add(new Text(localize("season_editor.existing_seasons", "Existing seasons")));
		group = table.createBodyRowGroup();
		int iRow = 1;
		
		if (seasons != null) {
			Iterator iter = seasons.iterator();
			Season run;
			while (iter.hasNext()) {
				row = group.createRow();
				run = ConverterUtility.getInstance().convertGroupToSeason((Group) iter.next());
				cell = row.createCell();
				cell.add(new Text(run.getName()));
				iRow++;
			}
		}
		form.add(table);
		form.add(new Break());
		SubmitButton newLink = (SubmitButton) getButton(new SubmitButton(localize("season_editor.new_season", "New season"), PARAMETER_ACTION, String.valueOf(ACTION_NEW)));
		form.add(newLink);
		add(form);
	}
	
	public void showEditor(IWContext iwc) throws java.rmi.RemoteException {
		Form form = new Form();
		TextInput season = new TextInput(PARAMETER_SEASON);
		DateInput seasonStartDate = new DateInput(PARAMETER_SEASON_START);
		DateInput seasonEndDate = new DateInput(PARAMETER_SEASON_END);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label label = new Label(localize("season_editor.season", "Season"), season);
		layer.add(label);
		layer.add(season);
		form.add(layer);
		form.add(new Break());

		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("season_editor.season_start", "Season start date"), season);
		layer.add(label);
		layer.add(seasonStartDate);
		form.add(layer);
		form.add(new Break());

		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("season_editor.season_end", "Season end date"), season);
		layer.add(label);
		layer.add(seasonEndDate);
		form.add(layer);
		form.add(new Break());

		SubmitButton save = (SubmitButton) getButton(new SubmitButton(localize("save", "Save"), PARAMETER_ACTION, String.valueOf(ACTION_SAVE)));
		SubmitButton cancel = (SubmitButton) getButton(new SubmitButton(localize("cancel", "Cancel"), PARAMETER_ACTION, String.valueOf(ACTION_VIEW)));

		form.add(save);
		form.add(cancel);
		add(form);
	}

	public void save(IWContext iwc) {
		getSeasonHome().update(null, 
				iwc.getParameter(PARAMETER_SEASON), 
				CoreConstants.EMPTY, 
				new IWTimestamp(iwc.getParameter(PARAMETER_SEASON_START)).getTimestamp(), 
				new IWTimestamp(iwc.getParameter(PARAMETER_SEASON_END)).getTimestamp());
	}
	
	private int parseAction(IWContext iwc) {
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			return Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		return ACTION_VIEW;
	}
	
	private GroupBusiness getGroupBiz(IWContext iwc) {
		GroupBusiness business = null;
		try {
			business = (GroupBusiness) IBOLookup.getServiceInstance(iwc, GroupBusiness.class);
		} catch (IBOLookupException e) {
			business = null;
		}
		return business;
	}
}