/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trabalhoed2.sgbd.entidades;

import trabalhoed2.sgbd.util.Configuracao;
import java.io.IOException;
import java.io.RandomAccessFile;
import trabalhoed2.sgbd.util.Funcao;

/**
 *
 *
 */
public class Catalogo {
    
    public Integer idTabela;
    public String nomeTabela;
    public long qtRegistro;
    public boolean removido;
    public static int tamanhoRegistro = Configuracao.tamanhoInteger + Configuracao.tamanhoString + Configuracao.tamanhoLong + Configuracao.tamanhoBoolean;
    public static String nomeArquivo = "catalogo.dat";
    
    
    public Catalogo() {
    }

    public Catalogo(Integer id, String nome, boolean removido, long qtRegistro) {
        
        this.idTabela = id;
        this.nomeTabela = nome;
        this.removido = removido;        
        this.qtRegistro = qtRegistro;
        
    }
        
    public void salva(RandomAccessFile out) throws IOException {
        out.writeInt(idTabela);
        out.writeUTF(Funcao.padRight(nomeTabela));
        out.writeBoolean(removido);
        out.writeLong(qtRegistro);

    }
    
    public static Catalogo le(RandomAccessFile in) throws IOException {
        return new Catalogo(in.readInt(), in.readUTF(), in.readBoolean(), in.readLong());
    }

    @Override
    public String toString() {
        return "Catalogo{" + "idTabela=" + idTabela + ", nomeTabela=" + nomeTabela + ", qtRegistro=" + qtRegistro + ", removido=" + removido + '}';
    }

    



    
}


