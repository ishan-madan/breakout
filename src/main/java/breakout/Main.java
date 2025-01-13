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
    public static final int SPEED = 4;

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
            this.pad = new Rectangle(0, 300, 40, 10);
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







    /**
     * Initialize what will be displayed.
     */
    @Override
    public void start (Stage stage) {
        ball = new Bouncer(new Circle(200, 200, RADIUS), Math.random()*2 - 1, Math.random()*2 - 1, SPEED);
        ball.bouncer.setFill(Color.LIGHTSTEELBLUE);

        pad = new Pad();

        Group root = new Group();
        root.getChildren().add(ball.bouncer);
        root.getChildren().add(pad.pad);

        Scene scene = new Scene(root, SIZE, SIZE, DUKE_BLUE);
        stage.setScene(scene);

        stage.setTitle(TITLE);
        stage.show();

        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(new KeyFrame(Duration.seconds(SECOND_DELAY), e -> step(SECOND_DELAY)));
        animation.play();
    }

    public Scene setupScene (int width, int height, Paint background) {
        Group root = new Group();
        Scene myScene = new Scene(root, width, height, background);
        return myScene;
    }

    private void step (double elapsedTime) {
        // ball.bouncer.setCenterX(ball.bouncer.getCenterX() + 1);
        updateBallPos();
        edgeDetection();
    }

    private void handleKeyInput (KeyCode code) {
        // switch (code) {
        //     case RIGHT -> myMover.setX(myMover.getX() + MOVER_SPEED);
        //     case LEFT -> myMover.setX(myMover.getX() - MOVER_SPEED);
        //     case UP -> myMover.setY(myMover.getY() - MOVER_SPEED);
        //     case DOWN -> myMover.setY(myMover.getY() + MOVER_SPEED);
        //     case R -> {bouncer1.getBouncer().setX(0); bouncer2.getBouncer().setX(0);}
        // }
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
