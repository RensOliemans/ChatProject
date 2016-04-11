package view;

import controller.MultiCast;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Eric on 6-4-2016.
 */
public class GUI extends JFrame {

    private JPanel peopleOnline;
    private JPanel chatsOverview;
    private JPanel availableChatsPanel;
    private JPanel newChatPanel;
    private JPanel chatRoom;
    private JPanel sendingFieldPanel;
    private int pcnumber;
    private MultiCast multiCast;
    private Dimension framesize = new Dimension(500,400);

    public GUI(int pcnumber, MultiCast multiCast) {
        super("ChatUI");
        this.pcnumber = pcnumber;
        this.multiCast = multiCast;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        setPreferredSize(framesize);
        setMinimumSize(framesize);
        setMaximumSize(framesize);
//        peopleOnline = new JPanel();
//        peopleOnline.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "People Online"));
//        add(peopleOnline);
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        setResizable(false);
    }

    private class ChatsOverview extends JPanel {

        public ChatsOverview() {
            super();
        }
    }

    private class ChatRoom extends JPanel {

        public ChatRoom() {
            super();
        }
    }



    public static void main(String[] args) {
        new GUI(4, new MultiCast());
    }

}