//
//  ========================================================================
//  Copyright (c) Mort Bay Consulting Pty Ltd and others.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package jetty.bootstrap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

public class JettyBootstrap
{
    public static void main(String[] args)
    {
        try
        {
            URL warLocation = JettyBootstrap.class.getProtectionDomain().getCodeSource().getLocation();
            if (warLocation == null)
            {
                throw new IOException("JettyBootstrap not discoverable");
            }

            LiveWarClassLoader clWar = new LiveWarClassLoader(warLocation);
            System.err.println("Using ClassLoader: " + clWar);
            Thread.currentThread().setContextClassLoader(clWar);

            File warFile = new File(warLocation.toURI());
            System.setProperty("org.eclipse.jetty.livewar.LOCATION",warFile.toPath().toRealPath().toString());

            Class<?> mainClass = Class.forName("jetty.livewar.ServerMain",false,clWar);
            Method mainMethod = mainClass.getMethod("main",args.getClass());
            mainMethod.invoke(mainClass,new Object[] { args });
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
            System.exit(-1);
        }
    }
}
