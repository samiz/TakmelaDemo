package utils_takmela;

public interface ITree<T>
{
	int nChildren();
	ITree<T> child(int i);
}
