
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LightSensorPoller{
	private static final Port lColorPort = LocalEV3.get().getPort("S2");		//The port for left light sensor
	private static final Port rColorPort = LocalEV3.get().getPort("S3");		//The port for right light sensor
	private static final Port fColorPort = LocalEV3.get().getPort("S4");		//The port for front light sensor
	private static SensorModes lColorSensor = new EV3ColorSensor(lColorPort);	// The corresponding SensorModes
	private static SensorModes rColorSensor = new EV3ColorSensor(rColorPort);
	private static SensorModes fColorSensor = new EV3ColorSensor(fColorPort);
	private SampleProvider lColorRed;
	private SampleProvider rColorRed;
	private SampleProvider fColorID;
	private SampleProvider fColorRGB;
	private float[] lColorData;
	private float[] rColorData;
	private float[] fColorIDData;
	private float[] fColorData;
	
	//Constructor
	LightSensorPoller()
	{
		this.lColorRed = lColorSensor.getMode("Red");				// LcolorR provides Red samples from this instance
		this.rColorRed = rColorSensor.getMode("Red");				// RcolorR provides Red samples from this instance
		this.fColorID = fColorSensor.getMode("ColorID");		// FcolorID provides one of the 8 color ID's corresponding integers
		this.fColorRGB = fColorSensor.getMode("RGB");			// FcolorRGB provides RGB samples from this instance
		this.lColorData = new float[lColorRed.sampleSize()];		//The data buffers
		this.rColorData = new float[rColorRed.sampleSize()];
		this.fColorIDData = new float[fColorID.sampleSize()];
		this.fColorData = new float[fColorRGB.sampleSize()];
	}
	
	//Data getters for the front light sensor
	//	The colorID getter
	public int getFColorID()
	{
		fColorID.fetchSample(fColorIDData, 0);
		int color = (int)fColorIDData[0];
		return color;
	}
	// The color RGB value getter (returns an float array of length 3)
	public float[] getFColorRGB()
	{
		float[] tempRGB = new float[3];
		fColorRGB.fetchSample(fColorData, 0);
		tempRGB[0] = fColorData[0];
		tempRGB[1] = fColorData[1];
		tempRGB[2] = fColorData[2];
		return tempRGB;
	}
	
	//Data getter for the left light sensor
	// The red color getter
	public int getLColorRed()
	{
		lColorRed.fetchSample(lColorData, 0);
		int color = (int)lColorData[0];
		return color;
	}
	
	//Data getter for the right light sensor
	// The red color getter
	public int getRColorRed()
	{
		rColorRed.fetchSample(rColorData, 0);
		int color = (int)rColorData[0];
		return color;
	}
}