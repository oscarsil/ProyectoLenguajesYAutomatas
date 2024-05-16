/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizadorlexico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.io.FileWriter;   
import java.io.IOException;  
/**
 *
 * @author oscarsilvaleon
 */
public class Traduccion {

    nodoCI nodos;
    List<String> listaIntegers = new ArrayList<>();
    List<String> listaStrings = new ArrayList<>();
    List<String> listaReal = new ArrayList<>();
    List<Integer> variableswrite = new ArrayList<>();
    String progE = "";
    String principio = "INCLUDE macros.mac" + "\n" + "INCLUDE fp.a." + "\n"
            + "INLCUDELIB stadlib.lib" + "\n" + ".model Small" + "\n" + "\n" + ".stack"
            + "\n" + "\n" + ".data" + "\n";

    public Traduccion(nodoCI lp, Nodovar lv, String nomprog) {
        separarVariables(lv);
        code(lp);

        insertarvar();
        concatenar();
        System.out.println(progE);
        try {
            FileWriter myWriter = new FileWriter("/Users/oscarsilvaleon/"
                    + "NetBeansProjects/AnalizadorLexico/src/"
                    + "analizadorlexico/"+nomprog+".asm", false);            
            myWriter.write(progE);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void concatenar() {
        progE = principio + Data + startcode + code + endcode;
    }

    private void code(nodoCI lp) {
        nodos = lp;
        while (nodos != null) {
            if (nodos.esEtiqueta == false) {
                while (nodos.esEtiqueta == false && nodos != null) {
                    if (Arrays.asList(operadorescomparativos).contains(nodos.contenido)
                            || Arrays.asList(operadoresnumericos).contains(nodos.contenido)
                            || nodos.contenido.equals(":=")
                            || nodos.contenido.equals("read")
                            || nodos.contenido.equals("write")
                            || nodos.contenido.contains("BRF")) {
                        evaluar(nodos.contenido);
                    } else {
                        pila.push(nodos.contenido);
                    }
                    nodos = nodos.sig;
                }
            } else {
                if (nodos.contenido.substring(0, 2).equals("BR")) {
                    insertarbrincoincondicional(nodos.contenido);
                } else {
                    insertaretiquetas(nodos.contenido);
                }
                nodos = nodos.sig;
            }
        }
    }
    String operadorescomparativos[] = {
        "<", ">,", ">=", "<=", "=", "<>"
    };
    String operadoresnumericos[] = {"*", "div", "+", "-"};

    String code = "";

    private void insertaretiquetas(String ne) {
        code = code + ne + ":" + "\n";
    }
    Stack<String> pila = new Stack();

    private void insertarbrincosFalsos(String etiqueta) {
        code = code + "\t" + "JNE " + etiqueta + "\n";
    }

    private void insertarbrincoincondicional(String et) {
        code = code + "\t" + "JMP " + et + "\n";
    }

    private void evaluar(String operador) {
        if (operador.contains("BRF")) {
            insertarbrincosFalsos(operador);
            return;
        }
        if (operador.equals("@") || operador.equals("write") || operador.equals("read")) {
            uni(operador);
        } else {
            bin(operador);
        }
    }
    int CVTI = 0;
    int CVTS = 0;
    int CVTR = 0;

    private void bin(String op) {
        String op1, op2;
        op2 = pila.pop();
        op1 = pila.pop();
        if (Arrays.asList(operadoresnumericos).contains(op)) {
            insertarnumerico(op, op2, op1);
        } else if (Arrays.asList(operadorescomparativos).contains(op)) {
            insertarcomparativo(op, op2, op1);
        } else {
            insertarAsignacion(op2, op1);
        }
    }

    private void insertarcomparativo(String op, String op2, String op1) {
        CVTI++;
        switch (op) {
            case "<":
                code = code +"\t" + "I_MENOR " + op1 + "," + op2 + "," + "CVTI" + CVTI + "\n"
                        + "\t" + "CMP " + "CVTI" + CVTI + "," + 1 + "\n";
                break;
            case ">":
                code = code + "I_MAYOR " + op1 + "," + op2 + "," + "CVTI" + CVTI + "\n"
                        + "\t" + "CMP " + "CVTI" + CVTI + "," + 1 + "\n";
                break;
            case "=":
                code = code + "I_IGUAL " + op1 + "," + op2 + "," + "CVTI" + CVTI + "\n"
                        + "\t" + "CMP " + "CVTI" + CVTI + "," + 1 + "\n";
                break;
            case ">=":
                code = code + "I_MENORIGUAL " + op1 + "," + op2 + "," + "CVTI" + CVTI + "\n"
                        + "\t" + "CMP " + "CVTI" + CVTI + "," + 1 + "\n";
                break;
            case "<=":
                code = code + "I_MAYORIGUAL " + op1 + "," + op2 + "," + "CVTI" + CVTI + "\n"
                        + "\t" + "CMP " + "CVTI" + CVTI + "," + 1 + "\n";
                break;
            case "<>":
                code = code + "I_DIFERENTES " + op1 + "," + op2 + "," + "CVTI" + CVTI + "\n"
                        + "\t" + "CMP " + "CVTI" + CVTI + "," + 1 + "\n";
                break;
        }
        pila.push("CVTI" + CVTI);
    }

    private int getres(String op2, String op1) {
        if (isreal(op1)) {
            return 2;
        } else if (isreal(op2)) {
            return 2;
        } else {
            return 1;
        }

    }

    private void insertarnumerico(String op, String op2, String op1) {
        int res = 0;
        switch (op) {
            case "div":
                res = 2;
                CVTR++;
                code = code + "\t" + "F_divide " + op1 + "," + op2 + "," + "CVTR" + CVTR + "\n";
                pila.push("CVTR" + CVTR);
                break;
            case "*":
                res = getres(op2, op1);
                if (res == 1) {
                    CVTI++;
                    code = code + "\t" + "I_MULTI " + op1 + "," + op2 + "," + "CVTI" + CVTI + "\n";
                    pila.push("CVTI" + CVTI);
                }
                if (res == 2) {
                    CVTR++;
                    code = code + "\t" + "F_MULTI " + op1 + "," + op2 + "," + "CVTR" + CVTR + "\n";
                    pila.push("CVTR" + CVTR);
                }
                break;
            case "-":
                res = getres(op2, op1);
                if (res == 1) {
                    CVTI++;
                    code = code + "\t" + "I_Resta " + op1 + "," + op2 + "," + "CVTI" + CVTI + "\n";
                    pila.push("CVTI" + CVTI);
                }
                if (res == 2) {
                    CVTR++;
                    code = code + "\t" + "F_Resta " + op1 + "," + op2 + "," + "CVTR" + CVTR + "\n";
                    pila.push("CVTI" + CVTR);
                }
                break;
            case "+":
                res = getres(op2, op1);
                if (res == 1) {
                    CVTI++;
                    code = code + "\t" + "I_SUMAR " + op1 + "," + op2 + "," + "CVTI" + CVTI + "\n";
                    pila.push("CVTI" + CVTI);
                }
                if (res == 2) {
                    CVTR++;
                    code = code + "\t" + "F_SUMAR " + op1 + "," + op2 + "," + "CVTR" + CVTR + "\n";
                    pila.push("CVTI" + CVTI);
                }
                break;
        }
    }

    private void insertarAsignacion(String op2, String op1) {
        if (isint(op1)) {
            code = code + "\t" + "I_ASIGNAR " + op1 + "," + op2 + "\n";
        } else {
            code = code + "\t" + "S_ASIGNAR " + op1 + "," + op2 + "\n";
        }

    }

    private void uni(String operador) {
        String op1;
        switch (operador) {
            case "@":
                negativo(pila.pop());
                break;
            case "read":
                insertarread(pila.pop());
                break;
            default:
                insertarwrite(pila.pop());
                break;
        }
    }

    private void separarVariables(Nodovar lv) {
        while (lv != null) {
            switch (lv.tipo) {
                case "integer":
                    listaIntegers.add(lv.lexema);
                    break;
                case "String":
                    listaStrings.add(lv.lexema);
                    break;
                case "Real":
                    listaReal.add(lv.lexema);
                    break;
                default:
            }
            lv = lv.sig;
        }
    }

    private void insertarread(String op1) {
        code = code + "\t" + "READ" + "\n";
        for (String element : listaIntegers) {
            if (element.contains(op1)) {
                code = code + "\t" + "ASCTODEC " + op1 + ", MSG" + "\n";
            }
        }
        for (String element : listaStrings) {
            if (element.contains(op1)) {
                code = code + "\t" + "S_ASIGNAR " + op1 + ", MSG" + "\n";
            }
        }
    }

    private void insertarwrite(String variable) {
        CVTS++;
        variableswrite.add(CVTS);
        var = var + "\t" + "\t" + "CVTS" + CVTS + " DB " + variable + ", '$'\n";
        code = code + "\t" + "WRITE " + variable + "\n";
        pila.push("CVTS" + CVTS);
    }

    private void negativo(String op) {
        CVTI++;
        code = code + "\tSIGNOMENOS " + op + ", " + CVTI + "\n";
        pila.push("CVTI" + CVTI);
    }

    private void insertarvar() {
        if (listaIntegers.isEmpty() == false) {
            for (int i = 0; i < listaIntegers.size(); i++) {
                var = var + "\t" + "\t" + listaIntegers.get(i) + " DW" + " 0" + "\n";
            }
        }
        if (listaStrings.isEmpty() == false) {
            for (int i = 0; i < listaStrings.size(); i++) {
                var = var + "\t" + "\t" + listaStrings.get(i) + " DB" + " ?," + " '$'" + "\n";
            }
        }
        if (listaReal.isEmpty() == false) {
            for (int i = 0; i < listaReal.size(); i++) {
                var = var + "\t" + "\t" + listaReal.get(i) + " DD" + " 0" + "\n";
            }
        }
        if (CVTI != 0) {
            while (CVTI != 0) {
                var = var + "\t" + "\t" + "CVTI" + CVTI + " DW" + " 0" + "\n";
                CVTI--;
            }
        }
        if (CVTR != 0) {
            while (CVTR != 0) {
                var = var + "\t" + "\t" + "CVTR" + CVTR + " DD" + " 0" + "\n";
                CVTR--;
            }
        }
        if (CVTS != 0) {
            while (CVTS != 0) {
                if (!variableswrite.contains(CVTS)) {
                    var = var + "CVTS" + CVTS + " DB " + "?, '$'\n";
                }
                CVTS--;
            }
        }

        Data = Data + var;
    }
    String var = "";
    String Data = "		MAXLEN DB 254\n"
            + "		LEN DB 0\n"
            + "		MSG   DB 254 DUP(?)\n"
            + "		MSG_DD   DD MSG            \n"
            + "		BUFFER      DB 8 DUP('$')\n"
            + "		CADENA_NUM      DB 10 DUP('$')\n"
            + "		BUFFERTEMP  DB 8 DUP('$')            \n"
            + "		BLANCO  DB '#'\n"
            + "		BLANCOS DB '$'\n"
            + "		MENOS   DB '-$'\n"
            + "		COUNT   DW 0\n"
            + "		NEGATIVO    DB 0            \n"
            + "		BUF DW 10\n"
            + "		LISTAPAR    LABEL BYTE\n"
            + "		LONGMAX DB 254\n"
            + "		TRUE    DW 1\n"
            + "		FALSE DW 0            \n"
            + "		INTRODUCIDOS    DB 254 DUP ('$')\n"
            + "		MULT10  DW 1 \n"
            + "		s_true  DB 'true$'\n"
            + "		s_false DB 'false$'\n";
    String startcode = ".code\n"
            + "main proc\n"
            + "	mov ax, SEG @data\n"
            + "	mov ds, ax\n"
            + "";
    String endcode = "	.exit\n"
            + "	main endp\n"
            + "end main";

    private boolean isreal(String strNum) {
        for (String element : listaReal) {
            if (element.contains(strNum)) {
                return true;
            }
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private boolean isint(String strNum) {
        for (String element : listaIntegers) {
            if (element.contains(strNum)) {
                return true;
            }
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
