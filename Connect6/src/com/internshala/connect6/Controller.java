package com.internshala.connect6;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	private  static  final int COLUMNS = 10; //Setting number of columns as 10
	private static final int ROWS = 8; //Setting number of rows as 8
	private static final int CIRCLE_DIAMETER = 80;

	private static String discColor1 = "#24303E"; // Assigning default Color code for disc Color1
	private static String discColor2 = "#4CAA88"; // Assigning default Color code for disc Color2
	private static  String PLAYER_ONE = "Player One"; // Assigning default Name for Player One
	private static String PLAYER_TWO = "Player Two"; // Assigning default Name for Player Two

	private  boolean isPlayerOneTurn = true;
	private boolean isAllowedToInsert = true;  // Flag to avoid same color disc being added
	private final Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS]; // For Structural Changes: For the developers

	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertedDiscsPane;
	@FXML
	public Label playerNameLabel;
	@FXML
	public TextField playerOneTextField,playerTwoTextField;
	@FXML
	public Button setNamesButton;
	@FXML
	public ColorPicker playerOneColor,playerTwoColor;

	public  void createPlayGround(){
		Shape rectangleWithHoles = createGameStructuralGrid();
		rootGridPane.add(rectangleWithHoles,0,1);
		List<Rectangle> rectangleList = createClikableColumns();
		for(Rectangle rectangle:rectangleList){
			rootGridPane.add(rectangle,0,1);
			setNamesButton.setOnAction(event->{
				if(isNameSame()) {
					PLAYER_ONE=playerOneTextField.getText();//Assigning Player One name entered by the user to String "PLAYER_ONE"
					PLAYER_TWO=playerTwoTextField.getText();//Assigning Player two name entered by the user to String "PLAYER_TWO"
				}
			});
			playerOneColor.setOnAction(event->{
				if(isColorSame()) {
					discColor1 = getColorCodeFromColourPicker(playerOneColor); //Assigning Color Picked by the user to the disc
				}
			});
			playerTwoColor.setOnAction(even -> {
				if(isColorSame()) {
					discColor2 = getColorCodeFromColourPicker(playerTwoColor); //Assigning Color Picked by the user to the disc
				}
			});
		}
	}

	private String getColorCodeFromColourPicker(ColorPicker colorPicker) {//Method to get Color code from Color Picker
		Color c = colorPicker.getValue();
		String str = String.valueOf(c);
		str = str.substring(2, str.length() - 2);
		return str;
	}

	private boolean isColorSame() {//Method to check whether player one and player two disc color is same or not
		if(playerOneColor.getValue().equals(playerTwoColor.getValue())){
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("Two players disc color cannot be same");
			alert.setContentText("Please select two different colors");
			alert.show();
			Platform.runLater(()->{
				Platform.exit();
				System.exit(0);});
		}
		return true;
	}

	private boolean isNameSame() { //Method to check whether player one and player two name entered by the user is same or not
		if(playerOneTextField.getText().equals(playerTwoTextField.getText())){
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("Two players name cannot be same");
			alert.setContentText("Please enter two different names");
			alert.show();
			Platform.runLater(()->{
				Platform.exit();
				System.exit(0);});
		}
		return true;
	}

	private Shape createGameStructuralGrid(){
		Shape rectangleWithHoles= new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
		for(int row=0;row<ROWS;row++){
			for(int col=0;col<COLUMNS;col++){
				Circle circle = new Circle();
				circle.setRadius((float)CIRCLE_DIAMETER/2);
				circle.setCenterX((float)CIRCLE_DIAMETER/2);
				circle.setCenterY((float)CIRCLE_DIAMETER/2);
				circle.setSmooth(true);
				circle.setTranslateY(row*(CIRCLE_DIAMETER+5)+ (float)CIRCLE_DIAMETER/4);
				circle.setTranslateX(col*(CIRCLE_DIAMETER+5)+(float)CIRCLE_DIAMETER/4);
				rectangleWithHoles = Shape.subtract(rectangleWithHoles,circle);
			}
		}
		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}
	private List<Rectangle> createClikableColumns() {
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int col = 0; col < COLUMNS; col++) {
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + (float)CIRCLE_DIAMETER / 4);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column=col;
			rectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert) {
					isAllowedToInsert=false;  // When disc is being dropped then no more disc will be inserted
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insertDisc(Disc disc,int column){
		int row = ROWS - 1;
		while (row >= 0){
			if(getDiscIfPresent(row,column) == null)
				break;
			row--;
		}
		if(row < 0) // If it is full, we cannot insert anymore disc
			return;
		int currentRow = row;
		insertedDiscArray[row][column] = disc;  // For structural Changes: For developers
		insertedDiscsPane.getChildren().add(disc); // For Visual Changes : For Players
		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + (float)CIRCLE_DIAMETER / 4);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
		translateTransition.setToY(row*(CIRCLE_DIAMETER+5)+ (float)CIRCLE_DIAMETER/4);
		translateTransition.setOnFinished(event -> {
			isAllowedToInsert = true; // Finally, when disc is dropped allow next player to insert disc
			if(gameEnded(currentRow,column)){
				gameOver();
				return;
			}
			isPlayerOneTurn=!isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO);
		});
		translateTransition.play();
	}

	private boolean gameEnded(int row,int column){//Method to check whether combination of six disc is achieved by any of the player either Vertically, horizontally, or diagonally
		List<Point2D> verticalPoints = IntStream.rangeClosed(row-5,row+5)  // If, row = 3, column = 3, then row = 0,1,2,3,4,5,6
				.mapToObj(r->new Point2D(r,column))
				.collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column-5,column+5)
				.mapToObj(col->new Point2D(row,col))
				.collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row-5,column+5);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0,10)
				.mapToObj(i->startPoint1.add(i,-i))
				.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row-5,column-5);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0,10)
				.mapToObj(i->startPoint2.add(i,i))
				.collect(Collectors.toList());

		return checkCombinations(verticalPoints)|| checkCombinations(horizontalPoints)
				|| checkCombinations(diagonal1Points)||checkCombinations(diagonal2Points);
	}

	private boolean checkCombinations(List<Point2D> points) {//Method to check whether Combination of six discs is achieved by any of the player
		int chain = 0;
		for (Point2D point : points) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();
			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);
			if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn){   // if the last inserted Disc belongs to the current player
				chain++;
				if(chain == 6){
					return true;
				}
			}else chain = 0;
		}
		return false;
	}
	private Disc getDiscIfPresent(int row, int column){//To prevent ArrayIndexOutOfBoundException
		if(row >= ROWS || row < 0 || column >= COLUMNS || column < 0) // If row or column index is invalid
			return  null;
		return insertedDiscArray[row][column];
	}

	private void gameOver(){//Method to declare Winner in a dialog box
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Six");
		alert.setHeaderText("The Winner is : "+decideWinner()+"\n"+"Color Code : "+winnerColorCode());
		alert.setContentText("Want to play again?");
		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType("No,Exit");
		alert.getButtonTypes().setAll(yesButton,noButton);
		Platform.runLater(()->{ // Helps us to resolve IllegalStateException.
			Optional<ButtonType> buttonClicked = alert.showAndWait();
			if (buttonClicked.isPresent() && buttonClicked.get() == yesButton){
				resetGame();
			}else {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	private String winnerColorCode() {// Method to find winner's colour code
		String winnerColorCode = isPlayerOneTurn?discColor1:discColor2;
		if(winnerColorCode.charAt(0) != '#') {
			winnerColorCode = "#" + winnerColorCode;
		}
		return winnerColorCode;
	}
	private String decideWinner(){ //Method to decide the winner
		String winner = isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO;
		return winner;
	}

	public void resetGame() { //Method to reset game
		insertedDiscsPane.getChildren().clear(); // Remove all Inserted Disc from Pane
		for (Disc[] discs : insertedDiscArray) {  // Structurally, Make all elements of insertedDiscsArray[][] to null
			Arrays.fill(discs, null);
		}
		isPlayerOneTurn = true;  // Let player start the game
		playerNameLabel.setText(PLAYER_ONE);
		createPlayGround(); // Prepare a fresh playground
	}

	private  static  class Disc extends Circle{ //Class disc which inherits the properties of Class Cicle
		private final boolean isPlayerOneMove;
		public Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius((float)CIRCLE_DIAMETER/2);
			setFill(isPlayerOneMove?Color.valueOf(discColor1):Color.valueOf(discColor2));
			setCenterX((float)CIRCLE_DIAMETER/2);
			setCenterY((float)CIRCLE_DIAMETER/2);
		}
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}

