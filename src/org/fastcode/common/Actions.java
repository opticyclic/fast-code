package org.fastcode.common;

public class Actions {

	private Action	actions[];

	public void setActions(final Action actions[]) {
		this.actions = actions;
	}

	public Action[] getActions() {
		return this.actions;
	}

	private Actions(final Builder builder) {
		this.actions = builder.actions;
	}

	public static class Builder {
		private Action	actions[];

		public Builder withActions(final Action actions[]) {
			this.actions = actions;
			return this;
		}

		public Actions build() {
			return new Actions(this);
		}
	}
}
