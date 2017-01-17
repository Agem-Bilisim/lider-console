/*
*
*    Copyright © 2015-2016 Agem Bilişim
*
*    This file is part of Lider Ahenk.
*
*    Lider Ahenk is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Lider Ahenk is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Lider Ahenk.  If not, see <http://www.gnu.org/licenses/>.
*/
package tr.org.liderahenk.liderconsole.core.widgets;

import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.current.UserSettings;
import tr.org.liderahenk.liderconsole.core.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;

/**
 * Provides confirm box that can be used by plugins.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class LiderConfirmBox extends MessageDialog {

	private boolean passwordConfirmation = false;
	private boolean validPassword = true;

	private Text txtPassword;

	public LiderConfirmBox(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
			int dialogImageType, String[] dialogButtonLabels, int defaultIndex, boolean passwordConfirmation) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
				defaultIndex);
		this.passwordConfirmation = passwordConfirmation;
		this.validPassword = !passwordConfirmation;
	}

	@Override
	protected Point getInitialSize() {
		return passwordConfirmation ? new Point(400, 210) : new Point(400, 180);
	}

	@Override
	protected Control createCustomArea(Composite parent) {
		if (passwordConfirmation) {
			Label lbl = new Label(parent, SWT.NONE);
			lbl.setText(Messages.getString("PASSWORD_CONFIRMATION"));

			txtPassword = new Text(parent, SWT.PASSWORD | SWT.BORDER);
			txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			txtPassword.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					validatePassword();
				}
			});
			return parent;
		}
		return null;
	}

	public static boolean open(Shell parent, String title, String message) {
		return open(parent, title, message, false);
	}

	public static boolean open(Shell parent, String title, String message, boolean passwordConfirmation) {
		LiderConfirmBox confirm = new LiderConfirmBox(parent, title,
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/32/warning.png"),
				message, MessageDialog.QUESTION,
				new String[] { JFaceResources.getString(IDialogLabelKeys.YES_LABEL_KEY),
						JFaceResources.getString(IDialogLabelKeys.NO_LABEL_KEY) },
				SWT.NONE, passwordConfirmation);
		boolean result = confirm.open() == 0;
		if (result) {
			if (!confirm.isValidPassword()) {
				Notifier.error(null, Messages.getString("PASSWORD_CONFIRMATION_FAILED"));
			}
			return confirm.isValidPassword();
		}
		return false;
	}

	private void validatePassword() {
		if (!passwordConfirmation) {
			validPassword = true;
			return;
		}
		validPassword = txtPassword != null && txtPassword.getText() != null
				&& txtPassword.getText().equals(UserSettings.USER_PASSWORD);
	}

	public boolean isValidPassword() {
		return validPassword;
	}

}
