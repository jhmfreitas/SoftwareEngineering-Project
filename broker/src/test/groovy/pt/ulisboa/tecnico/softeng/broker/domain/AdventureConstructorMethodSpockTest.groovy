package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.AdventureData
import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import spock.lang.Shared
import spock.lang.Unroll

class AdventureConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def broker
    def client

    @Override
    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF, BROKER_IBAN,
                new ActivityInterface(), new HotelInterface(), new CarInterface(), new BankInterface(), new TaxInterface())
    }

    @Unroll('success #label: #begin, #end, #age, #margin')
    def 'success'() {
        given: 'a client'
        client = getClientWithAge(age)

        when: 'an adventure is created'
        def adventure = new Adventure(broker, begin, end, client, margin, room, vehicle)

        then: 'all its attributes are correctly set'
        adventure.getBroker() == broker
        with(adventure) {
            getBroker() == broker
            getBegin() == begin
            getEnd() == end
            getClient() == client
            getMargin() == margin
            getBookRoom() == room
            getRentVehicle() == vehicle
            getAge() == age
            getIban().equals(iban)

            getPaymentConfirmation() == null
            getActivityConfirmation() == null
            getRoomConfirmation() == null
        }
        broker.getAdventureSet().contains(adventure)

        where:
        begin | end   | margin | age | room                      | vehicle                          | label
        BEGIN | END   | MARGIN | AGE | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.MOTORCYCLE | 'normal'
        BEGIN | BEGIN | MARGIN | AGE | Adventure.BookRoom.NONE   | Adventure.RentVehicle.MOTORCYCLE | 'begin begin'
        BEGIN | END   | 1      | AGE | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.MOTORCYCLE | 'margin 1'
        BEGIN | END   | MARGIN | 18  | Adventure.BookRoom.SINGLE | Adventure.RentVehicle.CAR        | '18 years old'
        BEGIN | END   | MARGIN | 100 | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.MOTORCYCLE | '100 years old'
        BEGIN | END   | MARGIN | AGE | Adventure.BookRoom.NONE   | Adventure.RentVehicle.MOTORCYCLE | 'no room'
        BEGIN | END   | MARGIN | AGE | Adventure.BookRoom.SINGLE | Adventure.RentVehicle.NONE       | 'no vehicle'
    }

    @Unroll('#label')
    def 'invalid arguments'() {
        given: 'a client'
        client = getClientWithAge(age)

        when: 'an adventure is created with invalid arguments'
        new Adventure(brok, begin, end, client, margin, room, vehicle)

        then: 'an exception is thrown'
        thrown(BrokerException)

        where:
        brok   | begin | end                | age | margin | room                      | vehicle                   | label
        null   | BEGIN | END                | 20  | MARGIN | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.CAR | 'broker is null'
        broker | null  | END                | 20  | MARGIN | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.CAR | 'begin date is null'
        broker | BEGIN | null               | 20  | MARGIN | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.CAR | 'end date is null'
        broker | BEGIN | BEGIN.minusDays(1) | 20  | MARGIN | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.CAR | 'end date before begin date'
        broker | BEGIN | BEGIN              | 20  | MARGIN | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.CAR | 'end date begin begin date and non null room'
        broker | BEGIN | END                | 17  | MARGIN | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.CAR | 'client is 17 years old'
        broker | BEGIN | END                | -1  | MARGIN | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.CAR | 'client is null'
        broker | BEGIN | END                | 20  | 0      | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.CAR | 'margin is zero'
        broker | BEGIN | END                | 20  | -100   | Adventure.BookRoom.DOUBLE | Adventure.RentVehicle.CAR | 'margin is negative'
    }

    def getClientWithAge(def age) {
        if (age != -1)
            return new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, age)
        else
            return null
    }
}
