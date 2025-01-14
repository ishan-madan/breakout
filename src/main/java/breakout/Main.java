package breakout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
    static Bouncer ball;
    static Scene myScene;
    static Pad pad;
    static Group root;
    static ArrayList<Tile> tiles;
    static Stage stage;
    static Timeline animation;
    static Text livesText;
    static Text scoreText;

    // global vars
    static int lives = 3;
    static int currLevel = 1;
    static int highscore = 0;
    static int score = 0;



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
            // decrease lives if it isnt a manual reset or next level
            if (!manualReset){
                lives--;
            }
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

    // splash screen creation methods
    public static Scene createStartScreen() {
        VBox startLayout = new VBox(20); // 20 is spacing between elements
        startLayout.setAlignment(Pos.CENTER);
        
        Text title = new Text("BREAKOUT");
        title.setFont(Font.font(48));
        title.setFill(DUKE_BLUE);
        
        Text instructions = new Text("Use LEFT/RIGHT or A/D to move the paddle");
        instructions.setFont(Font.font(15));
        instructions.setFill(DUKE_BLUE);

        Text hs = new Text("Highscore: " + highscore);
        instructions.setFont(Font.font(15));
        instructions.setFill(DUKE_BLUE);
        
        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> {
            loadNewScene(1);
            animation.play();
        });
        
        startLayout.getChildren().addAll(title, instructions, hs, startButton);
        
        Scene startScene = new Scene(startLayout, SIZE, SIZE, DUKE_BLUE);
        startScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                loadNewScene(1);
                animation.play();
            }
        });
        
        return startScene;
    }

    public static Scene createEndScreen(boolean won) {
        VBox endLayout = new VBox(20);
        endLayout.setAlignment(Pos.CENTER);
        
        Text endText = new Text(won ? "YOU WIN!" : "GAME OVER");
        endText.setFont(Font.font(48));
        endText.setFill(DUKE_BLUE);

        Text scoreText = new Text((highscore < score) ? "New Highscore: " + score : "Score: " + score);
        Text hs = new Text((highscore < score) ? "CONGRATS!" : "Highscore: " + highscore);
        
        Button restartButton = new Button("Play Again");
        restartButton.setOnAction(e -> {
            currLevel = 1;
            lives = 3;
            highscore = Math.max(highscore, score);
            score = 0;
            loadNewScene(1);
            animation.play();
        });
        
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> stage.close());
        
        endLayout.getChildren().addAll(endText, restartButton, scoreText, hs, quitButton);
        
        return new Scene(endLayout, SIZE, SIZE, DUKE_BLUE);
    }


    // helper methods

    // initialize game objects
    public static void initializeGameObjects() {
        // Initialize game objects if they don't exist
        if (root == null) root = new Group();
        if (ball == null) {
            ball = new Bouncer(new Circle(SIZE/2, PAD_START_Y - 10 - RADIUS, RADIUS), 
                              Math.random() * 2 - 1, Math.random() * 2 - 1, BALL_SPEED);
            ball.bouncer.setFill(Color.LIGHTSTEELBLUE);
        }
        if (pad == null) pad = new Pad();
        if (tiles == null) tiles = new ArrayList<>();
    }

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
            score -= 10;
        }
    }

    // pad collision detection
    void padDetection() { 
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
    static ArrayList<Tile> setupTiles(File input) {
        tiles = new ArrayList<>();
        
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

    // check to see if th ball has collided with any tiles
    public static Tile checkTileCollisions(){
        for (Tile tile : tiles){
            // get contact side (if any)
            int contactSide = tileCollide(ball.bouncer, tile.tile);

            if (contactSide != 0){
                // kill tile
                tiles.remove(tile);
                root.getChildren().remove(tile.tile);

                // increase score
                score += 5;

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

    // check to see if the tiles are all killed
    public static void checkWin() {
        if (tiles.isEmpty() && currLevel < 3){
            loadNewScene(++currLevel);
            score += 20;
        } else if (tiles.isEmpty() && currLevel >= 3) {
            animation.stop();
            stage.setScene(createEndScreen(true));
        }
    }

    // check to see if out of lives
    public static void checkLoss(){
        if (lives <= 0){
            animation.stop();
            stage.setScene(createEndScreen(false));
        }
    }

    // update the UI elements
    public static void updateUI() {
        livesText.setText("Lives: " + lives);
        scoreText.setText("Score: " + score);
    }
    // set new scene
    public static void loadNewScene(int lvlNum) {
        // Initialize game objects if they don't exist
        initializeGameObjects();
        
        // Create a new root Group for the new scene
        root = new Group();

        // Clear tiles
        tiles.clear();

        // Reset pad and ball
        pad.reset();
        ball.reset(true);

        // add UI elements
        livesText = new Text(10, 20, "Lives: " + lives);
        livesText.setFill(Color.WHITE);
        livesText.setFont(Font.font(16));

        scoreText = new Text(SIZE - 80, 20, "Score: " + score);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(16));

        // Add pad and ball to the new root
        root.getChildren().addAll(pad.pad, ball.bouncer, livesText, scoreText);

        // Load new tiles
        tiles = setupTiles(new File("/Users/ishanmadan/Desktop/CS308/breakout_im121/src/main/resources/lvl" + lvlNum + ".txt"));
        for (Tile tile : tiles) {
            root.getChildren().add(tile.tile);
        }

        // Create a new scene and set it on the stage
        myScene = new Scene(root, SIZE, SIZE, DUKE_BLUE);
        
        // Add key handling to the new scene
        myScene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        
        stage.setScene(myScene);
        stage.show();
    }
    
    // remove a random block on the screen
    public static void removeRandomBlock(){
        if (!tiles.isEmpty()){
            Tile randTile = tiles.get((int) Math.floor(Math.random() * tiles.size()));
            root.getChildren().remove(randTile.tile);
            tiles.remove(randTile);
        }
    }


    @Override
    public void start(Stage tempStage) {
        stage = tempStage;
        // Initialize game objects
        ball = new Bouncer(new Circle(SIZE/2, PAD_START_Y - 10 - RADIUS, RADIUS), 
                        Math.random() * 2 - 1, Math.random() * 2 - 1, BALL_SPEED);
        ball.bouncer.setFill(Color.LIGHTSTEELBLUE);
        pad = new Pad();
        
        // Show start screen instead of going directly to the game
        stage.setScene(createStartScreen());
        stage.setTitle(TITLE);
        stage.show();
        
        // Initialize animation timeline (but don't start it yet)
        animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(new KeyFrame(Duration.seconds(SECOND_DELAY), 
                                    e -> step(SECOND_DELAY)));
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
        padDetection();
        checkTileCollisions();
        // System.out.println(lives);
        updateUI();
        checkLoss();
        checkWin();
    }

    public static void handleKeyInput (KeyCode code) {
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
