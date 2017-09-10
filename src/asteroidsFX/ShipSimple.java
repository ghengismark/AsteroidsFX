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

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * This is the most basic ship: A triangle that pewpews little white balls.
 * @author Mark Knapp
 */
public class ShipSimple extends Ship {

    public ShipSimple(Group gRoot, double xScreen, double yScreen, double xStart, double yStart) {
        super(gRoot, xScreen, yScreen, 0);
        xSize = 30;
        ySize = 50;
        calcXY (LocationEnum.CENTER, xStart, yStart);

        draw();
        spriteTimer = new MyTimer();
        spriteTimer.start();
    }

    /**
     * Create the graphical image and add it to a parent Group.
     */  
    @Override
    void draw() {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
            xTopLeftLoc+(xSize/2), yTopLeftLoc,
            xTopLeftLoc, yTopLeftLoc + ySize,
            xTopLeftLoc + xSize, yTopLeftLoc + ySize });
        polygon.setFill(Color.BLUE);
        polygon.setStroke(Color.RED);
        polygon.setStrokeWidth(2);
        
        footPrint = polygon;
        this.getChildren().add(polygon);
    }
    /**
     * Calculate the X location of the tip of the gun barrel
     * @return              The X coordinate
     */  
    @Override
    public double getFiringStartPointX() {
        return xCenterLoc + Math.cos(Math.toRadians(angleFacing-90)) * xSize/2;
    }
    
    /**
     * Calculate the Y location of the tip of the gun barrel
     * @return              The Y coordinate
     */
    @Override
    public double getFiringStartPointY() {
        return yCenterLoc + Math.sin(Math.toRadians(angleFacing-90)) * ySize/2;
    } 

    /**
     * Get the bullet sub-class that this gun fires.
     * @param gRoot The bullet group that the new bullet should join
     * @param xBottomCenterStart The X,Y coord
     * @param yBottomCenterStart The X,Y coord 
     * @param angleStart The angle of the bullet path
     * @return   This should be a specific sub-class of Bullet
     */  
    public Bullet getNewBullet (Group gRoot, double xBottomCenterStart, double yBottomCenterStart, double angleStart) {
        return new BulletSimple(gRoot, xScreen, yScreen, xBottomCenterStart, yBottomCenterStart, angleStart);
    };      
}