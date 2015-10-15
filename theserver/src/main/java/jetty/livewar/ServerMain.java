package jetty.livewar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.webapp.Configuration;
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

        enableAnnotationScanning(server);

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
                break;
            default:
                throw new FileNotFoundException("Unable to configure WebAppContext base resource undefined");
        }

        server.setHandler(context);

        server.start();
        server.dumpStdErr();
        server.join();
    }

    private OperationalMode getOperationalMode() throws IOException
    {
        String warLocation = System.getProperty("org.eclipse.jetty.livewar.LOCATION");
        if (warLocation != null)
        {
            Path warPath = new File(warLocation).toPath().toRealPath();
            if (Files.exists(warPath) && Files.isDirectory(warPath))
            {
                this.basePath = warPath;
                return OperationalMode.PROD;
            }
        }

        Path devPath = new File("../thewebapp").toPath().toRealPath();
        if (Files.exists(devPath) && Files.isDirectory(devPath))
        {
            this.basePath = devPath;
            return OperationalMode.DEV;
        }

        return null;
    }

    private void enableAnnotationScanning(Server server)
    {
        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration",
                "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");
    }
}
