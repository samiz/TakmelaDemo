package takmela.lexer;

import utils_takmela.Utils;

public class Token
{
	private final int _id;
	private final String _text;
	private final int _pos, _line, _col;
	private final boolean _skip;

	public Token(int _id, String _text, int _pos, int _line, int _col, boolean _skip)
	{
		this._id = _id;
		this._text = _text;
		this._pos = _pos;
		this._line = _line;
		this._col = _col;
		this._skip = _skip;
	}

	public String text()
	{
		return _text;
	}
	
	public String getText()
	{
		return _text;
	}

	public int id()
	{
		return _id;
	}
	
	public int pos()
	{
		return _pos;
	}

	public int line()
	{
		return _line;
	}

	public int col()
	{
		return _col;
	}

	public boolean skip()
	{
		return _skip;
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + _pos;
		return result;
	}

	@Override public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		if (_pos != other._pos)
			return false;
		return true;
	}
	
	public String toString()
	{
		return String.format("'%s%s'", Utils.left(_text, 15), _text.length() > 15 ? ".." : "");
	}
}
