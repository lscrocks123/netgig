/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package netgig;

import java.io.IOException;

/**
 *
 * @author lscrocks123
 */
public class ClientException extends IOException {
    
    private String msg;
    
    public ClientException(String msg) {
        super();
        this.msg = msg;
    }
    
    public String toString() {
        return "TimeoutException: " + msg + ".";
    }
    
}
