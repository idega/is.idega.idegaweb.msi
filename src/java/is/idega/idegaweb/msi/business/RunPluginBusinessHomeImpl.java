/*
 * $Id: RunPluginBusinessHomeImpl.java,v 1.1 2007/06/07 22:54:35 palli Exp $
 * Created on Dec 7, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.msi.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2007/06/07 22:54:35 $ by $Author: palli $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public class RunPluginBusinessHomeImpl extends IBOHomeImpl implements RunPluginBusinessHome {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -1746756879795834752L;

	protected Class getBeanInterfaceClass() {
		return RunPluginBusiness.class;
	}

	public RunPluginBusiness create() throws javax.ejb.CreateException {
		return (RunPluginBusiness) super.createIBO();
	}
}
