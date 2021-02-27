package com.agog.latex

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import com.agog.mathdisplay.MTFontManager
import com.agog.mathdisplay.MTMathView
import com.agog.mathdisplay.MTMathView.MTMathViewMode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    var menufont: MenuItem? = null
    var menumode: MenuItem? = null
    var menusize: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createEquations()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menufont = menu.findItem(R.id.menufont)
        menumode = menu.findItem(R.id.menumode)
        menusize = menu.findItem(R.id.menusize)
        return true
    }


    var sampleEquations: MutableList<MTMathView> = mutableListOf()
    var defaultFontSize = 20.0f  // Starting fontsize dp pixels

    private fun createEquations() {
        val layoutPadHoriz = 24
        val layoutPadVert = 42

        // Some padding around the equations. Cosmetic
        val layoutParams = LinearLayout.LayoutParams(0, 0)
        layoutParams.setMargins(layoutPadHoriz, layoutPadVert, layoutPadHoriz, layoutPadVert)

        /*
             We read a plain text file with LaTeX strings and comments
             Each LaTeX string is placed in a separate MTMathViews.
             Comments are placed in TextViews.
             All are placed into a view to scroll vertically and horizontally.
         */
        val inputStream = resources.openRawResource(R.raw.samples)
        val lineList = mutableListOf<String>()

        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }

        lineList.forEach {
            if (it.isNotBlank()) {
                if (it[0] == '#') {
                    val tv = TextView(this)
                    tv.text = it.trim()
                    tv.setTextColor(Color.DKGRAY)
                    println("textSize ${tv.textSize}")
                    mainLayout.addView(tv)
                } else {
                    val mathView = MTMathView(this)
                    mathView.fontSize = MTMathView.convertDpToPixel(defaultFontSize)
                    mathView.latex = it
                    sampleEquations.add(mathView)
                    mainLayout.addView(mathView, layoutParams)
                }
            }
        }


    }


    private fun applyFont(fontname: String) {
        for (eq in sampleEquations) {
            eq.font = MTFontManager.fontWithName(fontname, eq.fontSize)
        }
    }

    private fun applyFontSize(fontsize: Float) {
        val pixelFontSize = MTMathView.convertDpToPixel(fontsize)
        for (eq in sampleEquations) {
            eq.font = MTFontManager.fontWithName(eq.font!!.name, pixelFontSize)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            // Font menu
            R.id.fontdefault -> {
                applyFont("latinmodern-math")
                fontMenuCheck(R.id.fontdefault)
                return true
            }
            R.id.fonttermes -> {
                applyFont("texgyretermes-math")
                fontMenuCheck(R.id.fonttermes)
                return true
            }
            R.id.fontxits -> {
                applyFont("xits-math")
                fontMenuCheck(R.id.fontxits)
                return true
            }
            // Size Menu
            R.id.f12 -> {
                applyFontSize(12.0f)
                sizeMenuCheck(R.id.f12)
                return true
            }
            R.id.f16 -> {
                applyFontSize(16.0f)
                sizeMenuCheck(R.id.f16)
                return true
            }
            R.id.f20 -> {
                applyFontSize(20.0f)
                sizeMenuCheck(R.id.f20)
                return true
            }
            R.id.f40 -> {
                applyFontSize(40.0f)
                sizeMenuCheck(R.id.f40)
                return true
            }
            // Mode Menu
            R.id.modedisplay -> {
                for (eq in sampleEquations) {
                    eq.labelMode = MTMathViewMode.KMTMathViewModeDisplay
                }
                modeMenuCheck(R.id.modedisplay)
                return true
            }
            R.id.modetext -> {
                for (eq in sampleEquations) {
                    eq.labelMode = MTMathViewMode.KMTMathViewModeText
                }
                modeMenuCheck(R.id.modetext)
                return true
            }
            // Good for profiling
            R.id.modereload -> {
                mainLayout.removeAllViewsInLayout()
                sampleEquations.clear()
                mainLayout.invalidate()
                // Nice to see the screen show blank before adding back equations
                mainLayout.postDelayed({ createEquations() }, 10)
                sizeMenuCheck(R.id.f20)
                modeMenuCheck(R.id.modedisplay)
                fontMenuCheck(R.id.fontdefault)
                return true
            }


            // Color Menu
            R.id.colorblack -> {
                for (eq in sampleEquations) {
                    eq.textColor = Color.BLACK
                }
                return true
            }
            R.id.colorpurple -> {
                for (eq in sampleEquations) {
                    eq.textColor = Color.MAGENTA
                }
                return true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

    // Utility functions to make sure menu checked state is correct
    private fun menuCheck(menu: MenuItem, itemId: Int) {
        val m = menu.subMenu
        for (i in 0 until m.size()) {
            val mi = m.getItem(i)
            mi.isChecked = mi.itemId == itemId
        }
    }

    private fun fontMenuCheck(itemId: Int) {
        if (menufont != null) {
            menuCheck(menufont!!, itemId)
        }
    }

    private fun modeMenuCheck(itemId: Int) {
        if (menumode != null) {
            menuCheck(menumode!!, itemId)
        }
    }

    private fun sizeMenuCheck(itemId: Int) {
        if (menusize != null) {
            menuCheck(menusize!!, itemId)
        }
    }


}
