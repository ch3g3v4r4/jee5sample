package org.talk.controller
{
    import com.adobe.cairngorm.control.FrontController;

    import org.talk.command.InitCommand;

    public final class ApplicationController extends FrontController
    {
        // Events
        public static const EVENT_INIT:String = "init";

        public function ApplicationController()
        {
            initializeCommands();
        }

        private function initializeCommands() : void
        {
            addCommand(EVENT_INIT, InitCommand);
        }
  }
}