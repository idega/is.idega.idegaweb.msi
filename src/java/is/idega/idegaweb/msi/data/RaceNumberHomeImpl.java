package is.idega.idegaweb.msi.data;


import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class RaceNumberHomeImpl extends IDOFactory implements RaceNumberHome {
	public Class getEntityInterfaceClass() {
		return RaceNumber.class;
	}

	public RaceNumber create() throws CreateException {
		return (RaceNumber) super.createIDO();
	}

	public RaceNumber findByPrimaryKey(Object pk) throws FinderException {
		return (RaceNumber) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((RaceNumberBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllNotInUseByType(RaceType raceType)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((RaceNumberBMPBean) entity)
				.ejbFindAllNotInUseByType(raceType);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	public int countAllNotInUseByType(RaceType raceType){
		try{
			RaceNumberBMPBean entity = (RaceNumberBMPBean) this.idoCheckOutPooledEntity();
			return entity.countAllNotInUseByType(raceType);
		}catch (Exception e) {
			getLog().log(Level.WARNING, "Failed counting race numbers", e);
		}
		return 0;
	}
	public Collection getMxInUseWithoutUser(int start, int max){
		try{
			RaceNumberBMPBean entity = (RaceNumberBMPBean) this.idoCheckOutPooledEntity();
			Collection ids = entity.getMxInUseWithoutUser(start, max);
			return findByPrimaryKeyCollection(ids);
		}catch (FinderException e) {
		}catch (Exception e) {
			getLog().log(Level.WARNING, "Failed counting race numbers", e);
		}
		return Collections.EMPTY_LIST;
	}
	public Collection getSnocrossInUseWithoutUser(int start, int max){
		try{
			RaceNumberBMPBean entity = (RaceNumberBMPBean) this.idoCheckOutPooledEntity();
			Collection ids = entity.getSnocrossInUseWithoutUser(start, max);
			return findByPrimaryKeyCollection(ids);
		}catch (FinderException e) {
		}catch (Exception e) {
			getLog().log(Level.WARNING, "Failed counting race numbers", e);
		}
		return Collections.EMPTY_LIST;
	}
	public int getMaxNumberByType(RaceType raceType){
		try{
			RaceNumberBMPBean entity = (RaceNumberBMPBean) this.idoCheckOutPooledEntity();
			Object pk = entity.getMaxNumberByType(raceType);
			RaceNumber rn = findByPrimaryKey(pk);
			return Integer.valueOf(rn.getRaceNumber()).intValue();
		}catch (Exception e) {
			getLog().log(Level.WARNING, "Failed counting race numbers", e);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceNumberHome#findAllByType(is.idega.idegaweb.msi.data.RaceType)
	 */
	@Override
	public Collection<RaceNumber> findAllByType(RaceType raceType) {
		RaceNumberBMPBean entity = (RaceNumberBMPBean) idoCheckOutPooledEntity();
		Collection<Integer> ids = entity.ejbFindAllByType(raceType);

		try {
			return getEntityCollectionForPrimaryKeys(ids);
		} catch (FinderException e) {
			getLog().log(Level.WARNING, 
					"Failed to get entities by primary keys: " + ids);
		}

		return Collections.emptyList();
	}

	public RaceNumber findByRaceNumber(int raceNumber, RaceType raceType)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((RaceNumberBMPBean) entity).ejbFindByRaceNumber(
				raceNumber, raceType);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}