/*
 * Copyright (C) 2017 mark.knapp
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

import javafx.scene.Group;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * The simplest of bullets: a white circle that goes PEW PEW!
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
     * Create the graphical image and add it to a parent Group.
     */  
    @Override
    public void draw () { 
        circle = new Circle(xCenterLoc, yCenterLoc, radius*2, color);
        footPrint = circle;
        this.getChildren().add(circle);
    }
    
}   
