package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.List;

public class UnregisterAllAction extends Action<Boolean> {
    private String courseID;
    private CoursePrivateState coursePS;

    public UnregisterAllAction(String courseID, CoursePrivateState coursePS){
        actionName="UnregAll";
        this.courseID=courseID;
        this.coursePS=coursePS;
        promise = new Promise<>();

    }

    protected void start(){
        started=true;
        List<Action<Boolean>> subActions = new ArrayList<>();
        coursePS.setAvailableSpots(-(coursePS.getRegistered()+1));
        for (String student : coursePS.getRegStudents()) { // goes through all the students and sends to them unregister actoin
            Action<Boolean> unreg = new UnregisterAction(student,courseID);
            subActions.add(unreg);
            sendMessage(unreg,courseID,coursePS);
        }
        then(subActions,()->{ // making it impossible to register
            coursePS.setAvailableSpots(-1);
            complete(true);
        });
    }
}
