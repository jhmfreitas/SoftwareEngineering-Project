package pt.ulisboa.tecnico.softeng.hotel.services.local

import org.joda.time.LocalDate
import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Processor
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData

class HotelInterfaceReserveRoomMethodSpockTest extends SpockRollbackTestAbstractClass {
    def ARRIVAL = new LocalDate(2016, 12, 19)
    def DEPARTURE = new LocalDate(2016, 12, 24)
    def NIF_HOTEL = '123456789'
    def NIF_BUYER = '123456700'
    def IBAN_BUYER = 'IBAN_CUSTOMER'
    def IBAN_HOTEL = 'IBAN_HOTEL'
    def ADVENTURE_ID = 'AdventureId'

    def room
    def hotel

    @Override
    def populate4Test() {
        hotel = new Hotel('XPTO123', 'Lisboa', NIF_HOTEL, IBAN_HOTEL, 20, 30, new Processor(new BankInterface(), new TaxInterface()))
        room = new Room(hotel, '01', Room.Type.SINGLE)
    }

    def 'success'() {
        given: 'a booking data'
        def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)

        when: 'a reservation is done'
        bookingData = HotelInterface.reserveRoom(bookingData)

        then: 'a correct reference is returned'
        bookingData.getReference() != null
        bookingData.getReference().startsWith("XPTO123")
    }

    def 'no vacancy'() {
        given: 'the single room is booked'
        def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, new LocalDate(2016, 12, 25),
                NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)
        HotelInterface.reserveRoom(bookingData)

        when: 'booking during the same period'
        bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, new LocalDate(2016, 12, 25), NIF_BUYER,
                IBAN_BUYER, ADVENTURE_ID + "1")
        HotelInterface.reserveRoom(bookingData)

        then: 'throws an HotelException'
        thrown(HotelException)
    }

    def 'no hotels'() {
        given: 'there is no hotels'
        for (def hotel : FenixFramework.getDomainRoot().getHotelSet()) {
            hotel.delete()
        }
        def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, DEPARTURE, NIF_BUYER,
                IBAN_BUYER, ADVENTURE_ID)

        when: 'reserve a room'
        HotelInterface.reserveRoom(bookingData)

        then: 'throws an HotelException'
        thrown(HotelException)
    }

    def 'no rooms'() {
        given: 'there is no rooms'
        for (def room : hotel.getRoomSet()) {
            room.delete();
        }
        def bookingData = new RestRoomBookingData("SINGLE", ARRIVAL, new LocalDate(2016, 12, 25),
                NIF_BUYER, IBAN_BUYER, ADVENTURE_ID);

        when: 'reserve a room'
        HotelInterface.reserveRoom(bookingData);

        then: 'throws an HotelException'
        thrown(HotelException)
    }
}
