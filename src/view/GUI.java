package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Scanner;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import controller.*;
import java.util.List;

/**
 * Created by Eric on 6-4-2016.
 */
public class GUI extends JFrame {

    private int pcnumber;
    private Scanner scanner;
    private JPanel chats;
    private Map<ChatButton,ChatWindow> chatmap = new LinkedHashMap<ChatButton,ChatWindow>();
    private Map<Integer, ChatButton> chatnumbers = new HashMap<Integer, ChatButton>();
    private JPanel chatLobbyPanel = null;
    private ChatWindow currentwindow = null;
    private Dimension chatwindowsize = new Dimension(200,400);
    private Dimension textfieldsize = new Dimension(150, 20);
    private Dimension textareasize = new Dimension(190, 315);
    private Dimension buttonsize = new Dimension(200, 40);
    private Dimension group22size = new Dimension(100,400);
    private Dimension fillupspace = new Dimension(5, 20);
    private Dimension framesize = new Dimension(500,400);
    private final int rens = 1;
    private final int birte = 2;
    private final int coen = 3;
    private final int eric = 4;
    private JCheckBox rensbox;
    private JCheckBox ericbox;
    private JCheckBox coenbox;
    private JCheckBox birtebox;
    private int groupnumber;
    private MultiCast2 multiCast;

    public GUI(int pcnumber, MultiCast2 multiCast) {

		/*
		Create frame.
		 */
        super("Chatlobby");
        this.pcnumber = pcnumber;
        groupnumber = pcnumber;
        this.multiCast = multiCast;
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
		Make a new JPanel, representing the available chats, with a 'new chat' button to begin new chats.
		 */
        chats = new JPanel();
        chats.setLayout(new BoxLayout(chats, BoxLayout.Y_AXIS));
        chats.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Chats"));
        JButton newchat = new JButton("New Chat");
        newchat.addActionListener(new NewChatButton());
        chats.add(newchat);
        chatLobbyPanel.add(chats, BorderLayout.CENTER);
        setResizable(false);
    }

    /*
    Prompts the user with a dialog window with a text field, the input the user then gives is returned.
     */
    public String askForInput(String question) {
        return JOptionPane.showInputDialog(question);
    }

    /*
    Prompts the user with a dialog window with an error message.
     */
    public void showError(String s) {
        JOptionPane.showMessageDialog(this, "Error: " + s, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    /*
    Auxiliary method to convert an int array to a string, so that the participants of a ChatWindow can be sent in a proper format.
     */
    public String intArrayToString(int[] array) {
        String string = "";
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0) {
                string.concat((char)array[i] + ",");
            }
        }
        return string;
    }

    /*
	ActionListener class to implement the chat button functionality.
	 */
    private class ChatChooser implements ActionListener {

        public void actionPerformed(ActionEvent a) {
            ((JButton) a.getSource()).setBackground(Color.LIGHT_GRAY);
            for (Map.Entry<ChatButton, ChatWindow> e: chatmap.entrySet()) {
                if (!a.getSource().equals(e.getKey())) {
                    e.getKey().setBackground(Color.WHITE);
                }
            }
            if (currentwindow != null) {
                chatLobbyPanel.remove(currentwindow);
            }
            currentwindow = chatmap.get(a.getSource());
            chatLobbyPanel.add(currentwindow, BorderLayout.EAST);
            GUI.this.validate();
            GUI.this.repaint();
        }
    }

