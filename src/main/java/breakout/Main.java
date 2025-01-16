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
    public static final int PAD_SPEED = 3;

    // game objects
    static ArrayList<Bouncer> balls = new ArrayList<>();
    static Scene myScene;
    static Pad pad;
    static Group root;
    static ArrayList<Tile> tiles;
    static Stage stage;
    static Timeline animation;
    static Text livesText;
    static Text scoreText;
    static Text levelText;

    // global vars
    static int lives = 3;
    static int currLevel = 1;
    static int highscore = 0;
    static int score = 0;
    static int paddleExtensionTime = 0;
    static int ballSpeedTime = 0;
    static int BALL_SPEED = 4;
    static boolean rightKey = false;
    static boolean leftKey = false;



    // classes
    // bouncer class
    static class Bouncer {
        Circle bouncer;
        double xDirection;
        double yDirection;

        public Bouncer(Circle bouncer, double xDirection, double yDirection) {
            // normalization
            double hyp = Math.sqrt(xDirection*xDirection + 1);
            this.bouncer = bouncer;
            this.xDirection = xDirection / hyp;
            this.yDirection = -1 / hyp;
            this.bouncer.setFill(Color.LIGHTSTEELBLUE);
        }

        // get methods
        public Circle getBouncer() {
            return bouncer;
        }

        public double getXDir() {
            return xDirection;
        }

        public double getYDir() {
            return yDirection;
        }

        // set methods
        public void setXDir(double val) {
            xDirection = val;
        }

        public void setYDir(double val) {
            yDirection = val;
        }

        // direction reverse methods
        public void reverseXDirection(){
            xDirection *= -1;
        }

        public void reverseYDirection(){
            yDirection *= -1;
        }

        // bounce methods
        public void dirBounce(int direction) {
            // left side
            if (direction == -1){
                xDirection = Math.abs(xDirection) * -1;
            }
            // right side
            else if (direction == 1) {
                xDirection = Math.abs(xDirection);
            }

            // boucne off pad
            reverseYDirection();
        }

        void edgeDetection(){
            // check left edge
            if (bouncer.getCenterX() <= bouncer.getRadius()){
                reverseXDirection();
                // prevent infinite bounce glitch
                bouncer.setCenterX(.001 + RADIUS);
            }
            // check right edge
            if (bouncer.getCenterX() >= SIZE - bouncer.getRadius()){
                reverseXDirection();
                bouncer.setCenterX(SIZE - RADIUS - .001);
            }
            // check top edge
            if (bouncer.getCenterY() <= bouncer.getRadius()){
                reverseYDirection();
                bouncer.setCenterY(.001 + RADIUS);
            }
            // check bottom edge
            if (bouncer.getCenterY() >= SIZE - bouncer.getRadius()){
                if (balls.size() <= 1){
                    reset(false);
                    pad.reset();
                    score -= 10;
                } else {
                    balls.remove(this);
                    root.getChildren().remove(bouncer);
                }
            }
        }

        void detectPad() { 
            Circle ballObj = bouncer;
            Rectangle padObj = pad.pad;

            // only bounce if the ball is moving downwards
            if (yDirection > 0){
                // check left side of pad
                if (getPadSection(ballObj, padObj, 1)){
                    dirBounce(-1);
                }

                // check middle of pad
                if (getPadSection(ballObj, padObj, 2)){
                    dirBounce(0);
                }

                // check right side of pad
                if (getPadSection(ballObj, padObj, 3)){
                    dirBounce(1);
                }
            }
        }

        // general method to detect asection of pad for the positional bounce
        boolean getPadSection(Circle ballObj, Rectangle padObj, int position){
            double sectionWidth = padObj.getWidth() / 3;
            double start = padObj.getX() + (position - 1) * sectionWidth;
            double end = padObj.getX() + position * sectionWidth;
            
            boolean inHorizontalBounds = ballObj.getCenterX() > start && ballObj.getCenterX() <= end;
            boolean atCorrectHeight = ballObj.getCenterY() + RADIUS >= PAD_START_Y;
            
            return inHorizontalBounds && atCorrectHeight;
        }

        public void updatePos() {
            // ball speed is faster on level 3
            double multiplier = ((currLevel == 3) ? 1.5 : 1);

            bouncer.setCenterX(bouncer.getCenterX() + xDirection * BALL_SPEED * multiplier);
            bouncer.setCenterY(bouncer.getCenterY() + yDirection * BALL_SPEED * multiplier);
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
            BALL_SPEED = 4;
        }
    }

    // pad class
    static class Pad {
        Rectangle pad;

        public Pad() {
            this.pad = new Rectangle(0, 300, 100, 10);
            pad.setFill(new Color(1, 1, 1, 1));
            this.reset();
        }

        // get methods
        public Rectangle getPad() {
            return pad;
        }

        public void reset(){
            pad.setWidth((currLevel != 1) ? 50 : 100);
            pad.setX(SIZE/2 - pad.getWidth()/2);
            pad.setY(PAD_START_Y);
        }

        void updatePos(){
            if (rightKey){
                pad.setX(Math.min(pad.getX() + PAD_SPEED, SIZE - 20));
            } 
            if (leftKey){
                pad.setX(Math.min(pad.getX() - PAD_SPEED, SIZE - 20));
            }
        }
    }

    // tile class
    static class Tile {
        Rectangle tile;
        char powerType;
        int health;

        public Tile(int x, int y, char powerType){
            this.tile = new Rectangle(x, y, 40, 20);
            this.powerType = powerType;
            this.health = (Character.isLetter(powerType)) ? 1 : Character.getNumericValue(powerType);
            setColor();
        }

        // get methods
        public Rectangle getTile() {
            return tile;
        }

        public char getPowerType() {
            return powerType;
        }

        public int getHealth() {
            return health;
        }
        
        public void setColor(){
            if(powerType == '1'){
                // standard tile
                tile.setFill(Color.BLACK);
            } else if (powerType == 'a'){
                // add a ball
                tile.setFill(Color.LIMEGREEN);
            } else if (powerType == 'p'){
                // pad extension for 5 seconds
                tile.setFill(Color.YELLOW);
            } else if (powerType == 's'){
                // speed ball up for 5 seconds
                tile.setFill(Color.PALETURQUOISE);
            } else if (powerType == 'e'){
                // explode
                tile.setFill(Color.ORANGE);
            } else {
                // multi hit block
                tile.setFill(Color.HOTPINK);
            }
        }

        public void damage(Bouncer ball) {
            health--;
            updateApperance();
            updateScore();

            if (health == 0){
                activatePowerup(ball, powerType);
                killTile();
            }
        }

        void updateApperance() {
            if (health == 1){
                tile.setFill(Color.BLACK);
            }
        }

        void updateScore() {
            if (health == 0) {
                score += (powerType != '1') ? 10 : 5;
            } else if (!Character.isAlphabetic(powerType)) {
                score += 5;
            }
        }

        void activatePowerup(Bouncer ball, char powerType){
            switch (powerType) {
                case 'a': {addBall(ball); break;}
                case 'p': {padExt(); break;}
                case 's': {speedUpBall(); break;}
                case 'e': {explode(tile.getX(), tile.getY(), ball); break;}
            }
        }

        void killTile(){
            tiles.remove(this);
            root.getChildren().remove(this.tile);
        }

        void explode(double x, double y, Bouncer ball){
            // MAKE THIS WORK
            int tileIndex = 0;

            while (tileIndex < tiles.size()){
                Tile tileToCheck = tiles.get(tileIndex);
                boolean increment = true;

                // damage the tile if it is in the exploding zone and it isnt the exploding tile itself
                if (isInExplosionRange(tileToCheck)){
                    tileToCheck.damage(ball);

                    // incrememnt forward if the tile didnt die
                    increment = (tileToCheck.getHealth() != 0);
                } 
                
                // increment if not in exploding zone or if it didnt die
                if (increment){
                    tileIndex++;
                }
                
            }
        }

        private boolean isInExplosionRange(Tile tileToCheck) {
            double xDiff = Math.abs(tileToCheck.getTile().getX() - this.getTile().getX());
            double yDiff = Math.abs(tileToCheck.getTile().getY() - this.getTile().getY());
            return xDiff <= 40.0 && yDiff <= 20.0 && tileToCheck != this;
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
        hs.setFont(Font.font(15));
        hs.setFill(DUKE_BLUE);
        
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

    public static Scene midLevelSplash(int lvl) {
        animation.pause();
        VBox midLayout = new VBox(20); // 20 is spacing between elements
        midLayout.setAlignment(Pos.CENTER);
        
        Text hs = new Text("Level " + (lvl-1) + " Complete!");
        hs.setFont(Font.font(25));
        hs.setFill(DUKE_BLUE);

        Text scoreTxt = new Text("Score: " + score);
        scoreTxt.setFont(Font.font(25));
        scoreTxt.setFill(DUKE_BLUE);
        
        Text livesTxt = new Text("You have " + lives + " lives remaining!");
        livesTxt.setFont(Font.font(15));
        livesTxt.setFill(DUKE_BLUE);
        
        Button startButton = new Button("Next Level");
        startButton.setOnAction(e -> {
            loadNewScene(lvl);
            animation.play();
        });
        
        midLayout.getChildren().addAll(hs, scoreTxt, livesTxt, startButton);
        
        Scene midScene = new Scene(midLayout, SIZE, SIZE, DUKE_BLUE);
        midScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                loadNewScene(lvl);
                animation.play();
            }
        });
        
        return midScene;
    }
    
    // helper methods

    // initialize game objects
    public static void initializeGameObjects() {
        // Initialize game objects if they don't exist
        balls.clear();
        if (root == null) root = new Group();
        if (balls == null || balls.size() == 0) {
            balls.add(new Bouncer(new Circle(SIZE/2, PAD_START_Y - 10 - RADIUS, RADIUS), 
                              Math.random() * 2 - 1, Math.random() * 2 - 1));
        }
        if (pad == null) pad = new Pad();
        if (tiles == null) tiles = new ArrayList<>();
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
                    if (c != '0'){
                        tiles.add(new Tile(col*40, row*20+80, c));
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
    public static int tileCollideDetect(Circle circle, Rectangle rectangle) {
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
    public static Tile checkTileCollisions(Bouncer ball){
        for (Tile tile : tiles){
            // get contact side (if any)
            int contactSide = tileCollideDetect(ball.bouncer, tile.getTile());

            if (contactSide != 0){
                // damage tile
                tile.damage(ball);

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
            System.out.println("Tesrt");
            stage.setScene(midLevelSplash(++currLevel));
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
    
    // reset the game
    public static void resetGame(){
        while (balls.size() > 1) {
            root.getChildren().remove(balls.remove(0).bouncer);

        }
        balls.get(0).reset(true); 
        pad.reset();
    }

    // set new scene
    public static void loadNewScene(int lvlNum) {
        // reset keydown bools
        leftKey = false;
        rightKey = false;

        // Initialize game objects if they don't exist
        initializeGameObjects();
        
        // Create a new root Group for the new scene
        root = new Group();

        // Clear tiles
        tiles.clear();

        // Reset pad and ball
        pad.reset();
        balls.get(0).reset(true);

        // make pad smaller
        if (lvlNum == 2){
            pad.pad.setWidth(50);
        } else {
            pad.pad.setWidth(100);
        }

        // add UI elements
        livesText = new Text(10, 20, "Lives: " + lives);
        livesText.setFill(Color.WHITE);
        livesText.setFont(Font.font(16));

        scoreText = new Text(SIZE - 80, 20, "Score: " + score);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(16));

        levelText = new Text(SIZE/2 - 20, 20, "Level: " + currLevel);
        levelText.setFill(Color.WHITE);
        levelText.setFont(Font.font(16));

        // Add pad and ball to the new root
        root.getChildren().addAll(pad.pad, balls.get(0).bouncer, livesText, levelText, scoreText);

        // Load new tiles
        tiles = setupTiles(new File("/Users/ishanmadan/Desktop/CS308/breakout_im121/src/main/resources/lvl" + lvlNum + ".txt"));
        for (Tile tile : tiles) {
            root.getChildren().add(tile.getTile());
        }

        // Create a new scene and set it on the stage
        myScene = new Scene(root, SIZE, SIZE, DUKE_BLUE);
        
        // Add key handling to the new scene
        myScene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        myScene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
        
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

    // add a new ball powerup
    public static void addBall(Bouncer ball){
        balls.add(new Bouncer(new Circle(ball.bouncer.getCenterX(), ball.bouncer.getCenterY(), RADIUS), 
        Math.random() * 2 - 1, Math.random() * 2 - 1));
        balls.get(balls.size() - 1).bouncer.setFill(Color.LIGHTSTEELBLUE);

        root.getChildren().add(balls.get(balls.size() - 1).bouncer);
    }

    // make paddle wider powerup
    public static void padExt(){
        if (pad.pad.getWidth() != 150){
            pad.pad.setWidth((currLevel != 1) ? 75 : 150);
            pad.pad.setX(pad.pad.getX() - 25);
        }
        paddleExtensionTime = 300;
    }

    public static void padWidthReset() {
        pad.pad.setWidth((currLevel != 1) ? 50 : 100);
    }

    public static void speedUpBall(){
        BALL_SPEED *= 1.5;
        ballSpeedTime = 300;
    }

    public static void resetBallSpeed() {
        BALL_SPEED = 4;
    }

    @Override
    public void start(Stage tempStage) {
        stage = tempStage;
        balls.clear();
        // Initialize game objects
        balls.add(new Bouncer(new Circle(SIZE/2, PAD_START_Y - 10 - RADIUS, RADIUS), 
                        Math.random() * 2 - 1, Math.random() * 2 - 1));
        balls.get(0).bouncer.setFill(Color.LIGHTSTEELBLUE);
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
        int numOfballs = balls.size();

        for (Bouncer ball : balls){
            ball.updatePos();
            ball.edgeDetection();
            // if a ball gets removed, then brek out of loop
            if (balls.size() != numOfballs){
                break;
            }
            ball.detectPad();
            checkTileCollisions(ball);
        }

        if (paddleExtensionTime == 0){
            padWidthReset();
        } else {
            paddleExtensionTime--;
        }

        if (ballSpeedTime == 0){
            resetBallSpeed();
        } else {
            ballSpeedTime--;
        }
        
        pad.updatePos();
        updateUI();
        checkLoss();
        checkWin();
    }

    public static void handleKeyInput (KeyCode code) {
        switch (code) {
            case RIGHT -> rightKey = true;
            case LEFT -> leftKey = true;
            case A -> leftKey = true;
            case D -> rightKey = true;
            case R -> resetGame();
            case L -> lives++;
            case M -> lives--;
            case B -> removeRandomBlock();
            case X -> {
                for (Bouncer ball : balls){
                    ball.reverseXDirection();
                }
            }
            case E -> addBall(balls.get(0));
            case W -> padExt();
            case Q -> speedUpBall();
            
        }
    }

    public static void handleKeyRelease (KeyCode code) {
        switch (code) {
            case RIGHT -> rightKey = false;
            case LEFT -> leftKey = false;
            case A -> leftKey = false;
            case D -> rightKey = false;
        }
    }

    public static void main (String[] args) {
        launch(args);
    }

}