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

   public Node parseProgram() {

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

   private Node parseFuncCall(){
      Token t = lex.getNextToken();
      if( lex.getNextToken().getKind().equals("var")) {
         if (lex.getNextToken().getDetails().equals("(")) {
            if (lex.getNextToken().getDetails().equals(")")) {
               return new Node ("funcCall", t.getDetails(),null, null, null);
            }

            else {
               lex.putBackToken(t);
               Node second = parseArgs();
               return new Node("funcCall", t.getDetails(), null, second, null);

            }
         }
      }
      // System.exit(1);
      return null;
   }

   private Node parseArgs(){
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

   private Node parseFuncDefs(){

      Node first = parseFuncDef();

      Token token = lex.getNextToken();

      if( token.isKind( "eof" ) ){
         return new Node( "funcDefs", first, null, null );
      }
      else{
         lex.putBackToken( token );
         Node second = parseFuncDefs();
         return new Node( "funcDefs", first, second, null );
      }

   }

   private Node parseFuncDef(){

      Token t = lex.getNextToken();
      Token f = lex.getNextToken();
      Token y = lex.getNextToken();
      Token x = lex.getNextToken();
      if(t.getDetails().equals("def") && f.isKind("var")) {
         if (y.getDetails().equals("(") && x.getDetails().equals(")"))
         {
            if (lex.getNextToken().toString().equals("end")) {
               return new Node("funcDef", t.getDetails(), null, null, null);
            } else {
               Node second = parseStatements();
               return new Node ("funcDef", f.getDetails(), null, second, null);
            }
         } else {
            lex.putBackToken(x);
            Node first = parseParams();
            if (lex.getNextToken().toString().equals("end")) {
               return new Node("funcDef", t.getDetails(), first, null, null);
            } else {
               lex.putBackToken(x);
               Node second = parseStatements();
               return new Node("funcDef", t.getDetails(), first, second, null);
            }
         }
      }

      // System.exit(1);
      return null;
   }


   //<params> -> <var> | <var> , <params>
   private Node parseParams(){
      System.out.println("-----> parsing <params>");

      Token t = lex.getNextToken();

      if ( t.isKind("var") ) {
         Node first = new Node("var", t.getDetails(), null, null, null);
         Token r = lex.getNextToken();
         if (r.matches("single", ")")) {
            return new Node("params", first, null, null);
         }
         else if (t.matches("single", ",")) {
            Node second = parseParams();
            return new Node("params", first, second, null);
         }
      }
      System.exit(1);
      return null;
   }

   private Node parseStatements() {
      System.out.println("-----> parsing <statements>:");

      Node first = parseStatement();

      // look ahead to see if there are more statement's
      Token token = lex.getNextToken();

      if ( token.isKind("eof") || token.isKind("end") || token.isKind("else") ) {
         return new Node( "stmts", first, null, null );
      }
      else {
         lex.putBackToken( token );
         Node second = parseStatements();
         return new Node( "stmts", first, second, null );
      }
   }// <statements>

   private Node parseStatement() {
      System.out.println("-----> parsing <statement>:");

      Token token = lex.getNextToken();

      // ---------------->>>  print <string>  or   print <expr>
      if ( token.isKind("print") ) {
         token = lex.getNextToken();

         if ( token.isKind("string") ) {// print <string>
            return new Node( "prtstr", token.getDetails(),
                    null, null, null );
         }
         else {// must be first token in <expr>
            // put back the token we looked ahead at
            lex.putBackToken( token );
            Node first = parseExpr();
            return new Node( "prtexp", first, null, null );
         }
         // ---------------->>>  newline
      }
      else if ( token.isKind("newline") ) {
         return new Node( "nl", null, null, null );
      }
      // --------------->>>   <var> = <expr>
      else if ( token.isKind("var") ) {
         String varName = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "=" );
         Node first = parseExpr();
         return new Node( "sto", varName, first, null, null );
      }

      // --------------->>>   <funcCall>
      else if (token.isKind("funcCall")) {}

      // --------------->>>   if/else
      else if (token.isKind("if")) {
         Node first = parseExpr();
         Token t = lex.getNextToken();
         if (t.isKind("else")) {
            t = lex.getNextToken();

            // --------------->>>   if <expr> else end
            if (t.isKind("end")) {
               return new Node("ifelse", first, null, null);
            }

            // --------------->>>   if <expr> else <statements> end
            else if (t.isKind("statements")) {
               Node second = parseStatements();
               t = lex.getNextToken();
               if (t.isKind("end")) {
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
                  return new Node("ifelse", first, second, null);
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
         parseExpr();
      }
      else {
         System.out.println("Token " + token +
                 " can't begin a statement");
         System.exit(1);
         return null;
      }
      return null;
   }// <statement>

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