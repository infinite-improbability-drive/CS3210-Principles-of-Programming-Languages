import java.io.*;
import java.util.*;

public class VPL
{
  static String fileName;
  static Scanner keys;

  static int max;
  static int[] mem;
  static int ip, bp, sp, rv, hp, numPassed, gp, rip, rbp;
  static int step;

  public static void main(String[] args) throws Exception {

    keys = new Scanner( System.in );

    if( args.length != 2 ) {
      System.out.println("Usage: java VPL <vpl program> <memory size>" );
      System.exit(1);
    }

    fileName = args[0];

    max = Integer.parseInt( args[1] );
    mem = new int[max];

    // load the program into the front part of
    // memory
    Scanner input = new Scanner( new File( fileName ) );
    String line;
    StringTokenizer st;
    int opcode;

    ArrayList<IntPair> labels, holes;
    labels = new ArrayList<IntPair>();
    holes = new ArrayList<IntPair>();
    int label;

    // load the code

    int k=0;
    while ( input.hasNextLine() ) {
      line = input.nextLine();
      System.out.println("parsing line [" + line + "]");
      if( line != null )
      {// extract any tokens
        st = new StringTokenizer( line );
        if( st.countTokens() > 0 )
        {// have a token, so must be an instruction (as opposed to empty line)

          opcode = Integer.parseInt(st.nextToken());

          // load the instruction into memory:

          if( opcode == labelCode )
          {// note index that comes where label would go
            label = Integer.parseInt(st.nextToken());
            labels.add( new IntPair( label, k ) );
          }
          else if( opcode == noopCode ){
          }
          else
          {// opcode actually gets stored
            mem[k] = opcode;  k++;
 
            if( opcode == callCode || opcode == jumpCode ||
                opcode == condJumpCode )
            {// note the hole immediately after the opcode to be filled in later
              label = Integer.parseInt( st.nextToken() );
              mem[k] = label;  holes.add( new IntPair( k, label ) );
              ++k;
            }

            // load correct number of arguments (following label, if any):
            for( int j=0; j<numArgs(opcode); ++j )
            {
              mem[k] = Integer.parseInt(st.nextToken());
              ++k;
            }

          }// not a label

        }// have a token, so must be an instruction
      }// have a line
    }// loop to load code
    
    //System.out.println("after first scan:");
    //showMem( 0, k-1 );

    // replace labels
    // fill in all the holes:
    int index;
    for( int m=0; m<holes.size(); ++m )
    {
      label = holes.get(m).second;
      index = -1;
      for( int n=0; n<labels.size(); ++n )
        if( labels.get(n).first == label )
          index = labels.get(n).second;
      mem[ holes.get(m).first ] = index;
    }

    System.out.println("after replacing labels:");
    showMem( 0, k-1 );

    // initialize registers:
    bp = k;  sp = k+2;  ip = 0;  rv = -1;  hp = max;
    numPassed = 0;
    
    int codeEnd = bp-1;

    System.out.println("Code is " );
    showMem( 0, codeEnd );

    gp = codeEnd + 1;

    // start execution:
    boolean done = false;
    int op, a=0, b=0, c=0;
    int actualNumArgs;

    int step = 0;

    int oldIp = 0;

    // repeatedly execute a single operation
    // *****************************************************************

    do {

      // show details of current step
      System.out.println("--------------------------");
      System.out.println("Step of execution with IP = " + ip + " opcode: " +
          mem[ip] + 
         " bp = " + bp + " sp = " + sp + " hp = " + hp + " rv = " + rv + " gp = " + gp );
      System.out.println(" chunk of code: " +  mem[ip] + " " +
                            mem[ip+1] + " " + mem[ip+2] + " " + mem[ip+3] );
      System.out.println("--------------------------");
      System.out.println( " memory from " + (codeEnd+1) + " up: " );
      showMem( codeEnd+1, sp+3 );
      System.out.println("hit <enter> to go on" );
      keys.nextLine();


      oldIp = ip;

      op = mem[ ip ];  ip++;
      // extract the args into a, b, c for convenience:
      a = -1;  b = -2;  c = -3;

      // numArgs is wrong for these guys, need one more!
      if( op == callCode || op == jumpCode ||
                op == condJumpCode )
      {
        actualNumArgs = numArgs( op ) + 1;
      }
      else
        actualNumArgs = numArgs( op );

      if( actualNumArgs == 1 )
      {  a = mem[ ip ];  ip++;  }
      else if( actualNumArgs == 2 )
      {  a = mem[ ip ];  ip++;  b = mem[ ip ]; ip++; }
      else if( actualNumArgs == 3 )
      {  a = mem[ ip ];  ip++;  b = mem[ ip ]; ip++; c = mem[ ip ]; ip++; }
 
      // implement all operations here:
      // ********************************************

      // put your work right here!

      if ( op == callCode ) {             // 2 call
          rip = ip;
          rbp = bp;
          bp = sp;
          sp = sp+2+numPassed;
          ip = a;
          numPassed=0;
      }
      else if ( op == passCode ) {          // 3 pass
          mem[ sp + 2 + numPassed]= a;
          numPassed++;
      }
      else if ( op == allocCode ) {			// 4 locals         *tested*
         sp = sp + a;
      }
      else if ( op == returnCode) {			// 5 return
          ip = mem[rip];
          bp = mem[rbp];
          sp = bp + 2;
      }
      else if ( op == getRetvalCode ) {		// 6 get retval
         mem[ a ] = rv;
      }
      else if ( op == jumpCode ) {			// 7 jump
         ip = a;
      }
      else if ( op == condJumpCode ) {		// 8 cond
         if ( mem[ a ] != 0 ) {
		    ip = b;
	     }
         else {
		    ip++;
	     }
      }
      else if ( op == addCode ) {			// 9 add            *tested*
        mem[ bp+2 + a ] = mem[ bp+2 + b ] + mem[ bp+2 + c ];
      }
      else if ( op == subCode ) {			// 10 subtract      *tested*
        mem[ bp+2 + a ] = mem[ bp+2 + b ] - mem[ bp+2 + c ];
      }
      else if ( op == multCode ) {			// 11 multiply      *tested*
        mem[ bp+2 + a ] = mem[ bp+2 + b ] * mem[ bp+2 + c ];
      }
      else if ( op == divCode ) {			// 12 divide        *tested*
        mem[ bp+2 + a ] = mem[ bp+2 + b ] / mem[ bp+2 + c ];
      }
      else if ( op == remCode ) {			// 13 remainder     *tested*
        mem[ bp+2 + a ] = mem[ bp+2 + b ] % mem[ bp+2 + c ];
      }
      else if ( op == equalCode ) {			// 14 equal         *tested*
        if (mem[ bp+2 + b ] == mem[ bp+2 + c ]) {
          mem[ bp+2 + a ] = 1;
        }
        else {
          mem[ bp+2 + a ] = 0;
        }
      }
      else if ( op == notEqualCode ) {		// 15 not equal     *tested*
        if (mem[ bp+2 + b ] != mem[ bp+2 + c ]) {
          mem[ bp+2 + a ] = 1;
        }
        else {
          mem[ bp+2 + a ] = 0;
        }
      }
      else if ( op == lessCode ) {			// 16 less than     *tested*
        if (mem[ bp+2 + b ] < mem[ bp+2 + c ]) {
          mem[ bp+2 + a ] = 1;
        }
        else {
          mem[ bp+2 + a ] = 0;
        }
      }
      else if ( op == lessEqualCode ) {		// 17 less than or equal    *tested*
        if (mem[ bp+2 + b ] <= mem[ bp+2 + c ]) {
          mem[ bp+2 + a ] = 1;
        }
        else {
          mem[ bp+2 + a ] = 0;
        }
      }
      else if ( op == andCode ) {			// 18 and                   *tested*
        if ((mem[ bp+2 + b ] == 1) && (mem[ bp+2 + c ] == 1)) {
          mem[ bp+2 + a ] = 1;
        }
        else {
          mem[ bp+2 + a ] = 0;
        }
      }
      else if ( op == orCode ) {			// 19 or                    *tested*
        if ((mem[ bp+2 + b ] == 1) || (mem[ bp+2 + c ] == 1)) {
          mem[ bp+2 + a ] = 1;
        }
        else {
          mem[ bp+2 + a ] = 0;
        }
      }
      else if ( op == notCode ) {			// 20 not
        if (mem[ bp+2 + b ] == 0) {
          mem[ bp+2 + a ] = 1;
        }
        else {
          mem[ bp+2 + a ] = 0;
        }
      }
      else if ( op == oppCode ) {			// 21 opposite
         mem[ bp+2 + a ] = - mem[ bp+2 + b ];
      }
      else if ( op == litCode ) {			// 22 literal
         mem[ bp+2 + a ] = b;
      }
      else if ( op == copyCode ) {			// 23 copy
         mem[ bp+2 + a ] = mem[ bp+2 + b ];
      }
      else if ( op == getCode ) {			// 24 get
         mem[ bp+2 + a ] = mem[ mem[ bp+2 + b ] + mem[ bp+2 + c ] ];
      }
      else if ( op == putCode ) {			// 25 put
          mem[ mem[ bp+2 + a ] + mem[ bp+2 + b ] ] = mem[ bp+2 + c ];
      }
      else if ( op == haltCode ) {			// 26 halt
        System.out.println("Program End");
        System.exit(1);
      }
      else if ( op == inputCode ) {			// 27 input         *tested* - no fancy error checking here
         System.out.print("? ");
          try {
              mem[ bp+2 + a ] = keys.nextInt();
          }

          catch (InputMismatchException e) {
                System.out.println("Error - invalid input");
          }
      }
      else if ( op == outputCode ) {		// 28 output
        System.out.println(mem[ bp+2 + a ]);
      }
      else if ( op == newlineCode ) {		// 29 newline
        System.out.println();
      }
      else if ( op == symbolCode ) {		// 30 symbol
          if (( mem[ bp+2 + a] >= 32 ) && ( mem[ bp+2 + a] <= 126 )) {
              System.out.print((char) mem[ bp+2 + a ]);
          }
      }
      else if ( op == newCode ) {			// 31 new
        int m = mem[ bp+2 + b ];
        hp = hp - m;
        mem[ bp+2 + a] = hp;

      }
      else if ( op == allocGlobalCode ) { 	// 32 allocate global space
        gp = codeEnd + 1;
        bp = codeEnd + 1 + a;
        sp = bp + 2;
      }
      else if ( op == toGlobalCode ) {		// 33 copy to global
        mem[ gp + a ] = mem[ bp+2 + b];
      }
      else if ( op == fromGlobalCode ) { 	// 34 copy from global
        mem[ bp+2 + a] = mem[ gp + b];
      }
      else if ( op == debugCode ) {			// 35 debug

      }

      else
      {
        System.out.println("Fatal error: unknown opcode [" + op + "]" );
        System.exit(1);
      }
       
      step++;

    }while( !done );
    

  }// main

