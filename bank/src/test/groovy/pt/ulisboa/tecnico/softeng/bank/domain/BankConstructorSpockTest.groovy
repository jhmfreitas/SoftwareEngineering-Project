package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class BankConstructorSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def BANK_CODE = 'BK01'
    @Shared def BANK_NAME = 'Money'

    @Override
    def populate4Test() {}

    def 'success'() {
        when: 'creating a  bank sourceAccount'
        def bank = new Bank(BANK_NAME, BANK_CODE)

        then: 'should all be ok, with the correct values'
        with(bank) {
            getName() == BANK_NAME
            getCode() == BANK_CODE
            getAccountSet().size() == 0
            getClientSet().size() == 0
        }
        FenixFramework.getDomainRoot().getBankSet().size() == 1
    }

    @Unroll('creating bank: #label')
    def 'exception'() {
        when: 'when creating an invalid bank'
        new Bank(name, code)

        then: 'throw an exception'
        thrown(BankException)

        where:
        name      | code      | label
        null      | BANK_CODE | 'null name'
        '   '     | BANK_CODE | 'blank name'
        BANK_NAME | null      | 'null code'
        BANK_NAME | '    '    | 'blank code'
        BANK_NAME | 'BK0'     | 'short code'
        BANK_NAME | 'BK011'   | 'long code'
    }

    def 'not unique code'() {
        given: 'given the fact that one bank sourceAccount was created'
        new Bank(BANK_NAME, BANK_CODE)

        when: 'creating another entity with the same code'
        new Bank(BANK_NAME, BANK_CODE)

        then: 'should through an exception'
        def error = thrown(BankException)
        FenixFramework.getDomainRoot().getBankSet().size() == 1
    }
}
