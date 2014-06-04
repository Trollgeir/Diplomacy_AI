package ai;

public abstract class AI implements MessageListener {

	Interpreter;

	public void messageReceived(String[] message) {
		Interpreter.interpret(message); 
	}

}
