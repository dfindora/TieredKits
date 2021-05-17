package com.shortcircuit.utils;

public class BlockingCallback<T>
{
    private final Object lock;
    private boolean value_set;
    private T value;

    public BlockingCallback()
    {
        this(null);
    }

    public BlockingCallback(T default_value)
    {
        this.lock = new Object();
        this.value_set = false;
        this.value = null;
        this.value = default_value;
    }

    public void setValue(T value)
    {
        this.value = value;
        this.value_set = true;
        synchronized (this.lock)
        {
            this.lock.notify();
        }
    }

    public T getValue()
    {
        if (!this.value_set)
        {
            synchronized (this.lock)
            {
                try
                {
                    this.lock.wait();
                }
                catch (InterruptedException var4)
                {
                    var4.printStackTrace();
                }
            }
        }

        return this.value;
    }
}
