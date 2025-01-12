/**
 * @author Mario Yusta
 */
package classPkg;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Session;

import orm.*;


public class MetodosHQL {
	public static Session session;
	
	/**
	 * Muestra los datos de un partido, indicado por el usuario por consola, mostrando de manera ordenada los datos generales del partido, el listado de jugadores Locales y el listado de jugadores Visitantes en dos columnas paralelas
	 * @param s
	 */
	public static void datosPartido(Session s) {
		session = s;
		
		//Selector de partido
		Scanner sc = new Scanner(System.in);
		int argsPartido = 0;
		Query qPartido = null;
		Partido partido = null;
		
		//El bucle se repetira hasta que se encuentre algun partido
		do {
			try {
				//Peticion de datos
				System.out.print("Introduce el id de un partido: ");
				argsPartido = sc.nextInt();
				
				//Busqueda de datos mediante sentencia HQL
				qPartido = s.createQuery("FROM Partido WHERE idPartido = :argsPartido").setParameter("argsPartido", argsPartido);
				partido = (Partido) qPartido.getSingleResult();
				
			}catch(NoResultException e) {
				//En caso de no encontrar a ningun partido se ejecutara la peticion de nuevo
				argsPartido = 0;
				System.out.println("Partido no encontrado, pruebe de nuevo.");
			}catch(InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				argsPartido = 0;
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
			}
			
		}while(argsPartido == 0);
		Equipo eLocal = partido.getEquipoByIdEquipoLocal();
		Equipo eVisit = partido.getEquipoByIdEquipoVisitante();
		
		//Cabecera
		System.out.println("=====================================\n");
		System.out.println("Local: " + eLocal.getNombre()+ " -- " + partido.getPuntosLocal() + " Puntos");
		System.out.println("Visitante: " + eVisit.getNombre()+ " -- " + partido.getPuntosVisitante() + " Puntos");
		System.out.println("\n=====================================\n");
		

		//Jugadores del equipo local que han participado en el partido
		
		Set<Jugador> jugadoresLocal = eLocal.getJugadors();
		
		System.out.println("Equipo Local:\n");
		for(Jugador x : jugadoresLocal) {
			Set<Datosjugadorpartido> datosTemp = x.getDatosjugadorpartidos();
			for(Datosjugadorpartido y : datosTemp) {
				if(y.getPartido().equals(partido)) {
					System.out.println(y.toString()+"\n");
				}
			}
		}
		
		//Jugadores del equipo visitante que han participado en el partido
		
		Set<Jugador> jugadoresVisit = eVisit.getJugadors();
		
		System.out.println("Equipo Visitante:\n");
		for(Jugador x : jugadoresVisit) {
			Set<Datosjugadorpartido> datosTemp = x.getDatosjugadorpartidos();
			for(Datosjugadorpartido y : datosTemp) {
				if(y.getPartido().equals(partido)) {
					System.out.println(y.toString()+"\n");
				}
			}
		}
		

		
		
	//FinMetodo	
	}
	
