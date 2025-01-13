package breakout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * Feel free to completely change this code or delete it entirely.
 *
 * @author Ishan Madan
 */
public class Main extends Application {

    // constants
    public static final String TITLE = "Example JavaFX Animation";
    public static final Color DUKE_BLUE = new Color(0, 0.188, 0.529, 1);
    public static final int SIZE = 400;
    public static final int RADIUS = 5;
    public static final int FRAMES_PER_SECOND = 60;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final int PAD_START_Y = (int) (SIZE * 0.9);
    public static final int BALL_START_Y = PAD_START_Y - RADIUS;
    public static final int BALL_SPEED = 4;
    public static final int PAD_SPEED = 10;

    // game objects
    Bouncer ball;
    Scene myScene;
    Pad pad;
    static Group root;
    static ArrayList<Tile> tiles;

    // global vars
    static int lives = 3;



    // classes
    // bouncer class
    static class Bouncer {
        public Circle bouncer;
        public double xDirection;
        public double yDirection;
        public double speed;

        public Bouncer(Circle bouncer, double xDirection, double yDirection, double speed) {
            // normalization
            double hyp = Math.sqrt(xDirection*xDirection + 1);
            this.bouncer = bouncer;
            this.xDirection = xDirection / hyp;
            this.yDirection = -1 / hyp;
            this.speed = speed;
        }

        public void reverseXDirection(){
            xDirection *= -1;
        }

        public void reverseYDirection(){
            yDirection *= -1;
        }

        public void leftBounce(){
            // want negative x movement
            xDirection = Math.abs(xDirection) * -1;
            reverseYDirection();;
        }

        public void midBounce(){
            // do nt alter x movment, just contine x directional motion
            reverseYDirection();;
        }
        
        public void rightBounce(){
            // want positive x movement
            xDirection = Math.abs(xDirection);
            reverseYDirection();;
        }

        public void reset(boolean manualReset) {
            // decrease lives
            lives--;
            // reset location
            bouncer.setCenterX(SIZE/2);
            bouncer.setCenterY(BALL_START_Y);
            // reset directions
            double newX = Math.random() * 2 - 1;
            double newY = (manualReset) ? -1 : 1;
            double newHyp = Math.sqrt(newX*newX + newY*newY);
            this.xDirection = newX / newHyp;
            this.yDirection = newY / newHyp;
        }
    }

    // pad class
    static class Pad {
        public Rectangle pad;

        public Pad() {
            this.pad = new Rectangle(0, 300, 100, 10);
            pad.setFill(new Color(1, 1, 1, 1));
            this.reset();
        }

        public void reset(){
            pad.setX(SIZE/2 - pad.getWidth()/2);
            pad.setY(PAD_START_Y);
        }
    }

    // tile class
    static class Tile {
        public Rectangle tile;
        public boolean broken;
        public int powerType;

        public Tile(int x, int y, boolean broken){
            this.tile = new Rectangle(x, y, 40, 20);
            this.broken = broken;
        }

        public Tile(int x, int y, boolean broken, int powerType){
            this.tile = new Rectangle(x, y, 40, 20);
            this.broken = broken;
            this.powerType = powerType;
        }
    }


    // helper methods

    // update ball position
    void updateBallPos(){
        ball.bouncer.setCenterX(ball.bouncer.getCenterX() + ball.xDirection * ball.speed);
        ball.bouncer.setCenterY(ball.bouncer.getCenterY() + ball.yDirection * ball.speed);
    }

    // edge detection and bounce
    void edgeDetection(){
        // check left edge
        if (ball.bouncer.getCenterX() <= ball.bouncer.getRadius()){
            ball.reverseXDirection();
        }
        // check right edge
        if (ball.bouncer.getCenterX() >= SIZE - ball.bouncer.getRadius()){
            ball.reverseXDirection();
        }
        // check top edge
        if (ball.bouncer.getCenterY() <= ball.bouncer.getRadius()){
            ball.reverseYDirection();
        }
        // check bottom edge
        if (ball.bouncer.getCenterY() >= SIZE - ball.bouncer.getRadius()){
            ball.reset(false);
            pad.reset();
        }
    }

