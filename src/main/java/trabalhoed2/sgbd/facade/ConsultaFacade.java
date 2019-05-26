/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.facade;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import trabalhoed2.sgbd.entidades.Atributo;
import trabalhoed2.sgbd.entidades.Catalogo;
import trabalhoed2.sgbd.util.Arquivo;
import trabalhoed2.sgbd.util.Erro;

/**
 *
 *
 */
public class ConsultaFacade {
        
    
    public static List<Atributo> getAtributosById(int id) throws IOException{
        // Este método está esceneando a tabela atributo inteira.
        
        List<Atributo> listaAtributos = new ArrayList<>();
        

        RandomAccessFile rafAtributos = null;
        Atributo atributo = null;
        
        int qtseek = 0;

        try {
            rafAtributos = Arquivo.abrirArquivo("atributo.dat", "r");
            boolean primeiraVez = true;
            
            while (true) {             
                if (primeiraVez) {
                    primeiraVez = false;
                }
                else
                {
                    rafAtributos.seek(Atributo.tamanhoRegistro + qtseek);
                    qtseek = Atributo.tamanhoRegistro + qtseek;
                }
                atributo = Atributo.le(rafAtributos);
                if (atributo.idTabela == id) {
                    listaAtributos.add(atributo);
                }
                
            }
        } 
        catch (Exception ex) 
        {
            Erro.log(ex);
        }
        finally
        {
            if (rafAtributos != null) 
            {
                rafAtributos.close();
            }  
            
            return listaAtributos;
        }        
        
        
    }
    
    public static List<Catalogo> listaTabelas() throws IOException{
        List<Catalogo> listaTabelas = new ArrayList<>();
        RandomAccessFile rafTabelas = null;
        Catalogo tabela;
        Catalogo ultimaTabela = null;
        int qtseek = 0;

        try {
            rafTabelas = Arquivo.abrirArquivo("catalogo.dat", "r");
            boolean primeiraVez = true;
            
            while (true) {             
                if (primeiraVez) {
                    primeiraVez = false;
                }
                else
                {
                    rafTabelas.seek(Catalogo.tamanhoRegistro + qtseek);
                    qtseek = Catalogo.tamanhoRegistro + qtseek;
                }
                tabela = Catalogo.le(rafTabelas);
                if (!tabela.removido) {
                    listaTabelas.add(tabela);
                }
            }
        } 
        catch (Exception ex) 
        {
            Erro.log(ex);
        }
        finally
        {
            if (rafTabelas != null) 
            {
                rafTabelas.close();
            }  
            
        }
        return listaTabelas;
    }
        
}
