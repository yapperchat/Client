package client;

import java.io.Serializable;

import client.Client;

public interface ClientInstruction extends Serializable {
	public void run(Client client, String[] args);
}
