package pt.ulisboa.tecnico.softeng.activity.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.local.ActivityInterface;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData;

@Controller
@RequestMapping(value = "/providers/{code}/activities")
public class ActivityController {
	private static Logger logger = LoggerFactory.getLogger(ActivityController.class);

	private static final ActivityInterface activityInterface = new ActivityInterface();

	@RequestMapping(method = RequestMethod.GET)
	public String activityForm(Model model, @PathVariable String code) {
		logger.info("activityForm providerCode:{}", code);

		ActivityProviderData providerData = activityInterface.getProviderDataByCode(code);

		if (providerData == null) {
			model.addAttribute("error", "Error: it does not exist an activity provider with the code " + code);
			model.addAttribute("provider", new ActivityProviderData());
			model.addAttribute("providers", ActivityInterface.getProviders());
			return "providers";
		}

		model.addAttribute("activity", new ActivityData());
		model.addAttribute("provider", providerData);
		return "activities";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String activitySubmit(Model model, @PathVariable String code, @ModelAttribute ActivityData activity) {
		logger.info(
				"activitySubmit providerCode:{}, activityName:{}, activityMinAge:{}, activityMaxAge:{}, activityCapacity:{}",
				code, activity.getName(), activity.getMinAge(), activity.getMaxAge(), activity.getCapacity());

		try {
			activityInterface.createActivity(code, activity);
		} catch (ActivityException be) {
			model.addAttribute("error", "Error: it was not possible to create the activity");
			model.addAttribute("activity", activity);
			model.addAttribute("provider", activityInterface.getProviderDataByCode(code));
			return "activities";
		}

		return "redirect:/providers/" + code + "/activities";
	}

}
