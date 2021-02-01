package pt.ulisboa.tecnico.softeng.activity.domain;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class ActivityProvider extends ActivityProvider_Base {
	static final int CODE_SIZE = 6;

	public ActivityProvider(String code, String name, String nif, String iban, Processor processor) {
		checkArguments(code, name, nif, iban);

		setCode(code);
		setName(name);
		setNif(nif);
		setIban(iban);

		setProcessor(processor);

		FenixFramework.getDomainRoot().addActivityProvider(this);
	}

	public void delete() {
		setRoot(null);

		getProcessor().delete();

		for (Activity activity : getActivitySet()) {
			activity.delete();
		}

		deleteDomainObject();
	}

	private void checkArguments(String code, String name, String nif, String iban) {
		if (code == null || name == null || code.trim().equals("") || name.trim().equals("") || nif == null
				|| nif.trim().length() == 0 || iban == null || iban.trim().length() == 0) {
			throw new ActivityException();
		}

		if (code.length() != ActivityProvider.CODE_SIZE) {
			throw new ActivityException();
		}

		for (ActivityProvider activityProvider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			if (activityProvider.getCode().equals(code) || activityProvider.getName().equals(name)) {
				throw new ActivityException();
			}
		}

		for (ActivityProvider activityProvider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			if (activityProvider.getNif().equals(nif)) {
				throw new ActivityException();
			}
		}
	}

	public List<ActivityOffer> findOffer(LocalDate begin, LocalDate end, int age) {
		List<ActivityOffer> result = new ArrayList<>();
		for (Activity activity : getActivitySet()) {
			result.addAll(activity.getOffers(begin, end, age));
		}
		return result;
	}

	public Booking getBooking(String reference) {
		for (Activity activity : getActivitySet()) {
			Booking booking = activity.getBooking(reference);
			if (booking != null) {
				return booking;
			}
		}
		return null;
	}

	@Override
	public int getCounter() {
		int counter = super.getCounter() + 1;
		setCounter(counter);
		return counter;
	}

	public Booking getBookingByAdventureId(String adventureId) {
		return getActivitySet().stream().flatMap(a -> a.getActivityOfferSet().stream())
				.flatMap(o -> o.getBookingSet().stream())
				.filter(b -> b.getAdventureId() != null && b.getAdventureId().equals(adventureId)).findFirst()
				.orElse(null);
	}

}
