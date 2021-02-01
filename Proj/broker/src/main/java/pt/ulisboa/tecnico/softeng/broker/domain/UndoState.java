package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.*;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.*;

public class UndoState extends UndoState_Base {

    @Override
    public State getValue() {
        return State.UNDO;
    }

    @Override
    public void process() {
        ActivityInterface activityInterface = getAdventure().getBroker().getActivityInterface();
        HotelInterface hotelInterface = getAdventure().getBroker().getHotelInterface();
        CarInterface carInterface = getAdventure().getBroker().getCarInterface();
        BankInterface bankInterface = getAdventure().getBroker().getBankInterface();
        TaxInterface taxInterface = getAdventure().getBroker().getTaxInterface();

        if (getAdventure().shouldCancelPayment()) {
            try {
                getAdventure()
                        .setPaymentCancellation(bankInterface.cancelPayment(getAdventure().getPaymentConfirmation()));
            } catch (BankException | RemoteAccessException ex) {
                // does not change state
            }
        }

        if (getAdventure().shouldCancelActivity()) {
            try {
                getAdventure().setActivityCancellation(
                        activityInterface.cancelReservation(getAdventure().getActivityConfirmation()));
            } catch (ActivityException | RemoteAccessException ex) {
                // does not change state
            }
        }

        if (getAdventure().shouldCancelRoom()) {
            try {
                getAdventure().setRoomCancellation(hotelInterface.cancelBooking(getAdventure().getRoomConfirmation()));
            } catch (HotelException | RemoteAccessException ex) {
                // does not change state
            }
        }

        if (getAdventure().shouldCancelVehicleRenting()) {
            try {
                getAdventure()
                        .setRentingCancellation(carInterface.cancelRenting(getAdventure().getRentingConfirmation()));
            } catch (CarException | RemoteAccessException ex) {
                // does not change state
            }
        }

        if (getAdventure().shouldCancelInvoice()) {
            try {
                taxInterface.cancelInvoice(getAdventure().getInvoiceReference());
                getAdventure().setInvoiceCancelled(true);
            } catch (TaxException | RemoteAccessException ex) {
                // does not change state
            }
        }

        if (getAdventure().roomIsCancelled() && getAdventure().activityIsCancelled()
                && getAdventure().paymentIsCancelled() && getAdventure().rentingIsCancelled()
                && getAdventure().invoiceIsCancelled()) {
            getAdventure().setState(State.CANCELLED);
        }
    }

}
