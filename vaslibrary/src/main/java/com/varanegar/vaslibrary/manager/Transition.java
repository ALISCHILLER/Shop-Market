package com.varanegar.vaslibrary.manager;

import java.util.UUID;

/**
 * Created by A.Torabi on 4/29/2018.
 */

public class Transition {
    public final TransitionType Type;
    public final UUID Region;

    public Transition(TransitionType type, UUID region) {
        this.Type = type;
        this.Region = region;
    }
}
