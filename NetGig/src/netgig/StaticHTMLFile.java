/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netgig;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author lscrocks123
 */
public class StaticHTMLFile implements PageListener {
    
    private String path = "";
    private String html = "";
    private FileTime modified;
    
    public StaticHTMLFile(String path) {
        
        this.path = WebServer.docRoot + path;
        loadFile();
        
    }
    
    private void loadFile() {
        
        try {
            
            modified = 
                    java.nio.file.Files
                    .getLastModifiedTime(java.nio.file.Paths.get(path));
            
            html = "";
            Scanner in = new Scanner(new File(path));
            
            while(in.hasNextLine()) {
                html += in.nextLine();
            }
            
            in.close();
            
        } catch(IOException e) {
            System.err.println("Failed to load static HTML file: " + path);
        }
        
    }
    
    public String getHTML(Map<String, String> values, METHOD method) {
        
        try {
            
            FileTime temp = java.nio.file.Files
                    .getLastModifiedTime(java.nio.file.Paths.get(path));
            
            if(modified.compareTo(temp) < 0) {
                loadFile();
            }
        
        } catch(IOException e) {
            System.err.println("Failed to compare modified date: " + path);
        }
        
        return html;
        
    }
    
}