package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Unroll

class IRSGetItemTypeByNameMethodSpockTest extends SpockRollbackTestAbstractClass {
    def FOOD = 'FOOD'
    def VALUE = 16 * IRS.SCALE
    def irs

    @Override
    def populate4Test() {
        irs = IRS.getIRSInstance()

        new ItemType(irs, FOOD, VALUE)
    }

    @Unroll('#label')
    def 'test: '() {
        when:
        def itemType = irs.getItemTypeByName(name)

        then:
        itemType == null

        where:
        label                 | name
        'null name'           | null
        'empty name'          | ' '
        'does not exist name' | 'CAR'
    }
}
