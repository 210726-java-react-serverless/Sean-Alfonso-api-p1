package com.services;
import com.dto.ClassDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.*;



import com.util.MongoClientFactory;
import com.util.exceptions.DataSourceException;
import com.util.exceptions.InvalidRequestException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class RegistrationCatalog {

   // private final Logger logger = LogManager.getLogger(StudentDashboard.class);
    private String className;
    private int classSize;
    private List<String> students;
    private ObjectMapper mapper = new ObjectMapper();

    public RegistrationCatalog(String className, int classSize){
        this.className = className;
        this.classSize = classSize;
    }
    //this is a test comment
    public RegistrationCatalog(String className, String students){
        this.className = className;
        this.students.add(students);
    }
    public RegistrationCatalog(){
        super();
    }

    public RegistrationCatalog(String className){
        this.className = className;
    }


    public RegistrationCatalog save(ClassDetails classDetails, String className) {

        //setting class details class name to the class name provided to this method to later retrieve this class details object
        classDetails.setClassName(className);
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection(); //connect to mongoDB

            MongoDatabase classDb = mongoClient.getDatabase("classes");
            //sets db to classes. all class names and student rosters exist here
            try{
                classDb.createCollection(className); //create new collection with class name

                //inserting a new class detail pojo in order to proceed class data not related too students
                MongoCollection<Document> usersCollection = classDb.getCollection(className);
                Document newUserDoc = new Document("classSize", classDetails.getClassSize())
                                           .append("className", classDetails.getClassName())
                                           .append("open", classDetails.isOpen())
                                           .append("registrationTime" , classDetails.getRegistrationTime())
                                           .append("meetingPeriod" , classDetails.getMeetingPeriod());
                usersCollection.insertOne(newUserDoc);
            } catch (Exception e){
              //  logger.error(e.getMessage());
                System.out.println("Class already exists!");
            }

         return null;

        } catch (Exception e) {
           // logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    public boolean UpdateFull(ClassDetails classDetails)
    {
        if(classDetails == null)
        {
            throw new InvalidRequestException("Cannot Search with null resource");
        }
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase classDb = mongoClient.getDatabase("classes");
            Document courseDescription = new Document("className" , classDetails.getClassName());
            Document authCourseDoc = classDb.getCollection(classDetails.getClassName())
                                            .findOneAndUpdate(courseDescription , new Document("classSize", classDetails.getClassSize())
                                                                                        .append("className", classDetails.getClassName())
                                                                                        .append("open", classDetails.isOpen())
                                                                                        .append("registrationTime" , classDetails.getRegistrationTime())
                                                                                        .append("meetingPeriod" , classDetails.getMeetingPeriod()));

           if(authCourseDoc == null)
               return false;



        }catch (Exception e)
        {

        }
        return true;
    }

    public boolean UpdateClassSize(String className , int classSize)
    {
        if(className == null || classSize < 0 )
        {
            throw new InvalidRequestException("Cannot Search with null resource");
        }

        if(classSize < 10)
        {
            classSize = 24;
        }

        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase classDb = mongoClient.getDatabase("classes");
            Document courseDescription = new Document("className" , className);
            Document authCourseDoc = classDb.getCollection(className)
                    .findOneAndUpdate(courseDescription , new Document("classSize", classSize));

            if(authCourseDoc == null)
                return false;



        }catch (Exception e)
        {

        }
        return true;
    }

    public boolean UpdateClassStatus(String className , boolean open)
    {
        if(className == null)
        {
            throw new InvalidRequestException("Cannot Search with null resource");
        }

        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase classDb = mongoClient.getDatabase("classes");
            Document courseDescription = new Document("className" , className);
            Document authCourseDoc = classDb.getCollection(className)
                    .findOneAndUpdate(courseDescription , new Document("open",open));

            if(authCourseDoc == null)
                return false;



        }catch (Exception e)
        {

        }
        return true;
    }

    public boolean UpdateClassStartDate(String className , Date date)
    {
        if(className == null)
        {
            throw new InvalidRequestException("Cannot Search with null resource");
        }

        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase classDb = mongoClient.getDatabase("classes");
            Document courseDescription = new Document("className" , className);
            Document authCourseDoc = classDb.getCollection(className)
                    .findOneAndUpdate(courseDescription , new Document("registrationTime", date));

            if(authCourseDoc == null)
                return false;



        }catch (Exception e)
        {

        }
        return true;
    }

    public RegistrationCatalog delete(RegistrationCatalog newUser, String name) {

        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase classDb = mongoClient.getDatabase("classes");
            classDb.getCollection(name).drop(); //deletes collection with class name

            return newUser;

        } catch (Exception e) {
            //logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    public void showClasses() {

        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase classDb = mongoClient.getDatabase("classes");

            MongoIterable<String> list = classDb.listCollectionNames();
            //iterate through all collections in class DB and print
            for (String name : list) {
                System.out.println(name);
            }

        } catch (Exception e) {
            //logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    public RegistrationCatalog showRoster(RegistrationCatalog newUser, String className) {
           // List<Document> doc = new ArrayList<>();
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase classDb = mongoClient.getDatabase("classes");
            MongoCollection<Document> usersCollection = classDb.getCollection(className);
            //document enables reading collection contents with .find(), i.e student names

            FindIterable<Document> iterDoc = usersCollection.find();
            Iterator it = iterDoc.iterator();
            while (it.hasNext()) {
                //iterates through class roster and only prints student names
                String stNames = it.next().toString().substring(49);
                //cuts off string until start of student name
                stNames = stNames.substring(0, stNames.length() - 2);
                //removes last two characters of string (contained two brackets at end)
                System.out.println(stNames);
                //prints out remaining string after trimming unnecessary characters (only student name remains)
            }

        } catch (Exception e) {
           // logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
        return newUser;
    }

    public boolean currentlyRegistered(RegistrationCatalog newUser, boolean reg, String className){
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase classDb = mongoClient.getDatabase("classes");
            MongoCollection<Document> usersCollection = classDb.getCollection(className);

            FindIterable<Document> iterDoc = usersCollection.find();
            Iterator it = iterDoc.iterator();
            while (it.hasNext()) {
                String stNames = it.next().toString().substring(49);
                stNames = stNames.substring(0, stNames.length() - 2);
                if(stNames.equals(newUser.getClassName())){
                    reg = true;
                }
            }
            //same basic process as showRoster, but instead of printing all student names, finds if
            //provided student name matches any inside collection, and returns true if it does

        } catch (Exception e) {
           // logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
        return reg;
    }

    public ClassDetails GetClassDetailsOf(String className)
    {
        try{
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
            MongoDatabase classDb = mongoClient.getDatabase("classes");

            Document queryDoc = new Document("className" , className);
            Document authDoc = classDb.getCollection(className).find(queryDoc).first();

            if(authDoc ==null)
            {
                System.out.println("Null could not find doc");
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            ClassDetails classDetails = mapper.readValue(authDoc.toJson() , ClassDetails.class);
            return classDetails;

        }catch (JsonProcessingException jpe)
        {
            System.out.println("Mapping Error");
            jpe.printStackTrace();

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getAllCollections(List<String> classNames){
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase classDb = mongoClient.getDatabase("classes");

            MongoIterable<String> list = classDb.listCollectionNames();
            //adds all classes in DB to a string list.
            //used to show all classes a student is registered for in StudentDashboard
            for (String name : list) {
                classNames.add(name);
            }
            return classNames;

        } catch (Exception e) {
           // logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }


    }

    public RegistrationCatalog register(RegistrationCatalog newUser, String classname) {

        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase classDb = mongoClient.getDatabase("classes");
            boolean collExists = false;
            //used to check if a class already exists. does not register student if it does not
            for (final String name : classDb.listCollectionNames()) {
                if (name.equalsIgnoreCase(classname)) {
                    //if provided class name matches a collection in DB, then it exists
                    collExists = true;
                }
            }

            if(collExists){
                try{
                    //class exists: add student to class

                    MongoCollection<Document> usersCollection = classDb.getCollection(classname);
                    Document newUserDoc = new Document("Students", newUser.getClassName());
                    usersCollection.insertOne(newUserDoc);
                }catch (Exception e){
                 //   logger.error(e.getMessage());
                    System.out.println("Student already registered");
                }

            }

            return newUser;

        } catch (Exception e) {
           // logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    public RegistrationCatalog withdraw(RegistrationCatalog newUser, String className){
        try {
            MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();

            MongoDatabase classDb = mongoClient.getDatabase("classes");
            MongoCollection<Document> usersCollection = classDb.getCollection(className);
            Document newUserDoc = new Document("Students", newUser.getClassName());

            usersCollection.deleteOne(newUserDoc); //removes provided class name from DB (deletes collection)
            //nothing happens if provided class does not exist in database

            return newUser;

        } catch (Exception e) {
            //logger.error(e.getMessage());
            throw new DataSourceException("An unexpected exception occurred.", e);
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getClassSize() {
        return classSize;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public void setClassSize(int classSize) {
        this.classSize = classSize;
    }
}
