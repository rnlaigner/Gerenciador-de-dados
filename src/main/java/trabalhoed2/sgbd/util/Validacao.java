/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import trabalhoed2.sgbd.entidades.Atributo;
import trabalhoed2.sgbd.entidades.Catalogo;

/**
 *
 *
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
    
    public static boolean nomeAtributoValido(String nomeAtributo, List<Atributo> listaAtributos){
        List<String> nomesInvalidos = new ArrayList<String>();
        nomesInvalidos.add("codigo");
        nomesInvalidos.add("removido");
        nomesInvalidos.add("prox");
        nomesInvalidos.add("select");
        nomesInvalidos.add("update");
        nomesInvalidos.add("delete");        
        nomesInvalidos.add("set");
        nomesInvalidos.add("where");
        
        for (String str : nomesInvalidos) {
            if (str.equals(nomeAtributo.trim().toLowerCase())) {
                return false;
            }
        }
       
        for (Atributo attr : listaAtributos) {
            if (attr.nomeAtributo.trim().equalsIgnoreCase(nomeAtributo.trim())) {
                return false;
            }
        }
        
        return true;
        
    }
    
    public static boolean tabelaExiste(String nomeTabela){
        try {
            List<Catalogo> listaTabelas = new ConteudoArquivos().getAllTabelas();
            for (Catalogo catalogo : listaTabelas) {
                if (catalogo.nomeTabela.trim().toLowerCase().equals(nomeTabela.trim().toLowerCase())) {
                    return false;
                }
            }
        } 
        catch (Exception ex) {
            Erro.log(ex);
            return false;
        }
        
        return true;
    }
}
