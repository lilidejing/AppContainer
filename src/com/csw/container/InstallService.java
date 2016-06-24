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

	
	String goalPath="";//Ŀ���ַ
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		Toast.makeText(this, "һ��app������ʼ��װ...", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
		// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{

					// ��ô洢����·��
					System.out.println("׼����ȡSD��·��");
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					goalPath=sdpath+"install/";
					
					File dir = new File(goalPath);
					// ���Ŀ¼���д��ڣ��������Ŀ¼
					if (!dir.exists()){
						dir.mkdir();
					}
//					useAsset(goalPath);
					myHandler.sendEmptyMessage(0);
					
				}else{
					Toast.makeText(InstallService.this, "SD��û�ж�д��Ȩ��,,", Toast.LENGTH_LONG).show();
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
	        
	       /* if(appExists(fileName)==true){//����Ѿ���װ�����򲻰�װ��
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
	     	        
	     	        if(appExists(fileName2)==true){//����Ѿ���װ�����򲻰�װ��
	     	        	continue;
	     	        }
	            	
					String paramString ="adb install -r " + goalPath +files[i];
					System.out.println("��Ĭ��װ");
					send_key_touch(paramString);
					System.out.println("��װ����");	
				}     
	        } catch (IOException e) {  
	        	e.printStackTrace();
	        }  
	    }  
	    
	    //apkȫ����װ���ɾ��sd�����apk
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
				System.out.println("��װ���쳣��");
				e.printStackTrace();
			}
	}
	
	 /**
	  * ���ݰ����ж�apk�Ƿ�װ
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
	         System.out.println("û�а�װ");
	         return false;
	     }else{
	         System.out.println("�Ѿ���װ");
	         return true;
	     }
	 }
	
	 
	 
	 /**
	  * ���ݰ�װ��·���õ���װ���İ���
	  */
	 private String getApkPackageName(String apkPath){
	        PackageManager pm = getPackageManager();    
	        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);    
	        if(info != null){    
	            ApplicationInfo appInfo = info.applicationInfo;    
	            String packageName = appInfo.packageName;  //�õ���װ������  
	           return packageName.toString().trim();
	        }   
	        return null;
	 }
	
}
