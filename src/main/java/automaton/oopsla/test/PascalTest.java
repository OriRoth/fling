package automaton.oopsla.test;
import automaton.oopsla.PascalExample.Body1;
import automaton.oopsla.PascalExample.ERROR;
import automaton.oopsla.PascalExample.Pascal;
import automaton.oopsla.PascalExample.Procedure_Definition1_rec;
import automaton.oopsla.PascalExample.Procedure_Definition4;
class PascalTest{
  public static void main(String[] args) {
      new Pascal().program_t().id_t().pair_t().semi_t();
      new Pascal().program_t().id_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().pair_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().semi_t().label_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().semi_t().const_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().semi_t().const_t().semi_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().semi_t().label_t().semi_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().pair_t().semi_t().label_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().pair_t().semi_t().const_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().semi_t().const_t().semi_t().semi_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().semi_t().label_t().semi_t().semi_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().pair_t().semi_t().label_t().semi_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().pair_t().semi_t().const_t().semi_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().pair_t().semi_t().const_t().semi_t().semi_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().pair_t().semi_t().label_t().semi_t().semi_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().semi_t().label_t().semi_t().const_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().semi_t().label_t().semi_t().const_t().semi_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().semi_t().label_t().semi_t().semi_t().const_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().pair_t().semi_t().label_t().semi_t().const_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().pair_t().semi_t().label_t().semi_t().const_t().semi_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().pair_t().semi_t().label_t().semi_t().semi_t().const_t().semi_t().begin_t().end_t();
      new Pascal().program_t().id_t().semi_t().procedure_t().id_t().semi_t().begin_t().end_t().begin_t().end_t();
  }
}