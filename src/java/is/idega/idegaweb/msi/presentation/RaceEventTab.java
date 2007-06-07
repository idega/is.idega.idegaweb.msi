/*
 * Created on Aug 17, 2004
 *
 */
package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.util.MSIConstants;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.TextInput;
import com.idega.user.presentation.UserGroupTab;
import com.idega.util.LocaleUtil;


/**
 * @author birna
 *
 */
public class RaceEventTab extends UserGroupTab{
	
	private static final String PARAMETER_PRICE = "price";

	private TextInput price;

	private Text priceText;
	
	public RaceEventTab() {
		super();
		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);

		setName(iwrb.getLocalizedString("msi.race_event_name", "Race event info"));
	}

	/* (non-Javadoc)
	 * @see com.idega.util.datastructures.Collectable#collect(com.idega.presentation.IWContext)
	 */
	public boolean collect(IWContext iwc) {
		if (iwc != null) {
			String priceISK = iwc.getParameter(PARAMETER_PRICE);

			if (priceISK != null) {
				this.fieldValues.put(PARAMETER_PRICE, new Float(priceISK));
			}

			updateFieldsDisplayStatus();
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.user.presentation.UserTab#initFieldContents()
	 */
	public void initFieldContents() {
		try {
			RaceEvent distance = ConverterUtility.getInstance().convertGroupToRaceEvent(new Integer(getGroupId()));
			
			this.fieldValues.put(PARAMETER_PRICE, new Float(0.0f));//distance.getPrice(LocaleUtil.getIcelandicLocale())));

			updateFieldsDisplayStatus();
		}
		catch (Exception e) {
			System.err.println("RaceEventTab error initFieldContents, GroupId : " + getGroupId());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.idega.user.presentation.UserTab#initializeFieldNames()
	 */
	public void initializeFieldNames() {
	}
	
	/* (non-Javadoc)
	 * @see com.idega.user.presentation.UserTab#initializeFields()
	 */
	public void initializeFields() {
		this.price = new TextInput(PARAMETER_PRICE);
		this.price.setAsFloat("Not a valid price");
	}
	
	/* (non-Javadoc)
	 * @see com.idega.user.presentation.UserTab#initializeFieldValues()
	 */
	public void initializeFieldValues() {
		this.fieldValues.put(PARAMETER_PRICE, new Float(0));
	}
	
	/* (non-Javadoc)
	 * @see com.idega.user.presentation.UserTab#initializeTexts()
	 */
	public void initializeTexts() {
		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		this.priceText = new Text(iwrb.getLocalizedString("msi.price", "Price"));
		this.priceText.setBold();		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.user.presentation.UserTab#lineUpFields()
	 */
	public void lineUpFields() {
		resize(1, 1);
		setCellpadding(0);
		setCellspacing(0);
		
		Table table = new Table(2, 4);
		table.setCellpadding(5);
		table.setCellspacing(0);
		table.setWidth(1, "50%");
		table.setWidth(2, "50%");
		
		table.setWidth(Table.HUNDRED_PERCENT);
		table.add(this.priceText, 1, 1);
		table.add(Text.getBreak(), 1, 1);
		table.add(this.price, 1, 1);
				
		add(table, 1, 1);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.util.datastructures.Collectable#store(com.idega.presentation.IWContext)
	 */
	public boolean store(IWContext iwc) {
		try {
			if (getGroupId() > -1) {
				RaceEvent distance = ConverterUtility.getInstance().convertGroupToRaceEvent(new Integer(getGroupId()));
				
				//distance.setPrice(((Float) this.fieldValues.get(PARAMETER_PRICE)).floatValue());

				distance.store();
			}
		}
		catch (Exception e) {
			//return false;
			e.printStackTrace(System.err);
			throw new RuntimeException("update group exception");
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.user.presentation.UserTab#updateFieldsDisplayStatus()
	 */
	public void updateFieldsDisplayStatus() {
		this.price.setContent(((Float) this.fieldValues.get(PARAMETER_PRICE)).toString());
	}
	
	public String getBundleIdentifier() {
		return MSIConstants.IW_BUNDLE_IDENTIFIER;
	}
}