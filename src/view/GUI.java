package view;

import controller.MultiCast2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

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
	private Dimension framesize = new Dimension(500,400);
	private List<Integer> group22 = new ArrayList<Integer>();
	private Map<ChatButton, MessageScroll> chatmap = new HashMap<ChatButton, MessageScroll>();
	private Map<MessageScroll, List<Integer>> participantsmap = new HashMap<MessageScroll, List<Integer>>();
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

	public void showError(String errormessage) {
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
			availableChatsPanel.setPreferredSize(new Dimension(150,200));
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
			setPreferredSize(new Dimension(200,380));
			setLayout(new BorderLayout());
			messages = new JTextArea();
			messages.setEditable(false);
			messages.setLayout(new BoxLayout(messages, BoxLayout.Y_AXIS));
			messages.setLineWrap(true);
			scrollmessages = new MessageScroll(messages, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollmessages.setPreferredSize(new Dimension(180,300));
			add(scrollmessages);
			textfield = new JTextField();
			textfield.setPreferredSize(new Dimension(150, 20));
			textfield.addActionListener(new SendingFieldListener());
			add(textfield, BorderLayout.SOUTH);
		}
	}

	private class ChatButton extends JButton {

		public ChatButton(String title) {
			super(title);
			setPreferredSize(new Dimension(160,30));
			setMinimumSize(new Dimension(160,30));
			setMaximumSize(new Dimension(160,30));
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
			JOptionPane.showMessageDialog(GUI.this, newChatOptions);
			JTextArea newChatArea = new JTextArea();
			newChatArea.setEditable(false);
			newChatArea.setLayout(new BoxLayout(newChatArea, BoxLayout.Y_AXIS));
			newChatArea.setLineWrap(true);
			MessageScroll newChatPane = new MessageScroll(newChatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			newChatPane.setPreferredSize(new Dimension(180,300));
			ChatButton chatButton = new ChatButton(Integer.toString(chatnumber));
			chatnumber += 4;
			chatmap.put(chatButton,newChatPane);
			participantsmap.put(newChatPane, newChatOptionsListener.participantlist);
			availableChatsPanel.add(chatButton);
			for (Integer i: newChatOptionsListener.participantlist) {
				multiCast.send("joinrequest:chat",i);
			}
		}
	}

	private class ChatChooseListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			chatRoom.remove(chatRoom.scrollmessages);
			chatRoom.scrollmessages = chatmap.get(e.getSource());
			chatRoom.add(chatRoom.scrollmessages, BorderLayout.CENTER);
			GUI.this.invalidate();
			GUI.this.validate();
			GUI.this.repaint();
			chatRoom.scrollmessages.invalidate();
			chatRoom.scrollmessages.validate();
			chatRoom.scrollmessages.repaint();
			chatRoom.invalidate();
			chatRoom.validate();
			chatRoom.repaint();
		}
	}

	private class SendingFieldListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			chatRoom.scrollmessages.messages.setText(chatRoom.scrollmessages.messages.getText() + "\n You: " + ((JTextField)e.getSource()).getText() + "\n");
			((JTextField)e.getSource()).setText("");
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

	public void printMessage(String message) {

	}

	public static void main(String[] args) {
		new GUI(4, new MultiCast2());
	}

}