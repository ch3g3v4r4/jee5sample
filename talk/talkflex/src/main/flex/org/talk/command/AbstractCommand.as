package org.talk.command
{
    import mx.core.Application;

    import com.adobe.cairngorm.commands.ICommand;
    import com.adobe.cairngorm.control.CairngormEvent;

    import org.talk.event.ApplicationEvent;
    import org.talk.model.AbstractModel;
    import org.talk.model.ApplicationModel;

    public class AbstractCommand implements ICommand
    {
        protected var application:talkflex;
        protected var applicationModel:ApplicationModel;
        protected var abstractModel:AbstractModel;

        public function AbstractCommand()
        {
        }

        public final function execute(event:CairngormEvent):void
        {
            this.application = talkflex(Application.application);

            this.applicationModel = ApplicationModel.getInstance();

            var talkEvent:ApplicationEvent = ApplicationEvent(event);
            this.abstractModel = AbstractModel(talkEvent.data);

            this.executeCommand();
        }

        public function executeCommand():void
        {
            // Commands should override this method.
        }
    }
}