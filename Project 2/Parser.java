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
      Token v = lex.getNextToken();
      Token w = lex.getNextToken();
      Token x = lex.getNextToken();
      Token y = lex.getNextToken();
      Token z = lex.getNextToken();
      if ( v.isKind("eof") || w.isKind("eof") || x.isKind("eof") || y.isKind("eof") || z.isKind("eof") ) {
         return new Node("funcDef", null, null, null);
      }
//      System.out.println("v = " + t.getDetails());
//      System.out.println("w = " + f.getDetails());
//      System.out.println("x = " + x.getDetails());
//      System.out.println("y = " + y.getDetails());
//      System.out.println("z = " + z.getDetails());

      if (v.getDetails().equals("def")) {
         if (w.isKind("var")) {
//             return new Node("funcDef", w.getDetails(), null, null, null);
             if (y.matches("single", ")")) {
                // def <var> ( ) end |
                if (z.matches("var", "end")) {
                   return new Node("funcDef", w.getDetails(), null, null, null);
                }
                // def <var> ( ) <statements> end
                else {
                   lex.putBackToken(z);
                   Node first = parseStatements();
                   return new Node("funcDef", w.getDetails(), first, null, null);
                }
             }
             else { // <params>
                lex.putBackToken(z);
                lex.putBackToken(y);
                // def <var> ( <params> ) end |
                Node first = parseParams();
                Token a = lex.getNextToken();
                if (a.matches("var", "end")) {
                   return new Node("funcDef", w.getDetails(), first, null ,null);
                }
                // def <var> ( <params> ) <statements> end
                else { // <statements>
//                   Node first = parseParams();
                   lex.putBackToken(a);
                   Node second = parseStatements();
                   return new Node("funcDef", w.getDetails(), first, second ,null);
                }
             }
         }
         else { return null; }
      }
      else { return null; }

//      if (v.getDetails().equals("def") && w.isKind("var")) {
//         if (y.getDetails().equals("(") && x.getDetails().equals(")")) {
//            if (z.getDetails().equals("end")) {
//               // def <var> ( ) end
//               return new Node("funcDef", w.getDetails(), null, null, null);
//            }
//            else {
//               lex.putBackToken(z);
//               Node second = parseStatements();
//               // def <var> ( ) <statements> end
//               return new Node ("funcDef", w.getDetails(), null, second, null);
//            }
//         }
//         else {
//            lex.putBackToken(z);
//            lex.putBackToken(x);
//            Node first = parseParams();
//            if (lex.getNextToken().toString().equals("end")) {
//               // def <var> ( <params> ) end
//               return new Node("funcDef", w.getDetails(), first, null, null);
//            }
//            else {
//               // lex.putBackToken(x);
//               Node second = parseStatements();
//               // def <var> ( <params> ) <statements> end
//               return new Node("funcDef", w.getDetails(), first, second, null);
//            }
//         }
//      }
//
//      // System.exit(1);
//      return null;
   }

   // <params> -> <var> | <var> , <params>
   private Node parseParams(){
      System.out.println("-----> parsing <params>");

      Token s = lex.getNextToken();
      Node first = new Node("var", s.getDetails(), null, null, null);
      Token t = lex.getNextToken();
      if (t.matches("single", ")")){
         return new Node("params",  first, null, null);
      }
     else if (t.matches("single", ",")){
         Node second = parseParams();
         return new Node("params", first, second, null);
      }
      return null;
   } // <params>


   // <statements> -> <statement> |
   //                 <statement> <statements>
   private Node parseStatements() {
      System.out.println("-----> parsing <statements>:");

      Node first = parseStatement();

      // look ahead to see if there are more statement's
      Token token = lex.getNextToken();
      if ( token.matches("var","else")) { lex.putBackToken(token); return new Node("stmnts", first, null, null); }

      if ( token.isKind("eof") || token.matches("var","end")) {
//      if ( token.isKind("eof") || token.matches("var","end") || token.matches("var", "else") ) {
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

      }
      else if ( token.isKind("string") ) {
         return new Node( "prtstr", token.getDetails(), null, null, null );
      }

      // ---------------->>>  newline
      else if ( token.isKind("newline") ) {
         return new Node( "nl", null, null, null );
      }

      // --------------->>>   <var> = <expr>
//      else if(token.isKind("single")){
//         Node first = parseExpr();
//         return new Node("statements", token.getDetails(), first, null, null);
//      }

      // --------------->>>   if/else
      //                 if <expr> else end |
      //                 if <expr> else <statements> end |
      //                 if <expr> <statements> else end |
      //                 if <expr> <statements> else <statements> end |
      else if (token.isKind("var")){
         if (token.matches("var", "return")) {
            Node first = parseExpr();
            return new Node("return", first, null, null);
         }
         else if (token.matches("var", "if")) {
            Node first = parseExpr();
            Token t = lex.getNextToken();
            if (t.matches("var", "else")) {
               Token u = lex.getNextToken();
               //                 if <expr> else end |
               if (u.matches("var", "end")) {
                  return new Node("ifelse", first, null, null);
               }
               //                 if <expr> else <statements> end |
               else {
                  Node third = parseStatements();
                  return new Node("ifelse", first, null, third);
               }
            }
            //                 if <expr> <statements> else end |
            //                 if <expr> <statements> else <statements> end |
            else {
               lex.putBackToken(t);
               Node second = parseStatements();
               Token v = lex.getNextToken();
               if (v.matches("var", "else")) {
                  Token u = lex.getNextToken();
                  if (u.matches("var", "end")) {
                     return new Node("ifelse", first, second, null);
                  }
                  else {
                     lex.putBackToken(u);
                     Node third = parseStatements();
                     return new Node("ifelse", first, second, third);
                  }
               }
            }
         }
         else {
            lex.putBackToken(token);
            Node one = parseExpr();
            return new Node("sto", token.getDetails(), one, null, null);
         }
      }
      return null;
   } // <statement>


   // <expr> -> <term> | <term> + <expr> | <term> - <expr>
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