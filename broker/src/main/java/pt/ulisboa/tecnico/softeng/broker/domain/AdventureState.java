package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;

public abstract class AdventureState extends AdventureState_Base {

	void incNumOfRemoteErrors() {
		setNumOfRemoteErrors(getNumOfRemoteErrors() + 1);
	}

	void resetNumOfRemoteErrors() {
		setNumOfRemoteErrors(0);
	}

	public abstract State getValue();

	public abstract void process();

	public void delete() {
		setAdventure(null);

		deleteDomainObject();
	}
}