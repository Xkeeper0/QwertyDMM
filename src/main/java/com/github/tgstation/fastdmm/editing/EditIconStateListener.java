package com.github.tgstation.fastdmm.editing;

import com.github.tgstation.fastdmm.FastDMM;
import com.github.tgstation.fastdmm.dmmmap.Location;
import com.github.tgstation.fastdmm.dmmmap.TileInstance;
import com.github.tgstation.fastdmm.objtree.ModifiedType;
import com.github.tgstation.fastdmm.objtree.ObjInstance;

public class EditIconStateListener extends SimpleContextMenuListener {
	
	public EditIconStateListener(FastDMM editor, Location mapLocation, ObjInstance instance) {
		super(editor, mapLocation, instance);
	}

	@Override
	public void doAction() {
		TileInstance ti = editor.dmm.instances.get(editor.dmm.map.get(location));
		
		if(ti == null)
			return;
		
		ModifiedType mt = ModifiedType.deriveFrom(oInstance);
		
		mt.editIconState(editor);

		String newKey = ti.replaceObject(oInstance, mt.vars.size() != 0 ? mt : mt.parent);
		
		editor.dmm.putMap(location, newKey);
	}
}
