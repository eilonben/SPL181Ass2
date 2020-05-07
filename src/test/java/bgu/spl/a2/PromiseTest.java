package bgu.spl.a2;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class PromiseTest extends TestCase {
    private Integer x = new Integer(1);

    @Test
    public void testGet() {
        try {
            Promise<Integer> p = new Promise<Integer>();
            try {
                Integer x = p.get(); // shouldn't be able to get a promise that hasn't been resolved
                Assert.fail();
            } catch (IllegalStateException e) {
                p.resolve(7);
                try {
                    Integer y = p.get();
                    assertEquals(y.intValue(), 7);
                } catch (Exception e2) {
                    Assert.fail();
                }
            }
        } catch (Exception e2) {
            Assert.fail();
        }

    }
    @Test
    public void testIsResolved() {
        try {
            Promise<Integer> p = new Promise<Integer>();
            try {
                assertEquals(false, p.isResolved());
                p.resolve(4);
                assertEquals(true, p.isResolved());
            } catch (Exception e2) {
                Assert.fail();
            }

        } catch (Exception e2) {
            Assert.fail();
        }
    }
@Test
    public void testResolve() {
        try {
            Promise<Integer> p = new Promise<Integer>();
            p.resolve(5);
            try {
                p.resolve(6);
                Assert.fail();
            } catch (IllegalStateException ex) {
                int x = p.get();
                assertEquals(x, 5);
            } catch (Exception ex) {
                Assert.fail();
            }
        } catch (Exception ex) {
            Assert.fail();

        }
    }
    @Test
    public void testResolve2() {
        try {
            Promise<Integer> p = new Promise<Integer>();

            callback c1 = new callback() {
                public void call() {
                    x++;
                }
            };
            callback c2 = new callback() {
                public void call() {
                    x = x + 3;
                }
            };
            if (x != 1) {
                Assert.fail();
            }
            p.subscribe(c1);

            if (x != 1) {
                Assert.fail();
            }
            p.resolve(7);
            p.subscribe(c2);
            assertEquals(5, x.intValue());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testSubscribe() {
        try {
            Promise<Integer> p = new Promise<Integer>();
            callback c1 = new callback() {
                public void call() {
                    x++;
                }
            };
            callback c2 = new callback() {
                public void call() {
                    x = x + 3;
                }
            };
            if (x != 1) {
                Assert.fail();
            }
            p.subscribe(c1);

            if (x != 1) {
                Assert.fail();
            }
            p.resolve(7);
            p.subscribe(c2);
            assertEquals(5, x.intValue());
        } catch (Exception e) {
            Assert.fail();
        }
    }

}
