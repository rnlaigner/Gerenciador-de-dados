/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.facade;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Scanner;
import trabalhoed2.sgbd.entidades.Atributo;
import trabalhoed2.sgbd.entidades.CompartimentoHash;
import trabalhoed2.sgbd.util.Arquivo;
import trabalhoed2.sgbd.util.ConteudoArquivos;
import trabalhoed2.sgbd.util.Erro;
import trabalhoed2.sgbd.util.Funcao;

/**
 *
 * @author pedro.gerico
 */
public class ManipulacaoFacade {
    
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
                    valor = scan.next();
                    out.writeUTF((String)valor);
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
}
