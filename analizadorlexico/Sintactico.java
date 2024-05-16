package analizadorlexico;


import java.util.ArrayList;

import java.util.List;
import java.util.Stack;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author oscarsilvaleon
 */
public class Sintactico {
    Nodovar head = null,a,b,d;
    Nodo p;
    boolean Error = false;
    String  mensaje = "";
    String nomprog;
    Stack<String> pilavar = new Stack();
    String tipovar = "";
    String var;
    Stack<Integer> pilaaux = new Stack();
    Stack<String> pilastrings = new Stack();
    ListaPolish start=null,l,lpp;
    nodoCI com =null,ncip,ncipp;
    int assigntoken;
    List<Integer> ListaTokens = new ArrayList<>();
    List <String> listastrings = new ArrayList<>();
    Stack<Integer> pilaeva = new Stack();
    
    
    
    public Sintactico(Nodo E) {
        try{
        p = E;       
            if (p.token == 215 ) {
                p = p.sig;
                if (p.token == 100) {
                    nomprog = p.lexema;
                    p = p.sig;
                    if (p.token == 115) {
                        p = p.sig;
                        block();
                        imprimirnodoCi();
                        if (Error) {
                            System.out.println(mensaje);
                            
                            return;
                        }
                        if (p.token == 113 ) {
                            System.out.println("Listo");
                            
                        } else {
                            System.out.println(erroresSin[0]);
                        }
                    } else {
                        System.out.println(erroresSin[1]);                     
                    }
                } else {
                    System.out.println(erroresSin[2]);                 
                }
            } else {
                System.out.println(erroresSin[3]);              
            } 
        System.out.println("Anlaisis sintactico terminado");
        } catch(NullPointerException e){
            System.out.println(erroresSin[15]);
        }
    }

    private void block() {      
        vardecpart(); 
        if (Error) {
            return;
        }

        compundstatement();
    }
    private void vardecpart() {
        if (p.token == 214) {
            p = p.sig;
            vardec();
        }
    }
    private void vardec() {
        if (p.token == 100) {
            if(nomprog.equals(p.lexema)){
                Error = true;
                mensaje = erroresSin[16];
                return;
            }
            pilavar.push(p.lexema);
            
            p = p.sig;
            if (p.token == 114) {
                p = p.sig;
                vardec();
                return;
            }
            if (p.token == 116) {
                p = p.sig;
                if (p.token == 216 || p.token == 217 || p.token == 218) {
                    pilavar.push(p.lexema);
                    
                    p = p.sig;
                    if (p.token == 115) {
                        insertarNodosVar();
                        p = p.sig;
                        if(p.token == 100){
                            vardec();
                            return;
                        }
                    } else {
                        Error = true;
                        mensaje = erroresSin[1];
                    }
                } else {
                    Error = true;
                    mensaje = erroresSin[4];
                }
            } else {
                Error = true;
                mensaje = erroresSin[5];
            }
        } else {
            Error = true;
            mensaje = erroresSin[2];
        }
    }
//modificar compund para poder tener crusion

int ics = 0;
    private void compundstatement() {
        if(p == null){Error = true; mensaje = "falta statement";return;}
        if (p.token == 210) {
            p = p.sig;
            if(p == null){Error = true; mensaje = "falta statement";return;}
            if(p.token == 115 ){
                Error = true;
                mensaje = "Error falta statement";
                return;
            }
            
            statement();
            if(Error){
                return;
            }
            if (p.token == 115) {
                while (isLastState == false) {
                    statement();
                    if(Error){return;}
                    if (p.token != 115) {
                        isLastState = true;
                    } else if (p.token == 115) {
                        p = p.sig;
                    }
                }
                if (p.token == 211 || ics < 0 ) {
                    ics = ics-1;
                    p = p.sig;
                } else {
                    Error = true;
                    
                    mensaje = erroresSin[6];//6
                }
            } else if (p.token == 211) {
                p = p.sig;
            } else {
                Error = true;
                mensaje = erroresSin[6];
            }
        } else {
            Error = true;
            mensaje = erroresSin[7];
        }
    }
    
    public boolean isStatement = true;
    public boolean isLastState = false;
    public void checkStatement() {
        if (p.token != 100 || p.token != 212 || p.token != 213 ||
                p.token != 210 || p.token != 208 || p.token != 204) {
            isStatement = false;
        }
    }

