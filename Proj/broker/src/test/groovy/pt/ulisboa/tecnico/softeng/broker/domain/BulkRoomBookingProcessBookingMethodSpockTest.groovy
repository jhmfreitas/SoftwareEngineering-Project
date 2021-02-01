package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class BulkRoomBookingProcessBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
    def hotelInterface
    def broker
    def bulk

    @Override
    def populate4Test() {
        hotelInterface = Mock(HotelInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF, BROKER_IBAN,
                new ActivityInterface(), hotelInterface, new CarInterface(), new BankInterface(), new TaxInterface())
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END)
    }

    def 'success'() {
        given: 'the hotel interface returns two references for booking'
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { new HashSet<>(Arrays.asList("ref1", "ref2")) }

        when: 'the bulk booking is processed'
        bulk.processBooking()

        then: 'the two references are stored'
        bulk.getReferences().size() == 2
    }

    def 'success twice'() {
        given: 'the hotel interface returns two references for booking'
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { new HashSet<>(Arrays.asList("ref1", "ref2")) }

        when: 'the bulk booking is processed'
        bulk.processBooking()

        then: 'the two references are stored'
        bulk.getReferences().size() == 2

        when: 'the bulk booking is processed again'
        bulk.processBooking()

        then: 'it does not request more bookings'
        0 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId())
        bulk.getReferences().size() == 2

    }

    def 'one hotel exception'() {
        when: 'the bulk booking is processed twice'
        bulk.processBooking()
        bulk.processBooking()

        then: 'the first invocation returns a hotel exception'
        1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new HotelException() }
        and: 'the second invocation returns data'
        1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { new HashSet<>(Arrays.asList("ref1", "ref2")) }
        and: 'the bulk booking is not cancelled'
        !bulk.getCancelled()
        and: 'the references are stored'
        bulk.getReferences().size() == 2
    }

    def 'max hotel exceptions'() {
        given: 'hotel exceptions are thrown'
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new HotelException() }

        when: 'processBooking is invoked max number of hotel exceptions'
        1.upto(BulkRoomBooking.MAX_HOTEL_EXCEPTIONS) { bulk.processBooking() }

        then: 'the bulk booking is cancelled'
        bulk.getCancelled()
    }

    def 'max minus one hotel exception'() {
        when: 'processBooking is invoked max number of hotel exceptions'
        1.upto(BulkRoomBooking.MAX_HOTEL_EXCEPTIONS) { bulk.processBooking() }

        then: 'the first max hotel error -1 invocations return an exception'
        (BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new HotelException() }
        and: 'the last invocation returns data'
        1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { new HashSet<>(Arrays.asList("ref1", "ref2")) }
        and: 'the bulk booking is not cancelled'
        !bulk.getCancelled()
        and: 'the references are stored'
        bulk.getReferences().size() == 2
    }

    def 'hotel exception value is reset by remote exception'() {
        when: 'processBooking is invoked max number of hotel exceptions'
        for (def i = 0; i < 2 * BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1; i++) {
            bulk.processBooking()
        }

        then: 'the first max hotel error -1 invocations return hotel exceptions'
        (BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new HotelException() }
        and: 'the next invocation return a remote access exception'
        1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new RemoteAccessException() }
        and: 'the last max hotel error -1 invocations return hotel exceptions'
        (BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new HotelException() }
        and: 'the bulk booking is not cancelled'
        !bulk.getCancelled()
    }

    def 'one remote exception'() {
        when: 'the bulk booking is processed twice'
        bulk.processBooking()
        bulk.processBooking()

        then: 'the first invocation returns a remote access exception'
        1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new RemoteAccessException() }
        and: 'the second invocation returns data'
        1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { new HashSet<>(Arrays.asList("ref1", "ref2")) }
        and: 'the bulk booking is not cancelled'
        !bulk.getCancelled()
        and: 'the references are stored'
        bulk.getReferences().size() == 2
    }

    def 'max remote exception'() {
        given: 'remote exceptions are thrown'
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new RemoteAccessException() }

        when: 'processBooking is invoked max number of hotel exceptions'
        1.upto(BulkRoomBooking.MAX_REMOTE_ERRORS) { bulk.processBooking() }

        then: 'the bulk booking is cancelled'
        bulk.getCancelled()
    }

    def 'max minus one remote exception'() {
        when: 'processBooking is invoked max number of remote exceptions'
        for (def i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS; i++) {
            bulk.processBooking()
        }

        then: 'the first max remote error -1 invocations return an exception'
        (BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new RemoteAccessException() }
        and: 'the last invocation returns data'
        1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { new HashSet<>(Arrays.asList("ref1", "ref2")) }
        and: 'the bulk booking is not cancelled'
        !bulk.getCancelled()
        and: 'the references are stored'
        bulk.getReferences().size() == 2
    }

    def 'remote exception value is reset by hotel exception'() {
        when: 'processBooking is invoked max number of remote access exceptions'
        for (def i = 0; i < 2 * BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
            bulk.processBooking()
        }

        then: 'the first max remote errors -1 invocations return remote access exceptions'
        (BulkRoomBooking.MAX_REMOTE_ERRORS - 1) * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new RemoteAccessException() }
        and: 'the next invocation return a hotel exception'
        1 * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new HotelException() }
        and: 'the last max remote access errors -1 invocations return remote access exceptions'
        (BulkRoomBooking.MAX_REMOTE_ERRORS - 1) * hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, broker.getNif(), broker.getIban(),
                bulk.getId()) >> { throw new RemoteAccessException() }
        and: 'the bulk booking is not cancelled'
        !bulk.getCancelled()
    }

}
