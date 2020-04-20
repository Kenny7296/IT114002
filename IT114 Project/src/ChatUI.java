import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatUI
{
	//public SocketClient client = new SocketClient();
	//Socket client;
	
	public static void main(String[] args)
	{
		// create frame
		JFrame frame = new JFrame("Just Chattin'");
		frame.setLayout(new BorderLayout());
		
		// exit chat when closing UI
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// create panel
		JPanel chatRoom = new JPanel();
		chatRoom.setPreferredSize(new Dimension (800, 800));
		chatRoom.setLayout(new BorderLayout());
		
		// create top panel
		JPanel topConnect = new JPanel();
		
		// create text areas for messages and displaying users
		JTextArea chatTextArea = new JTextArea();
		JTextArea usersTextArea = new JTextArea();
		
		// don't let the user edit these areas directly
		chatTextArea.setEditable(false);
		chatTextArea.setText("");
		usersTextArea.setEditable(false);
		usersTextArea.setText("");
		
		// create panel to hold multiple controls
		JPanel chatArea = new JPanel();
		chatArea.setPreferredSize(new Dimension(570, 800));
		chatArea.setLayout(new BorderLayout());
		
		// create panel to display connected users
		JPanel usersArea = new JPanel();
		usersArea.setPreferredSize(new Dimension(225, 800));
		usersArea.setLayout(new BorderLayout());
		
		// create label to see who's online
		JLabel uLabel = new JLabel();
		uLabel.setHorizontalAlignment(JLabel.CENTER);
		uLabel.setText("The Gang's All Here");
		
		// add text area to chat area
		chatArea.add(chatTextArea, BorderLayout.CENTER);
		chatArea.setBorder(BorderFactory.createLineBorder(Color.black));
		
		// add user area to chat area
		usersArea.add(usersTextArea, BorderLayout.CENTER);
		usersArea.setBorder(BorderFactory.createLineBorder(Color.green));
		
		// add label to users area
		usersArea.add(uLabel, BorderLayout.NORTH);
		
		// add chat area and users area to panel
		chatRoom.add(chatArea, BorderLayout.EAST);
		chatRoom.add(usersArea, BorderLayout.WEST);
		
		// username text field
		JTextField usernameField = new JTextField("Enter username");
		usernameField.setHorizontalAlignment(JTextField.CENTER);
		usernameField.setPreferredSize(new Dimension(160, 30));
		
		// port number text field
		JTextField defaultPort = new JTextField();
		defaultPort.setHorizontalAlignment(JTextField.CENTER);
		defaultPort.setPreferredSize(new Dimension(140, 30));
		defaultPort.setText("3000");
		
		// connect button
		JButton connect = new JButton();
		connect.setPreferredSize(new Dimension (100, 30));
		connect.setText("Connect");
		connect.addActionListener(new ActionListener()
		{		
				@Override
				public void actionPerformed(ActionEvent e)
				{
					String username = usernameField.getText();
					usersTextArea.append("\n" + username);
					
					connect.setText("Reconnect");
				}
			}
		);
		
		// create panel to hold multiple controls
		JPanel userInput = new JPanel();
		
		// setup text field
		JTextField messageField = new JTextField();
		messageField.setPreferredSize(new Dimension(450, 30));
		
		// setup submit button
		JButton send = new JButton();
		send.setPreferredSize(new Dimension(100, 30));
		send.setText("Send");
		send.addActionListener(new ActionListener ()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String message = messageField.getText();
				String username = usernameField.getText();
		
				if(message.length()> 0)
				{
					//append a new line & text from text field to text area
					chatTextArea.append("\n" + username + ": " + messageField.getText());
					//reset text field
					messageField.setText("");
				}
			}
		});
		
		// add username input and connect input 
		topConnect.add(usernameField);
		topConnect.add(defaultPort);
		topConnect.add(connect);
		
		// add text field and button to panel
		userInput.add(messageField);
		userInput.add(send);
		
		// add panels to chatRoom panel
		chatRoom.add(userInput, BorderLayout.SOUTH);
		chatRoom.add(topConnect, BorderLayout.NORTH);
		
		// add chatRoom panel to frame
		frame.add(chatRoom, BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
	}
}