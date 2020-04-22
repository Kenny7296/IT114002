import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;


public class SocketClient
{
	private Socket server;
	//private OnReceive switchListener;
	private OnReceive messageListener;
	
	/*public void registerSwitchListener(OnReceive listener)
	{
		this.switchListener = listener;
	}*/
	
	public void registerMessageListener(OnReceive listener)
	{
		this.messageListener = listener;
	}
	
	private Queue<Payload> toServer = new LinkedList<Payload>();
	private Queue<Payload> fromServer = new LinkedList<Payload>();
	
	public static SocketClient connect(String address, int port)
	{
		SocketClient client = new SocketClient();
		client._connect(address, port);
		Thread clientThread =  new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					client.start();
				}
				
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		
		clientThread.start();
		
		try
		{
			Thread.sleep(50);
		}
		
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		return client;
	}
	
	private void _connect(String address, int port)
	{
		try
		{
			server = new Socket(address, port);
			System.out.println("Client connected!");
		}
		
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void start() throws IOException
	{
		if(server == null)
		{
			return;
		}
		
		System.out.println("Client Started");
		try(	ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(server.getInputStream());)
		{
			Thread inputThread = new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						while(!server.isClosed())
						{
							Payload p = toServer.poll();
							
							if(p != null)
							{
								out.writeObject(p);
							}
							
							else
							{
								try
								{
									Thread.sleep(8);
								}
								
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						}
					}
					
					catch(Exception e)
					{
						System.out.println("Client shutdown");
					}
					
					finally
					{
						close();
					}
				}
			};
			
			inputThread.start();
			
			Thread fromServerThread = new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						Payload p;
						
						while(!server.isClosed() && (p = (Payload)in.readObject()) != null)
						{
							//System.out.println(fromServer);
							//processPayload(fromServer);
							fromServer.add(p);
						}
						
						System.out.println("Stopping server listen thread");
					}
					
					catch (Exception e)
					{
						if(!server.isClosed())
						{
							e.printStackTrace();
							System.out.println("Server closed connection");
						}
						
						else
						{
							System.out.println("Connection closed");
						}
					}
					
					finally
					{
						close();
					}
				}
			};
			
			fromServerThread.start();
			
			Thread payloadProcessor = new Thread()
			{
				@Override
				public void run()
				{
					while(!server.isClosed())
					{
						Payload p = fromServer.poll();
						
						if(p != null)
						{
							processPayload(p);
						}
						
						else
						{
							try
							{
								Thread.sleep(8);
							}
							
							catch (InterruptedException e)
							{
								e.printStackTrace();
							}
						}
					}
				}
			};
			
			payloadProcessor.start();
			
			while(!server.isClosed())
			{
				Thread.sleep(50);
			}
			
			System.out.println("Exited loop");
			System.exit(0);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			close();
		}
	}
	
	public void postConnectionData(String clientName)
	{
		Payload payload = new Payload();
		payload.setPayloadType(PayloadType.CONNECT);
		payload.setMessage(clientName);
		toServer.add(payload);
	}
	
	public void doClick(boolean isOn)
	{
		Payload payload = new Payload();
		payload.setPayloadType(PayloadType.SWITCH);
		payload.IsOn(isOn);
		toServer.add(payload);
	}
	
	public void sendMessage(String message)
	{
		Payload payload = new Payload();
		payload.setPayloadType(PayloadType.MESSAGE);
		payload.setMessage(message);
		toServer.add(payload);
	}
	
	private void processPayload(Payload payload)
	{
		System.out.println(payload);
		String msg = "";
		switch(payload.getPayloadType())
		{
		
		case CONNECT:
			msg = String.format("Client \"%s\" has connected", payload.getMessage());
			System.out.println(msg);
			if(messageListener != null) {
				messageListener.onReceivedMessage(msg);
			}
			break;
		case DISCONNECT:
			msg = String.format("Client \"%s\" has disconnected", payload.getMessage());
			System.out.println(msg);
			if(messageListener != null)
			{
				messageListener.onReceivedMessage(msg);
			}
			break;
		case MESSAGE:
			System.out.println(
					String.format("%s", payload.getMessage())
			);
			if(messageListener != null)
			{
				messageListener.onReceivedMessage(String.format("%s ", payload.getMessage()));
			}
			break;
		case STATE_SYNC:
			System.out.println("Sync");
			//break; //this state will drop down to next state
		/*case SWITCH:
			System.out.println("switch");
			if (switchListener != null)
			{
				switchListener.onReceivedSwitch(payload.IsOn());
			}
			if(messageListener != null)
			{
				messageListener.onReceivedMessage(
						String.format("%s turned the button %s", 
								payload.getMessage(),
								payload.IsOn()?"On":"Off")
				);
			}*/
			break;
		default:
			System.out.println("Unhandled payload type: " + payload.getPayloadType().toString());
			break;
		}
	}
	
	private void close()
	{
		if(server != null)
		{
			try
			{
				server.close();
				System.out.println("Closed socket");
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args)
	{
		SocketClient client = new SocketClient();
		SocketClient.connect("127.0.0.1", 3001);
		
		try
		{
			//if start is private, it's valid here since this main is part of the class
			client.start();
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		/*
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
		
		// add user info to chat area
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
	*/
	}
}

interface OnReceive
{
	void onReceivedMessage(String msg);
}