package de.codecentric.soap.logging;

import static net.logstash.logback.marker.Markers.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import de.codecentric.soap.common.BusinessException;
import de.codecentric.soap.common.FaultConst;

public class SoapFrameworkLogger {

	private Logger delegateLogger;
	
	private SoapFrameworkLogger() {};
	
	public static <L> SoapFrameworkLogger getLogger(Class<L> class2LogFor) {
		SoapFrameworkLogger frameworkLogger = new SoapFrameworkLogger();
		frameworkLogger.delegateLogger = LoggerFactory.getLogger(class2LogFor);
		return frameworkLogger;
	}
	
	/*
	 * Framework - 0xx
	 */
	public void logHttpHeader(String headers) {
		// see https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-4.5#event-specific-custom-fields
		// net.logstash.logback.marker.Markers.append() enables to directly push a field into elasticsearch, only for one message
		delegateLogger.info(append("http-header-inbound", headers), "000 >>> Header in Inbound-HTTP-Message: {}", headers);
	}
	
	public void successfullyCalledServeEndpointWithMethod(String calledServiceMethod) {
		logInfo("001", "The Serviceendpoint was called successfully with the Method '{}()' - handing over to internal processing.", calledServiceMethod);
	}
	
	
	/*
	 * Controller procedure - 1xx
	 */
	public void transformIncomingJaxbObjects2InternalModel() {
		logInfo("100", "Transformation of incoming JAXB-Bind Objects to internal Model");
	}
	
	public void callBackendWithInternalModel() {
		logInfo("101", "Call Backend with internal Model");
	}
	
	public void transformInternalModel2OutgoingJaxbObjects() {
		logInfo("102", "Transformation internal Model to outgoing JAXB-Bind Objects");
	}
	
	public void checkInternalModelsFunctionalPlausibilityAfterRequest() {
		logInfo("103", "Check internal models functional plausibility after Request");
	}
	
	
	/*
	 * Facade-Mode - 5xx
	 */
	public <T> void facadeModeReturningDummyResponseWithResponseType(Class<T> responseType) {
		logDebug("501", "Facade-Mode: Returning Dummy-Response with ResponseType {}", responseType);
	}
	
	
	/*
	 * Errors - 9xx
	 */
	public void errorAccuredInBackendProcessing(Throwable cause) {
		logError("901", "An Error accured in backend-processing: {}", cause.getMessage());
	}
	
	public void failedToBuildWeatherServiceCompliantSoapFaultDetails(Throwable cause) {
		logError("902", "Failed to build Weather-compliant SoapFault-details: {}\nStacktrace: {}", cause.getMessage(), cause.getStackTrace());
	}	
	
	public void schemaValidationError(FaultConst error, String faultMessage) {
		logDebug("903", error.getMessage() + ": {}", faultMessage);
	}
	
	public void calenderMappingNotWorking(Throwable cause) {
		logDebug("904", "Calendermapping not working, but it´s ok here: {}", cause.getMessage());
	}
	
	public BusinessException problemReadingOrMarshallingDummyResponse(Throwable cause) {
		return new BusinessException(logDebugAndBuildExceptionMessage("905", "Problem reading or marshalling Dummy-Response: {}", cause.getMessage()), cause);
	}
	
	public BusinessException problemReadingPdf(Throwable cause) {
		return new BusinessException(logDebugAndBuildExceptionMessage("906", "Problem reading Pdf-File: {}", cause.getMessage()), cause);
	}

	
	
	/*
	 * Logger-Methods - only private, to use just inside this class
	 */
	private String logDebugAndBuildExceptionMessage(String id, String messageTemplate, Object... parameters) {
		logDebug(id, messageTemplate, parameters);
		return exceptionMessage(id, messageTemplate, parameters);
	}
	
	private void logDebug(String id, String messageTemplate, Object... parameters) {
		String msg = formatMessage(id, messageTemplate);
		delegateLogger.debug(msg, parameters);
	}
	
	private void logInfo(String id, String messageTemplate, Object... parameters) {
		String msg = formatMessage(id, messageTemplate);
		delegateLogger.info(msg, parameters);
	}
	
	private void logError(String id, String messageTemplate, Object... parameters) {
		String msg = formatMessage(id, messageTemplate);
		delegateLogger.error(msg, parameters);
	}
	
	private String formatMessage(String id, String messageTemplate) {
		return id + " >>> " + messageTemplate;
	}
	
	private String exceptionMessage(String id, String messageTemplate, Object... parameters) {
		String message = formatMessage(id, messageTemplate);
	    if(parameters == null || parameters.length == 0) {
	      return message;
	    } else {
	      return MessageFormatter.arrayFormat(message, parameters).getMessage();
	    }
	}
	
}
