package myosgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.weaving.WeavingHook;

public class Activator implements BundleActivator {
    private ServiceRegistration<WeavingHook> weavingHookService;

    public void start(BundleContext context) throws Exception {
        WeavingHook wh = new MyWeavingHook();
        weavingHookService = context.registerService(WeavingHook.class, wh, null);
        System.out.println("Hello World!!");
    }


    public void stop(BundleContext context) throws Exception {
        weavingHookService.unregister();
        System.out.println("Goodbye World!!");
    }

}
