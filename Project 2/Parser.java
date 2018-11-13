/*
    This class provides a recursive descent parser 
    for Corgi (a simple calculator language),
    creating a parse tree which can be interpreted
    to simulate execution of a Corgi program
*/

import java.util.*;
import java.io.*;

public class Parser {

   private Lexer lex;

   public Parser( Lexer lexer ) {
      lex = lexer;
   }

   // <program> -> <funcCall> | <funcCall> <funcDefs>
   public Node parseProgram() {
      System.out.println("-----> parsing <program>");

      Node first = parseFuncCall();

      //look ahead to see if there are more statements
      Token token = lex.getNextToken();

      if( token.isKind( "eof") ){
         return new Node( "program", first, null, null );
      }
      else{
         lex.putBackToken( token );
         Node second = parseFuncDefs();
         return new Node( "program", first, second, null );
      }

   }

   // <funcCall> -> <var> ( ) | <var> ( <args> )
   private Node parseFuncCall(){
      System.out.println("-----> parsing <funcCall>");
      Token t = lex.getNextToken();
      Token u = lex.getNextToken();
      Token v = lex.getNextToken();
      if( t.isKind("var")) {
         if (u.matches("single", "(")) {
            if (v.matches("single", ")")) {
               return new Node ("funcCall", t.getDetails(),null, null, null);
            }

            else {
               lex.putBackToken(v);
               Node second = parseArgs();
               return new Node("funcCall", t.getDetails(), null, second, null);

            }
         }
      }
      // System.exit(1);
      return new Node ("funcCall", t.getDetails(),null, null, null);
   }

   // <args> -> <expr> | <expr> , <args>
   private Node parseArgs(){
      System.out.println("-----> parsing <args>");
      Node first = parseExpr();
      Token t = lex.getNextToken();
      if(t.matches("single", ")")){
         return new Node("args",  first, null, null);
      }
      else if(t.matches("single", ",")){
         Node second = parseArgs();
         return new Node("args", first, second, null);
      }
      return null;
   }

   // <funcDefs> -> <funcDef> | <funcDef> <funcDefs>
   private Node parseFuncDefs(){
      System.out.println("-----> parsing <funcDefs>");

      Node first = parseFuncDef();

      Token token = lex.getNextToken();

      if ( token.isKind( "eof" ) ) {
         return new Node( "funcDefs", first, null, null );
      }
      else {
         lex.putBackToken( token );
         Node second = parseFuncDefs();
         return new Node( "funcDefs", first, second, null );
      }

   }

   // <funcDef> ->  def <var> ( ) end |
   //               def <var> ( ) <statements> end
   //               def <var> ( <params> ) end |
   //               def <var> ( <params> ) <statements> end
   private Node parseFuncDef(){
      System.out.println("-----> parsing <funcDef>");
      Token t = lex.getNextToken();
      Token f = lex.getNextToken();
      Token y = lex.getNextToken();
      Token x = lex.getNextToken();
      Token z = lex.getNextToken();
//      System.out.println("t = " + t.getDetails());
//      System.out.println("f = " + f.getDetails());
//      System.out.println("y = " + y.getDetails());
//      System.out.println("x = " + x.getDetails());
//      System.out.println("z = " + z.getDetails());
      if (t.getDetails().equals("def") && f.isKind("var")) {
         if (y.getDetails().equals("(") && x.getDetails().equals(")")) {
            if (z.getDetails().equals("end")) {
               // def <var> ( ) end
               return new Node("funcDef", f.getDetails(), null, null, null);
            }
            else {
               lex.putBackToken(z);
               Node second = parseStatements();
               // def <var> ( ) <statements> end
               return new Node ("funcDef", f.getDetails(), null, second, null);
            }
         }
         else {
            lex.putBackToken(x);
            Node first = parseParams();
            if (lex.getNextToken().toString().equals("end")) {
               // def <var> ( <params> ) end
               return new Node("funcDef", f.getDetails(), first, null, null);
            }
            else {
               // lex.putBackToken(x);
               Node second = parseStatements();
               // def <var> ( <params> ) <statements> end
               return new Node("funcDef", f.getDetails(), first, second, null);
            }
         }
      }

      // System.exit(1);
      return null;
   }

