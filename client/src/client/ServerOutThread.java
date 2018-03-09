package client;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import core.command.*;
import core.message.Message;

@SuppressWarnings("unused")
public class ServerOutThread implements Runnable {
	
	private Socket socket;
    private Client client;
    private final LinkedList<Message> messagesToSend;
	
    public synchronized void addNextMessage(Message message) {
            messagesToSend.push(message);
    }
    
	public ServerOutThread(Socket socket, Client client) {
		this.socket = socket;
        this.client = client;
        messagesToSend = new LinkedList<Message>();
	}

	@Override
	public void run() {
		try {
            ObjectOutputStream serverOut = new ObjectOutputStream(socket.getOutputStream());
            serverOut.flush();
            
            while (!socket.isClosed() && this.client.isRunning()) {
                if(!messagesToSend.isEmpty()){
                    Message nextSend;
                    synchronized (messagesToSend) {
                        nextSend = messagesToSend.pop();
                    }
                    serverOut.writeObject(nextSend);
                    serverOut.flush();
                }
            }
            serverOut.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
	}
	
	public void print(String message) {
    	this.client.output(message);
    }

}
