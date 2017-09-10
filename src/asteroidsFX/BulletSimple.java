/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsFX;

import javafx.scene.Group;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Serves as the base for player-fired elements.
 * Extend this class for specialized bullets.
 *
 * @author Mark Knapp
 */
public class BulletSimple extends Bullet {
    
    public static final double  STARTING_SPEED = 10;
    
    //Sound Effects
    protected           AudioClip               soundEffect;
    public final static String                  SOUND_EFFECT_FILE   = "resources/audio/SimpleGun.m4a";   
    
    private Color color;
    private Circle circle;
        
    public BulletSimple(Group gRoot, double sXScreen, double sYScreen, double xBottomCenterStart, double yBottomCenterStart, double angleStart) {
        super(gRoot, sXScreen, sYScreen, angleStart, STARTING_SPEED);
        radius = 2;
        xSize = ySize = radius*2;
        timeToLive = 2;
        livesForever = false;
        color = Color.WHITE;
        calcXY (LocationEnum.BOTTOMCENTER, xBottomCenterStart, yBottomCenterStart);
        
        // PEW! PEW! PEW! Sound effects
        soundEffect = new AudioClip(getClass().getClassLoader().getResource(SOUND_EFFECT_FILE).toString());
        soundEffect.play();
        
        draw();
        spriteTimer = new MyTimer();
        spriteTimer.start();
    }  
    
    /**
     * Create the image of the sprite.
     */  
    @Override
    public void draw () { 
        circle = new Circle(xCenterLoc, yCenterLoc, radius*2, color);
        footPrint = circle;
        this.getChildren().add(circle);
    }
    
}   
