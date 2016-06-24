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
    String goalPath="";//Ŀ���ַ
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		installBtn=(Button)this.findViewById(R.id.installBtn);
		progressText=(TextView)this.findViewById(R.id.progress);
		installBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					// ��ô洢����·��
					System.out.println("׼����ȡSD��·��");
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					goalPath=sdpath+"install/";
					
					File dir = new File(goalPath);
					// ���Ŀ¼���д��ڣ��������Ŀ¼
					if (!dir.exists()){
						dir.mkdir();
					}
					yijianInstallTask mYijianInstallTask=new yijianInstallTask(MainActivity.this);
					mYijianInstallTask.execute();
					
				}else{
					Toast.makeText(MainActivity.this, "SD��û�ж�д��Ȩ��,,", Toast.LENGTH_LONG).show();
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
	         * ������UI�߳��У��ڵ���doInBackground()֮ǰִ�� 
	         */  
	        @Override  
	        protected void onPreExecute() {  
	            Toast.makeText(context,"һ��app������ʼ��װ...",Toast.LENGTH_SHORT).show();  
	        }  
	        /** 
	         * ��̨���еķ������������з�UI�̣߳�����ִ�к�ʱ�ķ��� 
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
	     	        
	     	       /* if(appExists(fileName)==true){//����Ѿ���װ�����򲻰�װ��
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
	        	
	            return null;  
	        }  
	  
	        /** 
	         * ������ui�߳��У���doInBackground()ִ����Ϻ�ִ�� 
	         */  
	        @Override  
	        protected void onPostExecute(Integer integer) {  
	            Toast.makeText(context,"��װ���",Toast.LENGTH_LONG).show();  
	            MainActivity.this.finish();
	        }  
	  
	        /** 
	         * ��publishProgress()�������Ժ�ִ�У�publishProgress()���ڸ��½��� 
	         */  
	        @Override  
	        protected void onProgressUpdate(Integer... values) {  
	        	int progress=values[0]+1;
	        	progressText.setText("���ڰ�װ�У��벻Ҫ�˳�����װ��Ϻ���Զ��˳�����ǰ��װ���ȣ�"+progress+"/12");  
	        	if(progress==12){
	        		Toast.makeText(MainActivity.this,"������װ��ϣ����Ժ�...",3000).show();
	        		Toast.makeText(MainActivity.this,"������װ��ϣ����Ժ�...",2000).show();
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
