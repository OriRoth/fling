package org.spartan.fajita.api.rllp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.ListenableDirectedGraph;

public class GraphVisualize extends JPanel {
  private static final long serialVersionUID = -2552108127473097087L;
  private JGraphModelAdapter<JSMVertex, JSMEdge> jgAdapter;
  private static JFrame frame;
  private static JTabbedPane tabs = new JTabbedPane();
  private static final Dimension DEFAULT_SIZE = new Dimension(1200, 820);
  private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");

  public void visualize(JSMGraph g, String label) {
    ListenableDirectedGraph<JSMVertex, JSMEdge> l = new ListenableDirectedGraph<>(g);
    jgAdapter = new JGraphModelAdapter<>(l);
    JGraph jgraph = new JGraph(jgAdapter);
    adjustDisplaySettings(jgraph);
    add(jgraph);
    JLabel jlabel = new JLabel(label, SwingConstants.CENTER);
    add(jlabel, 0);
    tabs.addTab("" + tabs.getTabCount(), this);
    setSize(DEFAULT_SIZE);
    int size = g.vertexSet().size();
    int n = (int) (Math.floor(Math.sqrt(size)) + 1);
    Iterator<JSMVertex> iter = g.vertexSet().iterator();
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++) {
        if (!iter.hasNext())
          break;
        positionVertexAt(iter.next(), (int) (j * (DEFAULT_SIZE.getWidth() / n)), (int) (i * (DEFAULT_SIZE.getHeight() / n)));
      }
    run();
  }
  private void run() {
    if (frame == null) {
      frame = new JFrame();
      frame.setContentPane(tabs);
      frame.getContentPane().add(this);
      frame.setTitle("JGraphT Adapter to JGraph Demo");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
    } else {
      frame.getContentPane().add(this);
    }
  }
  private static void adjustDisplaySettings(JGraph jg) {
    jg.setPreferredSize(DEFAULT_SIZE);
    jg.setBackground(DEFAULT_BG_COLOR);
  }
  @SuppressWarnings("unchecked") private void positionVertexAt(Object vertex, int x, int y) {
    DefaultGraphCell cell = jgAdapter.getVertexCell(vertex);
    AttributeMap attr = cell.getAttributes();
    Rectangle2D bounds = GraphConstants.getBounds(attr);
    Rectangle2D newBounds = new Rectangle2D.Double(x, y, bounds.getWidth(), bounds.getHeight());
    GraphConstants.setBounds(attr, newBounds);
    AttributeMap cellAttr = new AttributeMap();
    cellAttr.put(cell, attr);
    jgAdapter.edit(cellAttr, null, null, null);
  }
}
