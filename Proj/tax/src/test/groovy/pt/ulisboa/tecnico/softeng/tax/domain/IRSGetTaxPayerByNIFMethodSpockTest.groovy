package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Shared
import spock.lang.Unroll

class IRSGetTaxPayerByNIFMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def SELLER_NIF = '123456789'
    @Shared def BUYER_NIF = '987654321'
    def irs

    @Override
    def populate4Test() {
        irs = IRS.getIRSInstance()

        new TaxPayer(irs, SELLER_NIF, 'José Vendido', 'Somewhere')
        new TaxPayer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')
    }

    @Unroll('success #label')
    def 'success: '() {
        when:
        def taxPayer = irs.getTaxPayerByNif(nif)

        then:
        taxPayer != null
        taxPayer.getNif() == nif

        where:
        label        | nif
        'buyer nif'  | BUYER_NIF
        'seller nif' | SELLER_NIF
    }

    @Unroll('#label')
    def 'test: '() {
        when:
        def taxPayer = irs.getTaxPayerByNif('122456789')

        then:
        taxPayer == null

        where:
        label                | nif
        'null nif'           | null
        'empty nif'          | ''
        'nif does not exist' | '122456789'
    }

}
