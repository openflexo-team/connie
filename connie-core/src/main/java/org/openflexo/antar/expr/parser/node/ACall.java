/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.*;

@SuppressWarnings("nls")
public final class ACall extends PCall {
	private TIdentifier _identifier_;
	private PArgList _argList_;

	public ACall() {
		// Constructor
	}

	public ACall(@SuppressWarnings("hiding") TIdentifier _identifier_, @SuppressWarnings("hiding") PArgList _argList_) {
		// Constructor
		setIdentifier(_identifier_);

		setArgList(_argList_);

	}

	@Override
	public Object clone() {
		return new ACall(cloneNode(this._identifier_), cloneNode(this._argList_));
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseACall(this);
	}

	public TIdentifier getIdentifier() {
		return this._identifier_;
	}

	public void setIdentifier(TIdentifier node) {
		if (this._identifier_ != null) {
			this._identifier_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._identifier_ = node;
	}

	public PArgList getArgList() {
		return this._argList_;
	}

	public void setArgList(PArgList node) {
		if (this._argList_ != null) {
			this._argList_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._argList_ = node;
	}

	@Override
	public String toString() {
		return "" + toString(this._identifier_) + toString(this._argList_);
	}

	@Override
	void removeChild(@SuppressWarnings("unused") Node child) {
		// Remove child
		if (this._identifier_ == child) {
			this._identifier_ = null;
			return;
		}

		if (this._argList_ == child) {
			this._argList_ = null;
			return;
		}

		throw new RuntimeException("Not a child."); // NOPMD by beugnard on 31/10/14 00:08
	}

	@Override
	void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild) {
		// Replace child
		if (this._identifier_ == oldChild) {
			setIdentifier((TIdentifier) newChild);
			return;
		}

		if (this._argList_ == oldChild) {
			setArgList((PArgList) newChild);
			return;
		}

		throw new RuntimeException("Not a child."); // NOPMD by beugnard on 31/10/14 00:08
	}
}
