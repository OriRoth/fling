package org.spartan.fajita.api;

import org.spartan.fajita.api.examples.automatonCycles.AutomatonCycles;
import org.spartan.fajita.api.examples.balancedParenthesis.BalancedParenthesis;
import org.spartan.fajita.api.generators.ApiGenerator;

import com.squareup.javapoet.TypeSpec;

public class Main {
//  private static JGraphModelAdapter<State, LabeledEdge> model;

  public static void main(final String[] args) {
//    typeSpec(AutomatonCycles.buildBNF());
//    LR1.buildBNF();
    TypeSpec fluentAPI = ApiGenerator.generate(BalancedParenthesis.buildBNF());
    System.out.println(fluentAPI.toString());
  }
//  private static void typeSpec(final LRParser parser) {
//    lrAutomatonVisualisation(parser);
//    TypeArgumentManager tam = new TypeArgumentManager(parser);
//    Builder states = TypeSpec.classBuilder("States");
//    TypeSpec baseState = new BaseStateSpec(tam).generate();
//    states.addType(baseState);
//    final List<TypeSpec> types = new ArrayList<>();
//    parser.getStates().forEach(s -> {
//      types.add(generateClass(tam, s, s.name));
////      types.add(generateClass(tam, s, s.name + "\u02B9"));
//    });
//    // for (TypeSpec typeSpec : types)
//    // System.out.println(typeSpec);
//    states.addTypes(types);
//    System.out.println(states.build());
//  }
//  private static TypeSpec generateClass(final TypeArgumentManager tam, final State s, final String name) {
//    return TypeSpec.classBuilder(name).addModifiers(Modifier.STATIC,Modifier.PUBLIC).addTypeVariables(tam.getFormalParameters(s))
//        .superclass(tam.getInstantiatedBaseState(s)).build();
//  }
//  private static void lrAutomatonVisualisation(final LRParser parser) {
//    DirectedGraph<State, LabeledEdge> graph = generateGraph(parser);
//    model = new JGraphModelAdapter<>(graph);
//    parser.getStates().forEach(s -> positionVertexAt(s, 100 + (s.index % 4) * 350, 100 + (s.index / 4) * 100));
//    JGraph jgraph = new JGraph(model);
//    JFrame frame = new JFrame();
//    frame.setSize(1400, 700);
//    frame.add(jgraph);
//    frame.setVisible(true);
//  }
//  private static DirectedGraph<State, LabeledEdge> generateGraph(final LRParser parser) {
//    DefaultDirectedGraph<State, LabeledEdge> $ = new DefaultDirectedGraph<>(new LabeledEdgeFactory());
//    parser.getStates().forEach(s -> $.addVertex(s));
//    parser.getStates().forEach(s -> s.allLegalTransitions().forEach(symb -> {
//      State goTo = s.goTo(symb);
//      if (goTo.getClass() == AcceptState.class)
//        $.addVertex(goTo);
//      $.addEdge(s, goTo);
//    }));
//    return $;
//  }
//
//  private static class LabeledEdge {
//    private final State src;
//    private final State dst;
//
//    public LabeledEdge(final State src, final State dst) {
//      this.src = src;
//      this.dst = dst;
//    }
//    @Override public String toString() {
//      for (Symbol lh : src.allLegalTransitions())
//        if (src.goTo(lh).equals(dst))
//          return lh.name();
//      return super.toString();
//    }
//  }
//
//  protected static class LabeledEdgeFactory implements EdgeFactory<State, LabeledEdge> {
//    @Override public LabeledEdge createEdge(final State sourceVertex, final State targetVertex) {
//      return new LabeledEdge(sourceVertex, targetVertex);
//    }
//  }
//
//  @SuppressWarnings({ "rawtypes", "unchecked" }) private static void positionVertexAt(final Object vertex, final int x,
//      final int y) {
//    DefaultGraphCell cell = model.getVertexCell(vertex);
//    Map attr = cell.getAttributes();
//    Rectangle2D b = GraphConstants.getBounds(attr);
//    GraphConstants.setBounds(attr, new Rectangle(x, y, (int) b.getWidth(), (int) b.getHeight()));
//    Map cellAttr = new HashMap();
//    cellAttr.put(cell, attr);
//    model.edit(cellAttr, null, null, null);
//  }
}
