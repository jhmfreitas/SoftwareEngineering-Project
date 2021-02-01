package pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects;

import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.softeng.activity.domain.Activity;

public class ActivityData {
	private String codeProvider;
	private String nameProvider;
	private String code;
	private String name;
	private Integer minAge;
	private Integer maxAge;
	private Integer capacity;
	private List<ActivityOfferData> offers;

	public ActivityData() {
	}

	public ActivityData(Activity activity) {
		this.codeProvider = activity.getActivityProvider().getCode();
		this.nameProvider = activity.getActivityProvider().getName();
		this.code = activity.getCode();
		this.name = activity.getName();
		this.minAge = activity.getMinAge();
		this.maxAge = activity.getMaxAge();
		this.capacity = activity.getCapacity();
		this.offers = activity.getActivityOfferSet().stream().sorted((o1, o2) -> o1.getBegin().compareTo(o2.getBegin()))
				.map(o -> new ActivityOfferData(o)).collect(Collectors.toList());
	}

	public String getCodeProvider() {
		return this.codeProvider;
	}

	public void setCodeProvider(String codeProvider) {
		this.codeProvider = codeProvider;
	}

	public String getNameProvider() {
		return this.nameProvider;
	}

	public void setNameProvider(String nameProvider) {
		this.nameProvider = nameProvider;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMinAge() {
		return this.minAge;
	}

	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}

	public Integer getMaxAge() {
		return this.maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}

	public Integer getCapacity() {
		return this.capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public List<ActivityOfferData> getOffers() {
		return this.offers;
	}

	public void setOffers(List<ActivityOfferData> offers) {
		this.offers = offers;
	}

}
