/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netgig;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author lscrocks123
 */
public class WebServer {
    
    private int portNumber;
    private ServerSocket server;
    private Map<String, PageListener> listeners = new HashMap();
    private boolean running = false;
    private final int defaultTimeout = 1000;
    public static String docRoot = "";

    public WebServer(int portNumber) {
        this.portNumber = portNumber;
    }
    
    private void getValues(String s, Map<String, String> values) throws ClientException {
        
        String key = "";
        String value = "";
        boolean onKey = true;
        
        for(int i = 0; i < s.length(); i++) {
            switch(s.charAt(i)) {
                case '+':
                    if(onKey)   key += " ";
                    else        value += " ";
                case '=':
                    onKey = false;
                    break;
                case ' ':
                    values.put(key, value);
                    return;
                case '&':
                    values.put(key, value);
                    key = "";
                    value = "";
                    onKey = true;
                    break;
                default:
                    if(onKey)   key += s.charAt(i);
                    else        value += s.charAt(i);
            }
        }
        
        throw new ClientException("getValues parse error");
        
    }
    
    private METHOD getMethod(String requestLine) throws ClientException {
        
        if(requestLine.startsWith("GET ")) {
            return METHOD.GET;
        } else if(requestLine.startsWith("POST ")) {
            return METHOD.POST;
        } else {
            throw new ClientException("getMethod unknown/absent request method");
        }
        
    }
    
    private String getPath(String requestLine) throws ClientException {
        
        int i = requestLine.indexOf(" ");
        int j = requestLine.lastIndexOf(" ");
        
        if(i != j) {
            
            String path = requestLine.substring(i+1, j);
            if(path.contains("?")) {
                path = path.substring(0, path.indexOf("?"));
            }
            
            for(i = 0; i < path.length(); i++) {
                if(Character.isLetterOrDigit(path.charAt(i)) 
                        || path.charAt(i) == '/'
                        || path.charAt(i) == '.') continue;
                throw new ClientException("getPath invalid path");
            }
            
            if(path.contains(".") && path.indexOf(".") < path.lastIndexOf("/")) {
                throw new ClientException("getPath invalid path");
            }
            
            while(path.contains("//")) {
                path = path.substring(0, path.indexOf("//")) +
                        path.substring(path.indexOf("//") + 1);
            }
            
            while(path.contains("..")) {
                path = path.substring(0, path.indexOf("..")) +
                        path.substring(path.indexOf("..") + 1);
            }
            
            if(path.equals("/")) {
                return "index.html";
            }
            
            while(path.startsWith("/")) {
                path = path.substring(1);
            }
            
            return path;
            
        } else {
            throw new ClientException("getPath absent path");
        }
        
    }
    
    private String readLine(Socket client) throws ClientException {
        
        String s = "";
        char c;
        
        while(true) {
            c = readChar(client);
            if(c == '\n' || c == '\r') {
                return s;
            }
            s += c;
        }
        
    }
    
    private char readChar(Socket client) throws ClientException {
        
        int timeout = defaultTimeout;
        
        while(client.isConnected() && timeout-- > 0) {
                
                try {
                    
                    if(client.getInputStream().available() > 0) {
                        return (char) client.getInputStream().read();
                    }
                    
                    Thread.sleep(10);
                    
                } catch(InterruptedException e) {
                    throw new ClientException("readChar thread issue");
                } catch(IOException e) {
                    throw new ClientException("readChar io issue");
                }
        }
        
        throw new ClientException("readChar client timeout");
    }
    
    private void parseHeaderFields(Socket client, Map<String, String> header) 
                                                        throws ClientException {
        
        String line = "";
        String key;
        String value;
        
        boolean lastWasBlank = false;
        
        while(true) {
            try {
                line = readLine(client);
            } catch(ClientException e) {
                throw new ClientException("parseHeaderFields read error");
            }
            if(line.length() == 0) {

                if(lastWasBlank) {
                    break;
                } else {
                    lastWasBlank = true;
                    continue;
                }
                
            } else {
                lastWasBlank = false;
                if(line.contains(":")) {
                    key = line.substring(0, line.indexOf(":"));
                    value = line.substring(line.indexOf(":") + 1);
                    key = key.trim();
                    value = value.trim();
                    header.put(key, value);
                }
            }
        }
        
    }
    
    private void processRequest(Socket client) {
        try {
            String header = readLine(client);
            
            Map<String, String> values = new HashMap();
            Map<String, String> request = new HashMap();
            String path = getPath(header);
            METHOD method = getMethod(header);
            
            switch(method) {
                case GET:
                    if(header.contains("?")) {
                        getValues(header.substring(header.lastIndexOf("?")+1), values);
                    }
                    parseHeaderFields(client, request);
                    break;
                case POST:
                    parseHeaderFields(client, request);
                    break;
            }
            
            for(String key : values.keySet()) {
                System.out.println(key + "=\"" + values.get(key) + "\"");
            }
            
            PrintStream out = new PrintStream(client.getOutputStream());
            String response = "HTTP/1.1 200 OK\n" +
                                "Server: NetGig Custom\n" +
                                "Content-Type: text/html; charset=utf-8\n" +
                                "Content-Length: ";
            out.print(response);
            
            PageListener temp = listeners.get(path);
            String html = "";
            
            if(temp == null) {
                html = "<html><head><title>404: File Not Found</title>"
                        + "</head><body><h1>404 Error: File Not Found</h1>"
                        + "<h2>NetGig Server</h2></body></html>";
            } else {
                html = temp.getHTML(values, method);
            }
            
            out.print(html.length());
            out.print("\nConnection: close\n\n");
            out.print(html);
            out.flush();
            
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                System.err.println("client wait sleep error");
            }
            
            out.close();
            client.close();
            
        } catch(ClientException e) {
            System.err.println("Client Error: " + e);
        } catch(IOException e) {
            System.err.println("Connection Error: " + e);
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
