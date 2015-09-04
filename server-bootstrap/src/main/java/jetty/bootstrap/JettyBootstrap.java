package jetty.bootstrap;

import java.lang.reflect.Method;

public class JettyBootstrap
{
    public static void main(String[] args)
    {
        try
        {
            LiveWarClassLoader cl = LiveWarClassLoader.create();

            Class<?> mainClass = cl.loadClass("jetty.livewar.ServerMain");
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