    ;
    public boolean isSimpleStatement = true;
    public boolean existStatement = true;
    private void statement() {
        if(p.token == 211){
        Error = true; mensaje = "Error falta statement"; return;
        }
        simpleState();
        if(Error){return;}
        if (isSimpleStatement == false) {
            strucstatement();
            if(Error){return;}
        }

    }

    // if 204 while 208 begin 210
    // read 212 write 213 id 100
    private void simpleState() {
        if (p.token == 204 || p.token == 208 || p.token == 210) {
            isSimpleStatement = false;
            return;
        }
        if (p.token == 100) {
            var = p.lexema;
            ListaTokens.add(regresarTV(getTv(p.lexema)));
            listastrings.add(p.lexema);
            varNoDec();
            if(Error){return;}
            p = p.sig;
            assign();
            return;
        }
        if (p.token == 212) {
            p = p.sig;
            read();
            return;
        }
        if (p.token == 213) {
            p = p.sig;
            write();
        }
    }

    private void assign() {
        if (p.token == 112) {
            ListaTokens.add(p.token);
            listastrings.add(p.lexema);
            p = p.sig;
            
            expression();
        } else {
            Error = true;
            mensaje = erroresSin[8];//8
        }
        prueba();
    }

    //= 106| <> 107| < 108| <= 109| >= 110| 111>
    private void expression() {
        
        checkSign();
        
        if((numpa-numpc)==0){imprimirLT();}
        
        if(Error){return;}
        if (p.token == 106 || p.token == 107 || p.token == 108 || 
                p.token == 109 || p.token == 110 || p.token == 111) {
            ListaTokens.add(p.token);
            listastrings.add(p.lexema);
            p = p.sig;
            checkSign();
            imprimirLT();
        }
        
    }
    private void checkSign() {
        if (p.token == 103 || p.token == 104) {
            if (p.token == 104) {
                p.token = 121;
            }
            ListaTokens.add(p.token);
            listastrings.add(p.lexema);
            p = p.sig;
        }
        simpleExpression();
    }
    private void simpleExpression() { 
        term();
        
        if(Error){return;}
        if (p.token == 201 || p.token == 103 || p.token == 104) {
            ListaTokens.add(p.token);
            listastrings.add(p.lexema);
            p = p.sig;
            simpleExpression();
        }
    }
    int numpa,numpc;
    // div and 200 202  * 105 // or - + 201 103 104
    private void term() {
        factor();
        if(Error){return;}
        if (p.token == 200 || p.token == 202 || p.token == 105) {
            ListaTokens.add(p.token);
            listastrings.add(p.lexema);
            p = p.sig;
            factor();
            if(p.token == 105 || p.token == 200){Error = true;mensaje = erroresSin[14];}
        }
    }
    boolean isConstant = false;
    boolean insideP = false;
    private void factor() {
        if (p.token == 100) {
            var = p.lexema;
            varNoDec();
            if(Error){return;}
            ListaTokens.add(regresarTV(getTv(p.lexema)));
            listastrings.add(p.lexema);
            p = p.sig;
            return;
        }
        constant();
        if (isConstant) {
            isConstant = false;
            return;
        }
        if (p.token == 118) {
            insideP= true;
            numpa++;
            ListaTokens.add(p.token);
            listastrings.add(p.lexema);
            p = p.sig;
            expression();
            
            if (p.token == 119) {
                insideP = false;
                numpc++;
                ListaTokens.add(p.token);
                listastrings.add(p.lexema);
                p = p.sig;
                return;
            } else {
                Error = true;
                mensaje = erroresSin[9];//9
                return;
            }
        }
        
        if (p.token == 203) {
            ListaTokens.add(p.token);
            listastrings.add(p.lexema);
            p = p.sig;
            factor();
        }
    }
    private void constant() {
        if (p.token == 101 || p.token == 102) {
            isConstant = true;
            ListaTokens.add(p.token);
            listastrings.add(p.lexema);
            p = p.sig;
            return;
        }
        if (p != null &&p.token == 100) {
            isConstant = true;           
            ListaTokens.add(regresarTV(getTv(p.lexema)));
            listastrings.add(p.lexema);
            p = p.sig;
            return;
        }
        if (p.token == 120) {
            isConstant = true;
            ListaTokens.add(p.token);
            listastrings.add(p.lexema);
            p = p.sig;
        }
    }

