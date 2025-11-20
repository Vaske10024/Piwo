package org.example.parser;

import java.util.*;
import org.example.parser.AST.*;
import org.example.lexer.Token;
import org.example.lexer.TokenType;

public class Parser {
    private final List<Token> tokens;
    private int i=0;

    public static class ParseError extends RuntimeException{
        public ParseError(String m){ super(m); }
    }

    public Parser(List<Token> tokens){
        this.tokens=tokens; }

    private Token peek(){
        int j=Math.min(i,tokens.size()-1);
        return tokens.get(j);
    }

    private Token prev(){
        return tokens.get(i-1);
    }

    private Token safePrev(){
        if(i>0){
            return tokens.get(i-1);
        }
        else {
            return tokens.get(0);
        }
    }


    private boolean at(TokenType t){
        return peek().type==t;
    }

    /// Ako je trenutni token jedan od datih tipova pojedi ga i vrati true, inace false

    private boolean match(TokenType... ts){
        TokenType cur=peek().type;
        for(TokenType t:ts){
            if(cur==t){
                i++;
                return true;
            }
        }
        return false;
    }


    //Ako je trenutni token neki koji ubacimo onda ga pojedi
    private Token consume(TokenType t,String msg){
        if(at(t)){
            Token tok=peek();
            i++;
            return tok;
        }
        throw new ParseError("Sintaksna greska: "+msg+" (ocekivano "+t+", poslednje "+safePrev()+")");
    }



    /// Pravi listu deklaracija i dok ne dodje do EOF(kraj programa)
    public Program parse(){
        List<Object> decls=new ArrayList<>();
        while(!at(TokenType.EOF)){
            decls.add(declaration());
        }
        return new Program(decls);
    }


    //Prvo trazi FN, kljicna rec za funkciju, ako nije tu baca gresku
    //Cita ime funkcije, pa ( pa cita parametri dok ne naidje )
    //Gleda jel ima povratni tiop
    //Cita block kao telo funkcije
    // vraca new fndecl(nam,eparams,rettype,body)
    //Znaci ovo je za main bukv
    private Object declaration(){
        if(match(TokenType.FN)){
            String name=consume(TokenType.IDENTIFIER,"ime funkcije").lexeme;
            consume(TokenType.L_PAREN,"otvorena zagrada posle imena funkcije");

            List<Param> params=new ArrayList<>();
            if(!at(TokenType.R_PAREN)){
                do{
                    String tname=typeName();
                    int dims=arrayDims();
                    String pname=consume(TokenType.IDENTIFIER,"ime parametra").lexeme;
                    params.add(new Param(tname,pname,dims));
                } while(match(TokenType.COMMA));
            }
            consume(TokenType.R_PAREN,"zatvorena zagrada posle parametara");

            String retType=null;
            if(match(TokenType.COLON)){
                retType=typeName();
                retType+= "[]".repeat(arrayDims());
            }

            Block body=block();
            return new FnDecl(name,params,retType,body);
        }

        throw new ParseError("Sintaksna greska: ocekivan pocetak deklaracije funkcije ('pivnica'), poslednje "+safePrev());
    }

    private String typeName(){
        if(match(TokenType.CASA)) return "casa";
        if(match(TokenType.LIMENKA)) return "limenka";
        if(match(TokenType.KRIGLA)) return "krigla";
        if(match(TokenType.BOMBA)) return "bomba";
        if(match(TokenType.PINTA)) return "pinta";
        if(match(TokenType.PIJAN)) return "pijan";
        if(match(TokenType.CHAR)) return "char";

        throw new ParseError("Sintaksna greska: ocekivan tip ('casa','limenka','krigla','bomba','pinta','pijan'), poslednje "+safePrev());
    }


    //Broji uglaste zagrade, da nadje koliki je niz
    private int arrayDims(){
        int d=0;
        while(match(TokenType.L_BRACKET)){
            consume(TokenType.R_BRACKET,"zatvorena uglasta zagrada u tipu");
            d++;
        }
        return d;
    }


    //ocekuje { pa kuplja stetmente dok ne dodje do }
    private Block block(){
        consume(TokenType.L_BRACE,"otvorena viticasta zagrada bloka");
        List<Object> stmts=new ArrayList<>();
        while(!at(TokenType.R_BRACE)){
            stmts.add(statement());
        }
        consume(TokenType.R_BRACE,"zatvorena viticasta zagrada bloka");
        return new Block(stmts);
    }

