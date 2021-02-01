package pt.ulisboa.tecnico.softeng.activity.domain

import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface

class ActivityMatchAgeMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final int MIN_AGE=25
	private static final int MAX_AGE=80
	private static final int CAPACITY=30
	private Activity activity

	@Override
	def populate4Test() {
		def processor = new Processor(new BankInterface(), new TaxInterface())
		def provider = new ActivityProvider('XtremX','ExtremeAdventure','NIF','IBAN',processor)

		activity = new Activity(provider,'Bush Walking',MIN_AGE,MAX_AGE,CAPACITY)
	}

	def 'success'() {
		expect:
		activity.matchAge(arg)

		where:
		arg << [
			(MAX_AGE - MIN_AGE).intdiv(2) + MIN_AGE,
			MAX_AGE,
			MIN_AGE
		]
	}


	def 'less than min age'() {
		expect:
		!activity.matchAge(arg)

		where:
		arg << [MIN_AGE - 1, MAX_AGE + 1]
	}
}
