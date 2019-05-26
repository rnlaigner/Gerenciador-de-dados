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
public class RetornoGrid {
    private Grid grid;
    private List<String> selecionados;

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        
        
        Grid novaGrid = new Grid(grid.listaAtributos);
        
        for (Linha linha : grid.linhas) {
            for (Registro reg : linha.registros) {
                if (reg.atributo.nomeAtributo.trim().equalsIgnoreCase("removido")) {
                    if ((Boolean)reg.registro == false) {
                        novaGrid.linhas.add(linha);
                    }
                }
            }
        }
        
        this.grid = novaGrid;
    }

    public List<String> getSelecionados() {
        return selecionados;
    }

    public void setSelecionados(List<String> selecionados) {
        this.selecionados = selecionados;
    }

    public RetornoGrid() {
        this.grid = new Grid();
        this.selecionados = new ArrayList<>();
    }
    
    
    
}
