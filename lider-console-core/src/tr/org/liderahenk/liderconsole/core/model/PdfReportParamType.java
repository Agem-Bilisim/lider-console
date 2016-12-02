package tr.org.liderahenk.liderconsole.core.model;

import tr.org.liderahenk.liderconsole.core.i18n.Messages;

public enum PdfReportParamType {

	PAGE_NO(1), DATE(2), TEXT(3);

	private int id;

	private PdfReportParamType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	/**
	 * Provide mapping enums with a fixed ID in JPA (a more robust alternative
	 * to EnumType.String and EnumType.Ordinal)
	 * 
	 * @param id
	 * @return related PdfReportParamType enum
	 * @see http://blog.chris-ritchie.com/2013/09/mapping-enums-with-fixed-id-in
	 *      -jpa.html
	 * 
	 */
	public static PdfReportParamType getType(Integer id) {
		if (id == null) {
			return null;
		}
		for (PdfReportParamType type : PdfReportParamType.values()) {
			if (id.equals(type.getId())) {
				return type;
			}
		}
		throw new IllegalArgumentException("No matching type for id: " + id);
	}

	/**
	 * Provide i18n message representation of the enum type.
	 * 
	 * @return
	 */
	public String getMessage() {
		return Messages.getString(this.toString());
	}

}
