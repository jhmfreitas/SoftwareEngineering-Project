package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.*
import spock.lang.Unroll

class ConfirmedStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
    def rentingData
    def roomBookingData
    def activityReservationData
    def bankData
    def activityInterface
    def hotelInterface
    def carInterface
    def bankInterface
    def taxInterface
    def broker
    def client
    def adventure

    def populate4Test() {
        activityReservationData = Mock(RestActivityBookingData)
        rentingData = Mock(RestRentingData)
        bankData = Mock(RestBankOperationData)
        roomBookingData = Mock(RestRoomBookingData)
        activityInterface = Mock(ActivityInterface)
        hotelInterface = Mock(HotelInterface)
        carInterface = Mock(CarInterface)
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)


        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF, BROKER_IBAN,
                activityInterface, hotelInterface, carInterface, bankInterface, taxInterface)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN, Adventure.BookRoom.DOUBLE, Adventure.RentVehicle.CAR)

        adventure.setState(Adventure.State.CONFIRMED)
    }


    def 'successAll'() {
        given: 'a payment, activity, renting and room confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        carInterface.getRentingData(_) >> rentingData
        hotelInterface.getRoomBookingData(_) >> roomBookingData
        and:
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        rentingData.getPaymentReference() >> REFERENCE
        rentingData.getInvoiceReference() >> REFERENCE
        roomBookingData.getPaymentReference() >> REFERENCE
        roomBookingData.getInvoiceReference() >> REFERENCE

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure is confirmed'
        Adventure.State.CONFIRMED == adventure.getState().getValue()
    }


    def 'successActivityAndHotel'() {
        given: 'a payment, activity and room confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        bankInterface.getOperationData(_) >> bankData
        hotelInterface.getRoomBookingData(_) >> roomBookingData
        and:
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        roomBookingData.getPaymentReference() >> REFERENCE
        roomBookingData.getInvoiceReference() >> REFERENCE

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure is confirmed'
        Adventure.State.CONFIRMED == adventure.getState().getValue()
    }


    def 'successActivityAndCar'() {
        given: 'a payment, activity and renting confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        bankInterface.getOperationData(_) >> bankData
        carInterface.getRentingData(_) >> rentingData
        and:
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        rentingData.getPaymentReference() >> REFERENCE
        rentingData.getInvoiceReference() >> REFERENCE

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure is confirmed'
        Adventure.State.CONFIRMED == adventure.getState().getValue()
    }


    def 'successActivity'() {
        given: 'a payment and activity confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        bankInterface.getOperationData(_) >> bankData
        and:
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure is confirmed'
        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }

    @Unroll('BankException #exception')
    def 'BankException exceptions'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> { throw mock_exception }

        when: 'the adventure is processed #processing_times times'
        1.upto(processing_times) { adventure.process() }

        then: 'with #exception, the ending state is #ending_state'
        adventure.getState().getValue() == ending_state

        where:
        exception                  | mock_exception              | processing_times                       | ending_state
        'oneBankException'         | new BankException()         | 1                                      | Adventure.State.CONFIRMED
        'maxMinusOneBankException' | new BankException()         | ConfirmedState.MAX_BANK_EXCEPTIONS - 1 | Adventure.State.CONFIRMED
        'maxBankException'         | new BankException()         | ConfirmedState.MAX_BANK_EXCEPTIONS     | Adventure.State.UNDO
        'remoteAccessException'    | new RemoteAccessException() | 1                                      | Adventure.State.CONFIRMED
    }


    @Unroll('ActivityException #exception')
    def 'ActivityExceptions'() {
        given:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> { throw mock_exception }
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when: 'the adventure is processed #processing_times times'
        1.upto(processing_times) { adventure.process() }

        then: 'with #exception, the ending state is #ending_state'
        adventure.getState().getValue() == ending_state

        where:
        exception               | mock_exception              | processing_times | ending_state
        'ActivityException'     | new ActivityException()     | 1                | Adventure.State.UNDO
        'RemoteAccessException' | new RemoteAccessException() | 1                | Adventure.State.CONFIRMED
    }

    def 'activityNoPaymentConfirmation'() {
        given: 'a payment and activity confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        bankInterface.getOperationData(_) >> bankData
        and: 'no activity payment reference'
        activityReservationData.getPaymentReference() >> null

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure goes to state undo'
        Adventure.State.UNDO == adventure.getState().getValue()
    }

    def 'activityNoInvoiceReference'() {
        given: 'a payment and activity confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        bankInterface.getOperationData(_) >> bankData
        and:
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> null

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure goes to state undo'
        Adventure.State.UNDO == adventure.getState().getValue()
    }

    @Unroll('CarException #exception')
    def 'CarExceptions'() {
        given: 'a payment, activity and renting confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        bankInterface.getOperationData(_) >> bankData
        carInterface.getRentingData(RENTING_CONFIRMATION) >> { throw mock_exception }
        and:
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE

        when: 'the adventure is processed'
        adventure.process()

        then: 'with #exception, the ending state is #ending_state'
        adventure.getState().getValue() == ending_state

        where:
        exception                     | mock_exception              | ending_state
        'CarException'                | new CarException()          | Adventure.State.UNDO
        'OneCarRemoteAccessException' | new RemoteAccessException() | Adventure.State.CONFIRMED
    }

    def 'carNoPaymentConfirmation'() {
        given: 'a payment, activity and renting confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        bankInterface.getOperationData(_) >> bankData
        carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
        and:
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        rentingData.getPaymentReference() >> null

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure goes to state undo'
        Adventure.State.UNDO == adventure.getState().getValue()
    }

    def 'carNoInvoiceReference'() {
        given: 'a payment, activity and renting confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        bankInterface.getOperationData(_) >> bankData
        carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
        and:
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        rentingData.getPaymentReference() >> REFERENCE
        rentingData.getInvoiceReference() >> null

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure goes to state undo'
        Adventure.State.UNDO == adventure.getState().getValue()
    }

    @Unroll('HotelException #exception')
    def 'HotelExceptions'() {
        given: 'a payment, activity and room confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        bankInterface.getOperationData(_) >> bankData
        hotelInterface.getRoomBookingData(ROOM_CONFIRMATION) >> { throw mock_exception }
        and:
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE

        when: 'the adventure is processed'
        adventure.process()

        then: 'with #exception, the ending state is #ending_state'
        adventure.getState().getValue() == ending_state

        where:
        exception                       | mock_exception              | ending_state
        'hotelException'                | new HotelException()        | Adventure.State.UNDO
        'OneHotelRemoteAccessException' | new RemoteAccessException() | Adventure.State.CONFIRMED
    }

    def 'hotelNoPaymentConfirmation'() {
        given: 'a payment, activity and renting confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        bankInterface.getOperationData(_) >> bankData
        hotelInterface.getRoomBookingData(_) >> roomBookingData
        and:
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        roomBookingData.getPaymentReference() >> null

        when: 'the adventure is processed'
        adventure.process()

        then: 'the adventure is undone'
        Adventure.State.UNDO == adventure.getState().getValue()
    }

    def 'hotelNoInvoiceReference'() {
        given: 'a payment, activity and renting confirmations'
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        and:
        activityInterface.getActivityReservationData(_) >> activityReservationData
        bankInterface.getOperationData(_) >> bankData
        hotelInterface.getRoomBookingData(_) >> roomBookingData
        and:
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        roomBookingData.getPaymentReference() >> REFERENCE
        roomBookingData.getInvoiceReference() >> null

        when: 'the adventure is processed'
        adventure.process()

        then:
        Adventure.State.UNDO == adventure.getState().getValue()
    }
}