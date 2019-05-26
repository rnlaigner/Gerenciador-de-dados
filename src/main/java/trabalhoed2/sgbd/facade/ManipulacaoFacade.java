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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static trabalhoed2.sgbd.TrabalhoED2SGBD.main;
import trabalhoed2.sgbd.entidades.Atributo;
import trabalhoed2.sgbd.entidades.Atualizacao;
import trabalhoed2.sgbd.entidades.CompartimentoHash;
import trabalhoed2.sgbd.entidades.Grid;
import trabalhoed2.sgbd.entidades.Linha;
import trabalhoed2.sgbd.entidades.Registro;
import trabalhoed2.sgbd.entidades.RetornoGrid;
import trabalhoed2.sgbd.util.Arquivo;
import trabalhoed2.sgbd.util.Configuracao;
import trabalhoed2.sgbd.util.ConteudoArquivos;
import trabalhoed2.sgbd.util.Erro;
import trabalhoed2.sgbd.util.Funcao;

/**
 *
 *
 */
public class ManipulacaoFacade {
    
     public void inserirDados2(){

        Scanner scan = new Scanner(System.in);
        
        try 
        {

            int idTabela = EstruturaFacade.selecionarTabela();

            ConteudoArquivos ca = new ConteudoArquivos();
            List<Atributo> listaAtributos = ca.getAtributosById(idTabela);    
            List<Registro> listaRegistros = solicitarEntradaDados2(listaAtributos);
            
            //escrita
            inserirDadosEspecOps(listaRegistros, idTabela);
            System.out.println("\nRegistro inserido com sucesso.\n");
        }
        catch(Exception ex){
            System.out.println("zica ao inserir = " + ex);
        }
     }
    
    
    public void inserirDados(){
        
        Scanner scan = new Scanner(System.in);
        RandomAccessFile out = null;
        long posicao = 0;
        
        try {
            

            int idTabela = EstruturaFacade.selecionarTabela();

            ConteudoArquivos ca = new ConteudoArquivos();
            List<Atributo> listaAtributos = ca.getAtributosById(idTabela);        



            //abre o arquivo de dados
            out = Arquivo.abrirArquivo(ca.getNomeTabelaById(idTabela).trim()+".dat", "rw");


            /*  Obtendo primeiramente o código */
            System.out.println("Digite o código:");
            int codigoRegistro = scan.nextInt();//todo Validação
            //abre o arquivo de hash do arquivo de dados
            RandomAccessFile outHash = null;
            outHash = Arquivo.abrirArquivo(ca.getNomeTabelaById(idTabela).trim()+"_hash.dat", "rw");
            //obtem o valor do hash a partir do valor de entrada (codigo)
            int hash = Funcao.hash(codigoRegistro);
            //dá um seek no arquivo de hash
            outHash.seek(CompartimentoHash.tamanhoCompartimento * hash);                            

            //tamanho do registro da tabela dinâmica
            int tamanhoRegistro = new ConteudoArquivos().getTamanhoRegistro(idTabela);

            //valorCompartimento passa a receber o valor lido no seek do arquivo de hash
            long valorCompartimento = outHash.readLong();

            if (valorCompartimento == -1) {

                EstruturaFacade.addRemoveQtRegistro(idTabela, 'a');

                posicao = EstruturaFacade.getQuantidadeRegistrosById(idTabela) - 1;//posicao recebe o final do arquivo de dados

                outHash.seek(CompartimentoHash.tamanhoCompartimento * hash);//voltando
                outHash.writeLong(posicao);//escrito no arquivo de hash a posicao

                //seek para posicionar no arquivo de dados
                out.seek((posicao) * tamanhoRegistro);

                //agora gravar na última posição no arquivo de dados
                solicitarEntradaDados(out, listaAtributos, true,codigoRegistro);


            }
            else
            {

                long posicaoAtual = valorCompartimento;
                long posicaoRemovido = -1;
                long posicaoInserir = -1;

                out.seek(valorCompartimento * tamanhoRegistro);

                int codigo = out.readInt(); 
                boolean removido = out.readBoolean();
                long prox = out.readLong();


                while (true) {                

                    //já estamos no primeiro elemento da lista
                    if (posicaoRemovido == -1 && removido) {
                        posicaoRemovido = posicaoAtual;
                    }

                    if (codigoRegistro == codigo && !removido) {
                        throw new Exception("Já existe um registro com este código");
                    }

                    if (prox == -1) {
                        if (posicaoRemovido == -1) {
                            //inserir no final do arquivo
                            EstruturaFacade.addRemoveQtRegistro(idTabela, 'a');
                            posicaoInserir = EstruturaFacade.getQuantidadeRegistrosById(idTabela) - 1;
                            out.seek((posicaoInserir) * tamanhoRegistro);
                            solicitarEntradaDados(out, listaAtributos, true, codigoRegistro);
                            out.seek(posicaoAtual * tamanhoRegistro);
                            out.readInt();out.readBoolean();
                            out.writeLong(posicaoInserir);
                            System.out.println("Registro inserido.");
                            break;

                        }
                        else
                        {
                            //inserir na posição "removida"
                            out.seek(posicaoRemovido * tamanhoRegistro);
                            solicitarEntradaDados(out, listaAtributos, false, codigoRegistro);
                            System.out.println("Registro inserido no lugar de um removido.");
                            break;
                        }
                    }

                    posicaoAtual = prox;
                    out.seek(prox * tamanhoRegistro);
                    codigo = out.readInt(); 
                    removido = out.readBoolean();
                    prox = out.readLong();


                }


            }
        } catch (Exception ex) {
            Erro.log(ex);
        }
        
    }
    
    
    public void inserirDadosEspecOps(List<Registro> listaRegistros, int idTabela){
        
        RandomAccessFile out = null;
        long posicao = 0;
        
        try 
        {
     
            ConteudoArquivos ca = new ConteudoArquivos();
            List<Atributo> listaAtributos = ca.getAtributosById(idTabela);        


            //abre o arquivo de dados
            out = Arquivo.abrirArquivo(ca.getNomeTabelaById(idTabela).trim()+".dat", "rw");


            /*  Obtendo primeiramente o código */
            
            int codigoRegistro = (int)listaRegistros.get(0).registro;//scan.nextInt();//todo Validação
            //abre o arquivo de hash do arquivo de dados
            RandomAccessFile outHash = null;
            outHash = Arquivo.abrirArquivo(ca.getNomeTabelaById(idTabela).trim()+"_hash.dat", "rw");
            //obtem o valor do hash a partir do valor de entrada (codigo)
            int hash = Funcao.hash(codigoRegistro);
            //dá um seek no arquivo de hash
            outHash.seek(CompartimentoHash.tamanhoCompartimento * hash);                            

            //tamanho do registro da tabela dinâmica
            int tamanhoRegistro = new ConteudoArquivos().getTamanhoRegistro(idTabela);

            //valorCompartimento passa a receber o valor lido no seek do arquivo de hash
            long valorCompartimento = outHash.readLong();

            if (valorCompartimento == -1) {

                EstruturaFacade.addRemoveQtRegistro(idTabela, 'a');

                posicao = EstruturaFacade.getQuantidadeRegistrosById(idTabela) - 1;//posicao recebe o final do arquivo de dados

                outHash.seek(CompartimentoHash.tamanhoCompartimento * hash);//voltando
                outHash.writeLong(posicao);//escrito no arquivo de hash a posicao

                //seek para posicionar no arquivo de dados
                out.seek((posicao) * tamanhoRegistro);

                //agora gravar na última posição no arquivo de dados
                EscreverDados(listaRegistros, out, true);


            }
            else
            {

                long posicaoAtual = valorCompartimento;
                long posicaoRemovido = -1;
                long posicaoInserir = -1;

                out.seek(valorCompartimento * tamanhoRegistro);

                int codigo = out.readInt(); 
                boolean removido = out.readBoolean();
                long prox = out.readLong();


                while (true) {                

                    //já estamos no primeiro elemento da lista
                    if (posicaoRemovido == -1 && removido) {
                        posicaoRemovido = posicaoAtual;
                    }

                    if (codigoRegistro == codigo && !removido) {
                        throw new Exception("Já existe um registro com este código");
                    }

                    if (prox == -1) {
                        if (posicaoRemovido == -1) {
                            //inserir no final do arquivo
                            EstruturaFacade.addRemoveQtRegistro(idTabela, 'a');
                            posicaoInserir = EstruturaFacade.getQuantidadeRegistrosById(idTabela) - 1;
                            out.seek((posicaoInserir) * tamanhoRegistro);
                            EscreverDados(listaRegistros, out, true);
                            out.seek(posicaoAtual * tamanhoRegistro);
                            out.readInt();out.readBoolean();
                            out.writeLong(posicaoInserir);
                            System.out.println("Registro inserido.");
                            break;

                        }
                        else
                        {
                            //inserir na posição "removida"
                            out.seek(posicaoRemovido * tamanhoRegistro);
                            EscreverDados(listaRegistros, out, false);
                            System.out.println("Registro inserido no lugar de um removido.");
                            break;
                        }
                    }

                    posicaoAtual = prox;
                    out.seek(prox * tamanhoRegistro);
                    codigo = out.readInt(); 
                    removido = out.readBoolean();
                    prox = out.readLong();


                }


            }
        } catch (Exception ex) {
            Erro.log(ex);
        }
        
    }
    
