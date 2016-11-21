package gdn.nucleo.framework.lang.trie;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
/**
 * Created by Nathaniel on 11/19/2016.
 */
public class TrieStringTest {
    @Test
    public void testEmptyTrie(){
        TrieString cache = new TrieString();

        assertNull(cache.buildChain());
    }

    @Test
    public void testNoArgs() throws Exception{
        TrieString cache = new TrieString();

        assertFalse(cache.addToTree(this.getClass().getMethod("testEmptyTrie")));
    }

    @Test
    public void testRepeatEntry() throws Exception{
        TrieString cache = new TrieString();

        cache.addToTree(this.getClass().getMethod("testNoArgs"), "power");

        assertTrue(cache.addToTree(this.getClass().getMethod("testRepeatEntry"), "power"));

        assertEquals(cache.buildChain("power").next().method.size(),2);
    }

    @Test
    public void testNoMethod() throws Exception{
        TrieString cache = new TrieString();
        assertTrue(cache.addToTree(null, "power"));
    }

    @Test
    public void testOutOfBoundTrieSearch() throws Exception{
        TrieString cache = new TrieString();

        cache.addToTree(this.getClass().getMethod("testEmptyTrie"), "power");

        assertNull(cache.buildChain("power", "top"));
    }

    @Test
    public void testDepthOne() throws Exception{
        TrieString cache = new TrieString();

        cache.addToTree(this.getClass().getMethod("testEmptyTrie"), "power");

        assertEquals(cache.buildChain("power").length, 1);
    }

    @Test
    public void testDepthTwo() throws Exception{
        TrieString cache = new TrieString();

        assertTrue(cache.addToTree(this.getClass().getMethod("testEmptyTrie"), "super", "strong"));

        assertEquals(cache.buildChain("super", "strong").length, 1);
    }

    @Test
    public void testDepthThree() throws Exception{
        TrieString cache = new TrieString();

        assertTrue(cache.addToTree(this.getClass().getMethod("testEmptyTrie"), "do", "this", "now"));

        assertTrue(cache.addToTree(this.getClass().getMethod("testDepthThree"), "do", "this", "now", "this"));

        assertEquals(cache.buildChain("do", "this", "now").length, 1);

        assertEquals(cache.buildChain("do", "this", "now", "this").length, 2);
    }

    @Test
    public void testDepthVerifyOrder() throws Exception{
        TrieString cache = new TrieString();
        Method a = this.getClass().getMethod("testEmptyTrie");
        Method b = this.getClass().getMethod("testDepthTwo");
        Method c = this.getClass().getMethod("testDepthThree");

        assertTrue(cache.addToTree(a, "do", "this"));
        assertTrue(cache.addToTree(b, "do", "this", "now"));
        assertTrue(cache.addToTree(c, "do", "this", "now", "this"));

        NodeChain nc = cache.buildChain("do", "this", "now", "this");

        assertEquals(nc.next().method.get(0), a);
        assertEquals(nc.next().method.get(0), b);
        assertEquals(nc.next().method.get(0), c);
    }

    @Test
    public void testAddOutOfOrderChainInOrder() throws Exception{
        TrieString cache = new TrieString();
        Method a = this.getClass().getMethod("testEmptyTrie");
        Method b = this.getClass().getMethod("testDepthTwo");
        Method c = this.getClass().getMethod("testDepthThree");

        assertTrue(cache.addToTree(a, "do", "this"));
        assertTrue(cache.addToTree(c, "do", "this", "now", "this"));
        assertTrue(cache.addToTree(b, "do", "this", "now"));

        NodeChain nc = cache.buildChain("do", "this", "now", "this");

        assertEquals(nc.next().method.get(0), a);
        assertEquals(nc.next().method.get(0), b);
        assertEquals(nc.next().method.get(0), c);
    }

    public class URITestHelper{
        public int user;
        public boolean authed=false;
        public boolean execute = false;
        public URITestHelper(){}
        public void getAuth(){
            authed = true;
        }
        public void getUser(){
            user = 5;
        }
        public void doAction(){
            execute = true;
        }
    }
    @Test
    public void testURIExample() throws Exception{
        URITestHelper chainData = new URITestHelper();

        TrieString cache = new TrieString();
        Method a = URITestHelper.class.getMethod("getAuth");
        Method b = URITestHelper.class.getMethod("getUser");
        Method c = URITestHelper.class.getMethod("doAction");

        assertTrue(cache.addToTree(a, "auth"));
        assertTrue(cache.addToTree(b, "auth", "user"));
        assertTrue(cache.addToTree(c, "auth", "user", "like"));

        NodeChain nc = cache.buildChain("auth", "user", "like");

        nc.next().method.get(0).invoke(chainData);
        nc.next().method.get(0).invoke(chainData);
        nc.next().method.get(0).invoke(chainData);

        assertEquals( chainData.user, 5);
        assertTrue(chainData.authed);
        assertTrue(chainData.execute);

    }

    @Test
    public void testChainHasNext() throws Exception{
        URITestHelper chainData = new URITestHelper();

        TrieString cache = new TrieString();
        Method a = URITestHelper.class.getMethod("getAuth");
        Method b = URITestHelper.class.getMethod("getUser");
        Method c = URITestHelper.class.getMethod("doAction");

        assertTrue(cache.addToTree(a, "do", "this"));
        assertTrue(cache.addToTree(c, "do", "this", "now", "this"));
        assertTrue(cache.addToTree(b, "do", "this", "now"));

        NodeChain nc = cache.buildChain("do", "this", "now", "this");
        while(nc.hasNext()){
            nc.next().method.get(0).invoke(chainData);
        }
        assertEquals( chainData.user, 5);
        assertTrue(chainData.authed);
        assertTrue(chainData.execute);
    }
}
