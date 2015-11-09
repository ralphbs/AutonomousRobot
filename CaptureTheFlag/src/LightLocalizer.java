import lejos.hardware.Sound;

/**
 * @author Jack Giaao
 * @version 1.0 Nov 1, 2015
 * */

public class LightLocalizer {
	private Odometer odo;
	private Navigation navi;
	private LightSensorPoller lsPoller;
	private final float gridLine = 38;	//the light value of gridline
	private final float ROTATION_SPEED = 50;
	private float offset = 5;	//offset from sensor to the wheel center
	
	
	/**
	 * Constructs a LightLocalizer object
	 * */
	public LightLocalizer(Odometer odo, LightSensorPoller lsPoller, Navigation navi) {
		this.odo = odo;
		this.lsPoller = lsPoller;
		this.navi = navi;
	}
	
	/**
	 * Performs light sensor localization
	 * */
	public void doLocalization() {
		double[] lSensorAngle = new double[4];
		double[] rSensorAngle = new double[4];
		double lX, lY, lAngX, lAngY, LdH1, LdH2, rX, rY, rAngX, rAngY, RdH1, RdH2;
		
		navi.turnTo(225, true);
		Sound.beep();
		
		try {
			Thread.sleep(800); //wait 2 sec before next navigation
		} catch (InterruptedException e) {
		}
		
		// start rotating and clock all 4 gridlines from each correction light sensor
		navi.setSpeeds(ROTATION_SPEED,-ROTATION_SPEED);
		Display display = new Display();
		for(int i = 0; i<4; i++)
		{ 
			display.print("Color Red: ", "" + lsPoller.getLColorRed());
			while(lsPoller.getRColorRed() > gridLine){
				display.print("Color Red: ", "" + lsPoller.getLColorRed());
			}
			rSensorAngle[i] = odo.getAng();
			Sound.buzz();
			while(lsPoller.getLColorRed() > gridLine);
			lSensorAngle[i] = odo.getAng();
			Sound.buzz();
		}
		navi.stopMotors();
		Sound.beep();
		
		// do trig to compute (0,0) and 0 degrees
		rAngY = Math.abs(Odometer.minimumAngleFromTo(rSensorAngle[1], rSensorAngle[3]));
		rAngX = Math.abs(Odometer.minimumAngleFromTo(rSensorAngle[0], rSensorAngle[2]));
		rX = -offset*Math.cos(Math.toRadians(0.5*rAngY));
		rY = -offset*Math.cos(Math.toRadians(0.5*rAngX));
		lAngY = Math.abs(Odometer.minimumAngleFromTo(lSensorAngle[1], lSensorAngle[3]));
		lAngX = Math.abs(Odometer.minimumAngleFromTo(lSensorAngle[0], lSensorAngle[2]));
		lX = -offset*Math.cos(Math.toRadians(0.5*lAngY));
		lY = -offset*Math.cos(Math.toRadians(0.5*lAngX));
		
		//dH1 = 360 - 0.5*angY - sensorAngle[3];	//correct angle based on y-
		//dH2 = 90 - 0.5*angX - sensorAngle[2];		//correct angle based on x-
		
		odo.setPosition(new double[] {0.5*(rX+lX) , 0.5*(lY+rY) ,/*odo.getAng()+0.5*(dH1+dH2)*/ }, new boolean [] {true, true, false});
		
		try {
			Thread.sleep(800); //wait 2 sec before next navigation
		} catch (InterruptedException e) {
		}
		
		// when done travel to (0,0) and turn to 0 degrees
		navi.travelTo(0, 0);
		Sound.buzz();
		navi.turnTo(0, true);
		Sound.beep();
	}

}
