package org.talk.model
{
    [Bindable]
    public class ApplicationModel extends AbstractModel
    {
        private static var instance: ApplicationModel;

        public static function getInstance():ApplicationModel
        {
            if ( instance == null )
            {
              instance = new ApplicationModel();
            }
            return instance;
        }

        //Constructor should be private but current AS3.0 does not allow it yet (?)...
        public function ApplicationModel()
        {
            if ( instance != null )
            {
                throw new Error( "Only one ApplicationModel instance should be instantiated" );
            }
        }
    }
}