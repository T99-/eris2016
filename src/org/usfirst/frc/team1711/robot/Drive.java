package org.usfirst.frc.team1711.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.PIDController;

public class Drive {
	
	//creating master variables
	CANTalon motorDriveLeftMaster;
	CANTalon motorDriveRightMaster;
	
	//creating slave variables
	CANTalon motorDriveLeftSlave;
	CANTalon motorDriveRightSlave;

	//creating encoder variables
	Encoder encoderLeft;
	Encoder encoderRight;
	
	//creating drive system
	RobotDrive driveSystem;
	
	//creating gyro
	AnalogGyro gyroFlat;
//	PIDController PIDLeft;
//	PIDController PIDRight;
	
	//creating joystick
	Joystick driveStick = new Joystick(RobotMap.joystick0);
	

	public Drive() {

		//PID control for the gyro
		double Kp = .1;
		double Ki = 0;
		double Kd = 0;
		
//		PIDController PIDLeft = new PIDController(Kp, Ki, Kd, gyroFlat, motorDriveLeftMaster);
//		PIDController PIDRight = new PIDController(Kp, Ki, Kd, gyroFlat, motorDriveRightMaster);
		
		//creating master objects
		motorDriveLeftMaster = new CANTalon(RobotMap.motorDriveLeftMasterId);
		motorDriveRightMaster = new CANTalon(RobotMap.motorDriveRightMasterId);
		
		//creating slave objects
		motorDriveLeftSlave = new CANTalon(RobotMap.motorDriveLeftSlaveId);
		motorDriveRightSlave = new CANTalon(RobotMap.motorDriveRightSlaveId);
		
		//setting control mode
		motorDriveLeftSlave.changeControlMode(TalonControlMode.Follower);
		motorDriveRightSlave.changeControlMode(TalonControlMode.Follower);
		
		motorDriveLeftSlave.set(RobotMap.motorDriveLeftMasterId);
		motorDriveRightSlave.set(RobotMap.motorDriveRightMasterId);
		
		//encoder
		if(RobotMap.encoderDriveLeftA != -1 && RobotMap.encoderDriveLeftB != -1)
		{
			encoderLeft = new Encoder(RobotMap.encoderDriveLeftA, RobotMap.encoderDriveLeftB);
		}
		
		if(RobotMap.encoderDriveRightA != -1 && RobotMap.encoderDriveRightB != -1)
		{
			encoderRight = new Encoder(RobotMap.encoderDriveRightA, RobotMap.encoderDriveRightB);
		}
			
		//setting distance per pulse for the encoders
		if(RobotMap.encoderDriveLeftA != -1 && RobotMap.encoderDriveLeftB != -1)
		{
			encoderLeft.setDistancePerPulse(.02997);
		}
		
		if(RobotMap.encoderDriveRightA != -1 && RobotMap.encoderDriveRightB != -1)
		{
			encoderRight.setDistancePerPulse(.02997);
		}
			
		//creating gyro
		if(RobotMap.gyroFlat != -1)
		{
			gyroFlat = new AnalogGyro(RobotMap.gyroFlat);
		}
		
	}
	public void testDrive() {
		//this should be used to print values to get constants
		//you can put any print commands or other testing commands here
		//do not call this during matches
		System.out.println(encoderRight.get());
	}
	
	public void stopMotors()
	{
		motorDriveLeftMaster.set(0);
		motorDriveRightMaster.set(0);
	}
	
	public void DriveArcade(final Joystick driveStick)
	{
		if(driveStick.getRawButton(RobotMap.driveStickBtnSlow))
		{
			motorDriveLeftMaster.set(0.8*(-(driveStick.getY()-driveStick.getX())));
			motorDriveRightMaster.set(0.8*(-(driveStick.getY()+driveStick.getX())));

	        Timer.delay(0.005);		// wait for a motor update time
		}
		else
		{
			motorDriveLeftMaster.set(-(driveStick.getY()-driveStick.getX()));
			motorDriveRightMaster.set(-(driveStick.getY()+driveStick.getX()));

	        Timer.delay(0.005);		// wait for a motor update time
		}
		
	}

	public void driveForward(final double powerLevel, double time, final double leftConstant, final double rightConstant) 
	// drive so many inches (and fractions)
	// drives on a thread to not interfere with other robot functions
	{
		motorDriveLeftMaster.set(-powerLevel*leftConstant);
		motorDriveRightMaster.set(-powerLevel*rightConstant);
		Timer.delay(time);
		motorDriveLeftMaster.set(0);
		motorDriveRightMaster.set(0);
		
	}

	public void resetEncoders() 
	{
		//do not ever call this during a loop
		//if this is in the operatorControl or autonomous loop, you're doing it wrong
		
		if(encoderLeft != null)
		{
			encoderLeft.reset();
		}
		
		if(encoderRight != null)
		{
			encoderRight.reset();
		}
	}
	
