import java.awt.event.*;
import java.net.*;
import java.util.HashMap;

import javax.swing.*;

//NOTE: next time use string split

public class ChatWindowMain extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JPanel panel;
	JTextField connection_text_field;
	JTextField name_text_field;
	JButton open_button;
	JButton close_button;
	JButton name_button;
	HashMap<InetSocketAddress, ChatWindowSub> sub_windows;
	JLabel address_label;
	JLabel name_label;
	JLabel instruction_label;
	HashMap<String, InetSocketAddress> usernames;
	
	String name;
	boolean name_set;

	public ChatWindowMain() {
		name = "";
		name_set = false;
		sub_windows = new HashMap<>();
		usernames = new HashMap<>();
		
		panel = new JPanel();

		connection_text_field = new JTextField(20);
		name_text_field = new JTextField(16);
		
		open_button = new JButton("Open");
		open_button.addActionListener(this);
		close_button = new JButton("Close");
		close_button.addActionListener(this);
		name_button = new JButton("Set Name");
		name_button.addActionListener(this);
		
		try {
			address_label = new JLabel(InetAddress.getLocalHost().toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		instruction_label = new JLabel("Open a new Connection. Enter target username.");
		name_label = new JLabel("Set your name.");
		
		panel.add(name_label);
		panel.add(name_text_field);
		panel.add(name_button);
		panel.add(instruction_label);
		panel.add(connection_text_field);
		panel.add(open_button);
		panel.add(close_button);
		panel.add(address_label);

		this.setContentPane(panel);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Name not set, please set name.");
		setSize(480, 120);
		setVisible(true);
	}
	
	public void process_message(String message, InetSocketAddress socket_address)
	{
		if (message.startsWith("?????"))
		{
			System.out.println("Received a request: " + message);
			String targ_name = message.substring(6, message.indexOf("#") - 1);
			System.out.println("\"" + targ_name + "\"");
			if(targ_name.equals(name))
			{
				Driver.send(Driver.socket, "##### "+name+" ##### "+Driver.my_ip, socket_address);
				String reqer_name = message.substring(message.lastIndexOf("#") + 2);
				usernames.put(reqer_name, socket_address);
				sub_windows.put(socket_address, new ChatWindowSub(reqer_name, socket_address));
				sub_windows.get(socket_address).setVisible(false);
			}
			else
			{
				System.out.println("Request is not for us, ignoring.");
			}
			
			
		}
		else if (message.startsWith("#####"))
		{
			System.out.println("Received a reply: " + message);
			String other_name = message.substring(6, message.indexOf("#", 6)-1);
			System.out.println(other_name);
			String other_ip = message.substring(message.lastIndexOf("#")+2);
			System.out.println(other_ip);
			InetSocketAddress adr = new InetSocketAddress(other_ip, 64000);
			usernames.put(other_name, adr);
			if (!sub_windows.containsKey(adr))
			{
				sub_windows.put(adr, new ChatWindowSub(other_name, adr));
			}
			else
			{
				sub_windows.get(adr).setVisible(true);
			}
			
		}
		else if (sub_windows.containsKey(socket_address))
		{
			sub_windows.get(socket_address).add_message(socket_address.toString() + ": " + message);
			sub_windows.get(socket_address).setVisible(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand().toUpperCase();
		switch (action) {
		case "OPEN":
			if (!name_set)
			{
				instruction_label.setText("Error: You have not yet set your name.");
				break;
			}
			else
			{
				instruction_label.setText("Write the Name of the Person to Open a New Connection With.");
			}
			//TODO: eliminate this and replace it with the HW objective
			//all this will do is send the name request and break.
			
			usernames.put(connection_text_field.getText(), null);
			Driver.send(Driver.socket, "????? "+connection_text_field.getText()+" ##### "+name, Driver.local_broadcast);
			
//			String ip_and_port = connection_text_field.getText();
//			int of = ip_and_port.indexOf(":");
//			String ip = ip_and_port.substring(0, of);
//			String port = ip_and_port.substring(of+1);
//			int sock = Integer.parseInt(port);
//			InetAddress dress = null;
//			try {
//				dress = InetAddress.getByName(ip);
//			} catch (UnknownHostException e1) {
//				e1.printStackTrace();
//				return;
//			}
//			InetSocketAddress dress_sock = new InetSocketAddress(dress, sock);
//			if(sub_windows.containsKey(dress_sock))
//			{
//				//reopen old connection
//				sub_windows.get(dress_sock).setVisible(true);
//			}
//			else
//			{
//				//open new connection
//				sub_windows.put(dress_sock, new ChatWindowSub(dress_sock));
//			}
			
			break;
		case "CLOSE":
			for(ChatWindowSub sub: sub_windows.values())
			{
				sub.dispose();
			}
			dispose();
			Driver.running = false;
			break;
		case "SET NAME":
			name = name_text_field.getText();
			name_set = true;
			name_text_field.setVisible(false);
			name_label.setVisible(false);
			name_button.setVisible(false);
			setTitle("username: " + name);
			break;
		default:
			System.out.println("Unimplemented Action: " + action);
			break;
		}
	}

}
