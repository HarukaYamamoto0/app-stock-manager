package com.harukadev.stockmanager.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.harukadev.stockmanager.R
import com.harukadev.stockmanager.api.UserAPI
import com.harukadev.stockmanager.data.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import com.google.android.material.progressindicator.CircularProgressIndicator
import android.view.View
import com.harukadev.stockmanager.utils.DateTextWatcher
import com.harukadev.stockmanager.utils.SharedPreferencesManager
import kotlinx.coroutines.DelicateCoroutinesApi
import com.harukadev.stockmanager.utils.CPFTextWatcher;

class LoginActivity : AppCompatActivity() {

    private lateinit var textInputLayoutCPF: TextInputLayout
    private lateinit var textInputLayoutBirthDate: TextInputLayout
    private lateinit var editTextCPF: EditText
    private lateinit var editTextBirthDate: TextInputEditText
    private lateinit var btnLogin: TextView
    private lateinit var spinner: CircularProgressIndicator
    private val mainActivityIntent by lazy { Intent(this, MainActivity::class.java) }
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val userApi = UserAPI()
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferencesManager = SharedPreferencesManager.getInstance(this)

        isLogged()
        initializeViews()
		
		//editTextCPF.addTextChangedListener(CPFTextWatcher)
        editTextBirthDate.addTextChangedListener(DateTextWatcher)

        btnLogin.setOnClickListener {
            btnLogin.text = ""
            spinner.visibility = View.VISIBLE

            val cpf = editTextCPF.text.toString()
            val cleanedCPF = cpf.replace(Regex("\\D"), "")
            val birthDate = editTextBirthDate.text.toString()

            if (cleanedCPF.isEmpty() || birthDate.isEmpty()) {
                showMessage(getString(R.string.no_empty_fields))
                btnLogin.text = getString(R.string.login)
                spinner.visibility = View.INVISIBLE
            } else {
                isValidCredentials(cleanedCPF, birthDate)
            }
        }
    }

    private fun initializeViews() {
        textInputLayoutCPF = findViewById(R.id.textInputLayout_cpf_login)
        textInputLayoutBirthDate = findViewById(R.id.textInputLayout_birth_date_login)
        editTextCPF = findViewById(R.id.textInputEditText_cpf_login)
        editTextBirthDate = findViewById(R.id.textInputEditText_birth_date_login)
        btnLogin = findViewById(R.id.btn_login_screen)
        spinner = findViewById(R.id.spinner_login)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun isValidCredentials(cpf: String, birthDate: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val user = userApi.getUserById(cpf)
                if (user != null && user.birthDate == birthDate) {
                    registerLogin(user)
                    showMessage(getString(R.string.login_successful))
                    startMainActivity()
                } else {
                    showMessage(getString(R.string.invalid_credentials))
                }
            } catch (e: IOException) {
                handleException(e)
            } catch (e: Exception) {
                handleException(e)
            } finally {
                btnLogin.text = getString(R.string.login)
                spinner.visibility = View.INVISIBLE
            }
        }
    }

    private fun startMainActivity() {
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mainActivityIntent)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLogged() {
        val logged = sharedPreferencesManager.getBoolean("logged", false)

        if (logged) {
            startMainActivity()
        }
    }

    private fun registerLogin(user: UserData) {
        sharedPreferencesManager.setString("id", user.id)
        sharedPreferencesManager.setString("cpf", user.cpf)
        sharedPreferencesManager.setString("name", user.name)
        sharedPreferencesManager.setString("birthDate", user.birthDate)
        sharedPreferencesManager.setString("avatarURL", user.avatarURL)
        sharedPreferencesManager.setBoolean("logged", true)
    }

    private fun handleException(e: Exception) {
        Log.e(TAG, e.toString())
        showMessage(getString(R.string.error_occurred))
    }
}
