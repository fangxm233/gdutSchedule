package com.fangxm.schedule

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fangxm.schedule.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_schedule, R.id.navigation_attendance, R.id.navigation_my))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //对lateinit的变量进行初始化
//        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //如果手机的SDK版本使用新的权限模型，检查是否获得了位置权限，如果没有就申请位置权限，如果有权限就刷新位置
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
//                //requestPermissions是异步执行的
//                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
//                    LOCATION_PERMISSION)
//            }
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == 200) {
            println("登录成功")
        }
    }

//    private lateinit var locationManager: LocationManager
//
//    //定义一个权限COde，用来识别Location权限
//    private val LOCATION_PERMISSION = 1
//
//    //使用匿名内部类创建了LocationListener的实例
//    val locationListener = object : LocationListener {
//        override fun onProviderDisabled(provider: String) {
////            System.out.println("关闭了GPS")
//        }
//
//        override fun onProviderEnabled(provider: String) {
////            System.out.println("打开了GPS")
////            showLocation(locationManager)
//        }
//
//        override fun onLocationChanged(location: Location) {
////            System.out.println("变化了")
////            showLocation(locationManager)
//        }
//
//        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
//        }
//    }

//    override fun onPause() {
//        super.onPause()
//        val hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//        if ((locationManager != null) && ((hasLocationPermission == PackageManager.PERMISSION_GRANTED))) {
//            locationManager.removeUpdates(locationListener)
//        }
//    }

//    override fun onResume() {
//        //挂上LocationListener, 在状态变化时刷新位置显示，因为requestPermissionss是异步执行的，所以要先确认是否有权限
//        super.onResume()
//        val hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//        if ((locationManager != null) && ((hasLocationPermission == PackageManager.PERMISSION_GRANTED))) {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0F, locationListener)
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,0F, locationListener)
//            showLocation(locationManager)
//        }
//    }

    //申请下位置权限后，要刷新位置信息
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                System.out.println("获取了位置权限")
//                showLocation(locationManager)
//            }
//        }
//    }
//
//    fun showLocation(locationManager: LocationManager) {
////        System.out.println(getLocation().toString())
//    }
//
//    //获取位置信息
//    fun getLocation(): Location? {
//        var location: Location? = null
//        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
//            System.out.println("没有位置权限")
//        }
//        else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            System.out.println("没有打开GPS")
//        }
//        else {
//            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            if (location == null) {
//                System.out.println("位置信息为空")
//                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//                if (location == null) {
//                    System.out.println("网络位置信息也为空")
//                }
//                else {
//                    System.out.println("当前使用网络位置")
//                }
//            }
//        }
//        return location
//    }
}