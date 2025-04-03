package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Button btnPaired;
    ListView listDanhSach;
    public static int REQUEST_BLUETOOTH = 1;
    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ánh xạ
        btnPaired = (Button) findViewById(R.id.btnTimthietbi);
        listDanhSach = (ListView) findViewById(R.id.listTb);

        // Kiểm tra thiết bị có Bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            // Show a message. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Thiết bị Bluetooth chưa bật", Toast.LENGTH_LONG).show();
            // finish apk
            finish();
        } else if (!myBluetooth.isEnabled()) {
            // Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Thiết bị Bluetooth chưa bật", Toast.LENGTH_LONG).show();
            }
            Toast.makeText(getApplicationContext(), "Thiết bị Bluetooth đã bật", Toast.LENGTH_LONG).show();
            // starForResult.launch(turnBTon);
            startActivityForResult(turnBTon, REQUEST_BLUETOOTH);
        }
        // Kết thúc kiểm tra thiết bị có bluetooth

        // thực hiện tim thiết bị
        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList(); // gọi hàm tim thiết bị
            }
        });
    }
    private void pairedDevicesList() {
        // Kiểm tra quyền BLUETOOTH_CONNECT (chỉ một lần ở đầu phương thức)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 (API level 31) trở lên
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Yêu cầu quyền nếu chưa được cấp
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 1);
                return; // Dừng thực thi phương thức cho đến khi người dùng cấp quyền
            }
        }

        // Quyền đã được cấp hoặc trên phiên bản Android cũ hơn, tiếp tục xử lý
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); // Get the device's name and the address
            }
        } else {
            Toast.makeText(getApplicationContext(), "Không tìm thấy thiết bị kết nối.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listDanhSach.setAdapter(adapter);
        listDanhSach.setOnItemClickListener(myListClickListener); // Method called when the device from the list is clicked
    }

    // Xử lý kết quả yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Người dùng đã cấp quyền, gọi lại pairedDevicesList()
                pairedDevicesList();
            } else {
                // Người dùng từ chối quyền, hiển thị thông báo hoặc thực hiện hành động thay thế
                Toast.makeText(this, "Quyền Bluetooth bị từ chối.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Lấy địa chỉ MAC của thiết bị, 17 ký tự cuối cùng trong View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Tạo Intent để khởi động activity tiếp theo.
            Intent i = new Intent(MainActivity.this, BlueControl.class);

            // Chuyển activity.
            i.putExtra(MainActivity.EXTRA_ADDRESS, address); // Dữ liệu này sẽ được nhận tại BlueControl (class) Activity
            startActivity(i);
        }
    };
}