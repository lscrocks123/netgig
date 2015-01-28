/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netgig;

import java.io.InputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.net.Socket;

/**
 *
 * @author lscrocks123
 */
public class WebServer {
    
    private int portNumber;
    private ServerSocket server;
    private Map<String, PageListener> listeners = new HashMap();
    private boolean running = false;

    public WebServer(int portNumber) {
        this.portNumber = portNumber;
    }
    
    private void processRequest(Socket client) {
        try {
            InputStream in = client.getInputStream();
            String clientHeader = "";
            String path = "";
            String getValues = "";
            String clientBody = "";
            
            boolean onHead = true;
            while(client.isConnected()) {
                if(in.available() > 0) {
                    if(onHead) {
                        clientHeader += (char) in.read();
                        if(clientHeader.endsWith("\r")) {
                            onHead = false;
                        }
                    } else {
                        clientBody += (char) in.read();
                        if(clientBody.endsWith("\n")) {
                            break;
                        }
                    }
                }
            }

            clientHeader = clientHeader.trim();
            path = clientHeader
                    .substring(clientHeader.indexOf(" ") + 1);
            if(path.contains("?")) {
                getValues = path.substring(path.indexOf("?")+1, path.indexOf(" "));
                path = path.substring(0, path.indexOf("?"));
            } else {
                path = path.substring(0, path.indexOf(" "));
            }

            if(clientHeader.startsWith("GET")) {

                Map<String,String> values = new HashMap();

                while(getValues.length() != 0) {

                    String key = getValues.substring(0, 
                            getValues.indexOf("="));

                    String value = "";
                    if(getValues.contains("&")) {
                        value = getValues.substring( 
                            getValues.indexOf("=")+1,
                            getValues.indexOf("&"));
                        getValues = getValues.substring(
                                getValues.indexOf("&")+1);
                    } else {
                        value = getValues.substring( 
                            getValues.indexOf("=")+1);
                        getValues = "";
                    }

                    values.put(key, value);

                }

                for(String k : values.keySet()) {
                    System.out.println("Key: \"" + k + "\"");
                    System.out.println("Value: \"" 
                            + values.get(k) + "\"");

                }

            } else if(clientHeader.startsWith("POST")) {
                
            } else {
                
            }

            System.out.println("|" + clientHeader + "|");
            System.out.println("|" + clientBody + "|");
            in.close();
            client.close();
        } catch(Exception e) {
            System.err.println("Client Error: " + e);
        }
    }
    
    public void start() {
        running = true;
        
        Thread t = new Thread() {
            public void run() {
                try {
                    
                    server = new ServerSocket(portNumber);
                    
                    while(running) {
                        final Socket client = server.accept();
                        
                        Thread clientThread = new Thread() {
                            public void run() {
                                processRequest(client);
                            }
                        };
                        clientThread.start();
                    }
                    
                } catch(Exception e) {
                    System.err.println("Server Error: " + e);
                    System.exit(0);
                }
            }
        };
        t.start();
    }
    
    public void setPageListener(String path, PageListener listener) {
        if(listeners.containsKey(path)) {
            listeners.remove(path);
        }
        listeners.put(path, listener);
    }
    
    public void stop() {
        running = false;
    }
    
}
