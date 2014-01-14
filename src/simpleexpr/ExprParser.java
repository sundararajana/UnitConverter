// $ANTLR 3.5.1 Expr.g 2014-01-14 21:03:58


/*
 * Copyright (C) 2014 Sundararajan Athijegannathan
 * 
 * This file is part of UnitConverter.
 * 
 * UnitConverter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnitConverter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnitConverter.  If not, see <http://www.gnu.org/licenses/>.
 */

package simpleexpr;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.io.PrintStream;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class ExprParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "NUMBER", "WS", "'('", "')'", 
		"'*'", "'**'", "'+'", "','", "'-'", "'/'", "'='", "'^'", "'delete'"
	};
	public static final int EOF=-1;
	public static final int T__7=7;
	public static final int T__8=8;
	public static final int T__9=9;
	public static final int T__10=10;
	public static final int T__11=11;
	public static final int T__12=12;
	public static final int T__13=13;
	public static final int T__14=14;
	public static final int T__15=15;
	public static final int T__16=16;
	public static final int T__17=17;
	public static final int ID=4;
	public static final int NUMBER=5;
	public static final int WS=6;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public ExprParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public ExprParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return ExprParser.tokenNames; }
	@Override public String getGrammarFileName() { return "Expr.g"; }



	  // math constants
	  private static final Map<String, Double> constants = new HashMap<String, Double>();
	  static {
	      constants.put("pi", Math.PI);
	      constants.put("e", Math.E);
	  }

	  // variables
	  private Map<String, Double> variables = new HashMap<String, Double>();

	  // throw exception on error display
	  public void displayRecognitionError(String[] tokenNames,
	                                      RecognitionException e) {
	    String hdr = getErrorHeader(e);
	    String msg = getErrorMessage(e, tokenNames);
	    throw new RuntimeException(hdr + ": " + msg);
	  }

	  public static double eval(String expression) throws Exception {
	    return eval(expression, new HashMap<String, Double>()); 
	  }

	  public static double eval(String expression, Map<String, Double> vars) throws Exception {
	    ANTLRStringStream in = new ANTLRStringStream(expression);
	    ExprLexer lexer = new ExprLexer(in);
	    CommonTokenStream tokens = new CommonTokenStream(lexer);
	    ExprParser parser = new ExprParser(tokens);
	    parser.variables = vars;
	    return parser.parse(); 
	  }

	  // get variable value
	  private double getVarValue(String ident) {
	    if (variables.containsKey(ident)) {
	      return variables.get(ident);
	    }

	    if (constants.containsKey(ident)) {
	      return constants.get(ident);
	    }

	    throw new RuntimeException("undefined variable: " + ident);
	  }

	  // delete a variable by 'delete' unary operator
	  private double deleteVar(String ident) {
	    if (variables.containsKey(ident)) {
	      double val = variables.get(ident);
	      variables.remove(ident);
	      return val;
	    }
	    return 0.0;
	  }

	  // set a variable
	  private double setVarValue(String ident, double value) {
	    variables.put(ident, value);
	    return value;
	  }

	  private double callFunction(String funcName, List<Double> args) {
	    try {
	      final Method m;
	      // only single arg and double arg java.lang.Math methods!
	      switch (args.size()) {
	        case 1:
	          m = Math.class.getMethod(funcName, double.class);
	          break;
	        case 2:
	          m = Math.class.getMethod(funcName, double.class, double.class);
	          break;
	        default:
	          throw new RuntimeException("undefined function: " + funcName);
	      }
	     
	      return (Double)m.invoke(null, args.toArray()); 
	    } catch (NoSuchMethodException nsme) {
	      throw new RuntimeException("undefined function: " + funcName);
	    } catch (Exception e) {
	      if (e instanceof RuntimeException) {
	        throw (RuntimeException)e;
	      }
	      throw new RuntimeException(e);
	    }
	  }



	// $ANTLR start "parse"
	// Expr.g:183:1: parse returns [double value] : e= commaExpr ;
	public final double parse() throws RecognitionException {
		double value = 0.0;


		double e =0.0;

		try {
			// Expr.g:184:5: (e= commaExpr )
			// Expr.g:184:7: e= commaExpr
			{
			pushFollow(FOLLOW_commaExpr_in_parse63);
			e=commaExpr();
			state._fsp--;

			 value = e; 
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "parse"



	// $ANTLR start "commaExpr"
	// Expr.g:187:1: commaExpr returns [double value] : e1= assignExpr ( ',' e2= assignExpr )* ;
	public final double commaExpr() throws RecognitionException {
		double value = 0.0;


		double e1 =0.0;
		double e2 =0.0;

		try {
			// Expr.g:188:5: (e1= assignExpr ( ',' e2= assignExpr )* )
			// Expr.g:188:7: e1= assignExpr ( ',' e2= assignExpr )*
			{
			pushFollow(FOLLOW_assignExpr_in_commaExpr88);
			e1=assignExpr();
			state._fsp--;

			 value = e1; 
			// Expr.g:189:7: ( ',' e2= assignExpr )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==12) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// Expr.g:189:8: ',' e2= assignExpr
					{
					match(input,12,FOLLOW_12_in_commaExpr99); 
					pushFollow(FOLLOW_assignExpr_in_commaExpr103);
					e2=assignExpr();
					state._fsp--;

					 value = e2; 
					}
					break;

				default :
					break loop1;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "commaExpr"



	// $ANTLR start "assignExpr"
	// Expr.g:192:1: assignExpr returns [double value] : (e1= addExpr |i= ID '=' e2= assignExpr );
	public final double assignExpr() throws RecognitionException {
		double value = 0.0;


		Token i=null;
		double e1 =0.0;
		double e2 =0.0;

		try {
			// Expr.g:193:5: (e1= addExpr |i= ID '=' e2= assignExpr )
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==NUMBER||LA2_0==7||LA2_0==11||LA2_0==13||LA2_0==17) ) {
				alt2=1;
			}
			else if ( (LA2_0==ID) ) {
				int LA2_2 = input.LA(2);
				if ( (LA2_2==15) ) {
					alt2=2;
				}
				else if ( (LA2_2==EOF||(LA2_2 >= 7 && LA2_2 <= 14)||LA2_2==16) ) {
					alt2=1;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 2, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}

			switch (alt2) {
				case 1 :
					// Expr.g:193:7: e1= addExpr
					{
					pushFollow(FOLLOW_addExpr_in_assignExpr130);
					e1=addExpr();
					state._fsp--;

					 value = e1; 
					}
					break;
				case 2 :
					// Expr.g:194:7: i= ID '=' e2= assignExpr
					{
					i=(Token)match(input,ID,FOLLOW_ID_in_assignExpr142); 
					match(input,15,FOLLOW_15_in_assignExpr144); 
					pushFollow(FOLLOW_assignExpr_in_assignExpr148);
					e2=assignExpr();
					state._fsp--;

					 value = setVarValue((i!=null?i.getText():null), e2); 
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "assignExpr"



	// $ANTLR start "addExpr"
	// Expr.g:197:1: addExpr returns [double value] : m1= multExpr ( '+' m2= multExpr | '-' m2= multExpr )* ;
	public final double addExpr() throws RecognitionException {
		double value = 0.0;


		double m1 =0.0;
		double m2 =0.0;

		try {
			// Expr.g:198:5: (m1= multExpr ( '+' m2= multExpr | '-' m2= multExpr )* )
			// Expr.g:198:8: m1= multExpr ( '+' m2= multExpr | '-' m2= multExpr )*
			{
			pushFollow(FOLLOW_multExpr_in_addExpr174);
			m1=multExpr();
			state._fsp--;

			 value =  m1; 
			// Expr.g:199:7: ( '+' m2= multExpr | '-' m2= multExpr )*
			loop3:
			while (true) {
				int alt3=3;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==11) ) {
					alt3=1;
				}
				else if ( (LA3_0==13) ) {
					alt3=2;
				}

				switch (alt3) {
				case 1 :
					// Expr.g:199:9: '+' m2= multExpr
					{
					match(input,11,FOLLOW_11_in_addExpr192); 
					pushFollow(FOLLOW_multExpr_in_addExpr196);
					m2=multExpr();
					state._fsp--;

					 value += m2; 
					}
					break;
				case 2 :
					// Expr.g:200:9: '-' m2= multExpr
					{
					match(input,13,FOLLOW_13_in_addExpr209); 
					pushFollow(FOLLOW_multExpr_in_addExpr213);
					m2=multExpr();
					state._fsp--;

					 value -= m2; 
					}
					break;

				default :
					break loop3;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "addExpr"



	// $ANTLR start "multExpr"
	// Expr.g:204:1: multExpr returns [double value] : a1= expoExpr ( '*' a2= expoExpr | '/' a2= expoExpr )* ;
	public final double multExpr() throws RecognitionException {
		double value = 0.0;


		double a1 =0.0;
		double a2 =0.0;

		try {
			// Expr.g:205:5: (a1= expoExpr ( '*' a2= expoExpr | '/' a2= expoExpr )* )
			// Expr.g:205:7: a1= expoExpr ( '*' a2= expoExpr | '/' a2= expoExpr )*
			{
			pushFollow(FOLLOW_expoExpr_in_multExpr249);
			a1=expoExpr();
			state._fsp--;

			 value =  a1; 
			// Expr.g:206:7: ( '*' a2= expoExpr | '/' a2= expoExpr )*
			loop4:
			while (true) {
				int alt4=3;
				int LA4_0 = input.LA(1);
				if ( (LA4_0==9) ) {
					alt4=1;
				}
				else if ( (LA4_0==14) ) {
					alt4=2;
				}

				switch (alt4) {
				case 1 :
					// Expr.g:206:9: '*' a2= expoExpr
					{
					match(input,9,FOLLOW_9_in_multExpr267); 
					pushFollow(FOLLOW_expoExpr_in_multExpr271);
					a2=expoExpr();
					state._fsp--;

					 value *= a2; 
					}
					break;
				case 2 :
					// Expr.g:207:9: '/' a2= expoExpr
					{
					match(input,14,FOLLOW_14_in_multExpr284); 
					pushFollow(FOLLOW_expoExpr_in_multExpr288);
					a2=expoExpr();
					state._fsp--;

					 value /= a2; 
					}
					break;

				default :
					break loop4;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "multExpr"



	// $ANTLR start "expoExpr"
	// Expr.g:212:1: expoExpr returns [double value] : a1= unaryExpr ( '^' a2= unaryExpr )* ( '**' a2= unaryExpr )* ;
	public final double expoExpr() throws RecognitionException {
		double value = 0.0;


		double a1 =0.0;
		double a2 =0.0;

		try {
			// Expr.g:213:5: (a1= unaryExpr ( '^' a2= unaryExpr )* ( '**' a2= unaryExpr )* )
			// Expr.g:213:7: a1= unaryExpr ( '^' a2= unaryExpr )* ( '**' a2= unaryExpr )*
			{
			pushFollow(FOLLOW_unaryExpr_in_expoExpr325);
			a1=unaryExpr();
			state._fsp--;

			 value =  a1; 
			// Expr.g:214:7: ( '^' a2= unaryExpr )*
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( (LA5_0==16) ) {
					alt5=1;
				}

				switch (alt5) {
				case 1 :
					// Expr.g:214:8: '^' a2= unaryExpr
					{
					match(input,16,FOLLOW_16_in_expoExpr342); 
					pushFollow(FOLLOW_unaryExpr_in_expoExpr347);
					a2=unaryExpr();
					state._fsp--;

					 value = Math.pow(value,a2); 
					}
					break;

				default :
					break loop5;
				}
			}

			// Expr.g:215:7: ( '**' a2= unaryExpr )*
			loop6:
			while (true) {
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( (LA6_0==10) ) {
					alt6=1;
				}

				switch (alt6) {
				case 1 :
					// Expr.g:215:8: '**' a2= unaryExpr
					{
					match(input,10,FOLLOW_10_in_expoExpr360); 
					pushFollow(FOLLOW_unaryExpr_in_expoExpr364);
					a2=unaryExpr();
					state._fsp--;

					 value = Math.pow(value,a2); 
					}
					break;

				default :
					break loop6;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "expoExpr"



	// $ANTLR start "unaryExpr"
	// Expr.g:218:1: unaryExpr returns [double value] : (a1= atom | '-' e= unaryExpr | '+' e= unaryExpr | 'delete' i= ID );
	public final double unaryExpr() throws RecognitionException {
		double value = 0.0;


		Token i=null;
		double a1 =0.0;
		double e =0.0;

		try {
			// Expr.g:219:5: (a1= atom | '-' e= unaryExpr | '+' e= unaryExpr | 'delete' i= ID )
			int alt7=4;
			switch ( input.LA(1) ) {
			case ID:
			case NUMBER:
			case 7:
				{
				alt7=1;
				}
				break;
			case 13:
				{
				alt7=2;
				}
				break;
			case 11:
				{
				alt7=3;
				}
				break;
			case 17:
				{
				alt7=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 7, 0, input);
				throw nvae;
			}
			switch (alt7) {
				case 1 :
					// Expr.g:219:7: a1= atom
					{
					pushFollow(FOLLOW_atom_in_unaryExpr391);
					a1=atom();
					state._fsp--;

					 value = a1; 
					}
					break;
				case 2 :
					// Expr.g:220:7: '-' e= unaryExpr
					{
					match(input,13,FOLLOW_13_in_unaryExpr409); 
					pushFollow(FOLLOW_unaryExpr_in_unaryExpr413);
					e=unaryExpr();
					state._fsp--;

					 value = -e; 
					}
					break;
				case 3 :
					// Expr.g:221:7: '+' e= unaryExpr
					{
					match(input,11,FOLLOW_11_in_unaryExpr423); 
					pushFollow(FOLLOW_unaryExpr_in_unaryExpr427);
					e=unaryExpr();
					state._fsp--;

					 value = e;  
					}
					break;
				case 4 :
					// Expr.g:222:7: 'delete' i= ID
					{
					match(input,17,FOLLOW_17_in_unaryExpr437); 
					i=(Token)match(input,ID,FOLLOW_ID_in_unaryExpr441); 
					 value = deleteVar((i!=null?i.getText():null)); 
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "unaryExpr"



	// $ANTLR start "argList"
	// Expr.g:225:1: argList returns [List<Double> value] : '(' e= assignExpr ( ',' e2= assignExpr )* ')' ;
	public final List<Double> argList() throws RecognitionException {
		List<Double> value = null;


		double e =0.0;
		double e2 =0.0;

		try {
			// Expr.g:226:5: ( '(' e= assignExpr ( ',' e2= assignExpr )* ')' )
			// Expr.g:226:7: '(' e= assignExpr ( ',' e2= assignExpr )* ')'
			{
			 List<Double> args = new ArrayList<Double>(); 
			match(input,7,FOLLOW_7_in_argList474); 
			pushFollow(FOLLOW_assignExpr_in_argList485);
			e=assignExpr();
			state._fsp--;

			 args.add(e); 
			// Expr.g:229:7: ( ',' e2= assignExpr )*
			loop8:
			while (true) {
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( (LA8_0==12) ) {
					alt8=1;
				}

				switch (alt8) {
				case 1 :
					// Expr.g:229:8: ',' e2= assignExpr
					{
					match(input,12,FOLLOW_12_in_argList496); 
					pushFollow(FOLLOW_assignExpr_in_argList500);
					e2=assignExpr();
					state._fsp--;

					 args.add(e2); 
					}
					break;

				default :
					break loop8;
				}
			}

			match(input,8,FOLLOW_8_in_argList512); 
			 value = args; 
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "argList"



	// $ANTLR start "callExpr"
	// Expr.g:234:1: callExpr returns [double value] : i= ID args= argList ;
	public final double callExpr() throws RecognitionException {
		double value = 0.0;


		Token i=null;
		List<Double> args =null;

		try {
			// Expr.g:235:5: (i= ID args= argList )
			// Expr.g:235:7: i= ID args= argList
			{
			i=(Token)match(input,ID,FOLLOW_ID_in_callExpr544); 
			pushFollow(FOLLOW_argList_in_callExpr548);
			args=argList();
			state._fsp--;

			 value = callFunction((i!=null?i.getText():null), args); 
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "callExpr"



	// $ANTLR start "atom"
	// Expr.g:238:1: atom returns [double value] : (n= NUMBER |i= ID | '(' e= commaExpr ')' |c= callExpr );
	public final double atom() throws RecognitionException {
		double value = 0.0;


		Token n=null;
		Token i=null;
		double e =0.0;
		double c =0.0;

		try {
			// Expr.g:239:5: (n= NUMBER |i= ID | '(' e= commaExpr ')' |c= callExpr )
			int alt9=4;
			switch ( input.LA(1) ) {
			case NUMBER:
				{
				alt9=1;
				}
				break;
			case ID:
				{
				int LA9_2 = input.LA(2);
				if ( (LA9_2==EOF||(LA9_2 >= 8 && LA9_2 <= 14)||LA9_2==16) ) {
					alt9=2;
				}
				else if ( (LA9_2==7) ) {
					alt9=4;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 9, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 7:
				{
				alt9=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}
			switch (alt9) {
				case 1 :
					// Expr.g:239:7: n= NUMBER
					{
					n=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_atom573); 
					 value = Double.parseDouble((n!=null?n.getText():null)); 
					}
					break;
				case 2 :
					// Expr.g:240:7: i= ID
					{
					i=(Token)match(input,ID,FOLLOW_ID_in_atom596); 
					 value = getVarValue((i!=null?i.getText():null)); 
					}
					break;
				case 3 :
					// Expr.g:241:7: '(' e= commaExpr ')'
					{
					match(input,7,FOLLOW_7_in_atom621); 
					pushFollow(FOLLOW_commaExpr_in_atom625);
					e=commaExpr();
					state._fsp--;

					match(input,8,FOLLOW_8_in_atom627); 
					 value = e; 
					}
					break;
				case 4 :
					// Expr.g:242:7: c= callExpr
					{
					pushFollow(FOLLOW_callExpr_in_atom639);
					c=callExpr();
					state._fsp--;

					 value = c; 
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "atom"

	// Delegated rules



	public static final BitSet FOLLOW_commaExpr_in_parse63 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assignExpr_in_commaExpr88 = new BitSet(new long[]{0x0000000000001002L});
	public static final BitSet FOLLOW_12_in_commaExpr99 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_assignExpr_in_commaExpr103 = new BitSet(new long[]{0x0000000000001002L});
	public static final BitSet FOLLOW_addExpr_in_assignExpr130 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_assignExpr142 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_15_in_assignExpr144 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_assignExpr_in_assignExpr148 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multExpr_in_addExpr174 = new BitSet(new long[]{0x0000000000002802L});
	public static final BitSet FOLLOW_11_in_addExpr192 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_multExpr_in_addExpr196 = new BitSet(new long[]{0x0000000000002802L});
	public static final BitSet FOLLOW_13_in_addExpr209 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_multExpr_in_addExpr213 = new BitSet(new long[]{0x0000000000002802L});
	public static final BitSet FOLLOW_expoExpr_in_multExpr249 = new BitSet(new long[]{0x0000000000004202L});
	public static final BitSet FOLLOW_9_in_multExpr267 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_expoExpr_in_multExpr271 = new BitSet(new long[]{0x0000000000004202L});
	public static final BitSet FOLLOW_14_in_multExpr284 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_expoExpr_in_multExpr288 = new BitSet(new long[]{0x0000000000004202L});
	public static final BitSet FOLLOW_unaryExpr_in_expoExpr325 = new BitSet(new long[]{0x0000000000010402L});
	public static final BitSet FOLLOW_16_in_expoExpr342 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_unaryExpr_in_expoExpr347 = new BitSet(new long[]{0x0000000000010402L});
	public static final BitSet FOLLOW_10_in_expoExpr360 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_unaryExpr_in_expoExpr364 = new BitSet(new long[]{0x0000000000000402L});
	public static final BitSet FOLLOW_atom_in_unaryExpr391 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_13_in_unaryExpr409 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_unaryExpr_in_unaryExpr413 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_11_in_unaryExpr423 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_unaryExpr_in_unaryExpr427 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_17_in_unaryExpr437 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ID_in_unaryExpr441 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_7_in_argList474 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_assignExpr_in_argList485 = new BitSet(new long[]{0x0000000000001100L});
	public static final BitSet FOLLOW_12_in_argList496 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_assignExpr_in_argList500 = new BitSet(new long[]{0x0000000000001100L});
	public static final BitSet FOLLOW_8_in_argList512 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_callExpr544 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_argList_in_callExpr548 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NUMBER_in_atom573 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_atom596 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_7_in_atom621 = new BitSet(new long[]{0x00000000000228B0L});
	public static final BitSet FOLLOW_commaExpr_in_atom625 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_8_in_atom627 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_callExpr_in_atom639 = new BitSet(new long[]{0x0000000000000002L});
}
