package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.List;

public class CheckPrerequisitesAndRegAction extends Action<Boolean> {
    private List<String> prequisities;
    private StudentPrivateState studentPS;
    private Integer grade;
    private String courseID;

    public CheckPrerequisitesAndRegAction(String courseID, List<String> prequisities, StudentPrivateState studentPrivateState, Integer grade){
        actionName="CheckPreAndReg";
        this.prequisities=prequisities;
        this.studentPS= studentPrivateState;
        this.grade=grade;
        this.courseID=courseID;
        promise = new Promise<>();

    }

    public void start(){
        started=true;
        for (String course: prequisities) { // checks if the student has passed all of the courses
            if (!studentPS.getGrades().containsKey(course)){
                complete(false);
                return; // if he hasnt returns and complete = false
            }
        }
        studentPS.getGrades().put(courseID,grade);
        complete(true);
    }
}
