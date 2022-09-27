package me.saeha.android.navermovie_project

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat

class CustomEditText : AppCompatEditText, TextWatcher, View.OnTouchListener, View.OnFocusChangeListener{
    private lateinit var clearDrawable: Drawable
    private lateinit var onFocusChangeListener: OnFocusChangeListener
    private lateinit var onTouchListener: OnTouchListener
    private var count: Int = 0

    constructor(context: Context?) : super(context!!){
        init()
    }
    constructor(context: Context?,attrs: AttributeSet?) : super(context!!, attrs){
        init()
    }
    constructor(context: Context?,attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr){
        init()
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener) {
        onFocusChangeListener = l
        count = 1
    }

    override fun setOnTouchListener(l: OnTouchListener) {
        onTouchListener = l
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun onTouch(p0: View?, event: MotionEvent): Boolean {
        try {
            val eventXNum = event.x.toInt()
            //x버튼을 터치를 했을 때 텍스트와 에러 초기화
            if (clearDrawable.isVisible&& eventXNum >width-paddingRight- clearDrawable.intrinsicWidth) {
                if (event.action== MotionEvent.ACTION_UP) {
                    error= null
                    text= null
                }
                return true
            }

            return onTouchListener.onTouch(p0, event)
        }catch(e: Exception){
            return false
        }
    }

    override fun onFocusChange(p0: View?, hasFocus: Boolean) {
        if(hasFocus &&text!= null) setClearIconVisible(text!!.isNotEmpty())
        else setClearIconVisible(false)

        //입력값이 들어왔을 때
        if(count == 1){
            onFocusChangeListener.onFocusChange(p0,hasFocus)
        }
    }

    override fun getOnFocusChangeListener(): OnFocusChangeListener {
        return super.getOnFocusChangeListener()
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if(isFocused) setClearIconVisible(text!!.isNotEmpty())
    }

    private fun setClearIconVisible(visible: Boolean){
        clearDrawable.setVisible(visible, false)
        setCompoundDrawables(null, null, if(visible) clearDrawable else null, null)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(){
        //삭제 아이콘
        clearDrawable = DrawableCompat.wrap(
            (ResourcesCompat.getDrawable(resources,R.drawable.ic_circle_cross_gray_20,null)as Drawable)
        )
        DrawableCompat.setTintList(clearDrawable,hintTextColors)
        clearDrawable.setBounds(0, 0, clearDrawable.intrinsicWidth, clearDrawable.intrinsicHeight)

        clearDrawable.colorFilter= PorterDuffColorFilter(context.getColor(R.color.gray), PorterDuff.Mode.SRC_IN)

        //기본 상태는 false
        setClearIconVisible(false)
        super.setOnTouchListener(this)
        super.setOnFocusChangeListener(this)
        addTextChangedListener(this)
    }
}