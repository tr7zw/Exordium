package dev.tr7zw.exordium.util.rendersystem;

import lombok.Getter;
import lombok.ToString;

@ToString
public class MultiStateHolder implements StateHolder {

    @Getter
    private boolean fetched = false;
    private final StateHolder[] holders;

    public MultiStateHolder(StateHolder... holders) {
        this.holders = holders;
    }

    @Override
    public void fetch() {
        fetched = true;
        for (StateHolder s : holders) {
            s.fetch();
        }
    }

    @Override
    public void apply() {
        if (!fetched)
            return;
        for (StateHolder s : holders) {
            s.apply();
        }
    }

}
