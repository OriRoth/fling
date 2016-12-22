package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.Regex.NT.*;
import static org.spartan.fajita.api.examples.Regex.Term.*;

import java.io.IOException;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class Regex {
  private static final String apiName = "Regex";

  static enum Term implements Terminal {
    startOfString, endOfString //
    , moreThanZero, zeroOrMore, atLeast, times, atMost, exactly//
    , group, backref, anyChar //
    , str, alphanumeric, alphabetic, digit, whitespace //
    , chars, chr, to //
    , oneOf, notOneOf //
  }

  static enum NT implements NonTerminal {
    BASIC_REG_EXP, SIMPLE_RE //
    // , RE_EXPRESSION, OPT_RE_EXPRESSION, OPT_EOS//
    , NONDUPL_RE, QUANTIFIER, OPT_QUANTIFIER //
    , CHARACTERS//
    // , SET, INSIDE_SET, OPT_INSIDE_SET, ONE_OF
  }

  public static String buildBNF() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(BASIC_REG_EXP) //
        // .derive(BASIC_REG_EXP).to(startOfString).and(OPT_RE_EXPRESSION).and(OPT_EOS)
        // //
        // /* */.or(RE_EXPRESSION).and(OPT_EOS) //
        // /* */.or(endOfString)//
        .derive(BASIC_REG_EXP).to(SIMPLE_RE)//
        // .derive(OPT_RE_EXPRESSION).to(RE_EXPRESSION)//
        // /* */.orNone()//
        // .derive(OPT_EOS).to(endOfString)//
        // /* */.orNone()//
        // .derive(RE_EXPRESSION).to(SIMPLE_RE).and(OPT_RE_EXPRESSION) //
        .derive(SIMPLE_RE).to(NONDUPL_RE).and(OPT_QUANTIFIER)//
         /* */.or(zeroOrMore, NONDUPL_RE)//
         /* */.or(moreThanZero, NONDUPL_RE)//
        .derive(OPT_QUANTIFIER).to(QUANTIFIER) //
        /* */.orNone() //
        .derive(QUANTIFIER).to(atLeast, int.class).and(times)//
        /* */.or(atMost, int.class).and(times)//
        /* */.or(exactly, int.class).and(times)//
        /* */.or(times, int.class, int.class)//
        .derive(NONDUPL_RE).to(CHARACTERS)//
        /*				*/.or(group, int.class)//
        // /* */.or(group, RE_EXPRESSION)//
        /*				*/.or(backref, int.class)//
        .derive(CHARACTERS).to(str, String.class)//
        /*				*/.or(anyChar)//
        /// * */.or(SET)//
        // .derive(SET).to(oneOf, INSIDE_SET)//
        /// * */.or(notOneOf, INSIDE_SET)//
        // .derive(INSIDE_SET).to(ONE_OF).and(OPT_INSIDE_SET) //
        // .derive(OPT_INSIDE_SET).to(INSIDE_SET) //
        /// * */.orNone() //
        // .derive(ONE_OF).to(alphanumeric)//
        /// * */.or(alphabetic)//
        /// * */.or(digit)//
        /// * */.or(whitespace)//
        /// * */.or(chars, Fajita.ellipsis(char.class)) //
        /// * */.or(chr, char.class).and(to, char.class) //
        .go(Main.packagePath);
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(apiName, buildBNF());
  }
}
