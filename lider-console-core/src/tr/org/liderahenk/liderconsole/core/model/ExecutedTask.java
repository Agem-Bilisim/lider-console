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
package tr.org.liderahenk.liderconsole.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * This is a specialized class which is used to list executed tasks with some
 * additional info.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutedTask implements Serializable {

	private static final long serialVersionUID = -4158646812377132719L;

	private Long id;

	private String pluginName;

	private Map<String, String> pluginDisplayNames;

	private String pluginVersion;

	private String taskCode;

	private Map<String, String> taskCodeDisplayNames;

	private Date createDate;

	private Integer successResults;

	private Integer warningResults;

	private Integer errorResults;

	private Boolean cancelled;

	private Boolean scheduled;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public Map<String, String> getPluginDisplayNames() {
		return pluginDisplayNames;
	}

	public void setPluginDisplayNames(Map<String, String> pluginDisplayNames) {
		this.pluginDisplayNames = pluginDisplayNames;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public Map<String, String> getTaskCodeDisplayNames() {
		return taskCodeDisplayNames;
	}

	public void setTaskCodeDisplayNames(Map<String, String> taskCodeDisplayNames) {
		this.taskCodeDisplayNames = taskCodeDisplayNames;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getSuccessResults() {
		return successResults;
	}

	public void setSuccessResults(Integer successResults) {
		this.successResults = successResults;
	}

	public Integer getErrorResults() {
		return errorResults;
	}

	public void setErrorResults(Integer errorResults) {
		this.errorResults = errorResults;
	}

	public Boolean getCancelled() {
		return cancelled;
	}

	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Integer getWarningResults() {
		return warningResults;
	}

	public void setWarningResults(Integer warningResults) {
		this.warningResults = warningResults;
	}

	public Boolean getScheduled() {
		return scheduled;
	}

	public void setScheduled(Boolean scheduled) {
		this.scheduled = scheduled;
	}

}
