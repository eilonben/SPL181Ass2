package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class UnregisterAction extends Action<Boolean> {
    private final String studentID;
    private final String courseName;

    public UnregisterAction(String studentID, String courseName) {
        actionName= "Unregister";
        this.studentID = studentID;
        this.courseName = courseName;
        promise = new Promise<Boolean>();

    }

    protected void start(){
        started=true;
        CoursePrivateState coursePS = (CoursePrivateState)pool.getPrivateState(courseName);
        StudentPrivateState studentPS = (StudentPrivateState)pool.getPrivateState(studentID);
        List<Action<Boolean>> subActions = new ArrayList<>();
        Action<Boolean> unregStud = new StudentUnregisterAction(courseName,studentPS);
        subActions.add(unregStud);
        sendMessage(unregStud,studentID,studentPS); // delete the course from the students sheets
        then(subActions,()-> {
            if (subActions.get(0).getResult().get()) {
                coursePS.setAvailableSpots(coursePS.getAvailableSpots() + 1); // increasing the available spot in the course
                coursePS.setRegistered(coursePS.getRegistered() - 1);
                coursePS.getRegStudents().remove(studentID);
                complete(true);
            }else{
                complete(false);
            }
        });

    }
}
