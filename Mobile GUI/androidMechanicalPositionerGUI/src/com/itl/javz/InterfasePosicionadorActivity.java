package com.itl.javz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.VideoView;

public class InterfasePosicionadorActivity extends TabActivity {
    /** Called when the activity is first created. */
	
	String commandSent, commandReceived;
	boolean power_flag = false;
	UDPPacketsReceiver udpPacketReceiver = new UDPPacketsReceiver();
	TabHost mTabHost;
	ProgressDialog progressDialog;
	VideoView mVideoView;

	String path = "rtsp://192.168.43.10:8554/video.mp4";
	//String path = "rtsp://v6.cache7.c.youtube.com/CjYLENy73wIaLQlw8v34bayS0xMYESARFEIJbXYtZ29vZ2xlSARSBXdhdGNoYKuo8ePk4binTgw=/0/0/0/video.3gp";
		
	//Declaration of Tab Network Configuration Views--------------------------------
	EditText ipClient, ipServer, portClient, portServer, log;
	//End Declaration of Tab Network Configuration Views----------------------------
	
	//Declaration of Tab Scan Configuration Views-----------------------------------
	//SeekBars Declarations
	SeekBar sbAlturaDeseadaZ,sbEjeX,sbEjeY,sbEjeZ, sbOffsetX, sbOffsetY;
	
	//TextViews Declaration
	TextView tvAlturaZ,tvAlturaDeseadaZ, tvEjeX, tvEjeY, tvEjeZ;
	//End Declaration of Tab Scan Configuration Views-------------------------------
	
	//Declaration of Tab Scan Views-------------------------------------------------
	//SeekBars Declarations
	SeekBar sbAncho,sbAlto;
	
	//TextViews Declaration
	TextView tvAncho,tvAlto, tvMuestras;
	//End Declaration of Tab Scan Views---------------------------------------------
	
	
	private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        
    	setupTab("Configuraci—n de Red",R.id.configConexion);
    	setupTab("Configuraci—n Esc‡ner", R.id.configEscaner);
    	setupTab("Escanear", R.id.Escanear);
    	
    	Thread t = new Thread(udpPacketReceiver);
		t.start();
    	
    	//Linking Network Configuration Views--------------------------------------------------------
    	ipServer = (EditText) findViewById(R.id.editTextIPServer);
    	portServer = (EditText) findViewById(R.id.editTextPortServer);
    	log = (EditText) findViewById(R.id.editTextLog);
    	//End of Linking Network Configuration Views-------------------------------------------------
    	
    	
    	//Linking Scan Configuration Views-----------------------------------------------------------
    	sbAlturaDeseadaZ = (SeekBar) findViewById(R.id.seekBarAlturaDeseadaZ); sbAlturaDeseadaZ.setProgress(28);
    	sbEjeX = (SeekBar) findViewById(R.id.seekBarEjeX); sbEjeX.setProgress(25);
    	sbEjeY = (SeekBar) findViewById(R.id.seekBarEjeY); sbEjeY.setProgress(25);
    	sbEjeZ = (SeekBar) findViewById(R.id.seekBarEjeZ); sbEjeZ.setProgress(25);
    	sbOffsetX = (SeekBar) findViewById(R.id.seekBarOffsetX);
    	sbOffsetY = (SeekBar) findViewById(R.id.seekBarOffsetY);
    	
    	tvAlturaZ = (TextView) findViewById(R.id.textViewAlturaZ);
    	tvAlturaDeseadaZ = (TextView) findViewById(R.id.textViewAlturaDeseadaZ);
    	tvEjeX = (TextView) findViewById(R.id.textViewEjeX);
    	tvEjeY = (TextView) findViewById(R.id.textViewEjeY);
    	tvEjeZ = (TextView) findViewById(R.id.textViewEjeZ);
    	//End of Linking Scan Configuration Views------------------------------------------------------
    	
    	//Scan Configuration SeekBars Methods----------------------------------------------------------
    	
    	sbAlturaDeseadaZ.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
           @Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
           {          
                if (progress<28) progress = 28;
                tvAlturaDeseadaZ.setText(Integer.toString(progress) + " cm");
                
           }

