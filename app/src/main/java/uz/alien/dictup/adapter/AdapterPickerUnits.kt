package uz.alien.dictup.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.RecyclerView
import uz.alien.dictup.R
import uz.alien.dictup.activity.BaseActivity
import uz.alien.dictup.activity.SelectorActivity
import uz.alien.dictup.activity.LessonActivity
import uz.alien.dictup.activity.MainActivity.Companion.downloadAndSaveFiles
import uz.alien.dictup.activity.MainActivity.Companion.showInitialDialog
import uz.alien.dictup.databinding.ItemLessonUnitBinding
import uz.alien.dictup.manager.AppDictionary
import uz.alien.dictup.manager.PrefsManager
import uz.alien.dictup.manager.PrefsManager.Companion.BEGINNER_EN
import uz.alien.dictup.manager.PrefsManager.Companion.BEGINNER_STORY
import uz.alien.dictup.manager.PrefsManager.Companion.BEGINNER_UZ
import uz.alien.dictup.manager.PrefsManager.Companion.ESSENTIAL_EN
import uz.alien.dictup.manager.PrefsManager.Companion.ESSENTIAL_STORY
import uz.alien.dictup.manager.PrefsManager.Companion.ESSENTIAL_UZ
import uz.alien.dictup.receiver.ConnectionReceiver.Companion.isConnected
import uz.alien.dictup.utils.OnAllUnitsAnimatedListener
import kotlin.random.Random.Default.nextInt

