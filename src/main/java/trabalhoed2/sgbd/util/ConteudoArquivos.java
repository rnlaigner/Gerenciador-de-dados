/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import trabalhoed2.sgbd.entidades.Atributo;
import trabalhoed2.sgbd.entidades.Catalogo;
import trabalhoed2.sgbd.entidades.CompartimentoHash;
import trabalhoed2.sgbd.entidades.Grid;
import trabalhoed2.sgbd.entidades.Linha;
import trabalhoed2.sgbd.entidades.Registro;
import trabalhoed2.sgbd.facade.ManipulacaoFacade;

/**
 *
 *
 */
public class ConteudoArquivos {

    public static void main(String[] args) throws IOException, Exception {

        //new ConteudoArquivos().teste();
        //new ConteudoArquivos().teste2();
        //Funcao.listaTabelas();
        //List<String> selecionados = new ArrayList<>();
//        selecionados.add("*");

        //new ConteudoArquivos().lerArquivo2(new Catalogo(1, "teste", 0), selecionados, null);
        //new ConteudoArquivos().lerArquivo(new Catalogo(1,"teste",0));
        //new ConteudoArquivos().lerArquivoHash("teste_hash");
  //      new ConteudoArquivos().lerCatalogo();
  //      new ConteudoArquivos().lerAtributosDaTabela("clientes");
        new ConteudoArquivos().lerABagaca();
        
        
        
    }

