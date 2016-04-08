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
	private Dimension buttonsize = new Dimension(200, 40);
	private Map<JButton,ChatWindow> chatmap = new HashMap<JButton,ChatWindow>();
    private JPanel chatLobbyPanel = null;
    private ChatWindow currentwindow = null;
    private Dimension textfieldsize = new Dimension(200, 40);
    private Dimension textareasize = new Dimension(200, 300);
    private Dimension group22size = new Dimension(100,400);

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
		setSize(500, 400);

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
		Dimension fillupspace = new Dimension(5, 20);
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
//        JButton newchat = new JButton("Press here to make a new chat");
		chatmap.put(chat1,null);
		chatmap.put(chat2,null);
		chatmap.put(chat3,null);
		chats.add(chat1);
		chats.add(chat2);
		chats.add(chat3);
//        chats.add(newchat);
		chatLobbyPanel.add(chats, BorderLayout.CENTER);

		/*
		Make a new JPanel, with a TextArea to display the chat and a TextField to type messages.
		 */
		ChatWindow chatpanel1 = new ChatWindow();
        chatmap.put(chat1, chatpanel1);
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

    private class SendingField implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            ((JComponent)e.getSource()).getParent();
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
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(200, 100));
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Messages"));
            JTextArea messages = new JTextArea(10, 10);
            messages.setEditable(false);
            messages.setLayout(new BoxLayout(messages, BoxLayout.Y_AXIS));
            messages.setPreferredSize(textareasize);
            messages.setMaximumSize(textareasize);
            messages.setMinimumSize(textareasize);
            messages.setLineWrap(true);
            messages.setText("\n First chat1message \n" + messages.getText());
            messages.setText("\n Second chat1message \n" + messages.getText());
            messages.setText("\n Third chat1message \n" + messages.getText());
            add(messages);
            textfield = new JTextField();
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

    public void printMessage(String message) {
        currentwindow.textfield.setText("\n " + message + "\n" + currentwindow.textfield.getText());
    }

}