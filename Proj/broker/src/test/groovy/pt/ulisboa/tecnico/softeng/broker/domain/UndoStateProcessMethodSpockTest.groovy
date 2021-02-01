package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.*
import spock.lang.Unroll

class UndoStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
    def activityInterface
    def hotelInterface
    def carInterface
    def bankInterface
    def taxInterface
    def broker
    def client
    def adventure

    def populate4Test() {
        activityInterface = Mock(ActivityInterface)
        hotelInterface = Mock(HotelInterface)
        carInterface = Mock(CarInterface)
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF, BROKER_IBAN,
                activityInterface, hotelInterface, carInterface, bankInterface, taxInterface)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN, Adventure.BookRoom.DOUBLE, Adventure.RentVehicle.CAR)

        adventure.setState(Adventure.State.UNDO)
    }

    def 'success revert payment'() {
        given: 'a bank cancel payment succeeds'
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION
        and: 'the adventure has a payment confirmation'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure is cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'the adventure has a payment cancellation'
        adventure.getPaymentCancellation() == PAYMENT_CANCELLATION
    }

    @Unroll('#exception exception')
    def 'fail revert payment #exception exception'() {
        given: 'a bank cancel payment throws an exception'
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> { throw mock_exception }
        and: 'the adventure has a payment confirmation'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change state'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure payment cancellation is null'
        adventure.getPaymentCancellation() == null

        where:
        mock_exception              | exception
        new BankException()         | 'BankException'
        new RemoteAccessException() | 'RemoteAccessException'
    }

    def 'success revert activity'() {
        given: 'an activity cancel reservation succeeds'
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has an activity confirmation'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure is cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'the adventure has a activity cancellation token'
        adventure.getActivityCancellation() == ACTIVITY_CANCELLATION
    }

    @Unroll('#mock_exception exception')
    def 'fail revert activity due to #mock_exception exception'() {
        given: 'an activity cancel reservation throws an exception'
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> { throw mock_exception }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has an activity confirmation'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure activity cancellation token is null'
        adventure.getActivityCancellation() == null

        where:
        mock_exception              | exception
        new ActivityException()     | 'ActivityException'
        new RemoteAccessException() | 'RemoteAccessException'
    }

    def 'success revert room booking'() {
        given: 'a hotel cancel booking succeeds'
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has a room confirmation'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state changes to cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'the adventure has a room cancellation token'
        adventure.getRoomCancellation() == ROOM_CANCELLATION
    }

    @Unroll('#exception exception')
    def 'success revert room booking #exception exception'() {
        given: 'a hotel cancel booking throws an exception'
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> { throw mock_exception }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has a room confirmation'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure room cancellation token is null'
        adventure.getRoomCancellation() == null

        where:
        mock_exception              | exception
        new HotelException()        | 'HotelException'
        new RemoteAccessException() | 'RemoteAccessException'
    }

    def 'success revert rent car'() {
        given: 'a car cancel renting succeeds'
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has cancelled room'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        and: 'has a car renting confirmation'
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state changes to cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'the adventure has a renting cancellation token'
        adventure.getRentingCancellation() == RENTING_CANCELLATION
    }

    @Unroll('#exception exception')
    def 'fail revert rent car #exception exception'() {
        given: 'a car cancel renting throws a car exception'
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> { throw mock_exception }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has cancelled room'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        and: 'has a car renting confirmation'
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the adventure renting cancellation token is null'
        adventure.getRentingCancellation() == null

        where:
        mock_exception              | exception
        new CarException()          | 'CarException'
        new RemoteAccessException() | 'RemoteAccessException'
    }

    def 'success cancel invoice'() {
        given: 'an invoice cancel succeeds'
        taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has cancelled room'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        and: 'has cancelled car renting'
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CANCELLATION)
        and: 'has a invoice reference'
        adventure.setInvoiceReference(INVOICE_REFERENCE)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state is cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'the invoice is cancelled'
        adventure.getInvoiceCancelled()
    }

    @Unroll('#mock_exception exception')
    def 'fail cancel invoice #mock_exception exception'() {
        given: 'an invoice cancel throws an exception'
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw mock_exception }
        and: 'the adventure has cancelled the payment'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'has cancelled activity'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'has cancelled room'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        and: 'has cancelled car renting'
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CANCELLATION)
        and: 'has a invoice reference'
        adventure.setInvoiceReference(INVOICE_REFERENCE)

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure state does not change'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the invoice is not cancelled'
        !adventure.getInvoiceCancelled()

        where:
        mock_exception              | exception
        new TaxException()          | 'TaxException'
        new RemoteAccessException() | 'RemoteAccessException'
    }

}
