/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.util;

/**
 *
 * @author pedro
 */
public class Validacao {
    
    public static boolean validaString(String str){
        if ((str.length()+2) > Configuracao.tamanhoString) {
            return false;
        }
        if (str.trim().equals("")) {
            return false;
        }
        return true;
    }
}
