/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.entidades;

import java.io.IOException;
import java.io.RandomAccessFile;
import trabalhoed2.sgbd.util.Configuracao;

/**
 *
 *
 */
public class CompartimentoHash {
    public long prox;
    public static long tamanhoCompartimento = Configuracao.tamanhoLong;

    
    public CompartimentoHash(long prox) {
        this.prox = prox;
    }
    public void salva(RandomAccessFile out) throws IOException {
        out.writeLong(prox);
    }
    
    public static CompartimentoHash le(RandomAccessFile in) throws IOException {
        return new CompartimentoHash(in.readLong());
    }

}
