package org.spartan.fajita.api;

import static org.spartan.fajita.api.Main.NT.*;
import static org.spartan.fajita.api.Main.Term.*;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis;
import org.spartan.fajita.api.jlr.JLRRecognizer;
import org.spartan.fajita.api.jlr.JState;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.generation.RLLPEncoder;

public class Main {
  public static void main(final String[] args) throws IOException {
    apiGenerator(testBNF());
    // expressionBuilder();
  }
  public static void apiGenerator(BNF bnf) throws IOException {
    String code = RLLPEncoder.generate(new RLLP(bnf));
    System.out.println(code);
    try (FileOutputStream fos = new FileOutputStream(new File(
        "/home/tomerlevi/fajita/src/main/java/org/spartan/fajita/api/junk/Container_" + bnf.hashCode() % 1000 + ".java"))) {
      fos.write(code.getBytes(), 0, code.getBytes().length);
      fos.close();
    }
    // lrAutomatonVisualisation(bnf);
    // JavaFile fluentAPI = ApiGenerator.generate(bnf);
    // System.out.println(fluentAPI.toString());
  }
  static void expressionBuilder() {
    BalancedParenthesis.expressionBuilder();
  }
  public static Compound generateAST(List<DerivationRule> reduces) {
    Stack<Compound> compoundQueue = new Stack<>();
    for (DerivationRule reduce : reduces) {
      List<AbstractNode> children = new ArrayList<>();
      final List<Symbol> rhs = reduce.getChildren();
      for (int i = rhs.size() - 1; i >= 0; --i) {
        Symbol s = rhs.get(i);
        children.add(0, (s.isVerb() ? new Atomic((Verb) s) : compoundQueue.pop()));
      }
      compoundQueue.add(new Compound(reduce.lhs, children));
    }
    assert (compoundQueue.size() == 1);
    return compoundQueue.pop();
  }

  private static JGraphModelAdapter<JState, LabeledEdge> model;

  public static void lrAutomatonVisualisation(BNF bnf) {
    JLRRecognizer parser = new JLRRecognizer(bnf);
    DirectedGraph<JState, LabeledEdge> graph = generateGraph(parser);
    model = new JGraphModelAdapter<>(graph);
    parser.getStates().forEach(s -> positionVertexAt(s, 100 + (s.index % 4) * 350, 100 + (s.index / 4) * 100));
    JGraph jgraph = new JGraph(model);
    JFrame frame = new JFrame();
    frame.setSize(1400, 700);
    frame.add(jgraph);
    frame.setVisible(true);
  }
  private static DirectedGraph<JState, LabeledEdge> generateGraph(final JLRRecognizer parser) {
    DefaultDirectedGraph<JState, LabeledEdge> $ = new DefaultDirectedGraph<>(new LabeledEdgeFactory());
    parser.getStates().forEach(s -> $.addVertex(s));
    parser.getStates().forEach(s -> s.allLegalTransitions().forEach(symb -> {
      JState goTo = s.goTo(symb);
      $.addEdge(s, goTo);
    }));
    return $;
  }

  private static class LabeledEdge {
    private final JState src;
    private final JState dst;

    public LabeledEdge(final JState src, final JState dst) {
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

  protected static class LabeledEdgeFactory implements EdgeFactory<JState, LabeledEdge> {
    @Override public LabeledEdge createEdge(final JState sourceVertex, final JState targetVertex) {
      return new LabeledEdge(sourceVertex, targetVertex);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" }) private static void positionVertexAt(final Object vertex, final int x,
      final int y) {
    DefaultGraphCell cell = model.getVertexCell(vertex);
    Map attr = cell.getAttributes();
    Rectangle2D rect = GraphConstants.getBounds(attr);
    GraphConstants.setBounds(attr, new Rectangle(x, y, (int) rect.getWidth(), (int) rect.getHeight()));
    Map cellAttr = new HashMap();
    cellAttr.put(cell, attr);
    model.edit(cellAttr, null, null, null);
  }

  static enum Term implements Terminal {
    a, b, c, d;
  }

  static enum NT implements NonTerminal {
    S, A, B, C, D
  }

  static BNF testBNF() {
    return new BNFBuilder(Term.class, NT.class) //
        .start(S) //
        .derive(S).to(A).and(D) //
        .derive(A).to(B) //
        .derive(B).to(C).and(b) //
        .derive(C).to(c).orNone() //
        .derive(D).to(d) //
        .go();
  }
}
