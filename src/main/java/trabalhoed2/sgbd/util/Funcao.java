/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import trabalhoed2.sgbd.entidades.Atributo;
import trabalhoed2.sgbd.entidades.CompartimentoHash;
import trabalhoed2.sgbd.entidades.Grid;
import trabalhoed2.sgbd.entidades.Linha;
import trabalhoed2.sgbd.entidades.Registro;

/**
 *
 *
 */
public class Funcao {
        
    public static String padRight(String s) {
        int n=0;
        n = Configuracao.tamanhoString - 2;
        return String.format("%1$-" + n + "s", s);  
    }
    
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);  
    }
        
    
    public static int hash(int entrada){
        return entrada % Configuracao.tamanhoHash;
    }
    
    public static void inicializarHash(String nomeArquivoHash) throws IOException{
        
        RandomAccessFile raf = null;
        
        try
        {
            raf = Arquivo.abrirArquivo(nomeArquivoHash, "rw");

            CompartimentoHash comp = new CompartimentoHash(-1);

            for (int i = 0; i < Configuracao.tamanhoHash; i++) {
                comp.salva(raf);
            }
        }
        catch(Exception ioEx)
        {
            System.out.println(ioEx.toString());
        }
        finally{
            if (raf!=null) 
                raf.close();
        }
        
        
    }
    
    public static void imprimirGrid(Grid grid, List<String> selecionados) throws Exception{
        
        int ct = 0;
        String nomeAttr;
        
        //PROJEÇÃO DE CAMPOS-------------------------------------------------
        List<String> selecionados2 = new ArrayList<String>();
        
        //consulta com filtro
        for (Atributo attr : grid.listaAtributos) {
            if (selecionados.contains(attr.nomeAtributo.trim().toLowerCase())) {
                selecionados2.add(attr.nomeAtributo);
            }
        }
        
        //consulta *
        if (selecionados.get(0).toString().trim().equals("*")) {
            for (Atributo atributo : grid.listaAtributos) {
                
                if (atributo.nomeAtributo.trim().equalsIgnoreCase("removido")) {
                    continue;
                }
                
                selecionados2.add(atributo.nomeAtributo.trim());
            }        
        }
        
        
        
        selecionados = selecionados2;
  
        
        for (String string : selecionados) {
            for (Atributo atributo : grid.listaAtributos) {
                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {
                    ct++;
                }
            }
        }
        
        //END PROJEÇÃO DE CAMPOS---------------------------------------------
        
        StringBuilder sb = new StringBuilder();

        
        System.out.print("+");
        for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
            System.out.print("-");
        }
        System.out.println("+");

        //new
        for (String string : selecionados) {
            for (Atributo atributo : grid.listaAtributos) {
                nomeAttr = atributo.nomeAtributo.trim().toUpperCase();
                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {
                    System.out.print("| " + Funcao.padRight(nomeAttr, Configuracao.tamanhoString));
                }

            }
        }
      
        
        
        System.out.println("|");
        
        System.out.print("+");
        for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
            System.out.print("-");
        }
        System.out.println("+");
        
         
        for (Linha linha : grid.linhas) {
            for (Registro reg : linha.registros) {
                
                for (String string : selecionados) {
                    if (reg.atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {
                        sb.append("| " + Funcao.padRight(reg.registro.toString(), Configuracao.tamanhoString));
                    }
                }
                
            }
            System.out.println(sb.toString() + "|"); 
            sb = new StringBuilder();
        }
        
        System.out.print("+");
        for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        
    }
    
    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }
    

    public static String factoryTipoAtributo(int idTipo){
        
        switch (idTipo)
        {
            case 1:
                return "Inteiro";
            case 2:
                return "Float";
            case 3:
                return "String";
            case 4:
                return "Char";
            case 5:
                return "Double";
            case 6:
                return "Booleano";
            case 7:
                return "Long";
        }
        return null;  
    }
    
    
    
}
