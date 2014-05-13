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
import trabalhoed2.sgbd.entidades.Catalogo;
import trabalhoed2.sgbd.entidades.CompartimentoHash;

/**
 *
 * @author pedro
 */
public class ConteudoArquivos {

    public static void main(String[] args) throws IOException {

        //new ConteudoArquivos().teste();
        //new ConteudoArquivos().teste2();
        //Funcao.listaTabelas();
        List<String> selecionados = new ArrayList<>();
        selecionados.add("*");

        new ConteudoArquivos().lerArquivo2(new Catalogo(1, "teste", 0), selecionados, null);
        new ConteudoArquivos().lerArquivoHash("teste_hash");
        new ConteudoArquivos().lerCatalogo();
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

    public void lerArquivo(Catalogo tabela) throws IOException {

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

        RandomAccessFile rafTabelaHash = null;
        try {
            boolean primeiraVez = true;
            rafTabelaHash = Arquivo.abrirArquivo(nomeArquivo + ".dat", "r");
            int ct = 0;
            int qtSeek = 0;
            CompartimentoHash ch;
            System.out.println("---------" + nomeArquivo.toUpperCase() + "---------");

            while (true) {
                if (primeiraVez) {
                    primeiraVez = false;
                } else {

                    rafTabelaHash.seek(CompartimentoHash.tamanhoCompartimento + qtSeek);
                    qtSeek += CompartimentoHash.tamanhoCompartimento;

                }
                ch = CompartimentoHash.le(rafTabelaHash);
                System.out.println(ct + " - " + ch.prox);
                ct++;
            }

        } catch (Exception ex) {
            Erro.log(ex);
        } finally {
            if (rafTabelaHash != null) {
                rafTabelaHash.close();
            }
        }

    }

    public void lerCatalogo() throws IOException {

        RandomAccessFile rafTabelas = null;
        Catalogo tabela = null;
        Catalogo ultimaTabela = null;
        int qtseek = 0;
        System.out.println("--------CATÁLOGO--------");
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
                System.out.println("Id:" + tabela.idTabela + " - nome:" + tabela.nomeTabela.trim() + " - Qt Reg:" + tabela.qtRegistro);

            }
        } catch (Exception ex) {
            Erro.log(ex);
        } finally {
            if (rafTabelas != null) {
                rafTabelas.close();
            }

        }

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
            for (int i = 0; i < ((ct * 32) + ct + ct - 1); i++) {
                System.out.print("-");
            }

            System.out.println("+");
            String nomeAttr = "";

            for (String string : selecionados) {
                for (Atributo atributo : listaAtributos) {
                    nomeAttr = atributo.nomeAtributo.trim().toUpperCase();
                    if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {
                        System.out.print("| " + Funcao.padRight(nomeAttr, 32));
                    }

                }
            }

            System.out.println("|");

            System.out.print("+");
            for (int i = 0; i < ((ct * 32) + ct + ct - 1); i++) {
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
                                    sb.append("| " + Funcao.padRight(integer.toString(), 32));
                                }
                            }

                            break;
                        case 2:
                            Float fl = rafTabelas.readFloat();
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {

                                    sb.append("| " + Funcao.padRight(fl.toString(), 32));
                                }
                            }
                            break;
                        case 3:
                            String str = rafTabelas.readUTF().trim();
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {
                                    sb.append("| " + Funcao.padRight(str, 32));
                                }
                            }
                            break;
                        case 4:
                            char c = rafTabelas.readChar();
                            String strChar = c + "";
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {

                                    sb.append("| " + Funcao.padRight(strChar, 32));
                                }
                            }
                            break;
                        case 5:
                            Double d = rafTabelas.readDouble();
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {

                                    sb.append("| " + Funcao.padRight(d.toString(), 32));
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

                                    sb.append("| " + Funcao.padRight(removido, 32));
                                }
                            }
                            break;
                        case 7:
                            Long l = rafTabelas.readLong();
                            for (String string : selecionados) {
                                if (atributo.nomeAtributo.trim().equalsIgnoreCase(string.trim())) {
                                    sb.append("| " + Funcao.padRight(l.toString(), 32));
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
            for (int i = 0; i < ((ct * 32) + ct + ct - 1); i++) {
                System.out.print("-");
            }
            System.out.println("+");

            if (rafTabelas != null) {
                rafTabelas.close();
            }


        }
    }
    
    public Catalogo getTabelaByName(String nomeTabela) throws IOException {

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
        } catch (Exception ex) {
            Erro.log(ex);
        } finally {
            if (rafTabelas != null) {
                rafTabelas.close();
            }
            return tabelaEncontrada;
        }

    }
}
