package pt.ulisboa.tecnico.softeng.tax.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface;
import pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects.TaxPayerData;

@Controller
@RequestMapping(value = "/tax/payers")
public class TaxPayerController {
    private static final Logger logger = LoggerFactory.getLogger(TaxPayerController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String taxPayerForm(Model model) {
        logger.info("taxPayerForm");
        model.addAttribute("payer", new TaxPayerData());
        model.addAttribute("payers", TaxInterface.getTaxPayerDataList());
        model.addAttribute("taxesYear", TaxInterface.getTaxesPerYear());
        model.addAttribute("returnsYear",TaxInterface.getReturnsPerYear());
        return "payersView";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String taxPayerSubmit(Model model, @ModelAttribute TaxPayerData taxPayerData) {
        logger.info("taxPayerSubmit name:{}, address:{}, nif:{}", taxPayerData.getName(),
                taxPayerData.getAddress(), taxPayerData.getNif());

        try {
            TaxInterface.createTaxPayer(taxPayerData);
        } catch (TaxException be) {
            model.addAttribute("error", "Error: it was not possible to create the tax payer " + taxPayerData.getName());
            model.addAttribute("payer", taxPayerData);
            model.addAttribute("payers", TaxInterface.getTaxPayerDataList());
            model.addAttribute("taxesYear", TaxInterface.getTaxesPerYear());
            model.addAttribute("returnsYear",TaxInterface.getReturnsPerYear());
            return "payersView";
        }

        return "redirect:/tax/payers";
    }

}
