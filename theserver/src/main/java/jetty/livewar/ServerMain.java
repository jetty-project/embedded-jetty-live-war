package jetty.livewar;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerMain
{
    private static final String LIVEWAR_LOCATION_PROP = "org.eclipse.jetty.livewar.LOCATION";

    public static void main(String[] args) throws Throwable
    {
        Server server = new Server(8080);

        enableAnnotationScanning(server);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        
        // Configure from System Property
        String warLocation = System.getProperty(LIVEWAR_LOCATION_PROP);
        if (warLocation == null)
        {
            // Attempt to use relative Dev Path
            Path devBasePath = new File("../thewebapp").toPath().toRealPath();
            if (Files.exists(devBasePath))
            {
                // Configuring from Development Base
                context.setBaseResource(new PathResource(devBasePath.resolve("src/main/webapp")));
                // Add webapp compiled classes & resources (copied into place from src/main/resources)
                Path classesPath = devBasePath.resolve("target/thewebapp/WEB-INF/classes");
                context.setExtraClasspath(classesPath.toAbsolutePath().toString());
            }
            else
            {
                throw new FileNotFoundException("Unable to determine WAR file location: missing " + LIVEWAR_LOCATION_PROP + " System.property");
            }
        }
        else
        {
            // Using System Property
            context.setWar(new File(warLocation).getAbsolutePath());
        }
        
        server.setHandler(context);
        
        server.start();
        server.dumpStdErr();
        server.join();
    }

    private static void enableAnnotationScanning(Server server)
    {
        Configuration.ClassList classlist = Configuration.ClassList
                .setServerDefault(server);
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration",
                "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        classlist.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");
    }
}
