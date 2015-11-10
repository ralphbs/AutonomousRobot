
/*
 * OdometryCorrection.java
 */

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;

import java.lang.Math;

public class OdometryCorrection extends Thread {

	private Odometer odometer;
	private EV3ColorSensor leftLightSensor;
	private EV3ColorSensor rightLightSensor;
	
	private static final long CORRECTION_PERIOD = 10;
	private final static int LightThreshold = 28;
	private final static double sensorDistanceToCenter = 5; // Distance of the
															// sensor to the
															// center
	private final static Port leftLightPort = SensorPort.S2;
	private final static Port rightLightPort = SensorPort.S3;
	
	
	private final double northAngle = 90.00;
	private final double eastAngle = 0.00;
	private final double southAngle = 270.00;
	private final double westAngle = 180.00;
	SensorMode leftSensorMode;
	SensorMode rightSensorMode;
	
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		this.leftLightSensor = new EV3ColorSensor(leftLightPort);
		this.rightLightSensor = new EV3ColorSensor(rightLightPort);
		leftSensorMode = leftLightSensor.getRedMode();
		rightSensorMode = rightLightSensor.getRedMode();
	}

	float[] fetchLightSensorValues() {
		
		float[] leftSample = new float[leftSensorMode.sampleSize()];
		leftSensorMode.fetchSample(leftSample, 0);
		float leftLightIntensity = leftSample[0] * 100;

		float[] rightSample = new float[rightSensorMode.sampleSize()];
		rightSensorMode.fetchSample(rightSample, 0);
		float rightLightIntensity = rightSample[0] * 100;
		
		return new float[]{leftLightIntensity, rightLightIntensity};
		
	}

	double getThetaX() {
		return Math.sin(odometer.getAng() * sensorDistanceToCenter);
	}

	double getThetaY() {
		return Math.cos(odometer.getAng() * sensorDistanceToCenter);
	}

	public void run() {
		long correctionStart, correctionEnd;
		// turn red light on
		leftLightSensor.setFloodlight(true);
		rightLightSensor.setFloodlight(true);
		float[] lightIntensity = fetchLightSensorValues();
		float leftLightIntensity = lightIntensity[0];
		float rightLightIntensity = lightIntensity[1];
		while (true) {
			correctionStart = System.currentTimeMillis();

			
			
			
			// the robot is in a diagonal position with a positive slope
			if(leftLightIntensity < LightThreshold){
					Sound.beepSequence();
					odometer.getLeftMotor().stop();
					
					while(rightLightIntensity > LightThreshold){
						rightLightIntensity = fetchLightSensorValues()[1];
					}
					odometer.getRightMotor().stop();
					
					double xActual = odometer.getX() + Math.cos(odometer.getAng() * sensorDistanceToCenter);
					double yActual = odometer.getY() + Math.sin(odometer.getAng() * sensorDistanceToCenter);
				
					double errorX = xActual % 30.48;
					double errorY = yActual % 30.48;

					
					odometer.setPosition(new double[]{xActual + errorX,  yActual + errorY, 90.0}, new boolean[] {
							true, true, true});
					
					
					leftLightIntensity = fetchLightSensorValues()[0];
			}
			
			leftLightIntensity = fetchLightSensorValues()[0];
			
			// the robot is in a diagonal position with a negative slope
			if(rightLightIntensity < LightThreshold){
				
			}
			
			
//			if (lightIntensity < LightThreshold) {
//
//				double xActual = odometer.getX() + Math.cos(odometer.getAng() * sensorDistanceToCenter);
//				double yActual = odometer.getY() + Math.sin(odometer.getAng() * sensorDistanceToCenter);
//
//				double errorX = xActual % 30.48;
//				double errorY = yActual % 30.48;
//
//				double orientationAngle = odometer.getAng();
//
//				// North Direction
//				if (orientationAngle > 45 && orientationAngle < 135) {
//					odometer.setPosition(
//							new double[] { errorX + odometer.getX(), errorY + odometer.getY(), northAngle },
//							new boolean[] { false, true, true });
//				}
//
//				// East Direction
//				if (orientationAngle < 45 && orientationAngle > 315) {
//					odometer.setPosition(new double[] { errorX + odometer.getX(), errorY + odometer.getY(), eastAngle },
//							new boolean[] { true, false, true });
//				}
//
//				// South Direction
//				if (orientationAngle < 315 && orientationAngle > 225) {
//					odometer.setPosition(
//							new double[] { errorX + odometer.getX(), errorY + odometer.getY(), southAngle },
//							new boolean[] { false, true, true });
//				}
//
//				// West Direction
//				if (orientationAngle < 225 && orientationAngle > 135) {
//					odometer.setPosition(new double[] { errorX + odometer.getX(), errorY + odometer.getY(), westAngle },
//							new boolean[] { true, false, true });
//				}

				correctionEnd = System.currentTimeMillis();
				if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
					try {
						Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
					} catch (InterruptedException e) {
						// there is nothing to be done here because it is not
						// expected that the odometry correction will be
						// interrupted by another thread
					}
				}
			}
		}
	

	// When the robot has a theta in the range of -45 deg to 45 deg +ve Y
	// When the robot has a theta in the range of 135 deg to 225 de, -ve Y
	public boolean isMovingVertically(double theta) {
		if (Math.abs(theta) < 45 * Math.PI / 180 || (theta > 135 * Math.PI / 180 && theta < 225 * Math.PI / 180)) {
			return true;
		} else {
			return false;
		}
	}
}
