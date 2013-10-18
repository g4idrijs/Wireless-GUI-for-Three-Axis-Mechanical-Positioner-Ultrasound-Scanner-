import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.LineBorder;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.JEditorPane;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.EventQueue;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

//* Clase Posicionador */
public class interfase_posicionador extends JFrame {
	
	//Instance variables -> ivars
	private JTextField ip_client;
	private JTextField udp_client;
	private JTextField ip_server;
	private JTextField udp_server;
	private JSlider xslider;
	private JSlider yslider;
	OpenCVFrameGrabber grabber; 
	UDPConn socket = new UDPConn();
	UDPPacketsReceiver udpPacketReceiver = new UDPPacketsReceiver();
	boolean status = true;
	boolean grabber_status = false;
	IplImage frame;
	JLabel lblVideo = new JLabel("Video");
	Thread video;
	//End ivars definition
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					interfase_posicionador frame = new interfase_posicionador();
					frame.setVisible(true);
				} catch (Exception e){
					e.printStackTrace();
				}
			}

		});
	}

	/**
	 * Create the frame.
	 */
	public interfase_posicionador() {
		Thread t = new Thread(udpPacketReceiver);
		t.start();
			
		final PrintToTextPane editor = new PrintToTextPane();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 674, 531);
		getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 674, 503);
		getContentPane().add(tabbedPane);
		
		JPanel configuracion_red = new JPanel();
		tabbedPane.addTab("Configuración de Red", null, configuracion_red, null);
		configuracion_red.setLayout(null);
		
		JPanel panel_cliente = new JPanel();
		panel_cliente.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Cliente", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_cliente.setBounds(20, 23, 238, 146);
		configuracion_red.add(panel_cliente);
		panel_cliente.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(16, 28, 204, 98);
		panel_cliente.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblIpLocal = new JLabel("IP:");
		lblIpLocal.setBounds(33, 25, 21, 16);
		panel_1.add(lblIpLocal);
		
		ip_client = new JTextField();
		ip_client.setBounds(55, 19, 134, 28);
		panel_1.add(ip_client);
		ip_client.setColumns(10);
		
		JLabel lblUdpLocal = new JLabel("UDP:");
		lblUdpLocal.setBounds(17, 61, 38, 16);
		panel_1.add(lblUdpLocal);
		
		udp_client = new JTextField();
		udp_client.setBounds(55, 55, 134, 28);
		panel_1.add(udp_client);
		udp_client.setColumns(10);
		
		JPanel panel_servidor = new JPanel();
		panel_servidor.setBorder(new TitledBorder(null, "Servidor", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panel_servidor.setLayout(null);
		panel_servidor.setBounds(392, 23, 238, 146);
		configuracion_red.add(panel_servidor);
		
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(null);
		panel_5.setBounds(16, 28, 204, 98);
		panel_servidor.add(panel_5);
		
		JLabel label = new JLabel("IP:");
		label.setBounds(33, 25, 22, 16);
		panel_5.add(label);
		
		ip_server = new JTextField("192.168.43.11");
		ip_server.setColumns(10);
		ip_server.setBounds(55, 19, 134, 28);
		panel_5.add(ip_server);
		
		JLabel label_1 = new JLabel("UDP:");
		label_1.setBounds(17, 61, 73, 16);
		panel_5.add(label_1);
		
		udp_server = new JTextField("8888");
		udp_server.setColumns(10);
		udp_server.setBounds(55, 55, 134, 28);
		panel_5.add(udp_server);
		
		JPanel panel_estado_conexion = new JPanel();
		panel_estado_conexion.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Estado de Conexi\u00F3n", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_estado_conexion.setBounds(20, 190, 610, 200);
		configuracion_red.add(panel_estado_conexion);
		panel_estado_conexion.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(17, 31, 574, 151);
		panel_estado_conexion.add(scrollPane);
		
		final JTextPane editorPane_1 = new JTextPane();
		scrollPane.setViewportView(editorPane_1);
		
		final JButton btnProbarConexin = new JButton("Probar Conexi\u00F3n");
		btnProbarConexin.setBounds(496, 405, 134, 35);
		configuracion_red.add(btnProbarConexin);
		
		JLabel logo_itl = new JLabel("New label");
		logo_itl.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/logo_itl.png")));
		logo_itl.setBounds(272, 39, 104, 125);
		configuracion_red.add(logo_itl);
		
		JButton btnBorrar = new JButton("Borrar");
		btnBorrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editorPane_1.setText("");
			}
		});
		btnBorrar.setBounds(338, 405, 134, 35);
		configuracion_red.add(btnBorrar);
		
		btnProbarConexin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				//Getting system date & time
			    String str_status;
			    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			    Date date = new Date();
			    
			    socket.send_data("1");
			    str_status = dateFormat.format(date) + "\r\n";
	
				if (socket.data.equals("1")){
					editor.color = Color.blue;
					str_status += "Conexión realizada con éxito\r\n";
				}
				else{
					editor.color = Color.red;
					str_status += "Error: " + socket.data + "\r\n";
				}
				
				str_status += "\r\n---------------------------------------------------------------------\r\n";
			    
				editor.printToEditorPane(editorPane_1, str_status, editor.color);
							
			}
		});
		
		JPanel configuracion_escaner = new JPanel();
		tabbedPane.addTab("Configuración Escáner", null, configuracion_escaner, null);
		configuracion_escaner.setLayout(null);
		
		JPanel panel_offset = new JPanel();
		panel_offset.setBounds(22, 18, 225, 340);
		configuracion_escaner.add(panel_offset);
		panel_offset.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Offset", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_offset.setLayout(null);
		
		JPanel panel_10 = new JPanel();
		panel_10.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		panel_10.setBounds(19, 24, 161, 245);
		panel_offset.add(panel_10);
		panel_10.setLayout(null);
		
		JPanel panel_11 = new JPanel();
		panel_11.setBorder(new LineBorder(new Color(255, 0, 0), 4, true));
		panel_11.setBounds(31, 34, 102, 183);
		panel_10.add(panel_11);
		panel_11.setLayout(null);
		
		final JLabel lb_posicion_offset = new JLabel("(0,0)");
		lb_posicion_offset.setBounds(6, 6, 61, 16);
		panel_11.add(lb_posicion_offset);
		
		JLabel lb_posicion_inicial = new JLabel("(0,0)");
		lb_posicion_inicial.setBounds(6, 6, 28, 16);
		panel_10.add(lb_posicion_inicial);
		
		final JSlider xslider_offset = new JSlider();
		xslider_offset.setMaximum(200);
		xslider_offset.setMinimum(0);
		xslider_offset.setValue(0);
		xslider_offset.setBounds(19, 281, 161, 29);
		panel_offset.add(xslider_offset);
		
		final JSlider yslider_offset = new JSlider();
		yslider_offset.setMaximum(200);
		yslider_offset.setMinimum(0);
		yslider_offset.setValue(0);
		yslider_offset.setBounds(190, 49, 29, 199);
		panel_offset.add(yslider_offset);
		yslider_offset.setOrientation(SwingConstants.VERTICAL);
		
		xslider_offset.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int xslider_offset_value = xslider_offset.getValue();
				lb_posicion_offset.setText("("+ Integer.toString(xslider_offset_value) + "," + Integer.toString(yslider_offset.getValue()) + ")");
			}
		});
		
		yslider_offset.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int yslider_offset_value = yslider_offset.getValue();
				lb_posicion_offset.setText("("+ Integer.toString(xslider_offset.getValue()) + "," + Integer.toString(yslider_offset_value) + ")");
			}
		});
		
		JLabel lblNotaUnidadesBasadas = new JLabel("Nota: unidades basadas en mil\u00EDmetros.");
		lblNotaUnidadesBasadas.setHorizontalAlignment(SwingConstants.CENTER);
		lblNotaUnidadesBasadas.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblNotaUnidadesBasadas.setBounds(6, 305, 213, 29);
		panel_offset.add(lblNotaUnidadesBasadas);
		
		JPanel posicionamiento_eje_z = new JPanel();
		posicionamiento_eje_z.setBounds(267, 18, 364, 185);
		configuracion_escaner.add(posicionamiento_eje_z);
		posicionamiento_eje_z.setBorder(new TitledBorder(null, "Posicionamiento Eje Z", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		posicionamiento_eje_z.setLayout(null);
		
		JPanel panel_altura_actual = new JPanel();
		panel_altura_actual.setBorder(new TitledBorder(null, "Altura Actual", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_altura_actual.setBounds(16, 34, 158, 95);
		posicionamiento_eje_z.add(panel_altura_actual);
		panel_altura_actual.setLayout(null);
		
		
		final JLabel lb_altura_actual = new JLabel("0 cm");
		lb_altura_actual.setHorizontalAlignment(SwingConstants.CENTER);
		lb_altura_actual.setFont(new Font("Lucida Grande", Font.BOLD, 22));
		lb_altura_actual.setBounds(6, 17, 146, 40);
		panel_altura_actual.add(lb_altura_actual);
		
		final JButton btnleer_distancia = new JButton("Leer Distancia");
		btnleer_distancia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = "4x    ";
				socket.send_data(command);
				editorPane_1.setText("Comando enviado: 4x\nDistancia: " + socket.data);
				lb_altura_actual.setText(socket.data + " cm");
			}
		});
		btnleer_distancia.setBounds(16, 56, 117, 29);
		panel_altura_actual.add(btnleer_distancia);
		
		JPanel panel_altura_deseada = new JPanel();
		panel_altura_deseada.setBorder(new TitledBorder(null, "Altura Deseada", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_altura_deseada.setLayout(null);
		panel_altura_deseada.setBounds(186, 34, 158, 95);
		posicionamiento_eje_z.add(panel_altura_deseada);
		
		final JLabel lbl_altura_deseada = new JLabel("0 cm");
		lbl_altura_deseada.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_altura_deseada.setFont(new Font("Lucida Grande", Font.BOLD, 24));
		lbl_altura_deseada.setBounds(6, 17, 146, 40);
		lbl_altura_deseada.setText("30 cm");
		panel_altura_deseada.add(lbl_altura_deseada);
		
		final JSlider slider_altura = new JSlider();
		slider_altura.setValue(34);
		slider_altura.setMaximum(40);
		slider_altura.setMinimum(28);
		slider_altura.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int slider_altura_deseada = slider_altura.getValue();
				lbl_altura_deseada.setText(Integer.toString(slider_altura_deseada) + " cm"); 
			}
		});
		slider_altura.setBounds(3, 60, 149, 29);
		panel_altura_deseada.add(slider_altura);
		
		final JButton btnPosicionar = new JButton("Posicionar");
		btnPosicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = "3x" + Integer.toString(slider_altura.getValue())+"x";
				socket.send_data(command);
				
				//Validating data sent
				editorPane_1.setText("Comando enviado: " + socket.data);
			}
		});
		btnPosicionar.setBounds(121, 141, 117, 33);
		posicionamiento_eje_z.add(btnPosicionar);
		
		final JButton btnOffset = new JButton("Offset");
		btnOffset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = "2x" + Integer.toString(xslider_offset.getValue()) + "x" + Integer.toString(yslider_offset.getValue()) + "x";
				socket.send_data(command);
				
				//Validating data sent
				editorPane_1.setText("Comando enviado: " + socket.data);
			}
		});
		btnOffset.setFont(new Font("Lucida Grande", Font.PLAIN, 33));
		btnOffset.setBounds(22, 370, 225, 60);
		configuracion_escaner.add(btnOffset);
		
		JPanel panel_desplazamiento = new JPanel();
		panel_desplazamiento.setBounds(259, 215, 372, 214);
		configuracion_escaner.add(panel_desplazamiento);
		panel_desplazamiento.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Desplazamiento", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_desplazamiento.setLayout(null);
		
		JPanel panel_desplazamiento_eje_x = new JPanel();
		panel_desplazamiento_eje_x.setBounds(30, 26, 149, 79);
		panel_desplazamiento.add(panel_desplazamiento_eje_x);
		panel_desplazamiento_eje_x.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Eje X", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_desplazamiento_eje_x.setLayout(null);
		
		final JLabel lb_xslider = new JLabel("New label");
		lb_xslider.setHorizontalAlignment(SwingConstants.CENTER);
		lb_xslider.setBounds(16, 53, 127, 16);
		panel_desplazamiento_eje_x.add(lb_xslider);
		
		xslider = new JSlider();
		xslider.setMaximum(40);
		xslider.setMinimum(1);
		xslider.setValue(20);
		lb_xslider.setText(Integer.toString(xslider.getValue()) + " mm");
		xslider.setBounds(6, 25, 136, 29);
		panel_desplazamiento_eje_x.add(xslider);
		
		JPanel panel_desplazamiento_eje_y = new JPanel();
		panel_desplazamiento_eje_y.setBounds(204, 26, 149, 79);
		panel_desplazamiento.add(panel_desplazamiento_eje_y);
		panel_desplazamiento_eje_y.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Eje Y", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_desplazamiento_eje_y.setLayout(null);
		
		final JLabel lb_yslider = new JLabel("25 mm");
		lb_yslider.setHorizontalAlignment(SwingConstants.CENTER);
		lb_yslider.setBounds(16, 53, 126, 16);
		panel_desplazamiento_eje_y.add(lb_yslider);
		
		yslider = new JSlider();
		yslider.setValue(25);
		yslider.setMinimum(1);
		yslider.setMaximum(50);
		yslider.setBounds(6, 23, 136, 29);
		lb_yslider.setText(Integer.toString(yslider.getValue()) + " mm");
		panel_desplazamiento_eje_y.add(yslider);
		
		JPanel panel_desplazamiento_eje_z = new JPanel();
		panel_desplazamiento_eje_z.setBounds(29, 117, 324, 79);
		panel_desplazamiento.add(panel_desplazamiento_eje_z);
		panel_desplazamiento_eje_z.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Desplazamiento Eje Z", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_desplazamiento_eje_z.setLayout(null);
		
		final JLabel lb_zslider = new JLabel("25 mm");
		lb_zslider.setHorizontalAlignment(SwingConstants.CENTER);
		lb_zslider.setBounds(6, 53, 312, 16);
		panel_desplazamiento_eje_z.add(lb_zslider);
		
		final JSlider zslider = new JSlider();
		zslider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int slider_value = zslider.getValue();
				lb_zslider.setText(Integer.toString(slider_value) + " mm");
			}
		});
		
		zslider.setValue(25);
		zslider.setMinimum(1);
		zslider.setMaximum(50);
		zslider.setBounds(69, 25, 190, 29);
		panel_desplazamiento_eje_z.add(zslider);
		final Icon turn_on = new ImageIcon(interfase_posicionador.class.getResource("/images/btn_36x36_on.png"));
		final Icon turn_off = new ImageIcon(interfase_posicionador.class.getResource("/images/btn_36x36_off.png"));
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Video", null, panel, null);
		panel.setLayout(null);
		
		JPanel panel_video = new JPanel();
		panel_video.setBackground(Color.WHITE);
		panel_video.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_video.setBounds(18, 30, 317, 245);
		panel.add(panel_video);
		panel_video.setLayout(null);
		
		lblVideo.setForeground(Color.BLACK);
		lblVideo.setIcon(null);
		lblVideo.setHorizontalAlignment(SwingConstants.CENTER);
		lblVideo.setBounds(3, 3, 320, 240);
		panel_video.add(lblVideo);
		
		final JButton btnReiniciar = new JButton("Reiniciar");
		btnReiniciar.setEnabled(false);
		btnReiniciar.setForeground(Color.BLUE);
		btnReiniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				grabber_status = true;
				status = true;
				video = new VideoCapture();
				video.start();
				btnReiniciar.setEnabled(false);
			}
		});
		btnReiniciar.setBounds(130, 281, 98, 35);
		panel.add(btnReiniciar);
		
		final JButton btnDetenerVideo = new JButton("Detener");
		btnDetenerVideo.setEnabled(false);
		btnDetenerVideo.setForeground(Color.RED);
		btnDetenerVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				status = false;
				btnReiniciar.setEnabled(true);
			}
		});
		btnDetenerVideo.setBounds(238, 281, 98, 35);
		panel.add(btnDetenerVideo);
		
		final JButton btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				grabber = new OpenCVFrameGrabber("rtsp://192.168.43.10:8554/video.mp4");
				video = new VideoCapture();
				video.start();
				btnConectar.setEnabled(false);
				btnDetenerVideo.setEnabled(true);
			}
		});
		btnConectar.setBounds(18, 281, 98, 35);
		panel.add(btnConectar);
		
		JPanel panel_movimiento_manual = new JPanel();
		panel_movimiento_manual.setBounds(356, 22, 277, 259);
		panel.add(panel_movimiento_manual);
		panel_movimiento_manual.setBorder(new TitledBorder(null, "Movimiento Manual", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_movimiento_manual.setLayout(null);
		
		final JButton dir_up = new JButton("");
		dir_up.setBounds(75, 63, 46, 46);
		panel_movimiento_manual.add(dir_up);
		dir_up.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				dir_up.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/selected_dir_up.png")));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				dir_up.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_up.png")));
			}
		});
		dir_up.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_up.png")));
		
		final JButton dir_right = new JButton("");
		dir_right.setBounds(130, 114, 46, 46);
		panel_movimiento_manual.add(dir_right);
		dir_right.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				dir_right.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/selected_dir_right.png")));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				dir_right.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_right.png")));
			}
		});
		dir_right.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_right.png")));
		
		final JButton dir_left = new JButton("");
		dir_left.setBounds(21, 114, 46, 46);
		panel_movimiento_manual.add(dir_left);
		dir_left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				dir_left.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/selected_dir_left.png")));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				dir_left.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_left.png")));
			}
		});
		dir_left.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_left.png")));
		
		final JButton dir_down = new JButton("");
		dir_down.setBounds(75, 167, 46, 46);
		panel_movimiento_manual.add(dir_down);
		dir_down.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				dir_down.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/selected_dir_down.png")));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				dir_down.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_down.png")));
			}
		});
		dir_down.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_down.png")));
		
		final JButton z_dir_up = new JButton("");
		z_dir_up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = "0x2x1x" + Integer.toString(zslider.getValue()) + "x";
				socket.send_data(command);
				editorPane_1.setText(command);
			}
		});
		z_dir_up.setBounds(203, 63, 46, 46);
		panel_movimiento_manual.add(z_dir_up);
		z_dir_up.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				z_dir_up.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/selected_dir_up.png")));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				z_dir_up.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_up.png")));
			}
		});
		z_dir_up.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_up.png")));
		
		final JButton z_dir_down = new JButton("");
		z_dir_down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = "0x2x0x" + Integer.toString(zslider.getValue()) + "x";
				socket.send_data(command);
				editorPane_1.setText(command);
			}
		});
		z_dir_down.setBounds(203, 167, 46, 46);
		panel_movimiento_manual.add(z_dir_down);
		z_dir_down.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				z_dir_down.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/selected_dir_down.png")));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				z_dir_down.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_down.png")));
			}
		});
		z_dir_down.setIcon(new ImageIcon(interfase_posicionador.class.getResource("/images/dir_down.png")));
		
		final JCheckBox chk_power = new JCheckBox("");
		chk_power.setBounds(75, 114, 46, 46);
		chk_power.setIcon(turn_off);
		panel_movimiento_manual.add(chk_power);
		
		JPanel panel_escanear_area = new JPanel();
		panel_escanear_area.setBounds(18, 322, 615, 118);
		panel.add(panel_escanear_area);
		panel_escanear_area.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Escanear \u00C1rea", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_escanear_area.setLayout(null);
		
		JPanel panel_ancho = new JPanel();
		panel_ancho.setBorder(new TitledBorder(null, "Ancho", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_ancho.setLayout(null);
		panel_ancho.setBounds(17, 21, 151, 81);
		panel_escanear_area.add(panel_ancho);
		
		final JLabel lbl_ancho = new JLabel("0 cm");
		final JSlider slider_ancho = new JSlider();
		slider_ancho.setValue(20);
		slider_ancho.setMaximum(35);
		slider_ancho.setMinimum(5);
		lbl_ancho.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_ancho.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		lbl_ancho.setBounds(6, 22, 139, 24);
		lbl_ancho.setText(Integer.toString(slider_ancho.getValue()));
		panel_ancho.add(lbl_ancho);
		slider_ancho.setBounds(6, 48, 135, 24);
		panel_ancho.add(slider_ancho);
		
		JPanel panel_alto = new JPanel();
		panel_alto.setBorder(new TitledBorder(null, "Alto", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_alto.setLayout(null);
		panel_alto.setBounds(176, 21, 151, 81);
		panel_escanear_area.add(panel_alto);
		
		final JLabel lbl_alto = new JLabel("0 cm");
		final JSlider slider_alto = new JSlider();
		slider_alto.setValue(25);
		slider_alto.setMinimum(5);
		slider_alto.setMaximum(45);
		lbl_alto.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_alto.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		lbl_alto.setBounds(6, 22, 139, 24);
		lbl_alto.setText(Integer.toString(slider_alto.getValue()));
		panel_alto.add(lbl_alto);
		slider_alto.setBounds(6, 48, 135, 24);
		panel_alto.add(slider_alto);
		
		final JButton btnEscanear = new JButton("Escanear");
		btnEscanear.setBounds(498, 72, 93, 29);
		btnEscanear.setEnabled(false);
		panel_escanear_area.add(btnEscanear);
		
		final JButton btnDetener = new JButton("Detener");
		btnDetener.setEnabled(false);
		btnDetener.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnEscanear.setEnabled(true);
				btnleer_distancia.setEnabled(true);
				btnPosicionar.setEnabled(true);
				btnOffset.setEnabled(true);
				btnProbarConexin.setEnabled(true);
				
				String command = "5x";
				socket.send_data(command);
				
				//Validating data sent
				editorPane_1.setText("Comando enviado: " + socket.data);
				
			}
		});
		btnDetener.setForeground(Color.RED);
		btnDetener.setBounds(498, 43, 93, 29);
		panel_escanear_area.add(btnDetener);
		
		JPanel panel_muestras = new JPanel();
		panel_muestras.setBorder(new TitledBorder(null, "Muestras", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_muestras.setLayout(null);
		panel_muestras.setBounds(335, 21, 151, 81);
		panel_escanear_area.add(panel_muestras);
		
		final JLabel lblMuestras = new JLabel("");
		lblMuestras.setHorizontalAlignment(SwingConstants.CENTER);
		lblMuestras.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		lblMuestras.setBounds(6, 34, 139, 24);
		panel_muestras.add(lblMuestras);
		
		//Calulating number of samplings 
		int muestras_eje_x = slider_ancho.getValue()*10 / xslider.getValue();
		int muestras_eje_y = slider_alto.getValue()*10 / yslider.getValue();
		//end  
		
		final JLabel lblMuestrasEjeX = new JLabel("");
		lblMuestrasEjeX.setForeground(UIManager.getColor("Panel.background"));
		lblMuestrasEjeX.setEnabled(false);
		lblMuestrasEjeX.setBounds(84, 6, 61, 16);
		lblMuestrasEjeX.setText(Integer.toString(muestras_eje_x));
		panel_muestras.add(lblMuestrasEjeX);
		
		final JLabel lblMuestrasEjeY = new JLabel("");
		lblMuestrasEjeY.setForeground(UIManager.getColor("Panel.background"));
		lblMuestrasEjeY.setEnabled(false);
		lblMuestrasEjeY.setBounds(84, 59, 61, 16);
		panel_muestras.add(lblMuestrasEjeY);
		lblMuestrasEjeY.setText(Integer.toString(muestras_eje_y));
		lblMuestras.setText(Integer.toString(muestras_eje_x) + "x" + Integer.toString(muestras_eje_y));
		
		JButton btnPosicionInicio = new JButton("Posición Inicial");
		btnPosicionInicio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String command = "6x";
				socket.send_data(command);
				
				//Validating data sent
				editorPane_1.setText("Comando enviado: " + socket.data);
			}
		});
		btnPosicionInicio.setBounds(513, 284, 120, 35);
		panel.add(btnPosicionInicio);
		
		//Code executed when pressing Scan button (botón Escanear)
		btnEscanear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnEscanear.setEnabled(false);
				btnleer_distancia.setEnabled(false);
				btnPosicionar.setEnabled(false);
				btnOffset.setEnabled(false);
				btnProbarConexin.setEnabled(false);
				
				//Data sent over UDP socket
				String command = "1x" + Integer.toString(xslider.getValue()) + "x" + Integer.toString(yslider.getValue()) + "x" + lblMuestras.getText() + "x";
				socket.send_data(command);
				
				//Validating data sent
				editorPane_1.setText("Comando enviado: " + socket.data);
			}
		});
		
		slider_ancho.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int slider_value = xslider.getValue();
				lb_xslider.setText(Integer.toString(slider_value) + " mm");
				int muestras_eje_x = slider_ancho.getValue()*10 / xslider.getValue();
				lblMuestrasEjeX.setText(Integer.toString(muestras_eje_x));
				lblMuestras.setText(lblMuestrasEjeX.getText() + "x" + lblMuestrasEjeY.getText());
				lbl_ancho.setText(Integer.toString(slider_ancho.getValue()) + " cm");
				
			}
		});
		
		slider_alto.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int slider_value = yslider.getValue();
				lb_yslider.setText(Integer.toString(slider_value) + " mm");
				int muestras_eje_y = slider_alto.getValue()*10 / yslider.getValue();
				lblMuestrasEjeY.setText(Integer.toString(muestras_eje_y));
				lblMuestras.setText(lblMuestrasEjeX.getText() + "x" + lblMuestrasEjeY.getText());
				lbl_alto.setText(Integer.toString(slider_alto.getValue()) + " cm");
			}
		});
		
		dir_up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String command = "0x1x0x" + Integer.toString(yslider.getValue()) + "x";
				socket.send_data(command);
				
				//Validating data sent
				editorPane_1.setText("Comando enviado: " + socket.data);
			}
		});
		
		dir_right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = "0x0x0x" + Integer.toString(xslider.getValue()) + "x";
				socket.send_data(command);
				
				//Validating data sent
				editorPane_1.setText("Comando enviado: " + socket.data);
			}
		});
		
		dir_down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = "0x1x1x" + Integer.toString(yslider.getValue()) + "x";
				socket.send_data(command);
				
				//Validating data sent
				editorPane_1.setText("Comando enviado: " + socket.data);
			}
		});
		
		dir_left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = "0x0x1x" + Integer.toString(xslider.getValue()) + "x";
				socket.send_data(command);
				
				//Validating data sent
				editorPane_1.setText("Comando enviado: " + socket.data);
			}
		});
		
		xslider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int slider_value = xslider.getValue();
				lb_xslider.setText(Integer.toString(slider_value) + " mm");
				int muestras_eje_x = slider_ancho.getValue()*10 / xslider.getValue();
				lblMuestrasEjeX.setText(Integer.toString(muestras_eje_x));
				lblMuestras.setText(lblMuestrasEjeX.getText() + "x" + lblMuestrasEjeY.getText());
			}
		});
		
		yslider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int slider_value = yslider.getValue();
				lb_yslider.setText(Integer.toString(slider_value) + " mm");
				int muestras_eje_y = slider_alto.getValue()*10 / yslider.getValue();
				lblMuestrasEjeY.setText(Integer.toString(muestras_eje_y));
				lblMuestras.setText(lblMuestrasEjeX.getText() + "x" + lblMuestrasEjeY.getText());
				
					
			}
		});
		
		chk_power.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(chk_power.isSelected()){
					chk_power.setIcon(turn_on);
					btnEscanear.setEnabled(true);
					btnDetener.setEnabled(true);
						
				}
				else{
					chk_power.setIcon(turn_off);
					btnEscanear.setEnabled(false);	
					btnDetener.setEnabled(false);
				}
			}
		});
	}
	
	//Internal Classes developed to help on certain specific tasks
	
	//This class is used to write to JEditorPane with style
	class PrintToTextPane{
		protected Color color;

		public  void printToEditorPane(JEditorPane editor, String data, Color color){
			StyledDocument doc = (StyledDocument) editor.getDocument();
			Style style = doc.addStyle("StyleName", null);
			StyleConstants.setForeground(style, color);
			
			try {
		    	doc.insertString(1, data, style);
		    } catch (BadLocationException e) {
		    	e.printStackTrace();
		    }
		}
		
	}
		
	//This class is used to run a Thread, capture video frames from a rtsp broadcaster and display them on a JLabel
	class VideoCapture extends Thread{
		
		public void run() {
			try {	
				if (!grabber_status){
					grabber.start();
				}

				while (status && (frame = grabber.grab()) != null) {
					lblVideo.setIcon(new ImageIcon(frame.getBufferedImage()));
				}
			}catch (Exception e) {
				e.printStackTrace();
			}			       		 
		}
	}
	
	//This class is used to send data over a UDP socket on port defined by the JTextEditor udp_server
	class UDPConn{
		//Instance Variables
		DatagramSocket socket = null;
	    DatagramPacket inPacket = null;
	    DatagramPacket outPacket = null;
	    byte[] inBuf, outBuf;
	    int PORT = 0; 
	    String data;
	    
	    public void send_data(String msg){
	    	try {			 		
			      PORT = Integer.parseInt(udp_server.getText());
			      InetAddress address = InetAddress.getByName(ip_server.getText());				
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
			 
	    	}catch (Exception e) {				  
	    		data = "Error: " + e;
	    	}	
	    }
	}
	
	//This class is used to run a Thread and continuously receive UDP packets on port 8889
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
					System.out.print(data);
					while(data.charAt(0)!='0'){
						socket.receive(inPacket);
						data = new String(inPacket.getData(), 0, inPacket.getLength());
						samples +=data;
						System.out.print(data);
					}
					System.out.print("\n" + samples);
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
	
	//This class is used to generate a CSV file and save it on a specified directory. Also implements a reverse ordering array elements method
	class CreateCSVFile{
		CreateCSVFile(String samples){
			
			//Reversing odd rows samples
			String[] arraySamples = samples.split("\n");
			int arraySamplesLength = arraySamples.length-1;
			
			for(int i=1; i<arraySamplesLength; i+=2){
				arraySamples[i] = reverseRowSamples(arraySamples[i]);
			}
			
			FileWriter fw=null;
			BufferedWriter bw = null;
			Format formatter = new SimpleDateFormat("dd-MM-yy");
			Date d = new Date();
			String directoryName = "Samples/" + formatter.format(d);
			
			File directory = new File(directoryName);
			
			if(!directory.exists()) directory.mkdir();
			
			formatter = new SimpleDateFormat("HH:mm:ss");
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
	
	//End internal classes created
}