	public void driveDistance(final double distance, final double powerLevel, final double leftConstant, final double rightConstant) {
		
		//printing for console purposes
		System.out.println("Driving: " + distance + " @ " + powerLevel + " power level");

		//running the robot for the specified distance at the specified power
		Thread thread = new Thread() {
		    public void run() 
		    {		    	
		    	//resets the encoders at the beginning of the method
		    	if(encoderLeft != null)
				{
					encoderLeft.reset();
				}
				
				if(encoderRight != null)
				{
					encoderRight.reset();
				}
				
		    	//drives the robot until it reaches the specified distance
				if (encoderLeft != null && encoderRight != null)
				{
					while ((encoderLeft.getDistance() + encoderRight.getDistance() / 2) < (distance)) 
			    	{
						
						motorDriveLeftMaster.set(-powerLevel*leftConstant);
						motorDriveRightMaster.set(-powerLevel*rightConstant);
					}
					
						motorDriveLeftMaster.set(0);
						motorDriveRightMaster.set(0);
				}
		    }
		};
	    thread.start();
		
	}
	
	public void driveAngle(final int desiredAngle, final double powerLevel)
	// takes a desired angle and a power level, and then turns the robot (the shortest way) to the desired angle
	{
		if(gyroFlat != null)
		{
			//printing actions to the console
			System.out.println("Moving to " + desiredAngle + " angle");
		
			Thread thread = new Thread() 
			{
				//I changed the power levels, but we need to test to see if it really goes "right" or "left" where labeled
				public void run()
				{
					gyroFlat.reset();
					//finding the fastest way to turn to get to the desired angle
					//if angle is less than 180 turn right
					if(desiredAngle < 180)
					{
						while(gyroFlat.getAngle() < desiredAngle)
						{
							//running motors at specified power levels
				    		double leftPower=powerLevel;
							double rightPower=powerLevel;
							motorDriveLeftMaster.set(-leftPower);
							motorDriveRightMaster.set(rightPower);
						}
						//stop motors when you have reached the desired angle
						motorDriveLeftMaster.set(0);
						motorDriveRightMaster.set(0);
					}
					//if angle is more than 180 turn left
					if(desiredAngle > 180)
					{
						while(gyroFlat.getAngle() > desiredAngle)
						{
							//running motors at specified power levels
				    		double leftPower=powerLevel;
							double rightPower=powerLevel;
							motorDriveLeftMaster.set(leftPower);
							motorDriveRightMaster.set(-rightPower);
						}
						//stop motors when you have reached the desired angle
						motorDriveLeftMaster.set(0);
						motorDriveRightMaster.set(0);
					}
				}
			};
			thread.start();
		}
	}
	public void gyroInit()
	{
		if(gyroFlat != null)
		{
			gyroFlat.initGyro();
			gyroFlat.calibrate();
		}
	}
	
	public void gyroTest()
	{
		System.out.println(gyroFlat.getAngle());
	}
	public void driveGyro(double distance)
	//call continuously during auton 
	{
		if(gyroFlat != null) 
		{
			double Kp = 0.03;
			double distanceTraveled = ((encoderLeft.getDistance() + encoderRight.getDistance()) / 2);
			
			while(distanceTraveled < distance)
			{
				double angle = gyroFlat.getAngle();
				double turnRate = -angle * Kp;
				driveSystem.drive(-1.0, turnRate);
			}
		}
	}
	
	public void arcadeGyro(Joystick driveStick)
	{
		double error = 0;
		double targetRotation;
		double targetDirection;
		double targetMagnitude;
		double currentAngle;
		double directionError;
		double currentRotation;
		double rotationError;
		boolean gyroMode = false;
		
		
		if(driveStick.getRawButton(RobotMap.driveStickBtnGyro))
		{
			gyroMode = true;
		}				
		while(gyroMode == true)
		{
//			PIDLeft.enable();
//			PIDRight.enable();
			
			targetRotation = driveStick.getX();
			targetDirection = driveStick.getDirectionDegrees();
			targetMagnitude = driveStick.getMagnitude();
			currentAngle = gyroFlat.getAngle();
			currentRotation = gyroFlat.getRate();
			
			directionError = targetDirection - currentAngle;
			
			//set the power proportional to the direction and magnitude
			//set the right and left wheels to turn based on this proportion
			//what should the setpoint be?????????
			//this maybe??
			//idk what I'm doing here I'll work with it later
//			PIDLeft.setSetpoint(targetDirection);
//			PIDRight.setSetpoint(targetDirection);
			
		}
	}
	
	public void driveGyroZ(final double minDistance, final double powerLevel, final double distance, double leftConstant, double rightConstant){
		//this method sets the drive to be controlled by the gyro
		//and to be controlled by a minimum distance
		//used for auton
		//needs to be changed to a Z axis gyro if we get one on the robot
		//if not, we don't really need this method
		if(gyroFlat != null && encoderLeft != null && encoderRight != null)
		{

			double encoderValue = 0;
			
			Thread thread = new Thread() {
				public void run() {
			
					//set variables
					double gyroValue = gyroFlat.getAngle();
					
					//stop driving if the robot is flat and has traveled a minimum distance
					if(gyroValue > -5 || gyroValue < 5 && distance > minDistance) {
						motorDriveLeftMaster.set(0);
						motorDriveRightMaster.set(0);
						
					}
					else 
					{
						//if the gyro is not flat and/or minDistance has not been reached, keep driving
						while ((encoderLeft.getDistance() + encoderRight.getDistance() / 2) < (distance)) {
							
							motorDriveLeftMaster.set(-powerLevel*leftConstant);
							motorDriveRightMaster.set(-powerLevel*rightConstant);
						}
				    }
					//stop when the robot reaches the desired maximum distance
					if(encoderValue > distance) {
			    		motorDriveLeftMaster.set(0);
			    		motorDriveRightMaster.set(0);
					}
					
				} 
			};
			thread.start();
		}
	}
}
