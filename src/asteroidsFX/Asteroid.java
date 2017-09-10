/*
 * Copyright (C) 2017 mknapp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package asteroidsFX;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

/**
 * A basic asteroid, represented by a jagged polygon.
 * @author Mark Knapp
 */
public class Asteroid extends Sprite {
    
    // The parameters for our randomly generated asteroids
    public static final     double  MAX_ROTATION_RATE   = 2;
    public static final     double  MAX_SPEED           = 2;
    public static final     double  MAX_RADIUS          = 25;
    public static final     double  MIN_RADIUS          = 8;
    public static final     double  MINIMUM_MASS_FOR_SPLIT = 0.4;

    // The speed per frame in degrees that the ship rotates
    protected double    rotationRate;
    
    // The mass of the object. Used when figuring out turning and momemtum.
    protected double    mass = 1;
    
    // The power of thrust for the object
    protected double    thrust;
     
    // A percentage of how much the surface can go up and down
    protected int    bumpiness = 20;
    
    //Sound Effects
    protected           AudioClip               soundEffect;
    public final static String                  SOUND_EFFECT_FILE   = "resources/audio/Explosion.mp3";    
    
    protected Timeline    deathAnimation;
    private   int         deathIndex   = 0;
    private   AsteroidField asteroidField;

    // This constructor is if we want a new asteroid to appear at a random edge.
    public Asteroid(Group gRoot, AsteroidField sAF, double xScreen, double yScreen) {
        super(gRoot, xScreen, yScreen, 0, 0);
        asteroidField = sAF;
        rotationRate = (diceRoller.nextDouble() * MAX_ROTATION_RATE * 2) - MAX_ROTATION_RATE;
        thrust = Math.abs(diceRoller.nextDouble()) * MAX_SPEED;
        mass = Math.abs(diceRoller.nextGaussian());
        radius = (mass * (MAX_RADIUS-MIN_RADIUS)) + MIN_RADIUS;
        xSize = ySize = radius*2;
        
        
        // We want the asteroids to start on a random part of one of the random 4 border of the screen
        switch (diceRoller.nextInt(4)) {
            // Top
            case 0 :    angleFacing = diceRoller.nextDouble() * 90 + 180;
                        calcXY (LocationEnum.CENTER, (double)diceRoller.nextDouble()*xScreen, 1);
                        break;
            // Right
            case 1 :    angleFacing = diceRoller.nextDouble() * 90 + 270;
                        calcXY (LocationEnum.CENTER, xScreen - 1, (double)diceRoller.nextDouble()*yScreen);    
                        break;
            // Bottom
            case 2 :    angleFacing = diceRoller.nextDouble() * 90 ;
                        calcXY (LocationEnum.CENTER, (double)diceRoller.nextDouble()*xScreen, yScreen-1);
                        break;
            // Left
            default :    angleFacing = diceRoller.nextDouble() * 90 + 90;
                        calcXY (LocationEnum.CENTER, 1, (double)diceRoller.nextDouble()*yScreen);               
        }
        
        setAngleFacing(angleFacing + rotationRate);
        
        thrust();
        draw();
        spriteTimer = new MyTimer();
        spriteTimer.start();
    }

    // This constructor is if we want to spawn a smaller asteroid from the corpse of a larger one
    public Asteroid(Group gRoot, AsteroidField sAF, double xScreen, double yScreen, double sMass, double xCenter, double yCenter) {
        super(gRoot, xScreen, yScreen, 0, 0);
        asteroidField = sAF;
        rotationRate = (diceRoller.nextDouble() * MAX_ROTATION_RATE * 2) - MAX_ROTATION_RATE;
        thrust = Math.abs(diceRoller.nextDouble()) * MAX_SPEED;
        mass = sMass;
        radius = (mass * (MAX_RADIUS-MIN_RADIUS)) + MIN_RADIUS;
        xSize = ySize = radius*2;
        
        angleFacing = diceRoller.nextDouble() * 360;
        calcXY (LocationEnum.CENTER, xCenter, yCenter);
        
        setAngleFacing(angleFacing + rotationRate);
        
        thrust();
        draw();
        spriteTimer = new MyTimer();
        spriteTimer.start();
    }
    
    /**
     * Move the sprite on it's set path and velocity.
     */  
    @Override
    public void move () { 
        moveTo(xTopLeftLoc+xVel, yTopLeftLoc+yVel);
        setAngleFacing(angleFacing + rotationRate);
    }    

    /**
     * Fire the forward thrusters.
     */  
    public void thrust() {
        xVel += Math.cos(Math.toRadians(angleFacing-90)) * thrust;
        yVel += Math.sin(Math.toRadians(angleFacing-90)) * thrust;
    }        

    /**
     * Create the graphical image and add it to a parent Group.
     */  
    @Override
    void draw() {
        Polygon     polygon = new Polygon();
        double      depth;
        double      x;
        double      y;
        
        for (double z=0;z<24;z++) {
            depth = radius*((double)(100+bumpiness-diceRoller.nextInt(bumpiness*2))/100);
            x = Math.cos(Math.toRadians(z*15))*depth + xCenterLoc;
            y = Math.sin(Math.toRadians(z*15))*depth + yCenterLoc;
            polygon.getPoints().addAll(x,y);
        }        

        polygon.setFill(Color.YELLOW);
        polygon.setStroke(Color.RED);
        polygon.setStrokeWidth(2);
        
        footPrint = polygon;
        this.getChildren().add(polygon);
    }
    
    /**
     * Start the death process.
     * Start death animations, housekeeping, etc.
     */ 
    @Override
    public void startDeath () { 
        isDying = true;
        stop = true;
        
        // If the asteroid is not too small it should break up into 1 or 2 smaller ones
        if (mass > MINIMUM_MASS_FOR_SPLIT) {
            asteroidField.add(mass/2, xCenterLoc, yCenterLoc);
            if (diceRoller.nextBoolean()) {
                asteroidField.add(mass/2, xCenterLoc, yCenterLoc);
            }
        }
        
        EventHandler<ActionEvent> deathEventHandler = e -> {
            if (deathIndex > 6) {
                finishDeath();
            }
            this.setOpacity(1-(0.15*deathIndex++));
        };    
        deathAnimation = new Timeline(new KeyFrame(Duration.millis(50), deathEventHandler));
        deathAnimation.setCycleCount(8);
        deathAnimation.play();
        
        // KAPOW! Sound effects of the asteroid exploding
        soundEffect = new AudioClip(getClass().getClassLoader().getResource(SOUND_EFFECT_FILE).toString());
        soundEffect.play();
    }         
}
