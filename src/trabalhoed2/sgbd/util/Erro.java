/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.util;

import java.io.EOFException;

/**
 *
 * @author pedro.gerico
 */
public class Erro {
    
    public static void log(Exception ex){
        
        if (ex instanceof EOFException)
            return;
        
        System.out.println("Erro: "+ex.toString());
        
    }
    
}
