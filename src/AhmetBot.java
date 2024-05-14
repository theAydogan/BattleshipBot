
import battleship.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * A Sample random shooter - Takes no precaution on double shooting and has no strategy once
 * a ship is hit - This is not a good solution to the problem!
 *
 * @author mark.yendt@mohawkcollege.ca (Dec 2021)
 */
public class AhmetBot implements BattleShipBot {
    private int gameSize;
    private BattleShip2 battleShip;
    private Random random;
    int[][] coordinates;
    private CellState[][] cell;
    private ArrayList<Point> hitCoordinates;
    private ArrayList<Point> missCoordinates;
    int totalShots = 0;


    /**
     * Constructor keeps a copy of the BattleShip instance
     * Create instances of any Data Structures and initialize any variables here
     *
     * @param b previously created battleship instance - should be a new game
     */

    @Override
    public void initialize(BattleShip2 b) {
        battleShip = b;
        gameSize = b.BOARD_SIZE;
        cell = new CellState[gameSize][gameSize];
        hitCoordinates = new ArrayList<Point>();
        missCoordinates = new ArrayList<Point>();

        // Need to use a Seed if you want the same results to occur from run to run
        // This is needed if you are trying to improve the performance of your code

        random = new Random(0xAAAAAAAA);   // Needed for random shooter - not required for more systematic approaches
        coordinates = new int[gameSize][gameSize];
        for (int i = 0; i < coordinates.length; i++) {
            for (int j = 0; j < coordinates.length; j++) {
                coordinates[i][j] = 0;
            }
        }

        for (int i = 0; i < gameSize; i++) {//Populate the whole gameboard to be CellState.Empty
            for (int j = 0; j < gameSize; j++) {
                cell[i][j] = CellState.Empty;
            }
        }
    }

    /**
     * Create a random shot and calls the battleship shoot method
     * Put all logic here (or in other methods called from here)
     * The BattleShip API will call your code until all ships are sunk
     */


    /**
     * This method randomly selects a point on the game board and shoots at it.
     * If the shot hits a ship, the method calls the findNextCrosshairDirection(Point point) method to determine the next direction to shoot in, and continues to shoot in that direction until the ship is sunk.
     * If the shot misses, the method simply returns and waits for the next turn.
     * **/
    @Override
    public void fireShot() {
        int x = random.nextInt(gameSize);
        int y = random.nextInt(gameSize);
        Point newShot = new Point(x, y);
        while (cell[x][y] != CellState.Empty) {
            x = random.nextInt(gameSize);
            y = random.nextInt(gameSize);
            newShot = new Point(x, y);
        }
        boolean hit = takeShot(newShot);
        if (hit) {
            String direction = findNextCrosshairDirection(newShot);
            //boolean foundDirectionToShoot = false;
            Point crosshairShot = null;
            while (!direction.equals("")) {
                crosshairShot = getNextPointInDirection(direction, newShot);
                boolean crosshairHit = takeShot(crosshairShot);

                if (!crosshairHit) {
                    direction = findNextCrosshairDirection(newShot);
                } else {
                    //foundDirectionToShoot = true;
                    Point nextShot = getNextPointInDirection(direction, crosshairShot);
                    while (isLegal(nextShot)) {
                        boolean nextShotHit = takeShot(nextShot);
                        if (nextShotHit) {
                            nextShot = getNextPointInDirection(direction, nextShot);
                        }
                        else {
                            break;
                        }
                    }
                    direction = findNextCrosshairDirection(newShot);
                }
                //begin crosshair search
                //shoot upwards if empty and legal
                //otherwise shoot right/left/down
                //if none are legal, break out

                //shoot at the location if valid
                // if we miss go to the next crosshair direction if possible
                // if we hit, keep hitting in direction.
            }
        }

    }

    /**
     * This method takes a Point object and returns a string that represents the next direction to shoot in.
     * The method checks the coordinates around the point in the order of "top", "bottom", "left", and "right".
     * It returns the first direction that is a valid and empty location to shoot in.
     * If all surrounding locations have already been hit, it returns an empty string.
     * **/
    public String findNextCrosshairDirection(Point point) {
        if (point.y - 1 >= 0 && cell[point.x][point.y - 1] == CellState.Empty) {
            return "Top";
        }
        if (point.y + 1 < gameSize && cell[point.x][point.y + 1] == CellState.Empty) {
            return "Bottom";
        }

        if (point.x - 1 >= 0 && cell[point.x - 1][point.y] == CellState.Empty) {
            return "Left";
        }
        if (point.x + 1 < gameSize && cell[point.x + 1][point.y] == CellState.Empty) {
            return "Right";
        }
        return "";
    }

    /**
    * This method takes a Point object and calls the shoot method of the BattleShip2 instance with that point.
     * If the shot hits a ship, the method sets the cell at that point to CellState.Hit and adds the point to the hitCoordinates ArrayList.
     * If the shot misses, the method sets the cell at that point to CellState.Miss and adds the point to the missCoordinates ArrayList.
     * The method returns a boolean value indicating whether the shot was a hit or not.
    * **/
    public boolean takeShot(Point point) {
        boolean temp = battleShip.shoot(point);

        if (temp) {
            cell[point.x][point.y] = CellState.Hit;
            hitCoordinates.add(point);
        } else {
            cell[point.x][point.y] = CellState.Miss;
            missCoordinates.add(point);
        }
        return temp;
    }

    public boolean isLegal(Point point) {
        if (point.y >= 0 && point.y < gameSize && point.x >= 0 && point.x < gameSize && cell[point.x][point.y] == CellState.Empty) {
            return true;
        }
        return false;
    }

    public Point getNextPointInDirection(String direction, Point point) {
        Point shot = null;
        switch (direction) {
            case "Top" -> shot = new Point(point.x, point.y - 1);
            case "Bottom" -> shot = new Point(point.x, point.y + 1);
            case "Right" -> shot = new Point(point.x + 1, point.y);
            case "Left" -> shot = new Point(point.x - 1, point.y);
        }
        return shot;
    }

//        } else {
//            System.out.println("Missed a shot at " + newShot);
//            missCoordinates.add(newShot);
//            totalOfMisses+=1;
//            newShot = new Point(x, y);
//            if (!hitCoordinates.contains(newShot) && (!missCoordinates.contains(newShot))) {
//                battleShip.shoot(newShot);
//            }
//            if (x - 2 < 0) {
//                x = x - 1; //shift back to original position
//                if (y + 1 > 15) {
//                    y = y - 1;
//                } else if (y - 1 < 0) {
//                    y = y + 1;
//                }
//            } else if (x + 2 > 15) {
//                x = x + 1;
//                if (y + 1 > 15) {
//                    y = y - 1;
//                } else if (y - 1 < 0) {
//                    y = y + 1;
//                }
//            }


    /**
     * Authorship of the solution - must return names of all students that contributed to
     * the solution
     *
     * @return names of the authors of the solution
     */


    @Override
    public String getAuthors() {
        return "Ahmet Aydogan 000792453";
    }
}
