/**
 * <p>Copyright: Copyright (c) 2013, JoSE Group, Christopher Newport University. 
 * Permission to use, copy, modify, distribute and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice appear in all copies and that both that copyright
 * notice and this permission notice appear in supporting documentation.  
 * The JoSE Group makes no representations about the suitability
 * of  this software for any purpose. It is provided "as is" without express
 * or implied warranty.</p>
 * <p>Company: JoSE Group, Christopher Newport University</p>
 */

package edu.cnu.cs.gooey;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

public abstract class GooeyWindow <T extends Window> implements Runnable {
	/**
	 * Listener receiving window events from the toolkit (refer to {@link java.awt.Toolkit} for 
	 * details on the handling of GUI components). Listener is indirectly enabled by tests 
	 * expecting that a window will be displayed.
	 */
	private static final GooeyToolkitListener LISTENER;
	static {
		 LISTENER = new GooeyToolkitListener();
		 Toolkit.getDefaultToolkit().addAWTEventListener( LISTENER, AWTEvent.WINDOW_EVENT_MASK );
	}

	private GooeyToolkitListener.EventCriteria captureCriteria;
	private RuntimeException                   exception;
	private AssertionError					   assertion;
	private boolean                            done;
	
	protected GooeyWindow(final Class<T> windowClass) {
		if (windowClass == null) {
			throw new IllegalArgumentException( "parameter cannot be null" );
		}
		onInit();
		captureCriteria = new GooeyToolkitListener.EventCriteria() {
			@Override
			public boolean isAccepted(Object obj, AWTEvent event) {
				if (windowClass.isInstance( obj )) {
					long id = event.getID();
					if ( id == WindowEvent.WINDOW_OPENED ) {
						return true;
					}
				}
				return false;
			}
		};
	}
	public abstract void invoke();
	public abstract void test(T capturedWindow);

	@Override
	public final void run() {
		try {
			invoke();
		} catch (RuntimeException e) {
			exception = e;
		} catch (AssertionError e) {
			assertion = e;
		}
		finally {
			done = true;
		}
	}
	private void onInit() {
		done      = false;
		exception = null;
		assertion = null;
	}
	/**
	 * This method initializes (by calling {@link #onInit()}) the state of this object (in cases of reuse) 
	 * and allows subclasses to add their own customization (by calling {@link #onBeforeCapture()}).
	 * This method is called (see {@link #edu.cnu.cs.gooey.Gooey.capture}) before capturing a window.  
	 */
	private final void beforeCapture() {
		onInit();
	}
	private final void waitInvokeEnding() {
		Thread thread = Thread.currentThread();
		while (!done) {
			synchronized( thread ) {
				try {
					thread.wait( 10 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (exception != null) {
			throw exception;
		}
		if (assertion != null) {
			throw assertion;
		}
	}

	/**
	 * This method uses the capture mechanism in {@link #GooeyWindow.capture}, which calls 
	 * the method displaying a window, waits for the window to display (within a timeout period) and calls 
	 * the method testing this window. 
	 * The parameter gWindow is an instance of GooeyWindow that implements methods: <code>invoke</code> 
	 * (calls the code to display a window) and <code>test</code> (calls the code to test the window). 
	 * If no window is detected within a waiting period the method throws an AssertionError.  
	 * @param noWindowMessage jUnit message when window cannot be captured. 
	 * @param gWindow interface to display and handle the test of a window.
	 * @throws IllegalArgumentException if either parameter is null.
	 * @throws AssertionError if no window is displayed.
	 */
	@SuppressWarnings("unchecked")
	public synchronized void capture(String noWindowMessage) {
		if (noWindowMessage == null) {
			throw new IllegalArgumentException( "parameter cannot be null" );
		}
		// resets in cases when this GooeyWindow is reused
		beforeCapture();
		// sets capture criteria and begins listening
		LISTENER.setCriteria( captureCriteria );
		// runs method "invoke", which displays a window
		SwingUtilities.invokeLater ( this );
		// "getTarget" waits until detecting a window or timing out
		T   capturedWindow = (T) LISTENER.getTarget();
		if (capturedWindow != null) {
			test( capturedWindow );
		}
		// waits until "invoke" finishes running
		waitInvokeEnding();
		// customizable execution after capture & test
		if (capturedWindow == null) {
			throw new AssertionError( noWindowMessage );
		}
	}
}
