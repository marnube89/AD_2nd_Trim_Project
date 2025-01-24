/**
 * @author Mario Yusta
 */
package classPkg;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import orm.*;

public class MetodosHibernate {
	private static Session session;
	
	/**
	 * Metodo que busca cualquier dato de la tabla seleccionada por el usuario pasando la PK de dicha tabla
	 * @param s
	 */
	public static void pkSearch(Session s){
		session = s;
		
		//Temporal
		String info = ""; //Representara el nombre de la tabla al mostrar los datos
		Class infoCls = null; //Informara de la clase a la que se mapeara el objeto
		String opcion="";
		Scanner sc = new Scanner(System.in);
		do {
			//Dialogo opciones
			System.out.println("Tablas disponibles:"
					+ "\n1. Equipo"
					+ "\n2. Jugador"
					+ "\n3. Partido"
					+ "\n4. Datos Jugador Partido"
					+ "\n\ns. Salir");
			
			//Dialogo seleccion
			System.out.println("Selecciona una tabla:");
			opcion = sc.next().toLowerCase();
			switch (opcion) {
			case "1": //Equipo
				info = "equipo";
				infoCls = Equipo.class;
				System.out.println(searchAux(sc, s, info, infoCls).toString());
				break;
			case "2": //Jugador
				info = "jugador";
				infoCls = Jugador.class;
				System.out.println(searchAux(sc, s, info, infoCls).toString());
				break;
			case "3": //Partido
				info = "partido";
				infoCls = Partido.class;
				System.out.println(searchAux(sc, s, info, infoCls).toString());
				break;
			case "4": //DatosJugadorPartido TODO implementar seleccion de PK multiple
				boolean found = false;
				Datosjugadorpartido oTemp = null;
				do {
					int idP, idJ;
					try {
						System.out.println("Introduce la id del Partido:");
						idP = sc.nextInt();
						System.out.println("Introduce la id del Jugador:");
						idJ = sc.nextInt();
						DatosjugadorpartidoId temp = new DatosjugadorpartidoId(idJ, idP);
						oTemp = s.load(Datosjugadorpartido.class, temp);
						System.out.println("\nDATOS DEL JUGADOR:\n"+oTemp.toString());;
						
						found = true;

					}catch(ObjectNotFoundException e) {
						System.out.println("Datos no encontrados.");
					}catch(InputMismatchException ex) {
						//Igual que antes, pero se comprueba si el dato introducido no es un int
						System.out.println("Formato incorrecto pruebe de nuevo.");
						//Limpia el buffer recogiendo el dato incorrecto
						sc.nextLine();
					}
				}while(!found);
				break;
			case "s":
				System.out.println("Saliste con exito");
				break;
			default:
				System.out.println("\n------------------\n"
						+ "Opcion invalida prueba de nuevo."
						+ "\n------------------");
				opcion = "";
			}
		}while(opcion.isEmpty());
	}
	
	
	/**
	 * Realiza una consulta que se guarda en un objeto generico el cual puede ser casteado a la clase de la tabla que queramos usar. El bucle de ejecucion del metodo se repetira hasta encontrar una coincidencia
	 * @param sc
	 * @param s
	 * @param info
	 * @param infoCls
	 * @return
	 */
	private static Object searchAux(Scanner sc, Session s, String info, Class infoCls) {
		boolean found = false;
		Object oTemp = null;
		do {
			System.out.println("Introduce la id del "+info+":");
			int pkTemp;
			try {
				pkTemp = sc.nextInt();
				oTemp = s.load(infoCls, pkTemp);
				//Cuando no encuentra nada, llena un objeto con datos null, al hacer el toString fuerzo el ObjectNotFoundException
				oTemp.toString();
				
				found = true;

			}catch(ObjectNotFoundException e) {
				System.out.println("La id introducida no corresponde a ningun "+info+".");
			}catch(InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
			}
		}while(!found);
		
		return oTemp;
	}
	
