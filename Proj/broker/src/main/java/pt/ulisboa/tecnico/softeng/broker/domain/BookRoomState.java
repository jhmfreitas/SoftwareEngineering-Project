package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

public class BookRoomState extends BookRoomState_Base {
    public static final int MAX_REMOTE_ERRORS = 10;

    @Override
    public State getValue() {
        return State.BOOK_ROOM;
    }

    @Override
    public void process() {
        RestRoomBookingData bookingData = getAdventure().getBroker().getRoomBookingFromBulkBookings(getAdventure().getBookRoom().name(), getAdventure().getBegin(), getAdventure().getEnd());

        if (bookingData == null) {
            HotelInterface hotelInterface = getAdventure().getBroker().getHotelInterface();
            try {
                bookingData = hotelInterface.reserveRoom(new RestRoomBookingData(getAdventure().getBookRoom(),
                        getAdventure().getBegin(), getAdventure().getEnd(), getAdventure().getBroker().getNif(),
                        getAdventure().getBroker().getIban(), getAdventure().getID()));
            } catch (HotelException he) {
                getAdventure().setState(State.UNDO);
                return;
            } catch (RemoteAccessException rae) {
                incNumOfRemoteErrors();
                if (getNumOfRemoteErrors() == MAX_REMOTE_ERRORS) {
                    getAdventure().setState(State.UNDO);
                }
                return;
            }
        }

        getAdventure().setRoomConfirmation(bookingData.getReference());
        getAdventure().incAmountToPay(bookingData.getPrice());


        if (getAdventure().getRentVehicle() != Adventure.RentVehicle.CAR.NONE) {
            getAdventure().setState(State.RENT_VEHICLE);
        } else {
            getAdventure().setState(State.PROCESS_PAYMENT);
        }
    }

}
