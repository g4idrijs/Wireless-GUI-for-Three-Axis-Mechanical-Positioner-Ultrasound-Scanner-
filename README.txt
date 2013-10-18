This project was designed for the purpose of controlling wirelessly a 3 axis mechanical positioner with builtin ultrasound scanner in a manual and automatic way using open source technologies.

The positioner was built with 3 stepper motors (one per axis) and power interface to control them. A Rabbit microprocessor (BL5S220) was used to control motors and communicate wirelessly via UDP Sockets with Graphical User Interfaces built in Android and Java. Android and Java technologies were selected because of its open source nature. 

The system supports video transmission (.mp4) to the Graphical User Interface using OpenCV and RTSP. Scans can be programmed by area, reconstruction of the object scanned is done with a file (matrix) created with distances detected from the sensor to the object. This reconstruction is done apart with Octave using the file created by the system and stored in Samples directory.

Finally because of the use of wireless technology, the system can be operated remotely.

Technologies and programming paradigms used:


* Microprocessor Rabbit
* Ultrasound sensor
* Stepper Motors
* C
* Android
* Java
* OpenCV
* OOP (Object Oriented Programming)
* SWING
* UDP Communication
* Threads
* Exceptions
* Files
* Wireless video transmission (RTSP)

The project is divided in 3 parts:

	* Code for the Rabbit Microprocessor written in C
	* Code for the Desktop GUI written in Java
	* Code for the Mobile GUI for Android