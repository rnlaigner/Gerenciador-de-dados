/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.facade;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import trabalhoed2.sgbd.TrabalhoED2SGBD;
import trabalhoed2.sgbd.entidades.Atributo;
import trabalhoed2.sgbd.entidades.Catalogo;
import trabalhoed2.sgbd.util.Arquivo;
import trabalhoed2.sgbd.util.Configuracao;
import trabalhoed2.sgbd.util.Funcao;
import trabalhoed2.sgbd.util.Validacao;

/**
 *
 * @author pedro.gerico
 */
public class EstruturaFacade {
    
    
    public static void criarTabela(){
        
        System.out.println("+----------------------------+");
        System.out.println("|        Criar tabela        |");
        System.out.println("+----------------------------+");
        
        Scanner scan = new Scanner(System.in);
        String nomeTabela;
        
        do{
            System.out.print("Digite o nome da tabela: ");
            nomeTabela = scan.next();
        }while(!Validacao.validaString(nomeTabela));
        
        while (nomeTabela.equalsIgnoreCase("catalogo") || nomeTabela.equalsIgnoreCase("atributo")) {
            System.out.print("Digite o nome da tabela: ");
            nomeTabela = scan.next();
        }
        
        try {
            
            Catalogo catalogo = new Catalogo();
            catalogo.idTabela = getSequenciaTabela();
            catalogo.nomeTabela = nomeTabela;
            catalogo.qtRegistro = 0;

            List<Atributo> listaAtributos = new ArrayList<>();

            Atributo atributo = new Atributo(catalogo.idTabela, Funcao.padRight("Codigo"), 1, Atributo.factoryTamanhoAtributo(1));
            listaAtributos.add(atributo);         
            atributo = new Atributo(catalogo.idTabela, Funcao.padRight("removido"), 6, Atributo.factoryTamanhoAtributo(6));
            listaAtributos.add(atributo);        
            atributo = new Atributo(catalogo.idTabela, Funcao.padRight("prox"), 7, Atributo.factoryTamanhoAtributo(7));
            listaAtributos.add(atributo);        

            System.out.println("+----------------------------+");
            System.out.println("| Atributos de " + Funcao.padRight(nomeTabela, 14) + "|");
            System.out.println("+----------------------------+");

            while (true)
            {
                


                String nomeAtributo;
                do{
                    System.out.print("Nome do novo atributo: ");
                    nomeAtributo = scan.next();
                }while(!Validacao.validaString(nomeTabela));


                int tipoAtributo = 0;

                while(tipoAtributo >  6 || tipoAtributo < 1 )
                {
                    try
                    {  
                        

                        
                        System.out.println(" 1 - Integer");
                        System.out.println(" 2 - Float");
                        System.out.println(" 3 - String");
                        System.out.println(" 4 - Char");
                        System.out.println(" 5 - Double");
                        System.out.println(" 6 - Boolean");
                   
                        System.out.print("Selecione o tipo: ");
                        tipoAtributo = scan.nextInt();

                    }
                    catch(Exception ex){
                        System.out.println("Digite uma opção numérica disponível. ");   
                        tipoAtributo = 0;
                        scan.nextLine();//Limpar o scan
                    }
                }

                atributo = new Atributo(catalogo.idTabela, Funcao.padRight(nomeAtributo), tipoAtributo, Atributo.factoryTamanhoAtributo(tipoAtributo));
                listaAtributos.add(atributo);
                System.out.println("Atributo adicionado!");

                String novoAtributo;
                do{
                    System.out.print("Novo atributo? (S/N): ");
                    novoAtributo = scan.next();         
                }
                while(!novoAtributo.equalsIgnoreCase("N") && !novoAtributo.equalsIgnoreCase("S"));

                if (novoAtributo.equalsIgnoreCase("N")) {
                    break;
                }

            }

    /* Gravando arquivos */

            RandomAccessFile rafCatalogo = null;
            RandomAccessFile rafAtributo = null;
            RandomAccessFile rafTabela = null;
            RandomAccessFile rafTabelaHash = null;
            String nomeTabelaHash = nomeTabela + "_hash.dat";
            try
            {
                rafCatalogo = Arquivo.abrirArquivo(Catalogo.nomeArquivo, "rw");
                rafAtributo = Arquivo.abrirArquivo(Atributo.nomeArquivo, "rw");
                rafTabela = Arquivo.abrirArquivo(nomeTabela+".dat", "rw");
                rafTabelaHash = Arquivo.abrirArquivo(nomeTabelaHash, "rw");


                rafCatalogo.seek(rafCatalogo.length());
                catalogo.salva(rafCatalogo);

                for (Atributo attr : listaAtributos) {
                    rafAtributo.seek(rafAtributo.length());
                    attr.salva(rafAtributo);
                }

            }
            catch(Exception ex)
            {
                System.out.println("ex = " + ex.getMessage() + " - " + ex.getStackTrace());
            }
            finally
            {
                if (rafAtributo != null) {
                    rafAtributo.close();
                }
                if (rafCatalogo != null) {
                    rafCatalogo.close();
                }
                if (rafTabela != null) {
                    rafTabela.close();
                }
                if (rafTabelaHash != null) {
                    rafTabelaHash.close();
                }            
            }

            Funcao.inicializarHash(nomeTabelaHash);        
        
        } 
        catch (Exception e) 
        {
            System.out.println("e = " + e);
        }

    }
    
