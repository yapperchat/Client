package client;

import java.io.Serializable;

public interface ClientInstruction extends Serializable {
	public void run(Client client, String[] args);
}
