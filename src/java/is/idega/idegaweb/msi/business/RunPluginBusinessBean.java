/*
 * Created on Aug 17, 2004
 * 
 */
package is.idega.idegaweb.msi.business;

import is.idega.idegaweb.msi.data.Participant;
import is.idega.idegaweb.msi.presentation.CreateYearWindowPlugin;
import is.idega.idegaweb.msi.presentation.RaceEventTab;
import is.idega.idegaweb.msi.presentation.RunYearTab;
import is.idega.idegaweb.msi.presentation.UserRunTab;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.user.business.UserGroupPlugInBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 * @author birna
 * 
 */
public class RunPluginBusinessBean extends IBOServiceBean implements RunPluginBusiness, UserGroupPlugInBusiness {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3171562807916616945L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#afterGroupCreate(com.idega.user.data.Group)
	 */
	public void afterGroupCreateOrUpdate(Group group, Group parentGroup) throws CreateException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#afterUserCreate(com.idega.user.data.User)
	 */
	public void afterUserCreateOrUpdate(User user, Group parentGroup) throws CreateException, RemoteException {
		/*IWContext iwc = IWContext.getInstance();
		RaceBusiness runBiz = getRaceBiz(iwc);
		
		Group run = null;
		try {
			run = runBiz.getRunGroupOfTypeForGroup(parentGroup, MSIConstants.GROUP_TYPE_RACE);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		Group year = null;
		try {
			year = runBiz.getRunGroupOfTypeForGroup(parentGroup, MSIConstants.GROUP_TYPE_SEASON);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			Participant runEntry = getRaceBiz(iwc).getParticipantByRunAndYear(user, run, year);
			runEntry.setRunGroupGroup(parentGroup);
			runEntry.setRunDistanceGroup((Group) parentGroup.getParentNode());
			runEntry.store();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}*/
	}

	private RaceBusiness getRaceBiz(IWContext iwc) {
		RaceBusiness business = null;
		try {
			business = (RaceBusiness) IBOLookup.getServiceInstance(iwc, RaceBusiness.class);
		}
		catch (IBOLookupException e) {
			business = null;
		}
		return business;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#beforeGroupRemove(com.idega.user.data.Group)
	 */
	public void beforeGroupRemove(Group group, Group parentGroup) throws RemoveException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#beforeUserRemove(com.idega.user.data.User)
	 */
	public void beforeUserRemove(User user, Group parentGroup) throws RemoveException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getGroupPropertiesTabs(com.idega.user.data.Group)
	 */
	public List getGroupPropertiesTabs(Group group) throws RemoteException {
		if (MSIConstants.GROUP_TYPE_RACE_EVENT.equals(group.getGroupType())) {
			List list = new ArrayList();
			list.add(new RaceEventTab());
			return list;
		}
		if (MSIConstants.GROUP_TYPE_SEASON.equals(group.getGroupType())) {
			List list = new ArrayList();
			list.add(new RunYearTab());
			return list;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getUserPropertiesTabs(com.idega.user.data.User)
	 */
	public List getUserPropertiesTabs(User user) throws RemoteException {
		List list = new ArrayList();
		list.add(new UserRunTab());
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#instanciateEditor(com.idega.user.data.Group)
	 */
	public PresentationObject instanciateEditor(Group group) throws RemoteException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#instanciateViewer(com.idega.user.data.Group)
	 */
	public PresentationObject instanciateViewer(Group group) throws RemoteException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#isUserAssignableFromGroupToGroup(com.idega.user.data.User,
	 *      com.idega.user.data.Group, com.idega.user.data.Group)
	 */
	public String isUserAssignableFromGroupToGroup(User user, Group sourceGroup, Group targetGroup) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#isUserSuitedForGroup(com.idega.user.data.User,
	 *      com.idega.user.data.Group)
	 */
	public String isUserSuitedForGroup(User user, Group targetGroup) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getMainToolbarElements()
	 */
	public List getMainToolbarElements() throws RemoteException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#getGroupToolbarElements(com.idega.user.data.Group)
	 */
	public List getGroupToolbarElements(Group group) throws RemoteException {
		if (MSIConstants.GROUP_TYPE_RACE.equals(group.getGroupType())) {
			List list = new ArrayList(1);
			list.add(new CreateYearWindowPlugin());
			return list;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.user.business.UserGroupPlugInBusiness#canCreateSubGroup(com.idega.user.data.Group,java.lang.String)
	 */
	public String canCreateSubGroup(Group group, String groupTypeOfSubGroup) throws RemoteException {
		return null;
	}
}