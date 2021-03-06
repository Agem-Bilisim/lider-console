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
package tr.org.liderahenk.liderconsole.core.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.LiderConfirmBox;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

/**
 * Default task dialog implementation that can be used by plugins in order to
 * provide task modification capabilities. Plugins should extend this class for
 * task execution.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public abstract class DefaultTaskDialog extends TitleAreaDialog {

	private static final Logger logger = LoggerFactory.getLogger(DefaultTaskDialog.class);

	private Button btnExecuteNow;
	private Button btnExecuteScheduled;
	private DateTime dtActivationDate;
	private DateTime dtActivationDateTime;
	private Button btnEnableDate;
	private ProgressBar progressBar;

	private IEventBroker eventBroker = (IEventBroker) PlatformUI.getWorkbench().getService(IEventBroker.class);
	private List<EventHandler> handlers = new ArrayList<EventHandler>();

	private Set<String> dnSet = new LinkedHashSet<String>();
	private boolean hideActivationDate;

	public DefaultTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell);
		if (dnSet != null)
			this.dnSet.addAll(dnSet);
		this.hideActivationDate = false;
		init();
	}

	public DefaultTaskDialog(Shell parentShell, String dn) {
		super(parentShell);
		this.dnSet.add(dn);
		this.hideActivationDate = false;
		init();
	}

	public DefaultTaskDialog(Shell parentShell, Set<String> dnSet, boolean hideActivationDate) {
		super(parentShell);
		if (dnSet != null)
			this.dnSet.addAll(dnSet);
		this.hideActivationDate = hideActivationDate;
		init();
	}

	public DefaultTaskDialog(Shell parentShell, String dn, boolean hideActivationDate) {
		super(parentShell);
		this.dnSet.add(dn);
		this.hideActivationDate = hideActivationDate;
		init();
	}

	/**
	 * 
	 * @return dialog title
	 */
	public abstract String createTitle();

	/**
	 * Create task related widgets here!
	 * 
	 * @param parent
	 * @return
	 */
	public abstract Control createTaskDialogArea(Composite parent);

	/**
	 * Validate task data here before sending it to Lider for execution. If
	 * validation fails for any of task data, this method should throws a
	 * {@link ValidationException}.
	 * 
	 * @return
	 */
	public abstract void validateBeforeExecution() throws ValidationException;

	/**
	 * 
	 * @return parameter map of the task.
	 */
	public abstract Map<String, Object> getParameterMap();

	/**
	 * 
	 * @return task code
	 */
	public abstract String getTaskCode();

	/**
	 * 
	 * @return plugin name
	 */
	public abstract String getPluginName();

	/**
	 * 
	 * @return plugin version
	 */
	public abstract String getPluginVersion();

	@Override
	public void create() {
		super.create();
		setTitle(createTitle());
		setMessage(generateMsg(dnSet), IMessageProvider.INFORMATION);
	}

	public void openWithEventBroker() {
		super.setBlockOnOpen(true);
		super.open();
		unsubscribeEventHandlers();
	}

	public void subscribeEventHandler(EventHandler handler) {
		eventBroker.subscribe(getPluginName().toUpperCase(Locale.ENGLISH), handler);
		handlers.add(handler);
	}

	public void unsubscribeEventHandlers() {
		try {
			if (handlers != null && !handlers.isEmpty() && eventBroker != null) {
				for (EventHandler handler : handlers) {
					eventBroker.unsubscribe(handler);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		// Container
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(1, false));
		// Task-related inputs
		createTaskDialogArea(container);
		// Activation date inputs
		if (!hideActivationDate) {
			createTaskActivationDateArea(container);
		}
		// Progress bar
		progressBar = new ProgressBar(container, SWT.SMOOTH | SWT.INDETERMINATE);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
		GridData gdProgress = new GridData(GridData.FILL_HORIZONTAL);
		gdProgress.heightHint = 10;
		progressBar.setLayoutData(gdProgress);
		progressBar.setVisible(false);
		return container;
	}

	private void createTaskActivationDateArea(final Composite parent) {
		new Label(parent, SWT.NONE); // separate activate date from dialog area
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.BOTTOM, SWT.FILL, true, false));

		// Activation date enable/disable checkbox
		btnEnableDate = new Button(composite, SWT.CHECK);
		btnEnableDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		btnEnableDate.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dtActivationDate.setEnabled(btnEnableDate.getSelection());
				dtActivationDateTime.setEnabled(btnEnableDate.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Activation date label
		Label lblActivationDate = new Label(composite, SWT.NONE);
		lblActivationDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lblActivationDate.setText(Messages.getString("ACTIVATION_DATE_LABEL"));

		// Activation date
		dtActivationDate = new DateTime(composite, SWT.DROP_DOWN | SWT.BORDER);
		dtActivationDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		dtActivationDate.setEnabled(btnEnableDate.getSelection());

		// Activation time
		dtActivationDateTime = new DateTime(composite, SWT.DROP_DOWN | SWT.BORDER | SWT.TIME);
		dtActivationDateTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		dtActivationDateTime.setEnabled(btnEnableDate.getSelection());
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// Execute task now
		btnExecuteNow = createButton(parent, 5000, Messages.getString("EXECUTE_NOW"), false);
		btnExecuteNow.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/task-play.png"));
		btnExecuteNow.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Validation of task data
				if (validateTaskData()) {
					if (LiderConfirmBox.open(Display.getDefault().getActiveShell(),
							Messages.getString("TASK_EXEC_TITLE"), Messages.getString("TASK_EXEC_MESSAGE"), true)) {
						try {
							progressBar.setVisible(true);
							TaskRequest task = new TaskRequest(new ArrayList<String>(dnSet), DNType.AHENK,
									getPluginName(), getPluginVersion(), getTaskCode(), getParameterMap(), null,
									!hideActivationDate && btnEnableDate.getSelection()
											? SWTResourceManager.convertDate(dtActivationDate, dtActivationDateTime)
											: null,
									new Date());
							TaskRestUtils.execute(task);
							// Progress bar will be automatically hidden on
							// TASK_STATUS message received
						} catch (Exception e1) {
							progressBar.setVisible(false);
							logger.error(e1.getMessage(), e1);
							Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		// Schedule task to be executed
		btnExecuteScheduled = createButton(parent, 5001, Messages.getString("EXECUTE_SCHEDULED"), false);
		btnExecuteScheduled.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/task-wait.png"));
		GridData gridData = new GridData();
		gridData.widthHint = 140;
		btnExecuteScheduled.setLayoutData(gridData);
		btnExecuteScheduled.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Validation of task data
				if (validateTaskData()) {
					SchedulerDialog dialog = new SchedulerDialog(Display.getDefault().getActiveShell());
					dialog.create();
					if (dialog.open() != Window.OK) {
						return;
					}
					if (LiderConfirmBox.open(Display.getDefault().getActiveShell(),
							Messages.getString("TASK_EXEC_SCHEDULED_TITLE"),
							Messages.getString("TASK_EXEC_SCHEDULED_MESSAGE"), true)) {
						try {
							progressBar.setVisible(true);
							TaskRequest task = new TaskRequest(new ArrayList<String>(dnSet), DNType.AHENK,
									getPluginName(), getPluginVersion(), getTaskCode(), getParameterMap(),
									dialog.getCronExpression(),
									!hideActivationDate && btnEnableDate.getSelection()
											? SWTResourceManager.convertDate(dtActivationDate, dtActivationDateTime)
											: null,
									new Date());
							TaskRestUtils.execute(task);
							progressBar.setVisible(false);
						} catch (Exception e1) {
							progressBar.setVisible(false);
							logger.error(e1.getMessage(), e1);
							Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		// Close
		Button closeButton = createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("CANCEL"), true);
		closeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				unsubscribeEventHandlers();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	/**
	 * Generate title message from DN set. Abbreviate DNs if necessary.
	 * 
	 * @param dnSet
	 * @return
	 */
	private String generateMsg(Set<String> dnSet) {
		if (dnSet != null) {
			StringBuilder msg = new StringBuilder("");
			int i = 0;
			for (String dn : dnSet) {
				msg.append(dn).append(" ");
				if (i == 3) {
					break;
				}
				i++;
			}
			return msg.toString();
		}
		return "";
	}

	/**
	 * Handles validation result of task data.
	 */
	protected boolean validateTaskData() {
		try {
			validateBeforeExecution();
			return true;
		} catch (ValidationException e) {
			if (e.getMessage() != null && !"".equals(e.getMessage())) {
				Notifier.warning(null, e.getMessage());
			} else {
				Notifier.error(null, Messages.getString("ERROR_ON_VALIDATE"));
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			Notifier.error(null, Messages.getString("ERROR_ON_VALIDATE"));
			return false;
		}
	}

	/**
	 * Hook event handler for task status notifications, this event handler is
	 * responsible for hiding the progress bar.
	 */
	private void init() {
		EventHandler handler = new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				if (progressBar != null && !progressBar.isDisposed()) {
					progressBar.setVisible(false);
				}
			}
		};
		eventBroker.subscribe(LiderConstants.EVENT_TOPICS.TASK_STATUS_NOTIFICATION_RECEIVED, handler);
		handlers.add(handler);
	}

	/*
	 * Getters
	 */

	/**
	 * 
	 * @return
	 */
	public Set<String> getDnSet() {
		return dnSet;
	}

	/**
	 * Provide getter for progress bar, so that extending classes can hide/show
	 * it manually.
	 * 
	 * @return
	 */
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

}
