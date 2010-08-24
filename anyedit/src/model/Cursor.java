package model;

public class Cursor {
	private static Cursor uniqueInstance;

	private static Integer position = 0;

	private Cursor() {
		// TODO Auto-generated constructor stub
	}

	public static Cursor getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Cursor();
		}
		return uniqueInstance;
	}

	public static Integer getPosition() {
		return position;
	}

	public static void setPosition(int i) {
		if (i >= 0) {
			position = new Integer(i);
		}
	}
}
