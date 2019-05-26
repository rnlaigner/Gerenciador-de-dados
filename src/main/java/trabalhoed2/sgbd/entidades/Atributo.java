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
 
 */
public class Atributo {
    
    public int idTabela;
    public String nomeAtributo;
    public int tipoAtributo;
    public int tamanho;
    public static String nomeArquivo = "atributo.dat";
    public static int tamanhoRegistro = Configuracao.tamanhoInteger + 
                                        Configuracao.tamanhoString + 
                                        Configuracao.tamanhoInteger + 
                                        Configuracao.tamanhoInteger;

    public Atributo(int idTabela, String nomeAtributo, int tipoAtributo, Integer tamanho) {
        this.idTabela = idTabela;
        this.nomeAtributo = nomeAtributo;
        this.tipoAtributo = tipoAtributo;
        this.tamanho = tamanho;
    }
            
    public void salva(RandomAccessFile out) throws IOException {
        out.writeInt(idTabela);
        out.writeUTF(nomeAtributo);
        out.writeInt(tipoAtributo);
        out.writeInt(tamanho);
    }
    
    public static Atributo le(RandomAccessFile in) throws IOException {
        return new Atributo(in.readInt(), in.readUTF(), in.readInt(), in.readInt());
    }
    
    public static String factoryNomeAtributo(int tipoAtributo) {
        
        switch (tipoAtributo)
        {
            case 1:
                return Funcao.padRight("Integer");
            case 2:
                return Funcao.padRight("Float");
            case 3:
                return Funcao.padRight("String");
            case 4:
                return Funcao.padRight("Char");
            case 5:
                return Funcao.padRight("Double");
            case 6:
                return Funcao.padRight("Boolean");
              
        }
        return null;
    }  
    
    public static int factoryTamanhoAtributo(int tipoAtributo) {
        
        switch (tipoAtributo)
        {
            case 1:
                return Configuracao.tamanhoInteger;
            case 2:
                return Configuracao.tamanhoFloat;
            case 3:
                return Configuracao.tamanhoString;
            case 4:
                return Configuracao.tamanhoChar;
            case 5:
                return Configuracao.tamanhoDouble;
            case 6:
                return Configuracao.tamanhoBoolean;
            case 7:
                return Configuracao.tamanhoLong;
              
        }
        return 0;
    }     

    @Override
    public String toString() {
        return "Atributo{" + "idTabela=" + idTabela + ", nomeAtributo=" + nomeAtributo + ", tipoAtributo=" + tipoAtributo + ", tamanho=" + tamanho + '}';
    }
    
    
    
    
}
