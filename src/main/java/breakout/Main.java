package breakout;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
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
    public static final int RADIUS = 40;
    public static final int FRAMES_PER_SECOND = 60;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final int PAD_START_Y = (int) (SIZE * 0.9);
    public static final int BALL_START_Y = PAD_START_Y - RADIUS;
    public static final int BALL_SPEED = 4;
    public static final int PAD_SPEED = 4;

    // game objects
    Bouncer ball;
    Scene myScene;
    Pad pad;

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
            double hyp = Math.sqrt(xDirection*xDirection + yDirection*yDirection);
            this.bouncer = bouncer;
            this.xDirection = xDirection / hyp;
            this.yDirection = Math.abs(yDirection / hyp) * -1;
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

        public void reset() {
            // decrease lives
            lives--;
            // reset location
            bouncer.setCenterX(SIZE/2);
            bouncer.setCenterY(BALL_START_Y);
            // reset directions
            double newX = Math.random() * 2 - 1;
            double newY = Math.random() * -1;
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
        public int x;
        public int y;
        public int powerType;

        public Tile(int x, int y, boolean broken){
            this.tile = new Rectangle(x, y, 40, 20);
            this.x = x;
            this.y = y;
            this.broken = broken;
        }

        public Tile(int x, int y, boolean broken, int powerType){
            this.tile = new Rectangle(x, y, 40, 20);
            this.x = x;
            this.y = y;
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
            ball.reset();
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






    @Override
    public void start(Stage stage) {
        // Initialize ball and paddle
        ball = new Bouncer(new Circle(200, 200, RADIUS), Math.random() * 2 - 1, Math.random() * 2 - 1, BALL_SPEED);
        ball.bouncer.setFill(Color.LIGHTSTEELBLUE);
        pad = new Pad();

        // Set up the scene using the global myScene
        myScene = setupScene(SIZE, SIZE, DUKE_BLUE);
        Group root = (Group) myScene.getRoot();
        root.getChildren().addAll(ball.bouncer, pad.pad);

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
        Group root = new Group();
        myScene = new Scene(root, width, height, background);

        // Handle key inputs for paddle movement
        myScene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));

        return myScene;
    }


    private void step (double elapsedTime) {
        // ball.bouncer.setCenterX(ball.bouncer.getCenterX() + 1);
        updateBallPos();
        edgeDetection();
        padDetection(ball, pad);
    }

    private void handleKeyInput (KeyCode code) {
        switch (code) {
            case RIGHT -> pad.pad.setX(pad.pad.getX() + PAD_SPEED);
            case LEFT -> pad.pad.setX(pad.pad.getX() - PAD_SPEED);
            case A -> pad.pad.setX(pad.pad.getX() - PAD_SPEED);
            case D -> pad.pad.setX(pad.pad.getX() + PAD_SPEED);
            case R -> {ball.reset(); pad.reset();}
            case L -> lives++;
        }
    }

    // What to do each time a mouse button is clicked
    // private void handleMouseInput (double x, double y) {
    //     if (myGrower.contains(x, y)) {
    //         myGrower.setScaleX(myGrower.getScaleX() * GROWER_RATE);
    //         myGrower.setScaleY(myGrower.getScaleY() * GROWER_RATE);
    //     }
    // }

    public static void main (String[] args) {
        launch(args);
    }




}
