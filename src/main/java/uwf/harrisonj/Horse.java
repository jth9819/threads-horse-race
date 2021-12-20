package uwf.harrisonj;

import java.text.DecimalFormat;
import java.util.Random;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Horse implements Runnable {
	private int x1 = 0; // X position
	private int y1 = 0; // Y position
	private Canvas c; // Global Canvas variable
	private int whichHorse = 0; // Horse number
	private App drawer; // App class
	
	private boolean first = false; // First place boolean
	private double finishLine; // Finish line (Canvas width)
	private Random r = new Random();

	private GraphicsContext gc; // Global GraphicsContext variable
	private Rectangle head = new Rectangle(); // Rectangle for head
	private Rectangle body = new Rectangle(); // Rectangle for body
	private Rectangle backLegs = new Rectangle(); // Rectangle for back legs
	private Rectangle frontLegs = new Rectangle(); // Rectangle for front legs 

	public Horse(int x, int y, Canvas c, int horseNum, App drawer) {
		x1 = x;
		y1 = y;
		this.c = c;
		whichHorse = horseNum;
		this.drawer = drawer;

		gc = c.getGraphicsContext2D(); // Set gc to canvas' GraphicsContext
		finishLine = c.getWidth(); // Set finishLine to canvas' width
		setHorseParts(); // Call set horse parts function
	}
	
	@Override
	public void run() {
	    DecimalFormat df2 = new DecimalFormat("#.##"); // For formatting decimal #
        double startTime = System.currentTimeMillis() / 1000.0; // Get current time in seconds
        
		while(x1 < finishLine) { // While no horse has crossed the finish line
			drawer.getLock().lock(); // Horse gets lock
			try {
				draw(gc); // Draw horse
				if(x1 + 110 >= finishLine) { // If horse has crossed finish line
				    double endTime = System.currentTimeMillis() / 1000.0; // Get current time in seconds

					drawer.interruptAll(whichHorse); // Interrupt all threads
					System.out.println("Horse #" + (int)(whichHorse+1) + " has won in " + df2.format(endTime - startTime) + " seconds!");
					drawer.getLock().unlock(); // Horse releases the lock
					Platform.runLater(new Runnable() {
					    @Override
					    public void run() {
							createDialogBox(whichHorse, df2, startTime, endTime); // Pop up winner dialog box
					    }
					});
					return;
				}
				drawer.getLock().unlock(); // Horse releases the lock
				Thread.sleep(r.nextInt(500)); // Thread sleeps
			}catch(InterruptedException e) {
				// System.out.println("Got interrupedException" + e);
				return;
			}
		}
	}


	public void setPosition(int x, int y) {
		if(drawer.getThreads().isEmpty()) { // If app has already been ran
			x1 = x1 + x;
			y1 = y;
		}else { // If app has not been ran (Startup)
			x1 = x;
			y1 = y;
		}
		setHorseParts(); // Call set horse parts function
	}

	// Function to set horse to first place
	public void setFirst(boolean isFirst) {
		first = isFirst;
	}

	// Function to erase the horse' current position
	public void eraseCurrentPosition() {
		gc.clearRect(x1, y1 - 10, x1 + 100, y1 + 100);
	}
	
	// Function to set & draw horse
	public void draw(GraphicsContext gc) {
		if(!first) {
			gc.clearRect(x1, y1 - 10, x1 + 100, y1 + 100); // Erase horse
			x1 += r.nextInt(50); // Set position for horse redraw
		}
		first = false;
		head.setX(x1 + 40);
		body.setX(x1);
		backLegs.setX(x1);
		frontLegs.setX(x1 + 40);
		drawHorse(gc);
	}
	
	// Function to create the dialog box
	public static Alert createDialogBox(int whichHorseParam, DecimalFormat df2Param, double startTimeParam, double endTimeParam) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Horse #" + (int)(whichHorseParam+1) + " has won in " + df2Param.format(endTimeParam - startTimeParam) + " seconds!");
		alert.show();
		return alert;
	}
	
	// Function to set horse parts (Rectangles)
	public void setHorseParts() {
		head.setX(x1 + 40);
		head.setY(y1+20);
		head.setWidth(20);
		head.setHeight(10);

		body.setX(x1);
		body.setY(y1 + 30);
		body.setWidth(50);
		body.setHeight(20);

		backLegs.setX(x1);
		backLegs.setY(y1 + 50);
		backLegs.setWidth(10);
		backLegs.setHeight(10);

		frontLegs.setX(x1 + 40);
		frontLegs.setY(y1 + 50);
		frontLegs.setWidth(10);
		frontLegs.setHeight(10);
	}

	// Function to draw horse
	public void drawHorse(GraphicsContext gc) {
		this.c.getGraphicsContext2D().clearRect(x1, y1 - 10, this.c.getWidth(), this.c.getHeight());

		System.out.println("Drawing horse " + (int)(whichHorse+1) + " at " + x1);
		gc.setStroke(Color.BLACK);

		gc.strokeRect(head.getX(), head.getY(), head.getWidth(), head.getHeight());
		gc.strokeRect(body.getX(), body.getY(), body.getWidth(), body.getHeight());
		gc.strokeRect(backLegs.getX(), backLegs.getY(), backLegs.getWidth(), backLegs.getHeight());
		gc.strokeRect(frontLegs.getX(), frontLegs.getY(), frontLegs.getWidth(), frontLegs.getHeight());
	}
}
