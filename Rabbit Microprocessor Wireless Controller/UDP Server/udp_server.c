/*******************************************************************************

        UDP program, that receives packets from any host on UDP port 8888,then
        echoes the received packet back to the sender.

        The use of a UDP data handler is the fastest
        "official" way to implement UDP-based request/response servers.

*******************************************************************************/
#class auto
#define TCPCONFIG		1
#define MAX_UDP_SOCKET_BUFFERS 1
#define DISABLE_TCP		// Not using TCP
#define LOCAL_PORT	8888

#memmap xmem
#use "dcrtcp.lib"
#use "BLxS2xx.lib"

// PWM inversion
#define PWM_INVERT 0

// base PWM frequency
#define PWM_FREQ 50000

//Defining motors pwm ports
#define motorX 0
#define motorY 1
#define motorZ 2

udp_Socket sock;

char pktbuf[1024];	// Temp root buffer for packet reassembly
int i, j, k, length;
char prov_command[6];
int int_commands[6];
int command_received;
float distance;
word counter;
int emergencyBreak;
longword remoteIP;
word remotePort;
int datagramLength;

cofunc void pulse(int motor, int mmFactor){

   pulseEnable(motor);
	waitfor(DelayMs(mmFactor));
   pulseDisable(motor,0);
}

cofunc int readZDistance(){

	//Ultrasonic Routine to get distance measure

   //Sending Pulse to Ultrasonic Sensor
   digOut(6,1);
   waitfor(DelayMs(1));
   digOut(6,0);

   while(digIn(7)<1){
   }
   pulseEnable(8);
   while(digIn(7)){
   }
   pulseDisable(8,0);

   getCounter(9, &counter);
	resetCounter(9);

   distance = counter*2/5.83;

   //Cleaning Buffer
   memset(&pktbuf,0, sizeof(pktbuf));

   //Converting float (distance) to string and saving it on pktbuf variable
   sprintf(pktbuf, "%.2f", distance);

}

cofunc void adjustZHeight(int desiredZHeight){
	float dif;
   float actualZHeight;
   int mmFactorZ;
   wfd readZDistance();
   actualZHeight= distance;
	dif = actualZHeight - desiredZHeight;

   if(dif >= 0){
   	digOut(5,0); //Setting motor Z direction to go down
   }else{
   	digOut(5,1); //Setting motor Z direction to go up
      dif = dif*(-1);
   }
   mmFactorZ = dif*1200;
   wfd pulse(2,mmFactorZ);
}

void stopMotors(){

	emergencyBreak = 1;
	pulseDisable(0,0);
   pulseDisable(1,0);
   pulseDisable(2,0);

}

cofunc void moveToInitialPosition(){
	int flagMotorX;
   int flagMotorY;
   flagMotorX=0;
   flagMotorY=0;

   //Setting motors direction
	digOut(3,1);
   digOut(4,1);

  if (digIn(10)<1){
  		pulseEnable(motorX);
      flagMotorX=1;
  }
  if (digIn(11)<1){
  		pulseEnable(motorY);
      flagMotorY=1;
  }

  while( flagMotorX||flagMotorY ){

  		if (digIn(10)==1){
      	pulseDisable(motorX,0);
         flagMotorX=0;
      }
      if (digIn(11)==1){
      	pulseDisable(motorY,0);
         flagMotorY=0;
      }
  }
}

cofunc void manualMovement(int motor, int direction, int displacement){

	int mmFactor;
	if (motor == 0){
   	setFreq(0,100);
   	//Setting motor X direction
   	digOut(3, direction);

      //Setting mmFactor for motor X
		mmFactor = displacement*800;
   }else if (motor == 1){
   	setFreq(1,70);
   	digOut(4,direction);
   	mmFactor = displacement*78;
   }else if (motor == 2){
   	setFreq(2,50);
   	digOut(5,direction);
   	mmFactor = displacement*1200;
   }

   wfd pulse(motor,mmFactor);
}

cofunc void automaticMovement(int displacementX, int displacementY, int m, int n){
	int r;
   int c;
   int directionX;
   int directionY;
   int mmFactorX;
   int mmFactorY;
   emergencyBreak = 0;

   directionX = 0;   //moving to the right
   directionY = 0;  //moving up
	mmFactorX = displacementX*800;
   mmFactorY = displacementY*78;


   //setting motor X direction to moving left
   digOut(3,directionX);

   for(r=0; r<=n; r++){
   	wfd readZDistance();
      pktbuf[7]=',';
      udp_sendto(&sock, pktbuf, datagramLength, remoteIP, 8889);

      //Setting motor Y direction
   	digOut(4,directionY);
      setFreq(motorY,70);
   	for (c=1; c<=m; c++){

         wfd pulse(motorY,mmFactorY);
         //taking sample
         wfd readZDistance();
         if (c!=m) pktbuf[7]=',';
        	else{
         	pktbuf[7]='\n';
         }
         waitfor(DelayMs(1000));

         udp_sendto(&sock, pktbuf, datagramLength, remoteIP, 8889);
         if (emergencyBreak == 1) break;
      }
      if (emergencyBreak==1) break;

      if(directionY == 0) directionY = 1;
      else directionY = 0;

      if (r!=n){
         setFreq(motorX,100);
      	wfd pulse(motorX,mmFactorX);
      }
   }
  	memset(&pktbuf,0, sizeof(pktbuf));
   pktbuf[0]='0';
   udp_sendto(&sock, pktbuf, datagramLength, remoteIP, 8889);
}

