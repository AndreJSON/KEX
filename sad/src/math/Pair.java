package math;

public class Pair {
	private int from, to;

	public Pair(int from, int to) {
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public int first() {
		return from;
	}

	public int second() {
		return to;
	}
}