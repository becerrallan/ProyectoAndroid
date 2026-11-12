package com.example;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.widget.EditText;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.testclase1.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PersonButtonsTest {
    @Test
    public void buttonsCompleteCrudAndReturnFromListing() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Intent intent = new Intent(instrumentation.getTargetContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Activity main = instrumentation.startActivitySync(intent);
        Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(Listado.class.getName(), null, false);
        try {
            instrumentation.runOnMainSync(() -> {
                EditText id = main.findViewById(R.id.etId);
                EditText nombre = main.findViewById(R.id.etNombre);
                EditText apellido = main.findViewById(R.id.etApellido);
                nombre.setText("PruebaBotones");
                apellido.setText("Temporal");
                main.findViewById(R.id.btnCrear).performClick();
                assertTrue(id.getText().toString().matches("[0-9]+"));
                nombre.setText("");
                main.findViewById(R.id.btnConsultar).performClick();
                assertEquals("PruebaBotones", nombre.getText().toString());
                apellido.setText("Actualizado");
                main.findViewById(R.id.btnActualizar).performClick();
                apellido.setText("");
                main.findViewById(R.id.btnConsultar).performClick();
                assertEquals("Actualizado", apellido.getText().toString());
                main.findViewById(R.id.btnListar).performClick();
            });
            Activity listado = monitor.waitForActivityWithTimeout(5000);
            assertNotNull(listado);
            instrumentation.runOnMainSync(() -> {
                assertTrue(((android.widget.TableLayout) listado.findViewById(R.id.tblistado)).getChildCount() > 1);
            });
            instrumentation.waitForIdleSync();
            int[] location = new int[2];
            instrumentation.runOnMainSync(() -> {
                android.view.View button = listado.findViewById(R.id.button);
                button.getLocationOnScreen(location);
                int topInset = androidx.core.view.ViewCompat.getRootWindowInsets(button)
                        .getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars()).top;
                assertTrue(location[1] >= topInset);
                location[0] += button.getWidth() / 2;
                location[1] += button.getHeight() / 2;
            });
            long now = android.os.SystemClock.uptimeMillis();
            android.view.MotionEvent down = android.view.MotionEvent.obtain(
                    now, now, android.view.MotionEvent.ACTION_DOWN, location[0], location[1], 0);
            android.view.MotionEvent up = android.view.MotionEvent.obtain(
                    now, now + 100, android.view.MotionEvent.ACTION_UP, location[0], location[1], 0);
            instrumentation.sendPointerSync(down);
            instrumentation.sendPointerSync(up);
            down.recycle();
            up.recycle();
            instrumentation.waitForIdleSync();
            assertTrue(listado.isFinishing());
            instrumentation.runOnMainSync(() -> {
                main.findViewById(R.id.btnEliminar).performClick();
                assertEquals("", ((EditText) main.findViewById(R.id.etId)).getText().toString());
            });
        } finally {
            instrumentation.removeMonitor(monitor);
            instrumentation.runOnMainSync(main::finish);
        }
    }
}
