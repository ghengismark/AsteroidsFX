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

import java.util.ArrayList;
import java.util.Iterator;
import javafx.scene.Group;

/**
 * This is the abstract class for all player ships.
 * @author Mark Knapp
 */
abstract class Ship extends Sprite {
    
    // The speed per frame in degrees that the ship rotates
    protected int       rotationRate = 3;
    
    // The mass of the ship. Used when figuring out turning and momemtum.
    protected double    mass = 1;
    
    // The power of thrust for the ship
    protected double    thrust = 0.3;
    
    // The power of thrust for the ship. The ship will potentially go thrust + maxSpeed.
    protected double    maxSpeed = 4;   
    
    // Delay between gun shots, in seconds. The lower, the faster.
    protected double  delayBetweenShots = 0.4;
    
    protected Group             bullets = new Group();
    public ArrayList<Bullet>    bulletList = new ArrayList<Bullet>();
    protected long              lastFired = 0;

    public Ship(Group gRoot, double xScreen, double yScreen, double startAngle) {
        super(gRoot, xScreen, yScreen, startAngle, 0);
        root.getChildren().add(bullets);
    }
    
    /**
     * Rotate the ship a fixed amount based on settings
     * @param   clockwise    The direction of rotation. clockwise or counter
     */  
    public void rotate (boolean clockwise) {
        if (!paused) {
            setAngleFacing(angleFacing + rotationRate * (clockwise ? 1 : -1));
        }
    }

    /**
     * Fire the forward thrusters
     */  
    public void thrust() {
        xVel += Math.cos(Math.toRadians(angleFacing-90)) * thrust;
        yVel += Math.sin(Math.toRadians(angleFacing-90)) * thrust;
        double absoluteSpeed = (Math.abs(xVel) + Math.abs(yVel));
        if (maxSpeed < absoluteSpeed) {
            xVel *= (maxSpeed / absoluteSpeed); 
            yVel *= (maxSpeed / absoluteSpeed); 
        }
    }        
    
    /**
     * Reset the ship to a starting location.
     * @param   sXTopLeftLoc    X coordinate of where it should be reset to.
     * @param   sYTopLeftLoc    Y coordinate of where it should be reset to.
     */  
    public void reset(double sXTopLeftLoc, double sYTopLeftLoc) {
        setAngleFacing(0);
        xVel = yVel = 0;
        moveTo(sXTopLeftLoc, sYTopLeftLoc);
        clearAllBullets();
    }      

    /**
     * Remove all bullets visually and from ArrayLists for the GC.
     */ 
    public void clearAllBullets() {
        for (Iterator<Bullet> iterator = bulletList.iterator(); iterator.hasNext();){
            Bullet item = iterator.next();
            bullets.getChildren().remove(item);
            iterator.remove();
        }
    }    
    
    /**
     * Calculate the X location of the tip of the gun barrel
     * @return              The X coordinate
     */  
    abstract double getFiringStartPointX();
    
    /**
     * Calculate the Y location of the tip of the gun barrel
     * @return              The Y coordinate
     */   
    abstract double getFiringStartPointY();
  
    /**
     * A simple getter
     * @return              Delay between gun shots, in seconds. The lower, the faster.
     */  
    public double getFireRate() {
        return delayBetweenShots;
    }
    
    /**
     * A simple setter
     * @param   rate    Delay between gun shots, in seconds. The lower, the faster.
     */  
    public void setFireRate(double rate) {
        delayBetweenShots = rate;
    }  
    
    /**
     * Get the bullet sub-class that this gun fires.
     * A non-abstract ship sub-class will overwrite this to assign the bullet type that the ship fires
     * @param gRoot The bullet group that the new bullet should join
     * @param xBottomCenterStart The X,Y coord
     * @param yBottomCenterStart The X,Y coord 
     * @param angleStart The angle of the bullet path
     * @return   This should be a specific sub-class of Bullet
     */  
    abstract Bullet getNewBullet (Group gRoot, double xBottomCenterStart, double yBottomCenterStart, double angleStart);    

    /**
     * Potentially fire the gun, assuming the gun is ready based on fire rate.
     * @param timestamp   A ns timestamp provided by an AnimationTimer
     */  
    public void fireGun (long timestamp){
        if ((timestamp-lastFired >= getFireRate()*(long)1000000000)) {
            bulletList.add(getNewBullet(bullets, getFiringStartPointX(), getFiringStartPointY(), getAngleFacing()));
            lastFired = timestamp;
        }
    };      
    
    /**
     * Do a sweep of bullets that timed out and remove from our ArrayList for the GC.
     */  
    public void clearDeadBullets (){
        for (Iterator<Bullet> iterator = bulletList.iterator(); iterator.hasNext();){
            Bullet item = iterator.next();
            if (item.isDead()) {
                iterator.remove();
            }
        }
    };   
    
    /**
     * Sets pause or unpause
     * @param sPause True for paused, False for unpaused
     */ 
    @Override
    public void setPause(boolean sPause) {
        if (sPause && !paused) {
            spriteTimer.stop();
        }
        if (!sPause && paused) {
            if (!isDead()) {
                spriteTimer.start();
            }
            spriteTimer.setTimerPaused(true);
            paused = false;            
        }
        paused = sPause;
        for(Bullet pBullet : bulletList) 
            pBullet.setPause(sPause);
    }     
}
