package is.idega.idegaweb.msi.data;


import com.idega.data.IDOFactory;
import javax.ejb.CreateException;
import com.idega.data.IDOEntity;
import javax.ejb.FinderException;
import java.util.Collection;
import com.idega.user.data.Group;

public class ParticipantHomeImpl extends IDOFactory implements ParticipantHome {
	public Class getEntityInterfaceClass() {
		return Participant.class;
	}

	public Participant create() throws CreateException {
		return (Participant) super.createIDO();
	}

	public Participant findByPrimaryKey(Object pk) throws FinderException {
		return (Participant) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ParticipantBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllByRace(Group race) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ParticipantBMPBean) entity).ejbFindAllByRace(race);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}