	/**
	 * Muestra todos los datos de los jugadores del equipo Valencia
	 * @param s
	 */
	public static void datosValencia(Session s) {
		session = s;
		String localidad = "Valencia";
		
		Query qLocalidad = s.createQuery("FROM Equipo WHERE localidad = :pLocalidad").setParameter("pLocalidad", localidad).setReadOnly(true);
		Equipo equipoTemp = (Equipo) qLocalidad.getSingleResult();
		
		System.out.println("Equipo: " + equipoTemp.getNombre()+"\n============================\n");
		
		//Se recogen los jugadores del equipo en un set para operar con ellos
		Set<Jugador> listaJugadores = equipoTemp.getJugadors();
		
		for(Jugador jTemp : listaJugadores) {
			Set<Datosjugadorpartido> setTemp = jTemp.getDatosjugadorpartidos();
			
			System.out.println("Jugador: " +jTemp.getNombre() + " " + jTemp.getApellidos());
			
			if(setTemp.isEmpty()) {
				System.out.println("Este jugador taodavia no ha jugado ningun partido.\n");
			}else {
				
			//Se guarada la suma de cada estadistica en las siguientes variables
				int valoracion = 0,puntos=0,asistencias=0,rebotes=0,tapones=0;
				for(Datosjugadorpartido x : setTemp) {
					
					valoracion += x.getValoracion();
					puntos += x.getPuntos();
					asistencias += x.getAsistencias();
					rebotes += x.getRebotes();
					tapones += x.getTapones();
					
				}
				//Muestra de estadisticas
				System.out.println("------------------------\n\t-Valoracion: " + valoracion + "\n\t-Puntos: " + puntos + "\n\t-Asistencias: " + asistencias + "\n\t-Rebotes: " + rebotes + "\n\t-Tapones: " + tapones + "\n------------------------\n");
			}
		}
		
		//finMetodo	
	}
	
	/**
	 * Se mostraran los 3 mejores jugadores ordenados por valoracion del equipo introducido por el usuario por consola
	 * @param s
	 */
	public static void mejoresJugadores(Session s) {
		session = s;
		Scanner sc = new Scanner(System.in);
		int idEquipo = 0;
		do {
			try {
				System.out.print("Introduce el ID del equipo: ");
				idEquipo = sc.nextInt();
				
				//Se pide el id del equipo y se realiza una consulta para almacenar posteriormente los jugadores
				Query qEquipo = s.createQuery("FROM Equipo WHERE idEquipo = :paramId").setParameter("paramId", idEquipo).setReadOnly(true);
				Equipo equipoTemp = (Equipo) qEquipo.getSingleResult();
				
				ArrayList<Jugador> tempJugadores = new ArrayList<Jugador>();
				tempJugadores.addAll(equipoTemp.getJugadors());
				//Se crea este hashMap para poder almacenar a los jugadores y su valoracion para posteriomente ordenar los 3 mejores
				Map<Jugador, Integer> jugadoresMap = new HashMap<Jugador, Integer>();
				
				for(Jugador jTemp : tempJugadores) {
					Set<Datosjugadorpartido> setTemp = jTemp.getDatosjugadorpartidos();
					if(!setTemp.isEmpty()) {
						
					//Se guarada la suma de cada estadistica en las siguientes variables
						int valoracion = 0;
						for(Datosjugadorpartido x : setTemp) {
							valoracion += x.getValoracion();
							
						}
						jugadoresMap.put(jTemp, valoracion);
					}
				}
				
				//Las siguientes lineas las he sacado del siguiente post en StackOverflow
				//https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values
				//Ademas de mis propias modificaciones
				
				Stream<Map.Entry<Jugador,Integer>> sorted =
					    jugadoresMap.entrySet().stream()
					       .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
					       .limit(3);
				
				System.out.println("Mejores Jugadores del equipo:");
				//Se muestran los datos ya ordenados
				sorted.forEach(t -> System.out.println(t.getKey().getNombre()+" -- Valoracion "+t.getValue()));
				
			}catch(NoResultException e) {
				//En caso de no encontrar a ningun partido se ejecutara la peticion de nuevo
				idEquipo = 0;
				System.out.println("Equipo no encontrado, pruebe de nuevo.");
			}catch(InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				idEquipo = 0;
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
			}
			
		}while(idEquipo==0);
		
		//Fin metodo
	}

