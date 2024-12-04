package classPkg;

import org.hibernate.cfg.Configuration;
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
		// TODO Auto-generated method stub
//		MetodosHibernate.pkSearch(s);
//		MetodosHibernate.datosJugador(s);
//		MetodosHibernate.datosEquipo(s);
//		MetodosHQL.datosPartido(s);
//		MetodosHQL.datosValencia(s);
//		MetodosHQL.mejoresJugadores(s);
//		MetodosHQL.estadisticasEquipo(s);
		

	}

}
