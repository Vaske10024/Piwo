package org.example.lexer;

import java.util.*;
import java.util.regex.*;

public class Lexer {
    private final String whole_text;
    private int i=0;
    private int line=1;
    private int col=1;
    private final int n;
    private final List<Token> tokens=new ArrayList<>();

    private static final String BEER="\uD83C\uDF7A";

    private static final Pattern WS=Pattern.compile("[ \t]+");
    private static final Pattern NL=Pattern.compile("\r?\n");
    private static final Pattern LINE_COMMENT=Pattern.compile("//[^\n]*");
    private static final Pattern BLOCK_COMMENT=Pattern.compile("/\\*.*?\\*/",Pattern.DOTALL);

    private static final Pattern FLOAT_RE=Pattern.compile("(?:[0-9][0-9_]*\\.[0-9][0-9_]*)(?:[eE][+-]?[0-9]+)?");
    private static final Pattern INT_RE=Pattern.compile("(?:0|[1-9][0-9_]*)");
    private static final Pattern STR_RE=Pattern.compile("\"(?:\\\\.|[^\"\\\\])*\"");
    private static final Pattern IDENT_RE=Pattern.compile("[\\p{L}_][\\p{L}\\p{N}_]*");

    private static final Map<String,TokenType> KEYWORDS=Map.ofEntries(
            Map.entry("casa",TokenType.CASA),
            Map.entry("limenka",TokenType.LIMENKA),
            Map.entry("krigla",TokenType.KRIGLA),
            Map.entry("bomba",TokenType.BOMBA),
            Map.entry("pinta",TokenType.PINTA),
            Map.entry("pijan",TokenType.PIJAN),
            Map.entry("ako",TokenType.IF),
            Map.entry("inace",TokenType.ELSE),
            Map.entry("ziveli",TokenType.RETURN),
            Map.entry("pivnica",TokenType.FN),
            Map.entry("sipaj",TokenType.READ),
            Map.entry("nazdravi",TokenType.PRINT),
            Map.entry("tacno",TokenType.BOOL_LITERAL),
            Map.entry("netacno",TokenType.BOOL_LITERAL),
            Map.entry("kap", TokenType.CHAR)
            );

    private static final Map<String,TokenType> SYMBOLS=Map.ofEntries(
            Map.entry("(",TokenType.L_PAREN),
            Map.entry(")",TokenType.R_PAREN),
            Map.entry("{",TokenType.L_BRACE),
            Map.entry("}",TokenType.R_BRACE),
            Map.entry("[",TokenType.L_BRACKET),
            Map.entry("]",TokenType.R_BRACKET),
            Map.entry(",",TokenType.COMMA),
            Map.entry(".",TokenType.DOT),
            Map.entry(":",TokenType.COLON),
            Map.entry(BEER,TokenType.BEER),
            Map.entry("+",TokenType.PLUS),
            Map.entry("-",TokenType.MINUS),
            Map.entry("*",TokenType.STAR),
            Map.entry("/",TokenType.SLASH),
            Map.entry("%",TokenType.PERCENT),
            Map.entry("=",TokenType.EQUAL),
            Map.entry("<",TokenType.LESS),
            Map.entry(">",TokenType.GREATER)
    );

    private static final Set<String> TWO_CHAR=Set.of("!=","==",">=","<=","&&","||");

    public static class LexerError extends RuntimeException{
        public LexerError(String msg){ super(msg); }
    }

    public Lexer(String source){
        whole_text=source;
        n=source.length();
    }

    private char peek(){
        return peek(0);
    }

    private char peek(int k){
        int j=i+k;
        if(j>=n) return '\0';
        return whole_text.charAt(j);
    }


    //Cita trenutni i pomera se nas ledeci karakter
    private char advance(){
        char ch=peek();
        i++;
        if(ch=='\n'){
            line++;
            col=1;
        } else {
            col++;
        }
        return ch;
    }


    //k oristimo za dvokarakterino operatori
    private boolean matchString(String s){
        if(whole_text.startsWith(s,i)){
            for(int k=0;k<s.length();k++) {
                advance();
            }
            return true;
        }
        return false;
    }

    private String slice(){
        return whole_text.substring(i);
    }



    //Korisitmo ga da "pojedemo" komentari ili whitespacovi, stvari koej nam ne trebaju
    private boolean consumeIfMatches(Pattern p){
        Matcher m=p.matcher(slice());
        if(m.lookingAt()){
            String g=m.group();
            for(int k=0;k<g.length();k++) advance();
            return true;
        }
        return false;
    }


