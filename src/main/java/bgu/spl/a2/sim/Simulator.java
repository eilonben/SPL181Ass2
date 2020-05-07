/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import com.google.gson.Gson;
import com.google.gson.JsonArray;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
    public static Warehouse warehouse;
    public static ActorThreadPool actorThreadPool;
    public static List<ActionJson> phase1;
    public static List<ActionJson> phase2;
    public static List<ActionJson> phase3;
    public static ArrayList<Computer> computers;
    public static String address;
    public static UniversitySystem us;

    /**
     * Begin the simulation Should not be called before attachActorThreadPool()
     */
    public static void start() {

            ActionJson[] p1 = us.getPhase_1();
            phase1 = Arrays.asList(p1);
            ActionJson[] p2 = us.getPhase_2();
            phase2 = Arrays.asList(p2);
            ActionJson[] p3 = us.getPhase_3();
            phase3 = Arrays.asList(p3);
            JsonComputer[] c = us.getComputers();
            List<JsonComputer> computersNames = Arrays.asList(c);
            CountDownLatch count1 = new CountDownLatch(phase1.size());
            CountDownLatch count2 = new CountDownLatch(phase2.size());
            CountDownLatch count3 = new CountDownLatch(phase3.size());
            computers = ParseComputers(computersNames); // creates the computers
            warehouse = new Warehouse(computers); // sets the warehouse
            actorThreadPool.start();

            try {
                parseActions(phase1, count1);
                count1.await(); // waits until count ==0
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                parseActions(phase2, count2);
                count2.await();    // waits until count ==0
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            parseActions(phase3, count3);
            try {
                count3.await();     // waits until count ==0
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        
    }

    /**
     * attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
     *
     * @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
     */
    public static void attachActorThreadPool(ActorThreadPool myActorThreadPool) {
        actorThreadPool = myActorThreadPool;
    }

    /**
     * shut down the simulation
     * returns list of private states
     */
    public static HashMap<String, PrivateState> end() {
        try {
            actorThreadPool.shutdown();
            HashMap<String, PrivateState> output = new HashMap<>();
            output.putAll(actorThreadPool.getActors());
            return output;
        } catch (InterruptedException e) {
        }
        return null;
    }


    public static void main(String[] args) {
        address = args[0];
        
        Gson gson = new Gson();
        try {
            FileReader fileReader = new FileReader(address); // tries to read the json
            us = gson.fromJson(fileReader, UniversitySystem.class);
            attachActorThreadPool(new ActorThreadPool(us.getThreads())); // creates the Actor thread pool
            } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return;
        }
        start();
        HashMap<String,PrivateState> states = end();
        try{
            FileOutputStream fout = new FileOutputStream("result.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(states);
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    public static ArrayList<Computer> ParseComputers(List<JsonComputer> computerNames) {  //creates the computers - gets info from the json
        ArrayList<Computer> computers = new ArrayList<>();
        for (JsonComputer compName : computerNames) {
            Computer c = new Computer(compName.getType());
            c.setSuccessSig(compName.getSigSuccess());
            c.setFailSig(compName.getSigFail());
            computers.add(c);
        }
        return computers;


    }

    public static void parseActions(List<ActionJson> parseList, CountDownLatch cdl) {
        // checks the type of the action - then submits the action to the thread pool. when finishes - count--
        for (ActionJson action : parseList) {
            Action<Boolean> ActionToAdd = null;
            switch (action.getActionName()) {

                case ("Open Course"):
                    ActionToAdd = new OpenCourseAction(action.getSpaces(), action.getPrerequisites(), action.getCourse(), action.getDepartment());
                    actorThreadPool.submit(ActionToAdd, action.getDepartment(), new DepartmentPrivateState());
                    break;
                case ("Add Student"):
                    ActionToAdd = new AddStudentAction(action.getStudent(), action.getDepartment());
                    actorThreadPool.submit(ActionToAdd, action.getDepartment(), new DepartmentPrivateState());
                    break;
                case ("Participate In Course"):
                    ActionToAdd = new ParticipateInCourseAction(action.getStudent(), action.getCourse(), action.getGrade());
                    actorThreadPool.submit(ActionToAdd, action.getCourse(), new CoursePrivateState());
                    break;
                case ("Add Spaces"):
                    ActionToAdd = new OpenNewSpacesAction(action.getCourse(), action.getNumber());
                    actorThreadPool.submit(ActionToAdd, action.getCourse(), new CoursePrivateState());
                    break;
                case ("Register With Preferences"):
                    ActionToAdd = new RegisterWithPreferencesAction(action.getStudent(), action.getPreferences(), action.getGrades());
                    actorThreadPool.submit(ActionToAdd, action.getStudent(), new StudentPrivateState());
                    break;
                case ("Unregister"):
                    ActionToAdd = new UnregisterAction(action.getStudent(), action.getCourse());
                    actorThreadPool.submit(ActionToAdd, action.getCourse(), new CoursePrivateState());
                    break;
                case ("Close Course"):
                    ActionToAdd = new CloseACourseAction(action.getCourse(), action.getDepartment());
                    actorThreadPool.submit(ActionToAdd, action.getDepartment(), new DepartmentPrivateState());
                    break;
                case ("Administrative Check"):
                    ActionToAdd = new CheckAdministrativeAction(action.getStudents(), action.getConditions(), action.getComputer());
                    actorThreadPool.submit(ActionToAdd, action.getDepartment(), new DepartmentPrivateState());
                    break;
            }
            ActionToAdd.getResult().subscribe(() -> {
                cdl.countDown();
            });
        }
    }
}
