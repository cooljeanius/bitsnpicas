package com.kreative.keyedit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import java.util.Scanner;
import com.kreative.unicode.data.NameResolver;

public class HTMLWriter {
	public static void write(File file, KeyboardMapping km) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
		write(pw, km);
		pw.flush();
		pw.close();
		fos.close();
		if (km.htmlSquareChars != null && !km.htmlSquareChars.isEmpty()) {
			File fontFile = new File(file.getParentFile(), "KreativeSquare.ttf");
			FileOutputStream fontOut = new FileOutputStream(fontFile);
			InputStream fontIn = HTMLWriter.class.getResourceAsStream("ksquare.ttf");
			int read; byte[] buf = new byte[65536];
			while ((read = fontIn.read(buf)) >= 0) fontOut.write(buf, 0, read);
			fontIn.close();
			fontOut.flush();
			fontOut.close();
		}
	}
	
	public static void write(PrintWriter out, KeyboardMapping km) {
		out.println("<!DOCTYPE HTML>");
		out.println("<html>");
		out.println("\t<head>");
		out.println("\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
		String t = (km.htmlTitle == null || km.htmlTitle.length() == 0) ? (km.getNameNotEmpty() + " Keyboard Layout") : km.htmlTitle;
		out.println("\t\t<title>" + HTMLWriterUtility.htmlSpecialChars(t) + "</title>");
		out.println("\t\t<style>");
		writeResource(out, "\t\t\t", "base.css", km);
		writeResource(out, "\t\t\t", "keyboard.css", km);
		if (km.htmlOutlineChars != null && !km.htmlOutlineChars.isEmpty()) writeResource(out, "\t\t\t", "outline.css", km);
		if (km.htmlSquareChars != null && !km.htmlSquareChars.isEmpty()) writeResource(out, "\t\t\t", "ksquare.css", km);
		writeInline(out, "\t\t\t", km.htmlStyle);
		out.println("\t\t</style>");
		out.println("\t</head>");
		out.println("\t<body class=\"center\">");
		String h1 = (km.htmlH1 == null || km.htmlH1.length() == 0) ? (km.getNameNotEmpty() + " Keyboard Layout") : km.htmlH1;
		String h2 = (km.htmlH2 == null || km.htmlH2.length() == 0) ? "for Mac OS X, Linux, and Windows" : km.htmlH2;
		out.println("\t\t<h1>" + HTMLWriterUtility.htmlSpecialChars(h1) + "</h1>");
		out.println("\t\t<h2>" + HTMLWriterUtility.htmlSpecialChars(h2) + "</h2>");
		writeInline(out, "\t\t", km.htmlBody1);
		out.println("\t\t<h3>The Layout</h3>");
		writeInline(out, "\t\t", km.htmlBody2);
		writeKeyboard(out, km);
		writeInline(out, "\t\t", km.htmlBody3);
		if (km.htmlInstall != null && km.htmlInstall.length() > 0) {
			writeInline(out, "\t\t", km.htmlInstall);
		} else {
			String rn = km.isWindowsNativeCompatible() ? "install.html" : "install-nonbmp.html";
			writeResource(out, "\t\t", rn, km);
		}
		writeInline(out, "\t\t", km.htmlBody4);
		out.println("\t\t<script>");
		writeResource(out, "\t\t\t", "prep.js", km);
		out.println("\t\t</script>");
		out.println("\t</body>");
		out.println("</html>");
	}
	
	public static void writeKeyboard(PrintWriter out, KeyboardMapping km) {
		out.println("\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"k\">");
		
		out.println("\t\t\t<tr>");
		writeRowTop(out, km, NUMROW);
		out.println("\t\t\t\t<td colspan=\"8\" class=\"t m\"></td>");
		out.println("\t\t\t</tr>");
		out.println("\t\t\t<tr>");
		writeRowBot(out, km, NUMROW);
		out.println("\t\t\t\t<td colspan=\"8\" class=\"b m\">");
		out.println("\t\t\t\t\t<span class=\"mac hidden\">delete</span>");
		out.println("\t\t\t\t\t<span class=\"linux win hidden\">backspace</span>");
		out.println("\t\t\t\t</td>");
		out.println("\t\t\t</tr>");
		
		out.println("\t\t\t<tr>");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"t m\"></td>");
		writeRowTop(out, km, TOPROW);
		out.println("\t\t\t</tr>");
		out.println("\t\t\t<tr>");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"b m\">tab</td>");
		writeRowBot(out, km, TOPROW);
		out.println("\t\t\t</tr>");
		
		out.println("\t\t\t<tr>");
		out.println("\t\t\t\t<td colspan=\"7\" class=\"t m\"></td>");
		writeRowTop(out, km, HOMEROW);
		out.println("\t\t\t\t<td colspan=\"9\" class=\"t m\"></td>");
		out.println("\t\t\t</tr>");
		out.println("\t\t\t<tr>");
		out.println("\t\t\t\t<td colspan=\"7\" class=\"b m\">caps lock</td>");
		writeRowBot(out, km, HOMEROW);
		out.println("\t\t\t\t<td colspan=\"9\" class=\"b m\">");
		out.println("\t\t\t\t\t<span class=\"mac hidden\">return</span>");
		out.println("\t\t\t\t\t<span class=\"linux win hidden\">enter</span>");
		out.println("\t\t\t\t</td>");
		out.println("\t\t\t</tr>");
		
		out.println("\t\t\t<tr>");
		out.println("\t\t\t\t<td colspan=\"9\" class=\"t m\"></td>");
		writeRowTop(out, km, BOTROW);
		out.println("\t\t\t\t<td colspan=\"11\" class=\"t m\"></td>");
		out.println("\t\t\t</tr>");
		out.println("\t\t\t<tr>");
		out.println("\t\t\t\t<td colspan=\"9\" class=\"b m\">shift</td>");
		writeRowBot(out, km, BOTROW);
		out.println("\t\t\t\t<td colspan=\"11\" class=\"b m\">shift</td>");
		out.println("\t\t\t</tr>");
		
		out.println("\t\t\t<tr class=\"mac hidden\">");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"t m\"></td>");
		out.println("\t\t\t\t<td colspan=\"5\" class=\"t o\"></td>");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"t m\"></td>");
		out.println(spaceTop(km, 26));
		out.println("\t\t\t\t<td colspan=\"6\" class=\"t m\"></td>");
		out.println("\t\t\t\t<td colspan=\"5\" class=\"t o\"></td>");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"t m\"></td>");
		out.println("\t\t\t</tr>");
		out.println("\t\t\t<tr class=\"mac hidden\">");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"b m\">control</td>");
		out.println("\t\t\t\t<td colspan=\"5\" class=\"b o\">option</td>");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"b m\">command</td>");
		out.println(spaceBot(km, 26));
		out.println("\t\t\t\t<td colspan=\"6\" class=\"b m\">command</td>");
		out.println("\t\t\t\t<td colspan=\"5\" class=\"b o\">option</td>");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"b m\">control</td>");
		out.println("\t\t\t</tr>");
		
		out.println("\t\t\t<tr class=\"linux hidden\">");
		out.println(linuxModTop(km, 6, XkbComposeKey.lctrl));
		out.println(linuxModTop(km, 5, XkbComposeKey.lwin, XkbAltGrKey.lwin_switch, XkbAltGrKey.win_switch));
		out.println(linuxModTop(km, 5, null,               XkbAltGrKey.lalt_switch, XkbAltGrKey.alt_switch));
		out.println(spaceTop(km, 23));
		out.println(linuxModTop(km, 5, XkbComposeKey.ralt, XkbAltGrKey.ralt_switch, XkbAltGrKey.alt_switch));
		out.println(linuxModTop(km, 5, XkbComposeKey.rwin, XkbAltGrKey.rwin_switch, XkbAltGrKey.win_switch));
		out.println(linuxModTop(km, 5, XkbComposeKey.menu, XkbAltGrKey.menu_switch));
		out.println(linuxModTop(km, 6, XkbComposeKey.rctrl));
		out.println("\t\t\t</tr>");
		out.println("\t\t\t<tr class=\"linux hidden\">");
		out.println(linuxModBot(km, 6, "ctrl", XkbComposeKey.lctrl));
		out.println(linuxModBot(km, 5, "❖",    XkbComposeKey.lwin, XkbAltGrKey.lwin_switch, XkbAltGrKey.win_switch));
		out.println(linuxModBot(km, 5, "alt",  null,               XkbAltGrKey.lalt_switch, XkbAltGrKey.alt_switch));
		out.println(spaceBot(km, 23));
		out.println(linuxModBot(km, 5, "alt",  XkbComposeKey.ralt, XkbAltGrKey.ralt_switch, XkbAltGrKey.alt_switch));
		out.println(linuxModBot(km, 5, "❖",    XkbComposeKey.rwin, XkbAltGrKey.rwin_switch, XkbAltGrKey.win_switch));
		out.println(linuxModBot(km, 5, "▤",    XkbComposeKey.menu, XkbAltGrKey.menu_switch));
		out.println(linuxModBot(km, 6, "ctrl", XkbComposeKey.rctrl));
		out.println("\t\t\t</tr>");
		
		out.println("\t\t\t<tr class=\"win hidden\">");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"t m\"></td>");
		out.println("\t\t\t\t<td colspan=\"5\" class=\"t m\"></td>");
		out.println("\t\t\t\t<td colspan=\"5\" class=\"t m\"></td>");
		out.println(spaceTop(km, 23));
		out.println(winModTop(km));
		out.println("\t\t\t\t<td colspan=\"5\" class=\"t m\"></td>");
		out.println("\t\t\t\t<td colspan=\"5\" class=\"t m\"></td>");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"t m\"></td>");
		out.println("\t\t\t</tr>");
		out.println("\t\t\t<tr class=\"win hidden\">");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"b m\">ctrl</td>");
		out.println("\t\t\t\t<td colspan=\"5\" class=\"b m\">❖</td>");
		out.println("\t\t\t\t<td colspan=\"5\" class=\"b m\">alt</td>");
		out.println(spaceBot(km, 23));
		out.println(winModBot(km));
		out.println("\t\t\t\t<td colspan=\"5\" class=\"b m\">❖</td>");
		out.println("\t\t\t\t<td colspan=\"5\" class=\"b m\">▤</td>");
		out.println("\t\t\t\t<td colspan=\"6\" class=\"b m\">ctrl</td>");
		out.println("\t\t\t</tr>");
		
		out.println("\t\t</table>");
	}
	
	private static void writeInline(PrintWriter out, String prefix, String content) {
		if (content != null) {
			for (String line : content.split("\r\n|\r|\n")) {
				out.println(prefix + line);
			}
		}
	}
	
	private static void writeResource(PrintWriter out, String prefix, String name, KeyboardMapping km) {
		Scanner scan = HTMLWriterUtility.getTemplate(name);
		while (scan.hasNextLine()) out.println(prefix + HTMLWriterUtility.replaceFields(scan.nextLine(), km));
		scan.close();
	}
	
	private static void writeRowTop(PrintWriter out, KeyboardMapping km, Key... keys) {
		for (Key key : keys) {
			KeyMapping m = km.map.get(key);
			String l = olcpString(km, m.shiftedOutput,    m.shiftedDeadKey   );
			String r = olcpString(km, m.altShiftedOutput, m.altShiftedDeadKey);
			String lc = classString(km, m.shiftedOutput,    m.shiftedDeadKey,    "tl");
			String rc = classString(km, m.altShiftedOutput, m.altShiftedDeadKey, "tr");
			String span = "\"" + ((key == Key.BACKSLASH) ? 3 : 2) + "\"";
			out.println("\t\t\t\t<td colspan=" + span + " class=" + lc + ">" + l + "</td>");
			out.println("\t\t\t\t<td colspan=" + span + " class=" + rc + ">" + r + "</td>");
		}
	}
	
	private static void writeRowBot(PrintWriter out, KeyboardMapping km, Key... keys) {
		for (Key key : keys) {
			KeyMapping m = km.map.get(key);
			String l = olcpString(km, m.unshiftedOutput,    m.unshiftedDeadKey   );
			String r = olcpString(km, m.altUnshiftedOutput, m.altUnshiftedDeadKey);
			String lc = classString(km, m.unshiftedOutput,    m.unshiftedDeadKey,    "bl");
			String rc = classString(km, m.altUnshiftedOutput, m.altUnshiftedDeadKey, "br");
			String span = "\"" + ((key == Key.BACKSLASH) ? 3 : 2) + "\"";
			out.println("\t\t\t\t<td colspan=" + span + " class=" + lc + ">" + l + "</td>");
			out.println("\t\t\t\t<td colspan=" + span + " class=" + rc + ">" + r + "</td>");
		}
	}
	
	private static String olcpString(KeyboardMapping km, int output, DeadKeyTable dead) {
		String s = cpString(output, dead, km.htmlCpLabels);
		if (dead != null) {
			if      (dead.macTerminator > 0) output = dead.macTerminator;
			else if (dead.winTerminator > 0) output = dead.winTerminator;
			else if (dead.xkbOutput     > 0) output = dead.xkbOutput;
		}
		if (output > 0) {
			ArrayList<String> classes = new ArrayList<String>();
			if (km.htmlOutlineChars != null && km.htmlOutlineChars.get(output)) {
				classes.add("ol");
			}
			if (km.htmlSpanClasses != null) {
				for (Map.Entry<String,BitSet> e : km.htmlSpanClasses.entrySet()) {
					if (e.getValue() != null && e.getValue().get(output)) {
						classes.add(e.getKey());
					}
				}
			}
			if (!classes.isEmpty()) {
				StringBuffer sb = new StringBuffer();
				sb.append("<span class=\"");
				boolean first = true;
				for (String className : classes) {
					if (first) first = false;
					else sb.append(" ");
					sb.append(className);
				}
				sb.append("\">");
				sb.append(s);
				sb.append("</span>");
				s = sb.toString();
			}
		}
		return s;
	}
	
	private static String classString(KeyboardMapping km, int output, DeadKeyTable dead, String base) {
		StringBuffer sb = new StringBuffer("\"");
		sb.append(base);
		if (dead != null) {
			sb.append(" d");
			if      (dead.macTerminator > 0) output = dead.macTerminator;
			else if (dead.winTerminator > 0) output = dead.winTerminator;
			else if (dead.xkbOutput     > 0) output = dead.xkbOutput;
		}
		if (output > 0) {
			if (km.htmlSquareChars != null && km.htmlSquareChars.get(output)) {
				sb.append(" ksq");
			}
			if (km.htmlTdClasses != null) {
				for (Map.Entry<String,BitSet> e : km.htmlTdClasses.entrySet()) {
					if (e.getValue() != null && e.getValue().get(output)) {
						sb.append(" ");
						sb.append(e.getKey());
					}
				}
			}
		}
		sb.append("\"");
		return sb.toString();
	}
	
	private static String spaceTop(KeyboardMapping km, int span) {
		KeyMapping m = km.map.get(Key.SPACE);
		String bu = cpString(m.unshiftedOutput, m.unshiftedDeadKey, km.htmlCpLabels);
		String bs = cpString(m.shiftedOutput, m.shiftedDeadKey, km.htmlCpLabels);
		String b = bu.equals(bs) ? bu : (bu + " / " + bs);
		String tu = cpString(m.altUnshiftedOutput, m.altUnshiftedDeadKey, km.htmlCpLabels);
		String ts = cpString(m.altShiftedOutput, m.altShiftedDeadKey, km.htmlCpLabels);
		String t = tu.equals(ts) ? tu : (tu + " / " + ts);
		if (t.equals(b)) {
			String c = (m.unshiftedDeadKey == null && m.shiftedDeadKey == null) ? "\"t\"" : "\"t d\"";
			return "\t\t\t\t<td colspan=\"" + span + "\" class=" + c + "></td>";
		} else {
			String c = (m.altUnshiftedDeadKey == null && m.altShiftedDeadKey == null) ? "\"t\"" : "\"t d\"";
			return "\t\t\t\t<td colspan=\"" + span + "\" class=" + c + ">" + t + "</td>";
		}
	}
	
	private static String spaceBot(KeyboardMapping km, int span) {
		KeyMapping m = km.map.get(Key.SPACE);
		String bu = cpString(m.unshiftedOutput, m.unshiftedDeadKey, km.htmlCpLabels);
		String bs = cpString(m.shiftedOutput, m.shiftedDeadKey, km.htmlCpLabels);
		String b = bu.equals(bs) ? bu : (bu + " / " + bs);
		String c = (m.unshiftedDeadKey == null && m.shiftedDeadKey == null) ? "\"b\"" : "\"b d\"";
		return "\t\t\t\t<td colspan=\"" + span + "\" class=" + c + ">" + b + "</td>";
	}
	
	private static String winModTop(KeyboardMapping km) {
		String c = km.winAltGrEnable ? "\"t o\"" : "\"t m\"";
		return "\t\t\t\t<td colspan=\"5\" class=" + c + "></td>";
	}
	
	private static String winModBot(KeyboardMapping km) {
		String c = km.winAltGrEnable ? "\"b o\"" : "\"b m\"";
		String label = km.winAltGrEnable ? "alt gr" : "alt";
		return "\t\t\t\t<td colspan=\"5\" class=" + c + ">" + label + "</td>";
	}
	
	private static String linuxModTop(KeyboardMapping km, int span, XkbComposeKey compose, XkbAltGrKey... altgr) {
		boolean isCompose = (compose != null && km.xkbComposeKey == compose);
		boolean isAltGr = false; for (XkbAltGrKey a : altgr) if (km.xkbAltGrKey == a) isAltGr = true;
		String c = (isAltGr || isCompose) ? "\"t o\"" : "\"t m\"";
		return "\t\t\t\t<td colspan=\"" + span + "\" class=" + c + "></td>";
	}
	
	private static String linuxModBot(KeyboardMapping km, int span, String label, XkbComposeKey compose, XkbAltGrKey... altgr) {
		boolean isCompose = (compose != null && km.xkbComposeKey == compose);
		boolean isAltGr = false; for (XkbAltGrKey a : altgr) if (km.xkbAltGrKey == a) isAltGr = true;
		if (isCompose) label = "compose"; else if (isAltGr) label = "alt gr";
		String c = (isAltGr || isCompose) ? "\"b o\"" : "\"b m\"";
		return "\t\t\t\t<td colspan=\"" + span + "\" class=" + c + ">" + label + "</td>";
	}
	
	private static String cpString(int output, DeadKeyTable dead, Map<Integer,String> cpLabels) {
		if (dead != null) {
			if      (dead.macTerminator > 0) output = dead.macTerminator;
			else if (dead.winTerminator > 0) output = dead.winTerminator;
			else if (dead.xkbOutput     > 0) output = dead.xkbOutput;
		}
		if (output <= 0) return "";
		if (cpLabels.containsKey(output)) return cpLabels.get(output);
		if (output >= 0xFE00 && output <= 0xFE0F) return "vs" + (output - 0xFE00 + 1);
		if (output >= 0xE0100 && output <= 0xE01EF) return "vs" + (output - 0xE0100 + 17);
		switch (output) {
			case 0x00: return "nul";
			case 0x01: return "soh";
			case 0x02: return "stx";
			case 0x03: return "etx";
			case 0x04: return "eot";
			case 0x05: return "enq";
			case 0x06: return "ack";
			case 0x07: return "bel";
			case 0x08: return "bs";
			case 0x09: return "ht";
			case 0x0A: return "lf";
			case 0x0B: return "vt";
			case 0x0C: return "ff";
			case 0x0D: return "cr";
			case 0x0E: return "so";
			case 0x0F: return "si";
			case 0x10: return "dle";
			case 0x11: return "dc1";
			case 0x12: return "dc2";
			case 0x13: return "dc3";
			case 0x14: return "dc4";
			case 0x15: return "nak";
			case 0x16: return "syn";
			case 0x17: return "etb";
			case 0x18: return "can";
			case 0x19: return "em";
			case 0x1A: return "sub";
			case 0x1B: return "esc";
			case 0x1C: return "fs";
			case 0x1D: return "gs";
			case 0x1E: return "rs";
			case 0x1F: return "us";
			case 0x20: return "space";
			case 0x26: return "&amp;";
			case 0x3C: return "&lt;";
			case 0x3E: return "&gt;";
			case 0x7F: return "del";
			case 0x80: return "pad";
			case 0x81: return "hop";
			case 0x82: return "bph";
			case 0x83: return "nbh";
			case 0x84: return "ind";
			case 0x85: return "nel";
			case 0x86: return "ssa";
			case 0x87: return "esa";
			case 0x88: return "hts";
			case 0x89: return "htj";
			case 0x8A: return "vts";
			case 0x8B: return "pld";
			case 0x8C: return "plu";
			case 0x8D: return "ri";
			case 0x8E: return "ss2";
			case 0x8F: return "ss3";
			case 0x90: return "dcs";
			case 0x91: return "pu1";
			case 0x92: return "pu2";
			case 0x93: return "sts";
			case 0x94: return "cch";
			case 0x95: return "mw";
			case 0x96: return "spa";
			case 0x97: return "epa";
			case 0x98: return "sos";
			case 0x99: return "sgc";
			case 0x9A: return "sci";
			case 0x9B: return "csi";
			case 0x9C: return "st";
			case 0x9D: return "osc";
			case 0x9E: return "pm";
			case 0x9F: return "apc";
			case 0xA0: return "nbsp";
			case 0xAD: return "-";
			case 0x02DE: return "◌˞";
			case 0x034F: return "cgj";
			case 0x061C: return "alm";
			case 0x070F: return "sam";
			case 0x115F: return "hcf";
			case 0x1160: return "hjf";
			case 0x17B4: return "kivaq";
			case 0x17B5: return "kivaa";
			case 0x180B: return "fvs1";
			case 0x180C: return "fvs2";
			case 0x180D: return "fvs3";
			case 0x180E: return "mvs";
			case 0x180F: return "fvs4";
			case 0x2000: return "nqsp";
			case 0x2001: return "mqsp";
			case 0x2002: return "ensp";
			case 0x2003: return "emsp";
			case 0x2004: return "3/msp";
			case 0x2005: return "4/msp";
			case 0x2006: return "6/msp";
			case 0x2007: return "fsp";
			case 0x2008: return "psp";
			case 0x2009: return "thsp";
			case 0x200A: return "hsp";
			case 0x200B: return "zwsp";
			case 0x200C: return "zwnj";
			case 0x200D: return "zwj";
			case 0x200E: return "lrm";
			case 0x200F: return "rlm";
			case 0x2028: return "lsep";
			case 0x2029: return "psep";
			case 0x202A: return "lre";
			case 0x202B: return "rle";
			case 0x202C: return "pdf";
			case 0x202D: return "lro";
			case 0x202E: return "rlo";
			case 0x202F: return "nnbsp";
			case 0x205F: return "mmsp";
			case 0x2060: return "wj";
			case 0x2066: return "lri";
			case 0x2067: return "rli";
			case 0x2068: return "fsi";
			case 0x2069: return "pdi";
			case 0x206A: return "iss";
			case 0x206B: return "ass";
			case 0x206C: return "iafs";
			case 0x206D: return "aafs";
			case 0x206E: return "nads";
			case 0x206F: return "nods";
			case 0x3000: return "idsp";
			case 0x3164: return "hf";
			case 0xFEFF: return "zwnbsp";
			case 0xFFA0: return "hwhf";
			case 0xFFF0: return "cd";
			case 0xFFF1: return "rd";
			case 0xFFF2: return "pd";
			case 0xFFF3: return "sd";
			case 0xFFF9: return "iaa";
			case 0xFFFA: return "ias";
			case 0xFFFB: return "iat";
			case 0xFFFC: return "obj";
			case 0x1107F: return "bnj";
			case 0x16FE4: return "kssf";
			case 0x1BC9D: return "dtls";
			case 0x1D159: return "msnn";
			case 0x1D173: return "msbb";
			case 0x1D174: return "mseb";
			case 0x1D175: return "msbt";
			case 0x1D176: return "mset";
			case 0x1D177: return "msbs";
			case 0x1D178: return "mses";
			case 0x1D179: return "msbp";
			case 0x1D17A: return "msep";
			case 0x1DA9B: return "swf2";
			case 0x1DA9C: return "swf3";
			case 0x1DA9D: return "swf4";
			case 0x1DA9E: return "swf5";
			case 0x1DA9F: return "swf6";
			case 0x1DAA1: return "swr2";
			case 0x1DAA2: return "swr3";
			case 0x1DAA3: return "swr4";
			case 0x1DAA4: return "swr5";
			case 0x1DAA5: return "swr6";
			case 0x1DAA6: return "swr7";
			case 0x1DAA7: return "swr8";
			case 0x1DAA8: return "swr9";
			case 0x1DAA9: return "swr10";
			case 0x1DAAA: return "swr11";
			case 0x1DAAB: return "swr12";
			case 0x1DAAC: return "swr13";
			case 0x1DAAD: return "swr14";
			case 0x1DAAE: return "swr15";
			case 0x1DAAF: return "swr16";
		}
		String s = String.valueOf(Character.toChars(output));
		NameResolver r = NameResolver.instance(output);
		if (r.getCategory(output).startsWith("M")) {
			// Combining Mark
			s = "◌" + s;
			String ccc = r.getCombiningClass(output);
			if (ccc.equals("233") || ccc.equals("234")) {
				// Double Combining Mark
				s = s + "◌";
			}
		}
		return s;
	}
	
	private static final Key[] NUMROW = {
		Key.GRAVE_TILDE, Key.NUMROW_1, Key.NUMROW_2, Key.NUMROW_3, Key.NUMROW_4,
		Key.NUMROW_5, Key.NUMROW_6, Key.NUMROW_7, Key.NUMROW_8, Key.NUMROW_9,
		Key.NUMROW_0, Key.HYPHEN_UNDERSCORE, Key.EQUALS_PLUS
	};
	
	private static final Key[] TOPROW = {
		Key.Q, Key.W, Key.E, Key.R, Key.T, Key.Y, Key.U, Key.I, Key.O, Key.P,
		Key.LEFT_BRACKET, Key.RIGHT_BRACKET, Key.BACKSLASH
	};
	
	private static final Key[] HOMEROW = {
		Key.A, Key.S, Key.D, Key.F, Key.G, Key.H, Key.J, Key.K, Key.L,
		Key.SEMICOLON, Key.QUOTE
	};
	
	private static final Key[] BOTROW = {
		Key.Z, Key.X, Key.C, Key.V, Key.B, Key.N, Key.M,
		Key.COMMA, Key.PERIOD, Key.SLASH
	};
}
