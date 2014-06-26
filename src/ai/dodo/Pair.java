package ai.dodo;

public class Pair <I,T> {
	
	public I i;
	public T t;
	
	public Pair(I i, T t) {
		this.i = i;
		this.t = t;
	}

	public T getValue() {
		return this.t;
	}
}
