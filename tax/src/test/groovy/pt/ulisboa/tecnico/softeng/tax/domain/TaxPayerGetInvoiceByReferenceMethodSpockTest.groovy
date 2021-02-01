package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Unroll

class TaxPayerGetInvoiceByReferenceMethodSpockTest extends SpockRollbackTestAbstractClass {
    def SELLER_NIF = '123456789'
    def BUYER_NIF = '987654321'
    def FOOD = 'FOOD'
    def VALUE = 16 * IRS.SCALE
    def TAX = 23
    def date = new LocalDate(2018, 02, 13)
    def seller
    def buyer
    def itemType
    def invoice

    @Override
    def populate4Test() {
        def irs = IRS.getIRSInstance()

        seller = new TaxPayer(irs, SELLER_NIF, 'José Vendido', 'Somewhere')
        buyer = new TaxPayer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')
        itemType = new ItemType(irs, FOOD, TAX)
        invoice = new Invoice(VALUE, date, itemType, seller, buyer)
    }

    def 'success'() {
        expect:
        seller.getInvoiceByReference(invoice.getReference()) == invoice
    }

    @Unroll('#label')
    def 'test: '() {
        when:
        seller.getInvoiceByReference('')

        then:
        thrown(TaxException)

        where:
        label             | ref
        'null reference'  | null
        'empty reference' | ' '
    }

    def 'des not exist'() {
        expect:
        seller.getInvoiceByReference(BUYER_NIF) == null
    }

}
