package br.com.gabrielos.Abre_portao;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private String filename = "code.txt";
    private String filepath = "port";
    File myExternalFile;
    String myData = "";
    boolean mbound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button btn = (Button) findViewById(R.id.btnAbrir);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_edit);

        //Troca de codigo
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teste = readInfo();
                doChangeCode(teste);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Find all available drivers from attached devices.
                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
                if (availableDrivers.isEmpty()) {
                    return;
                }

                // Open a connection to the first available driver.
                UsbSerialDriver driver = availableDrivers.get(0);
                UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
                if (connection == null) {
                    // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
                    showMessage("Caiu no 1 if", Toast.LENGTH_LONG);
                    return;
                }

                UsbSerialPort port = driver.getPorts().get(0); // Most devices have just one port (port 0)
                try {
                    port.open(connection);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Envio de sinal
                try {
                    String code = readInfo();
                    port.write(code.getBytes(), 1);
                    showMessage("Sinal Enviado", Toast.LENGTH_LONG);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void doChangeCode(String code) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setTitle("Troca de código");
        alert.setMessage("Digite o código criptografico");

        final EditText input = new EditText(this);
        //input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        input.setText(code);

        alert.setView(input);

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                writeInfo(input.getText().toString());
            }
        });

        alert.show();
    }

    public void showMessage(String message, int duration) {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    public void writeInfo(String code) {

        myExternalFile = new File(getExternalFilesDir(filepath), filename);

        try {
            FileOutputStream fos = new FileOutputStream(myExternalFile, false);
            fos.write(code.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showMessage("Código salvo com sucesso", Toast.LENGTH_SHORT);
    }

    public String readInfo() {

        myData = "";

        myExternalFile = new File(getExternalFilesDir(filepath), filename);

        if (!myExternalFile.exists()) {
            return "";
        }

        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //showMessage("Código lido", Toast.LENGTH_SHORT);
        return myData;
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}