    // pad collision detection
    void padDetection(Bouncer ball, Pad pad) { 
        // grab circle and rectangle properties of the ball and pad
        Circle ballObj = ball.bouncer;
        Rectangle padObj = pad.pad;

        // check if the ball hits the left side of the pad
        if (leftPadDetection(ballObj, padObj)){
            ball.leftBounce();
        }

        // check if the ball hits the middle side of the pad
        if (midPadDetection(ballObj, padObj)){
            ball.midBounce();;
        }

        // check if the ball hits the right side of the pad
        if (rightPadDetection(ballObj, padObj)){
            ball.rightBounce();
        }
        
    }

    // left side of pad
    boolean leftPadDetection(Circle ballObj, Rectangle padObj){
        return (ballObj.getCenterX() > padObj.getX() 
                && ballObj.getCenterX() <= padObj.getX() + padObj.getWidth() / 3 
                && ballObj.getCenterY() + RADIUS >= PAD_START_Y);
    }

    // middle of pad
    boolean midPadDetection(Circle ballObj, Rectangle padObj){
        return (ballObj.getCenterX() > padObj.getX() + padObj.getWidth() / 3
                && ballObj.getCenterX() <= padObj.getX() + 2*padObj.getWidth() / 3 
                && ballObj.getCenterY() + RADIUS >= PAD_START_Y);
    }

    // right side of pad
    boolean rightPadDetection(Circle ballObj, Rectangle padObj){
        return (ballObj.getCenterX() > padObj.getX() + 2*padObj.getWidth() / 3 
                && ballObj.getCenterX() <= padObj.getX() + padObj.getWidth() 
                && ballObj.getCenterY() + RADIUS >= PAD_START_Y);
    }