cofunc offsetMovement(int displacementX, int displacementY){
	emergencyBreak = 0;

   if (digIn(10)==1){
   	wfd manualMovement(motorX,0,displacementX);
   }

   if ( (emergencyBreak != 1) && (digIn(11)==1) ){
      wfd manualMovement(motorY,0,displacementY);
   }
}

void getCommands(char command[]){
	i=0;
   k=0;
	j=0;
	memset(&int_commands[0], 0, sizeof(int_commands));
	length = strlen(command);

	for (i=0; i<length; i++){

		if( (command[i] != 'x')    ){
			prov_command[j] = command[i];
         j++;
		}else{
			int_commands[k] = atoi(prov_command);
			memset(&prov_command[0], 0, sizeof(prov_command));
         j=0;
         k++;
		}
	}

   switch (int_commands[0]){
   	case 0:  printf("Movimiento Manual...\n");  break;
      case 1:  printf("Escanear Area...\n");  break;
      case 2:  printf("Movimiento Offset...\n");  break;
      case 3:  printf("Ajustar Altura Eje Z...\n");  break;
      case 4:  printf("Leer Distancia...\n");  break;
      case 5:  printf("Detener Motores...\n");  break;
      case 6:  printf("Mover a Posición Inicial...\n");  break;
      default :
   }

	printf("Parámetros: %d\n", k);
   printf("p[0]: %d\n", int_commands[0]);
   printf("p[1]: %d\n", int_commands[1]);
   printf("p[2]: %d\n", int_commands[2]);
   printf("p[3]: %d\n", int_commands[3]);
   printf("p[4]: %d\n\n", int_commands[4]);

   command_received=1;
}
int echo_handler(int event, udp_Socket * s, ll_Gather * g,
						_udp_datagram_info * udi)
{
   remoteIP = udi->remip;
   datagramLength = g->len2+g->len3;

	memset(&pktbuf,0, sizeof(pktbuf));
	if (event == UDP_DH_ICMPMSG) {
		return 1;	// Just ignore incoming ICMP errors.
	}

   xmem2root(pktbuf, (long)g->data2, g->len2);
   xmem2root(pktbuf + g->len2, (long)g->data3, g->len3);

   printf("Datos recibidos: %s\n", pktbuf);

   getCommands(pktbuf);

  	if(strcmp(pktbuf,"4x    ")!=0){
      //Sending data to the sender of the packet
   	udp_sendto(s, pktbuf,g->len2+g->len3, udi->remip, udi->remport);
   }

	//Return 1 to indicate that all processing has been done.
	return 1;
}

void main()
{
	int channel;
   command_received=0;
   distance=0;

   // Initialize the controller
	brdInit();

    //Setting PWM's
    // Start PWM for motor X with 100 Hz frecuency and duty cycle of 50%
    setPWM(0, 100,50,PWM_INVERT,0);
    pulseDisable(0,0);

    // Start PWM for motor Y with 70 Hz frecuency and duty cycle of 50%
    setPWM(1,70,50,PWM_INVERT,0);
    pulseDisable(1,0);

    // Start PWM for motor Z with 50 Hz frecuency and duty cycle of 50%
    setPWM(2,50,50,PWM_INVERT,0);
    pulseDisable(2,0);


   //Setting DIO 3-6 as Outputs
	/*
      DIO3 -> direction motor X
      DIO4 -> direction motor Y
      DIO5 -> direction motor Z
      DIO6 -> ultrasonic sensor output
   */
   for(channel = 3; channel < 7; ++channel)
	{
   	// Set I/O to be general digital output
		setDigOut(channel, 0);
   }

   //Setting DIO7 as digital input
   //DIO7 -> ultrasonic sensor input
   setDigIn(7);

   //Setting DIO8 as PWM
   setPWM(8,PWM_FREQ,50, PWM_INVERT,0);
   pulseDisable(8,0);

   //Setting DIO9 as counter
   setCounter(9, BL_UP_COUNT, BL_EDGE_RISE, BL_EDGE_RISE);

   //Setting DIO 10-12 as Inputs
	/*
      DIO10 -> switch motor X
      DIO11 -> switch motor Y
      DIO12 -> switch motor Z
   */
   for(channel = 10; channel < 13; ++channel)
	{
   	// Set I/O to be general digital output
		setDigIn(channel);
   }

	// Start network and wait for interface to come up (or error exit).
	sock_init_or_exit(1);

	if(!udp_extopen(&sock, IF_ANY, LOCAL_PORT, -1L, 0, echo_handler, 0, 0)) {
		printf("!Error al abrir puerto UDP!\n");
		exit(0);
	}

	/* Let the stack do everything... */
	for(;;){
   	costate{
	  		tcp_tick(NULL);

      }
      costate{
      	if (command_received==1){
         	command_received=0;
            if(int_commands[0]==0){
     				wfd manualMovement(int_commands[1], int_commands[2], int_commands[3]);
   			}else if(int_commands[0]==1){
            	wfd automaticMovement(int_commands[1], int_commands[2], int_commands[3],int_commands[4]);
            }else if(int_commands[0]==2){
            	wfd offsetMovement(int_commands[1], int_commands[2]);
            }else if(int_commands[0]==3){
            	wfd adjustZHeight(int_commands[1]);
            }else if(int_commands[0]==4){
            	//Sending Distance to GUI:
            	wfd readZDistance();
               udp_sendto(&sock, pktbuf, datagramLength, remoteIP, 8888);
            }else if (int_commands[0] == 5){
            	stopMotors();
            }else if(int_commands[0]==6){
            	wfd moveToInitialPosition();
            }
         }
      }
      costate{
      	if(command_received==1){
         	command_received = 0;
      		if (int_commands[0] == 5) stopMotors();
         }
      }
   }
}

