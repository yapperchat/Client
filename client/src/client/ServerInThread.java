package client;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import core.command.*;
import core.encryption.Encryption;
import core.encryption.EncryptionException;
import core.message.Message;

@SuppressWarnings("unused")
public class ServerInThread implements Runnable {
	
	private Socket socket;
    private Client client;
	
	public ServerInThread(Socket socket, Client client) {
		this.socket = socket;
        this.client = client;
	}

	@Override
	public void run() {
		print("Welcome :" + this.client.getUserName());
        print("Local Port :" + socket.getLocalPort());
        print("Server = " + socket.getRemoteSocketAddress() + ":" + socket.getPort());
        
        try {
            ObjectInputStream serverIn = new ObjectInputStream(socket.getInputStream());
            
            while (!socket.isClosed() && this.client.isRunning()) {
                	try {
                		Message input = (Message) serverIn.readObject();
                		
                		switch (input.getAttachmentType()) {
                			case NONE:
                				print("[" + input.getTimestamp() + "] " + input.getUser() + ": " + input.getText());
                				break;
                			case IMAGE:
                				//Load Image
                				break;
                			case FILE:
                				//Load File
                				break;
                			case CLIENTINSTRUCTION:
                				Instruction inst = (Instruction) input.getAttachment();
                				inst.run(client, input.getArgs());
                				break;
                			//Should not happen
                			case SERVERINSTRUCTION:
                				break;
                		}
                	} catch (ClassNotFoundException e) {
                		System.out.println(e);
                	} catch (EOFException e) {
                		break;
                	} catch (SocketException e) {
                		break;
                	}
            }
            serverIn.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
	}
	
	public void print(String message) {
    	this.client.output(message);
    }
	
	/*public String getDecryptedText(String message) {
		String output = "";
		try {
			output = Encryption.decrypt(message, this.client.getKey());
		} catch (EncryptionException e) {
			output = "WARNING: ENCRYPTION EXCEPTION";
			System.out.println(e.getMessage());
		}
		
		return output;
	}*/

}
