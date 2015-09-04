package jetty.livewar;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerMain
{
    public static void main(String[] args) throws Throwable
    {
        Server server = new Server(8080);
        
        enableAnnotationScanning(server);
        
        URI warURI = getWarSelfURI();
        
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setWar(warURI.toASCIIString());
        server.setHandler(context);
        
        server.start();
        server.join();
    }
    
    private static void enableAnnotationScanning(Server server)
    {
        Configuration.ClassList configs = Configuration.ClassList.serverDefault(server);
        configs.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");
    }

    public static URI getWarSelfURI() throws FileNotFoundException, URISyntaxException
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String resourceSelf = "/index.html";
        URL url = cl.getResource(resourceSelf);
        if(url == null) {
            throw new FileNotFoundException("Unable to find expected resource: " + resourceSelf);
        }
        return url.toURI().resolve("./");
    }
}
