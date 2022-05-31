import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.exceptions.FhirClientConnectionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Patient;

public class SampleClient {

	public static void main(String[] theArgs) {

		// Create a FHIR client
		FhirContext fhirContext = FhirContext.forR4();
		IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
		// client.registerInterceptor(new LoggingInterceptor(false));
		ClientInterceptor intercep = new ClientInterceptor();
		client.registerInterceptor(intercep);
		SortSpec spec = new SortSpec();
		spec.setParamName(Patient.SP_GIVEN);
		spec.setOrder(SortOrderEnum.ASC);
		File file = new File("src/main/resources/lastnames.txt");
		Long result[] = new Long[3];
		for (int i = 0; i < 3; i++) {
			Scanner reader = null;
			try {
				reader = new Scanner(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			intercep.timeTaken = (long) 0;

			// Search for Patient resources
			while (reader.hasNextLine()) {
				String name = reader.nextLine();
				Bundle response = null;
				try {
			 		if (i == 2) {
						response = client.search().forResource("Patient").where(Patient.FAMILY.matches().value(name))
								.sort(spec).returnBundle(Bundle.class)
								.cacheControl(new CacheControlDirective().setNoCache(true)).execute();

					} else {
						response = client.search().forResource("Patient").where(Patient.FAMILY.matches().value(name))
								.sort(spec).returnBundle(Bundle.class).execute();
					}
					for (BundleEntryComponent entry : response.getEntry()) {
						if (entry.getResource() instanceof Patient) {
							Patient patient = (Patient) entry.getResource();
							System.out.println("First name: " + patient.getName().get(0).getGiven().get(0));
							System.out.println("Last name: " + patient.getName().get(0).getFamily());
							System.out.println("Birth date: " + patient.getBirthDate());
						}
					}
				} catch (FhirClientConnectionException ex) {
					System.out.println(ex.getMessage());
				}

			}
			result[i] = intercep.timeTaken / (long) 20;
			
		}
		
		System.out.println("Average response time for 1st iteration: " + result[0] + " ms");
		System.out.println("Average response time for 2st iteration: " + result[1] + " ms");
		System.out.println("Average response time for 3rd iteration: " + result[2] + " ms");

	}

}
