package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

public class AddStudentAction extends Action<Boolean> {
    private final StudentPrivateState StudentPS;
    private final String department;
    private final String studentID;


    public AddStudentAction(String studentID, String department){
        actionName="Add Student";
        promise = new Promise<>();
        StudentPS = new StudentPrivateState();
        this.studentID=studentID;
        this.department = department;

    }

    protected void start(){
        started=true;
        DepartmentPrivateState dPS = (DepartmentPrivateState)pool.getPrivateState(department);
        dPS.getStudentList().add(studentID); // adds the student to the department list
        sendMessage(new CreateActorAction(),studentID,StudentPS);  // creats the Student's actor
        complete(true);
    }
}