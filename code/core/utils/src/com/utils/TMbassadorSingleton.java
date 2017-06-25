package com.utils;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 2017/3/16.
 */
public class TMbassadorSingleton
{
    private static Map<String, MBassador> instanceMap = new HashMap<>();
    public static synchronized MBassador getInstance(final String busName)
    {
        if (TStringUtil.nullOrEmptyStr(busName))
        {
            return null;
        }

        MBassador bus = instanceMap.get(busName);
        if (bus == null)
        {
            bus = new MBassador(new BusConfiguration()
                                        .addFeature(Feature.SyncPubSub.Default())
                                        .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                                        .addFeature(Feature.AsynchronousMessageDispatch.Default())
                                        .addPublicationErrorHandler(new IPublicationErrorHandler.ConsoleLogger())
                                        .setProperty(IBusConfiguration.Properties.BusId,
                                                     busName));
            instanceMap.put(busName, bus);
        }
        return bus;
    }



}
