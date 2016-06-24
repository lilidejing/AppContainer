package com.csw.container;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class InstallService extends Service {

	
	String goalPath="";//目标地址
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		Toast.makeText(this, "一大波app即将开始安装...", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
		// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{

					// 获得存储卡的路径
					System.out.println("准备获取SD卡路径");
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					goalPath=sdpath+"install/";
					
					File dir = new File(goalPath);
					// 如果目录不中存在，创建这个目录
					if (!dir.exists()){
						dir.mkdir();
					}
//					useAsset(goalPath);
					myHandler.sendEmptyMessage(0);
					
				}else{
					Toast.makeText(InstallService.this, "SD卡没有读写的权限,,", Toast.LENGTH_LONG).show();
				}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	Handler myHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
     			useAsset(goalPath);
				break;
			case 1:
				break;
			default:
				break;
			}
		}
		
	};
	
	public void useAsset(String goalPath){  
	    AssetManager assetManager = getAssets();  
	    String assetPath = "preinstall";  
	    String [] files = null;  
	    try {  
	        files = assetManager.list(assetPath);  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	      
	    if(files == null){  
	        return;  
	    }  
	    for(int i = 0; i < files.length; i++){  
	        String filePath = assetPath + "/" + files[i];  
	        
//	        String fileName=files[i].trim().substring(0, files[i].indexOf(".apk"));
	        
//	        String fileName=  getApkPackageName(filePath);
	        
	       /* if(appExists(fileName)==true){//如果已经安装过，则不安装了
	        	continue;
	        }*/
	        try {  
	            InputStream inputStream = assetManager.open(filePath);  
	            FileOutputStream fileOutputStream = new FileOutputStream(goalPath + files[i]);  
	            byte [] b = new byte [1024];  
	            while(inputStream.read(b) != -1){  
	                fileOutputStream.write(b);  
	            }  
	            fileOutputStream.flush();  
	  
	            inputStream.close();  
	            fileOutputStream.close();  
	            
	            File apkFile = new File(goalPath  + files[i]);       
	            if(apkFile.exists()){

	            	String fileName2 =  getApkPackageName(goalPath  + files[i]);
	     	        
	     	        if(appExists(fileName2)==true){//如果已经安装过，则不安装了
	     	        	continue;
	     	        }
	            	
					String paramString ="adb install -r " + goalPath +files[i];
					System.out.println("静默安装");
					send_key_touch(paramString);
					System.out.println("安装完了");	
				}     
	        } catch (IOException e) {  
	        	e.printStackTrace();
	        }  
	    }  
	    
	    //apk全部安装完后删掉sd卡里的apk
	    for(int i = 0; i < files.length; i++){  
	    	File apkFile = new File(goalPath  + files[i]);  
	    	 if(apkFile.exists()){
	    		 
	    		 apkFile.delete();
	    		 try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	 }
	    }
	    
	    
	    
	} 
	
	 public void send_key_touch(String keycode) {
			try
			{
				String keyCommand = keycode;
				Runtime runtime = Runtime.getRuntime();
				Process proc = runtime.exec(keyCommand);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				System.out.println("安装出异常了");
				e.printStackTrace();
			}
	}
	
	 /**
	  * 根据包名判断apk是否安装
	  * @param packageName
	  * @return
	  */
	 private boolean appExists(String packageName){
		 PackageInfo packageInfo;

	     try {
	         packageInfo = this.getPackageManager().getPackageInfo(
	        		 packageName, 0);
	     } catch (NameNotFoundException e) {
	         packageInfo = null;
	         e.printStackTrace();
	     }
	     if(packageInfo ==null){
	         System.out.println("没有安装");
	         return false;
	     }else{
	         System.out.println("已经安装");
	         return true;
	     }
	 }
	
	 
	 
	 /**
	  * 根据安装包路径得到安装包的包名
	  */
	 private String getApkPackageName(String apkPath){
	        PackageManager pm = getPackageManager();    
	        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);    
	        if(info != null){    
	            ApplicationInfo appInfo = info.applicationInfo;    
	            String packageName = appInfo.packageName;  //得到安装包名称  
	           return packageName.toString().trim();
	        }   
	        return null;
	 }
	
}
