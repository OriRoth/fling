package org.spartan.fajita.api.uses;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import org.spartan.fajita.api.ast.Compound;

public class ASTViewer {
  JFrame frame;
  private JTabbedPane tabbedPane;

  public static void showASTs(final Compound... compounds) {
    ASTViewer astViewer = new ASTViewer();
    for (int i = 0; i < compounds.length; ++i)
      astViewer.addView(compounds[i].getRoot(), "Example " + i);
  }
  /**
   * Launch the application.
   */
  private void run() {
    EventQueue.invokeLater(new Runnable() {
      @Override public void run() {
        try {
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  /**
   * Create the application.
   * 
   * @param c
   * @param title
   */
  public ASTViewer() {
    initialize();
    run();
  }
  /**
   * Initialize the contents of the frame.
   * 
   * @param title
   */
  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 450, 500);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    tabbedPane = new JTabbedPane(SwingConstants.TOP);
    tabbedPane.setSize(frame.getWidth(), frame.getHeight());
    frame.getContentPane().add(tabbedPane, BorderLayout.NORTH);
  }
  private void addView(final Compound c, final String title) {
    tabbedPane.add(title, compoundToTree(c));
  }
  private JTree compoundToTree(final Compound root) {
    return new JTree(compoundToNode(root));
  }
  private DefaultMutableTreeNode compoundToNode(final Compound root) {
    DefaultMutableTreeNode top = new DefaultMutableTreeNode(root.toString());
    for (Compound child : root)
      top.add(compoundToNode(child));
    return top;
  }
}
