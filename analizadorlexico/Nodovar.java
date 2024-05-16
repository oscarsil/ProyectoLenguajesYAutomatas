/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadorlexico;

/**
 *
 * @author oscarsilvaleon
 */
public class Nodovar {
    
    String lexema;
    String tipo;
    Nodovar sig = null;
    
    Nodovar(String lexema, String tipo){
        this.lexema = lexema;
        this.tipo = tipo;
    }
    
}
