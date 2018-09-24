/*
 * OdometryCorrection.java
 */
package ca.mcgill.ecse211.odometer;

import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.EV3ColorSensor;

public class OdometryCorrection implements Runnable {
  private static final long CORRECTION_PERIOD = 10;
  private Odometer odometer;
  private SampleProvider lightSensor;
  private float[] lsData;

  /**
   * This is the default class constructor. An existing instance of the odometer is used. This is to
   * ensure thread safety.
   * 
   * @throws OdometerExceptions
   */
  public OdometryCorrection(SampleProvider lightSensor, float[] lsData) throws OdometerExceptions {

    this.odometer = Odometer.getOdometer();
    this.lightSensor = lightSensor;
    this.lsData = lsData;
    

  }

  /**
   * Here is where the odometer correction code should be run.
   * 
   * @throws OdometerExceptions
   */
  // run method (required for Thread)
  public void run() {
    long correctionStart, correctionEnd;
    float lastReading;

    while (true) {
      correctionStart = System.currentTimeMillis();

      // TODO Trigger correction (When do I have information to correct?)
      lightSensor.fetchSample(lsData, 0);
      // TODO Calculate new (accurate) robot position

      
      
      // TODO Update odometer with new calculated (and more accurate) vales

      odometer.setXYT(0.3, 19.23, 5.0);

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
}
