/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsFX;

import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.shape.Shape;

/**
 * Generic sprite object to be used as the base for all other moving objects
 * @author Mark Knapp
 */
abstract class Sprite extends Group {
 
    // The top "0,0" X,Y current location of the sprite
    protected double  xTopLeftLoc;
    protected double  yTopLeftLoc;    
    
    // The center X,Y current location of the sprite
    protected double  xCenterLoc;
    protected double  yCenterLoc;    

    // The bottom (y) middle (x) current location of the sprite
    protected double  xBottomCenterLoc;
    protected double  yBottomCenterLoc;     
    
    // The X,Y delta that the sprite will move in a frame. This is calculated based on angle and speed.
    protected double  xVel = 0;
    protected double  yVel = 0;
    
    // The desired X,Y size of the sprite
    protected double  xSize;
    protected double  ySize;    

    // The desired X,Y size of the area of operations. Will wrap around if goes off.
    protected double  xScreen;
    protected double  yScreen; 
    
    // The distance in points the sprite will move per frame.
    protected double  speed = 0;
    
    // The radius of space the sprite occupies. This determines collision space.
    protected double  radius;
    
    // Angle in degrees of sprite path. '0' is 12 o'clock. Can be negative.
    protected double   angleFacing = 0;
    
    // Angle in degrees of sprite path. '0' is 12 o'clock. Can be negative.
    protected double   angleMoving = 0;
    
    // Number of seconds until the sprite dies, to save memory. Should be off-screen when this happens.
    protected double  timeToLive;

    // Set to true to disable auto-dying with timeToLive
    protected boolean livesForever  = true;    
    
    // If the sprite should stop moving for any reason.
    protected boolean stop = false;

    // If the sprite wraps to the other side of the screen if it goes out of bounds.
    protected boolean wraps  = true;    
    
    // If the sprite is considered dead and ready for clean-up.
    protected boolean isDead  = false;
    protected boolean isDying = false;
    
    // True is paused, false is unpaused
    protected boolean paused = false;
    
    // A shape that determines the collision.
    protected Shape   footPrint;
    
    protected Group   root;
    protected MyTimer spriteTimer;
    
    // Used by many sub-classes
    protected Random  diceRoller = new Random();
    
    // The time in nano-seconds between move-frames.
    public static final double NS_MOVE_FRAME = 10000000;
    
     
    public Sprite (Group gRoot, double sXScreen, double sYScreen, double angleStart, double speedStart) {         
        setAngleFacing(angleStart);
        xScreen = sXScreen;
        yScreen = sYScreen;
        root = gRoot;
        root.getChildren().add(this);
        setSpeed(speedStart);
        
//        BE SURE TO ADD THESE TO NON-ABSTRACT SUBCLASS CONSTRUCTORS        
//        draw();
//        spriteTimer = new MyTimer();
//        spriteTimer.start();
    }
    
    /**
     * Create the image and footprint of the sprite.
     */  
    abstract void draw ();
    
    /**
     * Return a shape that defines the sprite's footprint, for collision checking
     */  
    public Shape getFootPrint () {
        return footPrint;
    };    

    /**
     * Move the sprite to a new X,Y (TopLeft)
     *
     * @param   xTopLeft    The X coordinate of the desired location
     * @param   yTopLeft    The Y coordinate of the desired location
     */  
    public void moveTo (double xTopLeft, double yTopLeft) { 
                
        this.setTranslateX(xTopLeft - xTopLeftLoc + this.getTranslateX());
        this.setTranslateY(yTopLeft - yTopLeftLoc + this.getTranslateY());
        setXYBasedOnTopLeft(xTopLeft, yTopLeft);
        if (wraps)
            checkBounds();
    }
    
