/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netgig;

import java.util.Map;

/**
 *
 * @author lscrocks123
 */
public class NetGig {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(System.getProperty("os.name").startsWith("Windows")) {
            WebServer.docRoot = "c:/public_html/";
        } else {
            WebServer.docRoot = "/home/pi/html/";
        }
        WebServer server = new WebServer(80);
        server.setPageListener("index.html", new StaticHTMLFile("index.html"));
        server.setPageListener("about.html", new StaticHTMLFile("about.html"));
        server.setPageListener("responder.html", new HTMLBuilder() {
            public String onGet() {
                Map<String, String> values = getValues();
                String html = "";
                if(values.containsKey("name") && values.containsKey("message")) {
                    html = "<h1>NetGig Server</h1>";
                    html += "<h2>Thank You!</h2>";
                    html += "<dl><dt>Name: </dt><dd>" + values.get("name");
                    html += "</dd><dt>Message: </dt><dd>" + values.get("message");
                    html += "</dd>";
                }
                return html;
            }
        });
        server.start();
    }
}