	/**
	 * Se mostraran los datos de todos los partidos jugador por un jugador, separados por local y visitante.
	 * @param s
	 */
	public static void datosJugador(Session s) {
		session = s;
		
		Scanner sc = new Scanner(System.in);
		int idJugador = 0;
		do {
			try {
				//Pedimos al usuario el ID del jugador a consultar
				System.out.print("Introduce el id de un jugador: ");
				idJugador = sc.nextInt();
				//Realizamos la consulta y la guardamos en un objeto
				Query qJugador = session.createQuery("FROM Jugador WHERE idJugador = :paramID").setParameter("paramID", idJugador);
				Jugador jugadorTemp = (Jugador) qJugador.getSingleResult();
				
				//Almacenamos los datos de todos los partidos donde a participado el jugador
				Set<Datosjugadorpartido> sDatosPartidos = jugadorTemp.getDatosjugadorpartidos();
				
				//Muestreo de los datos de los partidos donde fue titular
				System.out.println("Partidos donde fue titular: \n");
				for(Datosjugadorpartido x : sDatosPartidos) {
					if(x.getTitular()) {
						System.out.println(x.toString()+"\n");
					}
				}
				
				System.out.println("-----------------------------------\n");
				
				//Muestreo de los datos de los partidos donde fue suplente
				System.out.println("Partidos donde fue suplente: \n");
				for(Datosjugadorpartido x : sDatosPartidos) {
					if(!x.getTitular()) {
						System.out.println(x.toString()+"\n");
					}
				}	
				
			}catch(NoResultException e) {
				//En caso de no encontrar a ningun partido se ejecutara la peticion de nuevo
				idJugador = 0;
				System.out.println("Jugador no encontrado, pruebe de nuevo.");
			}catch(InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				idJugador = 0;
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
			}
			
		}while(idJugador == 0);
		
		
		//Fin metodo
	}
	
	/**
	 * Para un equipo pedido por consola, se mostrara un resumen de sus datos en los partidos como locay y visitante
	 * @param s
	 */
	public static void estadisticasEquipo(Session s) {
		session = s;
		Scanner sc = new Scanner(System.in);
		
		int idEquipo = 0;
		
		do {
			try {
				System.out.print("Introduce la id del equipo: ");
				idEquipo = sc.nextInt();
				Query qEquipo = s.createQuery("FROM Equipo WHERE idEquipo = :paramEquipo").setParameter("paramEquipo", idEquipo).setReadOnly(true);
				
				Equipo equipoTemp = (Equipo) qEquipo.getSingleResult();
				List<Partido> partidos = new ArrayList<Partido>();
				partidos.addAll(equipoTemp.getPartidosForIdEquipoLocal());
				
				float puntosVisitante = 0, puntosLocal = 0, puntosFavor = 0, puntosContra = 0, ganados = 0;
				//Se comprueban todos sus partidos y se muestra el resumen de los partidos como local
				System.out.println("\nResumen Partidos como Local: \n");
				if(equipoTemp.getPartidosForIdEquipoLocal().isEmpty()) {
					System.out.println("Este equipo no a jugado como local");
				}else {
					for(Partido x : partidos) {
						puntosLocal+=x.getPuntosLocal();
						puntosFavor = x.getPuntosLocal()-x.getPuntosVisitante();
						if(puntosFavor<0) {
							puntosContra=puntosFavor*-1;
							puntosFavor = 0;
						}else {
							ganados++;
						}
					}
					System.out.println("Puntos: " + puntosLocal + "\nPuntos a favor: " + puntosFavor + "\nPuntos en contra: " + puntosContra + "\nPartidos Ganados: " + ganados);
					//Se vacia el arrayList
					partidos.removeAll(partidos);
				}
				//Se reinician los datos necesarios
				puntosFavor=0;
				puntosContra=0;
				ganados=0;
				//Se almacenan los datos de los partidos como visitante
				partidos.addAll(equipoTemp.getPartidosForIdEquipoVisitante());
				
				//Se calculan las estadisticas y se muestran
				System.out.println("\nResumen Partidos como Visitante: \n");
				if(equipoTemp.getPartidosForIdEquipoVisitante().isEmpty()) {
					System.out.println("Este equipo no a jugado como visitante");
				}else {
					for(Partido y : partidos) {
						puntosVisitante+=y.getPuntosVisitante();
						puntosLocal+= y.getPuntosVisitante();
						puntosFavor = y.getPuntosVisitante()-y.getPuntosLocal();
						if(puntosFavor<0) {
							puntosContra = puntosFavor*-1;
							puntosFavor = 0;
						}else {
							ganados++;
						}
					}
					System.out.println("Puntos: " + puntosVisitante + "\nPuntos a favor: " + puntosFavor + "\nPuntos en contra: " + puntosContra + "\nPartidos Ganados: " + ganados);
					partidos.removeAll(partidos);
				}
				
			} catch(NoResultException e) {
				//En caso de no encontrar a ningun partido se ejecutara la peticion de nuevo
				idEquipo = 0;
				System.out.println("Jugador no encontrado, pruebe de nuevo.");
			}catch(InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				idEquipo = 0;
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
			}
		}while(idEquipo==0);
	}
	
