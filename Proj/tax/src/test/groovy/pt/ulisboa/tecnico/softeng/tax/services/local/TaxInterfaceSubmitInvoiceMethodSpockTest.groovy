package pt.ulisboa.tecnico.softeng.tax.services.local

import org.joda.time.DateTime
import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.tax.domain.TaxPayer
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import pt.ulisboa.tecnico.softeng.tax.services.remote.dataobjects.RestInvoiceData
import spock.lang.Shared
import spock.lang.Unroll

class TaxInterfaceSubmitInvoiceMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def REFERENCE = '123456789'
    @Shared def SELLER_NIF = '123456789'
    @Shared def BUYER_NIF = '987654321'
    @Shared def FOOD = 'FOOD'
    @Shared def VALUE = 160
    @Shared def TAX = 16
    @Shared def date = new LocalDate(2018, 02, 13)
    @Shared def time = new DateTime(2018, 02, 13, 10, 10)
    @Shared def irs

    @Override
    def populate4Test() {
        irs = IRS.getIRSInstance()

        new TaxPayer(irs, SELLER_NIF, 'José Vendido', 'Somewhere')
        new TaxPayer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')
        new ItemType(irs, FOOD, TAX)
    }

    def 'success'() {
        given:
        def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date, time)
        def invoiceReference = TaxInterface.submitInvoice(invoiceData)

        when:
        def invoice = irs.getTaxPayerByNif(SELLER_NIF).getInvoiceByReference(invoiceReference)

        then:
        with(invoice) {
            getReference() == invoiceReference
            getSeller().getNif() == SELLER_NIF
            getBuyer().getNif() == BUYER_NIF
            getItemType().getName() == FOOD
            160.0 == getValue()
            getDate() == date
        }
    }

    def 'submit twice'() {
        given:
        def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date, time)

        when:
        def invoiceReference = TaxInterface.submitInvoice(invoiceData)
        def secondInvoiceReference = TaxInterface.submitInvoice(invoiceData)

        then:
        secondInvoiceReference == invoiceReference
    }

    def 'equal 1970'() {
        given:
        def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, new LocalDate(1970, 01, 01), new DateTime(1970, 01, 01, 10, 10))

        expect:
        TaxInterface.submitInvoice(invoiceData)
    }

    @Unroll('#reference,  #sel,  #buy,  #food,  #value,  #dt,  #tm')
    def 'exceptions'() {
        given:
        def invoiceData = new RestInvoiceData(reference, sel, buy, food, value, dt, tm)

        when:
        TaxInterface.submitInvoice(invoiceData)

        then:
        thrown(TaxException)

        where:
        reference | sel        | buy       | food | value  | dt                          | tm
        REFERENCE | null       | BUYER_NIF | FOOD | VALUE  | date                        | time
        REFERENCE | ''         | BUYER_NIF | FOOD | VALUE  | date                        | time
        REFERENCE | SELLER_NIF | null      | FOOD | VALUE  | date                        | time
        REFERENCE | SELLER_NIF | ''        | FOOD | VALUE  | date                        | time
        REFERENCE | SELLER_NIF | BUYER_NIF | null | VALUE  | date                        | time
        REFERENCE | SELLER_NIF | BUYER_NIF | ''   | VALUE  | date                        | time
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | 0      | date                        | time
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | Math.round(-23.7 * IRS.SCALE) | date | time
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | date                        | null
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | new LocalDate(1969, 12, 31) | new DateTime(1969, 12, 31, 10, 10)
    }

    def 'null ref'() {
        when:
        new RestInvoiceData(null, SELLER_NIF, BUYER_NIF,
                FOOD, VALUE, new LocalDate(1970, 01, 01),
                new DateTime(1970, 01, 01, 10, 10))

        then:
        thrown(TaxException)
    }
}
