/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadorlexico;

import java.io.IOException;

/**
 *
 * @author oscarsilvaleon
 */
public class AnalizadorLexico {

    
    Nodo p;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{

       lexico lexico = new lexico();
       if(!lexico.errorEncontrado){
           System.out.println("Analasis lexico Terminado");
           Sintactico Sintactico = new Sintactico(lexico.cabeza);
           if(!Sintactico.Error){
               Traduccion Traduccion = new Traduccion(Sintactico.com,Sintactico.head,Sintactico.nomprog);
           }
       }
       
        // TODO code application logic here
    }

}
