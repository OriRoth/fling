package org.spartan.fajita.api.examples;

class $Datalog {
  class LITERAL {
    String name;
    String[] terms;
  }

  class BODY {
    LITERALS body;
  }

  class LITERALS {
    LITERAL literal;
    LITERALS_OR_NONE literals_or_none;
  }

  class augS {
    RULES rules;
  }

  class RULES {
    /* RULES$1 rules$1;RULES$2 rules$2; */}

  class LITERALS_OR_NONE {
    /* LITERALS_OR_NONE$1 literals_or_none$1;LITERALS_OR_NONE$2
     * literals_or_none$2; */}

  class RULE {
    /* RULE$1 rule$1;RULE$2 rule$2; */}

  class RULE$2 extends RULE {
    LITERAL head;
    BODY body;
  }

  class RULE$1 extends RULE {
    LITERAL fact;
  }

  class RULES$1 extends RULES {
    RULE rule;
    RULES rules;
  }

  class LITERALS_OR_NONE$2 extends LITERALS_OR_NONE {
  }

  class RULES$2 extends RULES {
  }

  class LITERALS_OR_NONE$1 extends LITERALS_OR_NONE {
    LITERAL literal;
    LITERALS_OR_NONE literals_or_none;
  }
}
