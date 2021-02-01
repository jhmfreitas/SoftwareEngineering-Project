package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class OperationRevertMethodSpockTest extends SpockRollbackTestAbstractClass {
    def TRANSACTION_SOURCE = 'transactionSource'
    def TRANSACTION_REFERENCE = 'transactionReference'
    def bank
    def account
    def targetAccount

    @Override
    def populate4Test() {
        bank = new Bank('Money', 'BK01')
        def client = new Client(bank, 'António')
        account = new Account(bank, client)
        targetAccount = new Account(bank, client)
        targetAccount.deposit(100)
    }

    def 'revert deposit'() {
        given: 'a deposit operation'
        def reference = account.deposit(100).getReference()
        def operation = bank.getOperation(reference)

        when: 'when reverting the deposit'
        def newReference = operation.revert()

        then: 'sourceAccount should have balance as before'
        account.getBalance() == 0

        and: 'a new operation is added'
        bank.getOperation(newReference) != null

        and: 'the initial operation is not removed'
        bank.getOperation(reference) != null
    }

    def 'revert withdraw'() {
        given: 'given a deposit operation'
        account.deposit(1000)
        def reference = this.account.withdraw(100).getReference()
        def operation = this.bank.getOperation(reference)

        when: 'when reverting the operation'
        def newReference = operation.revert()

        then: 'sourceAccount should have the balance as before'
        1000 == this.account.getBalance()

        and: 'a new operation is added'
        this.bank.getOperation(newReference) != null

        and: 'the initial operation is not removed'
        this.bank.getOperation(reference) != null
    }

    def 'revert transfer'() {
        given: 'a withdraw operation'
        def withdrawOperation = new WithdrawOperation()
        withdrawOperation.init(account, 100)
        and: 'a deposit operation'
        def depositOperation = new DepositOperation()
        depositOperation.init(targetAccount, 100)
        and: 'a transfer operation'
        def transferOperation = new TransferOperation()
        transferOperation.init(withdrawOperation, depositOperation, TRANSACTION_SOURCE, TRANSACTION_REFERENCE)

        when: 'when reverting the operation'
        def newReference = transferOperation.revert()

        then: 'sourceAccount should have the balance as before'
        this.account.getBalance() == 100
        this.targetAccount.getBalance() == 0

        and: 'a new operation transfer operation is added'
        TransferOperation revertedOperation = this.bank.getOperation(newReference)
        revertedOperation.getReference() != null
        revertedOperation.getTransactionSource() == 'REVERT'
        revertedOperation.getTransactionReference() == transferOperation.getReference()

        and: 'the initial operation is not removed'
        this.bank.getOperation(transferOperation.getReference()) != null
    }

    def 'revert reverted transfer'() {
        given: 'a withdraw operation'
        def withdrawOperation = new WithdrawOperation()
        withdrawOperation.init(account, 100)
        and: 'a deposit operation'
        def depositOperation = new DepositOperation()
        depositOperation.init(targetAccount, 100)
        and: 'a transfer operation'
        def transferOperation = new TransferOperation()
        transferOperation.init(withdrawOperation, depositOperation, TRANSACTION_SOURCE, TRANSACTION_REFERENCE)
        and: 'reverted'
        def newReference = transferOperation.revert()
        TransferOperation revertedOperation = this.bank.getOperation(newReference)

        when: 'when reverting the operation'
        revertedOperation.revert()

        then: 'a bank exception is thrown'
        thrown(BankException)
    }
}

