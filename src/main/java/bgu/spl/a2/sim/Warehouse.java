package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * represents a warehouse that holds a finite amount of computers
 *  and their suspended mutexes.
 * 
 */
public class Warehouse {
    private  ArrayList<Computer> computers;
    private ConcurrentHashMap<String,SuspendingMutex> mutexMap;

    public Warehouse(ArrayList<Computer> computers){
        this.computers= computers;
        mutexMap = new ConcurrentHashMap<>();// creates a map of computers - their type and Mutex
        for (Computer c: computers) {
            mutexMap.put(c.computerType,new SuspendingMutex(c));
        }
    }
// adds new computer
    public void addNewComp (Computer computer){
        this.computers.add(computer);
        mutexMap.put(computer.computerType,new SuspendingMutex(computer));
    }
// returns the promise when computer.down
    public Promise<Computer> acquire(Computer comp){
        return mutexMap.get(comp.getComputerType()).down();
    }
// releases the computer
    public void release(Computer comp){
        mutexMap.get(comp.getComputerType()).up();
    }
// returns the computer by type
    public Computer getComputer (String compType){
        return mutexMap.get(compType).getMyComp();
    }
}
