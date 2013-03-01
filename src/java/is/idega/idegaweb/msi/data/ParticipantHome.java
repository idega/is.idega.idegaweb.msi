package is.idega.idegaweb.msi.data;


import com.idega.user.data.Group;
import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface ParticipantHome extends IDOHome {
	public Participant create() throws CreateException;

	public Participant findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAll() throws FinderException;

	public Collection findAllByRace(Group race) throws FinderException;
}