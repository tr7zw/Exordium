package dev.tr7zw.exordium.components;

public interface BufferComponent<T> {

    public void captureState(T context);

    public boolean hasChanged(int tickCount, T context);

}
