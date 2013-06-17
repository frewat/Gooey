package edu.cnu.cs.gooey;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

public class Gooey {
	private static final GooeyToolkitListener ToolkitListener = new GooeyToolkitListener();
	static {
		Toolkit.getDefaultToolkit().addAWTEventListener( ToolkitListener, AWTEvent.WINDOW_EVENT_MASK );
	}
	
	public static JMenuBar getMenuBar(JFrame frame) {
		JMenuBar result = frame.getJMenuBar();
		if (result == null) {
			throw new AssertionError( "No menubar found" );
		}
		return result;
	}
	public static JLabel getLabel(Container container, final String text) {
		String message = "No label \"" + text + "\" found";
		return getComponent( message, container, new GooeyCriteria() {
			@Override
			public boolean isAccepted(Component obj) {
				if (obj instanceof JLabel) {
					String displayed = ((JLabel) obj).getText();
					if (text.equals( displayed )) {
						return true;
					}
				}
				return false;
			}
		});
	}
	public static JButton getButton(Container container, final String text) {
		String message = "No button \"" + text + "\" found";
		return getComponent( message, container, new GooeyCriteria() {
			@Override
			public boolean isAccepted(Component obj) {
				if (obj instanceof JButton) {
					String displayed = ((JButton) obj).getText();
					if (text.equals( displayed )) {
						return true;
					}
				}
				return false;
			}
		});
	}
	public static JMenu getMenu(JMenuBar bar, String text) {
		for (int i = 0; i < bar.getMenuCount(); i++) {
			JMenu  menu      = bar .getMenu( i );
			String displayed = menu.getText();
			if (text.equals( displayed )) {
				return menu;
			}
		}
		throw new AssertionError( "No menu \"" + text + "\" found" );
	}
	public static JMenuItem getMenu(JMenu menu, final String text) {
		return getMenu( menu, text, JMenuItem.class );
	}
	public static <T extends JMenuItem> T getMenu(JMenu menu, final String text, final Class<T> swing) {
		T result = getMenuEntity( menu, new GooeyCriteria() {
			@Override
			public boolean isAccepted(Component obj) {
				return swing.isInstance(obj) && text.equals(((JMenuItem) obj).getText());
			}
		});
		if (result == null) {
			throw new AssertionError( "No menu \"" + text + "\" found" );
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	private static <T extends JMenuItem> T getMenuEntity(JMenu menu, GooeyCriteria criteria) {
		for (Component m : menu.getMenuComponents()) {
			if (criteria.isAccepted( m )) {
				return (T) m;
			}
		}
		return null;
	}
	public static List<JMenu> getMenus(JMenuBar bar) {
		List<JMenu> result = new ArrayList<JMenu>();
		if (bar != null) {
			for (int i = 0; i < bar.getMenuCount(); i++) {
				JMenu menu = bar.getMenu( i );
				result.add( menu );
			}
		}
		return result;
	}
	public static List<JMenuItem> getMenus(JMenu menu) {
		List<JMenuItem> result = new ArrayList<JMenuItem>();
		for (Component m : menu.getMenuComponents()) {
			if (m instanceof JMenuItem) {
				result.add( (JMenuItem) m );
			}
		}
		return result;
	}

	public static <T extends Component> T getComponent(Container container, Class<T> swing) {
		return getComponent( container, swing, null );
	}
	public static <T extends Component> T getComponent(Container container, final Class<T> swing, final String name) {
		String message = "No \""+ swing.getName() +"\" component" + (name == null ? "" : " \'" + name + "\'") + " found";
		return getComponent( message, container, new GooeyCriteria() {
			@Override
			public boolean isAccepted(Component obj) {
				if (swing.isInstance( obj )) {
					if (name == null || name.equals( ((Component)obj).getName() )) {
						return true;
					}
				}
				return false;
			}
		});
	}
	public static <T extends Component> T getComponent(String message, Container container, GooeyCriteria criteria) {
		T   result = getNestedComponent( container, criteria );
		if (result == null) {
			throw new AssertionError( message );
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	private static <T extends Component> T getNestedComponent(Container container, GooeyCriteria criteria) {
		Component[] components = container.getComponents();
		for (Component c : components) {
			if (criteria.isAccepted( c )) {
				return (T)c;
			}
			if (c instanceof Container) {
				T result = getNestedComponent( (Container)c, criteria );
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	public static <T extends Component> int getComponentCount(Container container, final Class<T> swing) {
		return getComponentCount( container, new GooeyCriteria() {
			@Override
			public boolean isAccepted(Component obj) {
				return swing.isInstance( obj );
			}
		});
	}
	public static int getComponentCount(Container container, GooeyCriteria criteria) {
		return countNestedComponents( container, criteria );
	}
	private static int countNestedComponents(Container container, GooeyCriteria criteria) {
		Component[] components = container.getComponents();
		int         result     = 0;
		for (Component c : components) {
			if (criteria.isAccepted( c )) {
				result++;
			}
			if (c instanceof Container) {
				result += countNestedComponents( (Container)c, criteria );
			}
		}
		return result;
	}

	public static <T extends Component> List<T> getComponents(Container container, final Class<T> swing) {
		return getComponents( container, new GooeyCriteria() {
			@Override
			public boolean isAccepted(Component obj) {
				return swing.isInstance( obj );
			}
		});
	}
	public static <T extends Component> List<T> getComponents(Container container, GooeyCriteria criteria) {
		List<T> result = new ArrayList<T>();
		addNestedComponents( container, criteria, result );
		return result;
	}
	@SuppressWarnings("unchecked")
	private static <T extends Component> void addNestedComponents(Container container, GooeyCriteria criteria, List<T> result) {
		Component[] components = container.getComponents();
		for (Component c : components) {
			if (criteria.isAccepted( c )) {
				result.add( (T)c );
			}
			if (c instanceof Container) {
				addNestedComponents( (Container)c, criteria, result );
			}
		}
	}
	
	public synchronized static <T extends GooeyRunnable<U>, U extends Window> void capture(T doRun) {
		capture( "No window detected", doRun );
	}
	@SuppressWarnings("unchecked")
	public synchronized static <T extends GooeyRunnable<U>, U extends Window> void capture(String message, T doRun) {
		// set capture criteria
		ToolkitListener.setCriteria( doRun.getEventCriteria() );
		// run code creating window
		SwingUtilities.invokeLater ( doRun );
		// wait until detecting window or timeout
		U   window = (U) ToolkitListener.getTarget();
		if (window != null) {
			doRun.handle( window );
		}
		// wait until doRun.invoke finishes running
		doRun.finish();
		if (window == null) {
			throw new AssertionError( message );
		}
	}
}
