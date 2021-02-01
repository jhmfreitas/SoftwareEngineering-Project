package pt.ulisboa.tecnico.softeng.activity.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class ActivityProviderConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def PROVIDER_CODE = 'XtremX'
	@Shared def PROVIDER_NAME = 'Adventure++'
	@Shared def IBAN = 'IBAN'
	@Shared def NIF = 'NIF'

	@Override
	def populate4Test() { }

	def 'success'() {
		when:
		def processor = new Processor(new BankInterface(), new TaxInterface())
		def provider = new ActivityProvider(PROVIDER_CODE,PROVIDER_NAME,NIF,IBAN,processor)

		then:
		provider.getName() == PROVIDER_NAME
		provider.getCode().length() == ActivityProvider.CODE_SIZE
		FenixFramework.getDomainRoot().getActivityProviderSet().size() == 1
		provider.getActivitySet().size() == 0
	}

	@Unroll('exceptions: #code, #prov, #nif, #iban')
	def 'exceptions'() {
		when:
		new ActivityProvider(code, prov, nif, iban, new Processor(new BankInterface(), new TaxInterface()))

		then:
		thrown(ActivityException)

		where:
		code          | prov          | nif  | iban
		null          | PROVIDER_NAME | NIF  | IBAN
		'  '          | PROVIDER_NAME | NIF  | IBAN
		'12345'       | PROVIDER_NAME | NIF  | IBAN
		'1234567'     | PROVIDER_NAME | NIF  | IBAN
		PROVIDER_CODE | null          | NIF  | IBAN
		PROVIDER_CODE | '  '          | NIF  | IBAN
		PROVIDER_CODE | PROVIDER_NAME | null | IBAN
		PROVIDER_CODE | PROVIDER_NAME | '  ' | IBAN
		PROVIDER_CODE | PROVIDER_NAME | NIF  | null
		PROVIDER_CODE | PROVIDER_NAME | NIF  | '  '
	}

	@Unroll('uniques: #cd1, #cd2, #n1, #n2, #nif1, #nif2')
	def 'uniques'() {
		given: 'an acitivity providr'
		new ActivityProvider(cd1, n1, nif1, IBAN, new Processor(new BankInterface(), new TaxInterface()))

		when: 'it is created another'
		new ActivityProvider(cd2, n2, nif2, IBAN, new Processor(new BankInterface(), new TaxInterface()))

		then: 'throws an exception'
		def error = thrown(ActivityException)
		FenixFramework.getDomainRoot().getActivityProviderSet().size() == 1

		where: 'if they have de same, code, name, or nif'
		cd1           | cd2           | n1            | n2            | nif1 | nif2
		PROVIDER_CODE | PROVIDER_CODE | PROVIDER_NAME | 'Hello'       | NIF  | NIF + 2
		'123456'      | PROVIDER_CODE | PROVIDER_NAME | PROVIDER_NAME | NIF  | NIF + 2
		PROVIDER_CODE | '123456'      | PROVIDER_NAME | 'jdgdsk'      | NIF  | NIF
	}
}
