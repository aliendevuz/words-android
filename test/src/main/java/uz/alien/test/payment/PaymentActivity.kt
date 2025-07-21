package uz.alien.test.payment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import uz.alien.test.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {

  private lateinit var binding: ActivityPaymentBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityPaymentBinding.inflate(layoutInflater)

    setContentView(binding.root)
    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }

    binding.pay.setOnClickListener {
      copyCardAndOpenPaymentApp(this)
    }
  }

  @SuppressLint("ServiceCast")
  fun copyCardAndOpenPaymentApp(context: Context) {
    val cardNumber = "9860 1606 1764 1222"

    // Clipboard ga nusxalash
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("card_number", cardNumber)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Karta raqami nusxalandi", Toast.LENGTH_SHORT).show()

    // Click ilovasini ochish
    val clickIntent = context.packageManager.getLaunchIntentForPackage("air.com.ssdsoftwaresolutions.clickuz")
    val paymeIntent = context.packageManager.getLaunchIntentForPackage("uz.dida.payme")

    when {
      clickIntent != null -> context.startActivity(clickIntent)
      paymeIntent != null -> context.startActivity(paymeIntent)
      else -> Toast.makeText(context, "Click yoki Payme ilovasi o‘rnatilmagan", Toast.LENGTH_LONG).show()
    }
    val pm = context.packageManager
    val packages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES)

    for (packageInfo in packages) {
      Log.d("AppList", "Package: ${packageInfo.packageName}")
    }

    packages.forEach {
      Log.d("@@@@", it.packageName)
    }
  }
}