	/**
	 * Se mostrara un resumen de la liga de baloncesto, haciendo recuento de todos los partidos jugados por cada equipo, odenandolos por puntuacion (en caso de empate por diferencia de puntos) y se mostraran por pantalla de manera ordenada en una cuadricula
	 * @param s
	 */
	public static void clasificacionLiga(Session s) {
		int idEquipo = 1;
		ArrayList<HashMap<String, Integer>> listadoResultados = new ArrayList<HashMap<String,Integer>>();
		ArrayList<Equipo> listadoEquipos = new ArrayList<Equipo>();
		boolean antiWhileTrue = true;
		
		while(antiWhileTrue) {
			try {
				//Se itera por cada uno de los equipos y se guardan en una lista
				Query qEquipo = s.createQuery("FROM Equipo WHERE idEquipo = :paramEquipo").setParameter("paramEquipo", idEquipo).setReadOnly(true);
				Equipo equipoTemp = (Equipo) qEquipo.getSingleResult();
				listadoEquipos.add(equipoTemp);
				
				Set<Partido> partidosLocal = equipoTemp.getPartidosForIdEquipoLocal();
				Set<Partido> partidosVisitante = equipoTemp.getPartidosForIdEquipoVisitante();
				
				//Este mapa se crea para almacenar de manera individual cada dato correspondiente a las estadisticas de liga de cada equipo
				HashMap<String, Integer> resultadosEquipo = new HashMap<String, Integer>();
				
				//Inicializo las variables del hashMap para evitar errores
				resultadosEquipo.put("idEquipo", idEquipo);
				resultadosEquipo.put("puntos", 0);
				resultadosEquipo.put("PG", 0);
				resultadosEquipo.put("PP", 0);
				resultadosEquipo.put("pF", 0);
				resultadosEquipo.put("pC", 0);
				resultadosEquipo.put("pFL", 0);
				resultadosEquipo.put("pCL", 0);
				resultadosEquipo.put("pFV", 0);
				resultadosEquipo.put("pCV", 0);
				resultadosEquipo.put("dPT", 0);
				resultadosEquipo.put("dPTL", 0);
				resultadosEquipo.put("dPTV", 0);
				
				//Se guardan los datos de cada partido local en el mapa anterior
				for(Partido p : partidosLocal) {
					if(p.getPuntosLocal()>p.getPuntosVisitante()) {
						resultadosEquipo.put("puntos", resultadosEquipo.get("puntos")+2);
						resultadosEquipo.put("PG", resultadosEquipo.get("PG")+1);
						resultadosEquipo.put("pF", resultadosEquipo.get("pF") + p.getPuntosLocal() - p.getPuntosVisitante());
						resultadosEquipo.put("pFL", resultadosEquipo.get("pFL") + p.getPuntosLocal() - p.getPuntosVisitante());
					}else {
						resultadosEquipo.put("PP", resultadosEquipo.get("PP")+1);
						resultadosEquipo.put("pC", resultadosEquipo.get("pC") + p.getPuntosVisitante() - p.getPuntosLocal());
						resultadosEquipo.put("pCL", resultadosEquipo.get("pCL") + p.getPuntosVisitante() - p.getPuntosLocal());
					}
				}
				
				//Se guardan los datos de los partidos visitante en el mapa anterior
				for(Partido p : partidosVisitante) {
					if(p.getPuntosLocal()<p.getPuntosVisitante()) {
						resultadosEquipo.put("puntos", resultadosEquipo.get("puntos")+2);
						resultadosEquipo.put("PG", resultadosEquipo.get("PG")+1);
						resultadosEquipo.put("pF", resultadosEquipo.get("pF") + p.getPuntosVisitante() - p.getPuntosLocal());
						resultadosEquipo.put("pFV", resultadosEquipo.get("pFV") + p.getPuntosVisitante() - p.getPuntosLocal());
					}else {
						resultadosEquipo.put("PP", resultadosEquipo.get("PP")+1);
						resultadosEquipo.put("pC", resultadosEquipo.get("pC") + p.getPuntosLocal() - p.getPuntosVisitante());
						resultadosEquipo.put("pCV", resultadosEquipo.get("pCV") + p.getPuntosLocal() - p.getPuntosVisitante());
					}
				}
				int dPT = resultadosEquipo.get("pF") - resultadosEquipo.get("pC");
				int dPTL = resultadosEquipo.get("pFL") - resultadosEquipo.get("pCL");
				int dPTV = resultadosEquipo.get("pFV") - resultadosEquipo.get("pCV");
				resultadosEquipo.put("dPT", dPT);
				resultadosEquipo.put("dPTL", dPTL);
				resultadosEquipo.put("dPTV", dPTV);
				
				
				//Guardo las estadisticas en otra lista y avanzo el iterador
				listadoResultados.add(resultadosEquipo);
				idEquipo++;
				
			}catch (NoResultException e) {
				//Cuando salta esta excepcion significa que no hay mas equipos, por lo que el bucle se cierra
				antiWhileTrue = false;
			}
			
		}
		//Ordeno la lista de estadisticas primero por puntos y luego por diferencia de puntos
		//TODO diferencia de puntos
		listadoResultados.sort(Comparator
				.comparingInt(value -> ((HashMap<String, Integer>) value).get("puntos"))
				.reversed()
				.thenComparingInt(value -> ((HashMap<String, Integer>) value).get("dPT")));
		
		
		System.out.println("=======================================================================================================================");
		System.out.println("\t\t\t\t\t\tClasificacion de la liga");
		System.out.println("=======================================================================================================================\n");
		
		System.out.println("Equipo\t\t\tpuntos\tPG\tPP\tpF\tpC\tdPT\tpFL\tpCL\tdPTL\tpFV\tpCV\tdPTV");
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		//Se muestran las estadisticas ordenadas 
		for(HashMap<String, Integer> h : listadoResultados) {
			//Como hemos almacenado antes los equipos en una lista ordenada, podemos facilmente acceder a cualquiera de sus datos cogiendo el idEquipo almacenado en cada mapa de la listaResultados
			String nombreEquipo = listadoEquipos.get(h.get("idEquipo")-1).getNombre();
			if(nombreEquipo.length()<=6) {
				nombreEquipo = nombreEquipo + "\t\t";
			}else if(nombreEquipo.length()<=15){
				nombreEquipo = nombreEquipo + "\t";
			}
			//Finalmente se muestran los datos
			System.out.println(nombreEquipo + "\t" + h.get("puntos") + "\t" + h.get("PG") + "\t" + h.get("PP") + "\t" + h.get("pF") + "\t" + h.get("pC") + "\t" + h.get("dPT") + "\t" + h.get("pFL") + "\t" + h.get("pCL") + "\t" + h.get("dPTL") + "\t" + h.get("pFV") + "\t" + h.get("pCV") + "\t" + h.get("dPTV"));
			
			System.out.println();
		}
		
		//Fin de metodo
	}

}
