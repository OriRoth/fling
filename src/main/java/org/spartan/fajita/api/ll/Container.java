package org.spartan.fajita.api.ll;

import org.spartan.fajita.api.ll.Container.C_$;
import org.spartan.fajita.api.ll.Container.C_S;

class Container {
  public static C_S<C_$> start;

  class C_x<Tail> {
    Tail x() {
      return null;
    }
  }

  class C_lp<Tail> {
    Tail lp() {
      return null;
    }
  }

  class C_plus<Tail> {
    Tail plus() {
      return null;
    }
  }

  class C_rp<Tail> {
    Tail rp() {
      return null;
    }
  }

  class C_$ {
    void $() {
      return;
    }
  }

  class C_F<Tail> {
    Tail x() {
      return null;
    }
  }

  class C_S<Tail> {
    Tail x() {
      return null;
    }

    C_S<C_plus<C_F<C_rp<Tail>>>> lp() {
      return null;
    }
  }
}



class Main{
  void usage() {
    C_S<C_$> start = Container.start;
    start.x().$();
    start.lp().x().plus().x().rp().$();
    start.lp().lp().lp().x().plus().x().rp().plus().x().rp().plus().x().rp().$();
  }
}