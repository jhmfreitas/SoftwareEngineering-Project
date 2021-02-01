package pt.ulisboa.tecnico.softeng.hotel.services.local

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Processor
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import spock.lang.Unroll

class HotelInterfaceCancelBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
    def NIF_BUYER = "123456789"
    def IBAN_BUYER = "IBAN_BUYER"
    def ARRIVAL = new LocalDate(2016, 12, 19)
    def DEPARTURE = new LocalDate(2016, 12, 21)
    def hotel
    def room
    def booking

    def taxInterface
    def bankInterface

    def hotelInterface = new HotelInterface()

    @Override
    def populate4Test() {
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)
        def processor = new Processor(bankInterface, taxInterface)
        hotel = new Hotel("XPTO123", "Paris", "NIF", "IBAN",
                20, 30, processor)
        room = new Room(hotel, "01", Type.DOUBLE)
        booking = room.reserve(Type.DOUBLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)
    }

    def 'success'() {
        when: 'a booking is cancelled'
        def cancel = HotelInterface.cancelBooking(booking.getReference())

        then:
        booking.isCancelled()
        booking.getCancellation().equals(cancel)
    }

    @Unroll('invalid #reference')
    def 'invalid arguments'() {
        when: 'a booking is cancelled'
        HotelInterface.cancelBooking(reference)

        then: 'throws an exception'
        thrown(HotelException)

        where:
        reference | label
        'XPTO'    | 'reference does not exist'
        null      | 'null reference'
        ''        | 'empty reference'
        '   '     | 'bank reference'
    }

    def 'success integration'() {
        when: 'when cancelling an existing booking'
        def cancel = hotelInterface.cancelBooking(booking.getReference())

        then: 'booking is cancelled and gets a cancellation reference'
        1 * taxInterface.cancelInvoice(_)
        with(booking) {
            isCancelled()
            getCancellation() == cancel
        }
    }

    def 'does not exist integration'() {
        when: 'when cancelling a non-existing booking'
        hotelInterface.cancelBooking('XPTO')

        then: 'an exception is thrown'
        0 * taxInterface.cancelInvoice(_)
        thrown(HotelException)
    }
}
