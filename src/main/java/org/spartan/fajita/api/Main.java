package org.spartan.fajita.api;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.spartan.fajita.api.ast.AbstractNode;
import org.spartan.fajita.api.ast.Atomic;
import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.examples.automatonCycles.AutomatonCycles;
import org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis;
import org.spartan.fajita.api.generators.ApiGenerator;
import org.spartan.fajita.api.parser.AcceptState;
import org.spartan.fajita.api.parser.LRParser;
import org.spartan.fajita.api.parser.State;

import com.squareup.javapoet.TypeSpec;

public class Main {
  public static void main(final String[] args) {
//     apiGenerator();
    expressionBuilder();
    // serializeTest();
  }
  public static void serializeTest() {
    BNF buildBNF = AutomatonCycles.buildBNF();
    for (DerivationRule d : buildBNF.getRules()) {
      final String serial = d.serialize();
      System.out.println(serial);
      System.out.println(DerivationRule.deserialize(serial));
      System.out.println("");
    }
  }
  static void apiGenerator() {
    final BNF bnf = BalancedParenthesis.buildBNF();
    TypeSpec fluentAPI = ApiGenerator.generate(bnf);
    System.out.println(fluentAPI.toString());
    lrAutomatonVisualisation(new LRParser(bnf));
  }
  static void expressionBuilder() {
    BalancedParenthesis.expressionBuilder();
  }
  public static Compound generateAST(List<DerivationRule> reduces) {
    Queue<Compound> compoundQueue = new LinkedList<>();
    for (DerivationRule reduce : reduces) {
      List<AbstractNode> children = new ArrayList<>();
      for (Symbol s : reduce.getChildren()) {
        AbstractNode symbNode;
        if (s.isTerminal())
          symbNode = new Atomic((Terminal) s);
        else if (s == SpecialSymbols.epsilon)
          symbNode = AbstractNode.epsilon;
        else {
          symbNode = compoundQueue.remove();
        }
        children.add(symbNode);
      }
      compoundQueue.add(new Compound(reduce.lhs, children));
    }
    assert (compoundQueue.size() == 1);
    return compoundQueue.remove();
  }

  private static JGraphModelAdapter<State, LabeledEdge> model;

  private static void lrAutomatonVisualisation(final LRParser parser) {
    DirectedGraph<State, LabeledEdge> graph = generateGraph(parser);
    model = new JGraphModelAdapter<>(graph);
    parser.getStates().forEach(s -> positionVertexAt(s, 100 + (s.index % 4) * 350, 100 + (s.index / 4) * 100));
    JGraph jgraph = new JGraph(model);
    JFrame frame = new JFrame();
    frame.setSize(1400, 700);
    frame.add(jgraph);
    frame.setVisible(true);
  }
  private static DirectedGraph<State, LabeledEdge> generateGraph(final LRParser parser) {
    DefaultDirectedGraph<State, LabeledEdge> $ = new DefaultDirectedGraph<>(new LabeledEdgeFactory());
    parser.getStates().forEach(s -> $.addVertex(s));
    parser.getStates().forEach(s -> s.allLegalTransitions().forEach(symb -> {
      State goTo = s.goTo(symb);
      if (goTo.getClass() == AcceptState.class)
        $.addVertex(goTo);
      $.addEdge(s, goTo);
    }));
    return $;
  }

  private static class LabeledEdge {
    private final State src;
    private final State dst;

    public LabeledEdge(final State src, final State dst) {
      this.src = src;
      this.dst = dst;
    }
    @Override public String toString() {
      for (Symbol lh : src.allLegalTransitions())
        if (src.goTo(lh).equals(dst))
          return lh.name();
      return super.toString();
    }
  }

  protected static class LabeledEdgeFactory implements EdgeFactory<State, LabeledEdge> {
    @Override public LabeledEdge createEdge(final State sourceVertex, final State targetVertex) {
      return new LabeledEdge(sourceVertex, targetVertex);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" }) private static void positionVertexAt(final Object vertex, final int x,
      final int y) {
    DefaultGraphCell cell = model.getVertexCell(vertex);
    Map attr = cell.getAttributes();
    Rectangle2D b = GraphConstants.getBounds(attr);
    GraphConstants.setBounds(attr, new Rectangle(x, y, (int) b.getWidth(), (int) b.getHeight()));
    Map cellAttr = new HashMap();
    cellAttr.put(cell, attr);
    model.edit(cellAttr, null, null, null);
  }
}
