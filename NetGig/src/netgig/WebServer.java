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

    public WebServer(int portNumber) {
        this.portNumber = portNumber;
    }
    
    public void start() {
        Thread t = new Thread() {
            public void run() {
                try {
                    server = new ServerSocket(portNumber);
                    
                    while(true) {
                        
                        Socket client = server.accept();
                        InputStream in = client.getInputStream();
                        String clientMessage = "";
                        
                        while(client.isConnected()) {
                            if(in.available() > 0) {
                                clientMessage += (char) in.read();
                                if(clientMessage.endsWith("\r")) {
                                    break;
                                }
                            }
                        }
                        
                        clientMessage = clientMessage.trim();
                        
                        if(clientMessage.startsWith("GET")) {
                            
                        } else if(clientMessage.startsWith("POST")) {
                            
                        } else {
                            
                        }
                        
                        System.out.println("|" + clientMessage + "|");
                        in.close();
                        client.close();
                        
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
    
}
