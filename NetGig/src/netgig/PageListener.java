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

public interface PageListener {
    
    public String getHTML(Map<String,String> values, METHOD method);
    
}
