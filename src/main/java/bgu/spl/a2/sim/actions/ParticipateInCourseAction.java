package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class ParticipateInCourseAction extends Action<Boolean> {
    private final String studentID;
    private final String courseName;
    private final Integer grade;

    public ParticipateInCourseAction(String studentID, String courseName, String grade) {
        actionName="Participate In Course";
        this.studentID = studentID;
        this.courseName = courseName;
        promise = new Promise<>();
        if (grade == "-")
            this.grade = -1;
        else
            this.grade = Integer.parseInt(grade);
    }

    protected void start() {
        started=true;
        CoursePrivateState coursePS = (CoursePrivateState)pool.getPrivateState(courseName);
        StudentPrivateState studentPS = (StudentPrivateState) pool.getPrivateState(studentID);
        if (coursePS.getAvailableSpots() <= 0) {
            complete(false);
            return;
        }
        coursePS.setAvailableSpots(coursePS.getAvailableSpots() - 1);// "save" the spot for the current student's register request
        coursePS.setRegistered(coursePS.getRegistered() + 1);
        Action<Boolean> check = new CheckPrerequisitesAndRegAction(courseName,coursePS.getPrequisites(), studentPS,grade);
        sendMessage(check, studentID, studentPS);
        List<Action<Boolean>> subActions = new ArrayList<>();
        subActions.add(check);
        then(subActions, ()->{
            if (subActions.get(0).getResult().get()){
                if (coursePS.getAvailableSpots()>=0) { // checks if the course has been closed while the student added the course to its grade sheet
                    coursePS.getRegStudents().add(studentID);
                    complete(true);
                }
                else{ // if he has been closed, it unregisters the student
                    Action<Boolean> unreg = new UnregisterAction(studentID,courseName);
                    sendMessage(unreg,courseName,coursePS);
                    complete(false);
                }

            }else{ // if the student isn't fit to register frees the spot
                coursePS.setAvailableSpots(coursePS.getAvailableSpots() + 1);
                coursePS.setRegistered(coursePS.getRegistered() - 1);
                complete(false);
            }
        });
    }

}
