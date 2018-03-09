package client;

import java.io.Serializable;

import javax.swing.JOptionPane;

import core.command.Instruction;

public class Alert implements Instruction, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public void run(Client client, String[] args) {
		JOptionPane.showMessageDialog(client, args[0], "Alert", JOptionPane.PLAIN_MESSAGE);
	}

}
