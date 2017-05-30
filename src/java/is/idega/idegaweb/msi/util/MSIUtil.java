package is.idega.idegaweb.msi.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.business.DefaultSpringBean;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserBusiness;
import com.idega.util.CoreConstants;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

@Service(MSIUtil.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class MSIUtil extends DefaultSpringBean {

	public static final String BEAN_NAME = "msiUtil";
	
	public static String[] getParsedNames(String allNames) {
		if (StringUtil.isEmpty(allNames)) {
			return null;
		}
		
		String[] names = new String[3];
		
		String[] parsedNames = allNames.split("\\s+");
		if (parsedNames.length == 1) {
			names[0] = parsedNames[0];
			names[1] = null;
			names[2] = null;
			return names;
		} else if (parsedNames.length == 2) {
			names[0] = parsedNames[0];
			names[1] = null;
			names[2] = parsedNames[1];
			return names;
		} else if (parsedNames.length == 3) {
			return parsedNames;
		}
		
		return null;
	}
	
	public static boolean isPersonalId(IWContext iwc, String value) {
		if (StringUtil.isEmpty(value)) {
			return false;
		}

		String personalId = StringHandler.replace(value, CoreConstants.MINUS, CoreConstants.EMPTY);
		
		if (!StringHandler.isNumeric(personalId)) {
			return false;
		}
		
		if (getUserBusiness(iwc).validatePersonalId(personalId)) {
			return true;
		}

		return false;
	}
	
	public static UserBusiness getUserBusiness(IWApplicationContext iwac) {
		try {
			return (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
}
