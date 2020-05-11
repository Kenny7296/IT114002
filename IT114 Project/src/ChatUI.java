import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class ChatUI extends JFrame implements OnReceive
{
	static SocketClient client;
	static JTextArea chatLog;
	static JTextArea history;
	List<String> users = new ArrayList<String>();
	// create text area for displaying/ users
	static JTextArea usersTextArea = new JTextArea();
	
	
	public ChatUI()
	{
		super("Just Chattin'");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
			}
		});
	}
	
	public static void main(String[] args)
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
		} catch (InstantiationException ex) {
		} catch (IllegalAccessException ex) {
		} catch (UnsupportedLookAndFeelException ex) {
		}
		
		ChatUI window = new ChatUI();
		window.setLayout(new BorderLayout());
		window.setPreferredSize(new Dimension(600, 600));
				
		// create panel
		JPanel chatRoom = new JPanel();
		chatRoom.setPreferredSize(new Dimension(500, 500));
		chatRoom.setLayout(new BorderLayout());
		
		// create text area for messages
		JTextArea chatTextArea = new JTextArea();
		chatLog = chatTextArea;
		
		// don't let the user edit either of these areas directly
		chatTextArea.setEditable(false);
		chatTextArea.setText("");
		usersTextArea.setEditable(false);
		usersTextArea.setText("");
		
		// create panel to hold multiple controls
		JPanel chatArea = new JPanel();
		chatArea.setPreferredSize(new Dimension(420, 500));
		chatArea.setLayout(new BorderLayout());
		
		// add text to chat area
		chatArea.add(chatTextArea, BorderLayout.CENTER);
		chatArea.setBorder(BorderFactory.createLineBorder(Color.black));
		
		// create panel to display connected users
		JPanel usersArea = new JPanel();
		usersArea.setPreferredSize(new Dimension(168, 500));
		usersArea.setLayout(new BorderLayout());
		
		// add user info to user area
		usersArea.add(usersTextArea, BorderLayout.CENTER);
		usersArea.setBorder(BorderFactory.createLineBorder(Color.green));
		
		// add chat area and users area to panel
		chatRoom.add(chatArea, BorderLayout.EAST);
		chatRoom.add(usersArea, BorderLayout.WEST);
		
		// create label to see who's online
		JLabel uLabel = new JLabel();
		uLabel.setHorizontalAlignment(JLabel.CENTER);
		uLabel.setText("The Gang's All Here");

		// add label to users area
		usersArea.add(uLabel, BorderLayout.NORTH);
		
		// create top panel
		JPanel connectionDetails = new JPanel();
		
		// username text field
		JTextField usernameField = new JTextField("Enter username");
		usernameField.setHorizontalAlignment(JTextField.CENTER);
		usernameField.setPreferredSize(new Dimension(160, 30));
		
		// IP text field
		JTextField defaultIP = new JTextField();
		defaultIP.setHorizontalAlignment(JTextField.CENTER);
		defaultIP.setPreferredSize(new Dimension(140, 30));
		defaultIP.setText("127.0.0.1");
		
		// port number text field
		JTextField defaultPort = new JTextField();
		defaultPort.setHorizontalAlignment(JTextField.CENTER);
		defaultPort.setPreferredSize(new Dimension(140, 30));
		defaultPort.setText("3001");
		
		// connect button
		JButton connect = new JButton();
		connect.setPreferredSize(new Dimension (100, 30));
		connect.setText("Connect");
		
		connect.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
		    	client = new SocketClient();
		    	int _port = -1;
		    	
		    	try
		    	{
		    		_port = Integer.parseInt(defaultPort.getText());
		    	}
		    	
		    	catch(Exception num)
		    	{
		    		System.out.println("Port not a number");
		    	}
		    	
		    	if(_port > -1)
		    	{
			    	client = SocketClient.connect(defaultIP.getText(), _port);
			    	client.registerListener(window);
			    	client.postConnectionData(usernameField.getText());
			    	connect.setEnabled(false);
		    	}
		    }
		});
		
		// add username, IP, port, and connect input 
		connectionDetails.add(usernameField);
		connectionDetails.add(defaultIP);
		connectionDetails.add(defaultPort);
		connectionDetails.add(connect);
		
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
				
				if(message.length()> 0)
				{
					client.sendMessage(messageField.getText());
					messageField.setText("");
				}
			}
		});
		
		// add text field and button to panel
		userInput.add(messageField);
		userInput.add(send);
		
		// add panels to chatRoom panel
		chatRoom.add(userInput, BorderLayout.SOUTH);
		chatRoom.add(connectionDetails, BorderLayout.NORTH);
		
		// add chatRoom panel to frame
		window.add(chatRoom, BorderLayout.CENTER);
		
		window.pack();
		window.setVisible(true);
	}

	public void onReceivedMessage(String msg)
	{
		System.out.println(msg);
		chatLog.append(msg);
		chatLog.append(System.lineSeparator());
	}

	@Override
	public void onReceiveConnection(String name, boolean isConnected)
	{
		if (isConnected)
		{
			users.add(name);
		}
		
		else
		{
			users.remove(name);
		}
		
		usersTextArea.setText("");
		for(int i = 0; i < users.size(); i++)
		{
			usersTextArea.append(users.get(i));
			usersTextArea.append(System.lineSeparator());
		}
	}
}