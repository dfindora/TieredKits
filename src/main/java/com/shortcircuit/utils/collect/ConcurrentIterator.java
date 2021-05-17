package com.shortcircuit.utils.collect;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ConcurrentIterator<E> implements Iterator<E>
{
    private final ConcurrentArrayList<E> list;
    private int last_index = -1;
    private int index = 0;
    private int size;

    protected ConcurrentIterator(ConcurrentArrayList<E> list)
    {
        this.list = list;
        this.size = list.size();
    }

    public synchronized boolean hasNext()
    {
        synchronized (this)
        {
            return this.index < this.size;
        }
    }

    public synchronized E next()
    {
        synchronized (this)
        {
            if (this.index >= this.size)
            {
                throw new NoSuchElementException();
            }
            else
            {
                this.last_index = this.index;
                return this.list.get(this.index++);
            }
        }
    }

    public synchronized void remove()
    {
        synchronized (this)
        {
            if (this.last_index < 0)
            {
                throw new IllegalStateException();
            }
            else
            {
                this.list.remove(this.last_index);
                this.index = this.last_index;
                this.size = this.list.size();
            }
        }
    }
}
