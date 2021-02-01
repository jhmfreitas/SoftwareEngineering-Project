package pt.ulisboa.tecnico.softeng.activity.domain

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class ActivityConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def IBAN = 'IBAN'
	@Shared def NIF = 'NIF'
	@Shared def PROVIDER_NAME = 'Bush Walking'
	@Shared def MIN_AGE = 25
	@Shared def MAX_AGE = 50
	@Shared def CAPACITY = 30
	@Shared def provider

	@Override
	def populate4Test() {
		def processor = new Processor(new BankInterface(), new TaxInterface())
		provider = new ActivityProvider('XtremX','ExtremeAdventure',NIF,IBAN,processor)
	}

	@Unroll('success: #prov, #name, #min, #max, #cap')
	def 'success'() {
		when:
		def activity = new Activity(provider, name, min, max, cap)

		then:
		with(activity) {
			getCode().startsWith(provider.getCode())
			getCode().length() > ActivityProvider.CODE_SIZE
			getName() == name
			getMinAge() == min
			getMaxAge() == max
			getCapacity() == cap
			getActivityOfferSet().size() == 0
		}
		provider.getActivitySet().size() == 1

		where:
		name          | min      | max     | cap
		PROVIDER_NAME | MIN_AGE  | MAX_AGE | CAPACITY
		PROVIDER_NAME | 18       | MAX_AGE | CAPACITY
		PROVIDER_NAME | MIN_AGE  | 99      | CAPACITY
		PROVIDER_NAME | MIN_AGE  | MIN_AGE | CAPACITY
		PROVIDER_NAME | MIN_AGE  | MAX_AGE | 1
	}

	@Unroll('exception: #prov, #name, #min, #max, #cap')
	def 'exceptions'() {
		when:
		new Activity(prov, name, min, max, cap)

		then:
		thrown(ActivityException)

		where:
		prov     | name          | min          | max     | cap
		null     | PROVIDER_NAME | MIN_AGE      | MAX_AGE | CAPACITY
		provider | null          | MIN_AGE      | MAX_AGE | CAPACITY
		provider | '  '          | MIN_AGE      | MAX_AGE | CAPACITY
		provider | PROVIDER_NAME | 17           | MAX_AGE | CAPACITY
		provider | PROVIDER_NAME | 17           | 100     | CAPACITY
		provider | PROVIDER_NAME | MAX_AGE + 10 | 100     | CAPACITY
		provider | PROVIDER_NAME | MAX_AGE + 1  | 100     | CAPACITY
		provider | PROVIDER_NAME | MIN_AGE      | MAX_AGE | 0
	}
}
