/*
 * $Id: RunPluginBusinessHome.java,v 1.1 2007/06/07 22:54:35 palli Exp $
 * Created on Dec 7, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.msi.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2007/06/07 22:54:35 $ by $Author: palli $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public interface RunPluginBusinessHome extends IBOHome {

	public RunPluginBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
