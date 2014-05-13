/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import trabalhoed2.sgbd.entidades.CompartimentoHash;

/**
 *
 * @author pedro
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


    
    
    
}
