package sns.exception;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import sns.exception.HttpFailureResponse.FailureType;

@Component
public class ExceptionProcessor implements Processor {
	private static final int CODE_TECHNICAL_EX = 2000;
	private static final int CODE_BUSINESS_EX = 1000;
	private static final int CODE_UNKNOWN_EX = 2000;

	@Override
	public void process(Exchange arg0) throws Exception {
		HttpFailureResponse response = null;
		Exception exception = (Exception) arg0.getProperties().get("CamelExceptionCaught");
		if (exception != null) {
			if (exception instanceof BusinessException) {
				BusinessException be = (BusinessException) exception;
				response = new HttpFailureResponse(CODE_BUSINESS_EX, be.getMessage(), be.getClass().toString(),
						FailureType.Business);
			} else {
				response = new HttpFailureResponse(CODE_TECHNICAL_EX, exception.getMessage(),
						exception.getClass().toString(), FailureType.Technical);
			}
		} else {
			response = new HttpFailureResponse(CODE_UNKNOWN_EX, "Unknown error", null, FailureType.Technical);
		}

		arg0.getOut().setBody(response);
	}

}
