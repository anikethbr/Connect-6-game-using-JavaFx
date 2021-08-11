package com.internshala.connect6;
/*
 * Author: Aniketh B R
 * Connect Six game using JavaFx
 */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();
        controller = loader.getController();
        controller.createPlayGround();
        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);
        Scene scene = new Scene(rootGridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Six");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu(){
        // File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame= new MenuItem("New Game");
        newGame.setOnAction(event -> controller.resetGame());

        MenuItem resetGame= new MenuItem("Reset Game");
        resetGame.setOnAction(event ->  controller.resetGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        MenuItem exitGame= new MenuItem("Exit Game");
        exitGame.setOnAction(event -> exitGame());
        // Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About Connect6");
        aboutGame.setOnAction(event -> aboutConnect6());

        SeparatorMenuItem separator = new SeparatorMenuItem();

        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutMe());

        MenuBar menuBar = new MenuBar();
        fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);
        helpMenu.getItems().addAll(aboutGame,separator,aboutMe);
        menuBar.getMenus().addAll(fileMenu,helpMenu);
        return menuBar;
    }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developer");
        alert.setHeaderText("Aniketh B R");
        alert.setContentText("I love to create games. " +
                " I often try coding and develop a game during my leisure time." +
                " I am currently pursuing my BE degree, and connect6 is my second game in JavaFx platform." +
                " I developed Connect6 Game with the help of Internshala Trainings." +
                " Hope you liked and enjoyed playing my connect6 game.");
        alert.show();
    }

    private void aboutConnect6() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Six");
        alert.setHeaderText("How to Play");
        alert.setContentText("Connect Six is a two-player connection game" +
                " in which the players first choose a color and then take turns dropping colored discs" +
                " from the top into a ten-column, eight-row vertically suspended grid. The pieces fall straight down," +
                " occupying the next available space within the column. The objective" +
                " of the game is to be the first to form a horizontal, vertical, or diagonal line " +
                "of six of one's own discs. Connect Six is a solved game. The first player can " +
                "always win by playing the right moves.");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
