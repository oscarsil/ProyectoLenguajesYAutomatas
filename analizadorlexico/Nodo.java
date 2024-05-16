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
public class Nodo {

    String lexema;
    int token;
    int renglon;
    Nodo sig = null;

    Nodo(String lexema, int token, int renglon) {
        this.lexema = lexema;
        this.token = token;
        this.renglon = renglon;
    }

}
