package nz.ac.vuw.ecs.swen225.gp6.recorder;

import java.util.Stack;

import org.dom4j.DocumentException;

import nz.ac.vuw.ecs.swen225.gp6.app.App;
import nz.ac.vuw.ecs.swen225.gp6.recorder.datastructures.Pair;
import nz.ac.vuw.ecs.swen225.gp6.recorder.datastructures.RecordTimeline;
import nz.ac.vuw.ecs.swen225.gp6.recorder.datastructures.ReplayTimeline;
import nz.ac.vuw.ecs.swen225.gp6.app.utilities.Actions;

/**
 * Class for replaying a recorded game.
 * The Replay class observes the App to be updated of the time, 
 * to make sure actions are in sync with the game.
 * This allows the App to still have control of the game during replay.
 *
 * @author: Jayden Hooper
 */
public class Replay implements Runnable {
    private ReplayTimeline<Actions> timeline;
    private Pair<Long, Actions> queuedAction;
    private App app;
    private long time;
    private boolean step = false;
    private boolean isRunning = false;

    /**
     * Constructor takes in an app to observe
     * @param app the app to observe
     */
    public Replay(App app){
        if(app == null) {
            throw new IllegalArgumentException("App cannot be null");
        }
        this.app = app;
        
    }

    @Override
    public void run() {
        app.getGameClock().stop();
        time = app.getGameClock().getTimePlayed();
        System.out.println(time);
        if (!isRunning) {return;}
        autoPlayActions();
    }

    /**
     * Load method will load the game from the given file name
     * @param game the name of the game to load
     * @return this replay object to chain methods
     * @throws DocumentException
     */
    public Replay load(int slot) {
        // try {
        //     this.timeline = new ReplayTimeline<Actions>(Persistency.loadRecordTimeline(slot));
        // } catch (DocumentException e) {
        //     e.printStackTrace();
        // }
        return this;
    }

    @Deprecated
    public Replay load(String game) {
        return this;
    }

    /**
     * Method queues the next action in the timeline if available.
     * If there is no next action, the method will popup a message.
     * @return this replay object to chain methods
     */
    public Replay step(){
        // System.out.println("Stepping");
        Actions.PAUSE_GAME.run(app);
        if (checkInvalid()) {
            return this;
        }
        this.queuedAction = timeline.next();
        isRunning = true;
        return this;
    }

    /**
     * Method enables the autoPlay functionality.
     * @return this replay object to chain methods
     */
    public Replay autoPlay(){
        if (checkInvalid()) {
            System.out.println("Auto Replay failed");
            return this;
        }
        System.out.println("Auto Replay tured on");
        isRunning = true;
        return this;
    }

    /**
     * Method to pause the autoplay feature.
     * @return this replay object to chain methods
     */
    public Replay pauseReplay(){
        Actions.PAUSE_GAME.run(app);
        isRunning = false;
        return this;
    }

    /**
     * Method sets the speed of the autoplay.
     * @param speed the speed to set the replay to
     * @return this replay object to chain methods
     */
    public Replay setSpeed(int speed) {
        int delay = 34 / speed;    // default delay is 34ms
        this.app.getGameClock().setReplaySpeed(delay);
        return this;
    }

    /**
     * Method exits the replay mode.
     * @return this replay object to chain methods
     */
    public Replay stopReplay(){
        System.out.println("Replay stopped");
        return this;
    }
    
    
    //================================================================================================================//
    //=========================================== Helper Methods =====================================================//
    //================================================================================================================//

    /**
     * Method checks if a timeline has been initialized.
     * If not, it will display a popup message.
     */
    private boolean checkInvalid(){
        if (timeline == null){
            System.out.println("No game loaded");
            return true;
        }
        if (!timeline.hasNext()){
            System.out.println("Please reload a game.");
            return true;
        }
        return false;
    }
    
    /**
     * Method checks if the next action in the timeline is ready to be executed.
     * If it is, it calls the executeAction method.
     */
    private void autoPlayActions() { 
        if(queuedAction != null && queuedAction.getKey() <= this.time){
            executeAction(queuedAction.getValue());
        }
        if(step) {
            isRunning = false;
            step = false;
        }
        if (!timeline.hasNext()){
            System.out.println("Replay finished"); 
            return; 
        }
        queuedAction = timeline.next();
    }

    /**
     * Method executes action.
     * Throws IllegalArgumentException if the action is null or not recognized.
     * @param action Action to be executed
     */
    private void executeAction(Actions action) throws IllegalArgumentException {
        if(action == null) {throw new IllegalArgumentException("Null action encountered");}
        action.run(app);
        throw new IllegalArgumentException("No such action: " + action);
    }
}