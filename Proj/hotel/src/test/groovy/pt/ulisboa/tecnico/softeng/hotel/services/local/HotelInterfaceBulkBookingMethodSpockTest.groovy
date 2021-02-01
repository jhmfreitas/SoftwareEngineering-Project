package pt.ulisboa.tecnico.softeng.hotel.services.local

import org.joda.time.LocalDate
import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Processor
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

import java.util.stream.Collectors

class HotelInterfaceBulkBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared
    def NIF_BUYER = '123456789'
    @Shared
    def IBAN_BUYER = 'IBAN_BUYER'
    @Shared
    def BULK_ID = 'BULK_ID'
    @Shared
    def ARRIVAL = new LocalDate(2016, 12, 19)
    @Shared
    def DEPARTURE = new LocalDate(2016, 12, 21)

    def hotel
    def bankInterfaceOne
    def taxInterfaceOne
    def bankInterfaceTwo
    def taxInterfaceTwo

    @Override
    def populate4Test() {
        bankInterfaceOne = Mock(BankInterface)
        taxInterfaceOne = Mock(TaxInterface)
        hotel = new Hotel('XPTO123', 'Paris', 'NIF', 'IBAN', 20, 30, new Processor(bankInterfaceOne, taxInterfaceOne))
        new Room(hotel, '01', Type.DOUBLE)
        new Room(hotel, '02', Type.SINGLE)
        new Room(hotel, '03', Type.DOUBLE)
        new Room(hotel, '04', Type.SINGLE)

        bankInterfaceTwo = Mock(BankInterface)
        taxInterfaceTwo = Mock(TaxInterface)
        hotel = new Hotel('XPTO124', 'Paris', 'NIF2', 'IBAN2', 25, 35, new Processor(bankInterfaceTwo, taxInterfaceTwo))
        new Room(hotel, '01', Type.DOUBLE)
        new Room(hotel, '02', Type.SINGLE)
        new Room(hotel, '03', Type.DOUBLE)
        new Room(hotel, '04', Type.SINGLE)
    }

    @Unroll('bulkbooking #number rooms and the reference size is #refSize')
    def 'success'() {
        given: 'the remote invocations'
        bankInterfaceOne.processPayment(_) >> "OK1"
        taxInterfaceOne.submitInvoice(_) >> true
        bankInterfaceTwo.processPayment(_) >> "OK2"
        taxInterfaceTwo.submitInvoice(_) >> true

        when: 'bulkbooking rooms'
        def references = HotelInterface.bulkBooking(number, ARRIVAL, DEPARTURE, NIF_BUYER,
                IBAN_BUYER, BULK_ID)

        then: 'references are returned'
        references.size() == refSize

        where:
        number | refSize
        2     || 2
        1     || 1
        8     || 8
    }

    def 'unsuccess'() {
        when: 'bulkbooking rooms'
        def references = HotelInterface.bulkBooking(9, ARRIVAL, DEPARTURE, NIF_BUYER,
                IBAN_BUYER, BULK_ID)

        then: 'an exception is thrown'
        thrown(HotelException)
        and: 'no rooms are booked'
        HotelInterface.getAvailableRooms(8, ARRIVAL, DEPARTURE).size() == 8
    }

    def 'no rooms'() {
        given: 'there is a single hotel and has no rooms'
        for (def hotel : FenixFramework.getDomainRoot().getHotelSet()) {
            hotel.delete()
        }
        hotel = new Hotel('XPTO124', 'Paris', 'NIF', 'IBAN', 27, 37, new Processor(Mock(BankInterface), Mock(TaxInterface)))

        when: 'doing a bulkbooking'
        HotelInterface.bulkBooking(3, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER, BULK_ID)

        then: 'a HotelException is thrown'
        thrown(HotelException)
    }

    @Unroll('invalid arguments: #number | #arrival | #departure | #nif | #iban')
    def 'invalid arguments'() {
        when: 'a bulkbooking is done with an invalid argument'
        HotelInterface.bulkBooking(number, arrival, departure, nif, iban, BULK_ID)

        then: 'a HotelException is thrown'
        thrown(HotelException)

        where:
        number | arrival | departure | nif       | iban
        -1     | ARRIVAL | DEPARTURE | NIF_BUYER | IBAN_BUYER
        0      | ARRIVAL | DEPARTURE | NIF_BUYER | IBAN_BUYER
        2      | null    | DEPARTURE | NIF_BUYER | IBAN_BUYER
        2      | ARRIVAL | null      | NIF_BUYER | IBAN_BUYER
        2      | ARRIVAL | DEPARTURE | null      | IBAN_BUYER
        2      | ARRIVAL | DEPARTURE | '  '      | IBAN_BUYER
        2      | ARRIVAL | DEPARTURE | NIF_BUYER | null
        2      | ARRIVAL | DEPARTURE | NIF_BUYER | '   '
    }

    def 'idempotent bulk booking'() {
        given: 'a bulkboooking of 4 rooms'
        def references = HotelInterface.bulkBooking(4, ARRIVAL, DEPARTURE, NIF_BUYER,
                IBAN_BUYER, BULK_ID)

        when: 'do a bulkboooking with the same id'
        def equalReferences = HotelInterface.bulkBooking(4, ARRIVAL, DEPARTURE, NIF_BUYER,
                IBAN_BUYER, BULK_ID)

        then: 'returns the same references'
        HotelInterface.getAvailableRooms(4, ARRIVAL, DEPARTURE).size() == 4
        references.stream().sorted().collect(Collectors.toList()).equals(
                equalReferences.stream().sorted().collect(Collectors.toList()))
    }
}
