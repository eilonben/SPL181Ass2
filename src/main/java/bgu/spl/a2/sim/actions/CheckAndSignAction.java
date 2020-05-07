package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class CheckAndSignAction extends Action<Boolean> {
    private StudentPrivateState studentPS;
    private List<String> courses;
    private Computer comp;


    public CheckAndSignAction(StudentPrivateState studentPS, ArrayList courses, Computer comp){
        actionName="CheckAndSign";
        this.studentPS=studentPS;
        this.courses=courses;
        this.comp=comp;
        promise = new Promise<>();

    }

    protected void start() {
        started=true;
        //updated the signature of the student
        studentPS.setSignature(comp.checkAndSign(courses,studentPS.getGrades()));
        complete(true);
    }
}

