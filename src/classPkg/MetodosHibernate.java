package classPkg;

import java.util.List;
import java.util.Scanner;

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
			System.out.println("Introduce la ID de un jugador:");
			idJ = sc.nextInt();
			Jugador jTemp = s.load(Jugador.class, idJ);
			System.out.println(jTemp.toString());
			
			//Estadisticas
			System.out.println("Estadisticas:");

			CriteriaBuilder cb = s.getCriteriaBuilder();
			CriteriaQuery<Datosjugadorpartido> filtro2 = cb.createQuery(Datosjugadorpartido.class);
			Root<Datosjugadorpartido> root = filtro2.from(Datosjugadorpartido.class);
			filtro2.select(root).where(cb.equal(root.get("idJ"), jTemp.getIdJugador()));
			
			Query q = s.createQuery(filtro2);
			
			List<Datosjugadorpartido> l = q.getResultList();
			
			for(Datosjugadorpartido x : l) {
				System.out.println(x.getPuntos());
			}

			
			
			try {
				
			}catch(ObjectNotFoundException e) {
				System.out.println("Jugador no encontrado, pruebe de nuevo.");
			}
			
		}while(idJ == 0);
	}
	
	public static void datosEquipo(String id, Session s) {
		
	}
	
	public static void anyInsert(Object datos, Session s) {
		
	}
}
