import java.io.IOException;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

public class ClientInterceptor implements IClientInterceptor{
	Long timeTaken = (long) 0;
	@Override
	public void interceptRequest(IHttpRequest theRequest) {
		// TODO Auto-generated method stub
//		System.out.println(theRequest.getAllHeaders().toString());
	}

	@Override
	public void interceptResponse(IHttpResponse theResponse) throws IOException {
		// TODO Auto-generated method stub
		timeTaken += theResponse.getRequestStopWatch().getMillis();
	}

}
