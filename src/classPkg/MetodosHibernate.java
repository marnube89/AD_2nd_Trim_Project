package classPkg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;

import orm.*;

public class MetodosHibernate {
	private static Session session;
	
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
	
	public static void datosJugador(Session s) {
		int idJ = 0;
		Scanner sc = new Scanner(System.in);
		
		Jugador jTemp = (Jugador) searchAux(sc, s, "jugador", Jugador.class);
		
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
