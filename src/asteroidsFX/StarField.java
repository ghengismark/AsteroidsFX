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
import java.util.Random;
import javafx.scene.Group;

/**
 * A Group that holds all the background stars and generates them.
 * @author Mark Knapp
 */
public class StarField extends Group {
    
    public final static int         MAX_STAR_CLASS = 3;
    
    protected   int                 density     = 100;
    protected   Random              diceRoller  = new Random();
    protected   double              xSize;
    protected   double              ySize;
    protected   ArrayList<Star>     starList    = new ArrayList<Star>();
    
    public StarField(double xStartSize, double yStartSize){
        xSize = xStartSize;
        ySize = yStartSize;
        draw();
    }
    
    void draw() {
        int randomStarClass;
        for (int x=0;x<density;x++) {
            randomStarClass = ((int)(Math.abs(diceRoller.nextGaussian()) * MAX_STAR_CLASS)) + 1;
            starList.add(new Star(this, (double)diceRoller.nextDouble()*xSize, (double)diceRoller.nextDouble()*ySize, randomStarClass));
        }
    }
}
