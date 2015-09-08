package jetty.livewar;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerMain
{
    private static final String LIVEWAR_LOCATION_PROP = "org.eclipse.jetty.livewar.LOCATION";

    public static void main(String[] args) throws Throwable
    {
        Server server = new Server(8080);

        enableAnnotationScanning(server);

        File warFile = getWarFileReference();

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setWar(warFile.getAbsolutePath());
        server.setHandler(context);

        server.start();
        server.join();
    }

    private static void enableAnnotationScanning(Server server)
    {
        Configuration.ClassList configs = Configuration.ClassList.serverDefault(server);
        configs.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration","org.eclipse.jetty.annotations.AnnotationConfiguration");
    }

    public static File getWarFileReference() throws FileNotFoundException
    {
        String warLocation = System.getProperty(LIVEWAR_LOCATION_PROP);
        if (warLocation == null)
        {
            throw new FileNotFoundException("Unable to determine WAR file location: missing " + LIVEWAR_LOCATION_PROP + " System.property");
        }
        return new File(warLocation);
    }
}
