package com.github.tgstation.fastdmm.editing.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

import com.github.tgstation.fastdmm.dmirender.IconState;

public class IconStateListRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -7799441088157759804L;

    private Color textSelectionColor = Color.BLACK;
    private Color backgroundSelectionColor = Color.CYAN;
    private Color textNonSelectionColor = Color.BLACK;
    private Color backgroundNonSelectionColor = Color.WHITE;
    
    private Float iconScale = 0.75f;

    public IconStateListRenderer() {
    }
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean expanded) {
    	
    	IconState iconState = (IconState) value;
    	
    	Image scaledImage = iconState.getSubstate(0).getScaledImage(iconScale);
    	
    	if (scaledImage != null)
    		setIcon(new ImageIcon(scaledImage));
    	else
    		setIcon(null);
    	
        setText(iconState.name);

        if (selected) {
            setBackground(backgroundSelectionColor);
            setForeground(textSelectionColor);
        } else {
            setBackground(backgroundNonSelectionColor);
            setForeground(textNonSelectionColor);
        }

        return this;
    }
}