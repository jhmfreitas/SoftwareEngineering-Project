package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class ActivityOfferConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
    def CAPACITY = 25
    def MAX_AGE = 50
    def MIN_AGE = 25
    @Shared def begin = new LocalDate(2016, 12, 19)
    @Shared def end = new LocalDate(2016, 12, 21)
    @Shared def activity

    @Override
    def populate4Test() {
        def processor = new Processor(new BankInterface(), new TaxInterface())
        def provider = new ActivityProvider('XtremX', 'ExtremeAdventure', 'NIF', 'IBAN', processor)

        activity = new Activity(provider, 'Bush Walking', MIN_AGE, MAX_AGE, CAPACITY)
    }

    @Unroll('success: #the_beg, #the_end')
    def 'success'() {
        when:
        def offer = new ActivityOffer(activity, the_beg, the_end, amount)

        then:
        offer.getBegin() == the_beg
        offer.getEnd() == the_end
        offer.getNumberActiveOfBookings() == 0
        offer.getPrice() == amount
        activity.getActivityOfferSet().size() == 1

        where:
        the_beg | the_end | amount
        begin   | end     | 30
        begin   | begin   | 30
    }

    @Unroll('exception: #the_beg, #the_end')
    def 'exception'() {
        when:
        new ActivityOffer(act, the_beg, the_end, amnt)

        then:
        thrown(ActivityException)

        where:
        act      | the_beg | the_end            | amnt
        null     | begin   | end                | 30
        activity | null    | end                | 30
        activity | begin   | null               | 30
        activity | begin   | begin.minusDays(1) | 30
        activity | begin   | end                | 0
    }
}
