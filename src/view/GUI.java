package view;

import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.xml.internal.messaging.saaj.soap.JpegDataContentHandler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.awt.*;
import javax.swing.*;
import java.util.*;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * Created by Eric on 6-4-2016.
 */
public class GUI extends JFrame {

	private Scanner scanner;
	private Map<JButton,ChatWindow> chatmap = new HashMap<JButton,ChatWindow>();
	private JPanel chatLobbyPanel = null;
	private ChatWindow currentwindow = null;
	private Dimension chatwindowsize = new Dimension(200,400);
	private Dimension textfieldsize = new Dimension(150, 20);
	private Dimension textareasize = new Dimension(190, 325);
	private Dimension buttonsize = new Dimension(200, 40);
	private Dimension group22size = new Dimension(100,400);
	private Dimension fillupspace = new Dimension(5, 20);
	private Dimension framesize = new Dimension(500,400);
	private final int rens = 1;
	private final int birte = 2;
	private final int coen = 3;
	private final int eric = 4;

//    public GUI() {
//        scanner = new Scanner(System.in);
//    }


	public void showError(String s) {
		System.out.println("ERROR: " + s);
	}


	public String getHostName() {
		System.out.println("Enter the host name.");
		return scanner.nextLine();
	}

	public int getPortNumber() {
		System.out.println("Enter the port number. Should be an integer between 1 and 65536");
		return scanner.nextInt();
	}

	public void showStartScreen() {
	}

	public String sendMessage() {
		System.out.println("Enter message");
		return scanner.nextLine();
	}

	public GUI() {
		/*
		Create frame.
		 */
		super("Chatlobby");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setSize(framesize);

		/*
		Make a new JPanel to add to the frame's content pane.
		 */
		chatLobbyPanel = new JPanel();
		chatLobbyPanel.setLayout(new BorderLayout());
		this.getContentPane().add(chatLobbyPanel, BorderLayout.CENTER);

		/*
		Make a new JPanel, representing a list of the players of the group, and add it to the top-level JPanel.
		 */
		JPanel group22 = new JPanel();
		group22.setLayout(new BoxLayout(group22, BoxLayout.Y_AXIS));
		group22.setPreferredSize(group22size);
		group22.setMinimumSize(group22size);
		group22.setMaximumSize(group22size);
		group22.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Group 22"));
		group22.add(Box.createRigidArea(fillupspace));
		group22.add(new JLabel("Eric"));
		group22.add(Box.createRigidArea(fillupspace));
		group22.add(new JLabel("Rens"));
		group22.add(Box.createRigidArea(fillupspace));
		group22.add(new JLabel("Coen"));
		group22.add(Box.createRigidArea(fillupspace));
		group22.add(new JLabel("Birte"));
		chatLobbyPanel.add(group22, BorderLayout.WEST);

		/*
		Make a new JPanel, representing the available chats, with JButtons to get to the chats.
		 */
		JPanel chats = new JPanel();
		chats.setLayout(new BoxLayout(chats, BoxLayout.Y_AXIS));
		chats.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Chats"));
		JButton chat1 = new ChatButton("Chat 1");
		JButton chat2 = new ChatButton("Chat 2");
		JButton chat3 = new ChatButton("Chat 3");
		JButton newchat = new JButton("New Chat");
		chatmap.put(chat1,null);
		chatmap.put(chat2,null);
		chatmap.put(chat3,null);
        chats.add(newchat);
		chats.add(chat1);
		chats.add(chat2);
		chats.add(chat3);
		chatLobbyPanel.add(chats, BorderLayout.CENTER);

		/*
		Make a new JPanel, with a TextArea to display the chat and a TextField to type messages.
		 */
		ChatWindow chatpanel1 = new ChatWindow();
		chatmap.put(chat1, chatpanel1);
		chatmap.put(chat2, chatpanel1);
		chatmap.put(chat3, chatpanel1);
		currentwindow = chatpanel1;
		chatLobbyPanel.add(currentwindow, BorderLayout.EAST);
		setResizable(false);
	}

	private class ChatChooser implements ActionListener {

		public void actionPerformed(ActionEvent a) {
			((JButton) a.getSource()).setBackground(Color.LIGHT_GRAY);
			for (Map.Entry<JButton, ChatWindow> e: chatmap.entrySet()) {
				if (!a.getSource().equals(e.getKey())) {
					e.getKey().setBackground(Color.WHITE);
				}
			}
			if (!currentwindow.equals(chatmap.get(a.getSource()))) {
				chatLobbyPanel.remove(currentwindow);
				currentwindow = chatmap.get(a.getSource());
				chatLobbyPanel.add(currentwindow, BorderLayout.EAST);
			}
		}
	}

    private class NewChatButton implements ActionListener {

        public void actionPerformed(ActionEvent e) {

        }
    }

	private class SendingField implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			JTextArea textarea = ((ChatWindow)((JComponent)e.getSource()).getParent()).messages;
			JTextField textfield = (JTextField)e.getSource();
			textarea.setText(textarea.getText() + "\n \n " + "You: " + textfield.getText() );
			textfield.setText("");
		}
	}

	private class ChatButton extends JButton {

		public ChatButton(String name) {
			super(name);
			setMinimumSize(buttonsize);
			setMaximumSize(buttonsize);
			setBackground(Color.WHITE);
			addActionListener(new ChatChooser());
		}
	}

	private class ChatWindow extends JPanel {

		public JTextField textfield;
		public JTextArea messages;

		public ChatWindow() {
			super();
			setPreferredSize(chatwindowsize);
			setMinimumSize(chatwindowsize);
			setMaximumSize(chatwindowsize);
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Messages"));
			messages = new JTextArea();
			messages.setEditable(false);
			messages.setLayout(new BoxLayout(messages, BoxLayout.Y_AXIS));
			messages.setLineWrap(true);
            JScrollPane scrollmessages = new JScrollPane(messages, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollmessages.setPreferredSize(textareasize);
            scrollmessages.setMaximumSize(textareasize);
            scrollmessages.setMinimumSize(textareasize);
			add(scrollmessages);
			textfield = new JTextField();
			add(Box.createRigidArea(new Dimension(5,5)));
			add(textfield);
			textfield.setPreferredSize(textfieldsize);
			textfield.setMaximumSize(textfieldsize);
			textfield.setMinimumSize(textfieldsize);
			textfield.addActionListener(new SendingField());
		}
	}
//    public void showError(String s) {
//
//    }
//
//
//    public String getHostName() {
//        return null;
//    }
//
//    public int getPortNumber() {
//        return 0;
//    }
//
//    public String sendMessage() {
//        return null;
//    }

	public void printMessage(String message, int pc) {
		String name = null;
		switch (pc) {
			case rens:
				name = "Rens";
			case birte:
				name = "Birte";
            case coen:
                name = "Coen";
            case eric:
                name = "Eric";
		}
		currentwindow.messages.setText(currentwindow.messages.getText() + "\n " + name + ": " + message + "\n" );
	}

}