    // na istu foru kao consumeif matches samo sto vraca string g koji je uhvacen
    private String takeRegex(Pattern p){
        Matcher m=p.matcher(slice());
        if(m.lookingAt()){
            String g=m.group();
            for(int k=0;k<g.length();k++){
                advance();
            }
            return g;
        }
        return null;
    }



    //Nalazi svaku vrstu white spacea i komentara i consumuje ihc
    private void skipWhitespaceAndComments(){
        boolean any;
        do{
            any=false;
            if(consumeIfMatches(WS)) any=true;
            if(consumeIfMatches(NL)) any=true;
            if(consumeIfMatches(LINE_COMMENT)) any=true;
            if(consumeIfMatches(BLOCK_COMMENT)) any=true;
        }while(any);
    }

    public List<Token> lexiraj(){
        while(true){
            skipWhitespaceAndComments();
            if(i>=n) break;

            int sl=line, sc=col;



            //Hvatamo dvokarakterini
            String two=(i+2<=n)?whole_text.substring(i,i+2):"";
            if(TWO_CHAR.contains(two)){
                matchString(two);
                tokens.add(new Token(mapTwoChar(two),two,sl,sc));
                continue;
            }


            //Hvatamo pivo

            if(matchString(BEER)){
                tokens.add(new Token(TokenType.BEER,BEER,sl,sc));
                continue;
            }


            //Hvatamo cudni simboli
            char ch=peek();
            String cs=String.valueOf(ch);
            if(SYMBOLS.containsKey(cs)){
                advance();
                tokens.add(new Token(SYMBOLS.get(cs),cs,sl,sc));
                continue;
            }

            if(ch=='!'){
                advance();
                tokens.add(new Token(TokenType.BANG,"!",sl,sc));
                continue;
            }

            String f=takeRegex(FLOAT_RE);
            if(f!=null){
                String clean=f.replace("_","");
                tokens.add(new Token(TokenType.FLOAT_LITERAL,f,sl,sc,Double.valueOf(clean)));
                continue;
            }

            String in=takeRegex(INT_RE);
            if(in!=null){
                String clean=in.replace("_","");
                tokens.add(new Token(TokenType.INT_LITERAL,in,sl,sc,Long.valueOf(clean)));
                continue;
            }

            String s=takeRegex(STR_RE);
            if(s!=null){
                String val=parseEscapes(s.substring(1,s.length()-1));
                tokens.add(new Token(TokenType.STRING_LITERAL,s,sl,sc,val));
                continue;
            }

            String id=takeRegex(IDENT_RE);
            if(id!=null){
                if(id.equals("dok")){
                    int si=i, ll=line, cc=col;
                    takeRegex(WS);
                    String id2=takeRegex(IDENT_RE);
                    if(id2!=null&&id2.equals("toci")){
                        tokens.add(new Token(TokenType.WHILE,"dok toci",sl,sc));
                        continue;
                    } else {
                        i=si; line=ll; col=cc;
                    }
                }
                if(KEYWORDS.containsKey(id)){
                    TokenType t=KEYWORDS.get(id);
                    Object lit=null;
                    if(t==TokenType.BOOL_LITERAL) lit=id.equals("tacno");
                    tokens.add(new Token(t,id,sl,sc,lit));
                } else {
                    tokens.add(new Token(TokenType.IDENTIFIER,id,sl,sc));
                }
                continue;
            }

            char bad=advance();
            throw new LexerError("Leksicka greska: neocekivani simbol '"+bad+"' na "+sl+":"+sc);
        }
        tokens.add(new Token(TokenType.EOF,"",line,col));
        return tokens;
    }

    private static TokenType mapTwoChar(String s){
        switch(s){
            case "!=": return TokenType.BANG_EQUAL;
            case "==": return TokenType.EQUAL_EQUAL;
            case ">=": return TokenType.GREATER_EQUAL;
            case "<=": return TokenType.LESS_EQUAL;
            case "&&": return TokenType.AND_AND;
            case "||": return TokenType.OR_OR;
            default: throw new IllegalStateException("Unknown two char op: "+s);
        }
    }

    private static String parseEscapes(String raw){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<raw.length();i++){
            char c=raw.charAt(i);
            if(c=='\\'){
                i++;
                if(i>=raw.length()) break;
                char e=raw.charAt(i);
                switch(e){
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case '\\': sb.append('\\'); break;
                    case '"': sb.append('"'); break;
                    default: sb.append(e);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
