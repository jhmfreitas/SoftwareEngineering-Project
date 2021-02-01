package pt.ulisboa.tecnico.softeng.bank.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.bank.services.local.BankInterface;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.ClientData;

@Controller
@RequestMapping(value = "/banks/{code}/clients")
public class ClientController {
	private static Logger logger = LoggerFactory.getLogger(ClientController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String clientForm(Model model, @PathVariable String code) {
		logger.info("clientForm bankCode:{}", code);

		BankData bankData = BankInterface.getBankDataByCode(code);

		if (bankData == null) {
			model.addAttribute("error", "Error: it does not exist a bank with the code " + code);
			model.addAttribute("bank", new BankData());
			model.addAttribute("banks", BankInterface.getBanks());
			return "banks";
		}

		model.addAttribute("client", new ClientData());
		model.addAttribute("bank", bankData);
		return "clients";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String clientSubmit(Model model, @PathVariable String code, @ModelAttribute ClientData client) {
		logger.info("clientSubmit bankCode:{}, clientName:{}", code, client.getName());

		try {
			BankInterface.createClient(code, client);
		} catch (BankException be) {
			model.addAttribute("error", "Error: it was not possible to create the client");
			model.addAttribute("client", client);
			model.addAttribute("bank", BankInterface.getBankDataByCode(code));
			return "clients";
		}

		return "redirect:/banks/" + code + "/clients";
	}

}
