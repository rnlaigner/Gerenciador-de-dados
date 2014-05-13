/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import trabalhoed2.sgbd.entidades.Catalogo;
import trabalhoed2.sgbd.facade.EstruturaFacade;
import trabalhoed2.sgbd.util.ConteudoArquivos;
import trabalhoed2.sgbd.util.Erro;
import trabalhoed2.sgbd.util.Funcao;


public class TrabalhoED2SGBD {

    /**
     * @param args the command line arguments
     * 
     * Implementar um gerenciador de dados que seja capaz de fazer as seguintes tarefas:

    Permitir que o usuário especifique a estrutura das tabelas que deseja armazenar (nome da tabela, conjunto de atributos e tipo de cada atributo). 
    * Em gerenciadores de dados, essas informações normalmente ficam armazenadas numa estrutura chamada catálogo.
    Permitir que o usuário insira registros nas tabelas 
    * (usuário escolhe a tabela e o sistema pergunta valores para os atributos da tabela
    Permitir que o usuário faça consultas sobre os registros de uma determinada tabela. 
    * Usuário escolhe o nome da tabela e um ou mais atributos 
    * (não necessariamente os atributos chave da tabela). O usuário passa as condições de seleção de cada um dos atributos (exemplo: codigo > 30 e data = “05/10/2010”).
    Permitir que o usuário exclua registros de uma tabela. A seleção dos registros a serem excluídos será feita da mesma forma explicada no item 3.
    Permitir que o usuário modifique o valor de um determinado atributo de uma determinada tabela. O usuário escolhe a tabela, o atributo e os critérios de seleção da tupla a ser modificada, fornecendo o valor da chave.

O trabalho deve ser implementado com tabelas hash. O catálogo pode utilizar outra forma de armazenamento, à escolha do grupo. 
     */

    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws Exception {
    
        System.out.println(" ESTRUTURA DE DADOS II - SGBD\n");
        while (showMenu());
        
    }
    
    public static boolean showMenu(){
        
        Scanner scan = new Scanner(System.in);
        String opcao = null;
        
        System.out.println("+----------------------------+");
        System.out.println("| Selecione a opção desejada |");
        System.out.println("+----------------------------+");
        System.out.println("| (1) - Criar tabela         |");
        System.out.println("| (2) - Consultas            |");
        System.out.println("| (3) - Manipular dados      |");
        System.out.println("| (4) - Sair                 |");
        System.out.println("+----------------------------+");
        
        try
        {
                   
         
            opcao = scan.next();
            int opcaoInt = Integer.parseInt(opcao);
            if (opcaoInt < 1 || opcaoInt > 4) {
                throw new NumberFormatException();
            }
            else if (opcaoInt == 4){
                return false;
            }
            
            abrirOpcao(opcaoInt);
            
            return true;
        }
        catch(NumberFormatException ex)
        {
            System.out.println("'" + opcao + "' é uma opção inválida.");
            return showMenu();
        }
        finally
        {
            Funcao.clearConsole();
        }
    }

    public static void abrirOpcao(int opcao){
        
        switch (opcao)
        {
            case 1://Criar tabela
                EstruturaFacade.criarTabela();
                break;
            case 2://Consultas
                executarQuery();
                break;
            case 3://Manipular dados
                //abrir opções
                break;
            default:
                break;
        }
    }

    public static void manipularDados(){
        
    }
    
    
    private static void executarQuery() {
        
        Scanner scan = new Scanner(System.in);
        String projecao = null;
        String tabela = null;
        String where = null;
        
        System.out.println("Digite a query: ");
        System.out.print("> ");
        String comando = scan.nextLine();
        
        Matcher mProj = Pattern.compile("select(.*?)from").matcher(comando);
        Matcher mFromW = Pattern.compile("from(.*?)where").matcher(comando);
        Matcher mFrom = Pattern.compile("from[^\\n]*").matcher(comando);
        Matcher mWhere = Pattern.compile("where[^\\n]*").matcher(comando);
        
        if (mProj.find()) {
            projecao = mProj.group(1);
        }
        
        if (mFromW.find()) {
            tabela = mFromW.group(1);
        }else if (mFrom.find()) {
            tabela = mFrom.group(0).replace("from", "");
        }
        
        if (mWhere.find()) {
            where = mWhere.group(0).replace("where", "");
        }

        System.out.println("projecao = " + projecao);//pronta
        System.out.println("tabela = " + tabela);//pronta
        System.out.println("where = " + where);//todo
        
        
        
        /*------------FILTRO------------*/
        
        String[] partesWhere = null;
        String parte1Where = null;
        String parte2Where = null;
        String operador = null;
        
        if (where.contains(" or ")) {
            partesWhere = where.toLowerCase().split(" or ");
            parte1Where = partesWhere[0];
            parte2Where = partesWhere[1];            
            operador = "or";
        }else if (where.contains(" and ")) {
            partesWhere = where.toLowerCase().split(" and ");
            parte1Where = partesWhere[0];
            parte2Where = partesWhere[1];    
            operador = "and";
        }
        else if (!where.trim().equals("") || where != null) {
            parte1Where = partesWhere[0];
            operador = null;
        }        
        
        String[] filtros = new String[]{parte1Where, parte2Where, operador};
        
        
        /*------------END FILTRO------------*/
        
        
        
        List<String> atributos = new ArrayList<>();
        String[] parts = projecao.split(",");
        for (String part : parts) {
            atributos.add(part.trim().toLowerCase());
        }
     
        ConteudoArquivos ca = new ConteudoArquivos();
        
        Catalogo objTabela = null;
        try {
            objTabela = ca.getTabelaByName(tabela);
            ca.lerArquivo2(objTabela, atributos, filtros);
        } catch (IOException ex) {
            Erro.log(ex);
        }
        
    }
   


    
        
}
    
    

    
  
    
    

