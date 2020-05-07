package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;

public class CreateActorAction extends Action<Boolean> {

    public CreateActorAction(){ // An empty action which has one purpose:creating a new actor.
        actionName="CreateActor";
        promise = new Promise<>();

    }

    protected void start(){
        started=true;
        complete(true);
    }
}
