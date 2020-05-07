package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

public class OpenNewSpacesAction extends Action<Boolean> {
    private final String courseName;
    private final int newSpaces;

    public OpenNewSpacesAction(String courseName, int newSpaces){
        actionName="Add Spaces";
        this.courseName=courseName;
        this.newSpaces=newSpaces;
        promise = new Promise<>();

    }

    protected void start(){
        started=true;
        CoursePrivateState coursePS = (CoursePrivateState)pool.getPrivateState(courseName);
        coursePS.setAvailableSpots(coursePS.getAvailableSpots()+newSpaces); // adds more space to the course
        complete(true);
    }
}
