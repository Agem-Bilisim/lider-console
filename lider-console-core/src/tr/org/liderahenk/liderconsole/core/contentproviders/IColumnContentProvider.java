package tr.org.liderahenk.liderconsole.core.contentproviders;

import org.eclipse.jface.viewers.IContentProvider;

public interface IColumnContentProvider extends IContentProvider {
	Comparable getValue(Object element, int column);
}
