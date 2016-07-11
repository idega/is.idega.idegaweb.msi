package is.idega.idegaweb.msi.data;


import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;

public class RaceNumberHomeImpl extends IDOFactory implements RaceNumberHome {

	private static final long serialVersionUID = -3456492343873610116L;

	private RaceTypeHome getRaceTypeHome() {
		try {
			return (RaceTypeHome) IDOLookup.getHome(RaceType.class);
		} catch (IDOLookupException e) {
			getLog().log(Level.WARNING, 
					"Failed to get " + RaceTypeHome.class + " cause of: ", e);
		}

		return null;
	}

	public Class getEntityInterfaceClass() {
		return RaceNumber.class;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceNumberHome#create()
	 */
	public RaceNumber create() {
		try {
			return (RaceNumber) super.createIDO();
		} catch (CreateException e) {
			getLog().log(Level.WARNING, "Failed to create entity: ", e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceNumberHome#findByPrimaryKey(java.lang.Object)
	 */
	public RaceNumber findByPrimaryKey(Object pk) {
		if (pk != null) {
			try {
				return (RaceNumber) super.findByPrimaryKeyIDO(pk);
			} catch (FinderException e) {
				getLog().log(Level.WARNING, "Failed to get entity by id: " + pk);
			}
		}

		return null;
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

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceNumberHome#findByRaceNumber(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public RaceNumber findByRaceNumber(Integer raceNumber, Integer raceTypeId) {
		RaceNumberBMPBean entity = (RaceNumberBMPBean) idoCheckOutPooledEntity();
		if (raceNumber != null && raceTypeId != null) {
			return findByPrimaryKey(entity.ejbFindByRaceNumber(raceNumber, raceTypeId));
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceNumberHome#findByRaceNumber(int, is.idega.idegaweb.msi.data.RaceType)
	 */
	public RaceNumber findByRaceNumber(int raceNumber, RaceType raceType) {
		if (raceType != null) {
			return findByRaceNumber(raceNumber, (Integer) raceType.getPrimaryKey());
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceNumberHome#update(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.Boolean, java.lang.Boolean, java.lang.Integer)
	 */
	@Override
	public RaceNumber update(
			Integer primaryKey, 
			Integer raceNumber, 
			Date applicationDate, 
			Date approvalDate, 
			Boolean approved, 
			Boolean inUse, 
			Integer raceTypeId) {
		RaceNumber entity = findByPrimaryKey(primaryKey);
		if (entity == null) {
			entity = findByRaceNumber(raceNumber, raceTypeId);
		}

		if (entity == null) {
			entity = create();
		}

		if (approved != null) {
			entity.setIsApproved(approved);
		}

		if (inUse != null) {
			entity.setIsInUse(inUse);
		}

		if (raceTypeId != null) {
			entity.setRaceType(getRaceTypeHome().findByPrimaryKey(raceTypeId));
		}

		if (applicationDate != null) {
			entity.setApplicationDate(new Timestamp(applicationDate.getTime()));
		}

		if (approvalDate != null) {
			entity.setApprovedDate(new Timestamp(approvalDate.getTime()));
		}

		if (raceNumber != null) {
			entity.setRaceNumber(raceNumber.toString());
		}

		try {
			entity.store();
			return entity;
		} catch (Exception e) {
			getLog().log(Level.WARNING, "Failed to save entity, cause of: ", e);
		}

		return null;
	}

	@Override
	public RaceNumber update(Integer primaryKey, Integer raceNumber, 
			Date applicationDate, Date approvalDate, 
			Boolean approved, Boolean inUse, RaceType raceType) {
		return update(
				primaryKey, 
				raceNumber, 
				applicationDate, 
				approvalDate, 
				approved, inUse, 
				raceType != null ? (Integer) raceType.getPrimaryKey() : null);
	}
}