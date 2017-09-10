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
import java.util.Random;
import javafx.scene.Group;

/**
 *
 * @author Mark Knapp
 */
public class AsteroidField extends Group {
    
    protected   int                 density         = 10;
    protected   Random              diceRoller      = new Random();
    protected   double              xSize;
    protected   double              ySize;
    protected   ArrayList<Asteroid> asteroidList    = new ArrayList<Asteroid>();
    
    public AsteroidField(double xStartSize, double yStartSize){
        xSize = xStartSize;
        ySize = yStartSize;
    }
    
    /**
     * Create a new asteroid at a random border. 
     */ 
    public void add() {
       asteroidList.add(new Asteroid(this, this, xSize, ySize));
    }    

    /**
     * Create a new asteroid at a specific spot, with a specific size.
     * @param sMass The mass of the new 'roid. 0 to 1.
     * @param xCenter The y coord
     * @param yCenter The x coord
     */ 
    public void add(double sMass, double xCenter, double yCenter) {
       asteroidList.add(new Asteroid(this, this, xSize, ySize, sMass, xCenter, yCenter));
    }  
    
    /**
     * Remove all asteroids visually and from ArrayLists for the GC.
     */ 
    public void clearAll() {
        for (Iterator<Asteroid> iterator = asteroidList.iterator(); iterator.hasNext();){
            Asteroid item = iterator.next();
            this.getChildren().remove(item);
            iterator.remove();
        }
    }
    
    /**
     * Check if a Ship has collided with any asteroids in this field.
     * @param target The Ship to be checked
     * @return True if collision, false if not
     */ 
    public boolean checkForShipCollision(Ship target) {
        for (Iterator<Asteroid> iterator = asteroidList.iterator(); iterator.hasNext();){
            Asteroid item = iterator.next();
            if (item.hitByIntersect(target)) {
                return true;
            }
        }
        return false;
    }    

    /**
     * Check if any of the bullets fired(owned) by a Ship has collided with any asteroids in this field.
     * We return void instead of boolean since we handle the collision with this method.
     * @param target The Ship which owns the bullets to be checked.
     * @param scorer The Text object that we can use to update the score
     */ 
    public void checkForBulletCollision(Ship target, Text scorer) {
        ArrayList<Asteroid> needToDie = new ArrayList<Asteroid>();
        for (Iterator<Asteroid> iterator = asteroidList.iterator(); iterator.hasNext();){
            Asteroid item = iterator.next();
            for (Iterator<Bullet> iterator2 = target.bulletList.iterator(); iterator2.hasNext();){
                Bullet item2 = iterator2.next();
                // We use hitByRadius instead of hitByIntersect here, since it is much cheaper
                // and we run into performance issues otherwise with the permuntations of 
                // bullets to asteroids.
                if (item2.hitByRadius(item)) {
                    needToDie.add(item);
                    item2.startDeath();
                    iterator.remove();
                    iterator2.remove();
                    scorer.addScore(10);
                }
            }
        }
        // We cannot start an asteroid death during the above list since
        // we may add two more asteroids and that modifies the list.
        // So make a temp list "needToDie" and do it after
        for (Iterator<Asteroid> iterator = needToDie.iterator(); iterator.hasNext();){
            Asteroid item = iterator.next();
            item.startDeath();
        }
    }       
    
    /**
     * Pause or unpause the whole field.
     * @param paused True is paused. False is not.
     */  
    public void setPause(boolean paused) {
        for (Iterator<Asteroid> iterator = asteroidList.iterator(); iterator.hasNext();){
            Asteroid item = iterator.next();
            item.setPause(paused);
        }
    }    

    /**
     * See how many 'roids we have, and add more if needed.
     */  
    public void spawnAsteroids (){
        int diff = getDensity() - asteroidList.size();
        for (int x = 0; x < diff; x++){
            add();
        }
    }; 
    
    /**
     * Simple getter
     * @return The number of the minimum limit of asteroids in the field.
     */  
    public int getDensity() {
        return density;
    }

    /**
     * Simple setter
     * @param density The number of the minimum limit of asteroids in the field.
     */  
    public void setDensity(int density) {
        this.density = density;
    }    
    
}
