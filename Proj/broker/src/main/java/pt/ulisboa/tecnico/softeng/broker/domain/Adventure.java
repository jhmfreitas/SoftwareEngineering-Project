package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;

public class Adventure extends Adventure_Base {
    public static final int SCALE = 1000;

    public enum BookRoom {
        NONE, SINGLE, DOUBLE
    }

    public enum RentVehicle {
        NONE, CAR, MOTORCYCLE
    }

    public enum State {
        PROCESS_PAYMENT, RESERVE_ACTIVITY, BOOK_ROOM, RENT_VEHICLE, UNDO, CONFIRMED, CANCELLED, TAX_PAYMENT
    }

    public Adventure(Broker broker, LocalDate begin, LocalDate end, Client client, long margin, BookRoom bookRoom, RentVehicle rentVehicle) {
        checkArguments(broker, begin, end, client, margin, bookRoom, rentVehicle);

        setID(broker.getCode() + broker.getCounter());
        setBegin(begin);
        setEnd(end);
        setMargin(margin);
        setClient(client);
        setBookRoom(bookRoom);
        setRentVehicle(rentVehicle);
        broker.addAdventure(this);
        setBroker(broker);

        setCurrentAmount(0);
        setTime(DateTime.now());

        setState(State.RESERVE_ACTIVITY);
    }

    void delete() {
        setBroker(null);
        setClient(null);

        getState().delete();

        deleteDomainObject();
    }

    private void checkArguments(Broker broker, LocalDate begin, LocalDate end, Client client, double margin, BookRoom bookRoom, RentVehicle rentVehicle) {
        if (client == null || broker == null || begin == null || end == null || bookRoom == null || rentVehicle == null) {
            throw new BrokerException();
        }

        if (end.isBefore(begin)) {
            throw new BrokerException();
        }

        if (client.getAge() < 18 || client.getAge() > 100) {
            throw new BrokerException();
        }

        if (margin <= 0 || margin > 100 * SCALE) {
            throw new BrokerException();
        }

        if (bookRoom != BookRoom.NONE && begin.equals(end)) {
            throw new BrokerException();
        }
    }

    public int getAge() {
        return getClient().getAge();
    }

    public String getIban() {
        return getClient().getIban();
    }

    void incAmountToPay(long toPay) {
        setCurrentAmount(getCurrentAmount() + toPay);
    }

    long getAmount() {
        return Math.round(getCurrentAmount() / SCALE * (1 + new Double(getMargin()) / SCALE) * SCALE);
    }

    public void setState(State state) {
        if (getState() != null) {
            getState().delete();
        }

        switch (state) {
            case RESERVE_ACTIVITY:
                setState(new ReserveActivityState());
                break;
            case BOOK_ROOM:
                setState(new BookRoomState());
                break;
            case RENT_VEHICLE:
                setState(new RentVehicleState());
                break;
            case PROCESS_PAYMENT:
                setState(new ProcessPaymentState());
                break;
            case TAX_PAYMENT:
                setState(new TaxPaymentState());
                break;
            case UNDO:
                setState(new UndoState());
                break;
            case CONFIRMED:
                setState(new ConfirmedState());
                break;
            case CANCELLED:
                setState(new CancelledState());
                break;
            default:
                new BrokerException();
                break;
        }
    }

    public void process() {
        getState().process();
    }

    boolean shouldCancelRoom() {
        return getRoomConfirmation() != null && getRoomCancellation() == null;
    }

    boolean roomIsCancelled() {
        return !shouldCancelRoom();
    }

    boolean shouldCancelActivity() {
        return getActivityConfirmation() != null && getActivityCancellation() == null;
    }

    boolean activityIsCancelled() {
        return !shouldCancelActivity();
    }

    boolean shouldCancelPayment() {
        return getPaymentConfirmation() != null && getPaymentCancellation() == null;
    }

    boolean paymentIsCancelled() {
        return !shouldCancelPayment();
    }

    boolean shouldCancelVehicleRenting() {
        return getRentingConfirmation() != null && getRentingCancellation() == null;
    }

    boolean rentingIsCancelled() {
        return !shouldCancelVehicleRenting();
    }

    boolean shouldCancelInvoice() {
        return getInvoiceReference() != null && !getInvoiceCancelled();
    }

    boolean invoiceIsCancelled() {
        return !shouldCancelInvoice();
    }

}
