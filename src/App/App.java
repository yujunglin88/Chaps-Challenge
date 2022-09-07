package App;

import App.tempDomain.Game;
import Renderer.tempDomain.*;
import Renderer.Renderer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import static App.PanelCreator.*;

/**
 * Main class of the application. Includes the main method, GUI, and the main loop.
 *
 * @author Jeff Lin
 */
public class App extends JFrame {
    private final List<String> actionNames = List.of("Move Up","Move Down","Move Left","Move Right","Pause Game",
            "Resume Game","Jump To Level 1","Jump To Level 2","Quit Game","Save And Quit Game","Reload Game");
    @SuppressWarnings("FieldMayBeFinal")
    private List<String> actionKeyBindings = new ArrayList<>(List.of("Up","Down","Left","Right","Space",
            "Escape","1","2","X","S","R"));
    private int indexOfKeyToSet = -1;
    private Controller controller;

    private Game game;

    Runnable closePhase = ()->{};


    static final long serialVersionUID = 1L;
    static final int WIDTH = 1200;
    static final int HEIGHT = 800;
    JPanel outerPanel = new JPanel();
    JPanel menuPanel = new JPanel();
    JPanel gamePanel = new JPanel();
    CardLayout outerCardLayout = new CardLayout();
    CardLayout menuCardLayout = new CardLayout();
    CardLayout gameCardLayout = new CardLayout();

    /**
     * Constructor for the App class. Initializes the GUI and the main loop.
     */
    public App(){
        assert SwingUtilities.isEventDispatchThread();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                closePhase.run();
            }}
        );
        loadScreens();
    }

    private void loadScreens(){
        outerPanel.setLayout(outerCardLayout);
        loadMenuScreen();
        loadGameScreen();
        setContentPane(outerPanel);
        transitionToMenuScreen();
    }

    /***/
    public void transitionToMenuScreen(){
        System.out.println("Toggling to menu screen");
        menuCardLayout.show(menuPanel, MENU);
        outerCardLayout.show(outerPanel, MENU);
        System.out.println("Menu shown");
    }

    /***/
    public void transitionToGameScreen(){
        System.out.println("Toggling to game screen");
        gameCardLayout.show(gamePanel, GAME);
        outerCardLayout.show(outerPanel, GAME);
        System.out.println("Game shown");
    }

    /**
     * Enters the menu screen, where the user can start a new game, load a game, quit the game, or change key bindings.
     */
    private void loadMenuScreen(){
        // shell to hold all the components
        menuPanel = new JPanel();
        menuCardLayout = new CardLayout();
        PanelCreator.configureMenuScreen(this, menuPanel, menuCardLayout, actionKeyBindings, actionNames);
        outerPanel.add(menuPanel, MENU);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
    }

    /**
     * Enters the game screen, and starts the game loop.
     * <p></p>
     * This method is called when the user clicks the "Start Game" button.
     * It initializes the game and controller, then starts the game loop.
     */
    public void loadGameScreen(){
        gamePanel = new JPanel();
        gameCardLayout = new CardLayout();

        // initialise game settings
        game = new Game();
        controller = new Controller(actionKeyBindings, game);

        var gameRenderer = new Renderer(new Maze());
        PanelCreator.configureGameScreen(gamePanel, gameCardLayout,this, gameRenderer);
        outerPanel.add(gamePanel, GAME);
//        gameCardLayout.show(gamePanel, MENU);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(900, 600));
        pack();
    }


    //================================================================================================================//
    //============================================ Setter Method =====================================================//
    //================================================================================================================//

    /**
     * Sets the index of the action to set a different key binding.
     *
     * @param indexOfKeyToSet the index of the action to set key
     */
    public void setIndexOfKeyToSet(int indexOfKeyToSet) {
        this.indexOfKeyToSet = indexOfKeyToSet;
    }

    /**
     * exits the key setting mode so another action can be selected for setting key binding.
     */
    public void exitKeySettingMode(){
        indexOfKeyToSet = -1;
    }


    //================================================================================================================//
    //============================================ Getter Method =====================================================//
    //================================================================================================================//

    /**
     * Gets the current game.
     *
     * @return the game object
     */
    public Game getGame() {
        return game;
    }

    /**
     * Gets the current controller.
     *
     * @return the controller object
     */
    public Controller getController() {
        return controller;
    }


    /**
     * Gets the index of the action to set a different key binding.
     *
     * @return the setting key
     */
    public int indexOfKeyToSet() {
        return indexOfKeyToSet;
    }

    /**
     * Returns if any action is ready to be set to  different key binding.
     *
     * @return true if the key is bound to an action, false otherwise
     */
    public boolean inSettingKeyMode(){
        return indexOfKeyToSet != -1;
    }


    //================================================================================================================//
    //============================================= Main Method ======================================================//
    //================================================================================================================//

    /**
     * Main method of the application.
     *
     * @param args No arguments required for this application
     */
    public static void main(String... args){
        SwingUtilities.invokeLater(App::new);
    }
}
