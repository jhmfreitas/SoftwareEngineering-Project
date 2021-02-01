package pt.ulisboa.tecnico.softeng.hotel.services.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.RemoteAccessException;

public class BankInterface {
    private final Logger logger = LoggerFactory.getLogger(BankInterface.class);

    private static final String ENDPOINT = "http://localhost:8082";

    public String processPayment(RestBankOperationData bankOperationData) {
        this.logger.info("processPayment iban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
                bankOperationData.getSourceIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
                bankOperationData.getTransactionReference());

        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.postForObject(
                    ENDPOINT + "/rest/banks/accounts/" + bankOperationData.getSourceIban() + "/processPayment",
                    bankOperationData, String.class);
        } catch (HttpClientErrorException e) {
            this.logger.info(
                    "processPayment HttpClientErrorException  iban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
                    bankOperationData.getSourceIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
                    bankOperationData.getTransactionReference());
            throw new BankException();
        } catch (Exception e) {
            this.logger.info("processPayment Exception iban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
                    bankOperationData.getSourceIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
                    bankOperationData.getTransactionReference());
            throw new RemoteAccessException();
        }
    }

    public String cancelPayment(String reference) {
        this.logger.info("cancelPayment reference:{}", reference);

        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.postForObject(ENDPOINT + "/rest/banks/cancel?reference=" + reference, null,
                    String.class);
        } catch (HttpClientErrorException e) {
            this.logger.info("cancelPayment HttpClientErrorException reference:{}", reference);
            throw new BankException();
        } catch (Exception e) {
            this.logger.info("cancelPayment Exception reference:{}", reference);
            throw new RemoteAccessException();
        }
    }

}
