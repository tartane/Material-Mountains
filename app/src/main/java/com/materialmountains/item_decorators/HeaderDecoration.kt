package com.materialmountains.item_decorators

import androidx.recyclerview.widget.RecyclerView
import android.graphics.*
import android.view.View


class HeaderDecoration(
    private val mView: View,
    private val mHorizontal: Boolean,
    private val mParallax: Float,
    private val mShadowSize: Float,
    private val mColumns: Int
) : RecyclerView.ItemDecoration() {
    private val mShadowPaint: Paint?

    init {

        if (mShadowSize > 0) {
            mShadowPaint = Paint()
            mShadowPaint!!.setShader(
                if (mHorizontal)
                    LinearGradient(
                        mShadowSize, 0f, 0f, 0f,
                        intArrayOf(
                            Color.argb(55, 0, 0, 0),
                            Color.argb(55, 0, 0, 0),
                            Color.argb(3, 0, 0, 0)
                        ),
                        floatArrayOf(0f, .5f, 1f),
                        Shader.TileMode.CLAMP
                    )
                else
                    LinearGradient(
                        0f, mShadowSize, 0f, 0f,
                        intArrayOf(
                            Color.argb(55, 0, 0, 0),
                            Color.argb(55, 0, 0, 0),
                            Color.argb(3, 0, 0, 0)
                        ),
                        floatArrayOf(0f, .5f, 1f),
                        Shader.TileMode.CLAMP
                    )
            )
        } else {
            mShadowPaint = null
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        // layout basically just gets drawn on the reserved space on top of the first view
        mView.layout(parent.left, 0, parent.right, mView.getMeasuredHeight())

        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            if (parent.getChildAdapterPosition(view) == 0) {
                c.save()
                if (mHorizontal) {
                    c.clipRect(parent.left, parent.top, view.left, parent.bottom)
                    val width = mView.getMeasuredWidth()
                    val left = (view.left - width) * mParallax
                    c.translate(left, 0f)
                    mView.draw(c)
                    if (mShadowSize > 0) {
                        c.translate(view.left - left - mShadowSize, 0f)
                        c.drawRect(
                            parent.left.toFloat(),
                            parent.top.toFloat(),
                            mShadowSize,
                            parent.bottom.toFloat(),
                            mShadowPaint!!
                        )
                    }
                } else {
                    c.clipRect(parent.left, parent.top, parent.right, view.top)
                    val height = mView.getMeasuredHeight()
                    val top = (view.top - height) * mParallax
                    c.translate(0f, top)
                    mView.draw(c)
                    if (mShadowSize > 0) {
                        c.translate(0f, view.top - top - mShadowSize)
                        c.drawRect(parent.left.toFloat(), parent.top.toFloat(), parent.right.toFloat(), mShadowSize, mShadowPaint!!)
                    }
                }
                c.restore()
                break
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.getChildAdapterPosition(view) < mColumns) {
            if (mHorizontal) {
                if (mView.getMeasuredWidth() <= 0) {
                    mView.measure(
                        View.MeasureSpec.makeMeasureSpec(
                            parent.measuredWidth,
                            View.MeasureSpec.AT_MOST
                        ),
                        View.MeasureSpec.makeMeasureSpec(
                            parent.measuredHeight,
                            View.MeasureSpec.AT_MOST
                        )
                    )
                }
                outRect.set(mView.getMeasuredWidth(), 0, 0, 0)
            } else {
                if (mView.getMeasuredHeight() <= 0) {
                    mView.measure(
                        View.MeasureSpec.makeMeasureSpec(
                            parent.measuredWidth,
                            View.MeasureSpec.AT_MOST
                        ),
                        View.MeasureSpec.makeMeasureSpec(
                            parent.measuredHeight,
                            View.MeasureSpec.AT_MOST
                        )
                    )
                }
                outRect.set(0, mView.getMeasuredHeight(), 0, 0)
            }
        } else {
            outRect.setEmpty()
        }
    }
}