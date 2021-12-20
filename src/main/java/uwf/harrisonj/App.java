package uwf.harrisonj;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
	private final int WIDTH = 600; // For scene & canvas width
	private final int HEIGHT = 600; // For scene & canvas height
	private ArrayList<Horse> horses = new ArrayList<Horse>(); // ArrayList of horses
	private ArrayList<Thread> threads = new ArrayList<Thread>(); // ArrayList of HBoxes
	private ArrayList<Canvas> canvases = new ArrayList<Canvas>(); // ArrayList of Canvases
	private ReentrantLock lock = new ReentrantLock(); // ReentrantLock for winner
	
	private Canvas c = new Canvas(WIDTH + 80, HEIGHT); // Declare a global Canvas
	private GraphicsContext gc = c.getGraphicsContext2D(); // Declare a global GraphicsContext

	// Start function
	@Override
	public void start(Stage stage) {	
		Button btnRun = createRun(); // Create the start button
		Button btnReset = createReset(); // Create the reset button
		Button btnQuit = createQuit(); // Create quit button
		
		VBox vboxCenter = new VBox(); // VBox that holds all of the HBoxes
		HBox hboxBottom = new HBox(); // HBox for buttons at bottom
		ArrayList<HBox> hboxes = new ArrayList<HBox>(); // ArrayList of HBoxes
		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, WIDTH + 100, HEIGHT);

		setUpLayout(hboxes); // Call the setUpLayout function to set up HBoxes and canvases
		createAndDrawHorses(); // Create and draw horses for application startup

		vboxCenter.getChildren().addAll(hboxes); // Add HBoxes to vboxCenter
		hboxBottom.getChildren().addAll(btnRun, btnReset, btnQuit); // Add buttons to hboxBottom

		root.setCenter(vboxCenter);
		root.setBottom(hboxBottom);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
		
		Alert alert = new Alert(AlertType.INFORMATION); 
		alert.setHeaderText("Welcome to Horse Race!");
		alert.setContentText("Click the run button to start the race.\nClick the reset button to start the race over.\nClick the quit button to exit the program.");
		alert.showAndWait(); // Show dialog box for user instructions
	}

	// Function to run program
	private Button createRun() {
		Button runButton = new Button("Run");
		runButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Runnable runnable; // Runnable for thread
				Thread thread; // To start thread
				
				if (threads.isEmpty()) {
					for (int i = 0; i < 6; i++) {
						gc = canvases.get(i).getGraphicsContext2D();
						horses.get(i).eraseCurrentPosition();
						horses.get(i).setPosition(50, 10);

						runnable = horses.get(i);
						thread = new Thread(runnable);
						threads.add(thread);
						threads.get(i).start();
					}
				}
			}
		});
		return runButton;
	};

	// Function to reset program
	private Button createReset() {
		Button resetButton = new Button("Reset");
		resetButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!threads.isEmpty()) {
					for (int i = 0; i < 6; i++) {
						threads.get(i).interrupt();
						gc = canvases.get(i).getGraphicsContext2D();
						horses.get(i).eraseCurrentPosition();
						horses.get(i).setPosition(0, 10);
						horses.get(i).drawHorse(gc);
					}
				}
				threads.clear();
			}
		});
		return resetButton;
	};

	// Function to quit program
	private Button createQuit() {
		Button quitButton = new Button("Quit");
		quitButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});
		return quitButton;
	}
	
	// Set up layout
	public void setUpLayout(ArrayList<HBox> hboxesParam) {
		 String cssLayout = "-fx-border-color: black;\n" + "-fx-border-insets: 5;\n" + "-fx-border-width: 3;\n" + "-fx-border-style: solid;\n";

		// Create HBoxes, add them to am ArrayList, set style (for testing), & set minimum height
		for (int i = 0; i < 6; i++) {
			HBox hbox = new HBox();
			hboxesParam.add(hbox);
			hboxesParam.get(i).setStyle(cssLayout);
			hboxesParam.get(i).setMinHeight(68);
		}

		// Create Canvases and add them to ArrayList
		for (int i = 0; i < 6; i++) {
			c = new Canvas(WIDTH + 80, HEIGHT); // Declare the Canvas
			canvases.add(c);
		}

		// Add Canvases to HBoxes
		for (int i = 0; i < 6; i++) {
			hboxesParam.get(i).getChildren().add(canvases.get(i));
		}
	}
	
	// Function to create and draw horses
	public void createAndDrawHorses() {
		for (int i = 0; i < 6; i++) {
			gc = canvases.get(i).getGraphicsContext2D();
			horses.add(new Horse(0, 10, canvases.get(i), i, this));
			horses.get(i).drawHorse(gc);
		}
	}
	
	// Function to interrupt all threads
	public void interruptAll(int whichHorse) {
		for (int i = 0; i < 6; i++) {
			threads.get(i).interrupt();
		}
	}

	// Function to get lock
	public ReentrantLock getLock() {
		return lock;
	}
	
	// Function to get threads
	public ArrayList<Thread> getThreads() {
		return threads;
	}
	
	
	// Main
	public static void main(String[] args) {
		launch();
	}
}