package com.github.tgstation.fastdmm.objtree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.regex.*;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.tgstation.fastdmm.FastDMM;
import com.github.tgstation.fastdmm.Util;
import com.github.tgstation.fastdmm.dmirender.DMI;
import com.github.tgstation.fastdmm.dmirender.IconState;
import com.github.tgstation.fastdmm.dmmmap.DMM;
import com.github.tgstation.fastdmm.editing.ui.ModifiedTypeRenderer;
import com.github.tgstation.fastdmm.editing.ui.ModifiedTypeTableModel;
import com.github.tgstation.fastdmm.editing.ui.IconStateListRenderer;


public class ModifiedType extends ObjInstance {
	public ModifiedType(Map<String,String> vars, String parentType) {
		this.vars = vars;
		this.parentType = parentType;
	}
	
	public static final ObjInstance fromString(String s, ObjectTree objtree, DMM dmm) {
		if(!s.contains("{"))
			return objtree.get(s);
		// This will match the type path (/blah/blah) and the var list (a = "b"; c = 123)
		Matcher m = Pattern.compile("([\\w/]+)\\{(.*)\\}").matcher(s);
		if(m.find()) {
			Map<String,String> vars = new LinkedHashMap<>();
			// This will match variable key-val
			Matcher varmatcher = Pattern.compile("([\\w]+) ?= ?((?:\"(?:\\\\\"|[^\"])*\"|[^;])*)(?:$|;)").matcher(m.group(2));
			while(varmatcher.find()) {
				vars.put(varmatcher.group(1), varmatcher.group(2));
			}
			ModifiedType mt = new ModifiedType(vars, m.group(1));
			mt.parent = objtree.get(m.group(1));
			if(dmm.editor.modifiedTypes.containsKey(mt.toString())) {
				mt = dmm.editor.modifiedTypes.get(mt.toString());
			} else {
				dmm.editor.modifiedTypes.put(mt.toString(), mt);
				if(mt.parent != null) {
					mt.parent.addInstance(mt);
				}
			}
			return mt;
		}
		return null;
	}
	
	public static final ModifiedType deriveFrom(ObjInstance i) {
		if(i instanceof ObjectTreeItem){
			ModifiedType mt = new ModifiedType(new TreeMap<>(), i.typeString());
			mt.parent = (ObjectTreeItem)i;
			return mt;
		} else {
			ModifiedType p = (ModifiedType)i;
			ModifiedType mt = new ModifiedType(new TreeMap<>(p.vars), p.typeString());
			mt.parent = p.parent;
			return mt;
		}
	}
	
	public Map<String, String> vars;
	public String parentType;
	public ObjectTreeItem parent;
	
	@Override
	public String getVar(String key) {
		if(vars.containsKey(key))
			return vars.get(key);
		if(parent != null)
			return parent.getVar(key);
		return null;
	}
	
	public String toString() {
		StringBuilder out = new StringBuilder(parentType);
		out.append('{');
		boolean isFirst = true;
		for(Map.Entry<String,String> e : vars.entrySet()) {
			if(isFirst)
				isFirst = false;
			else
				out.append("; ");
			out.append(e.getKey());
			out.append(" = ");
			out.append(e.getValue());
		}
		out.append('}');
		return out.toString();
	}
	
	public String toStringTGM() {
		StringBuilder out = new StringBuilder(parentType);
		out.append("{\n\t");
		boolean isFirst = true;
		for(Map.Entry<String,String> e : vars.entrySet()) {
			if(isFirst)
				isFirst = false;
			else
				out.append(";\n\t");
			out.append(e.getKey());
			out.append(" = ");
			out.append(e.getValue());
		}
		out.append("\n\t}");
		return out.toString();
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof ModifiedType))
			return false;
		if(other == this)
			return true;
		if(other.toString().equals(toString()))
			return true;
		return false;
	}

	@Override
	public String typeString() {
		return parentType;
	}
	
	public boolean istype(String path) {
		if(parent != null)
			return parent.istype(path);
		return false;
	}
	
	public boolean viewVariables(FastDMM editor) {
		final JDialog dialog = new JDialog(editor, "View Variables", true);
		;
		
		final ModifiedTypeTableModel model = new ModifiedTypeTableModel(this);
		JTable table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setDefaultRenderer(Object.class, new ModifiedTypeRenderer(model));
		dialog.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
	
		
		JTextField filterField = Util.createRowFilter(table);
		
		filterField.setPreferredSize(new Dimension(600, 30));
		
		
		dialog.getContentPane().add(filterField, BorderLayout.NORTH);
		
		

		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
            model.doReturnTrue = true;
            dialog.setVisible(false);
            dialog.dispose(); 
        });
		bottomPanel.add(okButton, BorderLayout.EAST);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {	
			dialog.setVisible(false);
			dialog.dispose();
		});
		
		bottomPanel.add(cancelButton, BorderLayout.WEST);
		
		dialog.setLocationRelativeTo(editor);
		dialog.setSize(600, 500);
		dialog.setPreferredSize(dialog.getSize());
		dialog.setVisible(true);
		
		return model.doReturnTrue;
	}
	
	public void editIconState(FastDMM editor) {
		final JDialog dialog = new JDialog(editor, "Edit Icon State", true);
		

		DMI dmi = FastDMM.getFastDMM().getDmi(getIcon(), true);
		
		
		JLabel iconStateViewer;
		
		if (getIconState() != null)
			iconStateViewer = new JLabel(new ImageIcon(dmi.getIconState(getIconState()).getSubstate(0).getScaledImage(6)), JLabel.CENTER);
		else {
			iconStateViewer = new JLabel(new ImageIcon(), JLabel.CENTER);
		}
			
		iconStateViewer.setHorizontalAlignment(JLabel.CENTER);
		
		
		DefaultListModel<IconState> iconStateListModel = new DefaultListModel<IconState>();
		
	    for (IconState iconState : dmi.iconStates.values()) {
	    	iconStateListModel.addElement(iconState);
	    }
		
		JList<IconState> iconStateList = new JList<IconState>(iconStateListModel);
		
		
		iconStateList.setCellRenderer(new IconStateListRenderer());
		
		iconStateViewer.setPreferredSize(new Dimension(600,500));
		
		dialog.getContentPane().add(new JScrollPane(iconStateList), BorderLayout.WEST);
	
		dialog.getContentPane().add(iconStateViewer, BorderLayout.EAST);
		
		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		
		dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		
		
		iconStateList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					iconStateViewer.setIcon(new ImageIcon (iconStateList.getSelectedValue().getSubstate(0).getScaledImage(6)));
				}
			}
		});
		
		
		JButton okButton = new JButton("OK");
		
		okButton.addActionListener(e -> {
            dialog.setVisible(false);
            if (iconStateList.getSelectedValue() != null)
            	vars.put("icon_state", '"'+iconStateList.getSelectedValue().name+'"');
            	cachedIconState = null;
            dialog.dispose(); 
        });
		
		bottomPanel.add(okButton, BorderLayout.EAST);
		
		JButton cancelButton = new JButton("Cancel");
		
		cancelButton.addActionListener(e -> {
			dialog.setVisible(false);
			dialog.dispose();
		});
		
		bottomPanel.add(cancelButton, BorderLayout.WEST);
		
		dialog.setLocationRelativeTo(editor);
		dialog.setSize(750, 500);
		dialog.setPreferredSize(dialog.getSize());
		dialog.setVisible(true);
		
	}
}