class AdapterPickerUnits(private val amount: Int, private val activity: BaseActivity, private val onAllUnitsAnimatedListener: OnAllUnitsAnimatedListener? = null)
  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  val TAG = AdapterPickerUnits::class.java.simpleName

  class UnitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemLessonUnitBinding.bind(view)
  }

  override fun getItemCount(): Int {
    return amount
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return UnitViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lesson_unit, parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is UnitViewHolder) {

      holder.binding.root.setOnClickListener {

        val book = activity.intent.getIntExtra("book", 0)
        val pick = activity.intent.getIntExtra("pick", 0)

        when (book) {
          0 -> {

            if (PrefsManager.getInstance(activity).getInt(BEGINNER_EN) != 0) {
              AppDictionary.initBeginnerEn(activity)
            } else {
              if (isConnected(activity)) {
                downloadAndSaveFiles(activity, "beginner_en", BEGINNER_EN) {
                  AppDictionary.initBeginnerEn(activity)
                  Log.d("Network", "Downloaded!")
                  activity.runOnUiThread {
                    Toast.makeText(activity, "Downloaded!", Toast.LENGTH_SHORT).show()
                  }
                }
              } else {
                showInitialDialog(activity)
              }
            }

            if (PrefsManager.getInstance(activity).getInt(BEGINNER_UZ) != 0) {
              AppDictionary.initBeginnerUz(activity)
            } else {
              if (isConnected(activity)) {
                downloadAndSaveFiles(activity, "beginner_uz", BEGINNER_UZ) {
                  AppDictionary.initBeginnerUz(activity)
                  Log.d("Network", "Downloaded!")
                  activity.runOnUiThread {
                    Toast.makeText(activity, "Downloaded!", Toast.LENGTH_SHORT).show()
                  }
                }
              } else {
                showInitialDialog(activity)
              }
            }

            if (PrefsManager.getInstance(activity).getInt(BEGINNER_STORY) != 0) {
              AppDictionary.initBeginnerStory(activity)
            } else {
              if (isConnected(activity)) {
                downloadAndSaveFiles(activity, "beginner_story", BEGINNER_STORY) {
                  AppDictionary.initBeginnerStory(activity)
                  Log.d("Network", "Downloaded!")
                  activity.runOnUiThread {
                    Toast.makeText(activity, "Downloaded!", Toast.LENGTH_SHORT).show()
                  }
                }
              } else {
                showInitialDialog(activity)
              }
            }

            if (
              PrefsManager.getInstance(activity).getInt(BEGINNER_EN) != 0 &&
              PrefsManager.getInstance(activity).getInt(BEGINNER_UZ) != 0 &&
              PrefsManager.getInstance(activity).getInt(BEGINNER_STORY) != 0
            ) {
              val intent = Intent(activity, LessonActivity::class.java)
              intent.putExtra("book", book)
              intent.putExtra("pick", pick)
              intent.putExtra("unit", position)
              activity.setOpenZoomAnimation(intent)
            } else {
              activity.runOnUiThread {
                Toast.makeText(activity, "Kerakli fayllar mavjud emas!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Beginner booklar yuklanmagan!")
              }  
            }
          }
          1 -> {

            if (PrefsManager.getInstance(activity).getInt(ESSENTIAL_EN) != 0) {
              AppDictionary.initEssentialEn(activity)
            } else {
              if (isConnected(activity)) {
                downloadAndSaveFiles(activity, "essential_en", ESSENTIAL_EN) {
                  AppDictionary.initEssentialEn(activity)
                  Log.d("Network", "Downloaded!")
                  activity.runOnUiThread {
                    Toast.makeText(activity, "Downloaded!", Toast.LENGTH_SHORT).show()
                  }
                }
              } else {
                showInitialDialog(activity)
              }
            }

            if (PrefsManager.getInstance(activity).getInt(ESSENTIAL_UZ) != 0) {
              AppDictionary.initEssentialUz(activity)
            } else {
              if (isConnected(activity)) {
                downloadAndSaveFiles(activity, "essential_uz", BEGINNER_UZ) {
                  AppDictionary.initEssentialUz(activity)
                  Log.d("Network", "Downloaded!")
                  activity.runOnUiThread {
                    Toast.makeText(activity, "Downloaded!", Toast.LENGTH_SHORT).show()
                  }
                }
              } else {
                showInitialDialog(activity)
              }
            }

            if (PrefsManager.getInstance(activity).getInt(ESSENTIAL_STORY) != 0) {
              AppDictionary.initEssentialStory(activity)
            } else {
              if (isConnected(activity)) {
                downloadAndSaveFiles(activity, "essential_story", ESSENTIAL_STORY) {
                  AppDictionary.initEssentialStory(activity)
                  Log.d("Network", "Downloaded!")
                  activity.runOnUiThread {
                    Toast.makeText(activity, "Downloaded!", Toast.LENGTH_SHORT).show()
                  }
                }
              } else {
                showInitialDialog(activity)
              }
            }

            if (
              PrefsManager.getInstance(activity).getInt(ESSENTIAL_EN) != 0 &&
              PrefsManager.getInstance(activity).getInt(ESSENTIAL_UZ) != 0 &&
              PrefsManager.getInstance(activity).getInt(ESSENTIAL_STORY) != 0
            ) {
              val intent = Intent(activity, LessonActivity::class.java)
              intent.putExtra("book", book)
              intent.putExtra("pick", pick)
              intent.putExtra("unit", position)
              activity.setOpenZoomAnimation(intent)
            } else {
              activity.runOnUiThread {
                Toast.makeText(activity, "Kerakli fayllar mavjud emas!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Essential booklar yuklanmagan!")
              }
            }
          }
        }
      }

      holder.binding.tvUnit.setText("unit ${position + 1}")

      val percent = nextInt(0, 100)

      val startColor = ContextCompat.getColor(activity, R.color.lesson_progress_indicator_low)
      val endColor = ContextCompat.getColor(activity, R.color.lesson_progress_indicator)

      // startColor va endColor ni HSL ga aylantirish
      val startHSL = FloatArray(3)
      val endHSL = FloatArray(3)
      ColorUtils.colorToHSL(startColor, startHSL)
      ColorUtils.colorToHSL(endColor, endHSL)

      if (SelectorActivity.withAnimation) {
        val animator = ValueAnimator.ofInt(0, percent)
        animator.duration = activity.duration * 4
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
          val animatedValue = animation.animatedValue as Int

          // progress va foiz yangilanishi
          holder.binding.progress.progress = animatedValue
          holder.binding.tvPercent.text = "$animatedValue%"

          val fraction = animatedValue / 100f

          // Hue (rang burchagi) bo‘yicha interpolatsiya
          val currentHSL = FloatArray(3)
          currentHSL[0] = startHSL[0] + (endHSL[0] - startHSL[0]) * fraction
          currentHSL[1] = startHSL[1] + (endHSL[1] - startHSL[1]) * fraction
          currentHSL[2] = startHSL[2] + (endHSL[2] - startHSL[2]) * fraction

          val blendedColor = ColorUtils.HSLToColor(currentHSL)
          holder.binding.progress.progressTintList = ColorStateList.valueOf(blendedColor)
          holder.binding.progress.setIndicatorColor(blendedColor)
        }

        if (position == 0) {
          animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
              onAllUnitsAnimatedListener?.onAllUnitsAnimated()
            }
          })
        }

        holder.binding.progress.progress = 0
        activity.handler.postDelayed(activity.duration * 5) {
          animator.start()
        }
      } else {
        val animatedValue = percent

        // progress va foiz yangilanishi
        holder.binding.progress.progress = animatedValue
        holder.binding.tvPercent.text = "$animatedValue%"

        val fraction = animatedValue / 100f

        // Hue (rang burchagi) bo‘yicha interpolatsiya
        val currentHSL = FloatArray(3)
        currentHSL[0] = startHSL[0] + (endHSL[0] - startHSL[0]) * fraction
        currentHSL[1] = startHSL[1] + (endHSL[1] - startHSL[1]) * fraction
        currentHSL[2] = startHSL[2] + (endHSL[2] - startHSL[2]) * fraction

        val blendedColor = ColorUtils.HSLToColor(currentHSL)
        holder.binding.progress.progressTintList = ColorStateList.valueOf(blendedColor)
        holder.binding.progress.setIndicatorColor(blendedColor)
      }
    }
  }
}