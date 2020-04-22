import java.awt.BorderLayout; 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultCaret;

public class ChatUI extends JFrame implements OnReceive
{
	static SocketClient client;
	static JButton clickit;
	static JTextArea history;
	
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
		window.setPreferredSize(new Dimension(800,800));
		
		JPanel connectionDetails = new JPanel();
		window.add(connectionDetails, BorderLayout.NORTH);
		
		JTextField username = new JTextField("Enter username");
		username.setHorizontalAlignment(JTextField.CENTER);
		username.setPreferredSize(new Dimension(160, 30));
		
		JTextField host = new JTextField();
		host.setText("127.0.0.1");
		
		JTextField port = new JTextField();
		port.setText("3001");
		
		JButton connect = new JButton();
		connect.setText("Connect");
		
		connectionDetails.add(username);
		connectionDetails.add(host);
		connectionDetails.add(port);
		connectionDetails.add(connect);
		
		JButton click = new JButton("Send");
		clickit = click;
		click.setPreferredSize(new Dimension(100,30));
		click.setText("Send");
		click.setEnabled(true);
		
		connect.addActionListener(new ActionListener()
		{
		    @Override
		    public void actionPerformed(ActionEvent e)
		    {
		    	client = new SocketClient();
		    	int _port = -1;
		    	
		    	//String username = username.getText();
				//usersTextArea.append("\n" + username);
		    	
		    	try
		    	{
		    		_port = Integer.parseInt(port.getText());
		    	}
		    	
		    	catch(Exception num)
		    	{
		    		System.out.println("Port not a number");
		    	}
		    	
		    	if(_port > -1)
		    	{
			    	client = SocketClient.connect(host.getText(), _port);
			    	client.registerMessageListener(window);
			    	client.postConnectionData();
			    	connect.setEnabled(false);
			    	click.setEnabled(true);
		    	}
		    }
		});
		
		//JPanel area = new JPanel();
		//area.setLayout(new BorderLayout());
		//window.add(area);
		
		/*JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		
		JTextArea ta = new JTextArea();
		ta.setEditable(false);
		history = ta;
		history.setWrapStyleWord(true);
		history.setAutoscrolls(true);
		history.setLineWrap(true);
		
		JScrollPane scroll = new JScrollPane(history);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		DefaultCaret caret = (DefaultCaret)history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		container.add(scroll, BorderLayout.CENTER);*/
		
		//JPanel spacer = new JPanel();
		//JPanel panelSize = new JPanel();
		//container.setPreferredSize(panelSize);
		//spacer.setPreferredSize(panelSize);
		JPanel usersArea = new JPanel();
		usersArea.setPreferredSize(new Dimension(200, 500));
		usersArea.setLayout(new BorderLayout());
		
		JPanel chatArea = new JPanel();
		chatArea.setPreferredSize(new Dimension(200, 500));
		chatArea.setLayout(new BorderLayout());
		
		window.add(usersArea, BorderLayout.EAST);
		window.add(chatArea, BorderLayout.WEST);
		
		/*area.add(click, BorderLayout.SOUTH);
		area.add(chatArea, BorderLayout.WEST);
		area.add(usersArea, BorderLayout.EAST);*/
		//area.add(spacer, BorderLayout.EAST);
		
		window.pack();
		window.setVisible(true);
	}

	public void onReceivedMessage(String msg)
	{
		if(history != null)
		{
			history.append(msg);
			history.append(System.lineSeparator());
		}
	}
}