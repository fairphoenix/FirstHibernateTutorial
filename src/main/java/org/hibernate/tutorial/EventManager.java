package org.hibernate.tutorial;

import org.hibernate.Session;
import org.hibernate.tutorial.domain.Event;
import org.hibernate.tutorial.domain.Person;
import org.hibernate.tutorial.util.HibernateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by anatoliy on 12.11.2014.
 */
public class EventManager {
    public static void main(String[] args) {
        EventManager mgr = new EventManager();

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Person person = (Person) session.load(Person.class, 1L);
        Set personEvents = person.getEvents();
        Event event = (Event) personEvents.iterator().next();
        person.getEvents().clear();
        session.getTransaction().commit();




//        mgr.addEmailToPerson(1L, "vpukin@gmail.com");

//        if (args[0].equals("store")) {
//            mgr.createAndStoreEvent("My Event", new Date());
//        } else if (args[0].equals("addpersontoevent")) {
//            Long eventId = mgr.createAndStoreEvent("My Event", new Date());
//            Long personId = mgr.createAndStorePerson("Foo", "Bar");
//            mgr.addPersonToEvent(personId, eventId);
//            System.out.println("Added person " + personId + " to event " + eventId);
//        }
//        HibernateUtil.getSessionFactory().close();
    }

    private long createAndStoreEvent(String title, Date theDate) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Event theEvent = new Event();
        theEvent.setTitle(title);
        theEvent.setDate(theDate);
        session.save(theEvent);

        session.getTransaction().commit();
        return theEvent.getId();
    }

    private long createAndStorePerson(String firstName, String lastName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Person person = new Person();
        person.setFirstname(firstName);
        person.setLastname(lastName);
        session.save(person);

        session.getTransaction().commit();
        return person.getId();
    }

    private List listEvents() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session.createQuery("from Event").list();
        session.getTransaction().commit();
        return result;
    }

    private void addPersonToEvent(Long personId, Long eventId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Person aPerson = (Person) session
                .createQuery("select p from Person p left join fetch p.events where p.id = :pid")
                .setParameter("pid", personId)
                .uniqueResult(); // Eager fetch the collection so we can use it detached
        Event anEvent = (Event) session.load(Event.class, eventId);

        session.getTransaction().commit();

        // End of first unit of work

        aPerson.getEvents().add(anEvent); // aPerson (and its collection) is detached

        // Begin second unit of work

        Session session2 = HibernateUtil.getSessionFactory().getCurrentSession();
        session2.beginTransaction();
        session2.update(aPerson); // Reattachment of aPerson

        session2.getTransaction().commit();
    }

    private void addEmailToPerson(Long personId, String emailAddress) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Person aPerson = (Person) session.load(Person.class, personId);
        // adding to the emailAddress collection might trigger a lazy load of the collection
        aPerson.getEmailAddresses().add(emailAddress);

        session.getTransaction().commit();
    }

}
