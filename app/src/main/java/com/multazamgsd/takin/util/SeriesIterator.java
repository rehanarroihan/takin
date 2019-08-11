package com.multazamgsd.takin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeriesIterator<T,X> {
    private List<T> _items = null;
    private List<X> _returns = new ArrayList<X>();
    private SeriesIteratorFunctions<T,X> _callbacks = null;

    private T current = null;

    public SeriesIterator(List<T> items, SeriesIteratorFunctions<T,X> callbacks) {
        this._callbacks = callbacks;
        this._items = items;
    }

    public void execute() {
        current = _items.get(0);
        this._callbacks.onEveryItem(this, current);
    }


    public void next(X returns) {
        _returns.add(returns);
        _items.remove(0);

        if (_items.size() == 0 ) {
            this._callbacks.onReturn(_returns);
        } else {
            execute();
        }
    }

    public interface SeriesIteratorFunctions<T,X> {
        void onEveryItem(SeriesIterator<T,X> context,T item);
        void onReturn(List<X> values);
    }
}

