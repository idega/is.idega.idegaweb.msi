/*
 * $Id: CreateYearWindowPlugin.java,v 1.1 2007/06/07 22:54:34 palli Exp $
 * Created on Sep 1, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.msi.presentation;

import java.util.Map;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.user.app.ToolbarElement;


/**
 * 
 *  Last modified: $Date: 2007/06/07 22:54:34 $ by $Author: palli $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class CreateYearWindowPlugin implements ToolbarElement {
	
	private final static String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.msi";

	/* (non-Javadoc)
	 * @see com.idega.user.app.ToolbarElement#getButtonImage(com.idega.presentation.IWContext)
	 */
	public Image getButtonImage(IWContext iwc) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.app.ToolbarElement#isButton(com.idega.presentation.IWContext)
	 */
	public boolean isButton(IWContext iwc) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.app.ToolbarElement#getName(com.idega.presentation.IWContext)
	 */
	public String getName(IWContext iwc) {
		IWBundle bundle = iwc.getApplicationContext().getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
		IWResourceBundle resourceBundle = bundle.getResourceBundle(iwc);
		return resourceBundle.getLocalizedString("button.generate_year", "Generate Year Group");
	}

	/* (non-Javadoc)
	 * @see com.idega.user.app.ToolbarElement#getPresentationObjectClass(com.idega.presentation.IWContext)
	 */
	public Class getPresentationObjectClass(IWContext iwc) {
		return CreateYearWindow.class;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.app.ToolbarElement#getParameterMap(com.idega.presentation.IWContext)
	 */
	public Map getParameterMap(IWContext iwc) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.app.ToolbarElement#isValid(com.idega.presentation.IWContext)
	 */
	public boolean isValid(IWContext iwc) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.idega.user.app.ToolbarElement#getPriority(com.idega.presentation.IWContext)
	 */
	public int getPriority(IWContext iwc) {
		return 4;
	}
}
