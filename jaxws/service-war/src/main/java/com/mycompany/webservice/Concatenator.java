package com.mycompany.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.ecocoma.service.domain.whois.Whois;
import com.ecocoma.service.domain.whois.WhoisService;
import com.ecocoma.service.domain.whois.WhoisServiceSoap;

@WebService
public class Concatenator {

    @WebMethod
    public String whois(String name) {
		WhoisService service = new WhoisService();
		WhoisServiceSoap port = service.getWhoisServiceSoap();
		Whois result = port.getWhois("DOM-M89714033M", "", name);
		return result.getDescription().replaceAll("</br>", "\r\n");
    }
}
