INCLUDE macros.mac
INCLUDE fp.a.
INLCUDELIB stadlib.lib
.model Small

.stack

.data
		MAXLEN DB 254
		LEN DB 0
		MSG   DB 254 DUP(?)
		MSG_DD   DD MSG            
		BUFFER      DB 8 DUP('$')
		CADENA_NUM      DB 10 DUP('$')
		BUFFERTEMP  DB 8 DUP('$')            
		BLANCO  DB '#'
		BLANCOS DB '$'
		MENOS   DB '-$'
		COUNT   DW 0
		NEGATIVO    DB 0            
		BUF DW 10
		LISTAPAR    LABEL BYTE
		LONGMAX DB 254
		TRUE    DW 1
		FALSE DW 0            
		INTRODUCIDOS    DB 254 DUP ('$')
		MULT10  DW 1 
		s_true  DB 'true$'
		s_false DB 'false$'
		CVTS1 DB d, '$'
		d DW 0
		c DW 0
		b DW 0
		a DW 0
		z DB ?, '$'
		CVTI1 DW 0
		CVTR1 DD 0
		rInt1 DW 0
		rInt2 DW 0
.code
main proc
	mov ax, SEG @data
	mov ds, ax
	I_ASIGNAR d,0
D1:
	I_MENOR d,7,CVTI1
	CMP CVTI1,1
	JMP BRF-C1
	WRITE d
	F_SUMAR d,1,CVTR1
	I_ASIGNAR d,CVTI1
	JMP BRI-D1
C1:
	.exit
	main endp
end main