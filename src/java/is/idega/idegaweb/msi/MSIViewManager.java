package is.idega.idegaweb.msi;

import is.idega.idegaweb.msi.presentation.EventEditor;
import is.idega.idegaweb.msi.presentation.RaceEditor;
import is.idega.idegaweb.msi.presentation.RaceNumberEditor;
import is.idega.idegaweb.msi.presentation.RaceReport;
import is.idega.idegaweb.msi.presentation.SeasonEditor;

import java.util.ArrayList;
import java.util.Collection;

import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.workspace.view.WorkspaceApplicationNode;
import com.idega.workspace.view.WorkspaceClassViewNode;

public class MSIViewManager {

	private ViewNode msiViewNode;
	private IWMainApplication iwma;

	public static MSIViewManager getInstance(IWMainApplication iwma) {
		MSIViewManager instance = (MSIViewManager) iwma
				.getAttribute("msiviewmanager");
		if (instance == null) {
			instance = new MSIViewManager();
			instance.iwma = iwma;
			iwma.setAttribute("msiviewmanager", instance);
		}
		return instance;
	}

	public ViewManager getViewManager() {
		return ViewManager.getInstance(this.iwma);
	}

	public ViewNode getMSIViewNode() {
		IWBundle iwb = this.iwma.getBundle("is.idega.idegaweb.msi");
		if (this.msiViewNode == null) {
			this.msiViewNode = initalizeMSINode(iwb);
		}
		return this.msiViewNode;
	}

	private ViewNode initalizeMSINode(IWBundle iwb) {
		ViewManager viewManager = ViewManager.getInstance(this.iwma);
		ViewNode workspace = viewManager.getWorkspaceRoot();

		Collection roles = new ArrayList();
		roles.add("msi_admin");

		DefaultViewNode msiNode = new WorkspaceApplicationNode("msi",
				workspace, roles);
		msiNode.setName("#{localizedStrings['is.idega.idegaweb.msi']['MSI']}");

		DefaultViewNode setupNode = new WorkspaceClassViewNode("setup", msiNode);
		setupNode
				.setName("#{localizedStrings['is.idega.idegaweb.msi']['Setup']}");

		WorkspaceClassViewNode seasons = new WorkspaceClassViewNode("seasons",
				setupNode);
		seasons
				.setName("#{localizedStrings['is.idega.idegaweb.msi']['msi_seasons']}");
		seasons.setComponentClass(SeasonEditor.class);

		WorkspaceClassViewNode races = new WorkspaceClassViewNode("races",
				setupNode);
		races
				.setName("#{localizedStrings['is.idega.idegaweb.msi']['msi_season_races']}");
		races.setComponentClass(RaceEditor.class);

		WorkspaceClassViewNode events = new WorkspaceClassViewNode("events",
				setupNode);
		events
				.setName("#{localizedStrings['is.idega.idegaweb.msi']['msi_events']}");
		events.setComponentClass(EventEditor.class);

		WorkspaceClassViewNode report = new WorkspaceClassViewNode("report",
				setupNode);
		report
				.setName("#{localizedStrings['is.idega.idegaweb.msi']['msi_report']}");
		report.setComponentClass(RaceReport.class);

		WorkspaceClassViewNode raceNumbers = new WorkspaceClassViewNode("raceNumbers",
				setupNode);
		raceNumbers
				.setName("#{localizedStrings['is.idega.idegaweb.msi']['msi_raceNumbers']}");
		raceNumbers.setComponentClass(RaceNumberEditor.class);

		return msiNode;
	}

}