	/**
	 * Haciendo uso de searchAux(), busca y mustra los datos generales de un jugador ademas de todas sus estadisticas
	 * @param s
	 */
	public static void datosJugador(Session s) {
		int idJ = 0;
		Scanner sc = new Scanner(System.in);
		
		Jugador jTemp = (Jugador) searchAux(sc, s, "jugador", Jugador.class);
		
		//Muestro los datos del jugador
		System.out.println("\n------------------------\nDatos:\n\t"+jTemp.toString()+"\n------------------------");
		
		//Estadisticas
		System.out.println("\n------------------------\nEstadisticas:\n");
		
		//Lectura de entradas de datosjugadorpartido a la que pertenece el jugador seleccionado
		Set<Datosjugadorpartido> setTemp = jTemp.getDatosjugadorpartidos();
		if(setTemp.isEmpty()) {
			System.out.println("Este jugador taodavia no ha jugado ningun partido.");
		}else {
			//Se muestran los datos
			ArrayList<Datosjugadorpartido> list = new ArrayList<Datosjugadorpartido>();
			list.addAll(setTemp);
			
			//El siguiente metodo sort hace uso de metodos concatenados de la clase comparator, los cuales ordenan los datos segun los criterios dictados, con el orden de prioridad con el que fueron sentenciados por codigo
			list.sort(Comparator.comparingDouble(value -> ((Datosjugadorpartido) value).getValoracion())
					.thenComparingInt(value -> ((Datosjugadorpartido) value).getPuntos()).reversed()
					.thenComparingInt(value -> ((Datosjugadorpartido) value).getAsistencias()).reversed()
					.thenComparingInt(value -> ((Datosjugadorpartido) value).getRebotes()).reversed()
					);
			for(Datosjugadorpartido x : list) {
				System.out.println(x.toString()+ "\n");
			}
			System.out.println("------------------------\n");

		}
	}
	
	/**
	 * Haciendo uso del metodo searchAux(), se buscan y muestran los datos del equipo y un listado con los datos de sus jugadores
	 * @param s
	 */
	public static void datosEquipo(Session s) {
		int idEquipo = 0;
		Scanner sc = new Scanner(System.in);
		
		//Busqueda
		Equipo eTemp = (Equipo) searchAux(sc, s, "equipo", Equipo.class);
		
		//Datos equipo
		System.out.println("Datos del equipo:\n"+eTemp.toString());
		
		//Datos jugador y ordenacion
		System.out.println("Jugadores del equipo:");
		List<Jugador> jSet = new ArrayList<Jugador>();
		jSet.addAll(eTemp.getJugadors());
		jSet.sort(new Comparator<Jugador>() {
			@Override
			public int compare(Jugador o1, Jugador o2) {
				if(o1.getAlturaCm()>o2.getAlturaCm()) {
					return -1;
				}else {
					return 1;
				}
			}
		});
		//Muestreo jugadores
		for(Jugador x : jSet) {
			System.out.println("->" + x.getNombre() + " - " + x.getAlturaCm() + "cm");
		}

	}
	/**
	 * Este metodo hace uso de otros metodos auxiliares, cada cual relacionado con el objeto de datos relacional correspondiente. Este mostrara al usuario un listado de opciones disponibles para crear una entrada en la BBDD.
	 * @param s
	 */
	public static void anyInsert(Session s) {
		Scanner sc =  new Scanner(System.in);
		String opcion = "";
		System.out.println("Elige que entrada deseas crear:\n"
				+ "1. Jugador\n"
				+ "2. Equipo\n"
				+ "3. Partido\n"
				+ "4. DatosJugadorPartido");
		
		do {
			opcion = sc.next();
			switch (opcion) {
			case "1":
				s.save(createJugador(s));
				s.beginTransaction().commit();
				break;
			case "2":
				s.save(createEquipo(s));
				s.beginTransaction().commit();
				break;
			case "3":
				s.save(createPartido(s));
				s.beginTransaction().commit();
				break;
			case "4":
				try {
					s.save(createDatosjugador(s));
					s.beginTransaction().commit();
				} catch (PersistenceException e) {
					System.out.println("IDs invalidas, no se pudieron agreagar los datos");
				}
				break;
			default:
				System.out.println("Opcion invalida pruebe de nuevo");
				opcion = "";
			}
		}while(opcion.isEmpty());
	}
	
	
	//Metodos auxiliares para la creacion de datos
	
