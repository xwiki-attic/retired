package model;

import java.util.ArrayList;

import network.Payload;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.jdom.Verifier;

public class Translator {

	public static Payload translate(KeyEvent ke) {
		
		switch (ke.keyCode) {
		case 16777218:
			System.out.println("Case1 fired");
			return new Payload("navigation", SWT.ARROW_DOWN);

		case 16777219:
			return new Payload("navigation", SWT.ARROW_LEFT);
		case 16777220:
			return new Payload("navigation", SWT.ARROW_RIGHT);
		case 16777217:
			return new Payload("navigation", SWT.ARROW_UP);
		case 127:
			return new Payload("navigation?", "");// TODO implement forward delete
		case 8: {
			ArrayList a = new ArrayList();
			a.add("remove");
			a.add(Cursor.getInstance().getPosition());
			a.add("don't care");
			a.add(1);
			return new Payload("text", a);
		}

		default: {
			String goodIfNull = Verifier.checkCharacterData(String
					.valueOf(ke.character));
			Payload p;
			if (goodIfNull == null) {
				ArrayList a = new ArrayList();
				a.add("add");
				a.add(Cursor.getInstance().getPosition());
				a.add(String.valueOf(ke.character));
				p = new Payload("text", a);
			} else {
				p = new Payload("bad text", "");
			}
			return p;

		}
		}
	}
}