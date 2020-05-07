package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisterWithPreferencesAction extends Action<Boolean> {
    private String studentID;
    private List<String> preferences;
    private List<String> grades;
    private AtomicInteger prefIndex;

    public RegisterWithPreferencesAction(String studentID,List<String> preferences,List<String> grades){
        actionName="Register With Preferences";
        this.studentID=studentID;
        this.preferences=preferences;
        this.grades=grades;
        prefIndex= new AtomicInteger(0);
        promise = new Promise<>();

    }

    protected void start(){
        started=true;
        List<Action<Boolean>> preferenceRegister = new ArrayList<>();
      for(int i=0; i<preferences.size(); i++){// for each course in the preferences list, create a participate in course action
          Action<Boolean> prefReg = new ParticipateInCourseAction(studentID,preferences.get(i),grades.get(i));
          preferenceRegister.add(prefReg);
          prefReg.getResult().subscribe(()->{
              if (prefReg.getResult().get()){// if the register request was successful, stop trying to register to other courses
                 complete(true);
              }else{
                  prefIndex.incrementAndGet();
                  if (prefIndex.intValue()<preferenceRegister.size()){
                      sendMessage(preferenceRegister.get(prefIndex.get()),studentID,pool.getPrivateState(preferences.get(prefIndex.intValue())));
                  }else{
                      complete(false);
                  }
              }
          });
      }
      sendMessage(preferenceRegister.get(0),studentID,pool.getPrivateState(preferences.get(prefIndex.intValue())));
    }
}
