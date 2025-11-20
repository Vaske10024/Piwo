package org.example.main;

import org.example.JsonPrinter;
import org.example.lexer.Lexer;
import org.example.lexer.Token;
import org.example.parser.AST;
import org.example.parser.Parser;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception{
        if(args.length!=1){
            System.out.println("Upotreba: java -jar piwo.jar fajl_koji_hocemo.piwo");
            System.exit(2);
        }
        String fajl=Files.readString(Path.of(args[0]),StandardCharsets.UTF_8);
        try{
            List<Token> tokens=new Lexer(fajl).lexiraj();
            Parser parser=new Parser(tokens);
            AST.Program program=parser.parse();
            System.out.println(JsonPrinter.toJson(program));
        } catch(Lexer.LexerError e){
            System.out.println(e.getMessage());
            System.exit(1);
        } catch(Parser.ParseError e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
