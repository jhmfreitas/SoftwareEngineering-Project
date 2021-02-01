package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException
import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import spock.lang.Shared
import spock.lang.Unroll

class ClientConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared
    def broker

    @Override
    def populate4Test() {
        broker = new Broker('BR01', 'eXtremeADVENTURE', BROKER_NIF, BROKER_IBAN,
                new ActivityInterface(), new HotelInterface(), new CarInterface(), new BankInterface(), new TaxInterface())
    }

    def 'success'() {
        when: 'a client is created'
        def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        then: 'it set its attribute values'
        client.getIban().equals(CLIENT_IBAN)
        client.getNif().equals(CLIENT_NIF)
        client.getDrivingLicense().equals(DRIVING_LICENSE)
        client.getAge() == AGE
    }

    @Unroll('invalid arguments: #label')
    def 'invalid arguments'() {
        when: 'a client is created'
        new Client(brok, iban, nif, license, age)

        then: 'it throws an exception'
        thrown(BrokerException)

        where: 'the values are invalid'
        brok   | iban        | nif        | license         | age | label
        null   | CLIENT_IBAN | CLIENT_NIF | DRIVING_LICENSE | AGE | 'the broker is null'
        broker | null        | CLIENT_NIF | DRIVING_LICENSE | AGE | 'the sourceIban is null'
        broker | '     '     | CLIENT_NIF | DRIVING_LICENSE | AGE | 'the sourceIban is blank'
        broker | ''          | CLIENT_NIF | DRIVING_LICENSE | AGE | 'the sourceIban is empty'
        broker | CLIENT_IBAN | null       | DRIVING_LICENSE | AGE | 'the nif is null'
        broker | CLIENT_IBAN | '    '     | DRIVING_LICENSE | AGE | 'the nif is blank'
        broker | CLIENT_IBAN | ''         | DRIVING_LICENSE | AGE | 'the nif is empty'
        broker | CLIENT_IBAN | CLIENT_NIF | '      '        | AGE | 'the license is blank'
        broker | CLIENT_IBAN | CLIENT_NIF | '  '            | AGE | 'the license is empty'
        broker | CLIENT_IBAN | CLIENT_NIF | DRIVING_LICENSE | -1  | 'negative age'
    }

    def 'null driving license'() {
        when: 'a client is created with a null license'
        def client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, null, AGE);

        then: 'its attributes are correctly set'
        client.getIban().equals(CLIENT_IBAN)
        client.getNif().equals(CLIENT_NIF)
        client.getDrivingLicense() == null
        client.getAge() == AGE
    }

    def 'no client exists with same nif'() {
        given: 'a client'
        def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        when: 'another client is created with the same nif'
        new Client(broker, 'OTHER_IBAN', CLIENT_NIF, DRIVING_LICENSE + '1', AGE)

        then: 'throws and exception'
        thrown(BrokerException)
        broker.getClientByNIF(CLIENT_NIF) == client
    }

    def 'clients with the same iban'() {
        given: 'a client'
        def client1 = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        when: 'another client is created with the same sourceIban'
        def client2 = new Client(broker, CLIENT_IBAN, CLIENT_NIF + "1", DRIVING_LICENSE + '1', AGE)

        then: 'both clients have the same sourceIban'
        client1.getIban() == client2.getIban()
    }
}
