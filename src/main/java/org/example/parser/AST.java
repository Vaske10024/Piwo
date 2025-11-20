package org.example.parser;

import java.util.*;

public class AST {
    public static class Program {
        public final List<Object> decls;
        public Program(List<Object> d){ decls=d; }
    }

    public static class VarDecl {
        public final String typeName;
        public final String name;
        public final int arrayDims;
        public final Object init;
        public VarDecl(String t,String n,int d,Object i){
            typeName=t; name=n; arrayDims=d; init=i;
        }
    }

    public static class Param {
        public final String typeName;
        public final String name;
        public final int arrayDims;
        public Param(String t,String n,int d){
            typeName=t; name=n; arrayDims=d;
        }
    }

    public static class FnDecl {
        public final String name;
        public final List<Param> params;
        public final String retType;
        public final Block body;
        public FnDecl(String n,List<Param> p,String r,Block b){
            name=n; params=p; retType=r; body=b;
        }
    }

    public static class Block {
        public final List<Object> statements;
        public Block(List<Object> s){ statements=s; }
    }

    public static class IfStmt {
        public final Object cond;
        public final Block thenB;
        public final Block elseB;
        public IfStmt(Object c,Block t,Block e){
            cond=c; thenB=t; elseB=e;
        }
    }

    public static class WhileStmt {
        public final Object cond;
        public final Block body;
        public WhileStmt(Object c,Block b){ cond=c; body=b; }
    }

    public static class ReturnStmt {
        public final Object value;
        public ReturnStmt(Object v){ value=v; }
    }

    public static class ExprStmt {
        public final Object expr;
        public ExprStmt(Object e){ expr=e; }
    }

    public static class Binary {
        public final Object left;
        public final String op;
        public final Object right;
        public Binary(Object l,String o,Object r){ left=l; op=o; right=r; }
    }

    public static class Unary {
        public final String op;
        public final Object expr;
        public Unary(String o,Object e){ op=o; expr=e; }
    }

    public static class Call {
        public final String callee;
        public final List<Object> args;
        public Call(String c,List<Object> a){ callee=c; args=a; }
    }

    public static class Index {
        public final Object array;
        public final Object index;
        public Index(Object a,Object i){ array=a; index=i; }
    }

    public static class Literal {
        public final Object value;
        public Literal(Object v){ value=v; }
    }

    public static class Variable {
        public final String name;
        public Variable(String n){ name=n; }
    }

    public static class Assign {
        public final String name;
        public final Object value;
        public Assign(String n,Object v){ name=n; value=v; }
    }
}
