package bgu.spl.a2;



import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {

    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Action>> actorQueue;
    private ConcurrentHashMap<String, PrivateState> actorPrivateState;
    private ConcurrentHashMap<String, AtomicBoolean> actorAvailable;
    private ArrayList<Thread> threads;
    private AtomicInteger waitingThreads;

    private VersionMonitor vMon;


    public class EventLoop implements Runnable {
        private ActorThreadPool pool;

        public EventLoop(ActorThreadPool pool) {
            this.pool = pool;
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) { // if the thread has been interrupted they should stop running
                try {
                    boolean found = false;
                    boolean succeed = false;
                    String actorFound = "";
                    while (!succeed) {
                        try { //should find an available actor and enqueue an action
                            for (Map.Entry<String, ConcurrentLinkedQueue<Action>> a : actorQueue.entrySet()) {
                                if ( !a.getValue().isEmpty() &&(actorAvailable.get(a.getKey())).compareAndSet(true, false) && !Thread.currentThread().isInterrupted()) {
                                    actorFound = a.getKey();
                                    Action toHandle = actorQueue.get(actorFound).poll();
                                    if (toHandle!=null) {
                                        found = true;
                                        toHandle.handle(pool, actorFound, actorPrivateState.get(actorFound));
                                        vMon.inc(); // interrupts all threads
                                    }
                                    actorAvailable.get(actorFound).compareAndSet(false, true);// frees the actor
                                }
                            }
                            succeed = true;
                        } catch (java.util.ConcurrentModificationException e) {
                        }
                    }
                   if (waitingThreads.get() < threads.size() - 1 && !found) { // if everyone is waiting interrupt all
                        int currVer = vMon.getVersion();
                            vMon.await(currVer);
                        waitingThreads.incrementAndGet();
                    } else {
                        int exp = waitingThreads.get();
                        waitingThreads.compareAndSet(exp,0);
                        vMon.inc();
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    /**
     * creates a {@link ActorThreadPool} which has nthreads. Note, threads
     * should not get started until calling to the {@link #start()} method.
     * <p>
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this thread
     *                 pool
     */
    public ActorThreadPool(int nthreads) {
        threads = new ArrayList<>();
        actorQueue = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Action>>();
        actorPrivateState = new ConcurrentHashMap<String, PrivateState>();
        actorAvailable = new ConcurrentHashMap<String, AtomicBoolean>();
        vMon = new VersionMonitor();
        EventLoop e = new EventLoop(this);
        for (int i = 0; i < nthreads; i++) {
            threads.add(new Thread(e));
        }
        waitingThreads = new AtomicInteger(0);

    }

    /**
     * getter for actors
     *
     * @return actors
     */
    public Map<String, PrivateState> getActors() {

        return actorPrivateState;
    }

    /**
     * getter for actor's private state
     *
     * @param actorId actor's id
     * @return actor's private state
     */

    public PrivateState getPrivateState(String actorId) {
        return actorPrivateState.get(actorId);

    }


    /**
     * submits an action into an actor to be executed by a thread belongs to
     * this thread pool
     *
     * @param action     the action to execute
     * @param actorId    corresponding actor's id
     * @param actorState actor's private state (actor's information)
     */

    public void submit(Action<?> action, String actorId, PrivateState actorState) {
        if (!actorQueue.containsKey(actorId)) { // checks is the actor allready exists, if not - creates one
            ConcurrentLinkedQueue<Action> actorQ = new ConcurrentLinkedQueue<Action>();
            actorPrivateState.put(actorId, actorState);
            AtomicBoolean b = new AtomicBoolean(false);
            actorAvailable.put(actorId, b);
            actorQueue.put(actorId, actorQ);
            actorQueue.get(actorId).add(action);
            actorAvailable.get(actorId).compareAndSet(false, true);

        } else {
            actorQueue.get(actorId).add(action);
        }
        vMon.inc();
    }

    /**
     * closes the thread pool - this method interrupts all the threads and waits
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     * <p>
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is interrupted
     */
    public void shutdown() throws InterruptedException { // interrupts all threads
        for (Thread c : threads) {
            c.interrupt();
        }
        boolean shut = false;
        while (!shut) {
            shut = true;
            for (Thread c : threads) {
                if (c.isAlive()) {
                    shut = false;
                    break;
                }
            }
        } // all threads are dead
        actorAvailable.clear();
        actorQueue.clear();
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
        for (Thread t : threads) {
            t.start();
        }
    }


}
