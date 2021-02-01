package pt.ulisboa.tecnico.softeng.tax.services.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface;
import pt.ulisboa.tecnico.softeng.tax.services.remote.dataobjects.RestInvoiceData;

@RestController
@RequestMapping(value = "/rest/tax")
public class TaxRestController {
	private static Logger logger = LoggerFactory.getLogger(TaxRestController.class);

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ResponseEntity<String> submitInvoice(@RequestBody RestInvoiceData invoiceData) {
		logger.info("submitInvoice buyerNif:{}, sellerNif:{}, itemType:{}, value:{}, date:{}, time:{}",
				invoiceData.getBuyerNif(), invoiceData.getSellerNif(), invoiceData.getItemType(),
				invoiceData.getValue(), invoiceData.getDate(), invoiceData.getTime());

		try {
			return new ResponseEntity<String>(TaxInterface.submitInvoice(invoiceData), HttpStatus.OK);
		} catch (TaxException te) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public ResponseEntity<String> cancel(@RequestParam String reference) {
		logger.info("cancel reference:{}", reference);
		try {
			TaxInterface.cancelInvoice(reference);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (TaxException be) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
