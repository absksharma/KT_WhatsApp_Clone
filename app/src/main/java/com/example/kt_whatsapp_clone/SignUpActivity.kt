package com.example.kt_whatsapp_clone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kt_whatsapp_clone.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val dataBase by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var downloadUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.userImgView.setOnClickListener {
            checkPermissionForImage()
        }
        binding.nextBtn.setOnClickListener {
            binding.nextBtn.isEnabled = false

            val name = binding.nameEt.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(this, "Name Field can't be empty", Toast.LENGTH_SHORT).show()
            } else if (!::downloadUrl.isInitialized) {
                Toast.makeText(this, "Image Can't be empty", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(name, downloadUrl, downloadUrl, auth.uid!!)
                /** Send data To data Base **/
                dataBase.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
                    startActivity(Intent(this, MainActivity::class.java))
                }.addOnFailureListener {
                    binding.nextBtn.isEnabled = true
                }
            }
        }
    }

    private fun checkPermissionForImage() {
        if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        ) {
            val permissionRead = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            requestPermissions(permissionRead, 100)
            requestPermissions(permissionWrite, 101)
        } else {
            getImage.launch("image/*")
        }
    }


    private val getImage = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            binding.userImgView.setImageURI(it)
            uploadImage(it)
        }
    )

    private fun uploadImage(it: Uri) {
        binding.nextBtn.isEnabled = false
        val ref = storage.reference.child("uploads/" + auth.uid.toString())
        val uploadTask = ref.putFile(it)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            binding.nextBtn.isEnabled = true

            if (task.isSuccessful) {
                downloadUrl = task.result.toString()
                Log.i("URL", "Image URL : $downloadUrl")
            }

        }


    }
}