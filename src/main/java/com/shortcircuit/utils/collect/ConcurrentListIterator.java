package com.shortcircuit.utils.collect;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ConcurrentListIterator<E> implements ListIterator<E>
{
    private final ConcurrentArrayList<E> list;
    private int index = 0;
    private int size;
    private int last_index = -1;

    protected ConcurrentListIterator(ConcurrentArrayList<E> list)
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

    public synchronized boolean hasPrevious()
    {
        synchronized (this)
        {
            return this.index > 0;
        }
    }

    public synchronized E previous()
    {
        synchronized (this)
        {
            if (this.index <= 0)
            {
                throw new NoSuchElementException();
            }
            else
            {
                this.last_index = this.index;
                return this.list.get(--this.index);
            }
        }
    }

    public synchronized int nextIndex()
    {
        synchronized (this)
        {
            return this.index + 1;
        }
    }

    public synchronized int previousIndex()
    {
        synchronized (this)
        {
            return this.index - 1;
        }
    }

    public synchronized void remove()
    {
        synchronized (this)
        {
            int i = this.last_index - 1;
            if (i >= 0 && i < this.size)
            {
                this.list.remove(i);
                this.index = i;
                this.size = this.list.size();
            }
            else
            {
                throw new IllegalStateException();
            }
        }
    }

    public synchronized void set(E e)
    {
        synchronized (this)
        {
            if (this.last_index < 0)
            {
                throw new IllegalStateException();
            }
            else
            {
                this.list.set(this.last_index, e);
            }
        }
    }

    public void add(E e)
    {
        synchronized (this)
        {
            this.list.add(this.index++, e);
            this.last_index = -1;
        }
    }
}
