public class UISample {
	public static boolean isRunning = true;
	public static void main(String[] args) {
		//create frame
		JFrame frame = new JFrame("Devon's Chat");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(new BorderLayout());
		//create panel
		JPanel rps = new JPanel();
		rps.setPreferredSize(new Dimension(400,400));
		rps.setLayout(new BorderLayout());
		//create text area for messages
		JTextArea textArea = new JTextArea();
		//don't let the user edit this directly
		textArea.setEditable(false);
		textArea.setText("");
		//create panel to hold multiple controls
		JPanel attemptsArea = new JPanel();
		attemptsArea.setLayout(new BorderLayout());
		//add text area to history/attempts
		attemptsArea.add(textArea, BorderLayout.CENTER);
		attemptsArea.setBorder(BorderFactory.createLineBorder(Color.black));
		//add history/attempts to panel
		rps.add(attemptsArea, BorderLayout.CENTER);
		//create panel to hold multiple controls
		JPanel userInput = new JPanel();
		
		
		    	
		
		//Interaction will be our instance to interact with
		//socket client
		Interaction interaction = new Interaction();
		Thread clientMessageReader = new Thread() {
			@Override
			public void run() {
				while(isRunning && interaction.isClientConnected()) {
					String m = interaction.getMessage();
					if(m != null) {
						System.out.println("Got message " + m);
						if(m.indexOf("[name]") > -1) {
							String[] n = m.split("]");
							frame.setTitle(frame.getTitle() + " - " + n[1]);
						}
						else {
							System.out.println("Appending to textarea");
							textArea.append(m +"\n");
						}
					}
				
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("Message reader thread finished");
			}
			
		};
		
		JPanel connectionPanel = new JPanel();
		JTextField hostTextField = new JTextField();
		hostTextField.setText("127.0.0.1");
		JTextField portTextField = new JTextField();
		portTextField.setText("3000");
		JTextField errorTextField = new JTextField();
		errorTextField.setText("");
		JButton connect = new JButton();
		connect.setText("Connect");
		connect.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        String hostStr = hostTextField.getText();
		        String portString = portTextField.getText();
		        try {
		        	int port = Integer.parseInt(portString.trim());
		        	interaction.connect(hostStr, port, errorTextField);
		        	errorTextField.setText("Success!");
		        	connectionPanel.setVisible(false);
		        	clientMessageReader.start();
		        	System.out.println("Connected");
		        }
		        catch(Exception ex) {
		        	ex.printStackTrace();
		        	errorTextField.setText(ex.getMessage());
		        }
		    }
		});
		connectionPanel.add(hostTextField);
		connectionPanel.add(portTextField);
		connectionPanel.add(connect);
		connectionPanel.add(errorTextField);
		frame.addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
				  	interaction.client.disconnect();
				    System.exit(0);
				  }
		});
		
		
		JTextField input = new JTextField();
		input.setText("");
		input.setPreferredSize(new Dimension(100,30));
		
		
		//create send button
		JButton send = new JButton();
		send.setText("Send");
		send.setPreferredSize(new Dimension(100,30));
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				interaction.sendChoice(input.getText());
				input.setText("");;
			}
		});
		
		userInput.add(input);
		userInput.add(send);
		//add panel to rps panel
		rps.add(userInput, BorderLayout.SOUTH);
		//add rps panel to frame
		frame.add(rps, BorderLayout.CENTER);
		frame.add(connectionPanel, BorderLayout.NORTH);
		frame.pack();
		frame.setVisible(true);
		
	}
	
}
class Interaction {
	SocketClient client;
	public Interaction() {
		
	}
	public void connect(String host, int port, JTextField errorField) throws IOException{
		//thread just so we don't lock up main UI
		Thread connectionThread = new Thread() {
			@Override
			public void run() {
				client = new SocketClient();
				try {
					client.connect(host, port);
					client.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					errorField.setText(e.getMessage());
					errorField.getParent().setVisible(true);
				}//this terminates when client is closed
				
				System.out.println("Connection thread finished");
			}
		};
		connectionThread.start();
	}
	public void sendChoice(String choice) {
		client.sendChoice(choice);
	}
	public void sendpm(String pm) {
		client.sendpm(pm);
	}
	public boolean isClientConnected() {
		if(client == null) {
			return true;//just so loop doesn't die early
		}
		return client.isStillConnected();
	}
	public String getMessage() {
		if(client == null) {
			return null;
		}
		return client.messages.poll();
	}
}