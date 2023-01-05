package com.kreative.bitsnpicas.main;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import com.kreative.bitsnpicas.*;

public class ViewFont extends JFrame {
	private static final long serialVersionUID = 1;
	
	public static void main(String[] args) {
		BitmapInputOptions o = new BitmapInputOptions();
		for (String arg : args) {
			try {
				File file = new File(arg);
				BitmapInputFormat format = BitmapInputFormat.forFile(file);
				if (format == null) {
					System.err.println("Unknown type: " + arg);
				} else {
					if (format.macResFork) {
						file = new File(new File(file, "..namedfork"), "rsrc");
					}
					BitmapFont[] fonts = format.createImporter(o).importFont(file);
					if (fonts == null || fonts.length == 0) {
						System.err.println("No fonts found: " + arg);
					} else {
						for (BitmapFont font : fonts) new ViewFont(font);
					}
				}
			} catch (IOException e) {
				System.err.println("Could not load " + arg);
			}
		}
	}
	
	private final BitmapFont myFont;
	private final SpinnerNumberModel scale;
	private final JComponent alphaPanel;
	private final JTextArea textArea;
	private final JComponent textPanel;
	
	public ViewFont(BitmapFont bm) {
		myFont = bm;
		if (bm.getName(BitmapFont.NAME_FAMILY_AND_STYLE) != null) {
			setTitle(bm.getName(BitmapFont.NAME_FAMILY_AND_STYLE));
		}
		else if (bm.getName(BitmapFont.NAME_FAMILY) != null) {
			setTitle(bm.getName(BitmapFont.NAME_FAMILY));
		}
		
		scale = new SpinnerNumberModel(1, 1, 16, 1);
		scale.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				alphaPanel.repaint();
				textPanel.repaint();
			}
		});
		
		alphaPanel = new JComponent() {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				Insets i = getInsets();
				int x = i.left;
				int y = i.top;
				int w = getWidth()-i.left-i.right;
				int h = getHeight()-i.top-i.bottom;
				int s = scale.getNumber().intValue();
				g.setColor(Color.white);
				g.fillRect(x, y, w, h);
				g.setColor(Color.black);
				myFont.drawAlphabet(g, x, y + myFont.getLineAscent() * s, s, w);
			}
		};
		
		textArea = new JTextArea("The quick brown fox jumped over the lazy dogs with a razorback-jumping frog that could level six piqued gymnasts who speak Latin: Lorem ipsum dolor sit amet...");
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				textPanel.repaint();
			}
			public void insertUpdate(DocumentEvent e) {
				textPanel.repaint();
			}
			public void removeUpdate(DocumentEvent e) {
				textPanel.repaint();
			}
		});
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		textPanel = new JComponent() {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				Insets i = getInsets();
				int x = i.left;
				int y = i.top;
				int w = getWidth()-i.left-i.right;
				int h = getHeight()-i.top-i.bottom;
				int s = scale.getNumber().intValue();
				g.setColor(Color.white);
				g.fillRect(x, y, w, h);
				g.setColor(Color.black);
				myFont.draw(g, textArea.getText(), x, y + myFont.getLineAscent() * s, s, w);
			}
		};
		
		JPanel top = new JPanel(new BorderLayout(4,4));
		top.add(new JLabel("Scale:"), BorderLayout.LINE_START);
		top.add(new JSpinner(scale), BorderLayout.CENTER);
		
		JPanel top2 = new JPanel(new BorderLayout());
		top2.add(top, BorderLayout.LINE_END);
		
		JPanel main = new JPanel(new GridLayout(3,1,10,10));
		main.add(alphaPanel);
		main.add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		main.add(textPanel);
		
		JPanel main2 = new JPanel(new BorderLayout(10,10));
		main2.add(main, BorderLayout.CENTER);
		main2.add(top2, BorderLayout.PAGE_END);
		main2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(main2);
		
		setSize(512, 456);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void fontChanged() {
		alphaPanel.repaint();
		textPanel.repaint();
	}
}