    private void read() {
        if (p.token == 118) {
            p = p.sig;
            if (p.token == 100) {
                var = p.lexema;
                varNoDec();
                if(Error){return;}
                insertarnodoCi(p.lexema,false);
                p = p.sig;
                if (p.token == 114 /*, */) {
                    inputvariable();
                    if (Error) {
                        return;
                    }
                }
                if (p.token == 119) {
                    p = p.sig;
                    insertarnodoCi("read",false);
                } else {
                    Error = true;
                    mensaje = erroresSin[9];//9

                }
            } else {
                Error = true;
                mensaje = erroresSin[2];//2
            }

        } else {
            Error = true;
            mensaje = erroresSin[10];//10
        }
    }

    private void inputvariable() {
        p = p.sig;
        if (p.token == 100) {
            var = p.lexema;
            varNoDec();
            if(Error){return;}
            insertarnodoCi(p.lexema,false);
            p = p.sig;
            if (p.token == 114) {
                inputvariable();
            }
        } else {
            Error = true;
            mensaje = erroresSin[2];//2
        }
    }

    private void write() {
        if (p.token == 118) {
            p = p.sig;
            expression();
            prueba();
            if(Error){return;}
            if (p.token == 114) {
                p=p.sig;
                outputvariable();
                if (Error) {
                    return;
                }
            }
            
            if (p.token == 119) {
                p = p.sig;
                insertarnodoCi("write",false);
            } else {
                Error = true;
                mensaje = erroresSin[9];//9
            }
        } else {
            Error = true;
            mensaje = erroresSin[10];//10
        }
    }
    private void outputvariable() {
        expression();
        prueba();
        if(Error){return;}
        if (p.token == 114) {
            outputvariable();
        }
    }

    private void strucstatement() {
        if (p.token == 210) {
            ics = ics +1;
            compundstatement();
            return;
        }
        if (p.token == 204) {
            ciif ++;
            Ifstatement();
            return;
        }
        if (p.token == 208) {
            ciw ++;
            whilestatement();
        }
    }
    int ciif = 0;
    private void Ifstatement() {
        int caif = 0;
        caif = ciif;
        p = p.sig;
        if(p.token ==205){
            Error = true;
            mensaje = "Error falta expression"; return;}
        expression();
        prueba();
        if(Error){ return;}
        insertarnodoCi("BRF-A"+caif,true);
        if (p.token == 205) { //then
            p = p.sig;
            statement();
            insertarnodoCi("BRI-B"+caif,true);
            insertarnodoCi("A"+caif,true);
            if (p.token == 206) { //else
                p = p.sig;
                statement();               
            }
         insertarnodoCi("B"+caif,true);   
        } else {
            Error = true;
            mensaje = erroresSin[11];//11
        }
        
    }
    int ciw = 0;
    private void whilestatement() {
        int caw = ciw;
        p = p.sig;
        if(p.token == 209){Error = true;mensaje = "falta expression ";}
        if(Error){return;}
        insertarnodoCi("D"+caw,true);
        expression();
        prueba();
        if(Error){return;}
        insertarnodoCi("BRF-C"+caw,true);
        if (p.token == 209) {
            p = p.sig;
            statement();
            insertarnodoCi("BRI-D"+caw,true);
        } else {
            Error = true;
            mensaje = erroresSin[12];
        }
        insertarnodoCi("C"+caw,true);
    }
    
    String erroresSin[] = {
    "Error: falta . ",
    "Error: falta ;",
    "Error: falta id",
    "Error: falta palabra reservada program",
    "Error: falta tipo de variable",
    "Error: falta :",
    "Error: falta palabra reservada end",
    "Error: falta palabra reservada begin",
    "Error: falta :=",
    "Error: falta )",
    "Error: falta (",
    "Error: falta palabra reservada then",
    "Error: falta palabra reservada do",
    "Error: falta statemnt",
    "Error: falta Expression",
    "Error: falta cerrar programa",
    "Error: variable ya declarado ",
    "Error: variable no declarado ",
    "Error: incomptabilidad de tipos",//18
    "Error: Nombre programa"
    };
    
    

    private void insertarNodosVar() {
        tipovar = pilavar.pop();
        while (pilavar.empty() == false) {
            if (head != null) {
                revisarVar();
                if (Error) {
                    return;
                }
            }
            Nodovar nodov = new Nodovar(pilavar.pop(), tipovar);
            if (head == null) {
                head = nodov;
                a = head;
            } else {
                a.sig = nodov;
                a = nodov;
            }
        }
    }
    
