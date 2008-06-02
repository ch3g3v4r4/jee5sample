package example;

import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.frontend.ClientProxyFactoryBean;

public class WSClient {
    public static void main(String[] args) throws Exception {

        ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
        factory.setServiceClass(Echo.class);
        factory.setAddress("http://localhost:8080/cxfsample/api/echo");
        //factory.getServiceFactory().setDataBinding(new AegisDatabinding());
        Echo client = (Echo)factory.create();
        System.out.println("Invoke sayHi()....");
        System.out.println(client.echo(System.getProperty("user.name")));
        System.exit(0);
//        DynamicClientFactory dcf = DynamicClientFactory.newInstance();
//        dcf.setTemporaryDirectory("f:/tmp");
//        Client client = dcf.createClient("http://localhost:8080/cxfsample/api/echo?wsdl");
//
//        Object[] res = client.invoke("echo", "test echo");
//        System.out.println("Echo response: " + res[0]);
    }
}
