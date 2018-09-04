package requestecho;

import io.muserver.MuServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.Map;

import static io.muserver.MuServerBuilder.muServer;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        Map<String, String> settings = System.getenv();
        int port = Integer.parseInt(settings.getOrDefault("APP_PORT", "7928"));
        String appName = settings.getOrDefault("APP_NAME", "request-echo");

        MuServer server = muServer()
            .withHttpPort(port)
            .addHandler((req, resp) -> {
                resp.contentType("text/plain");

                PrintWriter writer = resp.writer();
                writer.println("Request URI: " + req.uri());
                writer.println(" Server URI: " + req.serverURI());
                writer.println("Request Raw Path: " + req.uri().getRawPath());
                writer.println(" Server Raw Path: " + req.serverURI().getRawPath());
                writer.println("Server info: " + req.server());
                writer.println("Headers:");
                for (Map.Entry<String, String> entry : req.headers().entries()) {
                    writer.println("    " + entry.getKey() + ": " + entry.getValue());
                }
                writer.println("End of message");

                return false;
            })
            .start();

        log.info("Started app at " + server.uri().resolve("/" + appName));
    }


}