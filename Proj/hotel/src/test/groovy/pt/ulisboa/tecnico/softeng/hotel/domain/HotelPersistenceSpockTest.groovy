package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface

class HotelPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
    def HOTEL_NIF = '123456789'
    def HOTEL_IBAN = 'IBAN'
    def HOTEL_NAME = 'Berlin Plaza'
    def HOTEL_CODE = 'H123456'
    def ROOM_NUMBER = '01'
    def CLIENT_NIF = '123458789'
    def CLIENT_IBAN = 'IBANC'
    def ADVENTURE_ID = 'AdventureId'

    def arrival = new LocalDate(2017, 12, 15)
    def departure = new LocalDate(2017, 12, 19)

    @Override
    def whenCreateInDatabase() {
        def hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, HOTEL_NIF, HOTEL_IBAN, 10, 20, new Processor(new BankInterface(), new TaxInterface()))
        new Room(hotel, ROOM_NUMBER, Type.DOUBLE)
        hotel.reserveRoom(Type.DOUBLE, arrival, departure, CLIENT_NIF, CLIENT_IBAN, ADVENTURE_ID)
    }

    @Override
    def thenAssert() {
        assert FenixFramework.getDomainRoot().getHotelSet().size() == 1

        def hotels = new ArrayList<>(FenixFramework.getDomainRoot().getHotelSet())
        Hotel hotel = hotels.get(0)

        assert hotel.getName().equals(HOTEL_NAME)
        assert hotel.getCode().equals(HOTEL_CODE)
        assert hotel.getIban().equals(HOTEL_IBAN)
        assert hotel.getNif().equals(HOTEL_NIF)
        assert hotel.getPriceSingle() == 10
        assert hotel.getPriceDouble() == 20
        assert hotel.getRoomSet().size() == 1
        Processor processor = hotel.getProcessor();
        assert processor != null
        assert processor.getBookingSet().size() == 1

        def rooms = new ArrayList<>(hotel.getRoomSet());
        Room room = rooms.get(0);

        assert room.getNumber().equals(ROOM_NUMBER)
        assert room.getType() == Type.DOUBLE
        assert room.getBookingSet().size() == 1

        def bookings = new ArrayList<>(room.getBookingSet());
        Booking booking = bookings.get(0);

        assert booking.getReference() != null
        assert booking.getArrival() == arrival
        assert booking.getDeparture() == departure
        assert booking.getBuyerIban().equals(CLIENT_IBAN)
        assert booking.getBuyerNif().equals(CLIENT_NIF)
        assert booking.getProviderIban().equals(HOTEL_IBAN)
        assert booking.getProviderNif().equals(HOTEL_NIF)
        assert booking.getPrice() == 80
        assert booking.getRoom() == room
        assert booking.getTime() != null
        assert booking.getProcessor() != null
    }

    @Override
    def deleteFromDatabase() {
        for (def hotel : FenixFramework.getDomainRoot().getHotelSet()) {
            hotel.delete()
        }
    }
}
