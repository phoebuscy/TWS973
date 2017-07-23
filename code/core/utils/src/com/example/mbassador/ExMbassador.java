package com.example.mbassador;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;

import java.io.File;

/**
 * Created by 123 on 2017/3/14.
 */
public class ExMbassador
{
    static MBassador bus = new MBassador(new BusConfiguration()
                                                 .addFeature(Feature.SyncPubSub.Default())
                                                 .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                                                 .addFeature(Feature.AsynchronousMessageDispatch.Default())
                                                 .addPublicationErrorHandler(new IPublicationErrorHandler.ConsoleLogger())
                                                 .setProperty(IBusConfiguration.Properties.BusId,
                                                              "global bus"));


    public static void main(String[] args)
    {
        crtMbassador();
    }


    public static void crtMbassador()
    {

        bus.subscribe(new SimpleFileListener.SimpleFileListener2());
        bus.publish(new File("/tmp/smallfile.csv"));
        bus.post(new File("/tmp/bigfile.csv")).asynchronously();

    }



}
