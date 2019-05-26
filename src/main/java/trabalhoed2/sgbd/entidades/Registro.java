/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.entidades;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import trabalhoed2.sgbd.util.Arquivo;
import trabalhoed2.sgbd.util.Configuracao;
import trabalhoed2.sgbd.util.Erro;

/**
 *
 *
 */
public class Registro <T>{

    public Atributo atributo;
    public T registro;

    public void salvarRegistro(Atributo atributo, T dado) {
        this.atributo = atributo;
        this.registro = dado;
    }

    public Registro() {
        
    }
        
    public static Registro getRegistro(Atributo atributo, RandomAccessFile rafTabelas) throws IOException {
        
        Registro registro = null;
        
        switch (atributo.tipoAtributo) {
             case 1:
                    registro = new Registro<Integer>();
                    registro.salvarRegistro(atributo, rafTabelas.readInt());
                 break;
             case 2:
                    registro = new Registro<Float>();
                    registro.salvarRegistro(atributo, rafTabelas.readFloat());
                 break;
             case 3:
                    registro = new Registro<String>();
                    registro.salvarRegistro(atributo, rafTabelas.readUTF().trim());
                 break;
             case 4:
                    registro = new Registro<Character>();
                    registro.salvarRegistro(atributo, rafTabelas.readChar());
                 break;
             case 5:
                    registro = new Registro<Double>();
                    registro.salvarRegistro(atributo, rafTabelas.readDouble());
                 break;
             case 6:
                    registro = new Registro<Boolean>();
                    registro.salvarRegistro(atributo, rafTabelas.readBoolean());
                 break;
             case 7:
                    registro = new Registro<Long>();
                    registro.salvarRegistro(atributo, rafTabelas.readLong());
                 break;

         }
        
        return registro;
    }
    
    
    public static Registro setRegistro(Atributo atributo) {
        Scanner scan = new Scanner(System.in);
        Registro registro = null;
        
        switch (atributo.tipoAtributo) {
             case 1:
                    registro = new Registro<Integer>();
                    
                    int valorInt = 0;
                    try
                    {
                         valorInt = scan.nextInt();
                         if (atributo.nomeAtributo.trim().toLowerCase().equals("codigo")) {
                             if (valorInt <= 0) {
                                 throw new Exception ("Código precisa ser > 0");
                             }
                         }
                         registro.salvarRegistro(atributo, valorInt);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("Digite um valor inteiro válido: ");
                        registro = setRegistro(atributo);
                    }
                    
                    
                 break;
             case 2:
                    registro = new Registro<Float>();
                    float valorFloat = 0f;
                    try
                    {
                         valorFloat = scan.nextFloat();
                         registro.salvarRegistro(atributo, valorFloat);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("Digite um valor float válido: ");
                        registro = setRegistro(atributo);
                    }
                    
                 break;
             case 3:
                    registro = new Registro<String>();
                    String valor = scan.nextLine();

                    while (valor.length() > (Configuracao.tamanhoString-2)){
                        System.out.println("Digite um texto menor de " +(Configuracao.tamanhoString-2)+" caracteres: ");
                        valor = scan.nextLine();
                    }
                    registro.salvarRegistro(atributo, valor);
                    
                 break;
             case 4:
                    registro = new Registro<Character>();
                    registro.salvarRegistro(atributo, scan.next().charAt(0));
                 break;
             case 5:
                    registro = new Registro<Double>();
                    double valorDouble = 0;
                    try
                    {
                         valorDouble = scan.nextDouble();
                         registro.salvarRegistro(atributo, valorDouble);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("Digite um valor double válido: ");
                        registro = setRegistro(atributo);
                    }
                    
                 break;
             case 6:
                    registro = new Registro<Boolean>();
                    boolean valorBool = false;
                    
                    if (atributo.nomeAtributo.trim().equalsIgnoreCase("removido")) {
                        registro.salvarRegistro(atributo, false);
                        break;
                    }
                    try
                    {
                         valorBool = scan.nextBoolean();
                         registro.salvarRegistro(atributo, valorBool);
                    }
                    catch(Exception ex)
                    {
                        System.out.println("Digite um valor booleano válido: ");
                        registro = setRegistro(atributo);
                    }
                 break;
             case 7:
                    registro = new Registro<Long>();
                    registro.salvarRegistro(atributo, scan.nextLong());
                 break;

         }
        
        return registro;
    }
    
    public Registro setProx(Atributo atributo){
        Registro registro = new Registro();
        registro.salvarRegistro(atributo, -2);
        return registro;
    }
    
    public void inserirRegistroTabela(Catalogo tabela) throws IOException
    {
        List<Atributo> listaAtributos = getAtributosById(tabela.idTabela);
        
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
}
