package org.fastcode;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;

public class FastCodeCommandState extends AbstractSourceProvider {
	public final static String	MY_STATE	= "org.fastcode.commands.sourceprovider.active";
	public final static String	ENABLED		= "ENABLED";
	public final static String	DISENABLED	= "DISENABLED";
	private final boolean				enabled		= true;

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map getCurrentState() {
		final Map map = new HashMap(1);
	    final String value = this.enabled ? ENABLED : DISENABLED;
	    map.put(MY_STATE, value);
	    return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { MY_STATE };
	}

	/*public void toogleEnabled() {
	    enabled = !enabled ;
	    String value = enabled ? ENABLED : DISENABLED;
	    fireSourceChanged(ISources.WORKBENCH, MY_STATE, value);
	  }*/

}
