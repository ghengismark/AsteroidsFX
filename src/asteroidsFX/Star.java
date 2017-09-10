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
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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

    /**
     * Create the graphical image and add it to a parent Group.
     */  
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
        } else {
            circle = new Circle(xCenterLoc, yCenterLoc, 1, Color.WHITE);
        }
        
        this.getChildren().add(circle);
        footPrint = circle;
    }    
}
