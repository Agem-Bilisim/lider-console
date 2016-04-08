package tr.org.liderahenk.liderconsole.core.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.editorinput.ProfileEditorInput;
import tr.org.liderahenk.liderconsole.core.editors.DefaultProfileEditor;
import tr.org.liderahenk.liderconsole.core.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.rest.requests.ProfileRequest;
import tr.org.liderahenk.liderconsole.core.rest.utils.ProfileUtils;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

/**
 * Default profile dialog implementation that can be used by plugins in order to
 * provide profile modification capabilities.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog
 *
 */
public class DefaultProfileDialog extends DefaultLiderDialog {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProfileDialog.class);

	private Profile selectedProfile;
	private DefaultProfileEditor editor;
	private ProfileEditorInput editorInput;

	private Text txtLabel;
	private Text txtDesc;
	private Button btnActive;
	private Button btnOverridable;
	
	// TODO IMPROVEMENT do not pass editor instance! find another way to refresh editor table

	public DefaultProfileDialog(Shell parentShell, Profile selectedProfile, DefaultProfileEditor editor,
			ProfileEditorInput editorInput) {
		super(parentShell);
		this.selectedProfile = selectedProfile;
		this.editor = editor;
		this.editorInput = editorInput;
		editorInput.getProfileDialog().init();
	}

	public DefaultProfileDialog(Shell parentShell, DefaultProfileEditor editor, ProfileEditorInput editorInput) {
		super(parentShell);
		this.editor = editor;
		this.editorInput = editorInput;
		editorInput.getProfileDialog().init();
	}

	/**
	 * Create profile input widgets
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		parent.setLayout(new GridLayout(1, false));

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		composite.setLayout(new GridLayout(2, false));

		// Profile label
		Label lblLabel = new Label(composite, SWT.NONE);
		lblLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblLabel.setText(Messages.getString("PROFILE_LABEL"));

		txtLabel = new Text(composite, SWT.BORDER);
		txtLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (selectedProfile != null && selectedProfile.getLabel() != null) {
			txtLabel.setText(selectedProfile.getLabel());
		}

		// Profile description
		Label lblDesc = new Label(composite, SWT.NONE);
		lblDesc.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblDesc.setText(Messages.getString("DESCRIPTION"));

		txtDesc = new Text(composite, SWT.BORDER);
		txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (selectedProfile != null && selectedProfile.getDescription() != null) {
			txtDesc.setText(selectedProfile.getDescription());
		}

		// Profile active
		btnActive = new Button(composite, SWT.CHECK);
		btnActive.setText(Messages.getString("ACTIVE"));
		btnActive.setSelection(selectedProfile != null && selectedProfile.isActive());

		// Profile overridable
		btnOverridable = new Button(composite, SWT.CHECK);
		btnOverridable.setText(Messages.getString("OVERRIDABLE"));
		btnOverridable.setSelection(selectedProfile != null && selectedProfile.isOverridable());

		// Create child composite for plugin
		Composite childComposite = new Composite(parent, SWT.NONE);
		childComposite.setLayout(new GridLayout(1, false));

		// Trigger plugin provided implementation
		editorInput.getProfileDialog().createDialogArea(childComposite, selectedProfile);

		applyDialogFont(composite);
		return composite;
	}

	/**
	 * Handle OK button press
	 */
	@Override
	protected void okPressed() {

		setReturnCode(OK);

		// Check if label is empty
		if (txtLabel.getText().isEmpty()) {
			Notifier.warning(null, Messages.getString("FILL_LABEL_FIELD"));
			return;
		}

		// Populate profile instance
		ProfileRequest profile = new ProfileRequest();
		profile.setPluginName(editorInput.getPluginName());
		profile.setPluginVersion(editorInput.getPluginVersion());
		if (selectedProfile != null && selectedProfile.getId() != null) {
			profile.setId(selectedProfile.getId());
		}
		profile.setActive(btnActive.getSelection());
		profile.setDescription(txtDesc.getText());
		profile.setLabel(txtLabel.getText());
		profile.setOverridable(btnOverridable.getSelection());
		logger.debug("Profile request: {}", profile);

		try {
			profile.setProfileData(editorInput.getProfileDialog().getProfileData());
			if (selectedProfile != null && selectedProfile.getId() != null) {
				ProfileUtils.update(profile);
			} else {
				ProfileUtils.add(profile);
			}
			editor.refresh();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_SAVE"));
		}

		close();
	}

}