	/**
	 * Recibira los datos necesarios para crear un equipo por consola y debolvera un objeto Equipo
	 * @param s
	 * @return
	 */
	private static Equipo createEquipo(Session s) {
		Scanner sc = new Scanner(System.in);
		int idEquipo = 0;
		String nombre, localidad, pais, nombreCorto;
		
		System.out.println("Introduce el nombre del equipo: ");
		nombre = sc.nextLine();
		System.out.println("Introduce la localidad del equipo: ");
		localidad = sc.nextLine();
		System.out.println("Introduce el pais del equipo: ");
		pais = sc.nextLine();
		System.out.println("Introduce el nombre corto del equipo: ");
		nombreCorto = sc.nextLine();
		
		boolean breakPoint = false;
		do {
			idEquipo++;
			try {
				//Con el toString() forzamos la excepcion
				s.load(Equipo.class, idEquipo).toString();
			} catch (ObjectNotFoundException e) {
				// TODO: handle exception
				breakPoint = true;
			}
		} while (breakPoint == false);
		
		Equipo nuevoEquipo = new Equipo(idEquipo, nombre, localidad, pais, nombreCorto, new HashSet(0) , new HashSet(0), new HashSet(0));

		
		return nuevoEquipo;
		
	}
	
	/**
	 * Recibira los datos necesarios por consola para crear un objeto Jugador, ademas si se desease crear un equipo nuevo para este jugador, se le pregunta al usuario para acceder al metodo createEquipo(). Devuelve un objeto Jugador
	 * @param s
	 * @return
	 */
	private static Jugador createJugador(Session s) {
		Scanner sc = new Scanner(System.in);
		int idEquipo = 0;
		int idJugador = 0, alturaCM, edad;
		String nombre, apellidos, nacionalidad;
		
		System.out.println("Introduce el nombre del jugador: ");
		nombre = sc.nextLine();
		System.out.println("Introduce los apellidos del jugador: ");
		apellidos = sc.nextLine();
		
		do {
			try {
				System.out.println("Introduce la altura en cm del jugador: ");
				alturaCM = sc.nextInt();
			} catch (InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
				alturaCM = -1;
			} 
		} while (alturaCM<0);
		
		do {
			try {
				System.out.println("Introduce la edad del jugador: ");
				edad = sc.nextInt();
			} catch (InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
				edad = -1;
			} 
		} while (edad<0);
		
		System.out.println("Introduce la nacionalidad del jugador: ");
		nacionalidad = sc.next();
		
		boolean breakPoint = false;
		do {
			idJugador++;
			try {
				//Con el toString() forzamos la excepcion
				s.load(Jugador.class, idJugador).toString();
			} catch (ObjectNotFoundException e) {
				// TODO: handle exception
				breakPoint = true;
			}
		} while (breakPoint == false);
		Jugador jugadorNuevo = null;
		Equipo equipo = null;
		System.out.println("Deseas crear un equipo nuevo? En caso negativo se buscara mediante ID (s/n)");
		boolean formatoCorrecto = true;
		String respuesta = "";
		
		do {
			respuesta = sc.next();
			if (respuesta.toLowerCase().equals("s") || respuesta.toLowerCase().equals("n")) {
				if (respuesta.toLowerCase().equals("n")) {
					System.out.println("a");
					equipo = (Equipo) searchAux(sc, s, "equipo", Equipo.class);
				} else {
					equipo = createEquipo(s);
					s.save(equipo);
				}
				formatoCorrecto = true;
			} else {
				System.out.println("Respuesta invalida pruebe de nuevo");
				formatoCorrecto = false;
			} 
		} while (!formatoCorrecto);
		
		jugadorNuevo = new Jugador(idJugador, equipo, nombre, apellidos, alturaCM, edad, nacionalidad,new HashSet(0));
		return jugadorNuevo;
	}
	
