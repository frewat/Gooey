package junit;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyDialog;
import edu.cnu.cs.gooey.GooeyFrame;

public class GooeyTestMenu {

	// JFrame without menu 
	private static class MainClassJFrameWithoutMenu {
		public static void main(String[] args) {
			JFrame f = new JFrame( "I don't have a MenuBar" );
			f.setVisible( true );
		}
	}
	@Test(expected=AssertionError.class)
	public void testMainClassJFrameWithNoMenu() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					MainClassJFrameWithoutMenu.main( null );
				}
				@Override
				public void handle(JFrame frame) {
					assertEquals( "Incorrect result", "I don't have a MenuBar", frame.getTitle());
					Gooey.getMenuBar( frame );
				}
			});
	}
	// JFrame with menu 
	private static class MainClassJFrameWithMenu {
		public static void main(String[] args) {
			final JFrame f = new JFrame( "I have a MenuBar" );
			
			JMenuBar bar   = new JMenuBar();
			JMenu    zero  = new JMenu( "zero" ); 
			JMenu    one   = new JMenu( "one" ); 
			bar.add( zero );
			bar.add( one );
			JMenuItem zero1 = zero.add ( "quit" );
			JMenu     one1  = new JMenu( "A" );
			one .add( one1 );
			JMenuItem one11 = one1.add ( "dialog" );
			JMenu     one2  = new JMenu( "B" );
			one .add( one2 );
			JMenuItem one21 = one2.add ( "exception" );
			JMenuItem one3  = one .add ( "nothing" );
			f.setJMenuBar( bar );
			
			zero1.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					f.dispose();
				}				
			});
			one11.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					JOptionPane.showMessageDialog( f, "Hello World" );
				}				
			});
			one21.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					throw new RuntimeException();
				}				
			});
			one3.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					// faking that a dialog is displayed here.
				}				
			});

			f.setVisible( true );
		}
	}
	@Test
	public void testMainClassJFrameWithMenuHasMenus() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					MainClassJFrameWithMenu.main( null );
				}
				@Override
				public void handle(JFrame frame) {
					assertEquals( "Incorrect result", "I have a MenuBar", frame.getTitle());

					JMenuBar        menubar = Gooey.getMenuBar( frame );
					JMenu           zero    = Gooey.getMenu   ( menubar, "zero" );
					JMenu           one     = Gooey.getMenu   ( menubar, "one" );

					List<JMenu>     menus;
					List<JMenuItem> items;

					menus = Gooey.getMenus( menubar );
					assertEquals( "Incorrect result", 2, menus.size() );
					assertTrue  ( "Incorrect result",    menus.contains( zero ));
					assertTrue  ( "Incorrect result",    menus.contains( one  ));

					JMenuItem zero1 = Gooey.getMenu( zero, "quit" ); 

					items = Gooey.getMenus( zero );
					assertEquals( "Incorrect result", 1, items.size() );
					assertTrue  ( "Incorrect result",    items.contains( zero1 ));

					JMenu     one1  = Gooey.getMenu( one, "A" ); 
					JMenu     one2  = Gooey.getMenu( one, "B" ); 
					JMenuItem one3  = Gooey.getMenu( one, "nothing" ); 

					items = Gooey.getMenus( one );
					assertEquals( "Incorrect result", 3, items.size() );
					assertTrue  ( "Incorrect result",    items.contains( one1 ));
					assertTrue  ( "Incorrect result",    items.contains( one2 ));
					assertTrue  ( "Incorrect result",    items.contains( one3 ));

					JMenuItem one11  = Gooey.getMenu( one1, "dialog" ); 

					items = Gooey.getMenus( one1 );
					assertEquals( "Incorrect result", 1, items.size() );
					assertTrue  ( "Incorrect result",    items.contains( one11 ));

					JMenuItem one21  = Gooey.getMenu( one2, "exception" ); 

					items = Gooey.getMenus( one2 );
					assertEquals( "Incorrect result", 1, items.size() );
					assertTrue  ( "Incorrect result",    items.contains( one21 ));
					
					frame.dispose();
				}					
			});
	}
	@Test
	public void testMainClassJFrameWithMenuQuits() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					MainClassJFrameWithMenu.main( null );
				}
				@Override
				public void handle(JFrame frame) {
					JMenuBar  menubar = Gooey.getMenuBar( frame );
					JMenu     zero    = Gooey.getMenu   ( menubar, "zero" );
					JMenuItem quit    = Gooey.getMenu   ( zero,    "quit" );

					assertTrue   ( "Incorrect result", frame.isShowing() );
					quit.doClick();
					assertFalse  ( "Incorrect result", frame.isShowing() );
				}
			});
	}
	@Test
	public void testMainClassJFrameWithMenuShowsDialog() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					MainClassJFrameWithMenu.main( null );
				}
				@Override
				public void handle(JFrame frame) {
					JMenuBar        menubar = Gooey.getMenuBar( frame );
					JMenu           menu    = Gooey.getMenu   ( menubar, "one" );
					JMenu           submenu = Gooey.getMenu   ( menu,     "A" );
					final JMenuItem option  = Gooey.getMenu   ( submenu, "dialog" );

					Gooey.capture( 
						new GooeyDialog() {
							@Override
							public void invoke() {
								option.doClick();
							}
							@Override
							public void handle(JDialog dialog) {
								assertEquals( "Incorrect result", "Message", dialog.getTitle() );
								Gooey.getLabel ( dialog, "Hello World" );
								Gooey.getButton( dialog, "OK").doClick();
							}
						});
					
					frame.dispose();
				}
			});
	}
	@Test(expected=RuntimeException.class)
	public void testMainClassJFrameWithMenuThrowsException() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					MainClassJFrameWithMenu.main( null );
				}
				@Override
				public void handle(JFrame frame) {
					JMenuBar        menubar = Gooey.getMenuBar( frame );
					JMenu           menu    = Gooey.getMenu   ( menubar, "one" );
					JMenu           submenu = Gooey.getMenu   ( menu,    "B" );
					final JMenuItem option  = Gooey.getMenu   ( submenu, "exception" );

					Gooey.capture( 
						new GooeyDialog() {
							@Override
							public void invoke() {
								option.doClick();
							}
							@Override
							public void handle(JDialog dialog) {
							}
						});
					frame.dispose();
				}
			});
	}
	@Test(expected=AssertionError.class)
	public void testMainClassJFrameWithMenuDoesNothing() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					MainClassJFrameWithMenu.main( null );
				}
				@Override
				public void handle(JFrame frame) {
					JMenuBar        menubar = Gooey.getMenuBar( frame );
					JMenu           menu    = Gooey.getMenu   ( menubar, "one" );
					final JMenuItem option  = Gooey.getMenu   ( menu,    "nothing" );

					Gooey.capture( 
						new GooeyDialog() {
							@Override
							public void invoke() {
								option.doClick();
							}
							@Override
							public void handle(JDialog dialog) {
							}
						});
					frame.dispose();
				}
			});
	}
	
	@SuppressWarnings("serial")
	private static class LovesMe extends JFrame {
		public LovesMe() {
			super( "Loves Me, Loves Me Not" );
			
			JMenuBar     menubar = new JMenuBar();
			setJMenuBar( menubar );
			
			JMenu game = new JMenu( "Game" );
			JMenu help = new JMenu( "Help" );
			
			JMenuItem exit     = game.add( "Exit" );
			JMenuItem solution = help.add( "Solution" );
			JMenuItem about    = help.add( "About" );
			
			menubar.add( game );
			menubar.add( help );
			
			exit.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showConfirmDialog( null, "Want to blah?", "Exit", JOptionPane.YES_NO_OPTION );
				}
			});
			solution.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog( null, "The solution is: blah", "Solution", JOptionPane.INFORMATION_MESSAGE );
				}
			});
			about.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JOptionPane.showMessageDialog( null, "I made this program", "About", JOptionPane.INFORMATION_MESSAGE );
				}
			});
		}
	}
	@Test
	public void testHasMenu() {
		JMenuBar menuBar = Gooey.getMenuBar( new LovesMe() );
		JMenu    game    = Gooey.getMenu( menuBar, "Game" );
		JMenu    help    = Gooey.getMenu( menuBar, "Help" );
		
		List<JMenu> menus = Gooey.getMenus( menuBar );
		assertEquals( "Incorrect result", 2, menus.size() );
		assertTrue  ( "Incorrect result",    menus.contains( game ));
		assertTrue  ( "Incorrect result",    menus.contains( help ));
		
		List<JMenuItem> items = Gooey.getMenus( game );
		assertEquals( "Incorrect result", 1, items.size() );
		assertTrue  ( "Incorrect result",    items.contains( Gooey.getMenu( game, "Exit" )));
		
		items = Gooey.getMenus( help );
		assertEquals( "Incorrect result", 2, items.size() );
		assertTrue  ( "Incorrect result",    items.contains( Gooey.getMenu( help, "Solution" )));
		assertTrue  ( "Incorrect result",    items.contains( Gooey.getMenu( help, "About" )));
	}
	@Test
	public void testHasExit() {
		final JMenuBar menuBar = Gooey.getMenuBar( new LovesMe() );

		Gooey.capture( new GooeyDialog() {
			@Override
			public void invoke() {
				JMenu game = Gooey.getMenu( menuBar, "Game" );
				Gooey.getMenu( game, "Exit" ).doClick();
			}
			@Override
			public void handle(JDialog dialog) {
				assertEquals( "Incorrect result", "Exit", dialog.getTitle() );
				Gooey.getButton( dialog, "No" ).doClick();
			}
		});
		Gooey.capture( new GooeyDialog() {
			@Override
			public void invoke() {
				JMenu game = Gooey.getMenu( menuBar, "Game" );
				Gooey.getMenu( game, "Exit" ).doClick();
			}
			@Override
			public void handle(JDialog dialog) {
				assertEquals( "Incorrect result", "Exit", dialog.getTitle() );				
				Gooey.getButton( dialog, "Yes" ).doClick();
			}
		});
	}
	@Test
	public void testHasSolution() {
		final JMenuBar  menuBar = Gooey.getMenuBar( new LovesMe() );

		Gooey.capture( new GooeyDialog() {
			@Override
			public void invoke() {
				JMenu help = Gooey.getMenu( menuBar, "Help" );
				Gooey.getMenu( help, "Solution" ).doClick();
			}
			@Override
			public void handle(JDialog dialog) {
				assertEquals( "Incorrect result", "Solution", dialog.getTitle() );
				Gooey.getButton( dialog, "OK" ).doClick();
			}
		});
	}
	@Test
	public void testHasAbout() {
		final JMenuBar  menuBar = Gooey.getMenuBar( new LovesMe() );

		Gooey.capture( new GooeyDialog() {
			@Override
			public void invoke() {
				JMenu help = Gooey.getMenu( menuBar, "Help" );
				Gooey.getMenu( help, "About" ).doClick();
			}
			@Override
			public void handle(JDialog dialog) {
				assertEquals( "Incorrect result", "About", dialog.getTitle() );
				Gooey.getButton( dialog, "OK" ).doClick();
			}
		});
	}
}
