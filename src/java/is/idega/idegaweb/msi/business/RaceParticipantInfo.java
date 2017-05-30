/*
 * $Id: RaceParticipantInfo.java,v 1.3 2008/05/21 09:04:17 palli Exp $ Created on May 16, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.msi.business;

import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.data.RaceVehicleType;

import com.idega.user.data.User;

/**
 * A holder class for information about runners and their selection when
 * registering.
 *
 * Last modified: $Date: 2008/05/21 09:04:17 $ by $Author: palli $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public class RaceParticipantInfo {

	private User user;
	private Race race;
	private RaceEvent event;

	private String chipNumber;

	private String email;
	private String homePhone;
	private String mobilePhone;
	private boolean agree;
	private float amount;
	private float seasonPrice = 0;

	private boolean rentChip = false;
	private boolean ownChip = false;

	private String raceNumber;
	private RaceVehicleType raceVehicle;
	private String sponsors;

	private RaceVehicleType raceVehicleSubtype;
	private String engine;
	private String engineCC;
	private String model;
	private String team;
	private String bodyNumber;

	private String comment;

	private String partner1;
	private String partner2;

	private User firstPartner;
	private User secondPartner;

	private boolean rentTimeTransmitter;

	public boolean isRentTimeTransmitter() {
		return rentTimeTransmitter;
	}

	public void setRentTimeTransmitter(boolean rentTimeTransmitter) {
		this.rentTimeTransmitter = rentTimeTransmitter;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Race getRace() {
		return this.race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public RaceEvent getEvent() {
		return this.event;
	}

	public void setEvent(RaceEvent event) {
		this.event = event;
	}

	public String getChipNumber() {
		return this.chipNumber;
	}

	public void setChipNumber(String chipNumber) {
		this.chipNumber = chipNumber;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHomePhone() {
		return this.homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getMobilePhone() {
		return this.mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public boolean isAgree() {
		return this.agree;
	}

	public void setAgree(boolean agree) {
		this.agree = agree;
	}

	public float getAmount() {
		return this.amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getRaceNumber() {
		return this.raceNumber;
	}

	public void setRaceNumber(String raceNumber) {
		this.raceNumber = raceNumber;
	}

	public RaceVehicleType getRaceVehicle() {
		return this.raceVehicle;
	}

	public void setRaceVehicle(RaceVehicleType raceVehicle) {
		this.raceVehicle = raceVehicle;
	}

	public String getSponsors() {
		return this.sponsors;
	}

	public void setSponsors(String sponsors) {
		this.sponsors = sponsors;
	}

	public boolean getRentChip() {
		return this.rentChip;
	}

	public void setRentChip(boolean rentChip) {
		this.rentChip = rentChip;
		this.ownChip = !rentChip;
	}

	public boolean getOwnChip() {
		return this.ownChip;
	}

	public void setOwnChip(boolean ownChip) {
		this.ownChip = ownChip;
		this.rentChip = !ownChip;
	}

	public RaceVehicleType getRaceVehicleSubtype() {
		return this.raceVehicleSubtype;
	}

	public void setRaceVehicleSubtype(RaceVehicleType subtype) {
		this.raceVehicleSubtype = subtype;
	}

	public String getEngine() {
		return this.engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getEngineCC() {
		return this.engineCC;
	}

	public void setEngineCC(String cc) {
		this.engineCC = cc;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getTeam() {
		return this.team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getBodyNumber() {
		return this.bodyNumber;
	}

	public void setBodyNumber(String bodyNumber) {
		this.bodyNumber = bodyNumber;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPartner1() {
		return partner1;
	}

	public void setPartner1(String partner1) {
		this.partner1 = partner1;
	}

	public String getPartner2() {
		return partner2;
	}

	public void setPartner2(String partner2) {
		this.partner2 = partner2;
	}

	public float getSeasonPrice() {
		return seasonPrice;
	}

	public void setSeasonPrice(float seasonPrice) {
		this.seasonPrice = seasonPrice;
	}

	public User getFirstPartner() {
		return firstPartner;
	}
	
	public void setFirstPartner(User firstPartner) {
		this.firstPartner = firstPartner;
	}

	public User getSecondPartner() {
		return secondPartner;
	}

	public void setSecondPartner(User secondPartner) {
		this.secondPartner = secondPartner;
	}
	
}