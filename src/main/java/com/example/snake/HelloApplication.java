package com.example.snake;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = WIDTH;
    private static final int ROWS = 20;
    private static final int COLUMNS = ROWS;
    private static final int SQUARE_SIZE = WIDTH / ROWS;
    private GraphicsContext gc;
    private List<Point2D> snakeBody = new ArrayList<>();
    private Point2D snakeHead;
    private Circle foodShape;
    private int foodX;
    private int foodY;
    private int currentDirection;
    private boolean gameOver = false;
    private int score = 0;
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Snake");
        Group root = new Group();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        gc = canvas.getGraphicsContext2D();

        scene.setOnKeyPressed(event -> {
            if (!gameOver) {
                KeyCode code = event.getCode();
                if (code == KeyCode.RIGHT) {
                    if (currentDirection != 1) {
                        currentDirection = 0;
                    }
                } else if (code == KeyCode.LEFT) {
                    if (currentDirection != 0) {
                        currentDirection = 1;
                    }
                } else if (code == KeyCode.UP) {
                    if (currentDirection != 3) {
                        currentDirection = 2;
                    }
                } else if (code == KeyCode.DOWN) {
                    if (currentDirection != 2) {
                        currentDirection = 3;
                    }
                }
            }
        });

        for (int i = 0; i < 3; i++) {
            snakeBody.add(new Point2D(5, ROWS / 2));
        }
        snakeHead = snakeBody.get(0);
        initFood();
        generateFoodPosition();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(130), e -> run(gc)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    private void initFood() {
        foodShape = new Circle(SQUARE_SIZE / 2);
        foodShape.setFill(Color.BROWN);
    }
    private void generateFoodPosition() {
        foodX = (int) (Math.random() * ROWS);
        foodY = (int) (Math.random() * COLUMNS);
        foodShape.setCenterX(foodX * SQUARE_SIZE + SQUARE_SIZE / 2);
        foodShape.setCenterY(foodY * SQUARE_SIZE + SQUARE_SIZE / 2);
    }
    private void run(GraphicsContext gc) {
        if (!gameOver) {
            drawBackground(gc);
            drawFood(gc);
            drawSnake(gc);

            Point2D previousHeadPosition = snakeHead;

            switch (currentDirection) {
                case 0:
                    snakeHead = new Point2D(snakeHead.getX() + 1, snakeHead.getY());
                    break;
                case 1:
                    snakeHead = new Point2D(snakeHead.getX() - 1, snakeHead.getY());
                    break;
                case 2: 
                    snakeHead = new Point2D(snakeHead.getX(), snakeHead.getY() - 1);
                    break;
                case 3:
                    snakeHead = new Point2D(snakeHead.getX(), snakeHead.getY() + 1);
                    break;
            }

            if (snakeHead.getX() >= 0 && snakeHead.getX() < ROWS && snakeHead.getY() >= 0 && snakeHead.getY() < COLUMNS) {
                snakeBody.set(0, snakeHead);
            } else {
                gameOver = true;
            }
            for (int i = 1; i < snakeBody.size(); i++) {
                Point2D temp = snakeBody.get(i);
                snakeBody.set(i, previousHeadPosition);
                previousHeadPosition = temp;
            }
            if (eatFood()) {
                generateFoodPosition();
                increaseSnakeLength();
                score += 5;
            }
            checkCollision();
        }
//changing the font size of game over
        else {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 50));
            gc.fillText("Game Over", WIDTH / 3.5, HEIGHT / 2);
        }
        drawScore();
    }
    private void drawBackground(GraphicsContext gc) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if ((i + j) % 2 == 0) {
                    gc.setFill(Color.YELLOW);
                } else {
                    gc.setFill(Color.YELLOWGREEN);
                }
                gc.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
    private void drawFood(GraphicsContext gc) {
        gc.setFill(foodShape.getFill());
        gc.fillOval(foodShape.getCenterX() - foodShape.getRadius(), foodShape.getCenterY() - foodShape.getRadius(), foodShape.getRadius() * 2, foodShape.getRadius() * 2);
    }

    private void drawSnake(GraphicsContext gc) {

        gc.setFill(Color.RED);
        gc.fillRoundRect(snakeHead.getX() * SQUARE_SIZE, snakeHead.getY() * SQUARE_SIZE, SQUARE_SIZE - 1, SQUARE_SIZE - 1, 35, 35);

        for (int i = 1; i < snakeBody.size(); i++) {
            gc.fillRoundRect(snakeBody.get(i).getX() * SQUARE_SIZE, snakeBody.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE - 1,
                    SQUARE_SIZE - 1, 20, 20);
        }
    }
    private boolean eatFood() {
        if (snakeHead.getX() == foodX && snakeHead.getY() == foodY) {
            return true;
        }
        return false;
    }
    private void increaseSnakeLength() {
        snakeBody.add(new Point2D(-1, -1));
    }
    private void checkCollision() {
        for (int i = 1; i < snakeBody.size(); i++) {
            if (snakeHead.getX() == snakeBody.get(i).getX() && snakeHead.getY() == snakeBody.get(i).getY()) {
                gameOver = true;
                break;
            }
        }
    }
//draw score
    private void drawScore() {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 35));
        gc.fillText("Score: " + score, 10, 35);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
