package ru.temaslikov.searchFactsEngine;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.OptionalDouble;

import static java.lang.System.currentTimeMillis;

/**
 * Created by temaslikov on 4.06.2017.
 */


public class Main {

    public static void main(String[] args) throws Exception {

        // runInfoboxParseService();
        runServer();
    }

    static void runInfoboxParseService() throws IOException {

        double start = currentTimeMillis();
        System.out.println("INFO: InfoboxParseService start at " +
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                .format(LocalDateTime.now()));

        InfoboxParseService infoboxParseService = new InfoboxParseService();
        infoboxParseService.loadTitleMap(Constants.titlePath);
        infoboxParseService.run();

        System.out.println("INFO: InfoboxParseService finished at " +
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                        .format(LocalDateTime.now()));
        System.out.println("INFO: duration of work InfoboxParseService: " +
                (currentTimeMillis() - start) / 1000 + " seconds");
    }

    private static void runServer() throws Exception {
        Server server = new Server(8080);

        HandlerCollection handlers = new HandlerCollection();

        WebAppContext webapp = new WebAppContext();
        webapp.setResourceBase(Paths.get("src", "main", "webapp").toString());
        webapp.setDescriptor(Paths.get("target", "web.xml").toString());
        webapp.setContextPath("/");

        webapp.setAttribute("org.eclipse.jetty.containerInitializers", new ContainerInitializer(new JettyJasperInitializer(), null));
        webapp.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());

        handlers.addHandler(webapp);

        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");

        server.setHandler(handlers);

        server.start();
        System.out.println("Server started");
        java.util.logging.Logger.getGlobal().info("Server started");
        server.join();
    }
}





