package is.idega.idegaweb.msi.data;


import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOFactory;
import com.idega.data.IDOStoreException;
import com.idega.util.StringUtil;

public class RaceCategoryHomeImpl extends IDOFactory implements
		RaceCategoryHome {

	private static final long serialVersionUID = 1296012008782827607L;

	public Class getEntityInterfaceClass() {
		return RaceCategory.class;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceCategoryHome#create()
	 */
	@Override
	public RaceCategory create() {
		try {
			return (RaceCategory) super.createIDO();
		} catch (CreateException e) {
			getLog().log(Level.WARNING, 
					"Failed to create new entity, cause of: ", e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceCategoryHome#findByPrimaryKey(java.lang.Object)
	 */
	@Override
	public RaceCategory findByPrimaryKey(Object pk) {
		if (pk != null) {
			try {
				return (RaceCategory) super.findByPrimaryKeyIDO(pk);
			} catch (FinderException e) {
				getLog().log(Level.WARNING, 
						"Failed to get entity by primary key: " + pk);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceCategoryHome#findAll()
	 */
	@Override
	public Collection<RaceCategory> findAll() {
		RaceCategoryBMPBean entity = (RaceCategoryBMPBean) idoCheckOutPooledEntity();
		try {
			return getEntityCollectionForPrimaryKeys(entity.ejbFindAll());
		} catch (FinderException e) {
			getLog().log(Level.WARNING, "Failed to get entities by primary keys");
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceCategoryHome#findByName(java.lang.String)
	 */
	@Override
	public RaceCategory findByName(String name) {
		if (!StringUtil.isEmpty(name)) {
			RaceCategoryBMPBean entity = (RaceCategoryBMPBean) idoCheckOutPooledEntity();
			return findByPrimaryKey(entity.ebjFindByName(name));
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.msi.data.RaceCategoryHome#update(java.lang.Integer, java.lang.String)
	 */
	@Override
	public RaceCategory update(Integer primaryKey, String localizationKey, boolean publishEvent) {
		RaceCategory entity = findByPrimaryKey(primaryKey);
		if (entity == null) {
			entity = findByName(localizationKey);
		}
		
		if (entity == null) {
			entity = create();
		}

		if (!StringUtil.isEmpty(localizationKey)) {
			entity.setCategoryKey(localizationKey);
		}

		entity.setNotification(publishEvent);
		
		try {
			entity.store();
			return entity;
		} catch (IDOStoreException e) {
			getLog().log(Level.WARNING, "Failed to store entity, cause of: ", e);
		}

		return null;
	}
}