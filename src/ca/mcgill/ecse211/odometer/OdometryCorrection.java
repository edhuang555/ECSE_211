/*
 * OdometryCorrection.java
 */
package ca.mcgill.ecse211.odometer;

import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.Sound;

public class OdometryCorrection implements Runnable {
  private static final long CORRECTION_PERIOD = 10;
  private Odometer odometer;
  private static SampleProvider lightSensor;
  private static float[] lsData;
  final double TILE_WIDTH = 30.48;
  final double TILE_HEIGHT = 30.48;
  static int lineCounter = 0;

  /**
   * This is the default class constructor. An existing instance of the odometer is used. This is to
   * ensure thread safety.
   * 
   * @throws OdometerExceptions
   */
  public OdometryCorrection(SampleProvider sample, float[] data) throws OdometerExceptions {

    this.odometer = Odometer.getOdometer();
    lightSensor = sample;
    lsData = data;
    

  }

  /**
   * Here is where the odometer correction code should be run.
   * 
   * @throws OdometerExceptions
   */
  // run method (required for Thread)
  public void run() {
    long correctionStart, correctionEnd;
    float[] lastReading = new float[lightSensor.sampleSize()];
    double x_lastLine = 0; //xy position at which last line was detected
    double y_lastLine = 0;
    //int lineCounter = 0;
    double[] position;
    		
    lightSensor.fetchSample(lastReading, 0);

    while (true){
      correctionStart = System.currentTimeMillis();
      // TODO Trigger correction (When do I have information to correct?)
      lightSensor.fetchSample(lsData, 0);
      if(lastReading[0] - lsData[0] > 0.15){ //check if difference between current and last reading > 0.2
    	  //beep if line detected
    	  Sound.beep();
    	  position = odometer.getXYT();
    	  if(lineCounter >= 0 && lineCounter <= 2)
          {
        	  if(lineCounter == 0){ //if no line has been detected yet
            	  y_lastLine = position[1];
            	  lineCounter++;
        	  }
        	  else{
        		  odometer.setY(y_lastLine + TILE_HEIGHT);
        		  y_lastLine += TILE_HEIGHT;
        		  lineCounter++;
        	  }
          }
    	  else if(lineCounter >= 3 && lineCounter <= 5)
          {
        	  if(lineCounter == 3){
            	  x_lastLine = position[0];
            	  lineCounter++;
        	  }
        	  else{
        		  odometer.setX(x_lastLine + TILE_WIDTH);
        		  x_lastLine += TILE_WIDTH;
        		  lineCounter++;
        	  }
          }
    	  else if(lineCounter >= 6 && lineCounter <= 8)
          {
        	  if(lineCounter == 6){
            	  y_lastLine = position[1];
            	  lineCounter++;
        	  }
        	  else{
        		  odometer.setY(y_lastLine - TILE_HEIGHT);
        		  y_lastLine -= TILE_HEIGHT;
        		  lineCounter++;
        	  }
          }
          else
          {
        	  if(lineCounter == 9){ //if no line has been detected yet
            	  x_lastLine = position[0];
            	  lineCounter++;
        	  }
        	  else{
        		  odometer.setX(x_lastLine - TILE_WIDTH);
        		  x_lastLine -= TILE_WIDTH;
        		  lineCounter++;
        	  }
          }
      // TODO Calculate new (accurate) robot position
    	  
      }
      lastReading[0] = lsData[0];
      // TODO Update odometer with new calculated (and more accurate) vales

      //odometer.setXYT(0.3, 19.23, 5.0);

      // this ensure the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        try {
          Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
        } catch (InterruptedException e) {
          // there is nothing to be done here
        }
      }
    }
  }
  
  public static float[] getLightData(){
	  return lsData;
  }
  
  public static int getLineCount(){
	  return lineCounter;
  }
}
