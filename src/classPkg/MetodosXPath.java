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
	public static void searchByQuery(String nomCol) {
		Scanner sc = new Scanner(System.in);
		try {
			//Carga los drivers
			Database dbDriver = (Database) Class.forName("org.exist.xmldb.DatabaseImpl").newInstance();
			DatabaseManager.registerDatabase(dbDriver);
			
			//Recupera la coleccion que le pasamos como dato y la almacena para poder hacer consultas
			Collection col = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc/db"+nomCol,"admin","");
			//Creamos un servicio mediante el cual haremos las consultas
			XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			
			//Peticion de datos
			System.out.println("Introduce tu consulta: ");
			//Almacenamos los datos recividos en un ResourceSet el cual posteriormente recorreremos y mostraremos el resultado
			ResourceSet result = servicio.query(sc.nextLine());
			ResourceIterator i = result.getIterator();
			
			//Si no encontramos datos se le avisa al usuario
			if(!i.hasMoreResources()) {
				System.out.println("No se encontraron datos");
			}
			while(i.hasMoreResources()) {
				Resource r = i.nextResource();
				System.out.println(r.getContent().toString());
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
		catch (XMLDBException e) {
			System.out.println("No se pudo realizar la consulta correctamente:");
			System.out.println(e.getCause());
		}
	}

}
