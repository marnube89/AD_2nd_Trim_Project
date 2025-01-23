/**
 * @author Mario Yusta
 */
package classPkg;


import java.util.Scanner;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

public class MetodosXPath {
	
	/**
	 * Pasandole el nombre de la coleccion a consultar, el usuario podra realizar cualquier consulta que escriba por consola, ya sea xPath o xQuery
	 * @param nomCol
	 */
	public static void buscarConsulta(String nomCol) {
		Scanner sc = new Scanner(System.in);
		//Peticion de datos
		System.out.println("Coleccion seleccionada: " + nomCol);
		System.out.println("Introduce tu consulta: ");
		String query = sc.nextLine();
		ejecutarConsulta(nomCol, query);
	}
	
	/**
	 * Mostrara a todos los jugadores de la coleccion ColeccionEquipoBaloncesto
	 */
	public static void infoJugadores() {
		ejecutarConsulta("/ColeccionEquipoBaloncesto", "/jugadores");
	}
	
	/**
	 * Mostrata todos los partidos jugados por el equipo que se pedira porconsola
	 */
	public static void infoPartidos() {
		Scanner sc = new Scanner(System.in);
		//Peticion de datos
		System.out.println("Introduce el id del equipo: ");
		String idEquipo = sc.nextLine();
		String query = "for $partido in /partidos/partido " 
				+ "where $partido/idEquipoLocal = " + idEquipo + " or $partido/idEquipoVisitante =  " + idEquipo + " "
				+ "return $partido";
		ejecutarConsulta("/ColeccionEquipoBaloncesto", query);
		
	}
	
	/**
	 * Se mostrara la informacion de todos los jugadores del equipo pedido por consola
	 */
	public static void jugadoresEquipo() {
		Scanner sc = new Scanner(System.in);
		//Peticion de datos
		System.out.println("Introduce el id del equipo: ");
		String idEquipo = sc.nextLine();
		String query = "for $jugador in /jugadores/jugador " 
				+ "where $jugador/idEquipo = " + idEquipo + " "
				+ "return $jugador";
		ejecutarConsulta("/ColeccionEquipoBaloncesto", query);
		
	}
	
	/**
	 * Mostrara al jugador conla mejor media de valoracion de DatosJugadorPartido
	 */
	public static void obtenerJugadorValoracionMasAlta() {
		String query = "let $datos :=\r\n"
				+ "    for $jugador in //jugadores/jugador\r\n"
				+ "    let $valoraciones := //DatosJugadoresPartidos/datosJugador[idJ = $jugador/idJugador]/valoracion\r\n"
				+ "    let $media := avg($valoraciones)  (: Asumiendo que las valoraciones son numéricas :)\r\n"
				+ "    return \r\n"
				+ "        <datos>\r\n"
				+ "            <jugador>{$jugador/idJugador}</jugador>\r\n"
				+ "            <media>{$media}</media>\r\n"
				+ "        </datos>\r\n"
				+ "let $maxMedia := max(for $dato in $datos return xs:double($dato/media))\r\n"
				+ "return \r\n"
				+ "    for $dato in $datos\r\n"
				+ "    where xs:double($dato/media) = $maxMedia\r\n"
				+ "    return //jugadores/jugador[idJugador = $dato/jugador]";
		ejecutarConsulta("/ColeccionEquipoBaloncesto", query);
		
	}

	/**
	 * Metodo auxiliar que recibira una coleccion y una consulta para despues mostrar su resultado por consola
	 * @param nomCol nombre de la coleccion a consultar
	 * @param query consulta, puede ser xPath o xQuery
	 */
	private static void ejecutarConsulta(String nomCol, String query) {
		try {
			//Carga los drivers
			Database dbDriver = (Database) Class.forName("org.exist.xmldb.DatabaseImpl").newInstance();
			DatabaseManager.registerDatabase(dbDriver);
			
			//Recupera la coleccion que le pasamos como dato y la almacena para poder hacer consultas
			Collection col = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc/db"+nomCol,"admin","");
			//Creamos un servicio mediante el cual haremos las consultas
			XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			
			//Almacenamos los datos recividos en un ResourceSet el cual posteriormente recorreremos y mostraremos el resultado
			ResourceSet result = servicio.query(query);
			ResourceIterator i = result.getIterator();
			
			//Si no encontramos datos se le avisa al usuario
			if(!i.hasMoreResources()) {
				System.out.println("No se encontraron datos");
			}
			while(i.hasMoreResources()) {
				Resource r = i.nextResource();
				System.out.println(r.getContent().toString()+"\n");
			}
		
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(NullPointerException e) {
			System.out.println("La coleccion introducida no existe:");
		}
		catch (XMLDBException e) {
			System.out.println("No se pudo realizar la consulta correctamente:");
			System.out.println(e.getCause());
		}
	}

}
