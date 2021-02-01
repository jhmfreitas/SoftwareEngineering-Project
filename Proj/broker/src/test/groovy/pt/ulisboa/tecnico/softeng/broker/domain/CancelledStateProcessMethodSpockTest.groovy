package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import spock.lang.Unroll

class CancelledStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {

    def activityInterface
    def bankInterface
    def hotelInterface
    def carInterface
    def broker
    def client
    def adventure

    @Override
    def populate4Test() {

        activityInterface = Mock(ActivityInterface)
        bankInterface = Mock(BankInterface)
        hotelInterface = Mock(HotelInterface)
        carInterface = Mock(CarInterface)

        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF, BROKER_IBAN,
                activityInterface, hotelInterface, carInterface, bankInterface,
                new TaxInterface())

        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        adventure = new Adventure(broker, BEGIN, END, client, MARGIN, Adventure.BookRoom.DOUBLE, Adventure.RentVehicle.CAR)
        adventure.setState(Adventure.State.CANCELLED)

    }

    def 'did not payed'() {

        when: 'a next step is processed'
        adventure.process()

        then: 'the adventure state remains in cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'get operation data from bank interface runs 0 times'
        0 * bankInterface.getOperationData(_)
        and: 'get activity reservation data from activity interface runs 0 times'
        0 * activityInterface.getActivityReservationData(_)
        and: 'get room booking data from hotel interface runs 0 times'
        0 * hotelInterface.getRoomBookingData(_)
        and: 'get car renting data from car interface runs 0 times'
        0 * carInterface.getRentingData(_)
    }

    @Unroll('#expection is thrown')
    def 'cancelled payment first exception'() {

        given: 'that the hotel interface throws an exception'
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> { throw mock_exception }

        and: 'payment confirmation and payment cancellation are set'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)

        when: 'a next step is processed'
        adventure.process()

        then: 'the adventure state progresses to cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'get activity reservation data from activity interface runs 0 times'
        0 * activityInterface.getActivityReservationData(_)
        and: 'get room booking data from hotel interface runs 0 times'
        0 * hotelInterface.getRoomBookingData(_)
        and: 'get car renting data from car interface runs 0 times'
        0 * carInterface.getRentingData(_)

        where:
        mock_exception              | exception
        new BankException()         | 'BankException'
        new RemoteAccessException() | 'RemoteAccessException'

    }

    @Unroll('#expection is thrown')
    def 'cancelled payment second exception'() {

        given: 'that the hotel interface throws an exception in second'
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> { new RestBankOperationData() } >> {
            throw mock_expection
        }
        and: 'payment confirmation and payment cancellation are set'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)

        when: 'a next step is processed'
        adventure.process()
        then: 'the adventure state progresses to cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'get activity reservation data from activity interface runs 0 times'
        0 * activityInterface.getActivityReservationData(_)
        and: 'get room booking data from hotel interface runs 0 times'
        0 * hotelInterface.getRoomBookingData(_)
        and: 'get car renting data from car interface runs 0 times'
        0 * carInterface.getRentingData(_)

        where:
        mock_expection              | expection
        new BankException()         | 'BankException'
        new RemoteAccessException() | 'RemoteAccessException'

    }

    def 'cancelled payment'() {
        given: 'the bank interface payment operation data'
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        bankInterface.getOperationData(PAYMENT_CANCELLATION)
        and: 'payment confirmation is set'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        when: 'a next step is processed'
        adventure.process()
        then: 'the adventure state progresses to cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'get activity reservation data from activity interface runs 0 times'
        0 * activityInterface.getActivityReservationData(_)
        and: 'get room booking data from hotel interface runs 0 times'
        0 * hotelInterface.getRoomBookingData(_)
        and: 'get car renting data from car interface runs 0 times'
        0 * carInterface.getRentingData(_)
    }

    def 'cancelled activity'() {
        given: 'the bank interface payment operation data'
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        bankInterface.getOperationData(PAYMENT_CANCELLATION)
        and: 'the activity reservation data for the activity cancellation'
        activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)
        and: 'payment confirmation and cancellation are set'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'activity confirmation is set'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        when: 'a next step is processed'
        adventure.process()
        then: 'the adventure state progresses to cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'get room booking data from hotel interface runs 0 times'
        0 * hotelInterface.getRoomBookingData(_)
        and: 'get car renting data from car interface runs 0 times'
        0 * carInterface.getRentingData(_)
    }


    def 'cancelled room'() {
        given: 'the bank interface payment operation data'
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        bankInterface.getOperationData(PAYMENT_CANCELLATION)
        and: 'the activity reservation data for the activity cancellation'
        activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)
        and: 'the booking data for the room cancellation'
        hotelInterface.getRoomBookingData(ROOM_CANCELLATION)
        and: 'payment confirmation and cancellation are set'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'activity confirmation and cancellation are set'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'the room confirmation is set'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        when: 'a next step is processed'
        adventure.process()
        then: 'the adventure state progresses to cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'get car renting data from car interface runs 0 times'
        0 * carInterface.getRentingData(_)
    }

    def 'cancelled renting'() {
        given: 'the bank interface payment operation data'
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        bankInterface.getOperationData(PAYMENT_CANCELLATION)
        and: 'the activity reservation data for the activity cancellation'
        activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)
        and: 'the renting data for the renting cancellation'
        carInterface.getRentingData(RENTING_CANCELLATION)
        and: 'payment confirmation and cancellation are set'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'activity confirmation and cancellation are set'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'the renting confirmation is set'
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        when: 'a next step is processed'
        adventure.process()
        then: 'the adventure state progresses to cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
        and: 'get room booking data from hotel interface runs 0 times'
        0 * hotelInterface.getRoomBookingData(_)
    }

    def 'cancelled book and renting'() {
        given: 'the bank interface payment operation data'
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        bankInterface.getOperationData(PAYMENT_CANCELLATION)
        and: 'the activity reservation data for the activity cancellation'
        activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)
        and: 'the booking data for the room cancellation'
        hotelInterface.getRoomBookingData(ROOM_CANCELLATION)
        and: 'the renting data for the renting cancellation'
        carInterface.getRentingData(RENTING_CANCELLATION)
        and: 'payment confirmation and cancellation are set'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        and: 'activity confirmation and cancellation are set'
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        and: 'room confirmation and cancellation are set'
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        when: 'a next step is processed'
        adventure.process()
        then: 'the adventure state progresses to cancelled'
        adventure.getState().getValue() == Adventure.State.CANCELLED
    }

}