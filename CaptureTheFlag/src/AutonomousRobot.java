
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.LED;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.remote.ev3.RMIEV3;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RMIRemoteRegulatedMotor;
import lejos.remote.ev3.RemoteEV3;
import lejos.remote.ev3.RemoteMotorPort;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * @author Ralph Bou Samra
 * @version 1.0 Nov 1, 2015
 * */

public class AutonomousRobot {
	
	public static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
 	public static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	public static int displayX = 0;
	public static int displayY = 0;
	
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args){
	
		Odometer odom = new Odometer(leftMotor, rightMotor, 30, true);
		Navigation nav = new Navigation(odom);
		//nav.travelTo(60, 60);
		UltrasonicPoller usPoller = new UltrasonicPoller();
		LightSensorPoller lsPoller = new LightSensorPoller();
		
		odom.start();
		//OdometryCorrection odomCorr = new OdometryCorrection(odom);
		//odomCorr.start();
		//nav.travelTo(0, 100);
		
		//odom.start();
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		USLocalizer usLocal = new USLocalizer(odom, usPoller, USLocalizer.LocalizationType.FALLING_EDGE, nav);
		usLocal.doLocalization();
		LightLocalizer lsLocalizer = new LightLocalizer(odom, lsPoller, nav);
		lsLocalizer.doLocalization();
		
		
		//leftMotor.setSpeed(100);
		//rightMotor.setSpeed(100);
		//leftMotor.forward();
		//rightMotor.forward();
		
//
//		
//		RemoteEV3 secondBrick = new RemoteEV3("172.20.10.7");
//		RMIRegulatedMotor motor = secondBrick.createRegulatedMotor("C", 'L');
//		RMIRegulatedMotor secondMotor = secondBrick.createRegulatedMotor("B", 'L');
//		
//		motor.setSpeed(100);
//		motor.forward();
//		secondMotor.setSpeed(100);
//		secondMotor.forward();
//		Thread.sleep(5000);
//		motor.stop(true);
//		secondMotor.stop(true);
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	
	

}
