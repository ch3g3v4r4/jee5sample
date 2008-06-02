package com.mycompany.webservice.service;

import java.math.BigInteger;
import javax.jws.WebService;

@WebService(targetNamespace = "http://www.example.org/DoubleIt",
            portName="DoubleItPort",
            serviceName="DoubleItService",
            endpointInterface="org.example.doubleit.DoubleItPortType")
public class DoubleItPortTypeImpl {

    public BigInteger doubleIt(BigInteger numberToDouble) {
        return numberToDouble.multiply(new BigInteger("2"));
    }
}