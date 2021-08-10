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

package jetty.livewar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerMain
{
    enum OperationalMode
    {
        DEV,
        PROD
    }

    private Path basePath;

    public static void main(String[] args)
    {
        try
        {
            new ServerMain().run();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    private void run() throws Throwable
    {
        Server server = new Server(8080);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");

        switch (getOperationalMode())
        {
            case PROD:
                // Configure as WAR
                context.setWar(basePath.toString());
                break;
            case DEV:
                // Configuring from Development Base
                context.setBaseResource(new PathResource(basePath.resolve("src/main/webapp")));
                // Add webapp compiled classes & resources (copied into place from src/main/resources)
                Path classesPath = basePath.resolve("target/thewebapp/WEB-INF/classes");
                context.setExtraClasspath(classesPath.toAbsolutePath().toString());
                server.setDumpAfterStart(true);
                break;
            default:
                throw new FileNotFoundException("Unable to configure WebAppContext base resource undefined");
        }

        server.setHandler(context);

        server.start();
        server.join();
    }

    private OperationalMode getOperationalMode() throws IOException
    {
        // Property set by jetty.bootstrap.JettyBootstrap
        String warLocation = System.getProperty("org.eclipse.jetty.livewar.LOCATION");
        if (warLocation != null)
        {
            Path warPath = new File(warLocation).toPath().toRealPath();
            if (Files.exists(warPath) && Files.isRegularFile(warPath))
            {
                this.basePath = warPath;
                return OperationalMode.PROD;
            }
        }

        // We are in development mode, likely building and testing from an IDE.
        Path devPath = new File("../thewebapp").toPath().toRealPath();
        if (Files.exists(devPath) && Files.isDirectory(devPath))
        {
            this.basePath = devPath;
            return OperationalMode.DEV;
        }

        return null;
    }
}