    /**
     * Check if the sprite is out of bounds and wrap around if so.
     */  
    public void checkBounds () { 
        
        if (xCenterLoc > xScreen) {
            xCenterLoc -= xScreen;
            this.setTranslateX(this.getTranslateX() - xScreen);
        }
        if (xCenterLoc < 0) {
            xCenterLoc += xScreen;
            this.setTranslateX(this.getTranslateX() + xScreen);
        }
        if (yCenterLoc > yScreen) {
            yCenterLoc -= yScreen;
            this.setTranslateY(this.getTranslateY() - yScreen);
        }
        if (yCenterLoc < 0) {
            yCenterLoc += yScreen;
            this.setTranslateY(this.getTranslateY() + yScreen);
        }
        setXYBasedOnCenter (xCenterLoc, yCenterLoc);
    }

    /**
     * Move the sprite on it's set path and velocity.
     */  
    public void move () { 
        moveTo(xTopLeftLoc+xVel, yTopLeftLoc+yVel);
    }
    
    /**
     * Calculate XY coordinates.
     */  
    public void calcXY (LocationEnum locE, double x, double y) { 
        switch (locE) {
            case CENTER:
                setXYBasedOnCenter(x, y);
                break;
            case BOTTOMCENTER:
                setXYBasedOnBottomCenter(x, y);
                break;
            case TOPLEFT:
                setXYBasedOnTopLeft(x, y);
                break;
        }
    }  

    /**
     * Calculate XY coordinates assuming we are given x,y for center.
     */  
    public void setXYBasedOnCenter (double x, double y) { 
        xCenterLoc =  x;
        yCenterLoc =  y;
        xTopLeftLoc = x - (xSize/2);
        yTopLeftLoc = y - (ySize/2);
        xBottomCenterLoc = x;
        yBottomCenterLoc = y + (ySize/2);
    }  
    
    /**
     * Calculate XY coordinates assuming we are given x,y for top-left.
     */  
    public void setXYBasedOnTopLeft (double x, double y) { 
        xTopLeftLoc = x;
        yTopLeftLoc = y;
        xBottomCenterLoc = x + (xSize/2);
        yBottomCenterLoc = y + ySize;
        xCenterLoc =  x + (xSize/2);
        yCenterLoc =  y + (ySize/2);
    }    

    /**
     * Calculate XY coordinates assuming we are given x,y for bottom-center.
     */  
    public void setXYBasedOnBottomCenter (double x, double y) { 
        xBottomCenterLoc = x;
        yBottomCenterLoc = y;
        xTopLeftLoc = x - (xSize/2);
        yTopLeftLoc = y - ySize;
        xCenterLoc = x;
        yCenterLoc = y - (ySize/2);
    }     
 
    /**
     * Simple getter of what angle the sprite in pointing to
     *
     * @return     Angle in degrees. '0' is 12 o'clock. Can be negative.
     */   
    public double getAngleFacing() {
        return angleFacing;
    }
    
    /**
     * Set the angle the sprite should be pointing to.
     *
     * @param   angle    Angle in degrees. '0' is 12 o'clock. Can be negative.
     */  
    public void setAngleFacing(double angle) {
        angleFacing = angle;
        setRotate(angleFacing);
        
    } 
        
    /**
     * Start the death process.
     * Start death animations, housekeeping, etc.
     */  
    public void startDeath () { 
        isDying = true;
        stop = true;
        finishDeath();
    }
    
    /**
     * Finish the death process.
     * Finally terminate the object.
     */  
    public void finishDeath () { 
        this.setOpacity(0);
        root.getChildren().remove(this); 
        isDead = true;
    }

    /**
     * A simple getter
     * @return    If the sprite is in the process of dying but not dead yet.
     */  
    public boolean isDying () { 
        return isDying;
    }    
    
    /**
     * A simple getter
     * @return    If the sprite is considered dead and ready for clean-up.
     */  
    public boolean isDead () { 
        return isDead;
    }    
    
    /**
     * A simple setter.
     * Calculates velocity too.
     *
     * @param   sSpeed    The distance in points the sprite will move per frame.
     */  
    public void setSpeed (double sSpeed) { 
        speed = sSpeed;
        xVel = Math.cos(Math.toRadians(angleFacing-90)) * speed;
        yVel = Math.sin(Math.toRadians(angleFacing-90)) * speed;
    }    
    
