package pt.ulisboa.tecnico.softeng.activity.services.local

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.domain.Activity
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider
import pt.ulisboa.tecnico.softeng.activity.domain.Booking
import pt.ulisboa.tecnico.softeng.activity.domain.Processor
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData
import spock.lang.Unroll

class ActivityInterfaceGetActivityReservationDataMethodSpockTest extends SpockRollbackTestAbstractClass {
	def NAME = 'ExtremeAdventure'
	def CODE = 'XtremX'
	def begin = new LocalDate(2016,12,19)
	def end = new LocalDate(2016,12,21)
	def provider
	def offer
	def booking

	def activityInterface

	@Override
	def populate4Test() {
		def processor = new Processor(new BankInterface(), new TaxInterface())

		provider = new ActivityProvider(CODE,NAME,'NIF','IBAN', processor)
		def activity = new Activity(provider,'Bush Walking',18,80,3)
		offer = new ActivityOffer(activity,begin,end,30)

		activityInterface = new ActivityInterface()
	}

	def 'success'() {
		given:
		booking = new Booking(provider,offer,'123456789','IBAN')

		when:
		RestActivityBookingData data = activityInterface.getActivityReservationData(booking.getReference())

		then:
		data.getReference() == booking.getReference()
		data.getCancellation() == null
		data.getName() == NAME
		data.getCode() == CODE
		data.getBegin() == begin
		data.getEnd() == end
		data.getCancellationDate() == null
	}

	def 'success cancelled'() {
		given: 'a cancelled booking'
		booking = new Booking(provider,offer,'123456789','IBAN')
		provider.getProcessor().submitBooking(booking)
		booking.cancel()

		when: 'get booking data'
		RestActivityBookingData data = activityInterface.getActivityReservationData(booking.getCancel())

		then: 'the information if OK'
		data.getReference() == booking.getReference()
		data.getCancellation() == booking.getCancel()
		data.getName() == NAME
		data.getCode() == CODE
		data.getBegin() == begin
		data.getEnd() == end
		data.getCancellationDate() != null
	}

	@Unroll('exceptions: #label')
	def 'exceptions'() {
		when:
		activityInterface.getActivityReservationData(ref)

		then:
		thrown(ActivityException)

		where:
		ref    | label
		null   | 'null reference'
		''     | 'empty reference'
		'XPTO' | 'not exists reference'
	}
}
