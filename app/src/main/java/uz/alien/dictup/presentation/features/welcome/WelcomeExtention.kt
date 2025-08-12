package uz.alien.dictup.presentation.features.welcome

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.core.os.postDelayed
import uz.alien.dictup.BuildConfig
import uz.alien.dictup.databinding.WelcomeActivityBinding

fun startWelcome(binding: WelcomeActivityBinding, setHomeScreen: Runnable, handler: Handler) {

    binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {

            binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

            var margin = 0.0f

            binding.lWelcomeLogoBackground.y += margin

            val layoutParams =
                binding.lWelcomeLogoBackground.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(0, 0, 0, 0)
            binding.lWelcomeLogoBackground.layoutParams = layoutParams

            binding.ivWelcomeLogo1.alpha = 0.0f
            binding.ivWelcomeLogo2.alpha = 0.0f
            binding.ivWelcomeLogo3.alpha = 0.0f

            binding.tvWelcomeBrand.alpha = 0.0f
            binding.tvWelcomeProduct.alpha = 0.0f

            handler.postDelayed(BuildConfig.DURATION) {

                binding.lWelcomeLogoBackground.measure(
                    View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED
                )

                margin = binding.root.height.toFloat() / 6


                handler.postDelayed(BuildConfig.DURATION * 2) {

                    binding.ivWelcomeLogo1.alpha = 1.0f
                    binding.ivWelcomeLogo1.startAnimation(AnimationSet(true).apply {
                        addAnimation(AlphaAnimation(0f, 1f))
                        this.duration = 300L
                        this.interpolator = LinearInterpolator()
                    })

                    handler.postDelayed(1000L) {
                        binding.ivWelcomeLogo2.alpha = 1.0f
                        binding.ivWelcomeLogo2.startAnimation(AnimationSet(true).apply {
                            addAnimation(AlphaAnimation(0f, 1f))
                            this.duration = 300L
                            this.interpolator = LinearInterpolator()
                        })

                        handler.postDelayed(1000L) {
                            binding.ivWelcomeLogo3.alpha = 1.0f
                            binding.ivWelcomeLogo3.startAnimation(AnimationSet(true).apply {
                                addAnimation(AlphaAnimation(0f, 1f))
                                this.duration = 300L
                                this.interpolator = LinearInterpolator()
                            })

                            handler.postDelayed(1000L) {
                                binding.lWelcomeLogoBackground.y -= margin
                                binding.lWelcomeLogoBackground.startAnimation(AnimationSet(true).apply {
                                    addAnimation(
                                        TranslateAnimation(
                                            0.0f,
                                            0.0f,
                                            margin,
                                            0.0f
                                        )
                                    )
                                    this.duration = 600L
                                    this.interpolator = AccelerateDecelerateInterpolator()
                                })

                                handler.postDelayed(1600L) {
                                    binding.tvWelcomeBrand.alpha = 1.0f
                                    binding.tvWelcomeBrand.startAnimation(AnimationSet(true).apply {
                                        addAnimation(AlphaAnimation(0f, 1f))
                                        this.duration = 300L
                                        this.interpolator = LinearInterpolator()
                                    })

                                    handler.postDelayed(1000L) {
                                        binding.tvWelcomeProduct.alpha = 1.0f
                                        binding.tvWelcomeProduct.startAnimation(AnimationSet(true).apply {
                                            addAnimation(AlphaAnimation(0f, 1f))
                                            this.duration = 300L
                                            this.interpolator = LinearInterpolator()
                                        })

                                        handler.postDelayed(2000L) {

                                            setHomeScreen.run()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    })
}