    public void teste() throws IOException {

        System.out.println("CATALOGO");
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
                } else {
                    rafTabelas.seek(Catalogo.tamanhoRegistro + qtseek);
                    qtseek = Catalogo.tamanhoRegistro + qtseek;
                }
                tabela = Catalogo.le(rafTabelas);
                System.out.println(tabela.toString());

            }
        } catch (Exception ex) {
            System.out.println("ex = " + ex.toString());
        } finally {
            if (rafTabelas != null) {
                rafTabelas.close();
            }

        }
    }

    public void teste2() throws IOException {

        System.out.println("ATRIBUTOS");
        RandomAccessFile rafTabelas = null;
        Atributo atributo = null;

        int qtseek = 0;

        try {
            rafTabelas = Arquivo.abrirArquivo("atributo.dat", "r");
            boolean primeiraVez = true;

            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {
                    rafTabelas.seek(Atributo.tamanhoRegistro + qtseek);
                    qtseek = Atributo.tamanhoRegistro + qtseek;
                }
                atributo = Atributo.le(rafTabelas);
                System.out.println(atributo.toString());

            }
        } catch (Exception ex) {
            System.out.println("ex = " + ex.toString());
        } finally {
            if (rafTabelas != null) {
                rafTabelas.close();
            }
        }
    }

    public int getTamanhoRegistro(int idTabela) throws IOException {

        List<Atributo> listaAtributos = getAtributosById(idTabela);
        int tamanhoRegistro = 0;

        for (Atributo atributo : listaAtributos) {
            tamanhoRegistro += Atributo.factoryTamanhoAtributo(atributo.tipoAtributo);
        }
        return tamanhoRegistro;
    }

    public int getTamanhoRegistro(String nomeTabela) throws IOException, Exception {

        Catalogo tabela = getTabelaByName(nomeTabela);
        List<Atributo> listaAtributos = getAtributosById(tabela.idTabela);
        int tamanhoRegistro = 0;

        for (Atributo atributo : listaAtributos) {
            tamanhoRegistro += Atributo.factoryTamanhoAtributo(atributo.tipoAtributo);
        }
        return tamanhoRegistro;
    }
    
    
    public Grid lerArquivo(Catalogo tabela) throws IOException {

        RandomAccessFile rafTabelas = null;

        List<Atributo> listaAtributos = getAtributosById(tabela.idTabela);
        int tamanhoRegistro = 0;

        for (Atributo atributo : listaAtributos) {
            tamanhoRegistro += Atributo.factoryTamanhoAtributo(atributo.tipoAtributo);
        }
       
        int qtseek = 0;

        StringBuilder sb = new StringBuilder();
        int posicao = 0;
        Grid grid = new Grid(listaAtributos);
        try {

            rafTabelas = Arquivo.abrirArquivo(tabela.nomeTabela.trim() + ".dat", "r");
            boolean primeiraVez = true;
            
            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {
                    rafTabelas.seek(tamanhoRegistro + qtseek);
                    qtseek = tamanhoRegistro + qtseek;
                }

                Linha linha = new Linha();
                
                
                for (Atributo atributo : listaAtributos) {
                    
                    Registro reg = new Registro<>();
                    reg = reg.getRegistro(atributo, rafTabelas);
                    linha.add(reg);
                
                }

                grid.add(linha);
                posicao++;
            }
            

        } finally {

            if (rafTabelas != null) {
                rafTabelas.close();
            }

            return grid;
        }
    }

    
    public Grid lerArquivo(Catalogo tabela, int codigo) throws IOException {

        RandomAccessFile rafTabelas = null;
        RandomAccessFile rafHash = null;

        List<Atributo> listaAtributos = getAtributosById(tabela.idTabela);
        int tamanhoRegistro = 0;

        for (Atributo atributo : listaAtributos) {
            tamanhoRegistro += Atributo.factoryTamanhoAtributo(atributo.tipoAtributo);
        }
       
        int qtseek = 0;

        StringBuilder sb = new StringBuilder();

        Grid grid = new Grid(listaAtributos);
        try {

            rafTabelas = Arquivo.abrirArquivo(tabela.nomeTabela.trim() + ".dat", "r");
            rafHash = Arquivo.abrirArquivo(tabela.nomeTabela.trim() + "_hash.dat", "r");
            
            rafHash.seek(Funcao.hash(codigo) * CompartimentoHash.tamanhoCompartimento);
            
            long posicaoHash = rafHash.readLong();
            
            if (posicaoHash == -1) {
                return new Grid(listaAtributos);
            }
            
            
            while (true) 
            {                
                
                rafTabelas.seek(posicaoHash * getTamanhoRegistro(tabela.nomeTabela));
                
                int codigoAtual = rafTabelas.readInt();
                boolean removido = rafTabelas.readBoolean();
                
                if (codigoAtual == codigo && !removido) 
                {
                    rafTabelas.seek(posicaoHash * getTamanhoRegistro(tabela.nomeTabela));
                    Linha linha = new Linha();

                    for (Atributo atributo : listaAtributos) {

                        Registro reg = new Registro<>();
                        reg = reg.getRegistro(atributo, rafTabelas);
                        linha.add(reg);

                    }

                    grid.add(linha);
                    return grid;
                }
                
                posicaoHash = rafTabelas.readLong();
            }
            

        }
        catch (Exception ex) {
            Erro.log(ex);
        }        
        finally {

            if (rafTabelas != null) {
                rafTabelas.close();
            }
        }
        return new Grid(listaAtributos);
    }
    
    public void lerAtributosDaTabela(String nomeTabela) throws Exception{
        
        StringBuilder sb = new StringBuilder();
        Catalogo catalogo = getTabelaByName(nomeTabela.trim().toLowerCase());
        List<Atributo> listaAtributos = getAtributosById(catalogo.idTabela);
        
        int ct = 4;
        System.out.print("+");
        for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        System.out.print("| " + Funcao.padRight("ID DA TABELA", Configuracao.tamanhoString));
        System.out.print("| " + Funcao.padRight("NOME", Configuracao.tamanhoString));
        System.out.print("| " + Funcao.padRight("TAMANHO", Configuracao.tamanhoString));
        System.out.print("| " + Funcao.padRight("TIPO", Configuracao.tamanhoString));
        System.out.println("|");
        
        System.out.print("+");
        for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        for (Atributo atributo : listaAtributos) 
        {
            sb.append("| " + Funcao.padRight(Integer.toString(atributo.idTabela), Configuracao.tamanhoString));
            sb.append("| " + Funcao.padRight(atributo.nomeAtributo.toString().trim(), Configuracao.tamanhoString));
            sb.append("| " + Funcao.padRight(Integer.toString(atributo.tamanho), Configuracao.tamanhoString));
            sb.append("| " + Funcao.padRight(Funcao.factoryTipoAtributo(atributo.tipoAtributo), Configuracao.tamanhoString));

            System.out.println(sb.toString() + "|"); 
            sb = new StringBuilder();  
        }
        
        System.out.print("+");
        for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        
    }
    
    public List<Atributo> getAtributosById(int id) throws IOException {
        // Este método está esceneando a tabela atributo inteira.

        List<Atributo> listaAtributos = new ArrayList<Atributo>();


        RandomAccessFile rafAtributos = null;
        Atributo atributo = null;

        int qtseek = 0;

        try {
            rafAtributos = Arquivo.abrirArquivo("atributo.dat", "r");
            boolean primeiraVez = true;

            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {
                    rafAtributos.seek(Atributo.tamanhoRegistro + qtseek);
                    qtseek = Atributo.tamanhoRegistro + qtseek;
                }
                atributo = Atributo.le(rafAtributos);
                if (atributo.idTabela == id) {
                    listaAtributos.add(atributo);
                }

            }
        } catch (Exception ex) {
            Erro.log(ex);
        } finally {
            if (rafAtributos != null) {
                rafAtributos.close();
            }

            return listaAtributos;
        }


    }
    
    public Atributo getAtributoByName(int idTabela, String nomeAtributo) throws IOException {

        RandomAccessFile rafAtributos = null;
        Atributo atributo = null;

        int qtseek = 0;

        try {
            rafAtributos = Arquivo.abrirArquivo("atributo.dat", "r");
            boolean primeiraVez = true;

            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {
                    rafAtributos.seek(Atributo.tamanhoRegistro + qtseek);
                    qtseek = Atributo.tamanhoRegistro + qtseek;
                }
                atributo = Atributo.le(rafAtributos);
                if (atributo.idTabela == idTabela) {
                    if (atributo.nomeAtributo.toLowerCase().trim().equals(nomeAtributo)) {
                        return atributo;
                    }
                }

            }
        } catch (Exception ex) {
            Erro.log(ex);
        } finally {
            if (rafAtributos != null) {
                rafAtributos.close();
            }
        }
        return null;

    }

    public String getNomeTabelaById(int idTabela) throws IOException {

        RandomAccessFile rafTabelas = null;
        Catalogo tabela;
        String nomeTabela = null;

        int qtseek = 0;

        try {
            rafTabelas = Arquivo.abrirArquivo("catalogo.dat", "r");
            boolean primeiraVez = true;

            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {
                    rafTabelas.seek(Catalogo.tamanhoRegistro + qtseek);
                    qtseek = Catalogo.tamanhoRegistro + qtseek;
                }
                tabela = Catalogo.le(rafTabelas);
                if (tabela.idTabela == idTabela) {
                    nomeTabela = tabela.nomeTabela;
                }

            }
        } catch (Exception ex) {
            Erro.log(ex);
        } finally {
            if (rafTabelas != null) {
                rafTabelas.close();
            }
            return nomeTabela;
        }

    }

    public void lerArquivoHash(String nomeArquivo) throws IOException {
        StringBuilder sb = new StringBuilder();
        RandomAccessFile rafTabelaHash = null;
        int ct = 2;
        try {
            boolean primeiraVez = true;
            rafTabelaHash = Arquivo.abrirArquivo(nomeArquivo + ".dat", "r");
            
            if (rafTabelaHash == null) {
                throw new Exception("Tabela inexistente.");
            }
            
            System.out.print("+");
            for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
                System.out.print("-");
            }
            System.out.println("+");

            System.out.print("| " + Funcao.padRight("NÚMERO DO COMPARTIMENTO HASH", Configuracao.tamanhoString));
            System.out.print("| " + Funcao.padRight("PRÓXIMO", Configuracao.tamanhoString));
            System.out.println("|");

            System.out.print("+");
            for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
                System.out.print("-");
            }
            System.out.println("+");
            

            int i = 0;
            int qtSeek = 0;
            CompartimentoHash ch;

            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {

                    rafTabelaHash.seek(CompartimentoHash.tamanhoCompartimento + qtSeek);
                    qtSeek += CompartimentoHash.tamanhoCompartimento;

                }
                ch = CompartimentoHash.le(rafTabelaHash);
                sb.append("| " + Funcao.padRight(Integer.toString(i), Configuracao.tamanhoString));
                sb.append("| " + Funcao.padRight(Long.toString(ch.prox), Configuracao.tamanhoString));
                
                i++;
                System.out.println(sb.toString() + "|"); 
                sb = new StringBuilder();                
            }

        } catch (Exception ex) {
            Erro.log(ex);
        } finally {
            if (rafTabelaHash != null) {
                rafTabelaHash.close();
                System.out.print("+");
                for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
                    System.out.print("-");
                }
                System.out.println("+");                
            }

        }

    }

    public void lerCatalogo() throws IOException {
        StringBuilder sb = new StringBuilder();
        RandomAccessFile rafTabelas = null;
        Catalogo tabela = null;
        Catalogo ultimaTabela = null;
        int qtseek = 0;

        int ct = 3;
        System.out.print("+");
        for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        System.out.print("| " + Funcao.padRight("CÓDIGO", Configuracao.tamanhoString));
        System.out.print("| " + Funcao.padRight("NOME", Configuracao.tamanhoString));
        System.out.print("| " + Funcao.padRight("QUANTIDADE DE REGISTROS", Configuracao.tamanhoString));
        //System.out.print("| " + Funcao.padRight("REMOVIDO", Configuracao.tamanhoString));
        System.out.println("|");
        
        System.out.print("+");
        for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
            System.out.print("-");
        }
        System.out.println("+");
        
        
        try {
            rafTabelas = Arquivo.abrirArquivo("catalogo.dat", "r");
            boolean primeiraVez = true;

            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {
                    rafTabelas.seek(Catalogo.tamanhoRegistro + qtseek);
                    qtseek = Catalogo.tamanhoRegistro + qtseek;
                }
                tabela = Catalogo.le(rafTabelas);
                
                if (tabela.removido) {
                    continue;
                }
                sb.append("| " + Funcao.padRight(tabela.idTabela.toString(), Configuracao.tamanhoString));
                sb.append("| " + Funcao.padRight(tabela.nomeTabela, Configuracao.tamanhoString));
                sb.append("| " + Funcao.padRight(Long.toString(tabela.qtRegistro), Configuracao.tamanhoString));
                //sb.append("| " + Funcao.padRight(Boolean.toString(tabela.removido), Configuracao.tamanhoString));
                
                System.out.println(sb.toString() + "|"); 
                sb = new StringBuilder();
            }
        
            
            
        } catch (Exception ex) {
            Erro.log(ex);
        } finally {
            if (rafTabelas != null) {
                rafTabelas.close();
            }
            System.out.print("+");
            for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
                System.out.print("-");
            }
            System.out.println("+");
        }

    }

    
    public void removerTabelaNoCatalogo(String nomeTabela) throws IOException {
        
        RandomAccessFile rafTabelas = null;
        Catalogo tabela = null;
        Catalogo ultimaTabela = null;
        int qtseek = 0;

        
        try {
            rafTabelas = Arquivo.abrirArquivo("catalogo.dat", "rw");
            boolean primeiraVez = true;

            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {
                    rafTabelas.seek(Catalogo.tamanhoRegistro + qtseek);
                    qtseek = Catalogo.tamanhoRegistro + qtseek;
                }
                tabela = Catalogo.le(rafTabelas);
                
                if (tabela.nomeTabela.trim().toLowerCase().equalsIgnoreCase(nomeTabela.trim().toLowerCase())) {
                   
                    Catalogo tabelaAtualizada = new Catalogo();
                    tabelaAtualizada.idTabela = tabela.idTabela;
                    tabelaAtualizada.nomeTabela = tabela.nomeTabela;
                    tabelaAtualizada.qtRegistro = tabela.qtRegistro;
                    tabelaAtualizada.removido = true;

                    rafTabelas.seek(qtseek);
                    
                    tabelaAtualizada.salva(rafTabelas);
                }

            }
        
            
            
        } catch (Exception ex) {
            Erro.log(ex);
        } finally {
            if (rafTabelas != null) {
                rafTabelas.close();
            }
        
        }

    }
    
    public List<Catalogo> getAllTabelas() throws IOException {

        RandomAccessFile rafTabelas = null;
        List<Catalogo> listaTabelas = new ArrayList<Catalogo>();
        Catalogo tabela = null;
        Catalogo ultimaTabela = null;
        int qtseek = 0;
  
        try {
            rafTabelas = Arquivo.abrirArquivo("catalogo.dat", "r");
            boolean primeiraVez = true;

            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {
                    rafTabelas.seek(Catalogo.tamanhoRegistro + qtseek);
                    qtseek = Catalogo.tamanhoRegistro + qtseek;
                }
                tabela = Catalogo.le(rafTabelas);
                //System.out.println("Id:" + tabela.idTabela + " - nome:" + tabela.nomeTabela.trim() + " - Qt Reg:" + tabela.qtRegistro);
                listaTabelas.add(tabela);
                
            }
        } catch (Exception ex) {
            if (!(ex instanceof NullPointerException)) {
                Erro.log(ex);
            }
            
        } finally {
            if (rafTabelas != null) {
                rafTabelas.close();
            }

        }
        return listaTabelas;

    }

    public void lerArquivo2(Catalogo tabela, List<String> selecionados, String[] filtros) throws IOException {


        
        /*--preparar filtros---*/
        
        
        
        if (filtros != null) {

            String filtro1 = filtros[0];
            String filtro2 = filtros[1];
            String operador = filtros[2];
            String atributoFiltro1 = null;
            String atributoFiltro2 = null;
            String valorFiltro1 = null;
            String valorFiltro2 = null;            
            String condicaoFiltro1 = null;
            String condicaoFiltro2 = null;
            String[] partesFiltro = null;
            
            //=,<,>,<>,<=,>=
            List<String> condicoes = new ArrayList<String>();
            condicoes.add("=");
            condicoes.add("<");
            condicoes.add(">");
            condicoes.add("<=");
            condicoes.add(">=");
            condicoes.add("<>");
            
            
            if (filtro1 != null) {
             
                for (String cond : condicoes) {
                    if (filtro1.contains(cond)) {
                        condicaoFiltro1 = cond;
                    }
                }
                partesFiltro = filtro1.split(condicaoFiltro1);
                atributoFiltro1 = partesFiltro[0];
                valorFiltro1 = partesFiltro[1];

            }
            else if (filtro2 != null){
                
                for (String cond : condicoes) {
                    if (filtro2.contains(cond)) {
                        condicaoFiltro2 = cond;
                    }
                }                
                
                partesFiltro = filtro2.split(condicaoFiltro2);
                atributoFiltro2 = partesFiltro[0];
                valorFiltro2 = partesFiltro[1];                
                
            }
            
            
        }
        /*---------------------*/
        
        
        
        RandomAccessFile rafTabelas = null;

        List<Atributo> listaAtributos = getAtributosById(tabela.idTabela);
        int tamanhoRegistro = 0;

        List<String> selecionados2 = new ArrayList<String>();
        
        for (Atributo attr : listaAtributos) {
            if (selecionados.contains(attr.nomeAtributo.trim().toLowerCase())) {
                selecionados2.add(attr.nomeAtributo);
            }
        }
        
        selecionados = selecionados2;
        
        for (Atributo atributo : listaAtributos) {
            tamanhoRegistro += Atributo.factoryTamanhoAtributo(atributo.tipoAtributo);
        }

        int qtseek = 0;


        if (selecionados.get(0).toString().trim().equals("*")) {
            for (Atributo atributo : listaAtributos) {
                selecionados.add(atributo.nomeAtributo.trim());
            }        
        }
        
        StringBuilder sb = new StringBuilder();
        int posicao = 0;
        int ct = 0;
        for (String string : selecionados) {
            for (Atributo atributo : listaAtributos) {
                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {
                    ct++;
                }
            }
        }

        try {

            rafTabelas = Arquivo.abrirArquivo(tabela.nomeTabela.trim() + ".dat", "r");
            boolean primeiraVez = true;

            //formatar cabecalho------------------------------------------------
            System.out.print("+");
            for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
                System.out.print("-");
            }

            System.out.println("+");
            String nomeAttr = "";

            for (String string : selecionados) {
                for (Atributo atributo : listaAtributos) {
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

            //end cabecalho-----------------------------------------------------------



            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {
                    rafTabelas.seek(tamanhoRegistro + qtseek);
                    qtseek = tamanhoRegistro + qtseek;
                }

                
                boolean imprime = true;

                for (Atributo atributo : listaAtributos) {
                    switch (atributo.tipoAtributo) {
                        case 1:
                            Integer integer = rafTabelas.readInt();
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {
                                    sb.append("| " + Funcao.padRight(integer.toString(), Configuracao.tamanhoString));
                                }
                            }

                            break;
                        case 2:
                            Float fl = rafTabelas.readFloat();
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {

                                    sb.append("| " + Funcao.padRight(fl.toString(), Configuracao.tamanhoString));
                                }
                            }
                            break;
                        case 3:
                            String str = rafTabelas.readUTF().trim();
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {
                                    sb.append("| " + Funcao.padRight(str, Configuracao.tamanhoString));
                                }
                            }
                            break;
                        case 4:
                            char c = rafTabelas.readChar();
                            String strChar = c + "";
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {

                                    sb.append("| " + Funcao.padRight(strChar, Configuracao.tamanhoString));
                                }
                            }
                            break;
                        case 5:
                            Double d = rafTabelas.readDouble();
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {

                                    sb.append("| " + Funcao.padRight(d.toString(), Configuracao.tamanhoString));
                                }

                            }
                            break;
                        case 6:
                            String removido = "false";
                            if (rafTabelas.readBoolean()) {
                                removido = "true";
                            }
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {

                                    sb.append("| " + Funcao.padRight(removido, Configuracao.tamanhoString));
                                }
                            }
                            break;
                        case 7:
                            Long l = rafTabelas.readLong();
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {
                                    sb.append("| " + Funcao.padRight(l.toString(), Configuracao.tamanhoString));
                                }
                            }
                            break;

                    }

                }


                System.out.println(sb.toString() + "|");//printa a tupla    

                sb = new StringBuilder();
                posicao++;
            }



        } catch (Exception ex) {
            Erro.log(ex);
        } finally {
            System.out.print("+");
            for (int i = 0; i < ((ct * Configuracao.tamanhoString) + ct + ct - 1); i++) {
                System.out.print("-");
            }
            System.out.println("+");

            if (rafTabelas != null) {
                rafTabelas.close();
            }


        }
    }
    
    public void lerArquivo3(Catalogo tabela, List<String> selecionados, String[] filtros) throws IOException {


        System.out.println("-----------Tabela" + tabela.nomeTabela.toUpperCase() + "-----------");
        RandomAccessFile rafTabelas = null;

        List<Atributo> listaAtributos = getAtributosById(tabela.idTabela);
        int tamanhoRegistro = 0;

        for (Atributo atributo : listaAtributos) {
            tamanhoRegistro += Atributo.factoryTamanhoAtributo(atributo.tipoAtributo);
        }

        int qtseek = 0;

        StringBuilder sb = new StringBuilder();
        int posicao = 0;

        try {

            rafTabelas = Arquivo.abrirArquivo(tabela.nomeTabela + ".dat", "r");
            boolean primeiraVez = true;

            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {
                    rafTabelas.seek(tamanhoRegistro + qtseek);
                    qtseek = tamanhoRegistro + qtseek;
                }




                for (Atributo atributo : listaAtributos) {
                    switch (atributo.tipoAtributo) {
                        case 1:
                            sb.append(" " + atributo.nomeAtributo.trim() + ": " + rafTabelas.readInt());
                            break;
                        case 2:
                            sb.append(" " + atributo.nomeAtributo.trim() + ": " + rafTabelas.readFloat());
                            break;
                        case 3:
                            sb.append(" " + atributo.nomeAtributo.trim() + ": " + rafTabelas.readUTF().trim());
                            break;
                        case 4:
                            sb.append(" " + atributo.nomeAtributo.trim() + ": " + rafTabelas.readChar());
                            break;
                        case 5:
                            sb.append(" " + atributo.nomeAtributo.trim() + ": " + rafTabelas.readDouble());
                            break;
                        case 6:
                            sb.append(" " + atributo.nomeAtributo.trim() + ": " + rafTabelas.readBoolean());
                            break;
                        case 7:
                            sb.append(" " + atributo.nomeAtributo.trim() + ": " + rafTabelas.readLong());
                            break;

                    }

                }


                System.out.println("pos:" + posicao + " - " + sb.toString());//printa a tupla    
                sb = new StringBuilder();
                posicao++;
            }

        } catch (Exception ex) {
            Erro.log(ex);
        } finally {
            if (rafTabelas != null) {
                rafTabelas.close();
            }


        }
    }
    
    
    public Catalogo getTabelaByName(String nomeTabela) throws Exception {

        RandomAccessFile rafTabelas = null;
        Catalogo tabela = null;
        Catalogo tabelaEncontrada = null;

        int qtseek = 0;

        try {
            rafTabelas = Arquivo.abrirArquivo("catalogo.dat", "r");
            boolean primeiraVez = true;

            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {
                    rafTabelas.seek(Catalogo.tamanhoRegistro + qtseek);
                    qtseek = Catalogo.tamanhoRegistro + qtseek;
                }
                tabela = Catalogo.le(rafTabelas);
                if (tabela.nomeTabela.trim().equalsIgnoreCase(nomeTabela.trim())) {
                    tabelaEncontrada = tabela;
                }

            }
        }
        finally {
            if (rafTabelas != null) {
                rafTabelas.close();
            }
            return tabelaEncontrada;
        }

    }

    private void lerABagaca(){
        
        
        List<Registro> listaRegistros = new ArrayList<Registro>();
        RandomAccessFile rafTabelas = null;
        Atributo attrCodigo = new Atributo(6, "Codigo", 1, Configuracao.tamanhoInteger);
        Atributo attrRemovido = new Atributo(6, "removido", 6, Configuracao.tamanhoBoolean);
        Atributo attrProx = new Atributo(6, "prox", 7, Configuracao.tamanhoLong);
        Atributo attrNome = new Atributo(6, "nome", 3, Configuracao.tamanhoString);
        Atributo attrIdade = new Atributo(6, "idade", 1, Configuracao.tamanhoInteger);
        

        attrCodigo.idTabela = 6;
        
        int qtseek = 0;

        try {
            rafTabelas = Arquivo.abrirArquivoFessora("dadosIniciais.dat", "r");

            while (true) {
                
                Registro reg = new Registro();
                reg.atributo = attrCodigo;
                reg.registro = rafTabelas.readInt();
                
                if ((int)reg.registro < 500) {
                    rafTabelas.readUTF();
                    rafTabelas.readInt();
                    continue;
                }
                listaRegistros.add(reg);
                        
                reg = new Registro();
                reg.atributo = attrRemovido;
                reg.registro = false;
                listaRegistros.add(reg);
                        
                reg = new Registro();
                reg.atributo = attrProx;
                reg.registro = -1;
                listaRegistros.add(reg);
                
                reg = new Registro();
                reg.atributo = attrNome;
                reg.registro = Funcao.padRight(rafTabelas.readUTF());
                listaRegistros.add(reg);
                
                reg = new Registro();
                reg.atributo = attrIdade;
                reg.registro = rafTabelas.readInt();
                listaRegistros.add(reg);
                
                new ManipulacaoFacade().inserirDadosEntradaProfessora(listaRegistros);
                listaRegistros.clear();
            }
        }
        catch(Exception ex){
            System.out.println("ex = " + ex);
        }
        finally {
            if (rafTabelas != null) {
                try {
                    rafTabelas.close();
                } catch (IOException ex) {
                    Erro.log(ex);
                }
            }
        }
    }


}
