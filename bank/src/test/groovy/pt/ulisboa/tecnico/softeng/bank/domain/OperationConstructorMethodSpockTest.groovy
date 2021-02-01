package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class OperationConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def bank
    @Shared def sourceAccount
    @Shared def targetAccount
    @Shared def TRANSACTION_SOURCE = 'transactionSource'
    @Shared def TRANSACTION_REFERENCE = 'transactionReference'

    @Override
    def populate4Test() {
        bank = new Bank('Money', 'BK01')
        def client = new Client(bank, 'António')
        sourceAccount = new Account(bank, client)
        targetAccount = new Account(bank, client)
    }

    def 'success deposit operation'() {
        when: 'when creating a deposit operation'
        def operation = new DepositOperation()
        operation.init(sourceAccount, 1000)

        then: 'the object should hold the proper values'
        with(operation) {
            getReference().startsWith(bank.getCode())
            getReference().length() > Bank.CODE_SIZE
            getType() == Type.DEPOSIT
            getSourceAccount() == sourceAccount
            getTargetAccount() == null
            getSourceIban() == sourceAccount.getIban()
            getTargetIban() == null
            getTransactionSource() == null
            getTransactionReference() == null
            getValue() == 1000
            getTime() != null
        }
        bank.getOperation(operation.getReference()) == operation
        bank.getOperationSet().size() == 1
    }

    def 'success withdraw operation'() {
        when: 'when creating withdraw operation'
        def operation = new WithdrawOperation()
        operation.init(sourceAccount, 1000)

        then: 'the object should hold the proper values'
        with(operation) {
            getReference().startsWith(bank.getCode())
            getReference().length() > Bank.CODE_SIZE
            getType() == Type.WITHDRAW
            getSourceAccount() == sourceAccount
            getTargetAccount() == null
            getSourceIban() == sourceAccount.getIban()
            getTargetIban() == null
            getTransactionSource() == null
            getTransactionReference() == null
            getValue() == 1000
            getTime() != null
        }
        bank.getOperation(operation.getReference()) == operation
        bank.getOperationSet().size() == 1
    }

    def 'success transfer operation between accounts of the same bank'() {
        given: 'a withdraw operation'
        def withdrawOperation = new WithdrawOperation()
        withdrawOperation.init(sourceAccount, 1000)
        and: 'a deposit operation'
        def depositOperation = new DepositOperation()
        depositOperation.init(targetAccount, 1000)

        when: 'when creating a tranfer operation'
        def transferOperation = new TransferOperation()
        transferOperation.init(withdrawOperation, depositOperation, TRANSACTION_SOURCE, TRANSACTION_REFERENCE)

        then: 'the object should hold the proper values'
        with(transferOperation) {
            getReference().startsWith(bank.getCode())
            getReference().length() > Bank.CODE_SIZE
            getType() == Type.TRANSFER
            getSourceAccount() == sourceAccount
            getTargetAccount() == targetAccount
            getSourceIban() == sourceAccount.getIban()
            getTargetIban() == targetAccount.getIban()
            getTransactionSource() == TRANSACTION_SOURCE
            getTransactionReference() == TRANSACTION_REFERENCE
            getValue() == 1000
            getTime() != null
        }
        bank.getOperation(transferOperation.getReference()) == transferOperation
        bank.getOperationSet().size() == 3

    }

    def 'success transfer operation between accounts of different banks'() {
        given: 'a new bank, client and account'
        def newBank = new Bank('Money++', 'BK02')
        def client = new Client(newBank, 'José')
        def newSourceAccount = new Account(newBank, client)
        'a withdraw operation'
        def withdrawOperation = new WithdrawOperation()
        withdrawOperation.init(newSourceAccount, 1000)
        and: 'a deposit operation'
        def depositOperation = new DepositOperation()
        depositOperation.init(targetAccount, 1000)

        when: 'when creating a tranfer operation'
        def transferOperation = new TransferOperation()
        transferOperation.init(withdrawOperation, depositOperation, TRANSACTION_SOURCE, TRANSACTION_REFERENCE)

        then: 'the object should hold the proper values'
        with(transferOperation) {
            getReference().startsWith('BK02')
            getReference().length() > Bank.CODE_SIZE
            getType() == Type.TRANSFER
            getSourceAccount() == newSourceAccount
            getTargetAccount() == targetAccount
            getSourceIban() == newSourceAccount.getIban()
            getTargetIban() == targetAccount.getIban()
            getTransactionSource() == TRANSACTION_SOURCE
            getTransactionReference() == TRANSACTION_REFERENCE
            getValue() == 1000
            getTime() != null
        }
        newBank.getOperation(transferOperation.getReference()) == transferOperation
        newBank.getOperationSet().size() == 2
        bank.getOperation(transferOperation.getReference()) == null
        bank.getOperationSet().size() == 1

    }

    @Unroll('single operation: #type, #acc, #value')
    def 'exception for single operations'() {
        when: 'when creating an invalid operation'
        createSingleOperation(type, acc, value)

        then: 'throw an exception'
        thrown(BankException)

        where:
        type          | acc           | value
        Type.WITHDRAW | null          | 1000
        Type.DEPOSIT  | sourceAccount | 0
        Type.DEPOSIT  | sourceAccount | -1000
        Type.WITHDRAW | sourceAccount | 0
        Type.WITHDRAW | sourceAccount | -1000
    }

    def 'one amount'() {
        when:
        def operation = new DepositOperation()
        operation.init(sourceAccount, 1)

        then:
        bank.getOperation(operation.getReference()) == operation
    }

    @Unroll('transfer operation: ')
    def 'exception for transfer operation'() {
        when: 'when creating an invalid transfer operation'
        def transferOperation = new TransferOperation()
        transferOperation.init(createSingleOperation(withdraw), createSingleOperation(deposit), ts, tr)

        then: 'throw an exception'
        thrown(BankException)

        where:
        withdraw      | deposit      | ts                 | tr                    | label
        null          | Type.DEPOSIT | TRANSACTION_SOURCE | TRANSACTION_REFERENCE | 'null withdraw operation'
        Type.WITHDRAW | null         | TRANSACTION_SOURCE | TRANSACTION_REFERENCE | 'null withdraw operation'
        Type.WITHDRAW | Type.DEPOSIT | null               | TRANSACTION_REFERENCE | 'null withdraw operation'
        Type.WITHDRAW | Type.DEPOSIT | '   '              | TRANSACTION_REFERENCE | 'null withdraw operation'
        Type.WITHDRAW | Type.DEPOSIT | ''                 | TRANSACTION_REFERENCE | 'null withdraw operation'
        Type.WITHDRAW | Type.DEPOSIT | TRANSACTION_SOURCE | null                  | 'null withdraw operation'
        Type.WITHDRAW | Type.DEPOSIT | TRANSACTION_SOURCE | '     '               | 'null withdraw operation'
        Type.WITHDRAW | Type.DEPOSIT | TRANSACTION_SOURCE | ''                    | ' null withdraw operation '

    }

    def createSingleOperation(type, acc, value) {
        switch (type) {
            case Type.DEPOSIT:
                DepositOperation depositOperation = new DepositOperation()
                depositOperation.init(acc, value)
                return depositOperation
            case Type.WITHDRAW:
                WithdrawOperation withdrawOperation = new WithdrawOperation()
                withdrawOperation.init(acc, value)
                return withdrawOperation;
            default:
                return null
        }
    }

    def createSingleOperation(type) {
        switch (type) {
            case Type.DEPOSIT:
                DepositOperation depositOperation = new DepositOperation()
                depositOperation.init(sourceAccount, 1000)
                return depositOperation
            case Type.WITHDRAW:
                WithdrawOperation withdrawOperation = new WithdrawOperation()
                withdrawOperation.init(sourceAccount, 1000)
                return withdrawOperation;
            default:
                return null
        }
    }
}
