package com.fairfareindia.ui.disputs

import android.R
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import java.util.*

class MultipleSelectionSpinner : AppCompatSpinner, OnMultiChoiceClickListener {
    var _items: Array<String>? = null
    var mSelection: BooleanArray? = null
    var simple_adapter: ArrayAdapter<String>
    var isClick : Boolean?=false
    private var sbLength = 0
    val numbers = ArrayList<Int>()

    constructor(context: Context?) : super(context!!) {
        simple_adapter = ArrayAdapter(
            context!!,
            R.layout.simple_spinner_item
        )
        super.setAdapter(simple_adapter)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
        simple_adapter = ArrayAdapter(
            context!!,
            R.layout.simple_spinner_item
        )
        super.setAdapter(simple_adapter)
    }

    override fun onClick(
        dialog: DialogInterface,
        which: Int,
        isChecked: Boolean
    ) {
        if (mSelection != null && which < mSelection!!.size) {

            if(numbers.size<3) {
                mSelection!![which] = isChecked
                if (isChecked == true) {
                    numbers.add(which)

                } else {
                    numbers.remove(which)
                }
                simple_adapter.clear()
                simple_adapter.add("Tap to select")
            }else{

                isChecked == false
                mSelection!![which] = false
              //  simple_adapter.notifyDataSetChanged()

            }
            /* if (buildSelectedItemString().length > 0) {
                 simple_adapter.add(buildSelectedItemString())
             } else {
                 simple_adapter.add("Tap to select")
             }*/
        } else {
            throw IllegalArgumentException(
                "Argument 'which' is out of bounds")
        }
    }

    override fun performClick(): Boolean {
        val builder = AlertDialog.Builder(context)
        builder.setMultiChoiceItems(_items, mSelection, this)
        builder.setCancelable(false)
        builder.setPositiveButton("Ok") { arg0, arg1 -> }
        if (numbers.size > 3){
            Toast.makeText(getContext(), "Cannot select more than 3", Toast.LENGTH_SHORT).show()
            return false
        }

        builder.show()
        return true
    }

    override fun setAdapter(adapter: SpinnerAdapter) {
        throw RuntimeException(
            "setAdapter is not supported by MultiSelectSpinner."
        )
    }

    fun setItems(items: Array<String>?) {
        _items = items
        mSelection = BooleanArray(_items!!.size)
        simple_adapter.clear()
        simple_adapter.add(_items!![0])
        Arrays.fill(mSelection, false)
    }

    fun setItems(items: List<String>) {
        _items = items.toTypedArray()
        mSelection = BooleanArray(_items!!.size)
        simple_adapter.clear()
        simple_adapter.add("Tap to select")
        ///simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false)
    }

    fun setSelection(selection: Array<String>) {
        for (cell in selection) {
            for (j in _items!!.indices) {
                if (_items!![j] == cell) {
                    mSelection!![j] = true
                }
            }
        }
    }

    fun setSelection(selection: List<String>) {
        for (i in mSelection!!.indices) {
            mSelection!![i] = false
        }
        for (sel in selection) {
            for (j in _items!!.indices) {
                if (_items!![j] == sel) {
                    mSelection!![j] = true
                }
            }
        }
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
    }

    fun myNumbers(): ArrayList<Int>? {
        return numbers
    }


    override fun setSelection(index: Int) {
        for (i in mSelection!!.indices) {
            mSelection!![i] = false
        }
        if (index >= 0 && index < mSelection!!.size) {
            mSelection!![index] = true
        } else {
            throw IllegalArgumentException(
                "Index " + index
                        + " is out of bounds."
            )
        }
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
        /*if (sbLength>0){
            Toast.makeText(getContext(), "Length greater than zero", Toast.LENGTH_SHORT).show();
            simple_adapter.add(buildSelectedItemString());
        }else{
            Toast.makeText(getContext(), "Length shorter", Toast.LENGTH_SHORT).show();
            simple_adapter.add("Tap to select");
        }*/
    }

    fun setSelection(selectedIndicies: IntArray) {
        for (i in mSelection!!.indices) {
            mSelection!![i] = false
        }
        for (index in selectedIndicies) {
            if (index >= 0 && index < mSelection!!.size) {
                mSelection!![index] = true
            } else {
                throw IllegalArgumentException(
                    "Index " + index
                            + " is out of bounds."
                )
            }
        }
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
    }

    val selectedStrings: List<String>
        get() {
            val selection: MutableList<String> =
                LinkedList()
            for (i in _items!!.indices) {
                if (mSelection!![i]) {
                    selection.add(_items!![i])
                }
            }
            return selection
        }

    val selectedIndicies: List<Int>
        get() {
            val selection: MutableList<Int> = LinkedList()
            for (i in _items!!.indices) {
                if (mSelection!![i]) {
                    selection.add(i)
                }
            }
            return selection
        }

     fun buildSelectedItemString(): String {
        val sb = StringBuilder()
        var foundOne = false
        for (i in _items!!.indices) {
            if (mSelection!![i]) {
                if (foundOne) {
                    sb.append(", ")
                }
                foundOne = true
                sb.append(_items!![i])
            }
        }

        //Log.e("sb length",""+sb.length());
        sbLength = sb.length
        return sb.toString()
    }

    /*String sbCheck;
        if (sb.length()>0){
           sbCheck=sb.toString();
        }else{
            sbCheck="Tap to select";
        }*/
    val selectedItemsAsString: String
        get() {
            val sb = StringBuilder()
            var foundOne = false
            for (i in _items!!.indices) {
                if (mSelection!![i]) {
                    if (foundOne) {
                        sb.append(", ")
                    }
                    foundOne = true
                    sb.append(_items!![i])
                }
            }
            /*String sbCheck;
        if (sb.length()>0){
           sbCheck=sb.toString();
        }else{
            sbCheck="Tap to select";
        }*/return sb.toString()
        }
}