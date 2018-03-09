package client;

import java.io.Serializable;

import javax.swing.JOptionPane;

import core.application.Application;
import core.command.Instruction;

public class Alert implements Instruction, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public void run(Application application, String[] args) {
		JOptionPane.showMessageDialog((Client) application, args[0], "Alert", JOptionPane.PLAIN_MESSAGE);
	}

}
