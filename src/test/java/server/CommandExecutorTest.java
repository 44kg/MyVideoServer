package server;

import org.junit.Assert;
import org.junit.Test;

public class CommandExecutorTest {
    @Test
    public void runLinuxCommandTest1() {
        Assert.assertEquals("Hello World\n", CommandExecutor.runLinuxCommand("echo Hello World"));
    }

    @Test
    public void runLinuxCommandTest2() {
        Assert.assertEquals("", CommandExecutor.runLinuxCommand("enyWrongCommand"));
    }

    @Test
    public void runLinuxCommandTest3() {
        Assert.assertEquals("", CommandExecutor.runLinuxCommand(null));
    }

    @Test
    public void runLinuxCommandTest4() {
        Assert.assertEquals("", CommandExecutor.runLinuxCommand(""));
    }

    @Test
    public void runLinuxCommandTest5() {
        Assert.assertEquals("", CommandExecutor.runLinuxCommand("    "));
    }
}
