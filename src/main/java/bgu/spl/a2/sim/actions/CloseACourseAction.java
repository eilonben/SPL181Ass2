package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class CloseACourseAction extends Action<Boolean> {
    private String courseID;
    private String department;

    public CloseACourseAction(String courseID, String department){
        actionName="Close Course";
        this.courseID=courseID;
        this.department=department;
        promise = new Promise<>();

    }

    protected void start(){
        CoursePrivateState coursePS = (CoursePrivateState)pool.getPrivateState(courseID);
        started=true;
        List<Action<Boolean>> subActions = new ArrayList<>();
        Action<Boolean> closeAll = new UnregisterAllAction(courseID,coursePS); // unregister all the students
        subActions.add(closeAll);
        sendMessage(closeAll,courseID,coursePS);
        then(subActions,()->{
            ((DepartmentPrivateState)pool.getPrivateState(department)).getCourseList().remove(courseID); // removes the course from the department
            complete(true);
        });

    }
}
