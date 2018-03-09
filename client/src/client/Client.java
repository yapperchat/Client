package client;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import constraints.Constraints;
import core.application.Application;
import core.command.Command;
import core.message.AttachmentType;
import core.message.Message;
import core.misc.Misc;
import tools.Tools;
import window.ApplicationWindow;

public class Client extends ApplicationWindow implements Application {
    
	private static final long serialVersionUID = 1L;
	private static final int portNumber = 4444;
    public final String ID;
    
    public static Client client = null;
    
    private boolean send = false;
    
    private String userName;
    private ServerInThread serverIn;
    private ServerOutThread serverOut;
    private Socket socket;
    
    private String serverHost;
    private int serverPort;
    
    private ArrayList<Command> commands;
    
    private GridBagLayout layout;
    
    private JTextArea textArea;
    private Constraints c_textArea;
    
    private JTextField textField;
    private Constraints c_textField;
    
    /*private JTextField keyField;
    private Constraints c_keyField;*/
    
    public static void main(String[] args) {
    	String readName = System.getProperty("user.name");
        client = new Client(readName, portNumber);
        client.startClient();
    }
    
    private Client (String userName, int portNumber) {
    	super("ChatApp");
    	Tools.setLookAndFeel();
        this.userName = userName;
        this.ID = userName;
        this.serverPort = portNumber;
        this.initActions();
        try {
        	this.serverHost = InetAddress.getByName("51S500036590").getHostAddress();
        } catch (UnknownHostException e) {
        }  
        
        layout = new GridBagLayout();
    	setLayout(layout);
    	
    	textArea = new JTextArea();
    	textArea.setColumns(75);
    	textArea.setRows(20);
    	c_textArea = new Constraints(0,1);
    	
    	textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setVisible(true);

        JScrollPane scroll = new JScrollPane (textArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        add(scroll, c_textArea);
        
    	textField = new JTextField();
        textField.setColumns(50);
        textField.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		for (Command command : commands) {
        			for (String trigger : command.getTriggers()) {
        				if (trigger.equals(textField.getText().split(" ")[0])) {
        					process(textField.getText());
        					return;
        				}
        			}
        		}
        		send = true;
        	}
        });
        c_textField = new Constraints(0,2);
        add(textField, c_textField);
        
        /*keyField = new JTextField();
        keyField.setColumns(25);
        c_keyField = new Constraints(0,0);
        add(keyField, c_keyField);*/
        
        pack();
        setVisible(true);
        setResizable(false);
    }
    
    public void output(String message) {
    	this.textArea.setText(this.textArea.getText() + "\n" + message);
    	this.textArea.setCaretPosition(this.textArea.getText().length());
    }

    private void startClient() {
        try{
            socket = new Socket(serverHost, serverPort);
            Thread.sleep(1000);
            serverIn = new ServerInThread(socket, this);
            serverOut = new ServerOutThread(socket, this);
            Thread serverInThread = new Thread(serverIn);
            Thread serverOutThread = new Thread(serverOut);
            serverInThread.start();
            serverOutThread.start();
            while(this.isRunning() && serverOutThread.isAlive() && serverInThread.isAlive()) {
                if (send) {
                	String time = Misc.getTime();
                	String text = textField.getText();
                	//Do Encryption here
                	
                	
                	Message message = new Message(text, time, this.userName, this.ID);
                    serverOut.addNextMessage(message);
                    send = false;
                    textField.setText("");
                }
                Thread.sleep(200);
            }
            
            socket.close();
            
        }catch(IOException ex){
            output("Could not connect to server!");
        }catch(InterruptedException ex){
            output("Connection interrupted!");
        }
    }
    
    
    public void setHost(String serverHost) {
    	this.serverHost = serverHost;
    }
    
    public String getUserName() {
    	return this.userName;
    }
    
    private void initActions() {
    	this.commands = new ArrayList<Command>();
    	this.commands.add(new Command() {
    		public void run(String[] args) {
    			output("[COMMAND] COMMANDS:");
    			for (int i = 1; i < commands.size(); i++) {
    				output(commands.get(i).getTriggers()[0] + ": " + commands.get(i).getInfo());
    			}
    		}
    		
    		public String[] getTriggers() {
    			return new String[] {
    				"/help",
    				"/commands",
    				"/h",
    			};
    		}
    	});
    	this.commands.add(new Command() {
    		public void run(String[] args) {
    			String temp = args[0];
    			
    			for (int i = 1; i < args.length; i++) {
    				temp += (" " + args[i]);
    			}
    			
    			userName = temp;
    		}
    		
    		public String[] getTriggers() {
    			return new String[] {
    					"/name",
    					"/n",
    			};
    		}
    		
    		public String getInfo() {
    			return "Changes the user's name to the entered value.";
    		}
    	});
    	this.commands.add(new Command() {
    		public void run(String[] args) {
    			String temp = args[0];
    			
    			for (int i = 1; i < args.length; i++) {
    				temp += (" " + args[i]);
    			}
    			
    			args[0] = temp;
    			Serializable inst = new Alert();
    			Message message = new Message(("[Alert]:" + args[0]), Misc.getTime(), userName, ID, inst, AttachmentType.CLIENTINSTRUCTION, args);
                serverOut.addNextMessage(message);
    		}
    		
    		public String[] getTriggers() {
    			return new String[] {
    				"/alert"
    			};
    		}
    		
    		public String getInfo() {
    			return "Sends an alert with the specified message.";
    		}
    	});
    }
    
    public boolean process(String command) {
    	String[] commands = command.split(" ");
    	command = commands[0];
    	if (commands.length > 1) {
    		String[] temp = commands;
    		commands = new String[commands.length - 1];
    		for (int i = 0; i < commands.length; i++) {
    			commands[i] = temp[i + 1];
    		}
    	}
    	for (Command c : this.commands) {
    		for (String trigger : c.getTriggers()) {
    			if (trigger.equals(command)) {
        			c.run(commands);
        			textField.setText("");
        			return true;
    			}
    		}
    	}
    	return false;
    }
    
    public void disconnect() {
    		try {
				this.socket.close();
			} catch (IOException e) {
				this.serverIn  = null;
				this.serverOut = null;
			}
    }
    
    public static Client getClient() {
    	return client;
    }
    
    /*public byte[] getKey() {
    	int keySize = 16;
    	byte[] key = this.keyField.getText().getBytes();
    	
    	byte[] output = Arrays.copyOf(key, keySize);
    	
    	return output;
    }*/
    
}