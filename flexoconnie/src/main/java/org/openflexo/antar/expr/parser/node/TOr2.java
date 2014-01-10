/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.*;

@SuppressWarnings("nls")
public final class TOr2 extends Token {
	public TOr2() {
		super.setText("||");
	}

	public TOr2(int line, int pos) {
		super.setText("||");
		setLine(line);
		setPos(pos);
	}

	@Override
	public Object clone() {
		return new TOr2(getLine(), getPos());
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseTOr2(this);
	}

	@Override
	public void setText(@SuppressWarnings("unused") String text) {
		throw new RuntimeException("Cannot change TOr2 text.");
	}
}
