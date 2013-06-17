package junit;
import static org.junit.Assert.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyDialog;

public class GooeyTestJOptionPane {

	// JOptionPane: Message 
	private static class MainClassJOptionPaneMessageDialog {
		public static void main(String[] args) {
			JOptionPane.showMessageDialog( null, "Hello World" );
		}
	}
	@Test
	public void testJOptionPaneMessageDialogDisplayed() {
		Gooey.capture(
			new GooeyDialog() {
				@Override
				public void invoke() {
					MainClassJOptionPaneMessageDialog.main( null );
				}
				@Override
				public void handle(JDialog dialog) {
					assertTrue  ( "JDialog should be displayed", dialog.isShowing() );
					assertEquals( "Incorrect title", "Message",  dialog.getTitle() );

					Gooey.getLabel ( dialog, "Hello World" );
					
					JButton ok = Gooey.getButton( dialog, "OK" );

					int  count = Gooey.getComponentCount( dialog, JButton.class );
					assertEquals ( "No buttons other than OK should exist", 1, count );

					ok.doClick();
					assertFalse( "JDialog should be hidden", dialog.isShowing() );
				}
			});
	}

	// JOptionPane: Confirm
	private static class MainClassJOptionPaneConfirmDialog {
		public static void main(String[] args) {
			JOptionPane.showConfirmDialog( null, "What to do?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION );
		}
	}
	@Test
	public void testJOptionPaneConfirmDialogDisplayed() {
		Gooey.capture(
			new GooeyDialog() {
				@Override
				public void invoke() {
					MainClassJOptionPaneConfirmDialog.main( null );
				}
				@Override
				public void handle(JDialog dialog) {
					assertTrue  ( "JDialog should be displayed", dialog.isShowing() );
					assertEquals( "Incorrect title", "Confirm",  dialog.getTitle() );

					Gooey.getLabel( dialog, "What to do?" );

					Gooey.getButton( dialog, "Yes" );
					Gooey.getButton( dialog, "No" );
					JButton cancel = Gooey.getButton( dialog, "Cancel" );

					int     count  = Gooey.getComponentCount( dialog, JButton.class );
					assertEquals ( "No buttons other than YES/NO/CANCEL should exist", 3, count );

					cancel.doClick();
					assertFalse( "JDialog should be hidden", dialog.isShowing() );
				}
			});
	}

	// JOptionPane: Input 
	private static class MainClassJOptionPaneInputDialog {
		public static void main(String[] args) {
			JOptionPane.showInputDialog( null, "Type your name", "Please", JOptionPane.OK_CANCEL_OPTION );
		}
	}
	@Test
	public void testJOptionPaneInputDialogDisplayed() {
		Gooey.capture(
			new GooeyDialog() {
				@Override
				public void invoke() {
					MainClassJOptionPaneInputDialog.main( null );
				}
				@Override
				public void handle(JDialog dialog) {
					assertTrue  ( "JDialog should be displayed", dialog.isShowing() );
					assertEquals( "Incorrect title", "Please",   dialog.getTitle() );

					Gooey.getLabel( dialog, "Type your name" );

					JTextField field  = Gooey.getComponent     ( dialog, JTextField.class );
					assertEquals( "Text field should be empty", "", field.getText() );
					int        fields = Gooey.getComponentCount( dialog, JTextField.class );
					assertEquals ( "Only 1 text field should exist", 1, fields );

					Gooey.getButton( dialog, "OK" );
					JButton    cancel = Gooey.getButton( dialog, "Cancel" );

					int        count  = Gooey.getComponentCount( dialog, JButton.class );
					assertEquals ( "No buttons other than OK/CANCEL should exist", 2, count );

					cancel.doClick();
					assertFalse( "JDialog should be hidden", dialog.isShowing() );
				}
			});
	}
}
