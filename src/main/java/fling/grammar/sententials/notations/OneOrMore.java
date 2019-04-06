package fling.grammar.sententials.notations;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import fling.compiler.Namer;
import fling.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import fling.grammar.sententials.DerivationRule;
import fling.grammar.sententials.Notation;
import fling.grammar.sententials.SententialForm;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Variable;
import fling.grammar.sententials.Verb;
import fling.grammar.types.ClassParameter;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;

// TODO support nested notations (?).
@JavaCompatibleNotation public class OneOrMore implements Notation {
  public final Symbol symbol;

  public OneOrMore(Symbol symbol) {
    Objects.requireNonNull(symbol);
    assert !symbol.isNotation() : "nested notations are not supported";
    this.symbol = symbol;
  }
  @Override public String name() {
    return symbol.name() + "*";
  }
  @Override public Variable extend(Namer namer, Consumer<Variable> variableDeclaration, Consumer<DerivationRule> ruleDeclaration) {
    Variable head = namer.createNotationChild(symbol);
    Variable tail = namer.createNotationChild(symbol);
    variableDeclaration.accept(head);
    variableDeclaration.accept(tail);
    ruleDeclaration.accept(new DerivationRule(head, asList( //
        new SententialForm(symbol, tail))));
    ruleDeclaration.accept(new DerivationRule(tail, asList(//
        new SententialForm(symbol, tail), //
        new SententialForm())));
    return head;
  }
  @Override public Collection<Symbol> abbreviatedSymbols() {
    return asList(symbol);
  }
  @Override public List<FieldNodeFragment> getFields(Function<Symbol, List<FieldNodeFragment>> fieldsSolver,
      @SuppressWarnings("unused") Function<String, String> nameFromBaseSolver) {
    // TODO manage inner symbol with no fields.
    return fieldsSolver.apply(symbol).stream() //
        .map(innerField -> FieldNodeFragment.of( //
            String.format("%s<%s>", //
                List.class.getCanonicalName(), //
                ClassParameter.unPrimitiveType(innerField.parameterType)), //
            innerField.parameterName)) //
        .collect(toList());
  }
  @Override public boolean isNullable(Function<Symbol, Boolean> nullabilitySolver) {
    return nullabilitySolver.apply(symbol);
  }
  @Override public Set<Verb> getFirsts(Function<Symbol, Set<Verb>> firstsSolver) {
    return firstsSolver.apply(symbol);
  }
  @Override public String toString() {
    return symbol + "*";
  }
  @SuppressWarnings("unchecked") public static List<List<Object>> abbreviate(List<Object> rawNode, int fieldCount) {
    List<List<Object>> $ = new ArrayList<>();
    List<Object> currentRawNode = rawNode;
    while (!currentRawNode.isEmpty()) {
      List<Object> rawArguments = (List<Object>) currentRawNode.get(0);
      assert rawArguments.size() == fieldCount;
      for (int i = 0; i < fieldCount; ++i)
        $.get(i).add(rawArguments.get(i));
      currentRawNode = (List<Object>) currentRawNode.get(1);
    }
    return $;
  }
}