	/**
	 * Crea un objeto Partido a partir de los datos introducidos por consola. Devuelve un objeto Partido
	 * @param s
	 * @return
	 */
	private static Partido createPartido(Session s) {
		Scanner sc = new Scanner(System.in);
		int idPartido = 0, idEquipoLocal, idEquipoVisitante, puntosLocal = 0, puntosVisitante = 0;
		boolean breakPoint = false;
		do {
			idPartido++;
			try {
				//Con el toString() forzamos la excepcion
				s.load(Partido.class, idPartido).toString();
			} catch (ObjectNotFoundException e) {
				// TODO: handle exception
				breakPoint = true;
			}
		} while (breakPoint == false);
		
		Equipo local = null;
		do {
			try {
				System.out.println("Introduce la ID del equipo local: ");
				idEquipoLocal = sc.nextInt();

				local = s.load(Equipo.class, idEquipoLocal);
				local.toString(); //Forzar excepcion

			} catch (ObjectNotFoundException e) {
				System.out.println("No se ha encontrado ese equipo\n");
				local =  null;
			} 
		} while (local==null);

		Equipo visitante = null;
		do {
			try {
				System.out.println("Introduce la ID del equipo Visitante: ");
				idEquipoVisitante = sc.nextInt();

				visitante = s.load(Equipo.class, idEquipoVisitante);
				visitante.toString(); //Forzar excepcion

			} catch (ObjectNotFoundException e) {
				System.out.println("No se ha encontrado ese equipo\n");
				visitante =  null;
			} 
		} while (visitante==null);
		
		do {
			try {
				System.out.println("Introduce los puntos del equipo local: ");
				puntosLocal = sc.nextInt();
			} catch (InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
				puntosLocal = -1;
			} 
		} while (puntosLocal<0);
		
		do {
			try {
				System.out.println("Introduce los puntos del equipo visitante: ");
				puntosVisitante = sc.nextInt();
			} catch (InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
				puntosVisitante = -1;
			} 
		} while (puntosVisitante<0);
		Partido nuevoPartido = new Partido(idPartido, visitante, local, puntosLocal, puntosVisitante, new HashSet(0));
		return nuevoPartido;
		
	}
	
	/**
	 * Crea un objeto tipo Datosjugadorpartido a partir de los datos pedidos por consola. Devuelve un objeto Datosjugadorpartido
	 * @param s
	 * @return
	 */
	private static Datosjugadorpartido createDatosjugador(Session s) {
		Scanner sc = new Scanner(System.in);
		boolean breakPoint = false;
		
		Jugador jugador = (Jugador) searchAux(sc, s, "jugador", Jugador.class);
		Partido partido = (Partido) searchAux(sc, s, "Partido", Partido.class);
		
		
		double valoracion = 0;
		do {
			try {
				System.out.println("Introduce la valoracion: ");
				valoracion = sc.nextInt();
			} catch (InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
				valoracion = -1;
			} 
		} while (valoracion<0);
		
		
		int puntos = 0;
		do {
			try {
				System.out.println("Introduce los puntos: ");
				puntos = sc.nextInt();
			} catch (InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
				puntos = -1;
			} 
		} while (puntos<0);
		
		
		int asistencias = 0;
		do {
			try {
				System.out.println("Introduce las asistencias: ");
				asistencias = sc.nextInt();
			} catch (InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
				asistencias = -1;
			} 
		} while (asistencias<0);
		
		
		int rebotes = 0;
		do {
			try {
				System.out.println("Introduce los rebotes: ");
				rebotes = sc.nextInt();
			} catch (InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
				rebotes = -1;
			} 
		} while (rebotes<0);
		
		
		int tapones = 0;
		do {
			try {
				System.out.println("Introduce los tapones: ");
				tapones = sc.nextInt();
			} catch (InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
				tapones = -1;
			} 
		} while (tapones<0);
		
		String titular = "";
		boolean fueTitular = false;
		do {
			System.out.println("Fue titular? (s/n): ");
			titular = sc.next();
			if(titular.toLowerCase().equals("s") || titular.toLowerCase().equals("n")) {
				if(titular.toLowerCase().equals("s")) {
					fueTitular = true;
				}
			}else {
				System.out.println("Valor erroneo pruebe de nuevo");
				titular = "";
			}
		} while (titular.isEmpty());
		
		DatosjugadorpartidoId datosId = new DatosjugadorpartidoId(jugador.getIdJugador(), partido.getIdPartido());
		Datosjugadorpartido datos = new Datosjugadorpartido(datosId, jugador, partido, valoracion, puntos, asistencias, rebotes, tapones, fueTitular);
		return datos;
	}
}