    private void solicitarEntradaDados(RandomAccessFile out, List<Atributo> listaAtributos, boolean alteraProx, int codigo) throws IOException {
        
        Scanner scan = new Scanner(System.in);
        Object valor = null;
        boolean primeiraVez = true;//para o código
        
        for (Atributo attr : listaAtributos) { 
            
            if (!primeiraVez 
                && (!attr.nomeAtributo.trim().equalsIgnoreCase("removido") 
                && !attr.nomeAtributo.trim().equalsIgnoreCase("prox"))) 
            {
                System.out.println("Digite o  " + attr.nomeAtributo.trim() + ":");
            }
            
            switch (attr.tipoAtributo)
            {
                case 1:
                    if (primeiraVez) {
                        out.writeInt(codigo);
                        primeiraVez = false;
                        break;
                    }
                    valor = scan.nextInt();
                    out.writeInt((int)valor);
                    break;
                case 2:
                    valor = scan.nextFloat();
                    out.writeFloat((float)valor);
                    break;
                case 3:
                    valor = scan.nextLine();
                    out.writeUTF(Funcao.padRight((String)valor));
                    break;
                case 4:
                    valor = scan.next();
                    String valorString = (String)valor;
                    out.writeChar(valorString.charAt(0));
                    break;
                case 5:
                    valor = scan.nextDouble();
                    out.writeDouble((double)valor);
                    break;
                case 6:
                    if (attr.nomeAtributo.trim().equalsIgnoreCase("removido")) {
                        out.writeBoolean(false);
                        break;
                    }
                    valor = scan.nextBoolean();
                    out.writeBoolean((boolean)valor);
                    break;
                case 7:
                    if (alteraProx) {
                        out.writeLong(-1);
                    }
                    else
                    {
                        out.readLong();//ignorar
                    }
                    
                    break;
            }
        }
    }

