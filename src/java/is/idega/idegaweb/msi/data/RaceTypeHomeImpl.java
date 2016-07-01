package is.idega.idegaweb.msi.data;


import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOFactory;
import com.idega.data.IDOStoreException;
import com.idega.util.StringUtil;

public class RaceTypeHomeImpl extends IDOFactory implements RaceTypeHome {

	private static final long serialVersionUID = 8167504236309738806L;

	public Class getEntityInterfaceClass() {
		return RaceType.class;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceTypeHome#create()
	 */
	@Override
	public RaceType create() {
		try {
			return (RaceType) super.createIDO();
		} catch (CreateException e) {
			getLog().log(Level.WARNING, 
					"Failed to create entity, cause of: ", e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceTypeHome#findByPrimaryKey(java.lang.Object)
	 */
	@Override
	public RaceType findByPrimaryKey(Object pk) {
		if (pk != null) {
			try {
				return (RaceType) super.findByPrimaryKeyIDO(pk);
			} catch (FinderException e) {
				getLog().log(Level.WARNING, 
						"Failed to get entity by primary key: " + pk);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceTypeHome#findAll()
	 */
	@Override
	public Collection<RaceType> findAll() {
		RaceTypeBMPBean entity = (RaceTypeBMPBean) idoCheckOutPooledEntity();
		Collection<Integer> ids = entity.ejbFindAll();

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
	 * @see is.idega.idegaweb.msi.data.RaceTypeHome#findByRaceType(java.lang.String)
	 */
	@Override
	public RaceType findByRaceType(String raceType) {
		RaceTypeBMPBean entity = (RaceTypeBMPBean) idoCheckOutPooledEntity();
		return findByPrimaryKey(entity.ejbFindByRaceType(raceType));
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceTypeHome#update(java.lang.Integer, java.lang.String)
	 */
	@Override
	public RaceType update(Integer primaryKey, String raceType, boolean notify) {
		RaceType entity = findByPrimaryKey(primaryKey);
		if (entity == null) {
			entity = findByRaceType(raceType);
		}

		if (entity == null) {
			entity = create();
		}

		if (!StringUtil.isEmpty(raceType)) {
			entity.setRaceType(raceType);
		}

		entity.setNotification(notify);

		try {
			entity.store();
			return entity;
		} catch (IDOStoreException e) {
			getLog().log(Level.WARNING, 
					"Failed to save entity, cause of: ", e);
		}

		return null;
	}
}