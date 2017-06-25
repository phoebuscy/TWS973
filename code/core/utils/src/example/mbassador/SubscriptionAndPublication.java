package example.mbassador;

/**
 * Created by 123 on 2017/3/14.
 */


import com.utils.ReturnObj;
import com.utils.TMbassadorSingleton;
import net.engio.mbassy.bus.MBassador;

import java.io.File;
import java.math.BigInteger;

/**
 * @author bennidi
 *         Date: 07.10.15
 */
public class SubscriptionAndPublication
{
    /*

    static MBassador bus = new MBassador(new BusConfiguration()
                                                 .addFeature(Feature.SyncPubSub.Default())
                                                 .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                                                 .addFeature(Feature.AsynchronousMessageDispatch.Default())
                                                 .addPublicationErrorHandler(new IPublicationErrorHandler.ConsoleLogger())
                                                 .setProperty(IBusConfiguration.Properties.BusId,
                                                              "global bus")); // this is used for identification in #toString
                                                              */
    static MBassador bus = TMbassadorSingleton.getInstance("myfirstBus");
    static MBassador bus2 = TMbassadorSingleton.getInstance("myfirstBus2");

    public static void main(String[] args)
    {
        TTestFrame tTestFrame = new TTestFrame();
      //  bus.subscribe(tTestFrame);

        // Listeners are subscribed by passing them to the #subscribe() method
      //  bus.subscribe(new ListenerDefinition.SyncAsyncListener());

        // #subscribe() is idem-potent => Multiple calls to subscribe do NOT add the listener more than once (set semantics)
        Object listener = new ListenerDefinition.SyncAsyncListener();
        bus.subscribe(listener);
        bus.subscribe(listener);

        // Classes without handlers will be silently ignored
        bus.subscribe(new Object());
        bus.subscribe(new String());

        bus.publishAsync(new File("/tmp/random.csv")); //returns immediately, publication will continue asynchronously
        bus.post(new File("/tmp/random.csv")).asynchronously(); // same as above

        bus.publish("some message");   // will return after each handler has been invoked
        bus.post("some message").now(); // same as above

        Object lsn = new ListenerDefinition.CustomInvocationListener();

        bus.subscribe(lsn);
        bus.post(new File("/tmp/random.csv")).asynchronously(); // same as above

      //  bus.subscribe(new TTestFrame());
      //  bus.publish("this is for ttesFram");

      //  bus2.subscribe(new TTestFrame());
      //  bus2.publish("bus2: this is for ttesFram");
        bus.publish(BigInteger.valueOf(50));
        bus.publish(BigInteger.valueOf(150));

        bus.publish(new ReturnObj());

        int a = 1;



    }
}