    private List<Registro> solicitarEntradaDados2(List<Atributo> listaAtributos) {
        
        Scanner scan = new Scanner(System.in);

        List<Registro> listaRegistros = new ArrayList<Registro>();
        
        try
        {
            for (Atributo attr : listaAtributos) { 

                
                if ((attr.nomeAtributo.trim().equalsIgnoreCase("removido") 
                    || attr.nomeAtributo.trim().equalsIgnoreCase("prox"))) 
                {
                    if (attr.nomeAtributo.trim().equalsIgnoreCase("removido")) {
                        Registro reg = new Registro<>();
                        reg = reg.setRegistro(attr);
                        listaRegistros.add(reg);
                    }
                    else if (attr.nomeAtributo.trim().equalsIgnoreCase("prox")){
                        Registro reg = new Registro<>();
                        reg = reg.setProx(attr);
                        listaRegistros.add(reg);
                    }
                    
                    continue;
                }

                if (attr.nomeAtributo.trim().toLowerCase().equals("codigo")) {
                    System.out.println("Digite o Código (Chave Primária): ");
                }
                else
                {
                    System.out.println("Digite o atributo '" + attr.nomeAtributo.trim() + "': ");
                }
                

                Registro reg = new Registro<>();
                reg = reg.setRegistro(attr);
                listaRegistros.add(reg);

            }
        }
        catch(Exception ex)
        {
            throw ex;
        }
        
        return listaRegistros;
    }

    
    
