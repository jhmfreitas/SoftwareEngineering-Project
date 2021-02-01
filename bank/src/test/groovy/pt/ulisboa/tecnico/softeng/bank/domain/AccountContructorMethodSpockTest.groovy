package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class AccountContructorMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def bank
    @Shared def client
    @Shared def allien


    @Override
    def populate4Test() {
        bank = new Bank('Money', 'BK01')
        client = new Client(bank, 'António')
        allien = new Client(new Bank('MoneyPlus', 'BK02'), 'António')
    }

    def 'success'() {
        when: 'creating an sourceAccount'
        def account = new Account(bank, client)

        then: 'all info in the object is correct'
        with(account) {
            getBank() == bank
            getIban().startsWith(bank.getCode())
            getClient() == client
            0.0d == getBalance()
        }
        bank.getAccountSet().size() == 1
        bank.getClientSet().contains(client)
    }

    @Unroll('#label for Account: #bnk, #clt')
    def 'throwing exception'() {
        when: 'when creating an invalid sourceAccount'
        new Account(bnk, clt)

        then: 'throw an exception'
        thrown(BankException)

        where:
        bnk  | clt    | label
        null | client | 'null bank'
        bank | null   | 'null client'
        bank | allien | 'not client of bank'
    }
}
