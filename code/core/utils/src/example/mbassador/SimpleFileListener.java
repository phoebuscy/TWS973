package example.mbassador;


import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Invoke;

import java.io.File;

/**
 * Created by 123 on 2017/3/14.
 */

public class SimpleFileListener
{

    static class SimpleFileListener2
    {

        /**
         * Any published message will be delivered to this handler (as it consumes any object of type Object.class)
         * Delivery is done using synchronous invocation, i.e. the handler is called from the thread running the message
         * publication.
         */
        @Handler
        public void synchronousHandler(Object message)
        {
            // do something
            int a = 1;
        }

        /**
         * According to the handler configuration, this handler is invoked asynchronously, meaning that each handler
         * invocation runs in a thread different from the one that runs the initial message publication.
         * <p>
         * This feature is useful for computationally expensive or IO-bound tasks.
         */
        @Handler(delivery = Invoke.Asynchronously)
        public void asynchronousHandler(File message)
        {
            // do something more expensive here
            int a = 1;
        }

    }
}