          @Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    	
    	sbEjeX.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
           @Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
           {
        	   if (progress<2){ progress = 1; sbEjeX.setProgress(1);}
               tvEjeX.setText(Integer.toString(progress) + " mm");
               if (sbEjeX.getProgress() !=0) tvMuestras.setText(Integer.toString(sbAncho.getProgress()*10/sbEjeX.getProgress()) + "x" + Integer.toString(sbAlto.getProgress()*10/sbEjeY.getProgress()));
           }

          @Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    	
    	sbEjeY.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
           @Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
           {
        	   if (progress<2){ progress = 1; sbEjeY.setProgress(1);}
               tvEjeY.setText(Integer.toString(progress) + " mm");
        	   if (sbEjeY.getProgress() !=0) tvMuestras.setText(Integer.toString(sbAncho.getProgress()*10/sbEjeX.getProgress()) + "x" + Integer.toString(sbAlto.getProgress()*10/sbEjeY.getProgress()));
           }

          @Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    	
    	sbEjeZ.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
           @Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
           {
        	   if (progress<2){ progress = 1; sbEjeZ.setProgress(1);}
               tvEjeZ.setText(Integer.toString(progress) + " mm");
           }

          @Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    	
    	//End Scan Configuration Seek Bars Methods---------------------------------------------------------
    	
    	
    	//Linking Scan Views-------------------------------------------------------------------------------
    	sbAncho = (SeekBar) findViewById(R.id.seekBarAncho); sbAncho.setProgress(20);
    	sbAlto = (SeekBar) findViewById(R.id.seekBarAlto); sbAlto.setProgress(15);
    	
    	tvAncho = (TextView) findViewById(R.id.textViewAncho);
    	tvAlto = (TextView) findViewById(R.id.textViewAlto);
    	tvMuestras = (TextView) findViewById(R.id.textViewMuestras);
    	//End of Linking Scan Views------------------------------------------------------------------------
    	
    	//Scan SeekBars Methods----------------------------------------------------------------------------
    	sbAncho.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
           @Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
           {
        	   if (progress<6) { progress = 5;  sbAncho.setProgress(5); }
               tvAncho.setText(Integer.toString(progress) + " cm");
               tvMuestras.setText(Integer.toString(sbAncho.getProgress()*10/sbEjeX.getProgress()) + "x" + Integer.toString(sbAlto.getProgress()*10/sbEjeY.getProgress()));
           }

