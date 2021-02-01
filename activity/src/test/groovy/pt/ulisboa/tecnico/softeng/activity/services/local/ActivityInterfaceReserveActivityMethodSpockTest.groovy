package pt.ulisboa.tecnico.softeng.activity.services.local

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.domain.Activity
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider
import pt.ulisboa.tecnico.softeng.activity.domain.Processor
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData

class ActivityInterfaceReserveActivityMethodSpockTest extends SpockRollbackTestAbstractClass {
    def IBAN = "IBAN"
    def NIF = "123456789"
    def MIN_AGE = 18
    def MAX_AGE = 50
    def CAPACITY = 30

    def provider1
    def provider2

    def activityInterface

    def bankInterface
    def taxInterface

    def activityBookingData

    @Override
    def populate4Test() {
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)
        def processor = new Processor(bankInterface, taxInterface)
        def processor1 = new Processor(bankInterface, taxInterface)

        provider1 = new ActivityProvider("XtremX", "Adventure++", "NIF", IBAN, processor)
        provider2 = new ActivityProvider("Walker", "Sky", "NIF2", IBAN, processor1)
        activityInterface = new ActivityInterface()

        activityBookingData = new RestActivityBookingData()
        activityBookingData.setAge(20)
        activityBookingData.setBegin(new LocalDate(2018, 02, 19))
        activityBookingData.setEnd(new LocalDate(2018, 12, 20))
        activityBookingData.setIban(IBAN)
        activityBookingData.setNif(NIF)
    }

    def 'success'() {
        given: 'given that activity and offer are available'
        def activity = new Activity(provider1, "XtremX", MIN_AGE, MAX_AGE, CAPACITY)
        new ActivityOffer(activity,
                new LocalDate(2018, 02, 19),
                new LocalDate(2018, 12, 20), 30)

        when: 'a reserve is invoked'
        def bookingData = activityInterface.reserveActivity(activityBookingData)

        then: 'there should be a booking with the proper data'
        bookingData != null
        bookingData.getReference().startsWith("XtremX")
    }

    def 'no option to reserve activity'() {
        when:
        activityInterface.reserveActivity(activityBookingData)

        then:
        thrown(ActivityException)
    }

}
