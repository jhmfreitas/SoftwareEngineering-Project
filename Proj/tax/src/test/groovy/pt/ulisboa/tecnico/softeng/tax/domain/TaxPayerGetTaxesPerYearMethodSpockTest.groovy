package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

class TaxPayerGetTaxesPerYearMethodSpockTest extends SpockRollbackTestAbstractClass {
    def SELLER_NIF = '123456788'
    def BUYER_NIF = '987654311'
    def FOOD = 'FOOD'
    def TAX = 10
    def date = new LocalDate(2018, 02, 13)
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
        given:
        new Invoice(100 * IRS.SCALE, new LocalDate(2017, 12, 12), itemType, seller, buyer)
        new Invoice(100 * IRS.SCALE, date, itemType, seller, buyer)
        new Invoice(100 * IRS.SCALE, date, itemType, seller, buyer)
        new Invoice(50 * IRS.SCALE, date, itemType, seller, buyer)

        when:
        def toPay = seller.getToPayPerYear()

        then:
        toPay.keySet().size() == 2
        10.0 == toPay.get(2017) / IRS.SCALE
        25.0 == toPay.get(2018) / IRS.SCALE
        Map<Integer, Double> taxReturn = buyer.getTaxReturnPerYear()

        taxReturn.keySet().size() == 2
        0.5 == taxReturn.get(2017) / IRS.SCALE
        1.25 == taxReturn.get(2018) / IRS.SCALE
    }

    def 'success empty'() {
        when:
        def toPay = seller.getToPayPerYear()

        then:
        toPay.keySet().size() == 0
        Map<Integer, Double> taxReturn = buyer.getTaxReturnPerYear()
        taxReturn.keySet().size() == 0
    }

}