    public void alterarDados() throws Exception {
        
        Scanner scan = new Scanner(System.in);
        System.out.println("Digite a query de alteração: ");
        System.out.print("> ");
        String comando = scan.nextLine();
        comando = comando.toLowerCase();
        if (comando.trim().toLowerCase().equals("quit")) {
           trabalhoed2.sgbd.TrabalhoED2SGBD.manipularDados();
           return;
        }
        
        StringBuilder comandoSelect = new StringBuilder();
        
        String tabela = null;
        String set = null;
        String where = null;
        
        //update teste set nome = pedro, idade = 20 where codigo = 2
        
        Matcher mFrom = Pattern.compile("update(.*?)set").matcher(comando);
        Matcher mSet = Pattern.compile("set[^\\n]*").matcher(comando);
        Matcher mSetW = Pattern.compile("set(.*?)where").matcher(comando);
        Matcher mWhere = Pattern.compile("where[^\\n]*").matcher(comando);
        
        
        if (mFrom.find()) {
            tabela = mFrom.group(1);
        }
        if(mSetW.find()){
            set = mSetW.group(1);
        }
        else if (mSet.find()) {
            set = mSet.group(0).replace("set", "");
        } 
        if (mWhere.find()) {
            where = mWhere.group(0).replace("where", "");
        }
        
          
        
        comandoSelect.append("select * from ");
        comandoSelect.append(tabela.trim());
        
        if (where != null) 
        {
            comandoSelect.append(" where "+ where.trim());
        }
        
        
        RandomAccessFile outHash = null;
        RandomAccessFile out = null;
        RetornoGrid retornoGrid = null;
        try
        {
             retornoGrid = new trabalhoed2.sgbd.TrabalhoED2SGBD().executarQuery(comandoSelect.toString());
        }
        catch(Exception ex){
            throw ex;
        }

        String[] partesSet = set.split(",");
        List<Atualizacao> listaAtualizacao = new ArrayList<Atualizacao>();
        
        try
        {
        
            for (String parte : partesSet) {

                Atualizacao atualizacao = new Atualizacao();
                String[] partidosPorIgual = parte.split("=");
                atualizacao.setAtributo(new ConteudoArquivos().getAtributoByName(new ConteudoArquivos().getTabelaByName(tabela.trim()).idTabela, partidosPorIgual[0].trim()));
                atualizacao.setValorNovo(partidosPorIgual[1].trim());

                listaAtualizacao.add(atualizacao);
            }
            
        }
        catch(Exception ex)
        {
            throw ex;
        }
            
        try
        {
            
            Integer codigoAAlterar = null;
            
            int tamanhoRegistro = new ConteudoArquivos().getTamanhoRegistro(tabela.trim());
            int ct = 0;
            for (Linha linha : retornoGrid.getGrid().linhas) {
            
                codigoAAlterar = (Integer)linha.registros.get(0).registro;
                
                try
                {
                    
                    outHash = Arquivo.abrirArquivo(tabela.trim()+"_hash.dat", "rw");
                    out = Arquivo.abrirArquivo(tabela.trim()+".dat", "rw");

                    //leitura hash
                    outHash.seek(CompartimentoHash.tamanhoCompartimento * Funcao.hash(codigoAAlterar));
                    long valorCompartimento = outHash.readLong();
                    //fim leitura hash

                    long posicaoAtual = valorCompartimento;

                    out.seek(valorCompartimento * tamanhoRegistro);

                    int codigo = out.readInt(); 
                    boolean removido = out.readBoolean();
                    long prox = out.readLong();
                
               
                    while (true)
                    {
                        if (codigo == codigoAAlterar) {
                            out.seek(posicaoAtual * tamanhoRegistro);
                           
                            atualizarDados(out, retornoGrid.getGrid().listaAtributos, listaAtualizacao, (posicaoAtual * tamanhoRegistro));
                           
                            break;
                        }
                        posicaoAtual = prox;
                        out.seek(prox * tamanhoRegistro);
                        codigo = out.readInt(); 
                        out.readBoolean();
                        prox = out.readLong();
                    }


                }
                finally
                {
                    if (out != null) {
                        out.close();
                    }
                    if (outHash != null) {
                        outHash.close();
                    }
                }
                ct++;
            }
            System.out.println("Query Ok. "+ct+" linha(s) afetada(s).");
        
        }
        catch(Exception ex)
        {
            throw ex;
        }
        
        
        
    }

