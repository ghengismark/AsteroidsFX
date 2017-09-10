/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsFX;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 *
 * @author mknapp
 */
public class AsteroidsFX extends Application {
    

    // Total screen size of game
    public final double              screenX                 = 1000;
    public final double              screenY                 = 800;  
    
    // The starting number of Asteroids
    public final static int          STARTING_NUM_OF_ASTEROIDS = 7;
    // How many seconds go by before the lower limit of asteroids number is increased.
    public final static int          SECONDS_TO_ADD            = 10;
    

    //Background Music
    protected           AudioClip       backgroundMusic;
    public final static String          BACKGROUND_MUSIC_FILE   = "resources/audio/BackgroundMusic.mp3";
    
    protected       BooleanProperty     aPressed                = new SimpleBooleanProperty();
    protected       BooleanProperty     wPressed                = new SimpleBooleanProperty();
    protected       BooleanProperty     sPressed                = new SimpleBooleanProperty();
    protected       BooleanProperty     dPressed                = new SimpleBooleanProperty();
    protected       BooleanProperty     pPressed                = new SimpleBooleanProperty();
    protected       BooleanProperty     oPressed                = new SimpleBooleanProperty();
    protected       BooleanProperty     spacePressed            = new SimpleBooleanProperty();
    protected       BooleanBinding      anyPressed              = aPressed.or(wPressed).or(dPressed).or(sPressed).or(spacePressed).or(pPressed).or(oPressed);
    
    protected       Scene               scene;
    protected       Text                text;
    protected       Stage               mainStage;    
    protected       Group               root;
    protected       AsteroidField       asteroidField;
    protected       StarField           starField;
    protected       Ship                playerOne;
    
    protected       Random              diceRoller              = new Random();
    
    protected       boolean             paused                  = false;
    protected       boolean             youLose                 = false;
    
    protected       AnimationTimer      statusTimer;
    protected       AnimationTimer      keyPressTimer;      

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {   
        
        mainStage = primaryStage;
        mainStage.setTitle("Star Control FX");
        
        root = new Group();
        scene = new Scene(root, screenX, screenY, Color.BLACK);
        mainStage.setScene(scene);
        text = new Text();   
        
        starField = new StarField(screenX, screenY);
        root.getChildren().add(starField);                    
        
        asteroidField = new AsteroidField(screenX, screenY);
        asteroidField.setDensity(STARTING_NUM_OF_ASTEROIDS);
        root.getChildren().add(asteroidField);
        
        playerOne = new ShipSimple(root, screenX, screenY, 300, 300);  
        
        root.getChildren().add(text.createInstructions(20,20));
        root.getChildren().add(text.createScore(screenX-200,20));  
        root.getChildren().add(text.createDiff(screenX-200,50));  
        
        backgroundMusic = new AudioClip(getClass().getClassLoader().getResource(BACKGROUND_MUSIC_FILE).toString());
        backgroundMusic.play();
        
        setupKeyPresses(scene);
        handleKeyPress();
        statusUpdate();
        
        mainStage.show();
    }

    /**
     * Resets game.
     * Basically create everything new. The Java GC will take care of the old
     * after we disconnect it.
     * 
     * NOTE: Currently disabled since it causes crashes. Just using it as init
     * for the moment
     */   
    public void reset() {
        setPause(true);
        youLose = false;
        root.getChildren().remove(text.loseLabel);
        text.setScore(0);
        playerOne.reset(screenX/2, screenY/2);
        asteroidField.clearAll();
        setPause(false);
    }  
    