    private Object statement(){

        //IsTypeToken funkcija gleda da li je taj token neki od nasih tipova

        if(isTypeToken(peek().type)){
            String tname=typeName();  //gleda ime
            int dims=arrayDims();    //gleda dimenzije
            String name=consume(TokenType.IDENTIFIER,"ime promenljive").lexeme; // I onda ime promenljive

            Object init=null;
            if(match(TokenType.EQUAL)){
                init=expression();
            }

            consume(TokenType.BEER,"casa piva (🍺) kao separator naredbe");
            return new VarDecl(tname,name,dims,init);
        }

        if(match(TokenType.IF)){
            consume(TokenType.L_PAREN,"otvorena zagrada posle 'ako'");
            Object cond=expression();
            consume(TokenType.R_PAREN,"zatvorena zagrada posle uslova");

            Block thenB=block();
            Block elseB=null;
            if(match(TokenType.ELSE)){
                elseB=block();
            }
            return new IfStmt(cond,thenB,elseB);
        }

        if(match(TokenType.WHILE)){
            consume(TokenType.L_PAREN,"otvorena zagrada posle 'dok toci'");
            Object cond=expression();
            consume(TokenType.R_PAREN,"zatvorena zagrada posle uslova");
            Block body=block();
            return new WhileStmt(cond,body);
        }

        if(match(TokenType.RETURN)){
            Object val=null;
            if(!at(TokenType.BEER)){
                val=expression();
            }
            consume(TokenType.BEER,"🍺 posle 'ziveli'");
            return new ReturnStmt(val);
        }

        if(match(TokenType.PRINT)){
            consume(TokenType.L_PAREN,"otvorena zagrada posle 'nazdravi'");
            Object arg=null;
            if(!at(TokenType.R_PAREN)){
                arg=expression();
            }
            consume(TokenType.R_PAREN,"zatvorena zagrada posle 'nazdravi'");
            consume(TokenType.BEER,"🍺 posle 'nazdravi'");
            List<Object> args=new ArrayList<>();
            if(arg!=null) args.add(arg);
            return new ExprStmt(new Call("nazdravi",args));
        }

        if(match(TokenType.READ)){
            consume(TokenType.L_PAREN,"otvorena zagrada posle 'sipaj'");
            String ident=consume(TokenType.IDENTIFIER,"ime promenljive").lexeme;
            consume(TokenType.R_PAREN,"zatvorena zagrada posle 'sipaj'");
            consume(TokenType.BEER,"🍺 posle 'sipaj'");
            return new ExprStmt(new Call("sipaj",List.of(new Variable(ident))));
        }

        Object expr=expression();
        consume(TokenType.BEER,"🍺 posle izraza");
        return new ExprStmt(expr);
    }

    private boolean isTypeToken(TokenType tt){
        return tt==TokenType.CASA
                || tt==TokenType.LIMENKA
                || tt==TokenType.KRIGLA
                || tt==TokenType.BOMBA
                || tt==TokenType.PINTA
                || tt==TokenType.PIJAN
                || tt == TokenType.CHAR;
    }

    private Object expression(){ return assignment(); }

    private Object assignment(){
        Object expr=logicOr();
        if(match(TokenType.EQUAL)){
            Object value=assignment();
            if(expr instanceof Variable v){
                return new Assign(v.name,value);
            }
            throw new ParseError("Sintaksna greska: leva strana dodele mora biti ime promenljive, dobio "+expr);
        }
        return expr;
    }

    private Object logicOr(){
        Object expr=logicAnd();
        while(match(TokenType.OR_OR)){
            Object right=logicAnd();
            expr=new Binary(expr,"||",right);
        }
        return expr;
    }

    private Object logicAnd(){
        Object expr=equality();
        while(match(TokenType.AND_AND)){
            Object right=equality();
            expr=new Binary(expr,"&&",right);
        }
        return expr;
    }

    private Object equality(){
        Object expr=comparison();
        while(match(TokenType.EQUAL_EQUAL,TokenType.BANG_EQUAL)){
            String op=prev().lexeme;
            Object right=comparison();
            expr=new Binary(expr,op,right);
        }
        return expr;
    }

    private Object comparison(){
        Object expr=term();
        while(match(TokenType.GREATER,TokenType.GREATER_EQUAL,TokenType.LESS,TokenType.LESS_EQUAL)){
            String op=prev().lexeme;
            Object right=term();
            expr=new Binary(expr,op,right);
        }
        return expr;
    }

    private Object term(){
        Object expr=factor();
        while(match(TokenType.PLUS,TokenType.MINUS)){
            String op=prev().lexeme;
            Object right=factor();
            expr=new Binary(expr,op,right);
        }
        return expr;
    }

    private Object factor(){
        Object expr=unary();
        while(match(TokenType.STAR,TokenType.SLASH,TokenType.PERCENT)){
            String op=prev().lexeme;
            Object right=unary();
            expr=new Binary(expr,op,right);
        }
        return expr;
    }

    private Object unary(){
        if(match(TokenType.BANG,TokenType.MINUS)){
            String op=prev().lexeme;
            Object right=unary();
            return new Unary(op,right);
        }
        return callOrPrimary();
    }

    private Object callOrPrimary(){
        Object expr=primary();
        while(true){
            if(match(TokenType.L_PAREN)){
                List<Object> args=new ArrayList<>();
                if(!at(TokenType.R_PAREN)){
                    args.add(expression());
                    while(match(TokenType.COMMA)){
                        args.add(expression());
                    }
                }
                consume(TokenType.R_PAREN,"zatvaranje zagrade poziva");
                if(expr instanceof Variable v){
                    expr=new Call(v.name,args);
                } else {
                    throw new ParseError("Sintaksna greska: nevazeci poziv funkcije: "+expr);
                }
            } else if(match(TokenType.L_BRACKET)){
                Object idx=expression();
                consume(TokenType.R_BRACKET,"zatvaranje indeksa niza");
                expr=new Index(expr,idx);
            } else {
                break;
            }
        }
        return expr;
    }

    private Object primary(){
        Token t=peek();
        if(match(TokenType.INT_LITERAL,TokenType.FLOAT_LITERAL,TokenType.STRING_LITERAL,TokenType.BOOL_LITERAL)){
            return new Literal(t.literal!=null?t.literal:t.lexeme);
        }
        if(match(TokenType.IDENTIFIER)){
            return new Variable(t.lexeme);
        }
        if(match(TokenType.L_PAREN)){
            Object e=expression();
            consume(TokenType.R_PAREN,"zatvaranje izraza");
            return e;
        }
        throw new ParseError("Sintaksna greska: neocekivano "+t);
    }
}
