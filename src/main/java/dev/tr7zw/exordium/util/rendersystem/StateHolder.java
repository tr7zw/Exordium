package dev.tr7zw.exordium.util.rendersystem;

public interface StateHolder {

    boolean isFetched();

    void fetch();

    void apply();

}
