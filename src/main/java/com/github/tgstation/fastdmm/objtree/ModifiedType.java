package com.github.tgstation.fastdmm.objtree;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.regex.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.github.tgstation.fastdmm.FastDMM;
import com.github.tgstation.fastdmm.dmmmap.DMM;
import com.github.tgstation.fastdmm.editing.ui.ModifiedTypeRenderer;
import com.github.tgstation.fastdmm.editing.ui.ModifiedTypeTableModel;

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
		
		final ModifiedTypeTableModel model = new ModifiedTypeTableModel(this);
		JTable table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setDefaultRenderer(Object.class, new ModifiedTypeRenderer(model));
		dialog.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
            model.doReturnTrue = true;
            dialog.setVisible(false);
        });
		bottomPanel.add(okButton, BorderLayout.EAST);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> dialog.setVisible(false));
		bottomPanel.add(cancelButton, BorderLayout.WEST);
		
		dialog.setLocationRelativeTo(editor);
		dialog.setSize(400, 450);
		dialog.setPreferredSize(dialog.getSize());
		dialog.setVisible(true);
		
		return model.doReturnTrue;
	}
}