          @Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
        });	
    	
    	sbAlto.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
           @Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
           {
        	   if (progress<6) { progress = 5; sbAlto.setProgress(5); }
               tvAlto.setText(Integer.toString(progress) + " cm");
               tvMuestras.setText(Integer.toString(sbAncho.getProgress()*10/sbEjeX.getProgress()) + "x" + Integer.toString(sbAlto.getProgress()*10/sbEjeY.getProgress()));
           }

          @Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
        });	
    	
    	//End Scan Seek Bars Methods-----------------------------------------------------------------------

    	//Setting Number of Samples
    	

    }
    
    public void powerOnScanner(View view){
    	ImageView power = (ImageView) findViewById(R.id.imageViewPowerOn);
    	if (!power_flag){
    		power.setImageResource(R.drawable.btn_72x72_on);
    		
    	}else power.setImageResource(R.drawable.btn_72x72_off); 
    	power_flag=!power_flag;
    }
    
    private void setupTab(final String tag, final int i) {
		View tabview = createTabView(mTabHost.getContext(), tag);

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(i);
		mTabHost.addTab(setContent);
		
	}

    private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
    
    public void printToLog(String strSent, String strReceived){
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    Date date = new Date();
	    String str_status;
    	
	    str_status = dateFormat.format(date) + "\r\n";
	    
	    if (strReceived.equals(strSent)){
	    	
	    	if (strReceived.equals("1")) str_status = "Conexi—n realizada con Žxito\r\n";
	    	else str_status += "Comando \"" + strReceived + "\" enviado con Žxito.\r\n";
	    	
	    }else str_status += "Error: " + strReceived+ "\r\n";
	    
	    str_status += "\r\n-------------------------------------------------------------------------------------------------\r\n";
	    
	    log.append(str_status);
    }
    
    public void dirUp(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent = "0x1x0x"+ Integer.toString(sbEjeY.getProgress()) + "x";
    	commandReceived = send_data(commandSent);
    	printToLog(commandSent, commandReceived);
    }
    
    public void dirRight(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent = "0x0x0x"+ Integer.toString(sbEjeX.getProgress()) + "x";
    	commandReceived = send_data(commandSent);
    	printToLog(commandSent, commandReceived);
    }
    
    public void dirDown(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent = "0x1x1x"+ Integer.toString(sbEjeY.getProgress()) + "x";
    	commandReceived = send_data(commandSent);
    	printToLog(commandSent, commandReceived);
    }
    
    public void dirLeft(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent = "0x0x1x"+ Integer.toString(sbEjeX.getProgress()) + "x";
    	commandReceived = send_data(commandSent);
    	printToLog(commandSent, commandReceived);
    }
    
    public void zDirUp(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent = "0x2x1x"+ Integer.toString(sbEjeZ.getProgress()) + "x";
    	commandReceived = send_data(commandSent);
    	printToLog(commandSent, commandReceived);
    }
    
    public void zDirDown(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent = "0x2x0x"+ Integer.toString(sbEjeZ.getProgress()) + "x";
    	commandReceived = send_data(commandSent);
    	printToLog(commandSent, commandReceived);
    }
    
    public void offsetMove(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent = "2x"+ Integer.toString(sbOffsetX.getProgress()) + "x"+ Integer.toString(sbOffsetY.getProgress()) + "x";
    	commandReceived = send_data(commandSent);
    	printToLog(commandSent, commandReceived);
    }
    
    public void setZHeight(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	String alturaDeseadaZ = (String) tvAlturaDeseadaZ.getText();
    	commandSent="3x"+ alturaDeseadaZ.charAt(0) + alturaDeseadaZ.charAt(1);
    	commandReceived = send_data(commandSent);
    	printToLog(commandSent,commandReceived);
    }
    
    public void readDistance(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent="4x    ";
    	commandReceived = send_data(commandSent);
    	printToLog(commandSent,commandReceived);
    	tvAlturaZ.setText(commandReceived.replaceAll("\00", "") + " cm");
    	
    }
    
    public void moveToInitialPosition(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent = "6x";
   		String commandReceived = send_data(commandSent);
   		printToLog(commandSent, commandReceived);
    }
    
    public void escanearArea(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent = "1x" + Integer.toString(sbEjeX.getProgress()) + "x" + Integer.toString(sbEjeY.getProgress()) + "x" + tvMuestras.getText() + "x";
   		String commandReceived = send_data(commandSent);
   		printToLog(commandSent, commandReceived);
    }
    
    
    
    public void detenerEscaneo(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent = "5x";
   		String commandReceived = send_data(commandSent);
   		printToLog(commandSent, commandReceived);
    }
    
    public void probarConexion(View view){
    	Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    	vib.vibrate(150);
    	commandSent = "1";
    	commandReceived = send_data(commandSent);
    	printToLog(commandSent, commandReceived);
    }
    
    public void borrarLog(View view){
    	log.setText("");
    }
    
    public String send_data(String msg){
 
    	DatagramSocket socket = null;
        DatagramPacket inPacket = null;
        DatagramPacket outPacket = null;
    	
    	byte[] inBuf, outBuf;
        int PORT = 0; 
        String data;
    	
    	try {			 		
		      PORT = Integer.parseInt(portServer.getText().toString());
		      InetAddress address = InetAddress.getByName(ipServer.getText().toString());				
		      socket = new DatagramSocket();
		 
		      //Convert string to byte and send to server
		      outBuf = msg.getBytes();
		      outPacket = new DatagramPacket(outBuf, 0, outBuf.length, address, PORT);
		      socket.setSoTimeout(2000);
		      socket.send(outPacket);
		 
		      //Receive confirmation message from server
		      inBuf = new byte[256];
		      inPacket = new DatagramPacket(inBuf, inBuf.length);
		      socket.receive(inPacket);
		      
		      data = new String(inPacket.getData(), 0, inPacket.getLength());	
		 
		    } catch (Exception e) {				  
		    	data = "" + e;
		    }	  		
    		return data;
    }
    
    
    public void getVideo(View view){
    	/*progressDialog = ProgressDialog.show( this, "", "Recibiendo Video...", true );
    	final VideoView videoView = ( VideoView ) findViewById( R.id.videoView1 );

        videoView.setMediaController( new MediaController( this ) );
        videoView.setVideoURI( Uri.parse( path ) );

        videoView.setOnPreparedListener( new OnPreparedListener() {
            public void onPrepared( MediaPlayer arg0 ) {
                progressDialog.dismiss();
                videoView.requestFocus();
                videoView.start();
            }
        } );*/

    	mVideoView = (VideoView) findViewById(R.id.videoView1);

        MediaController mc = new MediaController(this);
        mc.setAnchorView(mVideoView);
       
        mc.setMediaPlayer(mVideoView);
        Uri video = Uri.parse(path);
        mVideoView.setMediaController(mc);
        mVideoView.setVideoURI(video);
 
        
        try {
        	mVideoView.start();
		} catch (Exception e) {
			// TODO: handle exception
		}	
    }
    
    @Override
	public void onBackPressed(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("ÀDeseas salir de la aplicaci—n?")
    	       .setCancelable(false)
    	       .setPositiveButton("Si", new DialogInterface.OnClickListener() {
    	           @Override
				public void onClick(DialogInterface dialog, int id) {
    	        	   InterfasePosicionadorActivity.this.finish();
    	           }
    	       })
    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	           @Override
				public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    class UDPPacketsReceiver extends Thread{
		String data;
		String samples = "";
		
		public void run(){
			
			try {
				int port = 8889;
				DatagramSocket socket = new DatagramSocket(port);
				byte [] inBuffer = new byte[256];
				DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
				
				while(true){
					socket.receive(inPacket);
					data = new String(inPacket.getData(), 0, inPacket.getLength());
					samples +=data;
					//System.out.print(data);
					while(data.charAt(0)!='0'){
						socket.receive(inPacket);
						data = new String(inPacket.getData(), 0, inPacket.getLength());
						samples +=data;
						//System.out.print(data);
					}
					//System.out.print("\n" + samples);
					CreateCSVFile newFile = new CreateCSVFile(samples.replaceAll("\00", ""));
					
					samples="";
				}
				
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    
    class CreateCSVFile{
		CreateCSVFile(String samples){
			
			//reversing even rows samples
			String[] arraySamples = samples.split("\n");
			int arraySamplesLength = arraySamples.length-1;
			
			for(int i=1; i<arraySamplesLength; i+=2){
				arraySamples[i] = reverseRowSamples(arraySamples[i]);
			}
			
			FileWriter fw=null;
			BufferedWriter bw = null;
			Format formatter = new SimpleDateFormat("dd-MM-yy");
			Date d = new Date();
			String directoryName = "/sdcard/Samples/" + formatter.format(d);
			
			File directory = new File(directoryName);
			
			if(!directory.exists()) directory.mkdir();
			
			formatter = new SimpleDateFormat("HH-mm-ss");
			File file = new File(directoryName,"Sampling-"+ formatter.format(d)+ ".txt");
			
			try {
				fw = new FileWriter(file,true);
				bw = new BufferedWriter(fw);
				
				for(int i=0;i<arraySamplesLength; i++){
					bw.write(arraySamples[i]+"\n");

				}
				bw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
		public String reverseRowSamples(String str){
			String [] stringElements = str.split(",");;
			String reversedRowSamples ="";
			int arrayLastSample = stringElements.length -1;
			
			for(int i=arrayLastSample; i>= 0;  i--){
				if(i==arrayLastSample) reversedRowSamples += stringElements[i].substring(0,stringElements[i].length()-1) + ",";
				else if (i!=0) reversedRowSamples += stringElements[i]+",";
				else reversedRowSamples +=stringElements[i];
			}
			return reversedRowSamples;
		}
	}
}