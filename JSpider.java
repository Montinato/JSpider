import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * JSpider (version: 1.1)
 * By Jimmy Y. (June 25, 2019 to July 3, 2019)
 * 
 * Problems:
 * - After multiple resizings of the component, it freezes to fixed size?
 * 
 * TODO:
 * - Play game 10 times in a row to catch any potential bugs (make sure you say yes to the dialog for new game
 * and try undo, restart, and newGame buttons)
 * - Animations?
 * 
 * Changes:
 * - 7/17/2019: reset current difficulty only
 * - 7/17/2019: fixed deal, after deal, check for cards to remove
 * - 7/13/2019: added new rule, cannot undo after cards removed
 * - 7/6/2019: fixed a score bug
 */
@SuppressWarnings("serial")
public class JSpider extends JFrame implements ActionListener, ComponentListener, WindowListener {

    public static int width = 1100;
    public static int height = 700;

    private JMenuBar menuBar;

    private JMenu menu;

    public JMenuItem newGame;
    public JMenuItem restartGame;
    public static JMenuItem undo;
    public JMenuItem deal;
    public JMenuItem changeDifficulty;
    public JMenuItem showStats;
    public JMenuItem toggleDebugMode;
    public JMenuItem howto;
    public JMenuItem about;
    public JMenuItem exit;

    public static GameBoard board = null;

    public static DataTracker tracker;

    private ImageIcon icon;

    public static boolean debug;

    static {
        try {
//        	Returns the name of the <code>LookAndFeel</code> class that implements
//            * the native system look and feel if there is one
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());			
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }

