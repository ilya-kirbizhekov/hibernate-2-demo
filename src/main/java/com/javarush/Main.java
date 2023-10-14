package com.javarush;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.javarush.dao.*;
import com.javarush.domain.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.Configuration;
public class Main {

    private final ActorDAO actorDAO;
    private final AddressDAO addressDAO;
    private final CategoryDAO categoryDAO;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;
    private final CustomerDAO customerDAO;
    private final FilmDAO filmDAO;
    private final FilmTextDAO filmTextDAO;
    private final InventoryDAO inventoryDAO;
    private final LanguageDAO languageDAO;
    private final PaymentDAO paymentDAO;
    private final RentalDAO rentalDAO;
    private final StaffDAO staffDAO;
    private final StoreDAO storeDAO;
    private final SessionFactory sessionFactory;
    public Main()
    {
        Properties properties = new Properties();

        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.USER,"root");
        properties.put(Environment.PASS, "p@ssw0rd");
        properties.put(Environment.HBM2DDL_AUTO, "validate");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/movie");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");


        sessionFactory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(Actor.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Category.class)
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Feature.class)
                .addAnnotatedClass(Film.class)
                .addAnnotatedClass(FilmText.class)
                .addAnnotatedClass(Inventory.class)
                .addAnnotatedClass(Language.class)
                .addAnnotatedClass(Payment.class)
                .addAnnotatedClass(Rental.class)
                .addAnnotatedClass(Staff.class)
                .addAnnotatedClass(Store.class)
                .buildSessionFactory();

        actorDAO = new ActorDAO(sessionFactory);
        addressDAO = new AddressDAO(sessionFactory);
        categoryDAO = new CategoryDAO(sessionFactory);
        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);
        customerDAO = new CustomerDAO(sessionFactory);
        filmDAO = new FilmDAO(sessionFactory);
        filmTextDAO = new FilmTextDAO(sessionFactory);
        inventoryDAO = new InventoryDAO(sessionFactory);
        languageDAO = new LanguageDAO(sessionFactory);
        paymentDAO = new PaymentDAO(sessionFactory);
        rentalDAO = new RentalDAO(sessionFactory);
        staffDAO = new StaffDAO(sessionFactory);
        storeDAO = new StoreDAO(sessionFactory);
    }

    public static void main(String[] args) {

       Main main = new Main();

       Customer customer = main.createCustomer();
       main.customerReturnInventoryToStore();
       main.customerRentInventory(customer);
       main.newFilmWasMade();

    }

    private void newFilmWasMade() {

        try (Session session = sessionFactory.getCurrentSession()) {

            session.beginTransaction();

            Language language = languageDAO.getItems(0,20).stream().unordered().findAny().get();
            List<Category> categories = categoryDAO.getItems(0,5);

            List<Actor> actors = actorDAO.getItems(0,20);

            Film film = new Film();
            film.setActors(new HashSet<>(actors));
            film.setRating(Rating.R);
            film.setSpecialFeatures(Set.of(Feature.TRAILERS));
            film.setLength((short) 123);
            film.setReplacementCost(BigDecimal.TWO);
            film.setRentalRate(BigDecimal.TEN);
            film.setLanguage(language);
            film.setDescription("test");
            film.setTitle("scary");
            film.setRentalDuration((byte)44);
            film.setOriginalLanguage(language);
            film.setCategories(new HashSet<>(categories));
            film.setReleaseYear(Year.now());
            filmDAO.save(film);


            FilmText filmText = new FilmText();
            filmText.setFilm(film);
            filmText.setId((short) 5);
            filmText.setDescription("sss");
            filmText.setTitle("ffff");
            filmTextDAO.save(filmText);

            session.getTransaction().commit();

        }


    }

    private void customerRentInventory(Customer customer) {

        try (Session session = sessionFactory.getCurrentSession()) {

            session.beginTransaction();

            Film film = filmDAO.getFirstAvailableFilmForRent();
            Store store = storeDAO.getItems(0,1).get(0);

            Inventory inventory = new Inventory();
            inventory.setFilm(film);
            inventory.setStore(store);
            inventoryDAO.save(inventory);

            Staff staff = store.getStuff();

            Rental rental = new Rental();
            rental.setRentalDate(LocalDateTime.now());
            rental.setCustomer(customer);
            rental.setInventory(inventory);
            rental.setStaff(staff);
            rentalDAO.save(rental);

            Payment payment = new Payment();
            payment.setRental(rental);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setCustomer(customer);
            payment.setAmount(BigDecimal.valueOf(55.77));
            payment.setStaff(staff);
            paymentDAO.save(payment);

            session.getTransaction().commit();
        }

    }

    private Customer createCustomer() {

        try(Session session = sessionFactory.getCurrentSession()) {

            session.beginTransaction();

            Store store = storeDAO.getItems(0,1).get(0);
            City city = cityDAO.getbyName("Adana");


            Address address = new Address();
            address.setAddress("44 sigarlea"); //
            address.setDistrict("Manormlas"); //
            address.setPostalCode("3024"); //
            address.setPhone("+61452222"); //
            address.setCity(city); //
            addressDAO.save(address);

            Customer customer = new Customer();
            customer.setFirstName("Gordon1");
            customer.setLastName("Trak");
            customer.setEmail("info@gmail.com");
            customer.setAddress(address);
            customer.setActive(true);
            customer.setStore(store);
            customerDAO.save(customer);

            session.getTransaction().commit();
            return customer;

        }


    }

    public void customerReturnInventoryToStore() {
        try (Session session = sessionFactory.getCurrentSession()) {
          session.beginTransaction();

          Rental rental = rentalDAO.getAnyUnreturned();
          rental.setReturnDate(LocalDateTime.now());
          rentalDAO.save(rental);
          session.getTransaction().commit();

        }
    }




}