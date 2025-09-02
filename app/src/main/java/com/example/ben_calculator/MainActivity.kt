package com.example.ben_calculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.views.HyBidBannerAdView
import net.pubnative.lite.sdk.views.PNAdView
import java.math.BigDecimal
import android.app.Application

class App : Application(){
    override fun onCreate(){
        super.onCreate()
        HyBid.initialize("dde3c298b47648459f8ada4a982fa92d", this)
        HyBid.setTestMode(true)
    }
}

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var LCDFrame: TextView
    private var mBanner: HyBidBannerAdView? = null
    private var mInterstitial: HyBidInterstitialAd? = null
    private lateinit var Input1Frame: TextView
    private lateinit var Input2Frame: TextView
    private lateinit var OperatorFrame: TextView
    private var input1: String = ""
    private var input2: String = ""
    private var lastResult: String = ""
    private var operatorValue: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LCDFrame = findViewById(R.id.LCDFrame)
        Input1Frame = findViewById(R.id.Input1Frame) // debug
        Input2Frame = findViewById(R.id.Input2Frame) // debug
        OperatorFrame = findViewById(R.id.OperatorFrame) // debug
        mBanner = findViewById(R.id.hybid_banner)
        Log.d("APP", "applicationId=${packageName}")

        // Check Initialization
        if (HyBid.isInitialized()) {
            Log.d("HyBid", "Good to go, SDK is ready")
            HyBid.setTestMode(true)
            // loadBanner()
        } else {
            Log.e("HyBid", "SDK not initialized yet!")
        }

        // Numbers
        val numberIds = intArrayOf(
            R.id.num1, R.id.num2, R.id.num3,
            R.id.num4, R.id.num5, R.id.num6,
            R.id.num7, R.id.num8, R.id.num9,
            R.id.num0
        )
        for (id in numberIds) {
            val b: Button = findViewById(id)
            b.setOnClickListener(this)
        }

        // Add, Subtract, Multiply, Divide
        val fIds = intArrayOf(R.id.fAdd, R.id.fSub, R.id.fMul, R.id.fDiv)
        for (id in fIds) {
            val b: Button = findViewById(id)
            b.setOnClickListener(this)
        }

        // Clear
        findViewById<View>(R.id.fClr).setOnClickListener(this)

        // Calculate
        findViewById<View>(R.id.fCalc).setOnClickListener(this)
    }

    private fun loadBanner() {
        Log.d("HyBid", "loadBanner accessed")
        mBanner?.load("1", object : PNAdView.Listener {
            override fun onAdLoaded() {
                Log.e("HyBid", "Ad Loaded :)")
            }

            override fun onAdLoadFailed(error: Throwable) {
                Log.e("HyBid", "Failed to load ad :(")
            }

            override fun onAdImpression() {
                // no-op
            }

            override fun onAdClick() {
                // no-op
            }
        })
    }

    private fun loadInterstitial() {
        Log.d("HyBid", "Loading interstitial")
        mInterstitial = HyBidInterstitialAd(this, "1", object : HyBidInterstitialAd.Listener {
            override fun onInterstitialLoaded() {
                mInterstitial?.show()
            }

            override fun onInterstitialLoadFailed(error: Throwable) {
                // no-op
            }

            override fun onInterstitialImpression() {
                // no-op
            }

            override fun onInterstitialDismissed() {
                // no-op
            }

            override fun onInterstitialClick() {
                // no-op
            }
        })
    }

    override fun onDestroy() {
        mBanner?.destroy()
        super.onDestroy()
    }

    override fun onClick(v: View) {
        val b = v as Button
        val id = v.id
        val buttonText = b.text.toString()

        if (id == R.id.fAdd || id == R.id.fSub || id == R.id.fMul || id == R.id.fDiv) {
            if (input1 != "") {
                if (input2 != "") {
                    input1 = lastResult
                    input2 = ""
                    Input1Frame.text = input1
                    Input2Frame.text = ""
                    LCDFrame.text = buttonText
                }
                operatorValue = buttonText
                OperatorFrame.text = buttonText // Debug
            }
        } else if (id != R.id.fCalc) { // if the user clicked a number
            if (operatorValue == "") {
                input1 += buttonText
                Input1Frame.text = input1 // Debug
                LCDFrame.text = input1
            } else {
                input2 += buttonText
                Input2Frame.text = input2 // Debug
                LCDFrame.text = input2
            }
        }

        if (id == R.id.fClr) {
            input1 = ""
            input2 = ""
            operatorValue = ""
            LCDFrame.text = "0"
            Input1Frame.text = "" // Debug
            Input2Frame.text = "" // Debug
            OperatorFrame.text = "" // Debug
        } else if (id == R.id.fCalc) {
            try {
                val result = calculate()
                lastResult = BigDecimal.valueOf(result).stripTrailingZeros().toPlainString()
                LCDFrame.text = lastResult
                operatorValue = ""
            } catch (e: Exception) {
                LCDFrame.text = "Error"
                input1 = ""
            }
            loadInterstitial()
        }
    }

    private fun calculate(): Double {
        var result = 0.0
        when (operatorValue) {
            "+" -> result = input1.toDouble() + input2.toDouble()
            "-" -> result = input1.toDouble() - input2.toDouble()
            "*" -> result = input1.toDouble() * input2.toDouble()
            "/" -> result = input1.toDouble() / input2.toDouble()
            // "" -> result = input1.toDouble()
        }
        return result
    }
}
