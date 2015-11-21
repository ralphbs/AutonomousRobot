import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.TimerListener;

public class ObstacleAvoider implements TimerListener {

	public static boolean isAvoidingObstacles = true;
	private Navigation navigator;

	private double currentX;
	private double currentY;
	private UltrasonicPoller usPoller;
	private Odometer odometer;
	private final int OFFSET = 30;
	private Display display = new Display();
	private float distance;
	int bottomLeftX;
	int bottomLeftY;
	int topRightX;
	int topRightY;
	private EV3MediumRegulatedMotor usMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));

	public ObstacleAvoider(UltrasonicPoller usPoller, Navigation nav, Odometer odom, int[] coords) {
		this.usPoller = usPoller;
		this.navigator = nav;
		this.odometer = odom;
		this.bottomLeftX = coords[0];
		this.bottomLeftY = coords[1];
		this.topRightX = coords[2];
		this.topRightY = coords[3];
		usMotor.setSpeed(20);
	}

	public void avoidObstacles() {

		currentX = odometer.getX();
		currentY = odometer.getY();

		while (true) {
			distance = usPoller.getFilteredData();
			display.print("Distance: ", distance + "", 4);
			if (distance > 30) {
				double currentAngle = odometer.getAng();
				display.print("Angle: ", currentAngle + "", 5);

				// heading north
				if (currentAngle > 45.0 && currentAngle < 135.0) {
					navigator.travelTo(currentX, currentY + OFFSET);
					currentY = currentY + OFFSET;
				}

				// heading east
				else if (currentAngle < 45.0 || currentAngle > 315.0) {
					navigator.travelTo(currentX + OFFSET, currentY);
					currentX = currentX + OFFSET;
				}

				// heading west
				else if (currentAngle > 135.0 && currentAngle < 225.0) {
					navigator.travelTo(currentX - OFFSET, currentY);
					currentX = currentX - OFFSET;
				}

				// heading south
				else if (currentAngle > 225 && currentAngle < 315.0) {
					navigator.travelTo(currentX, currentY - OFFSET);
					currentY = currentY - OFFSET;
				}
			} else {
				boolean[] LR = { false, false }; // no block for l and r
				usMotor.rotate(-90, false);
				distance = usPoller.getFilteredData();

				if (distance < 20) {
					LR[0] = true;
				}

				usMotor.rotate(180, false);
				distance = usPoller.getFilteredData();

				if (distance < 20) {
					LR[1] = true;
				}
				
				usMotor.rotate(-90);
				
				
				
				if (LR[0] == true && LR[1] == true){
					navigator.turnTo(odometer.getAng() + 180.0, false);	
				}
				
				if (LR[0] == false && LR[1] == true){
					navigator.turnTo(odometer.getAng() + 90, false);
				}
				
				
				if (LR[0] == true && LR[1] == false){
					navigator.turnTo(odometer.getAng() -90, false);	
				}
				
				if (LR[0] == false && LR[1] == false) {
					double currentAngle = odometer.getAng();

					// heading north
					if (currentAngle > 45.0 && currentAngle < 135.0) {
						navigator.turnTo(0, false);
					}

					// heading east
					else if (currentAngle < 45.0 || currentAngle > 315.0) {
						navigator.turnTo(90, false);
					}

					// heading west
					else if (currentAngle > 135.0 && currentAngle < 225.0) {
						navigator.turnTo(90, false);
					}

					// heading south
					else if (currentAngle > 225 && currentAngle < 315.0) {
						navigator.turnTo(0, false);
					}

				}

			}
		}

	}

	private boolean searchForObject() {
		return false;
	}

	private float getFilteredData() {
		return usPoller.getFilteredData();
	}

	public void timedOut() {
		display.print("Distance: ", "" + distance, 4);
	}

}
