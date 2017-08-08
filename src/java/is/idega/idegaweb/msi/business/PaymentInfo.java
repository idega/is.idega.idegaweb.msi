package is.idega.idegaweb.msi.business;

public class PaymentInfo {

	private String participantId;
	private String name;
	private String tournament;
	private String group;
	private String localizedDate;
	private float price;

	public PaymentInfo() {
	}

	public PaymentInfo(String participantId, String name, String tournament,
			String group, String localizedDate, float price) {
		this.participantId = participantId;
		this.name = name;
		this.tournament = tournament;
		this.group = group;
		this.localizedDate = localizedDate;
		this.price = price;
	}

	public String getParticipantId() {
		return participantId;
	}

	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTournament() {
		return tournament;
	}

	public void setTournament(String tournament) {
		this.tournament = tournament;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getLocalizedDate() {
		return localizedDate;
	}

	public void setLocalizedDate(String localizedDate) {
		this.localizedDate = localizedDate;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return this.tournament + ", " + this.group + " " + this.localizedDate;
	}

}
