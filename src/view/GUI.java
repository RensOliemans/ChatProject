package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Scanner;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import controller.*;
import java.util.List;
import java.net.*;

/**
 * Created by Eric on 6-4-2016.
 */
public class GUI extends JFrame {

	private JPanel peopleOnline;
	private ChatsOverview chatsOverview;
	private JPanel availableChatsPanel;
	private JPanel newChatPanel;
	private ChatRoom chatRoom;
	private JScrollPane availableChatsScrollPane;
	private int pcnumber;
	private MultiCast2 multiCast;
	private Dimension framesize = new Dimension(700,400);
	private List<Integer> group22 = new ArrayList<Integer>();
	private Map<ChatButton, MessageScroll> chatmap = new HashMap<ChatButton, MessageScroll>();
	private Map<MessageScroll, List<Integer>> participantsmap = new HashMap<MessageScroll, List<Integer>>();
	private Map<MessageScroll, Integer> chatnumbermap = new HashMap<MessageScroll, Integer>();
	private int chatnumber;

	public GUI(int pcnumber, MultiCast2 multiCast) {
		super("ChatUI");
		this.pcnumber = pcnumber;
		this.chatnumber = pcnumber;
		this.multiCast = multiCast;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setPreferredSize(framesize);
		setMinimumSize(framesize);
		setMaximumSize(framesize);
		setLayout(new BorderLayout());
		peopleOnline = new JPanel();
		peopleOnline.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Group22"));
		peopleOnline.setPreferredSize(new Dimension(100,380));
		add(peopleOnline, BorderLayout.WEST);
		chatsOverview = new ChatsOverview();
		add(chatsOverview, BorderLayout.CENTER);
		chatRoom = new ChatRoom();
		add(chatRoom, BorderLayout.EAST);
		Thread updating = new Thread(new Updating());
		updating.start();
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		setResizable(false);
	}

	public void showError(String errormessage	) {
		JOptionPane.showMessageDialog(this, "Error: " + errormessage, "ERROR", JOptionPane.ERROR_MESSAGE);
	}

	public void updateOnlinePeople() {
		group22 = multiCast.presence;
		remove(peopleOnline);
		peopleOnline = new JPanel();
		peopleOnline.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Group22"));
		peopleOnline.setPreferredSize(new Dimension(100,380));
		peopleOnline.setLayout(new BoxLayout(peopleOnline, BoxLayout.Y_AXIS));
		if (group22.contains(new Integer(1))) {
			peopleOnline.add(new JLabel("Rens"));
		}
		if (group22.contains(new Integer(2))) {
			peopleOnline.add(new JLabel("Birte"));
		}
		if (group22.contains(new Integer(3))) {
			peopleOnline.add(new JLabel("Coen"));
		}
		if (group22.contains(new Integer(4))) {
			peopleOnline.add(new JLabel("Eric"));
		}
		add(peopleOnline, BorderLayout.WEST);
		invalidate();
		validate();
		repaint();
	}


	private class ChatsOverview extends JPanel {

		public ChatsOverview() {
			super();
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Chats Overview"));
			setPreferredSize(new Dimension(200,380));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			availableChatsPanel = new JPanel();
			availableChatsPanel.setLayout(new BoxLayout(availableChatsPanel, BoxLayout.Y_AXIS));
			availableChatsScrollPane = new JScrollPane(availableChatsPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			availableChatsScrollPane.setPreferredSize(new Dimension(180,300));
			add(availableChatsScrollPane);
			newChatPanel = new JPanel();
			NewChatButton newChatButton = new NewChatButton();
			newChatButton.addActionListener(new NewChatListener());
			newChatPanel.add(newChatButton);
			add(newChatPanel);
		}
	}

	private class ChatRoom extends JPanel {

		public JTextArea messages;
		public MessageScroll scrollmessages;
		public JTextField textfield;

