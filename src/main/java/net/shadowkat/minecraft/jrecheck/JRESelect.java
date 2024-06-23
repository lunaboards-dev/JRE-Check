package net.shadowkat.minecraft.jrecheck;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class JRESelect extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JScrollPane scrollPane;
    private JPanel scrollPanel;
    private List<JREInstance> jres;
    private ButtonGroup group;
    private HashMap<JRadioButton, String> button_vals;
    public String value;

    public JRESelect(List<JREInstance> j) {
        jres = j;
        button_vals = new HashMap<>();
        group = new ButtonGroup();
        setTitle("Select Java Version");
        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.setOpaque(true);
        // JRE Instances
        scrollPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        //scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.PAGE_AXIS));
        scrollPanel.setLayout(new GridLayout(0, 1));
        scrollPane = new JScrollPane(scrollPanel);
        //scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        try {
            String[] jinfo = Utils.javaInfo("java");
            addJREInstance(String.format("System Default - %s (%s)", jinfo[0], jinfo[1]), "java");
        } catch (IOException e) {
            // No system Java?
        }
        for (JREInstance inst : jres) {
            addJREInstance(String.format("%s %s (%s)", inst.name, inst.version_full, inst.arch), inst.path.getPath()+"/bin/java");
        }
        //addJREInstance("System Java", "java");
        //addJREInstance("Kill", "lol");
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        //scrollPanel.setMaximumSize(new Dimension(300, 200));
        contentPane.add(scrollPane);

        // Buttons
        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttonOK = new JButton("OK");
        buttonOK.addActionListener(actionEvent -> clickOK());
        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(actionEvent -> clickCancel());
        buttonPane.add(buttonOK);
        buttonPane.add(buttonCancel);
        buttonOK.setEnabled(false);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        // Display
        setContentPane(contentPane);
        pack();
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                clickCancel();
            }
        });
        setModalityType(ModalityType.APPLICATION_MODAL);
        setVisible(true);
    }

    private void clickOK() {
        button_vals.forEach((but, str) -> {
            if (but.isSelected()) {
                value = str;
            }
        });
        dispose();
    }

    private void clickCancel() {
        dispose();
    }

    private void addJREInstance(String name, String cmd) {
        JRadioButton but = new JRadioButton(name);
        but.addActionListener(actionEvent -> buttonOK.setEnabled(true));
        button_vals.put(but, cmd);
        group.add(but);
        scrollPanel.add(but);
    }

}
