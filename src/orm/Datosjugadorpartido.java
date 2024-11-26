package orm;
// Generated 26 nov 2024 13:33:18 by Hibernate Tools 6.5.1.Final

/**
 * Datosjugadorpartido generated by hbm2java
 */
public class Datosjugadorpartido implements java.io.Serializable {

	private DatosjugadorpartidoId id;
	private Jugador jugador;
	private Partido partido;
	private Double valoracion;
	private Integer puntos;
	private Integer asistencias;
	private Integer rebotes;
	private Integer tapones;
	private Boolean titular;

	public Datosjugadorpartido() {
	}

	public Datosjugadorpartido(DatosjugadorpartidoId id, Jugador jugador, Partido partido) {
		this.id = id;
		this.jugador = jugador;
		this.partido = partido;
	}

	public Datosjugadorpartido(DatosjugadorpartidoId id, Jugador jugador, Partido partido, Double valoracion,
			Integer puntos, Integer asistencias, Integer rebotes, Integer tapones, Boolean titular) {
		this.id = id;
		this.jugador = jugador;
		this.partido = partido;
		this.valoracion = valoracion;
		this.puntos = puntos;
		this.asistencias = asistencias;
		this.rebotes = rebotes;
		this.tapones = tapones;
		this.titular = titular;
	}

	public DatosjugadorpartidoId getId() {
		return this.id;
	}

	public void setId(DatosjugadorpartidoId id) {
		this.id = id;
	}

	public Jugador getJugador() {
		return this.jugador;
	}

	public void setJugador(Jugador jugador) {
		this.jugador = jugador;
	}

	public Partido getPartido() {
		return this.partido;
	}

	public void setPartido(Partido partido) {
		this.partido = partido;
	}

	public Double getValoracion() {
		return this.valoracion;
	}

	public void setValoracion(Double valoracion) {
		this.valoracion = valoracion;
	}

	public Integer getPuntos() {
		return this.puntos;
	}

	public void setPuntos(Integer puntos) {
		this.puntos = puntos;
	}

	public Integer getAsistencias() {
		return this.asistencias;
	}

	public void setAsistencias(Integer asistencias) {
		this.asistencias = asistencias;
	}

	public Integer getRebotes() {
		return this.rebotes;
	}

	public void setRebotes(Integer rebotes) {
		this.rebotes = rebotes;
	}

	public Integer getTapones() {
		return this.tapones;
	}

	public void setTapones(Integer tapones) {
		this.tapones = tapones;
	}

	public Boolean getTitular() {
		return this.titular;
	}

	public void setTitular(Boolean titular) {
		this.titular = titular;
	}

}
