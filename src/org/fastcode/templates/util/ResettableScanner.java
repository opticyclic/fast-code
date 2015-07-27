package org.fastcode.templates.util;

import org.eclipse.jface.text.rules.ICharacterScanner;

/**
 * Resettable scanner that will forward calls to
 * a given scanner, but stores a marked position.
 */
public class ResettableScanner implements ICharacterScanner {
	private int					readCount;
	private ICharacterScanner	delegate;

	/**
	 * Instantiates a new resettable scanner.
	 */
	public ResettableScanner() {
		this.readCount = 0;
		this.delegate = null;
	}

	/**
	 * Instantiates a new resettable scanner.
	 *
	 * @param scanner the delegation scanner
	 */
	public ResettableScanner(final ICharacterScanner scanner) {
		setScanner(scanner);
	}

	/**
	 * Sets the delegation scanner.
	 *
	 * @param scanner the delegation scanner
	 */
	public void setScanner(final ICharacterScanner scanner) {
		this.delegate = scanner;
		mark();
	}

	/*
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#getColumn()
	 */
	@Override
	public int getColumn() {
		return this.delegate.getColumn();
	}

	/*
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#getLegalLineDelimiters()
	 */
	@Override
	public char[][] getLegalLineDelimiters() {
		return this.delegate.getLegalLineDelimiters();
	}

	/*
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#read()
	 */
	@Override
	public int read() {
		final int ch = this.delegate.read();
		if (ch != ICharacterScanner.EOF) {
			++this.readCount;
		}
		return ch;
	}

	/*
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#unread()
	 */
	@Override
	public void unread() {
		if (this.readCount > 0) {
			this.readCount--;
		}
		this.delegate.unread();
	}

	/**
	 * Marks an offset in the scanned content.
	 */
	public void mark() {
		this.readCount = 0;
	}

	/**
	 * Resets the scanner to the marked position.
	 */
	public void reset() {
		while (this.readCount > 0) {
			unread();
		}
		while (this.readCount < 0) {
			read();
		}
	}

	/**
	 * Gets the read count.
	 *
	 * @return the read count
	 */
	public int getReadCount() {
		return this.readCount;
	}
}
