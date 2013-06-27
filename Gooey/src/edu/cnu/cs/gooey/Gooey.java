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
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * <p>Gooey is a test library with static methods for capturing windows and retrieving their components.</p>
 * 
 * <p>Methods in this class are designed to be called in functional JUnit tests. There
 * are methods to capture displayed windows (frames & dialogs), and methods to query their
 * components. Once a window is captured, methods can retrieve any component by type, by name,
 * and (in the case of buttons, menus, tabs and labels) by text.  
 * </p>
 * 
 * @author robertoflores
 * 
 */
public class Gooey {
	/**
	 * Listener receiving window events from the toolkit (refer to {@link java.awt.Toolkit} for 
	 * details on the handling of GUI components). Listener is indirectly enabled by tests 
	 * expecting that a window will be displayed.
	 */
	private static final GooeyToolkitListener ToolkitListener;
	static {
		 ToolkitListener = new GooeyToolkitListener();
		 Toolkit.getDefaultToolkit().addAWTEventListener( ToolkitListener, AWTEvent.WINDOW_EVENT_MASK );
	}
	
	private static <T> boolean has(T[] array, T value) {
		for (T a : array) {
			if (a.equals( value )) {
				return true;
			}
		}
		return false;
	}
	
//	public static JMenuBar getMenuBar(JFrame frame) {
//		JMenuBar result = frame.getJMenuBar();
//		if (result == null) {
//			throw new AssertionError( "No menubar found" );
//		}
//		return result;
//	}
//	/**
//	 * Returns whether a frame has a menu
//	 * @param frame the frame being investigated
//	 * @return true if a menu bar object exists; false otherwise.
//	 */
//	public static boolean hasMenuBar(JFrame frame) {
//		return frame.getJMenuBar() != null;
//	}
	/**
	 * Returns the component held by a tab associated with the given title.  
	 * @param tabPane pane holding the tab.
	 * @param title title of the tab sought.
	 * @return component found.
	 * @throws AssertionError if no tab with the given title is found.
	 */
	public static Component getTab(JTabbedPane tabPane, final String title) {
		for (int i = 0; i < tabPane.getTabCount(); i++) {
			String tabTitle = tabPane.getTitleAt( i );
			if (tabTitle.equals( title )) {
				return tabPane.getTabComponentAt( i );
			}
		}
		throw new AssertionError( "No tab \"" + title + "\" found" );
	}
	/**
	 * Returns the label displaying the given text. The label may be nested within the container. 
	 * @param container container holding the label.
	 * @param text text of the label sought.
	 * @return label found.
	 * @throws AssertionError if no label with the given text is found.
	 */
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
	/**
	 * Returns the button displaying the given text. The button may be nested within the container.
	 * @param container container holding the label.
	 * @param text text of the button sought.
	 * @return button found.
	 * @throws AssertionError if no button with the given text is found.
	 */
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
	/**
	 * Returns the menu bar associated with a frame.
	 * 
	 * @param frame the frame whose menu is requested.
	 * @return the frame's menu bar.
	 * @throws AssertionError if no menu bar is found.
	 * @see Gooey#hasMenuBar(JFrame)
	 */
	public static JMenuBar getMenuBar(JFrame frame) {
		JMenuBar menubar = frame.getJMenuBar();
		if (menubar == null) {
			throw new AssertionError( "No menubar found" );
		}
		return menubar;
	}
	/**
	 * Given a menu bar it returns the menu associated with the given text. Text may represent the label displayed 
	 * or the programmatic name of the menu. To look for a menu by displayed label (default setting) use the 
	 * GooeyFlag.Menu.BY_TEXT option. To look for a menu by name, use the GooeyFlag.Menu.BY_NAME option. If both 
	 * were present BY_TEXT takes precedence over BY_NAME. Menu searches can be made throughout the menu structure
	 * (nested search) or under the provided menu (flat search). Nested search is the default setting. To perform a
	 * nested search use the GooeyFlag.Menu.NESTED. To perform a flat search use the GooeyFlag.Menu.FLAT. If both 
	 * were present NESTED takes precedence over FLAT.
	 * 
	 * @param bar menu bar holding the menu.
	 * @param text text of the menu sought.
	 * @param flags optional flags for text search (by label, by name) and level search (nested, flat)
	 * @return menu found.
	 * @throws AssertionError if no menu with the given text is found.
	 * @see #getMenu(JMenu, String, edu.cnu.cs.gooey.GooeyFlag.Menu...)
	 */
	public static <T extends JMenuItem> T getMenu(JMenuBar menubar, String text, GooeyFlag.Menu... flags) {
		return getMenu( menubar.getComponents(), text, flags );
	}
	/**
	 * Given a sub-menu it returns the menu associated with the given text. Text may represent the label displayed 
	 * or the programmatic name of the menu. To look for a menu by displayed label (default setting) use the 
	 * GooeyFlag.Menu.BY_TEXT option. To look for a menu by name, use the GooeyFlag.Menu.BY_NAME option. If both 
	 * were present BY_TEXT takes precedence over BY_NAME. Menu searches can be made throughout the menu structure
	 * (nested search) or under the provided menu (flat search). Nested search is the default setting. To perform a
	 * nested search use the GooeyFlag.Menu.NESTED. To perform a flat search use the GooeyFlag.Menu.FLAT. If both 
	 * were present NESTED takes precedence over FLAT.
	 * 
	 * @param submenu sub-menu holding the menu.
	 * @param text text of the menu sought.
	 * @param flags (optional) flags for text search (by label, by name) and level search (nested, flat)
	 * @return menu found.
	 * @throws AssertionError if no menu with the given text is found.
	 * @see #getMenu(JMenu, String, edu.cnu.cs.gooey.GooeyFlag.Menu...)
	 */
	public static <T extends JMenuItem> T getMenu(JMenu submenu, String text, GooeyFlag.Menu... flags) {
		return getMenu( submenu.getMenuComponents(), text, flags );
	}
	/**
	 * Funnel method for public getMenu methods. It creates a criteria based on the given text and the flags
	 * GooeyFlag.Menu.BY_NAME and BY_TEXT. It then calls the (possibly recursive) private getMenu. 
	 * @param components menu components to evaluate
	 * @param text name or label of the menu sought. 
	 * @param flags (optional) flags for text search (by label, by name) and level search (nested, flat)
	 * @return menu found.
	 * @throws AssertionError if no menu with the given text is found.
	 * @see #getMenu(Component[], GooeyCriteria, edu.cnu.cs.gooey.GooeyFlag.Menu...)
	 */
	private static <T extends JMenuItem> T getMenu(Component[] components, final String text, GooeyFlag.Menu... flags) {
		final boolean doText   = has( flags, GooeyFlag.Menu.BY_TEXT )   || !has( flags, GooeyFlag.Menu.BY_NAME );
		GooeyCriteria criteria = new GooeyCriteria() {
			@Override
			public boolean isAccepted(Component obj) {
				if (obj instanceof JMenuItem) {
					JMenuItem item = (JMenuItem) obj;
					String    str  = doText ? item.getText() : item.getName();
					if (str.equals( text )) {
						return true;
					}
				}
				return false;
			}
		};
		T next = getMenu( components, criteria, flags );
		if (next != null) {
			return next;
		}
		throw new AssertionError( "No menu \"" + text + "\" found" );
	}
	/**
	 * Final method for public getMenu methods. It receives a criteria and may search nested menus recursively 
	 * based on values in the flags parameter (GooeyFlag.Menu.NESTED or FLAT).
	 * @param components menu components to evaluate.
	 * @param criteria criteria identifying the menu sought. 
	 * @param flags (optional) flags for text search (by label, by name) and level search (nested, flat)
	 * @return menu found or null if none is found.
	 */
	@SuppressWarnings("unchecked")
	private static <T extends JMenuItem> T getMenu(Component[] components, GooeyCriteria criteria, GooeyFlag.Menu... flags) {
		boolean doNested = has( flags, GooeyFlag.Menu.NESTED ) || !has( flags, GooeyFlag.Menu.FLAT );
		for (Component c : components) {
			if (criteria.isAccepted( c )) {
				return (T) c;
			}
			if (doNested && c instanceof JMenu) {
				Component[] array = ((JMenu)c).getComponents();
				T   item  = getMenu( array, criteria, flags );
				if (item != null) {
					return item;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns a list with top-level sub-menus in the menu bar. Nested sub-menus are not traversed.  
	 * @param menubar menu bar to evaluate. 
	 * @return list of sub-menus in the menu bar.
	 */
	public static List<JMenu> getMenus(JMenuBar menubar) {
		List<JMenu> result = new ArrayList<JMenu>();
		if (menubar != null) {
			for (int i = 0; i < menubar.getMenuCount(); i++) {
				JMenu menu = menubar.getMenu( i );
				result.add( menu );
			}
		}
		return result;
	}
	/**
	 * Returns a list with all menu options in the sub-menu provided. Nested sub-menus are not traversed.
	 * @param menu sub-menu to evaluate.
	 * @return list of menu options in the sub-menu.
	 */
	public static List<JMenuItem> getMenus(JMenu menu) {
		List<JMenuItem> result = new ArrayList<JMenuItem>();
		for (Component m : menu.getMenuComponents()) {
			if (m instanceof JMenuItem) {
				result.add( (JMenuItem) m );
			}
		}
		return result;
	}

	/**
	 * Returns the first component of a class found in a container, which could have other containers.
	 * @param container container to evaluate.
	 * @param swing class of component sought.
	 * @return component found.
	 * @throws AssertionError if no component of the given class is found.
	 */
	public static <T extends Component> T getComponent(Container container, Class<T> swing) {
		return getComponent( container, swing, null );
	}
	/**
	 * Returns the first component of a class found in a container. If a name is provided (i.e., it's not null) then
	 * the component found will match both the class and name sought.
	 * @param container container to evaluate
	 * @param swing
	 * @param name
	 * @return
	 */
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
	
	public synchronized static <T extends GooeyWindow<U>, U extends Window> void capture(T doRun) {
		capture( "No window detected", doRun );
	}
	@SuppressWarnings("unchecked")
	public synchronized static <T extends GooeyWindow<U>, U extends Window> void capture(String message, T doRun) {
		// reset in cases when "doRun" is reused
		doRun.reset();
		// set capture criteria and begin listening
		ToolkitListener.setCriteria( doRun.getEventCriteria() );
		// runs "doRun.invoke" to create window
		SwingUtilities.invokeLater ( doRun );
		// "getTarget" waits until detecting window or timing out
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
