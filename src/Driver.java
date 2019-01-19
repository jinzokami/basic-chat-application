import java.io.IOException;
import java.net.*;

public class Driver {
	
	public  static void send(DatagramSocket socket, String message, InetSocketAddress addr)
	{
		byte[] buffer = message.getBytes();
		DatagramPacket packie = new DatagramPacket(buffer, message.length(), addr.getAddress(), addr.getPort());
		try {
			socket.send(packie);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static DatagramSocket socket = null;
	public static InetAddress my_addr = null;
	public final static int my_port = 64000;
	public static boolean running = true;
	public static String my_ip;
	public static InetSocketAddress local_broadcast;
	
	public static void main(String args[])
	{
		try {
			System.out.println(InetAddress.getLocalHost());
			my_addr = InetAddress.getLocalHost();
			socket = new DatagramSocket(my_port);
			socket.setSoTimeout(200);
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		local_broadcast = new InetSocketAddress("255.255.255.255", 64000);
		
		my_ip = my_addr.toString().substring(my_addr.toString().indexOf("/")+1);
		System.out.println(my_ip);
		
		ChatWindowMain daedra = new ChatWindowMain();
		
		Thread recv_thr = new Thread(new Runnable(){
			public void run()
			{
				byte[] in_buffer = new byte[256];
				DatagramPacket in_packie = new DatagramPacket(in_buffer, in_buffer.length);
				boolean message_in = false;
				do
				{
					for (int i = 0; i < in_buffer.length; i++) {
						in_buffer[i] = ' ';
					}
					try {
						message_in = true;
						socket.receive(in_packie);
					} catch (IOException e) {
						message_in = false;
					}
					if(message_in){
						String message = new String(in_packie.getData());
						message = message.trim();
						System.out.println("Received a new message: \""+ message + "\"");
						daedra.process_message(message, new InetSocketAddress(in_packie.getAddress(), in_packie.getPort()));
					}
				} while(daedra.isDisplayable());
			}
		});
		recv_thr.start();
		
		try {
			recv_thr.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
