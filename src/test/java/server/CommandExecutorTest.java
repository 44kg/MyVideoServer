package server;

import org.junit.Assert;
import org.junit.Test;
import server.command.CommandExecutor;

public class CommandExecutorTest {
    @Test
    public void executeCommandTest1() {
        Assert.assertEquals("Hello World\n", CommandExecutor.executeCommand("echo Hello World"));
    }

    @Test
    public void executeCommandTest2() {
        Assert.assertEquals("", CommandExecutor.executeCommand("enyWrongCommand"));
    }

    @Test
    public void executeCommandTest3() {
        Assert.assertEquals("", CommandExecutor.executeCommand(null));
    }

    @Test
    public void executeCommandTest4() {
        Assert.assertEquals("", CommandExecutor.executeCommand(""));
    }

    @Test
    public void executeCommandTest5() {
        Assert.assertEquals("", CommandExecutor.executeCommand("    "));
    }
}