    /*
    ActionListener class to implement the 'new chat' button functionality
     */
    private class NewChatButton implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            JPanel newchatoptions = new JPanel();
            rensbox = new JCheckBox("Rens");
            NameSelecting namelistener = new NameSelecting();
            rensbox.addItemListener(namelistener);
            birtebox = new JCheckBox("Birte");
            birtebox.addItemListener(namelistener);
            coenbox = new JCheckBox("Coen");
            coenbox.addItemListener(namelistener);
            ericbox = new JCheckBox("Eric");
            ericbox.addItemListener(namelistener);
            if (pcnumber != 1) {
                newchatoptions.add(rensbox);
            }
            if (pcnumber != 2) {
                newchatoptions.add(birtebox);
            }
            if (pcnumber != 3) {
                newchatoptions.add(coenbox);
            }
            if (pcnumber != 4) {
                newchatoptions.add(ericbox);
            }
            JOptionPane.showMessageDialog(GUI.this, newchatoptions, "New Chat Options", JOptionPane.PLAIN_MESSAGE);
            ChatButton chat = new ChatButton(Integer.toString(groupnumber));
            chats.add(chat);
            int[] participants = new int[4];
            if (namelistener.rensselected) {
                participants[0] = 1;
            }
            if (namelistener.birteselected) {
                participants[1] = 2;
            }
            if (namelistener.coenselected) {
                participants[2] = 3;
            }
            if (namelistener.ericselected) {
                participants[3] = 4;
            }
            participants[pcnumber-1] = pcnumber;
            ChatWindow chatwindow = new ChatWindow(participants);
            namelistener.rensselected = false;
            namelistener.birteselected = false;
            namelistener.coenselected = false;
            namelistener.ericselected = false;
            chatmap.put(chat, chatwindow);
            chatLobbyPanel.add(chatwindow, BorderLayout.EAST);
            GUI.this.validate();
            GUI.this.repaint();
            for (int i = 0; i < participants.length; i++) {
                if (participants[i] != 0) {
                    multiCast.send("joinrequest:chat" + groupnumber + ";" + intArrayToString(participants),participants[i]);
                }
            }
            chatnumbers.put(new Integer(groupnumber), chat);
            groupnumber += 4;
        }

    }

    /*
    ItemListener class to make it more easy to listen to a collection of checkboxes (used in selecting the right participants for a new chat).
     */
    private class NameSelecting implements ItemListener {

        public boolean rensselected = false;
        public boolean birteselected = false;
        public boolean coenselected = false;
        public boolean ericselected = false;

        public void itemStateChanged(ItemEvent e) {
            if (e.getItemSelectable().equals(rensbox)) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    rensselected = true;
                }
                else {
                    rensselected = false;
                }
            }
            if (e.getItemSelectable().equals((birtebox))) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    birteselected = true;
                }
                else {
                    birteselected = false;
                }
            }
            if (e.getItemSelectable().equals(coenbox)) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    coenselected = true;
                }
                else {
                    coenselected = false;
                }
            }
            if (e.getItemSelectable().equals(ericbox)) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ericselected = true;
                }
                else {
                    ericselected = false;
                }
            }
        }
    }

    /*
       ActionListener class to implement the text field functionality of the chatwindow.
     */
    private class SendingField implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            ChatWindow chatwindow = (ChatWindow)((JComponent)e.getSource()).getParent();
            JTextArea textarea = chatwindow.messages;
            JTextField textfield = (JTextField)e.getSource();
            textarea.setText(textarea.getText() + "\n \n " + "You: " + textfield.getText() );
            textfield.setText("");
            ChatButton chatbutton = null;
            for (Map.Entry<ChatButton, ChatWindow> s: chatmap.entrySet()) {
                if (s.getValue().equals(chatwindow)) {
                    chatbutton = s.getKey();
                }
            }
            for (int i = 0; i < chatwindow.participants.length; i++) {
                multiCast.send("chat" + chatbutton.name + ":" + textfield.getText(), chatwindow.participants[i]);
            }
        }
    }

    /*
    Customized JButton class, used for representing different chats.
     */
    private class ChatButton extends JButton {

        public String name;

        public ChatButton(String name) {
            super(name);
            this.name = name;
            setMinimumSize(buttonsize);
            setMaximumSize(buttonsize);
            setBackground(Color.WHITE);
            addActionListener(new ChatChooser());
        }
    }

    /*
    JPanel extension, representing a chat window, therefore containing a TextArea where the messages are displayed and a TextField where new messages can be typed.
     */
    private class ChatWindow extends JPanel {

        public JTextField textfield;
        public JTextArea messages;
        public int[] participants;

        public ChatWindow(int[] participants) {
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

    /*
    This method is called when a message is received, and placed in the TextArea of the right chat.
     */
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
        if (message.contains("joinrequest:chat")) {
            char chatnumber = message.charAt(16);
            JOptionPane.showConfirmDialog(GUI.this, name + " added you to chat " + chatnumber, "New Chat", JOptionPane.OK_OPTION);
            ChatButton chatbutton = new ChatButton(Integer.toString(chatnumber));
            chats.add(chatbutton);
            chatnumbers.put(new Integer(chatnumber), chatbutton);
            int[] participants = new int[4];
            String participantsstr = message.substring(17);
            if (participantsstr.charAt(0) == 1) {
                participants[0] = 1;
            }
            if (participantsstr.charAt(1) == 2) {
                participants[1] = 2;
            }
            if (participantsstr.charAt(0) == 3) {
                participants[2] = 1;
            }
            if (participantsstr.charAt(0) == 4) {
                participants[3] = 4;
            }
            ChatWindow chatwindow = new ChatWindow(participants);
            chatmap.put(chatbutton,chatwindow);
            chatLobbyPanel.add(chatwindow, BorderLayout.EAST);
            GUI.this.validate();
            GUI.this.repaint();
        }
        if (message.startsWith("chat")) {
            String msg = message.substring(6);
            ChatWindow chatwindow = chatmap.get(chatnumbers.get(new Integer((int)message.charAt(4))));
            chatwindow.messages.setText(chatwindow.messages.getText() + "\n" + name + ": " + msg + "\n");
        }
    }

    public static void main(String[] args) {
        new GUI(4, new MultiCast2());
    }

}