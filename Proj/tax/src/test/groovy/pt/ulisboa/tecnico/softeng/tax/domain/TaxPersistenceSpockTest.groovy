package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ist.fenixframework.FenixFramework


class TaxPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
    def SELLER_NIF = '123456789'
    def BUYER_NIF = '987654321'
    def FOOD = 'FOOD'
    def VALUE = 16 * IRS.SCALE
    def date = new LocalDate(2018, 02, 13)

    @Override
    def whenCreateInDatabase() {
        def irs = IRS.getIRSInstance()
        def seller = new TaxPayer(irs, SELLER_NIF, 'José Vendido', 'Somewhere')
        def buyer = new TaxPayer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')
        def it = new ItemType(irs, FOOD, VALUE)
        new Invoice(VALUE, date, it, seller, buyer)
    }

    @Override
    def thenAssert() {
        def irs = IRS.getIRSInstance()
        assert 2 == irs.getTaxPayerSet().size()

        for (TaxPayer taxPayer : irs.getTaxPayerSet()) {
            assert taxPayer.getNif() == BUYER_NIF || taxPayer.getNif() == SELLER_NIF
        }

        assert 1 == irs.getItemTypeSet().size()

        def itemType = new ArrayList<>(irs.getItemTypeSet()).get(0)
        assert VALUE == itemType.getTax()
        assert FOOD == itemType.getName()
        assert 1 == irs.getInvoiceSet().size()

        def invoice = new ArrayList<>(irs.getInvoiceSet()).get(0)
        assert VALUE == invoice.getValue()
        assert invoice.getReference() != null
        assert date == invoice.getDate()
        assert BUYER_NIF == invoice.getBuyer().getNif()
        assert SELLER_NIF == invoice.getSeller().getNif()
        assert itemType == invoice.getItemType()
        assert invoice.getTime() != null
        assert !invoice.getCancelled()
    }

    @Override
    def deleteFromDatabase() {
        FenixFramework.getDomainRoot().getIrs().delete()
    }
}
