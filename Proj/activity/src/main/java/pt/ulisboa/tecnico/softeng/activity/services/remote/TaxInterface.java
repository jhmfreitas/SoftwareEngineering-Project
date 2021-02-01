package pt.ulisboa.tecnico.softeng.activity.services.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.TaxException;

public class TaxInterface {
	private static Logger logger = LoggerFactory.getLogger(TaxInterface.class);

	private static String ENDPOINT = "http://localhost:8086";

	public String submitInvoice(RestInvoiceData invoiceData) {
		logger.info("submitInvoice buyerNif:{}, sellerNif:{}, itemType:{}, value:{}, date:{}, time:{}",
				invoiceData.getBuyerNif(), invoiceData.getSellerNif(), invoiceData.getItemType(),
				invoiceData.getValue(), invoiceData.getDate(), invoiceData.getTime());

		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<String> result = restTemplate.postForEntity(ENDPOINT + "/rest/tax/submit", invoiceData,
					String.class);
			return result.getBody();
		} catch (HttpClientErrorException e) {
			logger.info(
					"submitInvoice HttpClientErrorException buyerNif:{}, sellerNif:{}, itemType:{}, value:{}, date:{}",
					invoiceData.getBuyerNif(), invoiceData.getSellerNif(), invoiceData.getItemType(),
					invoiceData.getValue(), invoiceData.getDate());
			throw new TaxException();
		} catch (Exception e) {
			logger.info("submitInvoice Exception buyerNif:{}, sellerNif:{}, itemType:{}, value:{}, date:{}",
					invoiceData.getBuyerNif(), invoiceData.getSellerNif(), invoiceData.getItemType(),
					invoiceData.getValue(), invoiceData.getDate());
			throw new RemoteAccessException();
		}
	}

	public void cancelInvoice(String invoiceReference) {
		logger.info("cancelInvoice invoiceReference:{}", invoiceReference);
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.postForObject(ENDPOINT + "/rest/tax/cancel?reference=" + invoiceReference, null, String.class);
		} catch (HttpClientErrorException e) {
			logger.info("cancelInvoice HttpClientErrorException invoiceReference:{}", invoiceReference);
			throw new TaxException();
		} catch (Exception e) {
			logger.info("cancelInvoice Exception invoiceReference:{}", invoiceReference);
			throw new RemoteAccessException();
		}
	}

}
