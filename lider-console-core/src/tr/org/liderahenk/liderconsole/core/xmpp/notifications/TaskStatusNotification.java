package tr.org.liderahenk.liderconsole.core.xmpp.notifications;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.liderconsole.core.model.CommandExecution;
import tr.org.liderahenk.liderconsole.core.model.CommandExecutionResult;
import tr.org.liderahenk.liderconsole.core.xmpp.enums.NotificationType;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskStatusNotification implements Serializable {

	private static final long serialVersionUID = -9205240660741633195L;

	private NotificationType type = NotificationType.TASK_STATUS;

	private String pluginName;

	private String pluginVersion;

	private String commandClsId;

	private CommandExecution commandExecution;

	private CommandExecutionResult result;

	private Date timestamp;

	public TaskStatusNotification() {
	}

	public TaskStatusNotification(String pluginName, String pluginVersion, String commandClsId,
			CommandExecution commandExecution, CommandExecutionResult result, Date timestamp) {
		this.pluginName = pluginName;
		this.pluginVersion = pluginVersion;
		this.commandClsId = commandClsId;
		this.commandExecution = commandExecution;
		this.result = result;
		this.timestamp = timestamp;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public String getCommandClsId() {
		return commandClsId;
	}

	public void setCommandClsId(String commandClsId) {
		this.commandClsId = commandClsId;
	}

	public CommandExecution getCommandExecution() {
		return commandExecution;
	}

	public void setCommandExecution(CommandExecution commandExecution) {
		this.commandExecution = commandExecution;
	}

	public CommandExecutionResult getResult() {
		return result;
	}

	public void setResult(CommandExecutionResult result) {
		this.result = result;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