   // <params> -> <var> | <var> , <params>
   private Node parseParams(){
      System.out.println("-----> parsing <params>");

       Token t = lex.getNextToken();
       Node first = new Node("var", t.getDetails(), null, null, null);

       //look ahead to see if there are more params
       Token token = lex.getNextToken();

       if ( token.isKind( "var")) {
           // lex.putBackToken( token );
           Node second = parseParams();
           return new Node( "params", first, second, null );
       }
       else if ( token.matches( "single", ",")) {
           lex.getNextToken();
           Node second = parseParams();
           return new Node( "params", first, second, null );
       }
       else {
           // lex.putBackToken( t );
           lex.putBackToken( token );
           return new Node( "params", first, null, null );
       }
      // System.exit(1);
      // return null;
   } // <params>


   // <statements> -> <statement> |
   //                 <statement> <statements>
   private Node parseStatements() {
      System.out.println("-----> parsing <statements>:");

      Node first = parseStatement();

      // look ahead to see if there are more statement's
      Token token = lex.getNextToken();

      if ( token.isKind("eof") || token.matches("var","end") || token.matches("var", "else") ) {
         return new Node( "stmts", first, null, null );
      }
      else {
         lex.putBackToken( token );
         Node second = parseStatements();
         return new Node( "stmts", first, second, null );
      }
   } // <statements>


   // <statement> ->  <string> |
   //                 <var> = <expr> |
   //                 <funcCall> |
   //                 if <expr> else end |
   //                 if <expr> else <statements> end |
   //                 if <expr> <statements> else end |
   //                 if <expr> <statements> else <statements> end |
   //                 return <expr>
   private Node parseStatement() {
      System.out.println("-----> parsing <statement>:");

      Token token = lex.getNextToken();

      // ---------------->>>  print <string>  or  print <expr>
      if ( token.isKind("print") ) {
         token = lex.getNextToken();
         Token tok = lex.getNextToken();
         if ( token.matches("single", "(") && tok.isKind("exprs") ) {// print <string>
            lex.putBackToken( token );
            Node first = parseExpr();
            return new Node( "prtexp", first, null, null );
         }
         // ---------------->>>  newline
      }
      else if ( token.isKind("string") ) {
         return new Node( "prtstr", token.getDetails(), null, null, null );
      }
      else if ( token.isKind("newline") ) {
         return new Node( "nl", null, null, null );
      }
      // --------------->>>   <var> = <expr>
      else if(token.isKind("single")){
         Node first = parseExpr();
         return new Node("statements", token.getDetails(), first, null, null);
      }
      else if ( token.isKind("var") ) {
         String varName = token.getDetails();
         token = lex.getNextToken();
         if(token.matches("single", "(")){
            Node first = parseFuncCall();
            return new Node("statement", first, null, null);
         }
         else {
            errorCheck(token, "single", "=");
            Node first = parseExpr();
            return new Node("sto", varName, first, null, null);
         }
      }

      // --------------->>>   <funcCall>
      else if (token.isKind("funcCall")) {} //dont think we need this

      // --------------->>>   if/else
      else if (token.matches("if", "if")) {
         Token r = lex.getNextToken();
         errorCheck(r, "single", "(");
         Node first = parseExpr();
         Token s = lex.getNextToken();
         errorCheck(s, "single", ")");
         Token t = lex.getNextToken();
         if (t.matches("else", "else")) {
            t = lex.getNextToken();

            // --------------->>>   if <expr> else end
            if (t.matches("var", "end")) {
               return new Node("ifelse", first, null, null);
            }

            // --------------->>>   if <expr> else <statements> end
            else if (t.matches("var", "statements")) {
               Node second = parseStatements();
               t = lex.getNextToken();
               if (t.matches("var", "end")) {
                  return new Node("ifelse", first, second, null);
               }
            }

         }
         else if (token.isKind("statements")) {
            Node second = parseStatements();
            t = lex.getNextToken();
            if (t.isKind("else")) {
               t = lex.getNextToken();

               // --------------->>>   if <expr> <statements> else end
               if (t.isKind("end")) {
                  return new Node("else", first, second, null);
               }

               // --------------->>>   if <expr> <statements> else <statements> end
               else if (t.isKind("statements")) {
                  Node third = parseStatements();
                  t = lex.getNextToken();
                  if (t.isKind("end")) {
                     return new Node("ifelse", first, second, third);
                  }
               }

            }
         }
         else {
            System.exit(1);
            return null;
         }
      }

      // --------------->>>   return <expr>
      else if (token.isKind("return")) {
         Node first = parseExpr();
         return new Node("return", first, null, null);
      }
      else {
         System.out.println("Token " + token +
                 " can't begin a statement");
         System.exit(1);
         return null;
      }
      return null;
   } // <statement>

