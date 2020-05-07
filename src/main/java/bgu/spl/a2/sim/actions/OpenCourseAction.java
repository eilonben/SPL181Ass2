package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.List;

public class OpenCourseAction extends Action<Boolean> {
    private final String courseName;
    private final CoursePrivateState coursePS;
    private final String department;


    public OpenCourseAction(int AvailableSpots , List<String> prerequisites, String courseName, String department){
        actionName="Open Course";
        coursePS = new CoursePrivateState();
        this.courseName=courseName;
        coursePS.setAvailableSpots(AvailableSpots);
        coursePS.setPrequisites(prerequisites);
        this.department = department;
        promise = new Promise<>();

    }

    protected void start(){
        started=true;
        DepartmentPrivateState dPS = (DepartmentPrivateState)pool.getPrivateState(department);
        dPS.getCourseList().add(courseName);
        sendMessage(new CreateActorAction(),courseName,coursePS); // creates course Actor
        complete(true);
    }
}
