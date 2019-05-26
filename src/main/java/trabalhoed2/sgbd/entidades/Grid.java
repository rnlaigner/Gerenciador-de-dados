/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.entidades;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class Grid {
    
    public List<Linha> linhas;
    public List<Atributo> listaAtributos;
    
    public Grid() {
        this.linhas = new ArrayList<Linha>();
    }
    
    public void add(Linha linha){
        linhas.add(linha);
    }

    public Grid(List<Atributo> listaAtributos) {
        this.linhas = new ArrayList<Linha>();
        this.listaAtributos = listaAtributos;
    }
    
    

    

}
