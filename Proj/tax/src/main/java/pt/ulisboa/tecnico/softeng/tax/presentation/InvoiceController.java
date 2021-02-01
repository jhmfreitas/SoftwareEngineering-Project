package pt.ulisboa.tecnico.softeng.tax.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface;
import pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects.InvoiceData;

@Controller
@RequestMapping(value = "/tax/payers/{nif}/invoices")
public class InvoiceController {
	private static Logger logger = LoggerFactory.getLogger(InvoiceController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String invoiceForm(Model model, @PathVariable String nif) {
		logger.info("invoiceForm nif:{}", nif);
		model.addAttribute("invoice", new InvoiceData());
		model.addAttribute("payer", TaxInterface.getTaxPayerDataByNif(nif));
		model.addAttribute("invoices", TaxInterface.getInvoiceDataList(nif));
		return "invoicesView";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String invoiceSubmit(Model model, @PathVariable String nif, @ModelAttribute InvoiceData invoiceData) {
		logger.info("invoiceSubmit nif:{}, buyerNif:{}, sellerNif:{}, itemType:{}, value:{}, date:{}, time:{}", nif,
				invoiceData.getBuyerNif(), invoiceData.getSellerNif(), invoiceData.getItemType(),
				invoiceData.getValue(), invoiceData.getDate(), invoiceData.getTime());

		try {
			TaxInterface.createInvoice(nif, invoiceData);
		} catch (TaxException be) {
			model.addAttribute("error", "Error: it was not possible to create the invoice");
			model.addAttribute("invoice", invoiceData);
			model.addAttribute("payer", TaxInterface.getTaxPayerDataByNif(nif));
			model.addAttribute("invoices", TaxInterface.getInvoiceDataList(nif));
			return "invoicesView";
		}

		return "redirect:/tax/payers/" + nif + "/invoices";
	}

}
