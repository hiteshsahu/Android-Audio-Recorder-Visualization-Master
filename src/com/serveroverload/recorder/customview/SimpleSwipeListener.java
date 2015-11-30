package com.serveroverload.recorder.customview;


/**
 * The listener interface for receiving simpleSwipe events.
 * The class that is interested in processing a simpleSwipe
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSimpleSwipeListener<code> method. When
 * the simpleSwipe event occurs, that object's appropriate
 * method is invoked.
 *
 * @see SimpleSwipeEvent
 */
public class SimpleSwipeListener implements SwipeLayout.SwipeListener {

    @Override
    public void onStartOpen(SwipeLayout layout) {
    }

    @Override
    public void onOpen(SwipeLayout layout) {
    }

    @Override
    public void onStartClose(SwipeLayout layout) {
    }

    @Override
    public void onClose(SwipeLayout layout) {
    }

    @Override
    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
    }

    @Override
    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
    }
}
