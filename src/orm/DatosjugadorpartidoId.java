package orm;
// Generated 26 nov 2024 13:22:35 by Hibernate Tools 6.5.1.Final

/**
 * DatosjugadorpartidoId generated by hbm2java
 */
public class DatosjugadorpartidoId implements java.io.Serializable {

	private int idJ;
	private int idP;

	public DatosjugadorpartidoId() {
	}

	public DatosjugadorpartidoId(int idJ, int idP) {
		this.idJ = idJ;
		this.idP = idP;
	}

	public int getIdJ() {
		return this.idJ;
	}

	public void setIdJ(int idJ) {
		this.idJ = idJ;
	}

	public int getIdP() {
		return this.idP;
	}

	public void setIdP(int idP) {
		this.idP = idP;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof DatosjugadorpartidoId))
			return false;
		DatosjugadorpartidoId castOther = (DatosjugadorpartidoId) other;

		return (this.getIdJ() == castOther.getIdJ()) && (this.getIdP() == castOther.getIdP());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getIdJ();
		result = 37 * result + this.getIdP();
		return result;
	}

}
