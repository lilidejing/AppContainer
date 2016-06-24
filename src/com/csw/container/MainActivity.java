package com.csw.container;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button installBtn;
    TextView progressText;
    String goalPath="";//目标地址
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		installBtn=(Button)this.findViewById(R.id.installBtn);
		progressText=(TextView)this.findViewById(R.id.progress);
		installBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					// 获得存储卡的路径
					System.out.println("准备获取SD卡路径");
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					goalPath=sdpath+"install/";
					
					File dir = new File(goalPath);
					// 如果目录不中存在，创建这个目录
					if (!dir.exists()){
						dir.mkdir();
					}
					yijianInstallTask mYijianInstallTask=new yijianInstallTask(MainActivity.this);
					mYijianInstallTask.execute();
					
				}else{
					Toast.makeText(MainActivity.this, "SD卡没有读写的权限,,", Toast.LENGTH_LONG).show();
				}// TODO Auto-generated method stub
				
			}
			
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	 class yijianInstallTask extends AsyncTask<Void,Integer,Integer>{  
	        private Context context;  
	        yijianInstallTask(Context context) {  
	            this.context = context;  
	        }  
	  
	        /** 
	         * 运行在UI线程中，在调用doInBackground()之前执行 
	         */  
	        @Override  
	        protected void onPreExecute() {  
	            Toast.makeText(context,"一大波app即将开始安装...",Toast.LENGTH_SHORT).show();  
	        }  
	        /** 
	         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法 
	         * @return 
	         */  
	        @Override  
	        protected Integer doInBackground(Void... params) {  
	        	 AssetManager assetManager = getAssets();  
	     	    String assetPath = "preinstall";  
	     	    String [] files = null;  
	     	    try {  
	     	        files = assetManager.list(assetPath);  
	     	    } catch (IOException e) {  
	     	        e.printStackTrace();  
	     	    }  
	     	      
	     	    if(files == null){  
	     	        return null;  
	     	    }  
	     	    for(int i = 0; i < files.length; i++){  
	     	        String filePath = assetPath + "/" + files[i];  
	     	        
//	     	        String fileName=files[i].trim().substring(0, files[i].indexOf(".apk"));
	     	        
//	     	        String fileName=  getApkPackageName(filePath);
	     	        
	     	       /* if(appExists(fileName)==true){//如果已经安装过，则不安装了
	     	        	continue;
	     	        }*/
	     	        publishProgress(i); 
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
	        	
	            return null;  
	        }  
	  
	        /** 
	         * 运行在ui线程中，在doInBackground()执行完毕后执行 
	         */  
	        @Override  
	        protected void onPostExecute(Integer integer) {  
	            Toast.makeText(context,"安装完毕",Toast.LENGTH_LONG).show();  
	            MainActivity.this.finish();
	        }  
	  
	        /** 
	         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度 
	         */  
	        @Override  
	        protected void onProgressUpdate(Integer... values) {  
	        	int progress=values[0]+1;
	        	progressText.setText("正在安装中，请不要退出，安装完毕后会自动退出。当前安装进度："+progress+"/12");  
	        	if(progress==12){
	        		Toast.makeText(MainActivity.this,"即将安装完毕，请稍候...",3000).show();
	        		Toast.makeText(MainActivity.this,"即将安装完毕，请稍候...",2000).show();
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
