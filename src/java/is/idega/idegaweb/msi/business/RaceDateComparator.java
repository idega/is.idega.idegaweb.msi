package is.idega.idegaweb.msi.business;

import is.idega.idegaweb.msi.data.Race;

import java.util.Comparator;

import com.idega.util.IWTimestamp;

public class RaceDateComparator implements Comparator {
	
	public RaceDateComparator() {
		super();
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		
		
		Race r1 = (Race) o1;
		Race r2 = (Race) o2;
	
		IWTimestamp t1 = new IWTimestamp(r1.getRaceDate());
		IWTimestamp t2 = new IWTimestamp(r2.getRaceDate());
		
		if (t1.isEarlierThan(t2)) {
			return -1;
		}
		
		if (t1.isLaterThan(t2)) {
			return 1;
		}
		
		return 0;
	}
}