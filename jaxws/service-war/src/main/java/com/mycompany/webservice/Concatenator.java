package com.mycompany.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class Concatenator {

    @WebMethod
    public String whois(String name) {
		return "Hello" + name;
    }
}
