import java.awt.event.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import javax.swing.*;

public class ChatWindowSub extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JPanel panel;
	JTextArea text_Area;
	JScrollPane text_scroll;
	JTextField text_field;
	JButton send_button;
	JButton close_button;
	ArrayList<String> messages;
	InetSocketAddress target_address;
	String name;
	
	public ChatWindowSub(String name, InetSocketAddress dress_sock) {
		this.target_address = dress_sock;
		text_Area = new JTextArea(5, 20);
		text_scroll = new JScrollPane(text_Area);
		text_Area.setEditable(false);
		messages = new ArrayList<>();
		panel = new JPanel();
		
		text_field = new JTextField(20);
		
		send_button = new JButton("Send");
		send_button.addActionListener(this);
		close_button = new JButton("Close");
		close_button.addActionListener(this);
		
		panel.add(text_field);
		panel.add(send_button);
		panel.add(close_button);
		panel.add(text_scroll);
		
		this.setContentPane(panel);
		
		setTitle(name + " @" + dress_sock.toString());
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setSize(320, 240);
		setVisible(true);
	}
	
	public void add_message(String message)
	{
		messages.add(message);
		text_Area.append(message + "\n");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		switch(action.toUpperCase())
		{
		case "SEND":
			String message = text_field.getText();
			add_message(message);
			Driver.send(Driver.socket, message, target_address);
			break;
		case "CLOSE":
			setVisible(false);
			break;
		default:
			System.out.println("Unimplemented Action: " + action);
			break;
		}
	}

}
