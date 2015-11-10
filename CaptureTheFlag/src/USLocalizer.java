import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * 
 * @author Ralph Bou Samra
 * @version 1.0 Nov 1, 2015
 * 
 */

public class USLocalizer {
	

	 enum LocalizationType {
		FALLING_EDGE, 
		RISING_EDGE
	};
	

	/**Speed of wheel rotation */
	public static double ROTATION_SPEED = 30;
	/** Odomter object passed by instantiater of USLocalizer*/
	private Odometer odo;
	/** UltrasonicPoller object passed by instantiater of USLocalizer */
	private UltrasonicPoller usPoller;
	/** LocalizationType object passed by instantiater of USLocalizer */
	private LocalizationType locType;
	/** Navigation object passed by instantiater of USLocalizer */
	private Navigation nav;

	
	/**
	 * Constructs a USLocalizer with initial field values
	 * 
	 * @param odo (required)
	 * @param usPoller (required)
	 * @param locType (required)
	 * @param nav (required)
	 * */
	public USLocalizer(Odometer odo, UltrasonicPoller usPoller, LocalizationType locType,
			Navigation nav) {
		this.odo = odo;
		this.usPoller = usPoller;
		this.locType = locType;
		this.nav = nav;
	}

	
	/**
	 * Performs ultrasonic localization
	 * by collecting and processing data from USPoller
	 * */
	public void doLocalization() {
		double angleA, angleB;

		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees no wall
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			nav.setSpeeds(70, -70);
			while (getFilteredData() < 35);
			
			while (getFilteredData() > 35);
			
			//nav.stopMotors();
		
			// nav.stopMotors();
			angleA = odo.getAng();
			Sound.beep();
			
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// switch direction and wait until it sees no wall
			nav.setSpeeds(-70, 70);
			while (getFilteredData() < 35);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (getFilteredData() > 35);
			//nav.stopMotors();

			angleB = odo.getAng();
			Sound.beep();
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			double avAng = (angleA + angleB) / 2;
			double correctionAngle;

			
			// formula in tutorial:
			if (angleA > angleB) {
				angleB += 360;
				correctionAngle = 225 - avAng;
			}
			else{
				correctionAngle = 45 - avAng;
			}
			odo.fixTheta(correctionAngle);

			nav.stopMotors();
			nav.turnTo(270, true);
			Sound.beepSequence();
			double x = getFilteredData();

			nav.turnTo(180, true);
			Sound.beepSequence();
			double y = getFilteredData();

			int robotOffsetToMid = 11;
			
			odo.setX(-30 + x + robotOffsetToMid);
			odo.setY(-30 + y + robotOffsetToMid);
			
			nav.travelTo(0, 0);
			nav.turnTo(0, true);
		}
	}

	/**
	 *	Gets the filtered value of distance collected
	 *  from the ultrasonic sensor
	 *  
	 *  @return distance collected from ultrasonic sensor
	 * */
	private float getFilteredData() {
		return usPoller.getFilteredData();
	}

}