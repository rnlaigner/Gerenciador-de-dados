/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.entidades;

import static trabalhoed2.sgbd.entidades.Registro.setRegistro;
import trabalhoed2.sgbd.util.Configuracao;

/**
 *
 *
 */
public class Atualizacao {
    
    private Atributo atributo;
    private Object valorNovo;

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }

    public Object getValorNovo() {
        return valorNovo;
    }

    public void setValorNovo(String valorNovo) throws Exception {
        
        switch (atributo.tipoAtributo) {
             case 1:
             
                int valorInt = 0;
                try
                {
                    if (atributo.nomeAtributo.trim().toLowerCase().equals("codigo")) {
                        throw new Exception();
                    }
                    valorInt = Integer.parseInt(valorNovo);
                    this.valorNovo = valorInt;
                }
                catch(Exception ex)
                {
                    if (atributo.nomeAtributo.trim().toLowerCase().equals("codigo")) {
                        System.out.print("Não se pode alterar o código. ");
                    }
                    else
                    {
                        System.out.print("Digite um valor inteiro válido para o atributo "+atributo.nomeAtributo.trim()+". ");
                    }
                    throw new Exception("Digite um valor inteiro válido");
                }
                break;
             case 2:
                    float valorFloat = 0f;
                    try
                    {
                         valorFloat = Float.parseFloat(valorNovo);
                         this.valorNovo = valorFloat;
                         
                    }
                    catch(Exception ex)
                    {
                        System.out.print("Digite um valor float válido para o atributo "+atributo.nomeAtributo.trim()+". ");
                        throw new Exception("Digite um valor float válido");
                    }
                    
                 break;
             case 3:
                    
                    String valorString = null;
                    try
                    {
                         valorString = valorNovo;
                         if (valorString.length() > Configuracao.tamanhoString-2) {
                            throw new Exception();
                         }
                         
                         this.valorNovo = valorString;
                    }
                    catch(Exception ex)
                    {
                        System.out.print("Digite um valor string válido para o atributo "+atributo.nomeAtributo.trim()+". ");
                        throw new Exception("Digite um valor string válido");
                    }
                    
                    
                 break;
             case 4:
                    Character valorChar = null;
                    try
                    {
                         valorChar = valorNovo.charAt(0);
                         this.valorNovo = valorChar;
                    }
                    catch(Exception ex)
                    {
                        System.out.print("Digite um valor char válido para o atributo "+atributo.nomeAtributo.trim()+". ");
                        throw new Exception("Digite um valor char válido");
                    }
                 break;
             case 5:
                    Double valorDouble = null;
                    try
                    {
                         valorDouble = Double.parseDouble(valorNovo);
                         this.valorNovo = valorDouble;
                    }
                    catch(Exception ex)
                    {
                        System.out.print("Digite um valor Double válido para o atributo "+atributo.nomeAtributo.trim()+". ");
                        throw new Exception("Digite um valor Double válido");
                    }
                 break;
             case 6:
                    Boolean valorBoolean = null;
                    try
                    {
                        if (atributo.nomeAtributo.trim().toLowerCase().equals("removido")) {
                            throw new Exception();
                        }
                        
                        if (valorNovo.trim().toLowerCase().equals("true")) {
                            valorBoolean = true;
                        }
                        else if (valorNovo.trim().toLowerCase().equals("false")) {
                            valorBoolean = false;
                        }
                        else
                        {
                            throw new Exception();
                        }
                         this.valorNovo = valorBoolean;
                    }
                    catch(Exception ex)
                    {
                        if (atributo.nomeAtributo.trim().toLowerCase().equals("removido")) {
                            System.out.print("Não se pode alterar atributos internos do sistema. ");
                        }
                        else
                        {
                            System.out.print("Digite um valor Boolean válido para o atributo "+atributo.nomeAtributo.trim()+". ");
                        }
                        throw new Exception("Digite um valor Boolean válido");
                    }
                 break;
             case 7:
                    if (atributo.nomeAtributo.trim().toLowerCase().equals("prox")) {
                        System.out.print("Não se pode alterar atributos internos do sistema. ");
                        throw new Exception();
                    }
                 break;

         }
    }
    
    
    
}
