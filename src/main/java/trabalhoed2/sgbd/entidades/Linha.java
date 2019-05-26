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
public class Linha {
    public List<Registro> registros;

    public Linha() {
        this.registros = new ArrayList<Registro>();
    }

    public Linha(List<Registro> registros) {
        this.registros = registros;
    }
    
    public void add(Registro reg){
        this.registros.add(reg);
    }
}
