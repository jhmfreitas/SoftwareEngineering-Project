package pt.ulisboa.tecnico.softeng.broker.domain

import org.joda.time.LocalDate
import spock.lang.Shared

trait SharedDefinitions {
    @Shared def BROKER_CODE = "BR01"
    @Shared def BROKER_NAME = "WeExplore"
    @Shared def BROKER_IBAN = "BROKER_IBAN"
    @Shared def BROKER_NIF_AS_BUYER = "buyerNIF"
    @Shared def NIF_AS_BUYER = "buyerNIF"
    @Shared def BROKER_NIF = "sellerNIF"
    @Shared def IBAN_BUYER = "IBAN"
    @Shared def NIF_CUSTOMER = "123456789"
    @Shared def OTHER_NIF = "987654321"
    @Shared def CLIENT_NIF = "123456789"
    @Shared def DRIVING_LICENSE = "IMT1234"
    @Shared def AGE = 20
    @Shared def NUMBER_OF_BULK = 20
    @Shared def MARGIN = 30
    @Shared def CLIENT_IBAN = "BK011234567"

    @Shared def REFERENCE = "REFERENCE"
    @Shared def PAYMENT_CONFIRMATION = "PaymentConfirmation"
    @Shared def PAYMENT_CANCELLATION = "PaymentCancellation"
    @Shared def ACTIVITY_CONFIRMATION = "ActivityConfirmation"
    @Shared def ACTIVITY_CANCELLATION = "ActivityCancellation"
    @Shared def ROOM_CONFIRMATION = "RoomConfirmation"
    @Shared def ROOM_CANCELLATION = "RoomCancellation"
    @Shared def RENTING_CONFIRMATION = "RentingConfirmation"
    @Shared def RENTING_CANCELLATION = "RentingCancellation"
    @Shared def INVOICE_REFERENCE = "InvoiceReference"
    @Shared def INVOICE_DATA = "InvoiceData"
    @Shared def SINGLE = "SINGLE"
    @Shared def DOUBLE = "DOUBLE"
    @Shared def REF_ONE = "ref1"
    @Shared def REF_TWO = "ref2"
    @Shared def REF_THREE = "ref3"
    @Shared def REF_FOUR = "ref4"

    @Shared def BEGIN = new LocalDate(2016, 12, 19)
    @Shared def END = new LocalDate(2016, 12, 21)
    @Shared def ARRIVAL = new LocalDate(2016, 12, 19)
    @Shared def DEPARTURE = new LocalDate(2016, 12, 21)
}
