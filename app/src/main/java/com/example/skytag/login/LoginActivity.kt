package com.example.skytag.login

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.skytag.MainActivity
import com.example.skytag.R
import com.example.skytag.databinding.ActivityLoginBinding
import com.example.skytag.login.model.LoginUserInfo
import com.example.skytag.login.viewmodel.LoginViewModel
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val mLoginViewModel: LoginViewModel by viewModels()
    private val appUtils: AppUtils = AppUtils(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnLogin.setOnClickListener {
            // login()
            loginFast()

        }

        binding.tvClaveCompuesta.setOnClickListener {
            val content = "${appUtils.getDeviceId()}_${appUtils.getDeviceName()}"
            val clipboardManager = ContextCompat.getSystemService(this, ClipboardManager::class.java)!!
            val clip = ClipData.newPlainText("Id Compuesto", content)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(this, "Clave compuesta copiada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginFast() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun login() {

        val user = binding.etUser.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        mLoginViewModel.onLogin(LoginUserInfo(user, password))

        mLoginViewModel.loginModel.observe(this){
            if (it.estado == "exito"){
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }else{
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }


}