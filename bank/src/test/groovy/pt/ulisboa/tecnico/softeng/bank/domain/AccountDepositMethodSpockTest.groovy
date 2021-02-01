package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountDepositMethodSpockTest extends SpockRollbackTestAbstractClass {
    def bank
    def account

    @Override
    def populate4Test() {
        bank = new Bank('Money', 'BK01')
        def client = new Client(bank, 'António')

        account = new Account(bank, client)
    }

    @Unroll('success deposit, #label: #amnt, #balance')
    def 'success'() {
        when: 'when depositing an amount to an sourceAccount'
        String reference = account.deposit(amnt).getReference()

        then: 'the sourceAccount is updated appropriately'
        amnt == account.getBalance()
        Operation operation = bank.getOperation(reference)
        operation != null
        operation.getType() == Operation.Type.DEPOSIT
        operation.getSourceAccount() == account
        balance == operation.getValue()

        where:
        label        | amnt | balance
        'fifty'      | 50   | 50
        'one amount' | 1    | 1
    }

    @Unroll('Deposit: #label')
    def 'throwing exception'() {
        when: 'when deposit an invalid amount'
        account.deposit(amnt)

        then: 'throw an exception'
        thrown(BankException)

        where:
        amnt | label
        0    | 'zero amount'
        -100 | 'negative amount'
    }
}
