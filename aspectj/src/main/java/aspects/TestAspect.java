package aspects;

import java.net.InetSocketAddress;

import net.spy.memcached.MemcachedClient;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class TestAspect {
    MemcachedClient c;

    @Before("call(* mypackage..*(..))")
    public void beforeCall() {
        System.out.println("About to make call to method inside mypackage");
        write();
    }

    private void write() {
        try {
            if (c == null ) c = new MemcachedClient(new InetSocketAddress("localhost", 11211));
            c.set(System.currentTimeMillis() + "", 3600, "Hi");
        } catch (Exception e) {
        }
    }

}
