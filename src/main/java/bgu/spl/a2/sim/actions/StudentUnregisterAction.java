package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class StudentUnregisterAction extends Action<Boolean> {
    private String courseID;
    private StudentPrivateState studentPS;

    public StudentUnregisterAction(String courseID,StudentPrivateState studentPS){
        actionName="UnregStud";
        this.courseID=courseID;
        this.studentPS=studentPS;
        promise = new Promise<>();

    }

    protected void start(){
        started=true;
        if (!studentPS.getGrades().containsKey(courseID)){ // if the student is not registered to the course, we return
            complete(false);
            return;
        }
        studentPS.getGrades().remove(courseID);
        complete(true);
    }
}
