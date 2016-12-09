package tr.org.liderahenk.liderconsole.core.model;

import tr.org.liderahenk.liderconsole.core.i18n.Messages;

/**
 * Enum class for report types.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public enum ReportType {

	// TODO Could not find pie chart in SWTChart library at the moment...
	TABLE(1), /*PIE_CHART(2),*/ BAR_CHART(3), LINE_CHART(4);

	private int id;

	private ReportType(int id) {
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
	 * @return related ReportType enum
	 * @see http://blog.chris-ritchie.com/2013/09/mapping-enums-with-fixed-id-in
	 *      -jpa.html
	 * 
	 */
	public static ReportType getType(Integer id) {
		if (id == null) {
			return null;
		}
		for (ReportType type : ReportType.values()) {
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
