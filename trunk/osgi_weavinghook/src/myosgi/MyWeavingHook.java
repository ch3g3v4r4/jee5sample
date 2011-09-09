package myosgi;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

public class MyWeavingHook implements WeavingHook {

    @Override
    public void weave(WovenClass wovenClass) {
        System.out.println("tobewoven:" + wovenClass.getClassName());
    }

}
