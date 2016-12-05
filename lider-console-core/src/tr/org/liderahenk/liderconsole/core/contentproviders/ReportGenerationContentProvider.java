package tr.org.liderahenk.liderconsole.core.contentproviders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;

import tr.org.liderahenk.liderconsole.core.config.ConfigProvider;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.model.ReportViewColumn;
import tr.org.liderahenk.liderconsole.core.utils.LiderCoreUtils;

public class ReportGenerationContentProvider extends ArrayContentProvider implements IColumnContentProvider {

	List<ReportViewColumn> viewColumns;

	public ReportGenerationContentProvider(Set<ReportViewColumn> viewColumns) {
		super();
		this.viewColumns = viewColumns != null && !viewColumns.isEmpty() ? new ArrayList<ReportViewColumn>(viewColumns)
				: null;
	}

	private static final SimpleDateFormat format = new SimpleDateFormat(
			ConfigProvider.getInstance().get(LiderConstants.CONFIG.DATE_FORMAT));

	@Override
	public Comparable getValue(Object element, int column) {
		Comparable retVal = null;
		if (element instanceof Object[]) {
			Object[] row = (Object[]) element;
			if (viewColumns != null) {
				ReportViewColumn viewColumn = viewColumns.get(column);
				int index = viewColumn.getReferencedCol().getColumnOrder() - 1;
				retVal = (Comparable) row[index];
			} else {
				retVal = (Comparable) row[column];
			}
			if (retVal != null && LiderCoreUtils.isValidDate(retVal.toString(),
					ConfigProvider.getInstance().get(LiderConstants.CONFIG.DATE_FORMAT))) {
				try {
					retVal = format.parse(retVal.toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return retVal;
	}

}
