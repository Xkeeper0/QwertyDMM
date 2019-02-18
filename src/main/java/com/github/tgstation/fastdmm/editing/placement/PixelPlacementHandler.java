package com.github.tgstation.fastdmm.editing.placement;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.tgstation.fastdmm.FastDMM;
import com.github.tgstation.fastdmm.dmirender.DMI;
import com.github.tgstation.fastdmm.dmirender.IconState;
import com.github.tgstation.fastdmm.dmirender.IconSubstate;
import com.github.tgstation.fastdmm.dmirender.RenderInstance;
import com.github.tgstation.fastdmm.dmmmap.Location;
import com.github.tgstation.fastdmm.dmmmap.TileInstance;
import com.github.tgstation.fastdmm.objtree.ModifiedType;
import com.github.tgstation.fastdmm.objtree.ObjInstance;

public class PixelPlacementHandler implements PlacementHandler {
	private Location usedLocation;
	private FastDMM editor;
	private ObjInstance oInstance;
	private ModifiedType usedInstance;
	private int initMouseY;
	private int initMouseX;
	
	@Override
	public void init(FastDMM editor, ObjInstance instance, Location initialLocation) {
		this.editor = editor;
		this.oInstance = instance;
		this.usedInstance = ModifiedType.deriveFrom(instance);
		this.usedLocation = initialLocation;
		this.initMouseX = editor.relMouseX;
		this.initMouseY = editor.relMouseY;
		
		DMI dmi = editor.getDmi(oInstance.getIcon(), false);
		if(dmi != null) {
			String iconStateStr = oInstance.getIconState();
			IconState state = dmi.getIconState(iconStateStr);
		}

	}

	@Override
	public void dragTo(Location location) {

	}

	@Override
	public int visualize(Set<RenderInstance> rendInstanceSet, int currCreationIndex) {
		if(usedInstance == null)
			return currCreationIndex;
		DMI dmi = editor.getDmi(usedInstance.getIcon(), true);
		if(dmi == null)
			return currCreationIndex;
		String iconState = usedInstance.getIconState();
		IconSubstate substate = dmi.getIconState(iconState).getSubstate(usedInstance.getDir());
		
		RenderInstance ri = new RenderInstance(currCreationIndex++);
		ri.layer = usedInstance.getLayer();
		ri.plane = usedInstance.getPlane();
		ri.x = usedLocation.x + (usedInstance.getPixelX()/(float)editor.objTree.icon_size);
		ri.y = usedLocation.y + (usedInstance.getPixelY()/(float)editor.objTree.icon_size);
		ri.substate = substate;
		ri.color = usedInstance.getColor();
		
		rendInstanceSet.add(ri);
		
		ri = new RenderInstance(currCreationIndex++);
		ri.plane = 101;
		ri.x = usedLocation.x;
		ri.y = usedLocation.y;
		ri.substate = editor.interface_dmi.getIconState("15").getSubstate(2);
		ri.color = new Color(255,255,255);
		
		rendInstanceSet.add(ri);
		
		return currCreationIndex;
	}

	@Override
	public void finalizePlacement() {
		// TODO Auto-generated method stub
		String key = editor.dmm.map.get(usedLocation);
		if(key != null) {
			TileInstance tInstance = editor.dmm.instances.get(key);
			String newKey = tInstance.addObject(usedInstance);
			editor.dmm.putMap(usedLocation, newKey);
			editor.addToUndoStack(editor.dmm.popDiffs());
		}
	}

	@Override
	public void dragToPixel(int x, int y) {
		int dx = x - initMouseX;
		int dy = y - initMouseY;
		System.out.println(x+" "+y);		
		
		usedInstance.cachedPixelX = dx;
		usedInstance.cachedPixelY = dy;
		usedInstance.vars.put("pixel_x", Integer.toString(dx));
		usedInstance.vars.put("pixel_y", Integer.toString(dy));	
	}
}
