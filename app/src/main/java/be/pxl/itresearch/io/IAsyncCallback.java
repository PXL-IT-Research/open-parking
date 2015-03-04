package be.pxl.itresearch.io;

/**
 * @author Tristan Fransen
 * @author Servaas Tilkin
 * @param <T>
 */
public interface IAsyncCallback<T> {
    void onOperationCompleted(T result);
}
