package com.kreative.bitsnpicas.edit;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontExporter;

public class SaveManager extends WindowAdapter implements SaveInterface {
	private JFrame frame;
	private File file;
	private FontExporter<?> format;
	private SaveRoutine routine;
	private Font<?> font;
	private boolean changed;
	
	public SaveManager(JFrame frame, File file, FontExporter<?> format, Font<?> font) {
		this.frame = frame;
		this.file = file;
		this.format = format;
		this.routine = null;
		this.font = font;
		this.changed = false;
		updateWindow();
	}
	
	public SaveManager(JFrame frame, SaveRoutine routine, Font<?> font) {
		this.frame = frame;
		this.file = null;
		this.format = null;
		this.routine = routine;
		this.font = font;
		this.changed = false;
		updateWindow();
	}
	
	public void setChanged() {
		this.changed = true;
		updateWindow();
	}
	
	public boolean save() {
		if (routine == null) {
			if (file == null || format == null) return saveAs();
			boolean succeeded = Main.saveFont(file, format, font);
			if (succeeded) changed = false;
			updateWindow();
			return succeeded;
		} else {
			boolean succeeded = routine.save(font);
			if (succeeded) changed = false;
			updateWindow();
			return succeeded;
		}
	}
	
	public boolean saveAs() {
		FontExporter<?> newFormat = Main.getSaveFormat(font);
		if (newFormat == null) return false;
		String newSuffix = Main.getSaveSuffix(font);
		if (newSuffix == null) return false;
		File newFile = Main.getSaveFile(newSuffix);
		if (newFile == null) return false;
		file = newFile;
		format = newFormat;
		boolean succeeded = Main.saveFont(file, format, font);
		if (succeeded) changed = false;
		updateWindow();
		return succeeded;
	}
	
	public void windowClosing(WindowEvent e) {
		Window w = e.getWindow();
		if (!changed || (file == null && font.isEmpty())) w.dispose();
		else switch (new SaveChangesDialog(w, font.toString()).showDialog()) {
			case SAVE: if (save()) w.dispose(); break;
			case DONT_SAVE: w.dispose(); break;
			case CANCEL: break;
		}
	}
	
	private void updateWindow() {
		String title = font.toString();
		if (file != null) title += " from " + file.getName();
		if (CommonMenuItems.IS_MAC_OS) {
			frame.getRootPane().putClientProperty("Window.documentFile", file);
			frame.getRootPane().putClientProperty("Window.documentModified", changed);
			frame.setTitle(title);
		} else {
			if (changed) title += " \u2022";
			frame.setTitle(title);
		}
	}
}
