package org.spartan.fajita.api.rllp;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.jgrapht.EdgeFactory;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.util.VertexPair;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class JSMGraph extends DefaultDirectedGraph<JSMVertex, JSMEdge> {
  public JSM root;

  public JSMGraph() {
    super(new JSMEdgeFactory());
  }

  private static final long serialVersionUID = -7987222753212096796L;

  public void calcAndVisualize(JSM _root,String label) {
    this.root = _root;
    calc(_root);
    visualize(label);
  }
  public void calc(JSM _root) {
    Stack<VertexPair<JSMVertex>> unhandled = new Stack<>();
    addVertex(new JSMVertex(_root));
    _root.legalJumps().forEach(jump -> unhandled.add(new VertexPair<>(new JSMVertex(_root), new JSMVertex(jump.getValue()))));
    while (!unhandled.isEmpty()) {
      VertexPair<JSMVertex> edge = unhandled.pop();
      final JSMVertex dst = edge.getSecond();
      Optional<JSMVertex> matching = vertexSet().stream().filter(vertex -> vertex.jsm.equals(dst.jsm)).findAny();
      if (matching.isPresent()) {
        addEdge(edge.getFirst(), matching.get());
      } else {
        addVertex(dst);
        addEdge(edge.getFirst(), dst);
        dst.jsm.legalJumps().forEach(jump -> unhandled.add(new VertexPair<>(dst, new JSMVertex(jump.getValue()))));
      }
    }
  }
  public void visualize(String label) {
    new GraphVisualize().visualize(this,label);
  }
  public void dfs(){
    DepthFirstIterator<JSMVertex, JSMEdge> iter = new DepthFirstIterator<>(this);
    iter.addTraversalListener(new TraversalListener<JSMVertex, JSMEdge>() {
      @Override public void vertexTraversed(VertexTraversalEvent<JSMVertex> e) {
        // TODO Auto-generated method stub
      }
      @Override public void vertexFinished(VertexTraversalEvent<JSMVertex> e) {
        // TODO Auto-generated method stub
      }
      @Override public void edgeTraversed(EdgeTraversalEvent<JSMEdge> e) {
        // TODO Auto-generated method stub
      }
      @Override public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {

      }
      @Override public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
        // TODO Auto-generated method stub
      }
    });
  }
}

class JSMVertex {
  public JSM jsm;

  public JSMVertex(JSM jsm) {
    this.jsm = jsm;
  }
  @Override public int hashCode() {
    return jsm.getS0().hashCode();
  }
  @Override public boolean equals(Object obj) {
    return jsm.equals(((JSMVertex) obj).jsm);
  }
  @Override public String toString() {
    String items = "";
    List<Item> s0 = jsm.getS0();
    for (int i = s0.size() - 1; i >= 0; i--)
      // for (int i = s0.size()-1; i > 0 && i > s0.size()-3; i--)
      items = s0.get(i).toString() + items;
    return items;
  }
}

class JSMEdge extends DefaultEdge {
  private static final long serialVersionUID = 6422130338593415451L;
  public final Verb v;

  public JSMEdge(Verb v) {
    this.v = v;
  }
  @Override public String toString() {
    return v.name();
  }
}

class JSMEdgeFactory implements EdgeFactory<JSMVertex, JSMEdge> {
  @Override public JSMEdge createEdge(JSMVertex src, JSMVertex dst) {
    Symbol symb = dst.jsm.peek().rule.getChildren().get(dst.jsm.peek().dotIndex - 1);
    if (!symb.isVerb())
      throw new IllegalStateException("cannot jump from " + src.toString() + " \n to \n" + dst.toString());
    return new JSMEdge((Verb) symb);
  }
}