   private Node parseExpr() {
      System.out.println("-----> parsing <expr>");

      Node first = parseTerm();

      // look ahead to see if there's an addop
      Token token = lex.getNextToken();

      if ( token.matches("single", "+") ||
              token.matches("single", "-")
              ) {
         Node second = parseExpr();
         return new Node( token.getDetails(), first, second, null );
      }
      else {// is just one term
         lex.putBackToken( token );
         return first;
      }

   }// <expr>

   private Node parseTerm() {
      System.out.println("-----> parsing <term>");

      Node first = parseFactor();

      // look ahead to see if there's a multop
      Token token = lex.getNextToken();

      if ( token.matches("single", "*") ||
              token.matches("single", "/")
              ) {
         Node second = parseTerm();
         return new Node( token.getDetails(), first, second, null );
      }
      else {// is just one factor
         lex.putBackToken( token );
         return first;
      }

   }// <term>

   private Node parseFactor() {
      System.out.println("-----> parsing <factor>");

      Token token = lex.getNextToken();

      if ( token.isKind("num") ) {
         return new Node("num", token.getDetails(), null, null, null );
      }
      else if ( token.isKind("var") ) {
         return new Node("var", token.getDetails(), null, null, null );
      }
      else if ( token.matches("single","(") ) {
         Node first = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );
         return first;
      }
      else if ( token.isKind("bif0") ) {
         String bifName = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "(" );
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );

         return new Node( bifName, null, null, null );
      }
      else if ( token.isKind("bif1") ) {
         String bifName = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "(" );
         Node first = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );

         return new Node( bifName, first, null, null );
      }
      else if ( token.isKind("bif2") ) {
         String bifName = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "(" );
         Node first = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", "," );
         Node second = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );

         return new Node( bifName, first, second, null );
      }
      else if ( token.matches("single","-") ) {
         Node first = parseFactor();
         return new Node("opp", first, null, null );
      }
      else {
         System.out.println("Can't have factor starting with " + token );
         System.exit(1);
         return null;
      }

   }// <factor>

   // check whether token is correct kind
   private void errorCheck( Token token, String kind ) {
      if( ! token.isKind( kind ) ) {
         System.out.println("Error:  expected " + token +
                 " to be of kind " + kind );
         System.exit(1);
      }
   }

   // check whether token is correct kind and details
   private void errorCheck( Token token, String kind, String details ) {
      if( ! token.isKind( kind ) ||
              ! token.getDetails().equals( details ) ) {
         System.out.println("Error:  expected " + token +
                 " to be kind=" + kind +
                 " and details=" + details );
         System.exit(1);
      }
   }

}