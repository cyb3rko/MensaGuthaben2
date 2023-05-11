package com.cyb3rko.mensaguthaben2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.cyb3rko.mensaguthaben2.cardreader.ValueData
import de.yazo_games.mensaguthaben.R
import java.util.Currency
import java.util.Locale

class ValueFragment : Fragment() {
    var valueData: ValueData? = null
        set(value) {
            field = value
            if (this::tvCurrentValue.isInitialized) updateView()
        }
    private lateinit var tvCurrentValue: TextView
    private lateinit var tvLastValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_value, container, false)
        tvCurrentValue = v.findViewById(R.id.current)
        tvLastValue = v.findViewById(R.id.last)
        ViewCompat.setTransitionName(tvCurrentValue, "current")
        ViewCompat.setTransitionName(tvLastValue, "last")
        if (savedInstanceState != null) {
            valueData = savedInstanceState.getSerializable(VALUE) as ValueData?
        }
        updateView()
        return v
    }

    private fun moneyStr(i: Int): String {
        val germany = Locale.GERMANY
        val currencySymbol = Currency.getInstance(germany).symbol
        val amount = i.toFloat() / 1000
        return String.format(germany, "%.2f%s", amount, currencySymbol)
    }

    private fun updateView() {
        if (valueData == null) {
            tvCurrentValue.text = getString(R.string.place_on_card)
            tvLastValue.visibility = View.GONE
        } else {
            val current = moneyStr(valueData!!.value)
            tvCurrentValue.text = current
            if (valueData!!.lastTransaction != null) {
                val last = moneyStr(valueData!!.lastTransaction!!)
                tvLastValue.text = getString(R.string.last_withdrawal, last)
                tvLastValue.visibility = View.VISIBLE
            } else {
                tvLastValue.visibility = View.GONE
            }
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        bundle.putSerializable(VALUE, valueData)
    }

    companion object {
        const val VALUE = "value"
    }
}
