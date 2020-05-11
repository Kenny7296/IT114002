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
	private OnReceive onReceiveListener;
	public void registerListener (OnReceive listener)
	{
		this.onReceiveListener = listener;
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
		
		System.out.println("Client Started!");
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
						System.out.println("Client shutdown.");
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
							fromServer.add(p);
						}
						
						System.out.println("Stopping server listen thread.");
					}
					
					catch (Exception e)
					{
						if(!server.isClosed())
						{
							e.printStackTrace();
							System.out.println("Server closed connection.");
						}
						
						else
						{
							System.out.println("Connection closed.");
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
			
			System.out.println("Exited loop.");
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
		Payload payload = new Payload(PayloadType.CONNECT, clientName);
		toServer.add(payload);
	}
	
	public void sendPM(String message, String person)
	{
		try
		{
			if (message.indexOf("@") == 0)
			{
				String[] m = message.split("@");
				String start = m[1];
				String[] part = start.split(" ");
				String clientName = part[0];
				Payload payload = new Payload(PayloadType.DIRECT, message);
				payload.setTarget(clientName);
				toServer.add(payload);
			}
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	String doBold(String message)
	{
		return message.replace("boldTrigger", "<b>");
	}
	
	String doItalic(String message)
	{
		return message.replace("italicTrigger", "<i>");
	}
	
	public void sendMessage(String message)
	{
		message = doBold(message);
		message = doItalic(message);
		
		if (message.indexOf("@") == 0)
		{
			sendPM(message, "");
			return;
		}
		
		else
		{
			Payload payload = new Payload(PayloadType.MESSAGE, message);
			toServer.add(payload);
		}
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
			if(onReceiveListener != null) {
				onReceiveListener.onReceiveConnection(payload.getMessage(), true);
			}
			break;
		case DISCONNECT:
			msg = String.format("Client \"%s\" has disconnected", payload.getMessage());
			System.out.println(msg);
			if(onReceiveListener != null)
			{
				onReceiveListener.onReceiveConnection(payload.getMessage(), false);
			}
			break;
		case MESSAGE:
			System.out.println(
					String.format("%s", payload.getMessage())
			);
			if(onReceiveListener != null)
			{
				onReceiveListener.onReceivedMessage(String.format("%s ", payload.getMessage()));
			}
			break;
		case STATE_SYNC:
			//if(onReceiveListener != null) {
			//	onReceiveListener.onReceiveConnection(payload.getMessage(), true);
			//}
			System.out.println("Sync");
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
				System.out.println("Closed socket.");
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
			client.start();
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

interface OnReceive
{
	void onReceivedMessage(String msg);
	void onReceiveConnection(String name, boolean isConnected);
}
