package aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class TestAspect {

    @Before("call(* mypackage..*(..))")
    public void beforeCall() {
        System.out.println("About to make call to method inside mypackage");
    }

}
