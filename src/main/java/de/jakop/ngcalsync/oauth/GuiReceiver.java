package de.jakop.ngcalsync.oauth;

import org.apache.commons.lang3.Validate;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * @author fjakop
 *
 */
public class GuiReceiver implements IUserInputReceiver {

	private final Composite parent;

	/** 
	 * 
	 * @param parent
	 */
	public GuiReceiver(final Composite parent) {
		Validate.notNull(parent);
		this.parent = parent;
	}

	@Override
	public String waitForUserInput(final String message) {
		final InputDialog dialog = new InputDialog(parent.getShell(), "title", "message", "", null);
		dialog.open();
		return dialog.getValue();
	}

}
