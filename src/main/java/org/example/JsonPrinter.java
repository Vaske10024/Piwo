package org.example;

import org.example.parser.AST;
import java.util.*;

public class JsonPrinter {
    private final StringBuilder sb=new StringBuilder();
    private int indent=0;
    private final Deque<Boolean> firstPropStack=new ArrayDeque<>();

    public static String toJson(Object o){
        return new JsonPrinter().print(o);
    }

    private String print(Object o){
        write(o);
        return sb.toString();
    }

    private void write(Object o){
        if(o==null){
            sb.append("null");
            return;
        }
        if(o instanceof String s){
            sb.append("\"").append(escape(s)).append("\"");
            return;
        }
        if(o instanceof Number||o instanceof Boolean){
            sb.append(o.toString());
            return;
        }

        if(o instanceof AST.Program p){
            startObj(); beginProps();
            prop("decls"); writeList(p.decls);
            endObj(); return;
        }
        if(o instanceof AST.VarDecl v){
            startObj(); beginProps();
            prop("type_name"); write(v.typeName);
            prop("name"); write(v.name);
            prop("array_dims"); write(v.arrayDims);
            prop("init"); write(v.init);
            endObj(); return;
        }
        if(o instanceof AST.Param p){
            startObj(); beginProps();
            prop("type_name"); write(p.typeName);
            prop("name"); write(p.name);
            prop("array_dims"); write(p.arrayDims);
            endObj(); return;
        }
        if(o instanceof AST.FnDecl f){
            startObj(); beginProps();
            prop("name"); write(f.name);
            prop("params"); writeList(f.params);
            prop("ret_type"); write(f.retType);
            prop("body"); write(f.body);
            endObj(); return;
        }
        if(o instanceof AST.Block b){
            startObj(); beginProps();
            prop("statements"); writeList(b.statements);
            endObj(); return;
        }
        if(o instanceof AST.IfStmt i){
            startObj(); beginProps();
            prop("cond"); write(i.cond);
            prop("then_branch"); write(i.thenB);
            prop("else_branch"); write(i.elseB);
            endObj(); return;
        }
        if(o instanceof AST.WhileStmt w){
            startObj(); beginProps();
            prop("cond"); write(w.cond);
            prop("body"); write(w.body);
            endObj(); return;
        }
        if(o instanceof AST.ReturnStmt r){
            startObj(); beginProps();
            prop("value"); write(r.value);
            endObj(); return;
        }
        if(o instanceof AST.ExprStmt e){
            startObj(); beginProps();
            prop("expr"); write(e.expr);
            endObj(); return;
        }
        if(o instanceof AST.Binary b){
            startObj(); beginProps();
            prop("left"); write(b.left);
            prop("op"); write(b.op);
            prop("right"); write(b.right);
            endObj(); return;
        }
        if(o instanceof AST.Unary u){
            startObj(); beginProps();
            prop("op"); write(u.op);
            prop("expr"); write(u.expr);
            endObj(); return;
        }
        if(o instanceof AST.Call c){
            startObj(); beginProps();
            prop("callee"); write(c.callee);
            prop("args"); writeList(c.args);
            endObj(); return;
        }
        if(o instanceof AST.Index x){
            startObj(); beginProps();
            prop("array"); write(x.array);
            prop("index"); write(x.index);
            endObj(); return;
        }
        if(o instanceof AST.Literal l){
            startObj(); beginProps();
            prop("value"); write(l.value);
            endObj(); return;
        }
        if(o instanceof AST.Variable v){
            startObj(); beginProps();
            prop("name"); write(v.name);
            endObj(); return;
        }
        if(o instanceof AST.Assign a){
            startObj(); beginProps();
            prop("name"); write(a.name);
            prop("value"); write(a.value);
            endObj(); return;
        }

        if(o instanceof List<?> list){
            writeList(list);
            return;
        }

        sb.append("\"").append(escape(String.valueOf(o))).append("\"");
    }

    private void writeList(List<?> list){
        sb.append("[");
        if(!list.isEmpty()){
            newline(); indent++;
            for(int idx=0; idx<list.size(); idx++){
                indent();
                write(list.get(idx));
                if(idx<list.size()-1) sb.append(",");
                newline();
            }
            indent--; indent();
        }
        sb.append("]");
    }

    private void startObj(){
        sb.append("{");
        indent++;
    }

    private void endObj(){
        indent--;
        newline(); indent();
        sb.append("}");
        if(!firstPropStack.isEmpty()) firstPropStack.pop();
    }

    private void beginProps(){
        firstPropStack.push(true);
    }

    private void prop(String name){
        boolean isFirst=firstPropStack.peek()==null||firstPropStack.peek();
        if(!isFirst) sb.append(",");
        newline(); indent();
        sb.append("\"").append(name).append("\": ");
        if(!firstPropStack.isEmpty()){
            firstPropStack.pop();
            firstPropStack.push(false);
        }
    }

    private void newline(){ sb.append("\n"); }
    private void indent(){ for(int k=0;k<indent;k++) sb.append("  "); }

    private static String escape(String s){
        return s.replace("\\","\\\\")
                .replace("\"","\\\"")
                .replace("\n","\\n")
                .replace("\r","\\r")
                .replace("\t","\\t");
    }
}