		public ChatRoom() {
			super();
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Chatroom"));
			setPreferredSize(new Dimension(400,380));
			setLayout(new BorderLayout());
			messages = new JTextArea();
			messages.setEditable(false);
			messages.setLayout(new BoxLayout(messages, BoxLayout.Y_AXIS));
			messages.setLineWrap(true);
			scrollmessages = new MessageScroll(messages, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollmessages.setPreferredSize(new Dimension(180,300));
			add(scrollmessages);
			JPanel textfieldpanel = new JPanel();
			textfield = new JTextField();
			textfield.setPreferredSize(new Dimension(300, 20));
			textfield.addActionListener(new SendingFieldListener());
			textfieldpanel.add(textfield);
			ImageIcon camera = new ImageIcon();
			ImageIcon imageIcon = new ImageIcon("icon-camera-128small.png");
			JButton addImage = new JButton(imageIcon);
			addImage.setPreferredSize(new Dimension(80,20));
			addImage.addActionListener(new SendingImageListener());
			textfieldpanel.add(addImage);
			add(textfieldpanel, BorderLayout.SOUTH);
		}
	}

	private class ChatButton extends JButton {

		public ChatButton(String title) {
			super(title);
			setPreferredSize(new Dimension(160,30));
			setMinimumSize(new Dimension(160,30));
			setMaximumSize(new Dimension(160,30));
			setBackground(Color.WHITE);
			addActionListener(new ChatChooseListener());
		}
	}

	private class NewChatButton extends JButton {

		public NewChatButton() {
			super("New Chat");
		}
	}

	private class MessageScroll extends JScrollPane {

		public JTextArea messages;

		public MessageScroll(Component view,  int vsbPolicy, int hsbPolicy) {
			super(view, vsbPolicy, hsbPolicy);
			this.messages = (JTextArea)view;
		}
	}

	private class NewChatListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			JPanel newChatOptions = new JPanel();
			NewChatOptionsListener newChatOptionsListener = new NewChatOptionsListener();
			for (Integer i: group22) {
				if (i==1) {
					JCheckBox rensbox = new JCheckBox("Rens");
					rensbox.addItemListener(newChatOptionsListener);
					newChatOptions.add(rensbox);
				}
				if (i==2) {
					JCheckBox birtebox = new JCheckBox("Birte");
					birtebox.addItemListener(newChatOptionsListener);
					newChatOptions.add(birtebox);
				}
				if (i==3) {
					JCheckBox coenbox = new JCheckBox("Coen");
					coenbox.addItemListener(newChatOptionsListener);
					newChatOptions.add(coenbox);
				}
				if (i==4) {
					JCheckBox ericbox = new JCheckBox("Eric");
					ericbox.addItemListener(newChatOptionsListener);
					newChatOptions.add(ericbox);
				}
			}
			int choice = JOptionPane.showConfirmDialog(GUI.this, newChatOptions, "choose participants", JOptionPane.OK_OPTION);
			JTextArea newChatArea = new JTextArea();
			newChatArea.setEditable(false);
			newChatArea.setLayout(new BoxLayout(newChatArea, BoxLayout.Y_AXIS));
			newChatArea.setLineWrap(true);
			MessageScroll newChatPane = new MessageScroll(newChatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			newChatPane.setPreferredSize(new Dimension(180,300));
			ChatButton chatButton = new ChatButton(Integer.toString(chatnumber));
			chatmap.put(chatButton,newChatPane);
			participantsmap.put(newChatPane, newChatOptionsListener.participantlist);
			chatnumbermap.put(newChatPane, chatnumber);
			availableChatsPanel.add(chatButton);
			for (Integer i: newChatOptionsListener.participantlist) {
				multiCast.send("joinrequest:chat" + chatnumber + ";" + ListToString(newChatOptionsListener.participantlist) ,i.intValue());
			}
			chatnumber += 4;
		}
	}

	private String ListToString(List<Integer> list) {
		String liststr = "";
		for (Integer i: list) {
			liststr.concat(Integer.toString(i) + ",");
		}
		return liststr;
	}

