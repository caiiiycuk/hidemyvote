package com.github.caiiiycuk.ruvote.ui;

import com.facebook.litho.EventHandler;
import com.facebook.litho.HasEventDispatcher;

public class UnScopedEventHandler<T> extends EventHandler<T> {

    public interface OnEvent<T> {
        void onEvent(T event);
    }

    private OnEvent<T> handler;

    private UnScopedEventHandler(OnEvent<T> handler) {
        this(() -> (eventHandler, eventState) -> {
            eventHandler.dispatchEvent(eventState);
            return null;
        }, 0);
        this.handler = handler;
    }

    protected UnScopedEventHandler(HasEventDispatcher hasEventDispatcher, int id) {
        super(hasEventDispatcher, id);
    }

    @Override
    public void dispatchEvent(T event) {
        if (handler == null) {
            return;
        }

        handler.onEvent(event);
    }

    public static <T> EventHandler<T> create(OnEvent<T> handlerFn) {
        return new UnScopedEventHandler<>(handlerFn);
    }

}