    private void revisarVar() {
        String nomvar = pilavar.peek();
        b = head;
        while (b != null) {
            if (b.lexema.equals(nomvar)) {
                Error = true;
                mensaje = erroresSin[16] + pilavar.peek();
                
                return;
            }
            b = b.sig;
        }
   
    }
    
    private void varNoDec(){
        if(nomprog.equals(var)){Error = true; mensaje = erroresSin[19]; return;}
        d = head;
        while(d != null){
            if(d.lexema.equals(var)){
                return;
            }
            d=d.sig;
        }
        
            Error = true;
            mensaje = erroresSin[17] + var;
    }
    private void insertarlistaPol(int e){
        ListaPolish lp = new ListaPolish(e);
        if(start == null){
            start = lp;
            l =start;
        }else{
            l.sig = lp;
            l = lp;
        }
    }
    
    
    private String getTv(String nomvar){
    b = head;
        while (b != null) {
            if (b.lexema.equals(nomvar)) {
               return b.tipo;
            }
            b = b.sig;
        }
    return "error";
    }
    
    private int regresarTV(String lex){      
        int valor=0;
        switch(lex){
            case "integer":
                valor = 101;
                break;
            case "String":
                valor = 120;
                break;
            case "real":
                valor = 102;
                break;
        }
    return valor;
    }
    
     
    
    
    private void posfijo() {
        int lt;
        for (int i = 0; i < ListaTokens.size(); i++) {
            lt = ListaTokens.get(i);
            if (lt == 120 || lt == 101 || lt == 102) {
                insertarlistaPol(lt);
                insertarnodoCi(listastrings.get(i),false);
            } else {
                if (pilaaux.empty() || lt == 118) {
                    pilaaux.push(lt);
                    pilastrings.push(listastrings.get(i));
                } else {
                    if (lt == 119) {
                        cerrarP();
                    } else {
                        if (pilaaux.peek() == 118) {
                            pilaaux.push(lt);
                            
                        } else {
                            if (getPrior(pilaaux.peek()) >= getPrior(lt)) {
                                while (getPrior(pilaaux.peek()) >= getPrior(lt)) {
                                    insertarlistaPol(pilaaux.pop());
                                    insertarnodoCi(pilastrings.pop(),false);
                                    if (pilaaux.empty() || pilaaux.peek() == 118) {
                                        break;
                                    }
                                }
                                pilaaux.push(lt);
                                pilastrings.push(listastrings.get(i));
                            } else {
                                pilaaux.push(lt);
                                pilastrings.push(listastrings.get(i));
                            }
                        }
                    }
                }
            }
        }
        if (!pilaaux.empty()) {
            while (!pilaaux.empty()) {
                insertarlistaPol(pilaaux.pop());
                insertarnodoCi(pilastrings.pop(),false);
            }
        }
    }
    
    private void cerrarP(){
        while(!pilaaux.empty() && pilaaux.peek() !=118){
            insertarlistaPol(pilaaux.pop());
        }
        pilaaux.pop();
    }
    
    
    
    private int getPrior(int op) {
        int p = 0;
        switch (op) {
            case 103:
                p = 4;
                break;
            case 104:
                p = 4;
                break;
            case 105:
                p = 5;
                break;
            case 200:
                p = 5;
                break;
            case 121:
                p = 6;
                break;
            case 112:
                p = 0;
                break;
            default:
                if (op < 200) {
                    p = 3;
                } else {
                    if (op == 201 || op == 202) {
                        p = 1;
                    } else {
                        p = 2;
                    };
                }
        }
        return p;
    }
    
    int op1,op2;
    private int evaluar() {
        int res = 0;
        lpp = start;
        while (lpp != null) {
            if (lpp.token == 101 || lpp.token == 102 || lpp.token == 120) {
                pilaeva.push(lpp.token);
            } else {
                if (lpp.token == 121) {
                    uni(lpp.token);
                    if (pilaeva.peek() == 0) {
                        break;
                    }
                } else {
                    //
                    bin(lpp.token);

                    if (pilaeva.peek() == 0) {
                        break;
                    }
                }
            }
            lpp = lpp.sig;
        }
        try{
        res = pilaeva.pop();
        }catch(Exception e){
        Error = true;
        mensaje = "falta expresion";
        res = 10;
        }
        
        return res;
    }
    
