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
	
	public static void datosPartido(Session s) {
		session = s;
		
		//Selector de partido
		Scanner sc = new Scanner(System.in);
		int argsPartido = 0;
		Query qPartido = null;
		Partido partido = null;
		
		do {
			try {
				System.out.print("Introduce el id de un partido: ");
				argsPartido = sc.nextInt();
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
	
	public static void datosValencia(Session s) {
		//ACLARACION: en la poblacion de mi base de datos no existe un equipo en el Palencia, pero si
		//uno en el Valencia, usare este en su lugar
		session = s;
		String localidad = "Valencia";
		
		Query qLocalidad = s.createQuery("FROM Equipo WHERE localidad = :pLocalidad").setParameter("pLocalidad", localidad).setReadOnly(true);
		Equipo equipoTemp = (Equipo) qLocalidad.getSingleResult();
		System.out.println("Equipo: " + equipoTemp.getNombre()+"\n============================\n");
		
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
	
	public static void mejoresJugadores(Session s) {
		session = s;
		Scanner sc = new Scanner(System.in);
		int idEquipo = 0;
		do {
			try {
				System.out.print("Introduce el ID del equipo: ");
				idEquipo = sc.nextInt();
				
				Query qEquipo = s.createQuery("FROM Equipo WHERE idEquipo = :paramId").setParameter("paramId", idEquipo).setReadOnly(true);
				Equipo equipoTemp = (Equipo) qEquipo.getSingleResult();
				
				ArrayList<Jugador> tempJugadores = new ArrayList<Jugador>();
				tempJugadores.addAll(equipoTemp.getJugadors());
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
		
	}

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
		
		
		
	}
	
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
					partidos.removeAll(partidos);
				}
				puntosFavor=0;
				puntosContra=0;
				ganados=0;
				
				partidos.addAll(equipoTemp.getPartidosForIdEquipoVisitante());
				
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
			} catch (Exception e) {
				// TODO: handle exception
			}
		}while(idEquipo==0);
	}
	
	public static void clasificacionLiga(Session s) {
		//Queda TODO
	}
}
