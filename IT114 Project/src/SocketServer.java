import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SocketServer
{
	int port = 3002;
	public static boolean isRunning = true;
	private List<ServerThread> clients = new ArrayList<ServerThread>();

	Queue<String> messages = new LinkedList<String>();
	public static long ClientID = 0;
	public synchronized long getNextId()
	{
		ClientID++;
		return ClientID;
	}
	
	private void start(int port)
	{
		this.port = port;
		startQueueReader();
		
		System.out.println("Waiting for client...");
		try (ServerSocket serverSocket = new ServerSocket(port);)
		{
			while(SocketServer.isRunning)
			{
				try
				{
					Socket client = serverSocket.accept();
					System.out.println("Client connecting...");
					ServerThread thread = new ServerThread(client, this);
					thread.start();
					thread.setClientId(getNextId());
					clients.add(thread);
					System.out.println("Client added to clients pool!");
				}
				
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				isRunning = false;
				Thread.sleep(50);
				System.out.println("Closing server socket...");
			}
			
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	void startQueueReader()
	{
		System.out.println("Preparing Queue Reader...");
		Thread queueReader = new Thread()
		{
			@Override
			public void run()
			{
				String message = "";
				try(FileWriter write = new FileWriter("chathistory.txt", true))
				{
					while(isRunning)
					{
						message = messages.poll();
						if(message != null)
						{
							message = messages.poll();
							write.append(message);
							write.write(System.lineSeparator());
							write.flush();
						}
						
						sleep(50);
					}
				}
				
				catch(IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		};
		
		queueReader.start();
		System.out.println("Started Queue Reader!");
	}
	
	@Deprecated
	int getClientIndexByThreadId(long id)
	{
		for(int i = 0, l = clients.size(); i < l;i++)
		{
			if(clients.get(i).getId() == id)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public synchronized void broadcast(Payload payload, String name)
	{
		String msg = payload.getMessage();
		payload.setMessage(
				(name!=null?name:"[Name Error]") 
				+ (msg != null?": "+ msg:"")
		);
		
		broadcast(payload);
	}
	
	public synchronized void broadcast(Payload payload)
	{
		System.out.println("Sending message to " + clients.size() + " clients");
		
		Iterator<ServerThread> iter = clients.iterator();
		while(iter.hasNext())
		{
			ServerThread client = iter.next();
			boolean messageSent = client.send(payload);
			if(!messageSent)
			{
				iter.remove();
				System.out.println("Removed client " + client.getId());
			}
		}
	}
	
	public synchronized void broadcast(Payload payload, long id)
	{
		int from = getClientIndexByThreadId(id);
		String msg = payload.getMessage();
		payload.setMessage(
				(from>-1?"Client[" + from+"]":"unknown") 
				+ (msg != null?": "+ msg:"")
		);

		broadcast(payload);
		
	}
	
	public synchronized void broadcast(String message, long id)
	{
		Payload payload = new Payload(PayloadType.MESSAGE, message);
		broadcast(payload, id);
	}
	
	public synchronized void sendToClientByName(String name, Payload payload)
	{
		for(int i = 0; i < clients.size(); i++)
		{
			if(clients.get(i).getClientName().equals(name))
			{
				payload.setPayloadType(PayloadType.MESSAGE);
				clients.get(i).send(payload);
				break;
			}
		}
	}
	
	public static void main(String[] args)
	{
		
		int port = 3001;
		if(args.length >= 1)
		{
			String arg = args[0];
			try
			{
				port = Integer.parseInt(arg);
			}
			
			catch(Exception e)
			{
			}
		}
		
		System.out.println("Starting Server...");
		SocketServer server = new SocketServer();
		System.out.println("Listening on port " + port + "...");
		server.start(port);
		System.out.println("Server Stopped!");
	}
}
