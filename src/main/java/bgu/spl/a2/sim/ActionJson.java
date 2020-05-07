package bgu.spl.a2.sim;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionJson {
    @SerializedName("Action")
    public String ActionName;

    public String Department;
    public String Course;
    public int Space;
    public String [] Prerequisites;
    public String Student;
    public String [] Students;
    public String [] Grade;
    public int Number;
    public String [] Preferences;
    public String Computer;
    public String [] Conditions;

    public String getActionName() {
        return ActionName;
    }

    public String getDepartment() {
        return Department;
    }

    public String getCourse() {
        return Course;
    }

    public int getSpaces() {
        return Space;
    }

    public List<String> getPrerequisites() {
        return new ArrayList<String>(Arrays.asList( Prerequisites));
    }

    public String getStudent() {
        return Student;
    }

    public ArrayList getStudents() {

        return new ArrayList<String>(Arrays.asList( Students));
    }

    public String getGrade() {
        return Grade[0];
    }
    public List<String> getGrades(){
        return new ArrayList<String>(Arrays.asList( Grade));
    }

    public int getNumber() {
        return Number;
    }

    public List<String> getPreferences() {
        return new ArrayList<String>(Arrays.asList(Preferences));
    }

    public String getComputer() {
        return Computer;
    }

    public ArrayList<String> getConditions() {

        return new ArrayList<String>(Arrays.asList(Conditions));
    }



}
