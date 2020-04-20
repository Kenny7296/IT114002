import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatUI
{
	public static void main(String[] args)
	{
		// create frame
		JFrame frame = new JFrame("Just Chattin'"); 
		frame.setLayout(new BorderLayout());
		
		// create panel
		JPanel chatRoom = new JPanel();
		chatRoom.setPreferredSize(new Dimension (800, 800));
		chatRoom.setLayout(new BorderLayout());
		
		// create top panel
		JPanel topConnect = new JPanel();
		
		// create text area for messages and displaying users
		JTextArea chatTextArea = new JTextArea();
		JTextArea usersText = new JTextArea();
		
		// don't let the user edit this directly
		chatTextArea.setEditable(false);
		chatTextArea.setText("");
		
		// create panel to hold multiple controls
		JPanel chatArea = new JPanel();
		chatArea.setPreferredSize(new Dimension(500, 800));
		chatArea.setLayout(new BorderLayout());
		
		// create panel to display connected users
		JPanel usersArea = new JPanel();
		usersArea.setPreferredSize(new Dimension(300, 800));
		usersArea.setLayout(new BorderLayout());
		
		// create label to see who's online
		JLabel uLabel = new JLabel();
		uLabel.setText("The Gang's All Here");
		
		// add text area to chat area
		chatArea.add(chatTextArea, BorderLayout.CENTER);
		chatArea.setBorder(BorderFactory.createLineBorder(Color.black));
		
		// add user area to chat area
		usersArea.add(usersText, BorderLayout.CENTER);
		usersArea.setBorder(BorderFactory.createLineBorder(Color.black));
		
		// add label to users area
		usersArea.add(uLabel, BorderLayout.NORTH);
		
		// add chat area and users area to panel
		chatRoom.add(chatArea, BorderLayout.WEST);
		chatRoom.add(usersArea, BorderLayout.EAST);
		
		// username textfield
		JTextField usernameField = new JTextField("Your username");
		usernameField.setPreferredSize(new Dimension(100, 30));
		
		/*
		// 
		JTextField ipAdd = new JTextField("IP address");
		ipAdd.setPreferredSize(new Dimension(100, 30));
		*/
		
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
					usersText.append("\n" + username);
					
					connect.setText("Reconnect");
				}
			}
		);
		
		// create panel to hold multiple controls
		JPanel userInput = new JPanel();
		
		// setup textfield
		JTextField messageField = new JTextField();
		messageField.setPreferredSize(new Dimension(100, 30));
		
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
					//append a new line & text from textfield to textarea
					chatTextArea.append("\n" + username + ": " + messageField.getText());
					//reset textfield
					messageField.setText("");
				}
			}
		});
		
		//add username input and connect input 
		topConnect.add(usernameField);
		//topConnect.add(ipAdd);
		//topConnect.add(portNum);
		topConnect.add(connect);
		
		//add textfield and button to panel
		userInput.add(messageField);
		userInput.add(send);
		
		//add panels to chatRoom panel
		chatRoom.add(userInput, BorderLayout.SOUTH);
		chatRoom.add(topConnect, BorderLayout.NORTH);
		
		//add chatRoom panel to frame
		frame.add(chatRoom, BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
	}
}