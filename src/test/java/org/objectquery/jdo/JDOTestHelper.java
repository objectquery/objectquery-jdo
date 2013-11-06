package org.objectquery.jdo;

import java.io.File;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.objectquery.jdo.domain.Dog;
import org.objectquery.jdo.domain.Home;
import org.objectquery.jdo.domain.Home.HomeType;
import org.objectquery.jdo.domain.Person;

public class JDOTestHelper {
	private static PersistenceManagerFactory factory;

	private static void initData() {
		PersistenceManager persistenceManager;
		persistenceManager = factory.getPersistenceManager();

		persistenceManager.currentTransaction().begin();

		Home tomHome = new Home();
		tomHome.setAddress("homeless");
		tomHome.setType(HomeType.HOUSE);
		tomHome = persistenceManager.makePersistent(tomHome);

		Person tom = new Person();
		tom.setName("tom");
		tom.setHome(tomHome);
		tom = persistenceManager.makePersistent(tom);

		Home dudHome = new Home();
		dudHome.setAddress("moon");
		dudHome.setType(HomeType.HOUSE);
		dudHome = persistenceManager.makePersistent(dudHome);

		Person tomDud = new Person();
		tomDud.setName("tomdud");
		tomDud.setHome(dudHome);
		tomDud = persistenceManager.makePersistent(tomDud);

		Person tomMum = new Person();
		tomMum.setName("tommum");
		tomMum.setHome(dudHome);

		tomMum = persistenceManager.makePersistent(tomMum);

		Home dogHome = new Home();
		dogHome.setAddress("royal palace");
		dogHome.setType(HomeType.KENNEL);
		dogHome.setPrice(1000000);
		dogHome.setWeight(30);

		dogHome = persistenceManager.makePersistent(dogHome);

		Dog tomDog = new Dog();
		tomDog.setName("cerberus");
		tomDog.setOwner(tom);
		tomDog.setHome(dogHome);
		tomDog = persistenceManager.makePersistent(tomDog);

		tom.setDud(tomDud);
		tom.setMum(tomMum);
		tom.setDog(tomDog);
		persistenceManager.makePersistent(tom);
		persistenceManager.currentTransaction().commit();
		persistenceManager.close();

	}

	public static PersistenceManagerFactory getFactory() {
		if (factory == null) {
			File toDelete = new File("test.dbo");
			if (toDelete.exists())
				toDelete.delete();
			factory = JDOHelper.getPersistenceManagerFactory("test");
			initData();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					factory.close();
					File toDelete = new File("test.dbo");
					if (toDelete.exists())
						toDelete.delete();
				}
			});
		}
		return factory;
	}
}
