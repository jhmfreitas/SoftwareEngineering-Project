package pt.ulisboa.tecnico.softeng.hotel.domain

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class RoomConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared
    def hotel

    @Override
    public Object populate4Test() {
        hotel = new Hotel('XPTO123', 'Lisboa', 'NIF', 'IBAN', 20, 30, new Processor(new BankInterface(), new TaxInterface()))
    }

    def 'success'() {
        when: 'create a room'
        Room room = new Room(hotel, '01', Type.DOUBLE)

        then: 'all fieds are correctly set'
        hotel == room.getHotel()
        room.getNumber().equals('01')
        room.getType() == Type.DOUBLE
        hotel.getRoomSet().size() == 1
    }

    @Unroll('one of the arguments is invalid: #hotel | #number | #type')
    def 'incorrect input arguments'() {
        when: 'create a room with incorrect arguments'
        new Room(ht, number, type)

        then: 'a HotelException is thrown'
        def error = thrown(HotelException)

        where:
        ht    | number  | type
        null  | '01'    | Type.DOUBLE
        hotel | null    | Type.DOUBLE
        hotel | ''      | Type.DOUBLE
        hotel | '     ' | Type.DOUBLE
        hotel | 'JOSE'  | Type.DOUBLE
        hotel | '01'    | null
    }

    def 'not unique room number'() {
        given: 'a room'
        new Room(hotel, '01', Type.SINGLE);

        when: 'another room is created with the same number'
        new Room(hotel, '01', Type.DOUBLE)

        then: 'a HotelException is thrown'
        def error = thrown(HotelException)
        hotel.getRoomSet().size() == 1
    }
}
