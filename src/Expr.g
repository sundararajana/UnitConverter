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

grammar Expr;

/*
 * ANTLR 3.5.1 grammar for a simple expression language.
 * Usual binary operators (+, -, *, /) are supported.
 * ^ or ** for exponentiation is supported. Multiple
 * expressions can be stringed by comma operator. Variable
 * assignment/read supported. java.lang.Math methods can
 * be called by simple names (sin, cos, log etc.).
 */

@lexer::header  { 

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

}

@parser::header {

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

}

@lexer::members {
  // bail out on first error.
  @Override
  public void reportError(RecognitionException e) {
    throw new RuntimeException(e);
  }
}

@parser::members {

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
}

parse returns [double value]
    : e=commaExpr { $value = $e.value; }
    ;

commaExpr returns [double value]
    : e1=assignExpr { $value = $e1.value; }
      (',' e2=assignExpr { $value = $e2.value; })*
    ;

assignExpr returns [double value]
    : e1=addExpr { $value = $e1.value; }
    | i=ID '=' e2=assignExpr { $value = setVarValue($i.text, $e2.value); }
    ;

addExpr returns [double value]
    :  m1=multExpr      { $value =  $m1.value; } 
      ( '+' m2=multExpr { $value += $m2.value; } 
      | '-' m2=multExpr { $value -= $m2.value; }
      )*  
    ;

multExpr returns [double value]
    : a1=expoExpr       { $value =  $a1.value; }
      ( '*' a2=expoExpr { $value *= $a2.value; } 
      | '/' a2=expoExpr { $value /= $a2.value; }
      )*  
    ;

// support '^' for power as well as pythonish **
expoExpr returns [double value]
    : a1=unaryExpr       { $value =  $a1.value; }
      ('^'  a2=unaryExpr { $value = Math.pow($value,$a2.value); })*
      ('**' a2=unaryExpr { $value = Math.pow($value,$a2.value); })*
    ;

unaryExpr returns [double value]
    : a1=atom         { $value = $a1.value; }
    | '-' e=unaryExpr { $value = -$e.value; }
    | '+' e=unaryExpr { $value = $e.value;  }
    | 'delete' i=ID   { $value = deleteVar($i.text); }
    ;

argList returns [List<Double> value]
    : { List<Double> args = new ArrayList<Double>(); }
      '(' 
      e=assignExpr { args.add($e.value); }
      (',' e2=assignExpr { args.add($e2.value); })*
      ')' 
      { $value = args; }
    ;

callExpr returns [double value]
    : i=ID args=argList { $value = callFunction($i.text, $args.value); }
    ;

atom returns [double value]
    : n=NUMBER            { $value = Double.parseDouble($n.text); }
    | i=ID                { $value = getVarValue($i.text); }
    | '(' e=commaExpr ')' { $value = $e.value; }
    | c=callExpr          { $value = $c.value; }
    ;

ID
    : ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*
    ;

NUMBER
    : ('0'..'9')+ ('.' ('0'..'9')+)?
    ;

WS  
    : (' ' | '\t' | '\r'| '\n') { skip(); }
    ;