  // use symbolic names for all opcodes:

  // op to produce comment
  private static final int noopCode 		= 0;	// no op

  // ops involved with registers
  private static final int labelCode 		= 1;	// label
  private static final int callCode 		= 2;	// call
  private static final int passCode 		= 3;	// pass
  private static final int allocCode 		= 4;	// locals
  private static final int returnCode 		= 5;  	// return 			a means "return and put
																		// copy of value stored in cell a in register rv
  private static final int getRetvalCode 	= 6;	// get retval - op a means "copy rv into cell a"
  private static final int jumpCode 		= 7;	// jump
  private static final int condJumpCode 	= 8;	// cond

  // arithmetic ops
  private static final int addCode 			= 9;	// add
  private static final int subCode 			= 10;	// subtract
  private static final int multCode 		= 11;	// multiply
  private static final int divCode 			= 12;	// divide
  private static final int remCode 			= 13;	// remainder
  private static final int equalCode 		= 14;	// equal
  private static final int notEqualCode 	= 15;	// not equal
  private static final int lessCode 		= 16;	// less than
  private static final int lessEqualCode 	= 17;	// less than or equal
  private static final int andCode 			= 18;	// and
  private static final int orCode 			= 19;	// or 			
  private static final int notCode 			= 20;	// not
  private static final int oppCode 			= 21;	// opposite
  
