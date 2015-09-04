package jetty.bootstrap;

import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;

public class LiveWarClassLoader extends URLClassLoader
{
    public static LiveWarClassLoader create() throws FileNotFoundException
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String resourceLocation = "WEB-INF/jetty-server/org/eclipse/jetty/server/Server.class";
        URL url = cl.getResource(resourceLocation);
        if (url == null)
        {
            throw new FileNotFoundException("Unable to find Classpath Resource: " + resourceLocation);
        }
        return new LiveWarClassLoader(new URL[]{url});
    }

    public LiveWarClassLoader(URL[] urls)
    {
        super(urls);
    }
}
