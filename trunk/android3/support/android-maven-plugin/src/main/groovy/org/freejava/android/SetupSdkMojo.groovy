package org.freejava.android
import org.codehaus.gmaven.mojo.GroovyMojo

/**
 * Example Maven2 Groovy Mojo.
 *
 * @goal setupsdk
 */
class SetupSdkMojo
    extends GroovyMojo
{
    /**
     * The hello message to display.
     *
     * @parameter expression="${message}" default-value="Hello World"
     */
    String message
    
    void execute() {
        println "${message}"
    }
}
