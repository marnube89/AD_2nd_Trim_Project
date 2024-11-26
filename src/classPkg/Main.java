package classPkg;

import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;

public class Main {
	private static final SessionFactory sessionFactory;
	
	static {
	    try {
	      sessionFactory = new Configuration().configure().buildSessionFactory();
	      System.out.println("Conexion exitosa!");
	    } catch (Throwable ex) {
	      System.err.println("Fallo en la creación de session Factory" + ex);
	      throw new ExceptionInInitializerError(ex);
	    }
	  }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
