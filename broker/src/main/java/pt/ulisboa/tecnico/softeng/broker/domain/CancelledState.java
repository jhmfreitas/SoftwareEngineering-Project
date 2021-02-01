package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.*;

public class CancelledState extends CancelledState_Base {
    @Override
    public State getValue() {
        return State.CANCELLED;
    }

    @Override
    public void process() {
        ActivityInterface activityInterface = getAdventure().getBroker().getActivityInterface();
        CarInterface carInterface = getAdventure().getBroker().getCarInterface();
        BankInterface bankInterface = getAdventure().getBroker().getBankInterface();

        if (getAdventure().getPaymentCancellation() != null) {
            try {
                bankInterface.getOperationData(getAdventure().getPaymentConfirmation());
            } catch (BankException | RemoteAccessException e) {
                return;
            }

            try {
                bankInterface.getOperationData(getAdventure().getPaymentCancellation());
            } catch (BankException | RemoteAccessException e) {
                return;
            }
        }

        if (getAdventure().getActivityCancellation() != null) {
            try {
                activityInterface.getActivityReservationData(getAdventure().getActivityCancellation());
            } catch (ActivityException | RemoteAccessException e) {
                return;
            }
        }

        if (getAdventure().getRoomCancellation() != null) {
            try {
                getAdventure().getBroker().getHotelInterface().getRoomBookingData(getAdventure().getRoomCancellation());
            } catch (HotelException | RemoteAccessException e) {
                return;
            }
        }

        if (getAdventure().getRentingCancellation() != null) {
            try {
                carInterface.getRentingData(getAdventure().getRentingCancellation());
            } catch (CarException | RemoteAccessException e) {
                return;
            }
        }

    }

}
