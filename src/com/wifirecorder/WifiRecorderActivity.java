package com.wifirecorder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WifiRecorderActivity extends Activity 
{
	private Button btnRefresh;
	private Button btnRecord;//����Wifi��T
	private Button btnExit;
	private TextView txtTime;
	private Calendar time;
	private ListView listWifiResult;//��ܱ��y�쪺Wifi��T
	private List<ScanResult> WifiList;//���y�쪺Wifi�T��
	private WifiManager mWifiMngr;//�޲z�ñ���Wifi
	private String[] WifiInfo;//�s��Wifi�ԲӸ�T 
	private String curTime;
	private Vector<String> WifiSelectedItem = new Vector<String>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//���o�����귽
		btnRefresh = (Button)findViewById(R.id.btnRefresh);
		btnRecord = (Button)findViewById(R.id.btnRecord);
		btnExit = (Button)findViewById(R.id.btnExit);
		txtTime = (TextView)findViewById(R.id.txtTime);
		listWifiResult = (ListView)findViewById(R.id.listResult);
		//�]�wWifi�˸m
		mWifiMngr = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);//���oWifiManager
		//�ҥ�Wifi�˸m
		OpenWifi();
		//���oWifi�C��
		GetWifiList();
		//�]�w���s�\��
		btnRefresh.setOnClickListener(btnListener);
		btnRecord.setOnClickListener(btnListener);
		btnExit.setOnClickListener(btnListener);
		//�]�wListView����ƥ�
		listWifiResult.setOnItemClickListener(listListener);
		listWifiResult.setOnItemLongClickListener(listLongListener);
	}
	
	private Button.OnClickListener btnListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
				case R.id.btnRefresh:
					//���oWifi�C��
					GetWifiList();
					break;
				case R.id.btnRecord:
					RecordCheckWindow();
					break;
				case R.id.btnExit:
					CloseWifi();
					finish();
					break;
			}
		}
	};
	
	private ListView.OnItemClickListener listListener = new ListView.OnItemClickListener()
	{		
		int ItemSelectedInVector;
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			
			//�p�G�Q�Ŀ�N�[�JVector
			if(listWifiResult.isItemChecked(position))
				WifiSelectedItem.add(WifiInfo[position]);
			//�p�G�Q�����Ŀ�N�qVector����
			else
			{
				//���o�ثe������ئbVector������m
				for(int i=0;i<WifiSelectedItem.size();i++)	
					if(WifiSelectedItem.get(i).equals(WifiInfo[position]))
						ItemSelectedInVector = i; 
				WifiSelectedItem.remove(ItemSelectedInVector);
			}
		}
		
	};
	private ListView.OnItemLongClickListener listLongListener = new ListView.OnItemLongClickListener()
	{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v,
				int position, long id) {
			// TODO Auto-generated method stub
			WifiInfo(position);
			return false;
		}
	};
	private void RecordCheckWindow()
	{
		final EditText edtFileName = new EditText(WifiRecorderActivity.this);
		new AlertDialog.Builder(WifiRecorderActivity.this)
		.setTitle("�T�{����")
		.setIcon(R.drawable.ic_launcher)
		.setMessage("�п�J���s�ɮצW��:")
		.setView(edtFileName)
		.setNegativeButton("����", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
			
		})
		.setPositiveButton("�T�w",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//�N�����List�O���åͦ��ɮ�
				DataFormer(edtFileName.getText().toString());
			}
		}).show();
	}
	private void WifiInfo(int index)
	{
		new AlertDialog.Builder(WifiRecorderActivity.this)
		.setTitle("�ԲӸ��")
		.setIcon(R.drawable.ic_launcher)
		.setMessage(WifiInfo[index])
		.setNeutralButton("�T�w", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
			
		})
		.show();
	}
	private void DataFormer(String FileName)
	{
		String WifiDatas = curTime+"\r\n";
		File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"WifiDatas");
		//�NWifi��Ʀs�iWifDatas
		for(int i=0;i<WifiSelectedItem.size();i++)
			WifiDatas += WifiSelectedItem.elementAt(i).toString()+"\r\n";
		//�إ��ɮצbSDCARD��
		if(!directory.exists())//�p�GSD�d�S����Ƨ��N�إ�
			directory.mkdir();
		try {
			
			FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory().getPath()
							+"/WifiData/"+FileName+".txt",false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(WifiDatas);
			Toast.makeText(WifiRecorderActivity.this
							,FileName+".txt �w�s�ܤ��",Toast.LENGTH_LONG).show();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(WifiRecorderActivity.this
							,"�s�ɥ���!",Toast.LENGTH_LONG).show();
		}
	}
	//���}Wifi�˸m
	private void OpenWifi()
	{
		//��Wifi�O�����ɱN���Ұ�
		if(!mWifiMngr.isWifiEnabled()){
			mWifiMngr.setWifiEnabled(true);
			Toast.makeText(WifiRecorderActivity.this,"WiFi�Ұʤ�...�еy��"
						   ,Toast.LENGTH_LONG).show();
			Toast.makeText(WifiRecorderActivity.this,"�Ы�Refresh���s�C��"
					,Toast.LENGTH_LONG).show();
		}
	}
	//����Wifi�˸m
	private void CloseWifi()
	{
		//��Wifi�O�}�ҮɱN���}��
		if(mWifiMngr.isWifiEnabled())
			mWifiMngr.setWifiEnabled(false);
	}
	private void GetWifiList()
	{
		//�}�l���yWifi���I
		mWifiMngr.startScan();
		//�o�챽�y���G
		WifiList = mWifiMngr.getScanResults();
		//�]�wWifi�}�C
		String[] Wifis = new String[WifiList.size()];
		//���o�ثe�ɶ�
		time = Calendar.getInstance();
		curTime = (time.get(Calendar.YEAR))+"/"  
				+(time.get(Calendar.MONTH)+1)+"/"  
				+(time.get(Calendar.DAY_OF_MONTH))+"  "	
				+time.get(Calendar.HOUR_OF_DAY)+":"  
				+time.get(Calendar.MINUTE)+":"	
				+time.get(Calendar.SECOND);
		txtTime.setText("Time:"+curTime);
		//�NWifi��T��J�}�C��(�h��M���)
		for(int i=0;i<WifiList.size();i++)
			Wifis[i] = "SSID:"+WifiList.get(i).SSID +"\n" //SSID
						+"�T���j��:"+WifiList.get(i).level+"dBm";//�T���j�z  
		//�NWifiSelectedItem���Ȧs����ƲM��
		WifiSelectedItem.removeAllElements();
		//�]�wWifi�M��
		SetWifiList(Wifis);
	}
	private void SetWifiList(String[] Wifis)
	{
		//�إ�ArrayAdpter
		 ArrayAdapter<String> adapterWifis = new ArrayAdapter<String>(WifiRecorderActivity.this
						,android.R.layout.simple_list_item_checked,Wifis);
		//�]�wListView���h��
		listWifiResult.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		//�]�wListView�ӷ�
		listWifiResult.setAdapter(adapterWifis);
		
		//��l��WifiInfo�}�C
		WifiInfo = null;
		//�]�wWifi��T��J�}�C��(�O���s�ɥ�)
		WifiInfo = new String[WifiList.size()];
		
		for(int i=0;i<WifiList.size();i++)
			WifiInfo[i] = "SSID:"+WifiList.get(i).SSID +"\r\n"      //SSID
						+"BSSID:"+WifiList.get(i).BSSID+"\r\n"   //BSSID
						+"�T���j��:"+WifiList.get(i).level+"dBm"+"\r\n" //�T���j�z 
						+"�q�D�W�v:"+WifiList.get(i).frequency+"MHz"+"\r\n"; //�q�D�W�v 
	}
	

}
