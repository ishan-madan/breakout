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
    public static final int FRAMES_PER_SECOND = 60;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

    // game objects
    Bouncer ball;
    Scene myScene;



    // classes
    // bouncer class
    static class Bouncer {
        public Circle bouncer;
        public double xDirection;
        public double yDirection;
        public double speed;

        public Bouncer(Circle bouncer, double xDirection, double yDirection) {
            this.bouncer = bouncer;
            this.xDirection = xDirection;
            this.yDirection = yDirection;
            this.speed = 50;
        }

        public void reverseXDirection(){
            xDirection *= -1;
        }

        public void reverseYDirection(){
            yDirection *= -1;
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
            this.x = x;
            this.y = y;
            this.broken = broken;
        }

        public Tile(int x, int y, boolean broken, int powerType){
            this.x = x;
            this.y = y;
            this.broken = broken;
            this.powerType = powerType;
        }
    }







    /**
     * Initialize what will be displayed.
     */
    @Override
    public void start (Stage stage) {
        ball = new Bouncer(new Circle(200, 200, 40), 0.7, 0.7);
        ball.bouncer.setFill(Color.LIGHTSTEELBLUE);

        Group root = new Group();
        root.getChildren().add(ball.bouncer);

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
        ball.bouncer.setCenterX(ball.bouncer.getCenterX() + 1);
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
