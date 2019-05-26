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
import trabalhoed2.sgbd.entidades.Atributo;
import trabalhoed2.sgbd.entidades.Catalogo;
import trabalhoed2.sgbd.entidades.Grid;
import trabalhoed2.sgbd.entidades.Linha;
import trabalhoed2.sgbd.entidades.Operadores;
import trabalhoed2.sgbd.entidades.Or;
import trabalhoed2.sgbd.entidades.Registro;
import trabalhoed2.sgbd.entidades.RetornoGrid;
import trabalhoed2.sgbd.facade.EstruturaFacade;
import trabalhoed2.sgbd.facade.ManipulacaoFacade;
import trabalhoed2.sgbd.util.Comparador;
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
        
        System.out.println("+-------------------------------+");
        System.out.println("| Selecione a opção desejada    |");
        System.out.println("+-------------------------------+");
        System.out.println("| (1) - Criar tabela            |");
        System.out.println("| (2) - Consultas               |");
        System.out.println("| (3) - Manipular dados         |");
        System.out.println("| (4) - Propriedades do Sistema |");
        System.out.println("| (5) - Sair                    |");
        System.out.println("+-------------------------------+");
        
        try
        {
                   
         
            opcao = scan.next();
            int opcaoInt = Integer.parseInt(opcao);
            if (opcaoInt < 1 || opcaoInt > 5) {
                throw new NumberFormatException();
            }
            else if (opcaoInt == 5){
                System.exit(0);
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
                try{
                    Scanner scan = new Scanner(System.in);
                    System.out.println("Digite a query: ");
                    System.out.print("> ");
                    String comando = scan.nextLine();
                    
                    if (comando.trim().toLowerCase().equals("quit")) {
                        showMenu();
                        return;
                    }
                    
                    RetornoGrid retGrid = executarQuery(comando);
                    Funcao.imprimirGrid(retGrid.getGrid(), retGrid.getSelecionados());
                    int ct = retGrid.getGrid().linhas.size();
                    System.out.println("Query Ok. "+ct+" tupla(s) recuperada(s).");
                   
                }
                catch(Exception ex)
                {
                    System.out.println("Consulta Inválida.");
                    abrirOpcao(2);
                }
                
                break;
            case 3://Manipular dados
                //abrir opções (inserir(pronto), remover(todo), alterar(todo))
                manipularDados();
                break;
            case 4://propriedades do sistema
                propriedadesDoSistema();
            default:
                break;
        }
    }

    public static void manipularDados(){

        Scanner scan = new Scanner(System.in);
        String opcao = null;
        
        System.out.println("+----------------------+");
        System.out.println("|      Manipulação     |");
        System.out.println("+----------------------+");
        System.out.println("| (1) - Inserir        |");
        System.out.println("| (2) - Remover        |");
        System.out.println("| (3) - Alterar        |");
        System.out.println("| (4) - Cancelar       |");
        System.out.println("+----------------------+");
              
        try
        {
         
            opcao = scan.next();
            int opcaoInt = Integer.parseInt(opcao);
            if (opcaoInt < 1 || opcaoInt > 4) {
                throw new NumberFormatException();
            }
            else if (opcaoInt == 4){
                return;
            }
            
            abrirOpcaoManipulacao(opcaoInt);
        }
        catch(NumberFormatException ex)
        {
            System.out.println("'" + opcao + "' é uma opção inválida.");
        }
        finally
        {
            Funcao.clearConsole();
        }  
        
    }
    
    public static RetornoGrid executarQuery(String comando) throws Exception {
        
        comando = comando.toLowerCase();
        
        RetornoGrid retGrid = new RetornoGrid();
        String projecao = null;
        String tabela = null;
        String where = null;
        
        
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

      
        
        /*------------FILTRO------------*/
        
        String[] partesWhere = null;
        String parte1Where = null;
        String parte2Where = null;
        String operador = null;
        
        String[] partesOr = null;
        String[] partesAnd = null;
        
        ConteudoArquivos ca = new ConteudoArquivos();
        Grid gridResultado = null;
        Grid grid = null;
        List<Grid> listaGrid = new ArrayList<>();
        List<Grid> gridsAComparar = new ArrayList<>();
        
        
        List<String> selecionados = new ArrayList<>();
        if (projecao.trim().equals("*")) {
            selecionados.add("*");
        }
        else
        {
            String[] parts = projecao.split(",");
            for (String part : parts) {
                selecionados.add(part.trim().toLowerCase());
            }
        }
        
        if (where == null) {
            try
            {
                Catalogo objTabela = ca.getTabelaByName(tabela.trim());
                grid = ca.lerArquivo(objTabela);      
            }catch(Exception ex){
                throw ex;
            }
            
            
            retGrid.setGrid(grid);
            retGrid.setSelecionados(selecionados);
            
            return retGrid;
        }
        
        //------------SPLIT AND E OR-----------//

        
        partesOr = where.toLowerCase().split(" or ");
        List<String> listaAnd = new ArrayList<String>();
        List<String> listaOr = new ArrayList<String>();
        
        
        Operadores ops = new Operadores();
        
        for (int i = 0; i < partesOr.length; i++) {
            List<String> ands = new ArrayList<>();
            
            if (partesOr[i].contains(" and ")) {
                partesAnd = null;
                partesAnd = partesOr[i].split(" and ");
                ands = new ArrayList<String>();
                for (int j = 0; j < partesAnd.length; j++) {
                    ands.add(partesAnd[j]);
                }
                
            }
            
            Or or = new Or();
            or.ands = ands;    
            if (ands.size() == 0) {
                or.or = partesOr[i];
            }
            ops.ors.add(or);
        }
        
  
        try
        {
            
//            Catalogo objTabela = ca.getTabelaByName(tabela.trim());
//            String codigoSelecionado = "-1";
//            for (Or or : ops.ors) {
//                if (or.or !=null) {
//                    if (or.or.replace(" ", "").trim().contains("codigo=")) {
//                        codigoSelecionado = or.or.replace(" ", "").trim().replace("codigo=", "");
//                    }
//                }
//                if (or.ands !=null ){
//                    if (or.ands.size() > 0) {
//                        for (String and : or.ands) {
//                            if (and.replace(" ", "").trim().contains("codigo=")) {
//                                codigoSelecionado = and.replace(" ", "").trim().replace("codigo=", "");
//                            }
//
//                        }
//                    }
//                }
//            }
//            int intCodigoSelecionado = Integer.parseInt(codigoSelecionado);
//            
//            if (intCodigoSelecionado <= 0) {
//                grid = ca.lerArquivo(objTabela);  
//            }
//            else
//            {
//                grid = ca.lerArquivo(objTabela, intCodigoSelecionado);
//            }
//            
//            for (Or or : ops.ors) {
//                for (String and : or.ands) {
//                    gridResultado = new TrabalhoED2SGBD().filtrarGrid(and, grid);
//                    gridsAComparar.add(gridResultado);
//                    
//                    if (gridsAComparar.size() == 2) {
//                        gridResultado = intersecaoGrids(gridsAComparar, "and");
//                        gridsAComparar.clear();
//                        gridsAComparar.add(gridResultado);
//                    }
//                    
//                }
//                if (gridsAComparar.size() > 0 ){
//                    or.grid = gridsAComparar.get(0);
//                }
//                
//            }
            Catalogo objTabela = ca.getTabelaByName(tabela.trim());
            String codigoSelecionado = "-1";
            grid = ca.lerArquivo(objTabela);  
            int intCodigoSelecionado = 0;
            
            for (Or or : ops.ors) {
                for (String and : or.ands) {
                    if (and.replace(" ", "").trim().contains("codigo=")) 
                    {
                                codigoSelecionado = and.replace(" ", "").trim().replace("codigo=", "");
                                intCodigoSelecionado = Integer.parseInt(codigoSelecionado);
                                gridResultado = ca.lerArquivo(objTabela,intCodigoSelecionado);
                    }
                    else
                    {
                    gridResultado = new TrabalhoED2SGBD().filtrarGrid(and, grid);
                    }                    
                    gridsAComparar.add(gridResultado);
                    
                    if (gridsAComparar.size() == 2) {
                        gridResultado = intersecaoGrids(gridsAComparar, "and");
                        gridsAComparar.clear();
                        gridsAComparar.add(gridResultado);
                    }
                    
                }
                if (gridsAComparar.size() > 0 ){
                    or.grid = gridsAComparar.get(0);
                }
                
            }
            
            
            gridsAComparar.clear();
            
            for (Or or : ops.ors) {
                
                if (or.grid != null) {
                    gridsAComparar.add(or.grid);
                }
                
                if (or.or != null) {
                    
                   gridResultado = new TrabalhoED2SGBD().filtrarGrid(or.or, grid);
                   gridsAComparar.add(gridResultado);
                
                }
                    
                if (gridsAComparar.size() == 2) {
                    gridResultado = intersecaoGrids(gridsAComparar, "or");
                    gridsAComparar.clear();
                    gridsAComparar.add(gridResultado);
                    
                }
                
            }
            
                        
            retGrid.setGrid(gridResultado);
            retGrid.setSelecionados(selecionados);
            
            return retGrid;
        }
        catch(Exception ex){
            throw ex;
        }
        
        
    }

    private static Grid intersecaoGrids(List<Grid> gridsAComparar, String op) {
        
        Grid grid1 = gridsAComparar.get(0);
        Grid grid2 = gridsAComparar.get(1);
        Grid gridResultado = new Grid(grid1.listaAtributos);
        
        gridResultado = new Grid(grid1.listaAtributos);
         
        if (op.equals("or")) 
        {
        
            for (Linha linhaGrid1 : grid1.linhas) {
                gridResultado.add(linhaGrid1);
            }
            for (Linha linhaGrid2 : grid2.linhas) {
                if (!gridResultado.linhas.contains(linhaGrid2)) {
                    gridResultado.linhas.add(linhaGrid2);
                }
            }
            
            return gridResultado;
        }
  
        
        for (Linha linhaGrid1 : grid1.linhas) {
            for (Linha linhaGrid2 : grid2.linhas){
                    if ((Integer) linhaGrid1.registros.get(0).registro == (Integer) linhaGrid2.registros.get((0)).registro) {
                    //if (linhaGrid1.equals(linhaGrid2)) {
                    gridResultado.add(linhaGrid1);
                }
            }
        }

        return gridResultado;
    }

    private static void abrirOpcaoManipulacao(int opcao) {
        switch (opcao)
        {
            case 1:
                //new ManipulacaoFacade().inserirDados();
                new ManipulacaoFacade().inserirDados2();
                break;
            case 2://alterar dados
                try
                {
                    new ManipulacaoFacade().removerDados();
                }catch(Exception ex)
                {
                    System.out.println("Query Inválida.");
                    abrirOpcaoManipulacao(2);
                }
                break;
            case 3://remover dados
                try
                {
                    new ManipulacaoFacade().alterarDados();    
                }catch(Exception ex){
                    System.out.println("Query Inválida.");
                    abrirOpcaoManipulacao(3);
                }
                
                break;
            default:
                break;
        }
    }

    private static void propriedadesDoSistema() {
        
        Scanner scan = new Scanner(System.in);
        String opcao = null;
        
        System.out.println("+-----------------------------------+");
        System.out.println("| Propriedades do Sistema           |");
        System.out.println("+-----------------------------------+");
        System.out.println("| (1) - Exibir Tabelas              |");
        System.out.println("| (2) - Exibir Tabela Hash          |");
        System.out.println("| (3) - Remover Tabela              |");
        System.out.println("| (4) - Exibir Atributos da Tabela  |");
        System.out.println("| (5) - Cancelar                    |");
        System.out.println("+-----------------------------------+");
              
        try
        {
            opcao = scan.next();
            int opcaoInt = Integer.parseInt(opcao);
            if (opcaoInt < 1 || opcaoInt > 5) {
                throw new NumberFormatException();
            }
            else if (opcaoInt == 5){
                return;
            }
            
            abrirOpcaoPropriedades(opcaoInt);
        }
        catch(NumberFormatException ex)
        {
            System.out.println("'" + opcao + "' é uma opção inválida.");
        }
        finally
        {
            Funcao.clearConsole();
        }
    }

    private static void abrirOpcaoPropriedades(int opcaoInt) {
        switch (opcaoInt)
        {
            case 1://exibir todas as tabelas
                new EstruturaFacade().showTables();
                break;
            case 2://exibir tabela hash
                new EstruturaFacade().showTabelaHash();
                break;
            case 3://remover tabela
                new EstruturaFacade().dropTable();
                break;
            case 4://remover tabela
                new EstruturaFacade().showAtributos();
                break;                
            default:
                break;
        }
    }
    
    public Grid filtrarGrid(String filtro, Grid grid) throws Exception{
        
        String condicaoDoFiltro = null;
        List<String> condicoes = new ArrayList<String>();
        Grid gridResultado = new Grid(grid.listaAtributos);
        
        
        String[] ladosString = null;
        String nomeAtributo;
        String valor;
       
        condicoes.add("=");
        condicoes.add("<");
        condicoes.add(">");
        condicoes.add("<=");
        condicoes.add(">=");
        condicoes.add("<>");
        
        for (String str : condicoes) {
            if (filtro.contains(str)) {
                condicaoDoFiltro = str;
            }
        }
        
        ladosString = filtro.split(condicaoDoFiltro);
        nomeAtributo = ladosString[0].trim();
        valor = ladosString[1].trim();
        
        
        boolean encontrou = false;
        //validação 
        for (Atributo attr : grid.listaAtributos) {
            if (attr.nomeAtributo.toLowerCase().trim().equals(nomeAtributo.trim())) {
                encontrou = true;
            }
        }
        
        if (!encontrou) {
            throw new Exception("Não existe este atributo");
        }
        
        Boolean pertence = false;
        for (Linha linha : grid.linhas) {
            
            for (Registro reg : linha.registros) {
                if (reg.atributo.nomeAtributo.trim().equalsIgnoreCase(nomeAtributo.trim())) {
                    pertence = verificarCondicao(reg, condicaoDoFiltro, valor);
                }
                if (pertence) {
                    gridResultado.add(linha);
                    pertence = false;
                }
            }
            
        }
        
        return gridResultado;
    }
        
    private Boolean verificarCondicao(Registro reg, String condicaoDoFiltro, String valor) {
        
        Object valor2 = null;
        valor = valor.trim();
        condicaoDoFiltro = condicaoDoFiltro.trim();
        if (reg.registro instanceof Integer) {
            valor2 = Integer.parseInt(valor);
            return Comparador.compareInt((int)reg.registro, (int)valor2, condicaoDoFiltro);
        }else if (reg.registro instanceof Float) {
            valor2 = Float.parseFloat(valor);
            return Comparador.compareFloat((Float)reg.registro, (Float)valor2, condicaoDoFiltro);
        }else if (reg.registro instanceof Double) {
            valor2 = Double.parseDouble(valor);
            return Comparador.compareDouble((Double)reg.registro, (Double)valor2, condicaoDoFiltro);
        }else if (reg.registro instanceof Boolean) {
            if (valor.trim().equals("false")) {
                valor2 = false;
            }
            else if (valor.trim().equals("true")){
                valor2 = true;
            }
            return Comparador.compareBoolean((boolean)reg.registro, (boolean)valor2, condicaoDoFiltro);
        }else if (reg.registro instanceof Character) {
            valor2 = valor.charAt(0);
            return Comparador.compareChar((Character)reg.registro, (Character)valor2, condicaoDoFiltro);
        }else if (reg.registro instanceof String) {
            valor2 = valor;
            return Comparador.compareString((String)reg.registro, (String)valor2, condicaoDoFiltro);
        }
        
        return false;
    }
    
   


    
        
}
    
    

    
  
    
    

