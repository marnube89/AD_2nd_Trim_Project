package classPkg;

import org.hibernate.cfg.Configuration;
import org.hibernate.loader.custom.ScalarResultColumnProcessor;

import java.util.InputMismatchException;
import java.util.Scanner;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import orm.*;

public class Main {
	private static final SessionFactory sessionFactory;
	private static final Session s;
	
	//Configuracion para crear la sesion mediante la cual nos conectamos a la base de datos
	static {
	    try {
	      sessionFactory = new Configuration().configure().buildSessionFactory();
	      s = sessionFactory.openSession();
	      System.out.println("Conexion exitosa!\n\n");
	    } catch (Throwable ex) {
	      System.err.println("Fallo en la creación de session Factory" + ex);
	      throw new ExceptionInInitializerError(ex);
	    }
	  }
	
	public static SessionFactory getSessionFactory() {
	    return sessionFactory;
	  }
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int opcion = -1;
		do {
			try {
				System.out.println("------------------------------------------\n"
						+ "Acciones disponibles:\n\n"
						+ "HIBERNATE\n"
						+ "1. Buscar datos mediante PK\n"
						+ "2. Datos jugador\n"
						+ "3. Datos equipo\n"
						+ "4. Insertar datos\n\n"
						+ "HQL\n"
						+ "5. Datos partido\n"
						+ "6. Datos del Valencia\n"
						+ "7. Mejores jugadores\n"
						+ "8. Datos jugador\n"
						+ "9. Estadisticas equipo\n"
						+ "10. Resultados de la liga\n\n"
						+ "XPATH/XQUERY\n"
						+ "11. Buscar consulta\n"
						+ "12. Informacion jugadores\n"
						+ "13. Informacion partidos\n"
						+ "14. Jugadores equipo\n"
						+ "15. Mejor jugador\n\n"
						+ "0. Salir\n"
						+ "------------------------------------------\n"
						+ "Selecciona una opcion: ");
				opcion = sc.nextInt();
				switch (opcion) {
				case 1: 
					MetodosHibernate.pkSearch(s);
					break;
				case 2: 
					MetodosHibernate.datosJugador(s);
					break;
				case 3: 
					MetodosHibernate.datosEquipo(s);
					break;
				case 4: 
					MetodosHibernate.anyInsert(s);
					break;
				case 5: 
					MetodosHQL.datosPartido(s);
					break;
				case 6: 
					MetodosHQL.datosValencia(s);
					break;
				case 7: 
					MetodosHQL.mejoresJugadores(s);
					break;
				case 8: 
					MetodosHQL.datosJugador(s);
					break;
				case 9: 
					MetodosHQL.estadisticasEquipo(s);
					break;
				case 10: 
					MetodosHQL.clasificacionLiga(s);
					break;
				case 11: 
					//La coleccion se cambiara dentro del codigo
					MetodosXPath.buscarConsulta("/ColeccionEquipoBaloncesto");
					break;
				case 12: 
					MetodosXPath.infoJugadores();
					break;
				case 13: 
					MetodosXPath.infoPartidos();
					break;
				case 14: 
					MetodosXPath.jugadoresEquipo();
					break;
				case 15: 
					MetodosXPath.obtenerJugadorValoracionMasAlta();
					break;
					
				default:
					break;
				}
			} catch (InputMismatchException e) {
				sc.next();
				System.out.println("Formato invalido pruebe de nuevo.");
			}
			
		}while(opcion!=0);
		System.out.println("Saliste con exito...");
	}

}
