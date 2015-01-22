/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netgig;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lscrocks123
 */
public class HTMLBuilder implements PageListener {
    
    private METHOD method = METHOD.GET;
    private String title = "Untitled";
    private Map<String, String> values = new HashMap();
    
    public final String getHTML(Map<String, String> values, METHOD method) {
        
        this.method = method;
        
        String buffer = getDocType();
        buffer += "<html>";
        buffer += getHeader();
        buffer += getBody();
        buffer += "</html>";
        
        return buffer;
        
    }
    
    public String onGet() {
        return "<h1>Blank Page</h1><h2>Implement GET to correct this.</h2>";
    }
    
    public String onPost() {
        return onGet();
    }
    
    public final METHOD getMETHOD() {
        return method;
    }
    
    public final Map getValues() {
        return values;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
        
    private String getHeader() {
        return "<head><title>" + title + "</title></head>";
    }

    private String getDocType() {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 "
                + "Transitional//EN\"http://www.w3.org/TR/xhtml1/DTD/"
                + "xhtml1-transitional.dtd\">";
    }

    private String getBody() {
        
        String buffer = "<body>";
        
        switch(method) {
            case GET:
                buffer += onGet();
                break;
            case POST:
                buffer += onPost();
                break;
        }
        
        buffer += "</body>";
        
        return buffer;
        
    }  
    
}
