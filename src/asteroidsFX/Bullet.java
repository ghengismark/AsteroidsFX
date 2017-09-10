/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsFX;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;

/**
 * Serves as the base for player-fired elements.
 * Extend this class for specialized bullets.
 *
 * @author Mark Knapp
 */
abstract class Bullet extends Sprite {
        
    public Bullet(Group gRoot, double sXScreen, double sYScreen, double angleStart, double speedStart) {
        super(gRoot, sXScreen, sYScreen, angleStart, speedStart);
        wraps = false;
    }       
    
    /**
     * Create the image of the sprite.
     */  
    abstract void draw ();
    
}   
