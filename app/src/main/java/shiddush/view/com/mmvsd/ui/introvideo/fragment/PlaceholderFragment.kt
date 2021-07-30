package shiddush.view.com.mmvsd.ui.introvideo.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_intro_notes.view.*
import shiddush.view.com.mmvsd.R
import java.util.*

class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            var position = arguments!!.getInt(EXTRA_POSITION)
            val view = inflater.inflate(R.layout.fragment_intro_notes, container, false)

            val introNotes = resources.getStringArray(R.array.intro_notes)
            val introNotes1 = resources.getStringArray(R.array.intro_notes_1)

                view.textViewNote1.text = introNotes.get(position)
                view.textViewNote2.text = introNotes1.get(position)


            return view
        }

        companion object {
            private const val EXTRA_POSITION = "EXTRA_POSITION"

            fun newInstance(position: Int) = PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_POSITION, position)
                }
            }
        }

    }