    private void uni(int op){
    op2 = pilaeva.pop();
        
    if(op == 121){
        
        pilaeva.push(neg[getIndex(op2)]);
        
    }
    if(op == 203){
    pilaeva.push(not[getIndex(op2)]);
    }
        
    }
    
    private int getIndex(int i){
        int index=0;
        switch(i){
            case 101: 
                index = 0;
                break;
            case 102 :
                index = 1;
                break;
            case 120:
                index = 2;
                break;
            case 2:
                index = 3;
        } 
        return index;
    }
    
    
    private void bin(int op) {
        op2 = pilaeva.pop();
        op1 = pilaeva.pop();
        switch (op) {
            case 103:
                pilaeva.push(mas[getIndex(op1)][getIndex(op2)]);
                break;
            case 104:
                 pilaeva.push(res[getIndex(op1)][getIndex(op2)]);
                break;
            case 105:
                 pilaeva.push(multi[getIndex(op1)][getIndex(op2)]);
                break;
            case 200:
                 pilaeva.push(div[getIndex(op1)][getIndex(op2)]);
                break;
            case 112:
                pilaeva.push(asi[getIndex(op1)][getIndex(op2)]);
                break;
            default:
                 if(op<200){
                     if(op>107){
                     pilaeva.push(rel[getIndex(op1)][getIndex(op2)]);
                     }else{
                     pilaeva.push(igudif[getIndex(op1)][getIndex(op2)]);
                     }
                 }else{
                     pilaeva.push(orand[getIndex(op1)][getIndex(op2)]);
                 }
        }
    }
    
    private void imprimirLT(){
    /*for(int i =0;i<ListaTokens.size();i++){
        System.out.println(ListaTokens.get(i));
    }*/
    
    }
    
    private void prueba(){
    posfijo();
    
    listastrings.clear();
    ListaTokens.clear();
    imprimirLp();
    }
    
    private void imprimirLp(){
        lpp = start;
        //while(lpp != null){
            //System.out.println(lpp.token);
          //  lpp = lpp.sig;
        //}
        int ev;
        ev = evaluar();
        //System.out.println(ev);
        if(ev == 0){
            Error = true;
            mensaje = erroresSin[18];
        }if(ev == 10){
            Error = true;
            mensaje = "falta expresion";
        }
        
    }
    
    int mas[][] = {
        {101,102,  0,  0},
        {102,102,  0,  0},
        {  0,  0,120,  0},
        {  0,  0,  0,  0}
    };
    
    int res[][] = {
        {101,102,  0,  0},
        {102,102,  0,  0},
        {  0,  0,  0,  0},
        {  0,  0,  0,  0}
    };
    
    int multi [] [] = {
        {101,102,  0,  0},
        {102,102,  0,  0},
        {  0,  0,  0,  0},
        {  0,  0,  0,  0}
    };
    
    int div [] [] = {
        {102,102,  0,  0},
        {102,102,  0,  0},
        {  0,  0,  0,  0},
        {  0,  0,  0,  0}
    };
    
    
    
    int asi [][] = {
        {  1,  0,  0,  0},
        {  1,  1,  0,  0},
        {  0,  0,  1,  0},
        {  0,  0,  0,  0}
    };
    
    int neg [] = {101,102,0,0};
    
    
    int not[] = {0,0,0,2};
    
    int igudif [] [] = {
        {2,2,0,0},
        {2,2,0,0},
        {0,0,2,0},
        {0,0,0,2}
    };
    
    int rel [] [] = {
        {2,2,0,0},
        {2,2,0,0},
        {0,0,0,0},
        {0,0,0,0}
    };
   
    
    int orand[][] = {
        {0,0,0,0},
        {0,0,0,0},
        {0,0,0,0},
        {0,0,0,1}
    };
    
   
//ListaTokens.add(regresarTV(getTv(p.lexema)));    
    
    public void insertarnodoCi(String contenido,boolean Eti){
        nodoCI nodoci = new nodoCI(contenido);
        if(Eti){nodoci.esEtiqueta=true;}
        if(com == null){
        com = nodoci;
        ncip = com;
        }else{
        ncip.sig = nodoci;
        ncip = nodoci;
        }
    }
    public void imprimirprueba(){
        for (int i = 0; i < listastrings.size(); i++) {
            System.out.println(listastrings.get(i));
        }
    }
    
    
    public void imprimirnodoCi(){
        ncipp = com;
        while(ncipp !=null){
            System.out.println(ncipp.contenido);
            ncipp = ncipp.sig;       
        }
}
    
}
