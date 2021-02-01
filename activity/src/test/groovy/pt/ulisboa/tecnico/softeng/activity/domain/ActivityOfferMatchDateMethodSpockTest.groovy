package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class ActivityOfferMatchDateMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def begin = new LocalDate(2016,12,19)
	@Shared def end = new LocalDate(2016,12,23)
	@Shared def offer

	@Override
	def populate4Test() {
		def processor = new Processor(new BankInterface(), new TaxInterface())
		def provider = new ActivityProvider('XtremX','ExtremeAdventure','NIF','IBAN',processor)
		def activity = new Activity(provider,'Bush Walking',18,80,3)
		offer=new ActivityOffer(activity,begin,end,30)
	}

	@Unroll('success: #theb, #thee ==> #res')
	def 'success'() {
		expect:
		offer.matchDate(theb, thee) == res

		where:
		theb               | thee             || res
		begin              | end              || true
		begin.minusDays(1) | end              || false
		begin.plusDays(1)  | end              || false
		begin              | end.plusDays(1)  || false
		begin              | end.minusDays(1) || false
	}

	@Unroll('exception: #label')
	def 'exceptions'() {
		when:
		offer.matchDate(theb ,thee)

		then:
		thrown(ActivityException)

		where:
		theb                 | thee | label
		null                 | end  | 'null begin date'
		begin                | null | 'null end date'
	}
}
