package org.talk.command
{
    import mx.core.Application;

    import org.talk.model.ApplicationModel;

    public class InitCommand extends AbstractCommand
    {

        public function InitCommand()
        {
        }

        public override function executeCommand():void
        {
            application.mylabel.text = "Hi";
        }

    }
}