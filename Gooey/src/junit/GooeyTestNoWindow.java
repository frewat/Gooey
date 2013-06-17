package junit;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyDialog;
import edu.cnu.cs.gooey.GooeyFrame;

public class GooeyTestNoWindow {

	private static class MainClassNoWindow {
		public static void main(String[] args) {
		}
	}
	@Test(timeout=5000,expected=AssertionError.class)
	public void testNoWindowDisplayedWithinDefaultTimeout() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					MainClassNoWindow.main( null );
				}
				@Override
				public void handle(JFrame window) {
				}
			});
	}
	@Test(timeout=5000,expected=AssertionError.class)
	public void testNoDialogDisplayedWithinDefaultTimeout() {
		Gooey.capture(
			new GooeyDialog() {
				@Override
				public void invoke() {
					MainClassNoWindow.main( null );
				}
				@Override
				public void handle(JDialog window) {
				}
			});
	}

	// Exception test
	@Test(timeout=5000,expected=RuntimeException.class)
	public void testExceptionThrownInInvoke() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					throw new RuntimeException();
				}
				@Override
				public void handle(JFrame window) {
				}
			});
	}
	@Test(timeout=5000,expected=RuntimeException.class)
	public void testExceptionThrownInHandle() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					JFrame f = new JFrame();
					f.setVisible( true );
				}
				@Override
				public void handle(JFrame window) {
					throw new RuntimeException();
				}
			});
	}

	// A JFrame (not a JDialog) displayed 
	@Test(expected=AssertionError.class)
	public void testNoDialogDisplayed() {
		Gooey.capture(
			new GooeyDialog() {
				@Override
				public void invoke() {
					JFrame frame = new JFrame( "My Frame" );
					frame.setVisible( true );
				}
				@Override
				public void handle(JDialog frame) {
				}
			});
	}
	// A JDialog (not a JFrame) displayed 
	@Test(expected=AssertionError.class)
	public void testNoFrameDisplayed() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					JDialog dialog = new JDialog();
					dialog.setVisible( true );
				}
				@Override
				public void handle(JFrame frame) {
				}
			});
	}
}
