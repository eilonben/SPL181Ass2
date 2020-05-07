package bgu.spl.a2;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class VersionMonitorTest extends TestCase {
    private VersionMonitor v;
    private int[] arr = new int[1];
    private Boolean flag = new Boolean(false);
    int x;

    @Test
    public void testGetVersion() {
        v = new VersionMonitor();
        try {
            int x = v.getVersion();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testInc() {
        v = new VersionMonitor();
        try {
            x = v.getVersion();
            v.inc();
        } catch (Exception e) {
            Assert.fail();
        }
        assertEquals(x + 1, v.getVersion());
    }

    @Test
    public void testAwait1() {
        v = new VersionMonitor();
        try {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        v.await(0);
                    } catch (Exception e) {
                        flag = true;
                    }
                }
            });
            t.start();
            v.inc();
            Thread.sleep(50);
            t.interrupt();
            if (flag)
                Assert.fail();

        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testAwait2() {
        try {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                       VersionMonitor x = new VersionMonitor();
                        x.await(0);
                    } catch (Exception e) {
                        System.out.println(e);
                        System.out.println("flag");
                        flag = true;
                    }
                }
            });

            try {
                if (flag)
                    Assert.fail();
                System.out.println(t.getState());
                t.start();
                Thread.sleep(100);
                System.out.println(t.getState());
                assertEquals(Thread.State.WAITING, t.getState());
            } catch (Exception e) {
                Assert.fail();
            }
        } catch (Exception e) {
            Assert.fail();
        }

    }

}