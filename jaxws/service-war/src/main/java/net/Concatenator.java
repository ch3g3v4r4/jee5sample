package net;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.xml.ws.Endpoint;

@WebService
public class Concatenator {

	@WebMethod
	public String concatenate(String a, String b) {
		return a + " " + b;
	}

	public static void main(String[] args) {
		// e.g.
		String publishUrl = "http://localhost:8080/concatservice";

		System.out.println("publishing service at: " + publishUrl);
		Concatenator concatenator = new Concatenator();
		Endpoint endpoint = Endpoint.publish(publishUrl, concatenator);
	}
}