package utils_takmela;

public class Box<T>
{
	public T Value;

	public Box(T value)
	{
		Value = value;
	}
	
	public Box()
	{
		Value = null;
	}
	
	@Override public String toString()
	{
		return String.format("box(%s)", Value);
	}
}
