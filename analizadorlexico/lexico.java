/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadorlexico;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author oscarsilvaleon
 */
public class lexico {

    Nodo cabeza = null, p;
    int estado = 0, columna, valorMT, numRenglon = 1;
    int caracter =0;
    boolean errorEncontrado = false;
    String lexema = "";

    String archivo = "/Users/oscarsilvaleon/NetBeansProjects/AnalizadorLexico/src/analizadorlexico/codigo2.txt";

    int matriz[][] = {
        //     D    L   +   -   *   =    <    >    :    ;    ,    .    (    )    "   OC   EB   NL  TAB  EOL  EOF
        //     0    1   2   3   4   5    6    7    8    9   10   11   12   13   14   15   16   17   18   19   20
        /*0 */ {2  ,1  ,103,104,105,106, 5, 6, 7, 115, 114, 8, 10, 119, 9, 503, 0, 0, 0, 0, 0},
        /*1 */ {1  ,1  ,100,100,100,100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
        /*2 */ {2  ,101,101,101,101,101, 101, 101, 101, 101, 101, 3, 101, 101, 101, 101, 101, 101, 101, 101, 101},
        /*3 */ {4  ,500,500,500,500,500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 502, 501},
        /*4 */ {4  ,102,102,102,102,102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 103, 102, 102, 102, 102, 102},
        /*5 */ {108,108,108,108,108,109, 108, 107, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108},
        /*6 */ {111,111,111,111,111,110, 111, 111, 111, 111, 111, 111, 111, 111, 111, 111, 111, 111, 111, 111, 111},
        /*7 */ {116,116,116,116,116,112, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116},
        /*8 */ {113,113,113,113,113,113, 113,113, 113, 113, 113, 117, 113, 113, 113, 113, 113, 113, 113, 113, 113},
        /*9 */ {9  ,9  ,9  ,9  ,9  ,9  , 9, 9, 9, 9, 9, 9, 9, 9, 120, 9, 9, 502, 9, 502, 501},
        /*10*/ {118,118,118,118,11 ,118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118},
        /*11*/ {11 ,11 ,11 ,11 ,12 ,11 , 11, 11, 11, 11, 11, 11, 11, 11, 11, 501, 11, 501, 11, 502, 501},
        /*12*/ {11 ,11 ,11 ,11 ,11 ,11 , 11, 11, 11, 11, 11, 11, 11, 0, 11, 11, 11, 11, 11, 11, 11}
    };
    String palabrasreservadas[][] = {
        // palabra / token
        {"div", "200"},
        {"or", "201"},
        {"and", "202"},
        {"not", "203"},
        {"if", "204"},
        {"then", "205"},
        {"else", "206"},
        {" of", "207"},
        {"while", "208"},
        {"do", "209"},
        {"begin", "210"},
        {"end", "211"},
        {"read", "212"},
        {"write", "213"},
        {"var", "214"},
        {"program", "215"},
        {"integer", "216"},
        {"Real", "217"},
        {"String", "218"},
        {"true", "219"},
        {"false", "220"}
    };

    String Errores[][] = {
        {"se espera digito", "500"},
        {"end od file inesperado", "501"},
        {"end of line inesperado", "502"},
        {"simbolo no valido", "503"}
    };

    private void imprimirNodos() {
        p = cabeza;
        while (p != null) {
            System.out.println(p.lexema + " " + p.token );
            p = p.sig;
        }
    }

    private void validarPalabraReservada() {
        for (String[] palabrasreservada : palabrasreservadas) {
            if (lexema.equals(palabrasreservada[0])) {
                valorMT = Integer.valueOf(palabrasreservada[1]);
            }
        }
    }

    private void insertarNodo() {
        Nodo nodo = new Nodo(lexema, valorMT, numRenglon);
        if (cabeza == null) {
            cabeza = nodo;
            p = cabeza;
        } else {
          p.sig = nodo;
          p = nodo;  
        }
        
    }

    private void imprimirMensajeError() {
        if (caracter != -1 && valorMT >= 500) {
            for (String[] Errore : Errores) {
                if (valorMT == Integer.valueOf(Errore[1])) {
                    System.out.println("El error encontrado es "
                            + "" + Errore[0] + " renlgon " + numRenglon);
                }
            }
            errorEncontrado = true;
        }
    }
    

    public lexico() throws FileNotFoundException, IOException{
        RandomAccessFile file = new RandomAccessFile(archivo,"r");
        try {          
            while (caracter != -1) {
                caracter =  file.read();
                if (Character.isLetter((char)caracter)) {
                    columna = 1;
                } else if (Character.isDigit((char)caracter)) {
                    columna = 0;
                } else {
                    switch ((char)caracter) {
                        case '+':
                            columna = 2;
                            break;
                        case '-':
                            columna = 3;
                            break;
                        case '*':
                            columna = 4;
                            break;
                        case '=':
                            columna = 5;
                            break;
                        case '<':
                            columna = 6;
                            break;
                        case '>':
                            columna = 7;
                            break;
                        case ':':
                            columna = 8;
                            break;
                        case ';':
                            columna = 9;
                            break;
                        case ',':
                            columna = 10;
                            break;
                        case '.':
                            columna = 11;
                            break;
                        case '(':
                            columna = 12;
                            break;
                        case ')':
                            columna = 13;
                            break;
                        case 39:
                            columna = 14;
                            break;
                        case 32: //espacio blanco
                            columna = 16;
                            break;
                        case 10: //nueva linea
                        {
                            columna = 17;
                            numRenglon = numRenglon + 1;
                        }
                            break;
                        case 9: //tab
                            columna = 18;
                            break;
                        case 13: // regreso carreo
                            columna = 19;
                            break;
                        default:
                            columna = 15;
                            break;
                    }
                }

                valorMT = matriz[estado][columna];
                
                if (valorMT < 100) {
                    estado = valorMT;
                    if (estado == 0) {
                        lexema = "";
                    } else {
                        lexema = lexema + (char)caracter;
                    }
                } else if (valorMT >= 100 && valorMT < 500) 
                {
                    if (valorMT == 100) {
                        validarPalabraReservada();
                    }
                    if (valorMT == 100 || valorMT == 101 || valorMT == 102 || valorMT == 111 || valorMT == 108 || valorMT == 116  || valorMT == 118 || valorMT >= 200) 
                    {
                        file.seek(file.getFilePointer() - 1);
                    } else {
                        lexema = lexema + (char)caracter;
                    }
                    insertarNodo();
                    estado = 0;
                    lexema = "";
                } else {
                    imprimirMensajeError();
                }
                
            }
            
        // imprimirNodos();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