    // setup tiles on game
    ArrayList<Tile> setupTiles(File input) {
        ArrayList<Tile> tiles = new ArrayList<>();
        
        try {
            Scanner s = new Scanner(input);

            // grab input
            int row = 0;
            while (s.hasNextLine()){
                String line = s.nextLine();
                
                // split input
                int col = 0;
                for (char c : line.toCharArray()){
                    if (c == '1'){
                        tiles.add(new Tile(col*40, row*20+80, false));
                    }
                    col++;
                }
                row++;
            }

            s.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
        return tiles;
    }

    // collsiion detect between ball and tile
    public static int tileCollide(Circle circle, Rectangle rectangle) {
        // get circle center coordinates
        double circleX = circle.getCenterX();
        double circleY = circle.getCenterY();
        double radius = circle.getRadius();
        
        // get rectangle bounds
        double rectX = rectangle.getX();
        double rectY = rectangle.getY();
        double rectWidth = rectangle.getWidth();
        double rectHeight = rectangle.getHeight();
        
        // find closest point on rectangle to circle center
        double closestX = Math.max(rectX, Math.min(circleX, rectX + rectWidth));
        double closestY = Math.max(rectY, Math.min(circleY, rectY + rectHeight));
        
        // calc distance between closest point and circle center
        double distanceX = circleX - closestX;
        double distanceY = circleY - closestY;
        
        // check if there's a collision at all
        double distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        if (distanceSquared > (radius * radius)) {
            return 0; // No collision
        }
        
        // determine which side was hit
        
        // get the center of the rectangle
        double rectCenterX = rectX + rectWidth / 2;
        double rectCenterY = rectY + rectHeight / 2;
        
        // calc the angle between circle center and rectangle center
        double angle = Math.toDegrees(Math.atan2(circleY - rectCenterY, circleX - rectCenterX));
        
        // normalize angle to 0-360 range
        if (angle < 0) {
            angle += 360;
        }
        
        // calc the angle threshold based on rectangle dimensions
        double angleThreshold = Math.toDegrees(Math.atan2(rectHeight, rectWidth));
        
        // determine which side was hit based on the angle
        if (angle >= 360 - angleThreshold || angle < angleThreshold) {
            return 2; // R side
        } else if (angle >= angleThreshold && angle < 180 - angleThreshold) {
            return 1; // T side
        } else if (angle >= 180 - angleThreshold && angle < 180 + angleThreshold) {
            return 2; // L side
        } else {
            return 1; // B side
        }
    }

    public static Tile checkTileCollisions(Bouncer ball, ArrayList<Tile> tiles){
        for (Tile tile : tiles){
            // get contact side (if any)
            int contactSide = tileCollide(ball.bouncer, tile.tile);

            if (contactSide != 0){
                // kill tile
                tiles.remove(tile);
                root.getChildren().remove(tile.tile);

                // bounce off side depending on side of tile that is hit
                if (contactSide == 1){
                    ball.reverseYDirection();
                } else if (contactSide == 2){
                    ball.reverseXDirection();
                }

                // kill loop if we collide to prevent concurrent modification
                break;
            }
        }

        return null;
    }

    public static void checkWin(ArrayList<Tile> tiles) {
        // if out of tiles, console FOR NOW
        // TODO
        if (tiles.isEmpty()){
            System.out.println("Next lvl");
        }
    }
    
    public static void removeRandomBlock(){
        if (!tiles.isEmpty()){
            Tile randTile = tiles.get((int) Math.floor(Math.random() * tiles.size()));
            root.getChildren().remove(randTile.tile);
            tiles.remove(randTile);
        }
    }

    @Override
    public void start(Stage stage) {
        // Initialize ball and paddle
        ball = new Bouncer(new Circle(SIZE/2, PAD_START_Y - 10 - RADIUS, RADIUS), Math.random() * 2 - 1, Math.random() * 2 - 1, BALL_SPEED);
        ball.bouncer.setFill(Color.LIGHTSTEELBLUE);
        pad = new Pad();

        // create all tiles
        tiles = setupTiles(new File("/Users/ishanmadan/Desktop/CS308/breakout_im121/src/main/resources/lvl1.txt"));

        // Set up the scene using the global myScene
        myScene = setupScene(SIZE, SIZE, DUKE_BLUE);
        root = (Group) myScene.getRoot();
        root.getChildren().addAll(ball.bouncer, pad.pad);
        
        // add all tiles
        for (Tile tile : tiles){
            root.getChildren().addAll(tile.tile);
        }

        // Set up the stage
        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();

        // Start the animation loop
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(new KeyFrame(Duration.seconds(SECOND_DELAY), e -> step(SECOND_DELAY)));
        animation.play();
    }

    public Scene setupScene(int width, int height, Paint background) {
        root = new Group();
        myScene = new Scene(root, width, height, background);

        // Handle key inputs for paddle movement
        myScene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));

        return myScene;
    }

    private void step (double elapsedTime) {
        updateBallPos();
        edgeDetection();
        padDetection(ball, pad);
        checkTileCollisions(ball, tiles);

        checkWin(tiles);
    }

    private void handleKeyInput (KeyCode code) {
        switch (code) {
            case RIGHT -> pad.pad.setX(Math.min(pad.pad.getX() + PAD_SPEED, SIZE - 20));
            case LEFT -> pad.pad.setX(Math.max(pad.pad.getX() - PAD_SPEED, 20 - pad.pad.getWidth()));
            case A -> pad.pad.setX(Math.max(pad.pad.getX() - PAD_SPEED, 20 - pad.pad.getWidth()));
            case D -> pad.pad.setX(Math.min(pad.pad.getX() + PAD_SPEED, SIZE - 20));
            case R -> {ball.reset(true); pad.reset();}
            case L -> lives++;
            case M -> lives--;
            case B -> removeRandomBlock();
            case X -> ball.reverseXDirection();
        }
    }

    public static void main (String[] args) {
        launch(args);
    }

}