    public void removerDados() throws Exception {//ok
        
        Scanner scan = new Scanner(System.in);
        System.out.println("Digite a query de remoção: ");
        System.out.print("> ");
        String comando = scan.nextLine();
        comando = comando.toLowerCase();
        if (comando.trim().toLowerCase().equals("quit")) {
           trabalhoed2.sgbd.TrabalhoED2SGBD.manipularDados();
           return;
        }
                    
        StringBuilder comandoSelect = new StringBuilder();
        
        
        String tabela = null;
        String where = null;
        
        
        Matcher mFromW = Pattern.compile("from(.*?)where").matcher(comando);
        Matcher mFrom = Pattern.compile("from[^\\n]*").matcher(comando);
        Matcher mWhere = Pattern.compile("where[^\\n]*").matcher(comando);
        
        
        if (mFromW.find()) {
            tabela = mFromW.group(1);
        }else if (mFrom.find()) {
            tabela = mFrom.group(0).replace("from", "");
        }
        
        if (mWhere.find()) {
            where = mWhere.group(0).replace("where", "");
        }
        
        
        comandoSelect.append("select * from ");
        comandoSelect.append(tabela.trim());
        
        if (where != null) 
        {
            comandoSelect.append(" where "+ where.trim());
        }
        
        
        RandomAccessFile outHash = null;
        RandomAccessFile out = null;
        RetornoGrid retornoGrid = null;
        try
        {
             retornoGrid = new trabalhoed2.sgbd.TrabalhoED2SGBD().executarQuery(comandoSelect.toString());
        }
        catch(Exception ex){
            throw ex;
        }
        

        try
        {

            
            Integer codigoARemover = null;
            
            int tamanhoRegistro = new ConteudoArquivos().getTamanhoRegistro(tabela.trim());
            int ct = 0;
            for (Linha linha : retornoGrid.getGrid().linhas) {
            
                codigoARemover = (Integer)linha.registros.get(0).registro;
                
                try
                {
                    
                    outHash = Arquivo.abrirArquivo(tabela.trim()+"_hash.dat", "rw");
                    out = Arquivo.abrirArquivo(tabela.trim()+".dat", "rw");

                    //leitura hash
                    outHash.seek(CompartimentoHash.tamanhoCompartimento * Funcao.hash(codigoARemover));
                    long valorCompartimento = outHash.readLong();
                    //fim leitura hash

                    long posicaoAtual = valorCompartimento;

                    out.seek(valorCompartimento * tamanhoRegistro);

                    int codigo = out.readInt(); 
                    boolean removido = out.readBoolean();
                    long prox = out.readLong();
                
               
                    while (true)
                    {
                        if (codigo == codigoARemover) {
                            out.seek(posicaoAtual * tamanhoRegistro);
                            out.readInt();
                            out.writeBoolean(true);
                            break;
                        }
                        posicaoAtual = prox;
                        out.seek(prox * tamanhoRegistro);
                        codigo = out.readInt(); 
                        removido = out.readBoolean();
                        prox = out.readLong();
                    }


                }
                finally
                {
                    if (out != null) {
                        out.close();
                    }
                    if (outHash != null) {
                        outHash.close();
                    }
                }
                ct++;
            }
            
            System.out.println("Query Ok. "+ct+" linha(s) afetada(s).");
            
        
        }
        catch(Exception ex)
        {
            throw  ex;
        }
        
    }

    
    private void atualizarDados(RandomAccessFile out, List<Atributo> listaAtributos, List<Atualizacao> listaAtualizacao, long valorSeek) throws IOException, Exception {
        
        try
        {
            for (Atualizacao atualizacao : listaAtualizacao) {

                for (Atributo attr : listaAtributos) { 

                    switch (attr.tipoAtributo)
                    {
                        case 1:
                            if (attr.nomeAtributo.trim().equalsIgnoreCase(atualizacao.getAtributo().nomeAtributo.trim())) {
                                String valorInt = atualizacao.getValorNovo().toString();
                                out.writeInt(Integer.parseInt(valorInt.trim()));
                                break;
                            }
                            out.readInt();
                            break;
                        case 2:
                            if (attr.nomeAtributo.trim().equalsIgnoreCase(atualizacao.getAtributo().nomeAtributo.trim())) {
                                out.writeFloat((Float)atualizacao.getValorNovo());
                                break;
                            }
                            out.readFloat();
                            break;
                        case 3:
                            if (attr.nomeAtributo.trim().equalsIgnoreCase(atualizacao.getAtributo().nomeAtributo.trim())) {
                                String antiBug = atualizacao.getValorNovo().toString();
                                out.writeUTF(Funcao.padRight(antiBug));
                                break;
                            }
                            out.readUTF();
                            break;
                        case 4:
                            if (attr.nomeAtributo.trim().equalsIgnoreCase(atualizacao.getAtributo().nomeAtributo.trim())) {
                                String strChar = atualizacao.getValorNovo().toString();
                                out.writeChar(strChar.charAt(0));
                                break;
                            }
                            out.readChar();
                            break;
                        case 5:
                            if (attr.nomeAtributo.trim().equalsIgnoreCase(atualizacao.getAtributo().nomeAtributo.trim())) {
                                out.writeDouble((Double)atualizacao.getValorNovo());
                                break;
                            }
                            out.readDouble();
                            break;
                        case 6:
                            if (attr.nomeAtributo.trim().equalsIgnoreCase(atualizacao.getAtributo().nomeAtributo.trim())) {
                                out.writeBoolean((Boolean)atualizacao.getValorNovo());
                                break;
                            }
                            out.readBoolean();
                            break;
                        case 7:
                            if (attr.nomeAtributo.trim().equalsIgnoreCase(atualizacao.getAtributo().nomeAtributo.trim())) {
                                out.writeLong((Long)atualizacao.getValorNovo());
                                break;
                            }
                            out.readLong();
                            break;
                    }
                }
            
                out.seek(valorSeek);
            }
        }
        catch(Exception ex){
            throw new Exception("Erro zuado do atualizarDados");
        }
        finally{
            if (out!=null) {
                out.close();
            }
        }
    }

    private void EscreverDados(List<Registro> listaRegistros, RandomAccessFile out, boolean alteraProx) throws IOException {
        
        for (Registro reg : listaRegistros) { 
            
            switch (reg.atributo.tipoAtributo)
            {
                case 1:
                    out.writeInt((int)reg.registro);
                    break;
                case 2:
                    out.writeFloat((float)reg.registro);
                    break;
                case 3:
                    out.writeUTF(Funcao.padRight((String)reg.registro));
                    break;
                case 4:
                    out.writeChar((Character)reg.registro);
                    break;
                case 5:
                    out.writeDouble((double)reg.registro);
                    break;
                case 6:
                    out.writeBoolean((boolean)reg.registro);
                    break;
                case 7:
                    if (alteraProx) {
                        out.writeLong(-1);
                    }
                    else
                    {
                        out.readLong();//ignorar
                    }
                    break;
            }
        }
    }
    
    public void inserirDadosEntradaProfessora(List<Registro> listaRegistros){
        try 
        {

            inserirDadosEspecOps(listaRegistros, 6);
            System.out.println("\nRegistro inserido com sucesso.\n");
        }
        catch(Exception ex){
            System.out.println("zica ao inserir = " + ex);
        }
     }

    
}
