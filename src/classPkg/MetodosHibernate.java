package classPkg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import orm.*;

public class MetodosHibernate {
	private static Session session;
	
	//Queda por saber si esta sera la implementacion definitiva, a la espera
	// de la respuesta de Miguel
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
				searchAux(sc, s, info, infoCls);
				break;
			case "2": //Jugador
				info = "jugador";
				infoCls = Jugador.class;
				searchAux(sc, s, info, infoCls);
				break;
			case "3": //Partido
				info = "partido";
				infoCls = Partido.class;
				searchAux(sc, s, info, infoCls);
				break;
			case "4": //DatosJugadorPartido TODO implementar seleccion de PK multiple
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
		sc.close();
	}
	
	
	//Este metodo se ejecutara constantemente hasta encontrar una coincidencia dentro de la BBDD.
	
	//Realiza una consulta que se guarda en un objeto temporal el cual es casteado a la clase de
	// la tabla que deseemos usar, mostrando posteriormente sus datos por consola.
	private static void searchAux(Scanner sc, Session s, String info, Class infoCls) {
		boolean found = false;
		Object oTemp = null;
		do {
			System.out.println("Introduce la id del "+info+":");
			int pkTemp = sc.nextInt();
			try {
				oTemp = s.load(infoCls, pkTemp);
				
				//Se realiza un casteo a la clase pasada como parametro para obtener la implementacion
				// del toString de la clase deseada.
				System.out.println(infoCls.cast(oTemp).toString());
				found = true;
			}catch(ObjectNotFoundException e) {
				System.out.println("La id introducida no corresponde a ningun "+info+".");
			}
		}while(!found);
		
		//Se elimina el objeto para ahorrar espacion en memoria
		oTemp = null;
	}
	
	public static void datosJugador(Session s) {
		int idJ = 0;
		Scanner sc = new Scanner(System.in);
		
		do {
			try {
				System.out.print("Introduce la ID de un jugador: ");
				idJ = sc.nextInt();
				Jugador jTemp = s.load(Jugador.class, idJ);
				
				System.out.println("\n------------------------\nDatos:\n\t"+jTemp.toString()+"\n------------------------");
				
			//Estadisticas
				System.out.println("\n------------------------\nEstadisticas:");
				
			//Lectura de entradas de datosjugadorpartido a la que pertenece el jugador seleccionado
				Set<Datosjugadorpartido> setTemp = jTemp.getDatosjugadorpartidos();
				if(setTemp.isEmpty()) {
					System.out.println("Este jugador taodavia no ha jugado ningun partido.");
				}else {
					
				//Se guarada la suma de cada estadistica en las siguientes variables
					int valoracion = 0,puntos=0,asistencias=0,rebotes=0,tapones=0,vecesTitular=0;
					for(Datosjugadorpartido x : setTemp) {
						
						valoracion += x.getValoracion();
						puntos += x.getPuntos();
						asistencias += x.getAsistencias();
						rebotes += x.getRebotes();
						tapones += x.getTapones();
						
						//Se cuenta cuantas veces este jugador a sido titular
						if(x.getTitular()) {
							vecesTitular++;
						}
					}
					//Muestra de estadisticas
					System.out.println("\t-Valoracion: " + valoracion + "\n\t-Puntos: " + puntos + "\n\t-Asistencias: " + asistencias + "\n\t-Rebotes: " + rebotes + "\n\t-Tapones: " + tapones + "\n\t-Veces Titular: " + vecesTitular + "\n------------------------");
				}
			}catch(ObjectNotFoundException e) {
				//En caso de no encontrar a ningun jugador se ejecutara la peticion de nuevo
				idJ = 0;
				System.out.println("Jugador no encontrado, pruebe de nuevo.");
			}catch(InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				idJ = 0;
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
			}
			
		}while(idJ == 0);
	}
	
	public static void datosEquipo(Session s) {
		int idEquipo = 0;
		Scanner sc = new Scanner(System.in);
		do {
			try {
				//Busqueda
				System.out.print("Introduce la id del equipo a consultar:");
				idEquipo = sc.nextInt();
				Equipo eTemp = s.load(Equipo.class, idEquipo);
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
							return 1;
						}else {
							return 0;
						}
					}
				});
				//Muestreo jugadores
				for(Jugador x : jSet) {
					System.out.println("->" + x.getNombre() + " - " + x.getAlturaCm() + "cm");
				}
			}catch(ObjectNotFoundException e) {
				idEquipo = 0;
				System.out.println("Equipo no encontrado pruebe de nuevo:");
			}catch(InputMismatchException ex) {
				//Igual que antes, pero se comprueba si el dato introducido no es un int
				idEquipo = 0;
				System.out.println("Formato incorrecto pruebe de nuevo.");
				//Limpia el buffer recogiendo el dato incorrecto
				sc.nextLine();
			}
		}while(idEquipo==0);
	}
	
	public static void anyInsert(Object datos, Session s) {
		
	}
}
