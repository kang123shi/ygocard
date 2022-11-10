package com.eason.ygosearchcard

import android.R.attr
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.eason.ygosearchcard.bean.ResultData
import com.eason.ygosearchcard.bean.ResultStrData
import com.eason.ygosearchcard.databinding.ActivityMainBinding
import com.eason.ygosearchcard.ui.home.HomeViewModel
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.yuyh.library.imgsel.ISNav
import com.yuyh.library.imgsel.config.ISCameraConfig
import java.io.File


class MainActivity :  AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private  var searchItem : MenuItem? =null;
    private val viewModel: HomeViewModel by viewModels()
    private var photoURL : Uri?=null
//    private var choiceFileHelper: ChoiceFileHelper? =null

    private val REQUEST_LIST_CODE = 0
    private val REQUEST_CAMERA_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_deck
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // android 7.0系统解决拍照的问题
        // android 7.0系统解决拍照的问题
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()

        initData()

    }

    private fun initData() {
//         choiceFileHelper = ChoiceFileHelper(this)
        // 请选择您的初始化方式

        // 请选择您的初始化方式
        initAccessToken()
    }

    /**
     * 以license文件方式初始化
     */
    private fun initAccessToken() {
        OCR.getInstance(applicationContext).initAccessToken(object : OnResultListener<AccessToken> {
            override fun onResult(accessToken: AccessToken) {
                val token = accessToken.accessToken
                Log.d("TAA","初始化成功")
            }

            override fun onError(error: OCRError) {
                error.printStackTrace()
                Log.d("TAA","初始化失败"+error.message)
            }
        }, applicationContext)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        searchItem = menu.findItem(R.id.action_search)
        val cameraItem = menu.findItem(R.id.action_camera)
        cameraItem.setOnMenuItemClickListener {
            checkPermiss()
        }
        var searchView = MenuItemCompat.getActionView(searchItem) as SearchView;
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                Log.e("CSDN_LQR", "TextSubmit --> $s")
                viewModel?.setTitle(s);
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                Log.e("CSDN_LQR", "TextChange --> $s")
                return false
            }
        })
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        choiceFileHelper!!.onActivityResult(requestCode, resultCode, data);
         if (requestCode === REQUEST_CAMERA_CODE && resultCode === RESULT_OK && attr.data != null) {
            val path: String? = data?.getStringExtra("result")
             if (StringUtils.isNull(path)){
                 return
             }
                 RecognizeService.recAccurateBasic(
                     applicationContext, File(path)){ result ->
                     Log.d("TAGGGGGG",result)
                     var data : List<ResultStrData> = Gson().fromJson(result, ResultData::class.java).words_result;
                     var getName =false
                     for (i in data.indices){
                         if (StringUtils.isName(data[i].words)){
                            var cardName = StringUtils.cardName(data[i].words)
                             Log.d("TAGGGGGG",cardName)
                             binding.appBarMain.toolbar.title = cardName
                             viewModel.setTitle(cardName)
                             getName =true
                             break
                         }
                     }

                     if (getName){
                         return@recAccurateBasic
                     }

                     for (index in data.indices){
                         if (StringUtils.isJAP(data[index].words)){
                            var cardName = data[index].words
                             cardName = StringUtils.cardName(cardName)
                             Log.d("TAGGGGGG",cardName)
                             viewModel.setTitle(cardName)
                             binding.appBarMain.toolbar.title = cardName
                             break
                         }

                     }
                 }



        }

    }
    private fun checkPermiss():Boolean {
        if (!XXPermissions.isGranted(this,Permission.CAMERA,Permission.WRITE_EXTERNAL_STORAGE)){
            XXPermissions.with(this).permission(Permission.CAMERA)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                       toTakePhone()

                        //
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {

                    }
                })
        }else{
            toTakePhone()
        }
        return false
    }

    private fun toTakePhone() {

        var config = ISCameraConfig.Builder()
            .needCrop(false).build()

        ISNav.getInstance().toCameraActivity(this, config, REQUEST_CAMERA_CODE)



    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



}