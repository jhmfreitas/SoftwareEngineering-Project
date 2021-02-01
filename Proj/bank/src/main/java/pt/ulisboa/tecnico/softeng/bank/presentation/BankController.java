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

@Controller
@RequestMapping(value = "/banks")
public class BankController {
	private static Logger logger = LoggerFactory.getLogger(BankController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String bankForm(Model model) {
		logger.info("bankForm");
		model.addAttribute("bank", new BankData());
		model.addAttribute("banks", BankInterface.getBanks());
		return "banks";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String bankSubmit(Model model, @ModelAttribute BankData bank) {
		logger.info("bankSubmit name:{}, code:{}", bank.getName(), bank.getCode());

		try {
			BankInterface.createBank(bank);
		} catch (BankException be) {
			model.addAttribute("error", "Error: it was not possible to create the bank");
			model.addAttribute("bank", bank);
			model.addAttribute("banks", BankInterface.getBanks());
			return "banks";
		}

		return "redirect:/banks";
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public String deleteBanks(Model model) {
		logger.info("deleteBanks");

		BankInterface.deleteBanks();

		return "redirect:/banks";
	}


	@RequestMapping(value="/{code}/undo/{reference}", method = RequestMethod.POST)
	public String accountUndo(Model model, @PathVariable String code, @PathVariable String reference){

		logger.info("accountUndo bankCode:{} referenceCode:{}", code, reference);

		try{
			BankInterface.revertOperation(reference);
		}catch(BankException be){
			model.addAttribute("error", "Error: it was not possible to execute the operation");
			model.addAttribute("bank", BankInterface.getBankDataByCode(code));
			model.addAttribute("operation", BankInterface.getOperationData(reference));
			return "clients";
		}
		return "redirect:/banks/" + code + "/clients";
	}
}