    public JSpider() 
    {
        super("JSpider");

        setMinimumSize(new Dimension(width, height));
        setLocationRelativeTo(null);

        menuBar = new JMenuBar();

        menu = new JMenu("Menu");

        newGame = new JMenuItem("New game");
        restartGame = new JMenuItem("Restart current game");
        undo = new JMenuItem("Undo");
        deal = new JMenuItem("Deal!");
        changeDifficulty = new JMenuItem("Change difficulty");
        showStats = new JMenuItem("Show statistics");
        toggleDebugMode = new JMenuItem("Enter debug mode");
        howto = new JMenuItem("How to play??");
        about = new JMenuItem("About JSpider");
        exit = new JMenuItem("Exit window");

        newGame.addActionListener(this);
        restartGame.addActionListener(this);
        undo.addActionListener(this);
        deal.addActionListener(this);
        changeDifficulty.addActionListener(this);
        showStats.addActionListener(this);
        toggleDebugMode.addActionListener(this);
        howto.addActionListener(this);
        about.addActionListener(this);
        exit.addActionListener(this);

        toggleDebugMode.setEnabled(false);

        menu.add(newGame);
        menu.add(restartGame);
        menu.add(undo);
        menu.add(deal);
        menu.add(changeDifficulty);
        menu.add(showStats);
        menu.add(toggleDebugMode);
        menu.add(howto);
        menu.add(about);
        menu.add(exit);

        menuBar.add(menu);

        setJMenuBar(menuBar);

        // Prima di iniziare a giocare scelgo la difficoltà
        selectDifficulty();		
        
        setContentPane(board);
        
        board.setInsets(getInsets());

        
        tracker = new DataTracker("stats.db");

        icon = new ImageIcon(Utility.readImage("images\\icon.png"));
        setIconImage(icon.getImage());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        addComponentListener(this);
        addWindowListener(this);

        debug = false;
    }
    
    
    // METODO OK 
    private void selectDifficulty() 
    {
        String[] difficulties = { "Easy", "Medium", "Hard" };
        String choice = (String) JOptionPane.showInputDialog(this, "Select difficulty:", "New game",
                JOptionPane.QUESTION_MESSAGE, null, difficulties, difficulties[0]);

        if (board == null && choice == null) {
            board = new GameBoard("Easy"); // if user does not select any (ie. he cancelled), then easy is
                                           // defaulted at startup
        }
        else if (board == null && choice != null) {
            board = new GameBoard(choice);
        }
        else if (choice != null) {
            board.clearCards();
            board.loadImages(choice);
            board.newGame();
        }

        // else if choice is null then do nothing
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        if (source == newGame) {
            board.newGame();
            int[] a = tracker.getData(board.getDifficulty());
            a[3]++;
        }
        else if (source == restartGame) {
            board.resetGame();
        }
        else if (source == undo) {
            board.undo();
        }
        else if (source == deal) {
            board.deal();
        }
        else if (source == changeDifficulty) {
            selectDifficulty();
        }
        else if (source == showStats) {
            JDialog dialog = new JDialog(this, true);

            dialog.setTitle("Statistics");
            dialog.setSize(190, 170);
            dialog.setLocationRelativeTo(null);
            dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            JTabbedPane tabbedPane = new JTabbedPane();

            for (String difficulty : new String[] { "Easy", "Medium", "Hard" }) {
                JPanel panel = new JPanel();

                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                int[] a = tracker.getData(difficulty);

                JLabel label1 = new JLabel("Best Score: " + a[0]);
                JLabel label2 = new JLabel("Best moves: " + a[1]);
                JLabel label3 = new JLabel("Wins: " + a[2]);
                JLabel label4 = new JLabel("Quits: " + a[3]);

                label1.setAlignmentX(JComponent.CENTER_ALIGNMENT);
                label2.setAlignmentX(JComponent.CENTER_ALIGNMENT);
                label3.setAlignmentX(JComponent.CENTER_ALIGNMENT);
                label4.setAlignmentX(JComponent.CENTER_ALIGNMENT);

                JButton button = new JButton("Reset");
                button.addActionListener(evt -> {
                    tracker.reset(difficulty);
                    dialog.dispose();
                });
                button.setAlignmentX(JComponent.CENTER_ALIGNMENT);

                panel.add(label1);
                panel.add(label2);
                panel.add(label3);
                panel.add(label4);
                panel.add(button);

                tabbedPane.addTab(difficulty, panel);
            }

            dialog.add(tabbedPane);
            dialog.setVisible(true);
        }
        else if (source == toggleDebugMode) {
            debug = !debug;
            toggleDebugMode.setText(debug ? "Exit debug mode" : "Enter debug mode");
        }
        else if (source == howto) {
            JDialog dialog = new JDialog(this, true);

            dialog.setTitle("Spider solitaire gameplay (shamelessly copied and pasted from Wikipedia)");
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(null);
            dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            JEditorPane textRegion = new JEditorPane("text/html", "");

            textRegion.setEditable(false);

            String text = "The game is played with two decks of cards for a total of 104 " +
                    "cards. Fifty-four of the cards are laid out horizontally in ten " +
                    "columns with only the top card showing. The remaining fifty cards " +
                    "are laid out in the lower right hand corner in five piles of ten with " +
                    "no cards showing." +
                    "<p>In the horizontal columns a card may be moved to any other card " +
                    "in the column as long as it is in descending numerical sequence. " +
                    "For example, a six of hearts may be moved to a seven of any suit. " +
                    "However, a sequence of cards can only be moved if they are all of " +
                    "the same suit in numerical descending order. For example, a six " +
                    "and seven of hearts may be moved to an eight of any suit, but a six " +
                    "of hearts and seven of clubs cannot be moved together. Moving the top card in a " +
                    "column allows the topmost hidden card to be turned over. This card then enters " +
                    "into the play. Other cards can be placed on it, and it can be moved to other cards " +
                    "in a sequence or to an empty column.</p>" +
                    "<p>The object of the game is to uncover all the hidden cards and by moving cards " +
                    "from one column to another to place cards in sequential order from King to Ace " +
                    "using the fewest moves. Each final sequence must be all of the same suit. Once a " +
                    "complete sequence is achieved the cards are removed from the table and 100 " +
                    "points are added to the score. Once a player has made all the moves possible with the current card layout, the player draws a new row of cards from one of the piles of ten in the right lower hand corner by "
                    +
                    "clicking on the cards. Each of the ten cards in this draw lands face up on each of the ten horizontal columns and the player then "
                    +
                    "proceeds to place these in such a way to create a sequence of cards all in one suit.</p>";

            textRegion.setText(text);

            dialog.add(new JScrollPane(textRegion));
            dialog.setVisible(true);
        }
        else if (source == about) {
            JOptionPane.showMessageDialog(this,
                    "JSpider (version: 1.0)\nThis program is built by Jimmy Y. (codingexpert123@gmail.com)",
                    "About JSpider", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            int[] a = tracker.getData(board.getDifficulty());
            a[3]++;
            tracker.writeToFile();
            System.exit(0);
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        width = e.getComponent().getWidth();
        height = e.getComponent().getHeight();

        if (debug) {
            System.err.println("width:" + width + ", height:" + height);
        }

        board.calcYCutoff();
        board.fixPiles();
        board.fixDeck();
        board.fixJunk();
        board.repaint();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        int[] a = tracker.getData(board.getDifficulty());
        a[3]++;
        tracker.writeToFile();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

}
