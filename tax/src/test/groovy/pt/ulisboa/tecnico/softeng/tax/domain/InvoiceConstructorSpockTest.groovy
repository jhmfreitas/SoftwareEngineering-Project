package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll

class InvoiceConstructorSpockTest extends SpockRollbackTestAbstractClass {
    def SELLER_NIF = '123456789'
    def BUYER_NIF = '987654321'
    def FOOD = 'FOOD'
    @Shared def VALUE = 16 * IRS.SCALE
    def TAX = 23
    @Shared def date = new LocalDate(2018, 02, 13)
    def seller
    def buyer
    def itemType

    @Override
    def populate4Test() {
        def irs = IRS.getIRSInstance()

        seller = new TaxPayer(irs, SELLER_NIF, 'José Vendido', 'Somewhere')
        buyer = new TaxPayer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')
        itemType = new ItemType(irs, FOOD, TAX)
    }

    def 'success'() {
        when:
        def invoice = new Invoice(VALUE, date, itemType, seller, buyer)

        then:
        with(invoice) {
            getReference() != null
            16000 == getValue()
            getDate() == date
            getItemType() == itemType
            getSeller() == seller
            getBuyer() == buyer
            3.68 == getIva() / IRS.SCALE
            !isCancelled()
        }

        seller.getInvoiceByReference(invoice.getReference()) == invoice
        buyer.getInvoiceByReference(invoice.getReference()) == invoice
    }

    @Unroll('testing exceptions: #value, #dt, #it, #sel, #buy')
    def 'testing exceptions'() {
        when:
        new Invoice(value, dt, getItemType(it), getTaxPayer(sel), getTaxPayer(buy))

        then:
        thrown(TaxException)

        where:
        value           | dt                          | it    | sel  | buy  | label
        VALUE           | date                        | true  | null | 'B'  | 'null seller'
        VALUE           | date                        | true  | 'S'  | null | 'null buyer'
        VALUE           | date                        | false | 'S'  | 'B'  | 'null item'
        0               | date                        | true  | 'S'  | 'B'  | '0 value'
        -23 * IRS.SCALE | date                        | true  | 'S'  | 'B'  | 'negative value'
        VALUE           | null                        | true  | 'S'  | 'B'  | 'null date'
        VALUE           | new LocalDate(1969, 12, 31) | true  | 'S'  | 'B'  | 'incorrect date'
        VALUE           | date                        | true  | 'S'  | 'S'  | 'buyer equal to seller'
    }

    def getTaxPayer(type) {
        if (type == 'B') {
            return buyer
        } else if (type == 'S') {
            return seller
        } else {
            return null
        }
    }

    def getItemType(value) {
        if (value) {
            return itemType
        }
        return null
    }
}
