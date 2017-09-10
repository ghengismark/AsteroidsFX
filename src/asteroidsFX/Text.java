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

import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * All text appears from this class.
 * Good to put it all in one class since later I might make a graphical font using this.
 * @author Mark Knapp
 */
public class Text {
    public  Label           scoreLabel;
    public  Label           diffLabel;
    public  Label           loseLabel;
    private AnimationTimer  loserTimer;
    private int             score       =   0;
    private int             diff        =   1;
    
    public Text() {
    } 
    
    /**
     * Create instructions on screen
     * @param   xLoc    The x coordinate of the top-left corner
     * @param   yLoc    The y coordinate of the top-left corner
     * @return              The JavaFX label object
     * @see                 Label
     */   
    public Label createInstructions(double xLoc, double yLoc) {
        
        
        Label instructions = new Label(
            "Use A and D to rotate.\n" + 
            "Use W to thrust.\n" + 
            "Use SPACE to fire.\n" +
            "Use P to pause and O to reset."
        );
        instructions.setTextFill(Color.WHITE);
        instructions.setTranslateX(xLoc);
        instructions.setTranslateY(yLoc);
        return instructions;
    } 
    
    /**
     * Create GAME OVER on screen
     * @param   xLoc    The x coordinate of the top-left corner
     * @param   yLoc    The y coordinate of the top-left corner
     * @return              The JavaFX label object
     * @see                 Label
     */   
    public Label createLoseText(double xLoc, double yLoc) {
        loseLabel = new Label("GAME OVER");
        loseLabel.setFont(new Font("Arial", 50));
        loseLabel.setTextFill(Color.WHITE);
        loseLabel.setTranslateX(xLoc);
        loseLabel.setTranslateY(yLoc);
        
        loserTimer = new AnimationTimer() {
            long lastFlash = 0;
            boolean lastColor = true;
            @Override
            public void handle(long timestamp) {
                if (lastFlash == 0) {
                    lastFlash = timestamp;
                }
                if ((timestamp-lastFlash >= 0.25*(long)1000000000)) {
                    if (lastColor) {
                        loseLabel.setTextFill(Color.RED);
                    } else {
                        loseLabel.setTextFill(Color.WHITE);
                    }
                    lastColor = !lastColor;
                    lastFlash = timestamp;
                }
            }
        };
        loserTimer.start();
        
        return loseLabel;
    } 
    
    /**
     * Put the score up
     * @param   xLoc    The x coordinate of the top-left corner
     * @param   yLoc    The y coordinate of the top-left corner
     * @return              The JavaFX label object
     * @see                 Label
     */ 
    public Label createScore(double xLoc, double yLoc) {
        
        scoreLabel = new Label("Score: " + score);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", 30));
        scoreLabel.setTranslateX(xLoc);
        scoreLabel.setTranslateY(yLoc);
        return scoreLabel;
    }
    
    /**
     * Simple setter
     * @param   sScore    The new score
     */ 
    public void setScore(int sScore) {       
        score = sScore;
        scoreLabel.setText("Score: " + score);
    }    

    /**
     * An additive setter
     * @param   sScore    The increment to be added to the score
     */ 
    public void addScore(int sScore) {     
        setScore(score+sScore);
    } 

    /**
     * Simple getter
     * @return    The current score
     */ 
    public int getScore() {       
        return score;
    } 
    
    /**
     * Put the difficulty up
     * @param   xLoc    The x coordinate of the top-left corner
     * @param   yLoc    The y coordinate of the top-left corner
     * @return              The JavaFX label object
     * @see                 Label
     */ 
    public Label createDiff(double xLoc, double yLoc) {
        
        diffLabel = new Label("Difficulty: " + diff);
        diffLabel.setTextFill(Color.RED);
        diffLabel.setFont(new Font("Arial", 30));
        diffLabel.setTranslateX(xLoc);
        diffLabel.setTranslateY(yLoc);
        return diffLabel;
    }
    
    /**
     * Simple setter
     * @param   sDiff    The new difficulty
     */ 
    public void setDiff(int sDiff) {       
        diff = sDiff;
        diffLabel.setText("Difficulty: " + diff);
    }    

    /**
     * An additive setter
     * @param   sDiff    The increment to be added to the difficulty
     */ 
    public void addDiff(int sDiff) {     
        setDiff(diff+sDiff);
    }      
    
    /**
     * Simple getter
     * @return    The current difficulty
     */ 
    public int getDiff() {       
        return diff;
    } 
}
