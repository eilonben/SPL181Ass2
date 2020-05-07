package bgu.spl.a2.sim;
import bgu.spl.a2.Promise;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 *
 * Note: this class can be implemented without any synchronization. 
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {
	private Computer myComp;
	private AtomicBoolean isFree;
	private ConcurrentLinkedQueue<Promise> promises;

	/**
	 * Constructor
	 * @param computer
	 */
	public SuspendingMutex(Computer computer){
		this.myComp= computer;
		isFree= new AtomicBoolean(true);
		promises= new ConcurrentLinkedQueue<>();
	}
	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 *
	 * @return a promise for the requested computer
	 */
	public Promise<Computer> down(){
		Promise<Computer> promise = new Promise<>();
		if (isFree.compareAndSet(true,false))
			promise.resolve(myComp);
		else
			promises.add(promise);

		return promise;
	}
	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up(){
		if (!promises.isEmpty()){
			promises.poll().resolve(myComp);
			return;
		}
		isFree.compareAndSet(false,true);
	}
	public Computer getMyComp (){
		return myComp;
	}
}
