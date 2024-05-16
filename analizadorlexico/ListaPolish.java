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
public class ListaPolish {
    int token;
    ListaPolish sig = null;
    ListaPolish (int token){
        this.token= token;
    }
}
