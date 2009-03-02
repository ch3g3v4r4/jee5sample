package com.mycompany.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.ecocoma.service.domain.whois.Whois;
import com.ecocoma.service.domain.whois.WhoisService;
import com.ecocoma.service.domain.whois.WhoisServiceSoap;

@WebService
public class Concatenator {

    @WebMethod
    public String concatenate(String a, String b) {
    	return whois(a);
    }

	private String whois(String a) {
		WhoisService service = new WhoisService();
		WhoisServiceSoap port = service.getWhoisServiceSoap();
		Whois result = port.getWhois("DOM-S81987308M", "", "20.203.133.27");
		return result.getDescription();

	}
}
