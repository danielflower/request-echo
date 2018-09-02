package requestecho;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;

public class App {
    public static final Logger log = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) throws Exception {
        Map<String, String> settings = System.getenv();

        // When run from app-runner, you must use the port set in the environment variable APP_PORT
        int port = Integer.parseInt(settings.getOrDefault("APP_PORT", "7928"));
        // All URLs must be prefixed with the app name, which is got via the APP_NAME env var.
        String appName = settings.getOrDefault("APP_NAME", "request-echo");
        String env = settings.getOrDefault("APP_ENV", "local"); // "prod" or "local"
        boolean isLocal = "local".equals(env);
        log.info("Starting " + appName + " in " + env + " on port " + port);

        Server jettyServer = new Server(new InetSocketAddress("localhost", port));
        jettyServer.setStopAtShutdown(true);

        HandlerList handlers = new HandlerList();
        // TODO: set your own handlers
        handlers.addHandler(resourceHandler(isLocal));

        // you must serve everything from a directory named after your app
        ContextHandler ch = new ContextHandler();
        ch.setContextPath("/" + appName);
        ch.setHandler(handlers);
        jettyServer.setHandler(ch);

        try {
            jettyServer.start();
        } catch (Throwable e) {
            log.error("Error on start", e);
            System.exit(1);
        }

        log.info("Started app at http://localhost:" + port + ch.getContextPath());
        jettyServer.join();
    }

    private static Handler resourceHandler(boolean useFileSystem) {
        ResourceHandler resourceHandler = new ResourceHandler();
        if (useFileSystem) {
            resourceHandler.setResourceBase("src/main/resources/web");
            resourceHandler.setMinMemoryMappedContentLength(-1);
        } else {
            resourceHandler.setBaseResource(Resource.newClassPathResource("/web", true, false));
        }
        resourceHandler.setEtags(true);
        resourceHandler.setWelcomeFiles(new String[] {"index.html"});
        return resourceHandler;
    }

}