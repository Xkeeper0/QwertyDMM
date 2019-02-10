package com.github.tgstation.fastdmm.editing;

import com.github.tgstation.fastdmm.FastDMM;
import com.github.tgstation.fastdmm.dmmmap.Location;
import com.github.tgstation.fastdmm.dmmmap.TileInstance;
import com.github.tgstation.fastdmm.objtree.ModifiedType;
import com.github.tgstation.fastdmm.objtree.ObjInstance;

public class SetDirListener extends SimpleContextMenuListener {
	
	private int newDir;
	
	public SetDirListener(FastDMM editor, Location mapLocation, ObjInstance instance, int dir) {
		super(editor, mapLocation, instance);
		newDir = dir;
	}
	
	@Override
	public void doAction() {
		synchronized(editor) {
			TileInstance ti = editor.dmm.instances.get(editor.dmm.map.get(location));
			
			if(ti == null)
				return;
			
			ModifiedType mt = ModifiedType.deriveFrom(oInstance);
			
			mt.vars.put("dir", Integer.toString(newDir));
			
			String newKey = ti.replaceObject(oInstance, mt);
			
			editor.dmm.putMap(location, newKey);
		}
	}
}