    public static int getSequenciaTabela() throws IOException{
        RandomAccessFile rafTabelas = null;
        Catalogo tabela = null;
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
                ultimaTabela = tabela;
            }
        } 
        catch (Exception ex) 
        {
            //System.out.println("ex = " + ex.toString());
        }
        finally
        {
            if (rafTabelas != null) 
            {
                rafTabelas.close();
            }  
            if (ultimaTabela != null) {
                return ultimaTabela.idTabela + 1;
            }
            else
            {
                return 1;
            }
        }
    }
    
    
    
    public static int selecionarTabela() throws IOException{
        Scanner scan = new Scanner(System.in);
        int idTabela = 0;
        while(idTabela <  1 || idTabela > (getSequenciaTabela()-1) )
        {
            try
            {  
                ConsultaFacade.listaTabelas();
                idTabela = scan.nextInt();
            }
            catch(Exception ex){
                System.out.println("Digite um número entre 1 e "+getSequenciaTabela() + 1);   
                idTabela = 0;
                scan.nextLine();//Limpar o scan
            }
        }
        return idTabela;
    }
    
    
    public static long getQuantidadeRegistrosById(int codigoTabela) throws IOException{
                
        RandomAccessFile rafTabelas = null;
        Catalogo tabela;
        long quantidade = 0;
        
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
                if (tabela.idTabela == codigoTabela) {
                    quantidade = tabela.qtRegistro;
                }
                
            }
        } 
        catch (Exception ex) 
        {
            //System.out.println("ex = " + ex.toString());
        }
        finally
        {
            if (rafTabelas != null) 
            {
                rafTabelas.close();
            }  
            return quantidade;
        }
        
    }
    
    public static long addRemoveQtRegistro(int codigoTabela, char addRemove) throws IOException{
                
        RandomAccessFile rafTabelas = null;
        Catalogo tabela;
        long quantidade = 0;
        
        int qtseek = 0;

        try {
            rafTabelas = Arquivo.abrirArquivo("catalogo.dat", "rw");
            boolean primeiraVez = true;
            boolean primeiraVezComparacao = true;
            
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
                if (tabela.idTabela == codigoTabela) {
                    quantidade = tabela.qtRegistro;
                    if (!primeiraVezComparacao) {//segunda para frente
                        rafTabelas.seek((Catalogo.tamanhoRegistro + qtseek) - Configuracao.tamanhoLong);        
                    }
                    else
                    {
                        rafTabelas.seek(Catalogo.tamanhoRegistro - Configuracao.tamanhoLong);
                    }
                    
                    if (addRemove == 'r') {
                        rafTabelas.writeLong(quantidade - 1);
                        break;
                    }
                    else if (addRemove == 'a') {
                        rafTabelas.writeLong(quantidade + 1);
                        break;
                    }
                    
                }
                primeiraVezComparacao = false;
                
            }
        } 
        catch (Exception ex) 
        {
            System.out.println("ex = " + ex.toString());
        }
        finally
        {
            if (rafTabelas != null) 
            {
                rafTabelas.close();
            }  
            return quantidade;
        }
        
    }
    
    
}
