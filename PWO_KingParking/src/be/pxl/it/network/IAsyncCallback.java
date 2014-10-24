package be.pxl.it.network;

/**
 * @author Tristan Fransen
 * @author Servaas Tilkin
 * @param <T>
 */
public interface IAsyncCallback<T> {
    public void onOperationCompleted(T result);
}
