package pt.ulisboa.tecnico.softeng.activity.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.local.ActivityInterface;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData;

@Controller
@RequestMapping(value = "/providers")
public class ActivityProviderController {
	private static Logger logger = LoggerFactory.getLogger(ActivityProviderController.class);
	
	private static final ActivityInterface activityInterface = new ActivityInterface();

	@RequestMapping(method = RequestMethod.GET)
	public String providerForm(Model model) {
		logger.info("providerForm");
		model.addAttribute("provider", new ActivityProviderData());
		model.addAttribute("providers", ActivityInterface.getProviders());
		return "providers";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String providerSubmit(Model model, @ModelAttribute ActivityProviderData provider) {
		logger.info("providerSubmit name:{}, code:{}, nif:{}, iban:{}", provider.getName(), provider.getCode(),
				provider.getNif(), provider.getIban());

		try {
			activityInterface.createProvider(provider);
		} catch (ActivityException be) {
			model.addAttribute("error", "Error: it was not possible to create the activity provider");
			model.addAttribute("provider", provider);
			model.addAttribute("providers", ActivityInterface.getProviders());
			return "providers";
		}

		return "redirect:/providers";
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public String deleteProviders(Model model) {
		logger.info("deleteProviders");

		activityInterface.deleteActivityProviders();

		return "redirect:/providers/";
	}

}