    /**
     * A simple getter
     * @return              The current X position of the sprite.
     */  
    public double getCenterX() {
        return xCenterLoc;
    }

    /**
     * A simple getter
     * @return              The current Y position of the sprite.
     */  
    public double getCenterY() {
        return yCenterLoc;
    }

    /**
     * A simple getter
     * @return              The current X position of the sprite.
     */  
    public double getTopLeftX() {
        return xTopLeftLoc;
    }

    /**
     * A simple getter
     * @return              The current Y position of the sprite.
     */  
    public double getTopLeftY() {
        return yTopLeftLoc;
    }    
    
    /**
     * A simple getter
     * @return              The current radius of the sprite.
     */  
    public double getRadius() {
        return radius;
    }
    
    /**
     * A simple getter
     * @return              The current X size of the sprite.
     */  
    public double getSizeX() {
        return xSize;
    }

    /**
     * A simple getter
     * @return              The current Y size of the sprite.
     */  
    public double getSizeY() {
        return ySize;
    }  
    
    /**
     * Does the math to see if this sprite collides with another.
     * @param   target    The Enemy object to check
     * @return            True/False if it IS a hit.
     */   
    public boolean hitByRadius(Sprite target) {
        double dx = target.getCenterX() - this.getCenterX();
        double dy = target.getCenterY() - this.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        boolean inRange = ((target.getRadius() + this.getRadius()) >= distance);
        boolean bothAlive = (!this.isDying() && !this.isDead() && !target.isDying() && !target.isDead());
        return (inRange && bothAlive);
    }
    
    /**
     * Does the math to see if this sprite collides with another.
     * @param   target    The Enemy object to check
     * @return            True/False if it IS a hit.
     */   
    public boolean hitByIntersect(Sprite target) {
//        Shape inter = Shape.intersect(this.getFootPrint(), target.getFootPrint());
//        boolean inRange = !(inter.getLayoutBounds().getHeight()<=0 || inter.getLayoutBounds().getWidth()<=0);
        Shape inter = Shape.intersect(this.getFootPrint(), target.getFootPrint());
        boolean inRange = !(inter.getLayoutBounds().getHeight()<=0 || inter.getLayoutBounds().getWidth()<=0);        
        boolean bothAlive = (!this.isDying() && !this.isDead() && !target.isDying() && !target.isDead());
        return (inRange && bothAlive);
    }    
    
    /**
     * Sets pause or unpause
     * @param sPause True for paused, False for unpaused
     */   
    public void setPause(boolean sPause) {
        if (sPause && !paused) {
            spriteTimer.stop();
        }
        if (!sPause && paused) {
            if (!isDead())
                    spriteTimer.start();
            spriteTimer.setTimerPaused(true);
        }
        paused = sPause;
    } 
    
    /**
     * Get pause or unpause
     * @return True for paused, False for unpaused
     */   
    public boolean getPause() {
        return(paused);
    } 
    
    // We need to make a custom timer since we need to add some vars to mess with
    // the pause/unpause feature
    public class MyTimer extends AnimationTimer {

        private long    timealive   = 0;
        private long    lasttime    = 0;
        private long    lastmove    = 0;
        private boolean timerPaused = false;

        public MyTimer() {
            super();
        }           

        public void setTimerPaused(boolean stp) {
            timerPaused = stp;
        }

        @Override
        public void handle(long timestamp) {
            // Move along it's path.
            if (!stop && ((timestamp - lastmove) > NS_MOVE_FRAME)) {
                move();
                lastmove = timestamp;
            }
            
            // We need to do fancy math to stop the clock during a pause
            if (lasttime == 0) {
                lasttime = timestamp;
            } else { 
                if (!timerPaused) {
                    timealive += (timestamp - lasttime);
                } else {
                    timerPaused = false;
                }
                lasttime = timestamp;
            }
            // Kill the sprite after it has lived for a certain number of seconds.
            // It should be off-screen by then, and we want to avoid memory issues.
            if ((timealive > timeToLive*1000000000) && !livesForever) {
               this.stop();
               startDeath();
            }

        }
    }    
}

