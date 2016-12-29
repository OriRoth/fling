package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.Regex.NT.*;
import static org.spartan.fajita.api.examples.Regex.Term.*;
import static org.spartan.fajita.api.examples.Regex.Term.anyChar;
import static org.spartan.fajita.api.examples.Regex.Term.backref;
import static org.spartan.fajita.api.examples.Regex.Term.endOfString;
import static org.spartan.fajita.api.examples.Regex.Term.group;
import static org.spartan.fajita.api.examples.Regex.Term.moreThanZero;
import static org.spartan.fajita.api.examples.Regex.Term.notOneOf;
import static org.spartan.fajita.api.examples.Regex.Term.oneOf;
import static org.spartan.fajita.api.examples.Regex.Term.startOfString;
import static org.spartan.fajita.api.examples.Regex.Term.str;
import static org.spartan.fajita.api.examples.Regex.Term.zeroOrMore;
import static org.spartan.fajita.api.junk.Regex.endOfString;
import static org.spartan.fajita.api.junk.Regex.group;
import static org.spartan.fajita.api.junk.Regex.notOneOf;
import static org.spartan.fajita.api.junk.Regex.startOfString;
import static org.spartan.fajita.api.junk.Regex.zeroOrMore;

import java.io.IOException;
import java.util.Map;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.junk.InsideSet;

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
    ANCHORED//
    , SIMPLE_RE, RE, OPT_RE, OPT_EOS//
    , NO_DUPL_RE, QUANTIFIER, OPT_QUANTIFIER //
    , SET, INSIDE_SET, INSIDE_SETS, ONE_OF
  }

  public static Map<String, String> buildBNF() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(ANCHORED) //
        .derive(ANCHORED).to(startOfString).and(OPT_RE).and(OPT_EOS)//
        /* */.or(RE).and(OPT_EOS) //
        /* */.or(endOfString)//
        .derive(OPT_RE).to(RE)//
        /* */.orNone()//
        .derive(OPT_EOS).to(endOfString)//
        /* */.orNone()//
        .derive(RE).to(SIMPLE_RE).and(OPT_RE) //
        .derive(SIMPLE_RE).to(NO_DUPL_RE).and(OPT_QUANTIFIER)//
        /* */.or(zeroOrMore, NO_DUPL_RE)//
        /* */.or(moreThanZero, NO_DUPL_RE)//
        .derive(OPT_QUANTIFIER).to(QUANTIFIER) //
        /* */.orNone() //
        .derive(QUANTIFIER).to(atLeast, int.class).and(times)//
        /* */.or(atMost, int.class).and(times)//
        /* */.or(exactly, int.class).and(times)//
        /* */.or(times, int.class, int.class)//
        .derive(NO_DUPL_RE).to(str, String.class)//
        /* */.or(anyChar)//
        /* */.or(SET)//
        /* */.or(group, RE)//
        /* */.or(backref, int.class)//
        .derive(SET).to(oneOf, INSIDE_SET)//
        /* */.or(notOneOf, INSIDE_SET)//
        .derive(INSIDE_SET).to(ONE_OF).and(INSIDE_SETS) //
        .derive(INSIDE_SETS).to(INSIDE_SET) //
        /* */.orNone() //
        .derive(ONE_OF).to(alphanumeric)//
        /* */.or(alphabetic)//
        /* */.or(digit)//
        /* */.or(whitespace)//
        /* */.or(chars, Fajita.ellipsis(char.class)) //
        /* */.or(chr, char.class).and(to, char.class) //
        .go(Main.packagePath);
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }
  static void test() {
    startOfString();
    endOfString();
    startOfString().endOfString();
    group(zeroOrMore(notOneOf(InsideSet.whitespace())).str("<"));
  }
}
