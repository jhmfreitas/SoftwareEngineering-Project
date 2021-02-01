package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class ClientContructorMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def CLIENT_NAME = 'António'
    @Shared def bank

    @Override
    def populate4Test() {
        bank = new Bank('Money', 'BK01')
    }

    def 'success'() {
        when: 'creating a clint with appropriate arguments'
        def client = new Client(bank, CLIENT_NAME)

        then: 'is successful and the object client has the proper values'
        client.getName() == CLIENT_NAME
        client.getId().length() >= 1
        bank.getClientSet().contains(client)
    }

    @Unroll('creating client: #label')
    def 'exception'() {
        when: 'when creating an invalid client'
        new Client(bnk, name)

        then: 'throw an exception'
        thrown(BankException)

        where:
        bnk  | name        | label
        null | CLIENT_NAME | 'null bank'
        bank | null        | 'null client name'
        bank | '   '       | 'blank client name'
        bank | ''          | 'empty client name'
    }
}
