import lejos.utility.TimerListener;

public class ObstacleAvoider implements TimerListener {

	public static boolean isAvoidingObstacles = true;
	private Navigation navigator;
	private int initialX = -15;
	private int initialY = -15;
	private int currentX;
	private int currentY;
	private UltrasonicPoller usPoller;
	private Odometer odometer;
	private final int TILE_SIZE = 30;
	private Display display = new Display();
	private float distance;

	public ObstacleAvoider(UltrasonicPoller usPoller, Navigation nav, Odometer odom) {
		this.usPoller = usPoller;
		this.navigator = nav;
		this.odometer = odom;
	}

	public void avoidObstacles() {
		currentX = initialX;
		currentY = initialY;
		

		while (true) {

			distance = usPoller.getFilteredData();
			display.print("Distance: ", distance + "", 4);
			if (distance > 40) {
				double currentAngle = odometer.getAng();
				display.print("Angle: ", currentAngle + "", 5);
				// heading north
				if (currentAngle > 45.0 && currentAngle < 135.0) {
					navigator.travelTo(currentX, currentY + TILE_SIZE);
					currentY = currentY + TILE_SIZE;
				}

				// heading east
				else if (currentAngle < 45.0 || currentAngle > 315.0) {
					navigator.travelTo(currentX + TILE_SIZE, currentY);
					currentX = currentX + TILE_SIZE;
				}

				// heading west
				else if (currentAngle > 135.0 && currentAngle < 225.0) {
					navigator.travelTo(currentX - TILE_SIZE, currentY);
					currentX = currentX - TILE_SIZE;
				}

				// heading south
				else if (currentAngle > 225 && currentAngle < 315.0) {
					navigator.travelTo(currentX, currentY - TILE_SIZE);
					currentY = currentY - TILE_SIZE;
				}
			} 
		}
		
	}

	private float getFilteredData() {
		return usPoller.getFilteredData();
	}

	public void timedOut() {
		display.print("Distance: ", "" + distance, 4);
	}

}