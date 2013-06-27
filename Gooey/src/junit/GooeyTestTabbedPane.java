package junit;

import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyFrame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.List;

public class GooeyTestTabbedPane {

	@SuppressWarnings("serial")
	private static class TabbedPaneDemo extends JPanel {
		public TabbedPaneDemo() {
			super(new GridLayout(1, 1));

			JTabbedPane tabbedPane = new JTabbedPane();
			// ImageIcon icon = createImageIcon("images/middle.gif");
			ImageIcon icon = null;

			JComponent panel1 = makeTextPanel("Panel #1");
			tabbedPane.addTab("Tab 1", icon, panel1, "Does nothing");
			tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

			JComponent panel2 = makeTextPanel("Panel #2");
			tabbedPane.addTab("Tab 2", icon, panel2, "Does twice as much nothing");
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

			JComponent panel3 = makeTextPanel("Panel #3");
			tabbedPane.addTab("Tab 3", icon, panel3, "Still does nothing");
			tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

			JComponent panel4 = makeTextPanel("Panel #4 (has a preferred size of 410 x 50).");
			panel4.setPreferredSize(new Dimension(410, 50));
			tabbedPane.addTab("Tab 4", icon, panel4, "Does nothing at all");
			tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

			// Add the tabbed pane to this panel.
			add(tabbedPane);

			// The following line enables to use scrolling tabs.
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		}

		protected JComponent makeTextPanel(String text) {
			JPanel panel = new JPanel(false);
			JLabel filler = new JLabel(text);
			filler.setHorizontalAlignment(JLabel.CENTER);
			panel.setLayout(new GridLayout(1, 1));
			panel.add(filler);
			return panel;
		}

		// /** Returns an ImageIcon, or null if the path was invalid. */
		// protected static ImageIcon createImageIcon(String path) {
		// java.net.URL imgURL = TabbedPaneDemo.class.getResource(path);
		// if (imgURL != null) {
		// return new ImageIcon(imgURL);
		// } else {
		// System.err.println("Couldn't find file: " + path);
		// return null;
		// }
		// }
		public static void main(String[] args) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JFrame frame = new JFrame("TabbedPaneDemo");
					frame.add(new TabbedPaneDemo(), BorderLayout.CENTER);
					frame.pack();
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.setVisible(true);
				}
			});
		}
	}

	@Test
	public void test1() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				TabbedPaneDemo.main( null );
			}
			@Override
			public void handle(JFrame frame) {
				JTabbedPane tabPane = Gooey.getComponent( frame, JTabbedPane.class );
				assertNotNull( "Incorrect result", tabPane );
				assertEquals ( "Incorrect result", 4, tabPane.getTabCount() );
				assertEquals ( "Incorrect result", 0, tabPane.getSelectedIndex() );
				
				assertEquals ( "Incorrect result", "Tab 1", tabPane.getTitleAt( 0 ));
				assertEquals ( "Incorrect result", "Tab 2", tabPane.getTitleAt( 1 ));
				assertEquals ( "Incorrect result", "Tab 3", tabPane.getTitleAt( 2 ));
				assertEquals ( "Incorrect result", "Tab 4", tabPane.getTitleAt( 3 ));
				
				List<JPanel> panels = Gooey.getComponents( tabPane, JPanel.class );
				assertEquals( "Incorrect result", 4, panels.size() );

				assertEquals  ( "Incorrect result", "Does nothing",               tabPane.getToolTipTextAt(0) );
				assertEquals  ( "Incorrect result", "Does twice as much nothing", tabPane.getToolTipTextAt(1) );
				assertEquals  ( "Incorrect result", "Still does nothing",         tabPane.getToolTipTextAt(2) );
				assertEquals  ( "Incorrect result", "Does nothing at all",        tabPane.getToolTipTextAt(3) );

				Gooey.getLabel( panels.get(0), "Panel #1" );
				Gooey.getLabel( panels.get(1), "Panel #2" );
				Gooey.getLabel( panels.get(2), "Panel #3" );
				Gooey.getLabel( panels.get(3), "Panel #4 (has a preferred size of 410 x 50)." );
			}
		});
	}
}