    /**
     * Registers key press events to the universal booleans.
     * @param   scene    The root scene the keys are harvested from.
     */   
    private void setupKeyPresses(Scene scene) {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case P: pPressed.set(true); break; 
                case O: oPressed.set(true); break; 
                case A: aPressed.set(true); break;
                case D: dPressed.set(true); break; 
                case W: wPressed.set(true); break;
                case S: sPressed.set(true); break;                 
                case SPACE: spacePressed.set(true); break;
            }   
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case P: pPressed.set(false); break; 
                case O: oPressed.set(false); break; 
                case A: aPressed.set(false); break;
                case D: dPressed.set(false); break;
                case W: wPressed.set(false); break;
                case S: sPressed.set(false); break;  
                case SPACE: spacePressed.set(false); break;
            }   
        });          
    } 
    
    /**
     * Take action on key presses
     * 
     * @param   gun     We need to pass in the gun object that the keys control
     * @see                 AnimationTimer, anyPressed
     */   
    private void handleKeyPress() {

        keyPressTimer = new AnimationTimer() {
            long lastBullet = 0;
            long lastPause = 0;
            long lastReset = 0;
            @Override
            public void handle(long timestamp) {
                if (aPressed.get() && !paused) {
                    playerOne.rotate(false);
                }
                if (wPressed.get() && !paused) {
                    playerOne.thrust();
                }                
                if (dPressed.get() && !paused) {
                    playerOne.rotate(true);
                }
                if (pPressed.get()) {
                    if ((timestamp-lastPause >= 1*(long)1000000000)) {
                        togglePause();
                        lastPause = timestamp;
                    }
                }
                if (oPressed.get()) {
                    if ((timestamp-lastReset >= 1*(long)1000000000)) {
                        lastReset = timestamp;
                        reset();
                    }                    
                }                
                if (spacePressed.get() && !paused) {
                    playerOne.fireGun(timestamp);
                }
            }
        };
        keyPressTimer.start();
        anyPressed.addListener((obs, wasPressed, isNowPressed) -> {
            if (isNowPressed) {
                keyPressTimer.start();
            } else {
                keyPressTimer.stop();
            }
        });
    }

    
    /**
     * A constant timer that does various maintenance of game status.
     * Tracks and updates: Object collisions, game loss conditions, 
     * object cleanup, score maintenance, etc.
     * @see                 AnimationTimer
     */    
    private void statusUpdate() {
        statusTimer = new AnimationTimer() {
            long clearBulletsInterval = 0;
            long asteroidAddInterval = 0;
            @Override
            public void handle(long timestamp) {
                
                // Check if our player hit an asteroid. YOU LOSE if so.
                if (asteroidField.checkForShipCollision(playerOne) && !youLose) {
                    root.getChildren().add(text.createLoseText((screenX/2)-100, (screenY/2)-30));
                    setPause(true);
                    youLose = true;
                }
                
                // Check if we shot any asteroids
                asteroidField.checkForBulletCollision(playerOne, text);
                
                // Do house-cleaning on bullets that are old and gone
                if ((timestamp-clearBulletsInterval >= 1*(long)1000000000)) {
                    playerOne.clearDeadBullets();
                    clearBulletsInterval = timestamp;
                }

                // Periodically raise the difficulty
                if (asteroidAddInterval == 0)
                        asteroidAddInterval = timestamp;
                if ((timestamp-asteroidAddInterval >= SECONDS_TO_ADD*(long)1000000000)) {
                    asteroidField.setDensity(asteroidField.getDensity() + 1);
                    text.addDiff(1);
                    asteroidAddInterval = timestamp;
                }
                
                // Add more asteroids if we drop below the difficult number
                asteroidField.spawnAsteroids();
            }
        };
        statusTimer.start();
    }     

    /**
     * Toggles pause or unpause
     */   
    public void togglePause() {
        setPause(!paused);
    }
    
    /**
     * Sets pause or unpause
     * @param sPause True for paused, False for unpaused
     */   
    public void setPause(boolean sPause) {
        paused = sPause;
        asteroidField.setPause(sPause);
        playerOne.setPause(sPause);
        if (sPause)
            statusTimer.stop();
        else
            statusTimer.start();
    }       
 
    
}
