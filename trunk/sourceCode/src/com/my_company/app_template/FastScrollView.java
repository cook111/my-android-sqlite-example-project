/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.my_company.app_template;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * FastScrollView is meant for embedding {@link ListView}s that contain a large
 * number of items that can be indexed in some fashion. It displays a special
 * scroll bar that allows jumping quickly to indexed sections of the list in
 * touch-mode. Only one child can be added to this view group and it must be a
 * {@link ListView}, with an adapter that is derived from {@link BaseAdapter}.
 * 
 * <p>
 * This file was copied from the Contacts application. In the future it should
 * be provided as a standard part of the Android framework.
 */
public class FastScrollView extends FrameLayout implements OnScrollListener,
    OnHierarchyChangeListener {

  private Drawable mCurrentThumb;
  private Drawable mOverlayDrawable;

  private int mThumbH;
  private int mThumbW;
  private int mThumbY;

  private RectF mOverlayPos;

  // Hard coding these for now
  private int mOverlaySize = 104;

  private boolean mDragging;
  private ListView mList;
  private boolean mScrollCompleted;
  private boolean mThumbVisible;
  private int mVisibleItem;
  private Paint mPaint;
  private int mListOffset;

  private Object[] mSections;
  private String mSectionText;
  private boolean mDrawOverlay;
  private ScrollFade mScrollFade;

  private Handler mHandler = new Handler();

  private BaseAdapter mListAdapter;

  private boolean mChangedBounds;

  interface SectionIndexer {
    Object[] getSections();

    int getPositionForSection(int section);

    int getSectionForPosition(int position);
  }// end SectionIndexer

  public FastScrollView(Context context) {
    super(context);

    init(context);
  }// end constructor

  public FastScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);

    init(context);
  }// end constructor

  public FastScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    init(context);
  }// end constructor

  private void useThumbDrawable(Drawable drawable) {
    mCurrentThumb = drawable;
    mThumbW = 64; // mCurrentThumb.getIntrinsicWidth();
    mThumbH = 52; // mCurrentThumb.getIntrinsicHeight();
    mChangedBounds = true;
  }// end useThumbDrawable

  private void init(Context context) {
    // Get both the scrollbar states drawables
    final Resources res = context.getResources();
    useThumbDrawable(res
        .getDrawable(R.drawable.scrollbar_handle_accelerated_anim2));

    mOverlayDrawable = res.getDrawable(R.drawable.dialog_full_dark);

    mScrollCompleted = true;
    setWillNotDraw(false);

    // Need to know when the ListView is added
    setOnHierarchyChangeListener(this);

    mOverlayPos = new RectF();
    mScrollFade = new ScrollFade();
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setTextAlign(Paint.Align.CENTER);
    mPaint.setTextSize(mOverlaySize / 2);
    mPaint.setColor(0xFFFFFFFF);
    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
  }// end init

  private void removeThumb() {

    mThumbVisible = false;
    // Draw one last time to remove thumb
    invalidate();
  }// end removeThumb

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);

    if (!mThumbVisible) {
      // No need to draw the rest
      return;
    }// end if (!mThumbVisible)

    final int y = mThumbY;
    final int viewWidth = getWidth();
    final FastScrollView.ScrollFade scrollFade = mScrollFade;

    int alpha = -1;
    if (scrollFade.mStarted) {
      alpha = scrollFade.getAlpha();
      if (alpha < ScrollFade.ALPHA_MAX / 2) {
        mCurrentThumb.setAlpha(alpha * 2);
      }// end if (alpha < ScrollFade.ALPHA_MAX / 2)
      int left = viewWidth - (mThumbW * alpha) / ScrollFade.ALPHA_MAX;
      mCurrentThumb.setBounds(left, 0, viewWidth, mThumbH);
      mChangedBounds = true;
    }// end if (scrollFade.mStarted)

    canvas.translate(0, y);
    mCurrentThumb.draw(canvas);
    canvas.translate(0, -y);

    // If user is dragging the scroll bar, draw the alphabet overlay
    if (mDragging && mDrawOverlay) {
      mOverlayDrawable.draw(canvas);
      final Paint paint = mPaint;
      float descent = paint.descent();
      final RectF rectF = mOverlayPos;
      canvas.drawText(mSectionText, (int) (rectF.left + rectF.right) / 2,
          (int) (rectF.bottom + rectF.top) / 2 + mOverlaySize / 4 - descent,
          paint);
    } else if (alpha == 0) {
      scrollFade.mStarted = false;
      removeThumb();
    } else {
      invalidate(viewWidth - mThumbW, y, viewWidth, y + mThumbH);
    }// end if (mDragging && mDrawOverlay)
  }// end draw

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    if (mCurrentThumb != null) {
      mCurrentThumb.setBounds(w - mThumbW, 0, w, mThumbH);
    }// end if (mCurrentThumb != null)

    final RectF pos = mOverlayPos;
    pos.left = (w - mOverlaySize) / 2;
    pos.right = pos.left + mOverlaySize;
    pos.top = h / 10; // 10% from top
    pos.bottom = pos.top + mOverlaySize;
    mOverlayDrawable.setBounds((int) pos.left, (int) pos.top, (int) pos.right,
        (int) pos.bottom);
  }// end onSizeChanged

  public void onScrollStateChanged(AbsListView view, int scrollState) {
  }// end onScrollStateChanged

  public void onScroll(AbsListView view, int firstVisibleItem,
      int visibleItemCount, int totalItemCount) {

    if (totalItemCount - visibleItemCount > 0 && !mDragging) {
      mThumbY = ((getHeight() - mThumbH) * firstVisibleItem)
          / (totalItemCount - visibleItemCount);
      if (mChangedBounds) {
        final int viewWidth = getWidth();
        mCurrentThumb.setBounds(viewWidth - mThumbW, 0, viewWidth, mThumbH);
        mChangedBounds = false;
      }// end if (mChangedBounds)
    }// end if (totalItemCount - visibleItemCount > 0 && ...
    mScrollCompleted = true;
    if (firstVisibleItem == mVisibleItem) {
      return;
    }// end if (firstVisibleItem == mVisibleItem)
    mVisibleItem = firstVisibleItem;
    if (!mThumbVisible || mScrollFade.mStarted) {
      mThumbVisible = true;
      mCurrentThumb.setAlpha(ScrollFade.ALPHA_MAX);
    }// end if (!mThumbVisible || mScrollFade.mStarted)
    mHandler.removeCallbacks(mScrollFade);
    mScrollFade.mStarted = false;
    if (!mDragging) {
      mHandler.postDelayed(mScrollFade, 1500);
    }// end if (!mDragging)
  }// end onScroll

  private void getSections() {
    Adapter adapter = mList.getAdapter();
    if (adapter instanceof HeaderViewListAdapter) {
      mListOffset = ((HeaderViewListAdapter) adapter).getHeadersCount();
      adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
    }// end if (adapter instanceof HeaderViewListAdapter)
    if (adapter instanceof SectionIndexer) {
      mListAdapter = (BaseAdapter) adapter;
      mSections = ((SectionIndexer) mListAdapter).getSections();
    }// end if (adapter instanceof SectionIndexer)
  }// end getSections

  public void onChildViewAdded(View parent, View child) {
    if (child instanceof ListView) {
      mList = (ListView) child;

      mList.setOnScrollListener(this);
      getSections();
    }// end if (child instanceof ListView)
  }// end onChildViewAdded

  public void onChildViewRemoved(View parent, View child) {
    if (child == mList) {
      mList = null;
      mListAdapter = null;
      mSections = null;
    }// end if (child == mList)
  }// end onChildViewRemoved

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (mThumbVisible && ev.getAction() == MotionEvent.ACTION_DOWN) {
      if (ev.getX() > getWidth() - mThumbW && ev.getY() >= mThumbY
          && ev.getY() <= mThumbY + mThumbH) {
        mDragging = true;
        return true;
      }// end if (ev.getX() > getWidth() - mThumbW && ...
    }// end if (mThumbVisible && ev.getAction() == MotionEvent.ACTION_DOWN)
    return false;
  }// end onInterceptTouchEvent

  private void scrollTo(float position) {
    int count = mList.getCount();
    mScrollCompleted = false;
    final Object[] sections = mSections;
    int sectionIndex;
    if (sections != null && sections.length > 1) {
      final int nSections = sections.length;
      int section = (int) (position * nSections);

      if (section >= nSections) {
        section = nSections - 1;
      }// end if (section >= nSections)

      sectionIndex = section;
      final SectionIndexer baseAdapter = (SectionIndexer) mListAdapter;
      int index = baseAdapter.getPositionForSection(section);

      // Given the expected section and index, the following code will
      // try to account for missing sections (no names starting with..)
      // It will compute the scroll space of surrounding empty sections
      // and interpolate the currently visible letter's range across the
      // available space, so that there is always some list movement while
      // the user moves the thumb.
      int nextIndex = count;
      int prevIndex = index;
      int prevSection = section;
      int nextSection = section + 1;
      // Assume the next section is unique
      if (section < nSections - 1) {
        nextIndex = baseAdapter.getPositionForSection(section + 1);
      }// end if (section < nSections - 1)

      // Find the previous index if we're slicing the previous section
      if (nextIndex == index) {
        // Non-existent letter
        while (section > 0) {
          section--;
          prevIndex = baseAdapter.getPositionForSection(section);
          if (prevIndex != index) {
            prevSection = section;
            sectionIndex = section;
            break;
          }// end if (prevIndex != index)
        }// end while (section > 0)
      }// end if (nextIndex == index)

      // Find the next index, in case the assumed next index is not
      // unique. For instance, if there is no P, then request for P's
      // position actually returns Q's. So we need to look ahead to make
      // sure that there is really a Q at Q's position. If not, move
      // further down...
      int nextNextSection = nextSection + 1;
      while (nextNextSection < nSections
          && baseAdapter.getPositionForSection(nextNextSection) == nextIndex) {
        nextNextSection++;
        nextSection++;
      }// end while
      // Compute the beginning and ending scroll range percentage of the
      // currently visible letter. This could be equal to or greater than
      // (1 / nSections).
      float fPrev = (float) prevSection / nSections;
      float fNext = (float) nextSection / nSections;
      index = prevIndex
          + (int) ((nextIndex - prevIndex) * (position - fPrev) / (fNext - fPrev));
      // Don't overflow
      if (index > count - 1)
        index = count - 1;

      mList.setSelectionFromTop(index + mListOffset, 0);
    } else {
      int index = (int) (position * count);
      mList.setSelectionFromTop(index + mListOffset, 0);
      sectionIndex = -1;
    }// end if (index > count - 1)

    if (sectionIndex >= 0) {
      String text = mSectionText = sections[sectionIndex].toString();
      mDrawOverlay = (text.length() != 1 || text.charAt(0) != ' ')
          && sectionIndex < sections.length;
    } else {
      mDrawOverlay = false;
    }// end if (sectionIndex >= 0)
  }// end scrollTo

  private void cancelFling() {
    // Cancel the list fling
    MotionEvent cancelFling = MotionEvent.obtain(0, 0,
        MotionEvent.ACTION_CANCEL, 0, 0, 0);
    mList.onTouchEvent(cancelFling);
    cancelFling.recycle();
  }// end cancelFling

  @Override
  public boolean onTouchEvent(MotionEvent me) {
    if (me.getAction() == MotionEvent.ACTION_DOWN) {
      if (me.getX() > getWidth() - mThumbW && me.getY() >= mThumbY
          && me.getY() <= mThumbY + mThumbH) {

        mDragging = true;
        if (mListAdapter == null && mList != null) {
          getSections();
        }

        cancelFling();
        return true;
      }// end if (me.getX() > getWidth() - mThumbW && ...
    } else if (me.getAction() == MotionEvent.ACTION_UP) {
      if (mDragging) {
        mDragging = false;
        final Handler handler = mHandler;
        handler.removeCallbacks(mScrollFade);
        handler.postDelayed(mScrollFade, 1000);
        return true;
      }// end if (mDragging)
    } else if (me.getAction() == MotionEvent.ACTION_MOVE) {
      if (mDragging) {
        final int viewHeight = getHeight();
        mThumbY = (int) me.getY() - mThumbH + 10;
        if (mThumbY < 0) {
          mThumbY = 0;
        } else if (mThumbY + mThumbH > viewHeight) {
          mThumbY = viewHeight - mThumbH;
        }// end if (mThumbY < 0)
        // If the previous scrollTo is still pending
        if (mScrollCompleted) {
          scrollTo((float) mThumbY / (viewHeight - mThumbH));
        }
        return true;
      }// end if (mDragging)
    }// end if (me.getAction() == MotionEvent.ACTION_DOWN)

    return super.onTouchEvent(me);
  }// end onTouchEvent

  public class ScrollFade implements Runnable {

    long mStartTime;
    long mFadeDuration;
    boolean mStarted;
    static final int ALPHA_MAX = 255;
    static final long FADE_DURATION = 200;

    void startFade() {
      mFadeDuration = FADE_DURATION;
      mStartTime = SystemClock.uptimeMillis();
      mStarted = true;
    }// end startFade

    int getAlpha() {
      if (!mStarted) {
        return ALPHA_MAX;
      }
      int alpha;
      long now = SystemClock.uptimeMillis();
      if (now > mStartTime + mFadeDuration) {
        alpha = 0;
      } else {
        alpha = (int) (ALPHA_MAX - ((now - mStartTime) * ALPHA_MAX)
            / mFadeDuration);
      }
      return alpha;
    }// end getAlpha

    public void run() {
      if (!mStarted) {
        startFade();
        invalidate();
      }

      if (getAlpha() > 0) {
        final int y = mThumbY;
        final int viewWidth = getWidth();
        invalidate(viewWidth - mThumbW, y, viewWidth, y + mThumbH);
      } else {
        mStarted = false;
        removeThumb();
      }// end if (getAlpha() > 0)
    }// end run
  }// end ScrollFade
}// end FastScroll
