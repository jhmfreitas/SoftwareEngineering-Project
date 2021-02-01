package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class ActivityProviderFindOfferMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def MIN_AGE=25
	@Shared def MAX_AGE=80
	@Shared def CAPACITY=25
	@Shared def AGE=40
	@Shared def begin=new LocalDate(2016,12,19)
	@Shared def end=new LocalDate(2016,12,21)
	@Shared def provider
	@Shared def activity
	@Shared def offer

	@Override
	def populate4Test() {
		def processor = new Processor(new BankInterface(), new TaxInterface())
		provider = new ActivityProvider('XtremX','ExtremeAdventure','NIF','IBAN',processor)
		activity = new Activity(provider,'Bush Walking', MIN_AGE, MAX_AGE, CAPACITY)
		offer = new ActivityOffer(activity, begin, end,30)
	}

	@Unroll('success: #theb, #thee, #age')
	def 'success'() {
		when:
		def offers = provider.findOffer(theb, thee, age)

		then: 'finds an offer'
		offers.size() == 1
		offers.contains(offer)

		where: 'for all possible ages'
		theb  | thee | age
		begin | end  | AGE
		begin | end  | MIN_AGE
		begin | end  | MAX_AGE
	}

	@Unroll('exceptions: #theb, #thee, #age')
	def 'exceptions'() {
		when:
		provider.findOffer(theb, thee, age)

		then: 'throws exceptions'
		thrown(ActivityException)

		where:
		theb  | thee | age
		null  | end  | AGE
		begin | null | AGE
	}

	@Unroll('empty: #theb, #thee, #age')
	def 'empties'() {
		when:
		def offers = provider.findOffer(theb, thee, age)

		then:
		offers.isEmpty()

		where:
		theb | thee | age
		begin | end | MIN_AGE - 1
		begin | end | MAX_AGE + 1
	}


	def 'empty activity set'() {
		given:
		def otherProvider = new ActivityProvider('Xtrems','Adventure',
				'NIF2','IBAN', new Processor(new BankInterface(), new TaxInterface()))

		when:
		def offers = otherProvider.findOffer(begin, end, AGE)

		then:
		offers.isEmpty()
	}

	def 'empty activity offer set'() {
		given:
		def otherProvider = new ActivityProvider('Xtrems','Adventure',
				'NIF2','IBAN', new Processor(new BankInterface(), new TaxInterface()))
		new Activity(otherProvider,'Bush Walking',18,80,25)

		when:
		def offers = otherProvider.findOffer(begin, end, AGE)

		then:
		offers.isEmpty()
	}

	def 'two match activity offers'() {
		given:
		new ActivityOffer(activity, begin, end, 30)

		when:
		def offers = provider.findOffer( begin, end, AGE)

		then:
		offers.size() == 2
	}

	def 'one match activity offer and one not match'() {
		given:
		new ActivityOffer(activity, begin, end.plusDays(1),30)

		when:
		def offers = provider.findOffer(begin, end, AGE)

		then:
		offers.size() == 1
	}

	def 'one match activity offer and other no capacity'() {
		given:
		def otherActivity = new Activity(provider, 'Bush Walking', MIN_AGE, MAX_AGE, 1)
		def otherActivityOffer = new ActivityOffer(otherActivity, begin, end, 30)

		when:
		new Booking(provider,otherActivityOffer,'123456789','IBAN')

		and:
		def offers = provider.findOffer(begin, end, AGE)

		then:
		offers.size() == 1
	}
}
