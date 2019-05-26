/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

/**
 *
 *
 */
public class Arquivo {
    
    

    public static RandomAccessFile abrirArquivo(String nomeArquivo, String modo){
        
        RandomAccessFile raf = null;
        
        try {
        
            new File(System.getProperty("user.dir")+"\\"+Configuracao.nomePastaTabelas+"\\").mkdir();
              
            raf = new RandomAccessFile(new File(System.getProperty("user.dir") + "\\tabelas\\" , nomeArquivo), modo);
            
        }
        catch(FileNotFoundException fne){
        }
        catch (Exception e) {
            System.out.println("Erro ao abrir arquivo = " + e);
        }
        finally{
            return raf;
        }
               
    }
    
    
    
    public static RandomAccessFile abrirArquivoFessora(String nomeArquivo, String modo){
        
        RandomAccessFile raf = null;
        
        try {
        
            new File(System.getProperty("user.dir")+"\\"+Configuracao.nomePastaTabelas+"\\dadosiniciais\\").mkdir();
              
            raf = new RandomAccessFile(new File(System.getProperty("user.dir") + "\\tabelas\\dadosiniciais\\" , nomeArquivo), modo);
            
        }
        catch(FileNotFoundException fne){
        }
        catch (Exception e) {
            System.out.println("Erro ao abrir arquivo = " + e);
        }
        finally{
            return raf;
        }
               
    }
}
