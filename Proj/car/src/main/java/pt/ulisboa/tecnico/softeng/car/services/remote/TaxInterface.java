package pt.ulisboa.tecnico.softeng.car.services.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.softeng.car.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.TaxException;

public class TaxInterface {
	private final Logger logger = LoggerFactory.getLogger(TaxInterface.class);

	private static final String ENDPOINT = "http://localhost:8086";

	public String submitInvoice(RestInvoiceData invoiceData) {
		this.logger.info("submitInvoice buyerNif:{}, sellerNif:{}, itemType:{}, value:{}, date:{}, time:{}",
				invoiceData.getBuyerNif(), invoiceData.getSellerNif(), invoiceData.getItemType(),
				invoiceData.getValue(), invoiceData.getDate(), invoiceData.getTime());

		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<String> result = restTemplate.postForEntity(ENDPOINT + "/rest/tax/submit", invoiceData,
					String.class);
			return result.getBody();
		} catch (HttpClientErrorException e) {
			this.logger.info(
					"submitInvoice HttpClientErrorException buyerNif:{}, sellerNif:{}, itemType:{}, value:{}, date:{}",
					invoiceData.getBuyerNif(), invoiceData.getSellerNif(), invoiceData.getItemType(),
					invoiceData.getValue(), invoiceData.getDate());
			throw new TaxException();
		} catch (Exception e) {
			this.logger.info("submitInvoice Exception buyerNif:{}, sellerNif:{}, itemType:{}, value:{}, date:{}",
					invoiceData.getBuyerNif(), invoiceData.getSellerNif(), invoiceData.getItemType(),
					invoiceData.getValue(), invoiceData.getDate());
			throw new RemoteAccessException();
		}
	}

	public void cancelInvoice(String invoiceReference) {
		this.logger.info("cancelInvoice invoiceReference:{}", invoiceReference);
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.postForObject(ENDPOINT + "/rest/tax/cancel?reference=" + invoiceReference, null,
					String.class);
		} catch (HttpClientErrorException e) {
			this.logger.info("cancelInvoice HttpClientErrorException invoiceReference:{}", invoiceReference);
			throw new TaxException();
		} catch (Exception e) {
			this.logger.info("cancelInvoice Exception invoiceReference:{}", invoiceReference);
			throw new RemoteAccessException();
		}
	}

}