  // ops involving transfer of data
  private static final int litCode 			= 22;	// litCode a b means "cell a gets b"
  private static final int copyCode 		= 23;	// copy a b means "cell a gets cell b"
  private static final int getCode 			= 24;	// op a b means "cell a gets
													// contents of cell whose 
													// index is stored in b"
  private static final int putCode = 25;  			// op a b means "put contents
     // of cell b in cell whose offset is stored in cell a"

  // system-level ops:
  private static final int haltCode 		= 26;	// halt
  private static final int inputCode 		= 27;	// input
  private static final int outputCode 		= 28;	// output
  private static final int newlineCode 		= 29;	// newline
  private static final int symbolCode 		= 30;	// symbol
  private static final int newCode 			= 31;	// new
  
  // global variable ops:
  private static final int allocGlobalCode 	= 32;	// allocate global space
  private static final int toGlobalCode 	= 33;	// copy to global
  private static final int fromGlobalCode 	= 34;	// copy from global

  // debug ops:
  private static final int debugCode 		= 35;

  // return the number of arguments after the opcode,
  // except ops that have a label return number of arguments
  // after the label, which always comes immediately after 
  // the opcode
  private static int numArgs( int opcode )
  {
    // highlight specially behaving operations
    if( opcode == labelCode ) 		  return 1;  // not used
    else if( opcode == jumpCode ) 	  return 0;  // jump label
    else if( opcode == condJumpCode ) return 1;  // condJump label expr
    else if( opcode == callCode ) 	  return 0;  // call label

    // for all other ops, lump by count:

    else if( opcode==noopCode ||
             opcode==haltCode ||
             opcode==newlineCode ||
             opcode==debugCode
           ) 
      return 0;  // op

    else if( opcode==passCode || opcode==allocCode || 
             opcode==returnCode || opcode==getRetvalCode || 
             opcode==inputCode || 
             opcode==outputCode || opcode==symbolCode ||
             opcode==allocGlobalCode
           )  
      return 1;  // op arg1

    else if( opcode==notCode || opcode==oppCode || 
             opcode==litCode || opcode==copyCode || opcode==newCode ||
             opcode==toGlobalCode || opcode==fromGlobalCode

           ) 
      return 2;  // op arg1 arg2

    else if( opcode==addCode ||  opcode==subCode || opcode==multCode ||
             opcode==divCode ||  opcode==remCode || opcode==equalCode ||
             opcode==notEqualCode ||  opcode==lessCode || 
             opcode==lessEqualCode || opcode==andCode ||
             opcode==orCode || opcode==getCode || opcode==putCode
           )
      return 3;		// op arg1 arg2 arg3
   
    else
    {
      System.out.println("Fatal error: unknown opcode [" + opcode + "]" );
      System.exit(1);
      return -1;
    }

  }// numArgs

  private static void showMem( int a, int b )
  {
    for( int k=a; k<=b; ++k )
    {
      System.out.println( k + ": " + mem[k] );
    }
  }// showMem

}// VPL
