/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsFX;

import javafx.scene.Group;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * A star is a single point of light in the background
 * @author Mark Knapp
 */
public final class Star extends Sprite {
    
    protected   int     starClass;
    protected   Circle  circle; 

    public Star(Group gRoot, double xStart, double yStart, int startStarClass) {
        super(gRoot, (double)100, (double)100, xStart, yStart);
        starClass = startStarClass;
        radius = starClass;
        xSize = ySize = radius*2;
        calcXY (LocationEnum.CENTER, xStart, yStart);
        livesForever = true;
        draw();
    }

    @Override
    public void draw() {
        if (starClass > 1) {
            circle = new Circle(xCenterLoc, yCenterLoc, radius, Color.WHITE);
            circle.setStroke(Color.GRAY);
            circle.setStrokeWidth(radius/2);

            BoxBlur bb = new BoxBlur();
            bb.setWidth(radius/2);
            bb.setHeight(radius/2);
            bb.setIterations(3);
            this.setEffect(bb);
            
            this.getChildren().add(circle);
            
        } else {
            Circle circle = new Circle(xCenterLoc, yCenterLoc, 1, Color.WHITE);
            this.getChildren().add(circle);
        }
        
        footPrint = circle;
    }    
}
