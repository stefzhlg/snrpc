package org.stefan.snrpc.util;

/**
 * @author zhaoliangang
 *	2014-11-14
 */
public class Sequence{
    private static final Object locker = new Object();
    private static int sequence = 1000;

    public static int next()
    {
        synchronized (locker)
        {
            sequence++;
            if (sequence < 0)
                sequence = 1;
            return sequence;
        }
    }
}
