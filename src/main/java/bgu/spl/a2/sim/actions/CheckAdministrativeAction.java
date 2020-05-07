package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Simulator;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class CheckAdministrativeAction extends Action<Boolean> {
    private ArrayList<String> students;
    private ArrayList<String> courses;
    private String  compType;
    private Warehouse warehouse;

    public CheckAdministrativeAction(ArrayList students, ArrayList courses, String compType) {
        actionName="Administrative Check";
        this.students = students;
        this.courses = courses;
        this.compType = compType;
        this.warehouse = Simulator.warehouse;
        promise = new Promise<>();
    }

    protected void start() {
        started=true;
        Computer comp = warehouse.getComputer (compType); // returns the Computer by type
        Promise<Computer> myComp = warehouse.acquire(comp); // down to the computer
        List<Action<Boolean>> subActions = new ArrayList<>();
        myComp.subscribe(() -> {
            for (String student : students) {
                StudentPrivateState studentPS = (StudentPrivateState) pool.getPrivateState(student);
                Action<Boolean> CandS = new CheckAndSignAction(studentPS, courses, comp); // updates the signature of the student
                sendMessage(CandS, student, studentPS);
                subActions.add(CandS);
            }
        });

        then(subActions, () -> {
            warehouse.release(comp); // when all of the Check and sign Actions are finished we release the  computer
            complete(true);
        });
    }
}
