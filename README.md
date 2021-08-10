Live/Executable War with Jetty Embedded
=======================================

This project should provide a baseline for those investigating the use of Embedded Jetty
from the point of view of a Self Executing WAR file.

The project has 4 main parts:

 1. `/thewebapp/` - this is the WAR file, the webapp, as it exists in its native format, with normal maven
    `<packaging>war</packaging>` and a produced artifact that is just a WAR file that isn't (yet) self-executing.
 2. `/theserver/` - this is the Embedded Jetty Server `jetty.livewar.ServerMain.main(String args[])` which you 
    customize to initialize your Jetty server and its WebApp.  This project also is the place where you customize
    for things like JDBC servers libraries, JNDI, logging, etc.   This project produces a uber-jar with all the
    dependencies needed to run the server.  Special care is taken with the `maven-shade-plugin` to merge
    `META-INF/services/` files.
 3. `/server-bootstrap/` - this contains 2 small classes that sets up a `LiveWarClassLoader` from the content
    in the live WAR and then runs `jetty.livewar.ServerMain.main(String args[])` from this new ClassLoader.
    This project also contains the live `META-INF/MANIFEST.MF` that the live WAR will need/use
 4. `/livewar-assembly/` - this is the project that ties together the above 3 projects into a Live/Executable WAR file.
    The artifacts from from the above 3 projects are unpacked by the `maven-assembly-plugin` and put into
    place where they will be most functional (and safe).  For example, the server classes from
    `/theserver/` are placed in `/WEB-INF/jetty-server/` to make them inaccessible from Web Clients
    accessing the WAR file.

Note: there are 3 files present in your new assembled WAR file that you should be aware of, as these
files can be downloaded by a Web Client as static content if you use this setup.

 * `/jetty/bootstrap/JettyBootstrap.class`
 * `/jetty/bootstrap/LiveWarClassLoader.class`
 * `/META-INF/MANIFEST.MF`

The example project is setup in such a way that information present in these bootstrap files should not
reveal private or sensitive information about your Server or its operations.  Merely that the Webapp
can be started as a Live/Executable WAR file.

Example:

```shell
$ mvn clean install
...(snip lots of build output)...
$ java -jar livewar-assembly/target/livewar-example.war 
Using ClassLoader: jetty.bootstrap.LiveWarClassLoader[file:/home/joakim/code/jetty/github-jetty.project/embedded-jetty-live-war/livewar-assembly/target/livewar-example.war]
2021-08-10 15:41:23.499:INFO :oejs.Server:main: jetty-11.0.6; built: 2021-06-29T16:16:33.537Z; git: 69469432898becda3aed32a32d4b0adbb7b6daee; jvm 11.0.12+7
2021-08-10 15:41:23.933:INFO :oejw.StandardDescriptorProcessor:main: NO JSP Support for /, did not find org.eclipse.jetty.jsp.JettyJspServlet
2021-08-10 15:41:23.996:INFO :oejss.DefaultSessionIdManager:main: Session workerName=node0
2021-08-10 15:41:24.018:INFO :oejsh.ContextHandler:main: Started o.e.j.w.WebAppContext@49dc7102{/,file:///tmp/jetty-0_0_0_0-8080-livewar-example_war-_-any-1525063685359350259/webapp/,AVAILABLE}{/home/joakim/code/jetty/github-jetty.project/embedded-jetty-live-war/livewar-assembly/target/livewar-example.war}
2021-08-10 15:41:24.029:INFO :oejs.AbstractConnector:main: Started ServerConnector@289d1c02{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
2021-08-10 15:41:24.041:INFO :oejs.Server:main: Started Server@275710fc{STARTING}[11.0.6,sto=0] @813ms
```


