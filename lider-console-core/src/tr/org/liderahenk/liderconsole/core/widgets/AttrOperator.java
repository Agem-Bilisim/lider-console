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

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import tr.org.liderahenk.liderconsole.core.model.SearchFilterEnum;

/**
 * @author Emre Akkaya <emre.akkaya@agem.com.tr>
 *
 */
public class AttrOperator extends Combo {

	/**
	 * @param parent
	 * @param style
	 */
	public AttrOperator(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * 
	 * @param combo
	 * @return selected value of this combo
	 */
	public String getSelectedValue() {
		int selectionIndex = getSelectionIndex();
		if (selectionIndex > -1 && getItem(selectionIndex) != null && getData(selectionIndex + "") != null) {
			return ((SearchFilterEnum) getData(selectionIndex + "")).getOperator();
		}
		return SearchFilterEnum.EQ.getOperator();
	}

	@Override
	protected void checkSubclass() {
		// By default, subclassing is not allowed for many of the SWT Controls
		// This empty method disables the check that prevents subclassing of
		// this class
	}

}
