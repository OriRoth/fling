package org.spartan.fajita.api.uses;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.spartan.fajita.api.ast.Compound;

public class ASTViewer {

	private JFrame frame;

	public static void showASTs(final Compound ... compounds){
		for (int i = 0; i < compounds.length; i++)
			new ASTViewer(compounds[i],"Example "+i);
	}
	/**
	 * Launch the application.
	 */
	private void run(final Compound c) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
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
	 * @param c 
	 * @param title 
	 */
	public ASTViewer(final Compound c, final String title) {
		initialize(c, title);
		run(c);
	}

	/**
	 * Initialize the contents of the frame.
	 * @param title 
	 */
	private void initialize(final Compound c, final String title) {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setTitle(title);
		frame.add(compoundToTree(c));
	}

	private JTree compoundToTree(final Compound root){		
		return new JTree(compoundToNode(root));
	}
	
	private DefaultMutableTreeNode compoundToNode(final Compound root){
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(root.toString());
		for (Compound child : root.children)
			top.add(compoundToNode(child));
		return top;
	}
}