	private List StringToList(String string) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < string.length(); i = i+2) {
			list.add(new Integer(string.charAt(i)));
		}
		return list;
	}

	private class ChatChooseListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			chatRoom.remove(chatRoom.scrollmessages);
			chatRoom.scrollmessages = chatmap.get(e.getSource());
			for (ChatButton c: chatmap.keySet()) {
				if (c.equals(e.getSource())) {
					c.setBackground(Color.LIGHT_GRAY);
				}
				else {
					c.setBackground(Color.WHITE);
				}
 			}
			chatRoom.add(chatRoom.scrollmessages, BorderLayout.CENTER);
			chatRoom.invalidate();
			chatRoom.validate();
			chatRoom.repaint();
		}
	}

	private class SendingFieldListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			chatRoom.scrollmessages.messages.setText(chatRoom.scrollmessages.messages.getText() + "\n You: " + ((JTextField)e.getSource()).getText() + "\n");
			((JTextField)e.getSource()).setText("");
			for (Integer i: participantsmap.get(chatRoom.scrollmessages)) {
				multiCast.send("chat" + chatnumbermap.get(chatRoom.scrollmessages) + ":", i);
			}
		}
	}

	private class SendingImageListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			int returnValue = fileChooser.showOpenDialog(GUI.this);
			if (returnValue == fileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				chatRoom.scrollmessages.messages.setText(chatRoom.scrollmessages.messages.getText() + "\n You: sent image" + selectedFile.getName() + "\n");
				for (Integer i: participantsmap.get(chatRoom.scrollmessages)) {
//					multiCast.sendImage(selectedFile.getName(), i);
				}
			}
		}
	}

	private class NewChatOptionsListener implements ItemListener {

		public List<Integer> participantlist = new ArrayList<Integer>();

		public void itemStateChanged(ItemEvent e) {
			if (((JCheckBox)e.getSource()).getText().equals("Rens")) {
				participantlist.add(new Integer(1));
			}
			if (((JCheckBox)e.getSource()).getText().equals("Birte")) {
				participantlist.add(new Integer(2));
			}
			if (((JCheckBox)e.getSource()).getText().equals("Coen")) {
				participantlist.add(new Integer(3));
			}
			if (((JCheckBox)e.getSource()).getText().equals("Eric")) {
				participantlist.add(new Integer(4));
			}
		}
	}

	private class Updating implements Runnable {

		public void run() {
			while (true) {
				try {
					Thread.sleep(500);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				updateOnlinePeople();
			}
		}
	}

	public void printMessage(String message, int src) {
		String name = "";
		switch (src) {
			case 1:
				name = "Rens";
				break;
			case 2:
				name = "Birte";
				break;
			case 3:
				name = "Coen";
				break;
			case 4:
				name = "Eric";
				break;
		}
		if (message.startsWith("joinrequest:chat")) {
			String[] chatnumberandparticipants = message.split(";");
			JOptionPane.showMessageDialog(GUI.this, name + " added you to chat" + chatnumberandparticipants[0].substring(16));
			JTextArea newChatArea = new JTextArea();
			newChatArea.setEditable(false);
			newChatArea.setLayout(new BoxLayout(newChatArea, BoxLayout.Y_AXIS));
			newChatArea.setLineWrap(true);
			MessageScroll newChatPane = new MessageScroll(newChatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			newChatPane.setPreferredSize(new Dimension(180,300));
			ChatButton chatButton = new ChatButton(chatnumberandparticipants[0].substring(16));
			chatmap.put(chatButton,newChatPane);
			List<Integer> stringlist = StringToList(chatnumberandparticipants[1]);
			stringlist.add(new Integer(src));
			participantsmap.put(newChatPane, stringlist);
			chatnumbermap.put(newChatPane, new Integer((chatnumberandparticipants[0].substring(16))));
			availableChatsPanel.add(chatButton);
		}
		else if (message.startsWith("chat")) {
			for (Map.Entry<MessageScroll, Integer> e: chatnumbermap.entrySet()) {
				if (Character.getNumericValue(message.charAt(4)) == e.getValue()) {
					e.getKey().messages.setText(e.getKey().messages.getText() + "\n " + name + message.substring(5) + "\n");
				}
			}
		}
		else {
			JOptionPane.showMessageDialog(GUI.this, name + " sent " + message + ", not via GUI");
		}
	}

	public static void main(String[] args) {
		new GUI(4, new MultiCast2());
	}

}
