package org.talk.event
{
    import com.adobe.cairngorm.control.CairngormEvent;

    public class ApplicationEvent extends CairngormEvent
    {
        public function ApplicationEvent(type:String, data:*, bubbles:Boolean=false, cancelable:Boolean=false)
        {
            super(type, bubbles, cancelable);
            super.data = data;
        }
    }
}