package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

public class RentVehicleState extends RentVehicleState_Base {
    public static final int MAX_REMOTE_ERRORS = 5;

    @Override
    public State getValue() {
        return State.RENT_VEHICLE;
    }

    @Override
    public void process() {
        try {
            RestRentingData rentingData = getAdventure().getBroker().getCarInterface().rentCar(getAdventure().getRentVehicle(), getAdventure().getClient().getDrivingLicense(),
                    getAdventure().getBroker().getNif(), getAdventure().getBroker().getIban(),
                    getAdventure().getBegin(), getAdventure().getEnd(), getAdventure().getID());

            getAdventure().setRentingConfirmation(rentingData.getReference());
            getAdventure().incAmountToPay(rentingData.getPrice());
        } catch (CarException ce) {
            getAdventure().setState(State.UNDO);
            return;
        } catch (RemoteAccessException rae) {
            incNumOfRemoteErrors();
            if (getNumOfRemoteErrors() == MAX_REMOTE_ERRORS) {
                getAdventure().setState(State.UNDO);
            }
            return;
        }

        getAdventure().setState(State.PROCESS_PAYMENT);
    }

}
