package pt.ulisboa.tecnico.softeng.tax.services.local

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.domain.*
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Unroll

class TaxInterfaceCancelInvoiceMethodSpockTest extends SpockRollbackTestAbstractClass {
    def SELLER_NIF = '123456789'
    def BUYER_NIF = '987654321'
    def FOOD = 'FOOD'
    def VALUE = 16
    def date = new LocalDate(2018, 02, 13)
    def irs
    def reference
    def invoice

    @Override
    def populate4Test() {
        irs = IRS.getIRSInstance()

        def seller = new TaxPayer(irs, SELLER_NIF, 'José Vendido', 'Somewhere')
        def buyer = new TaxPayer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')
        def itemType = new ItemType(irs, FOOD, VALUE)

        invoice = new Invoice(30, date, itemType, seller, buyer)
        reference = invoice.getReference()
    }

    def 'success'() {
        when:
        TaxInterface.cancelInvoice(reference)

        then:
        invoice.isCancelled()
    }

    @Unroll('#label')
    def 'test: '() {
        when:
        TaxInterface.cancelInvoice(ref)

        then:
        thrown(TaxException)

        where:
        label                      | ref
        'null reference'           | null
        'empty reference'          | ' '
        'reference does not exist' | 'XXXXXXXX'
    }
}
