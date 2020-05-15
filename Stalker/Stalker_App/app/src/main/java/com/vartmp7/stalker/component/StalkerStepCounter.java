/*
 * MIT License
 *
 * Copyright (c) 2020 VarTmp7
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vartmp7.stalker.component;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Contatore dei passi utilizzando i sensori messi a disposizione da Android permette di resettare il contatore dopo la lettura
 */
class StalkerStepCounter {
    private SensorManager manager;
    private AtomicLong stepFromLastRead;
    private Sensor sensor;
    private SensorEventListener li;

    /**
     * Costruttore a 2 parametri
     * @param manager SensorManager di default di Android
     * @param sensor Sensor
     */
    StalkerStepCounter(SensorManager manager, Sensor sensor) {
        this.manager = manager;
        this.sensor = sensor;
        stepFromLastRead = new AtomicLong();
        addListener();
    }

    private void addListener() {
        li = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor1 = event.sensor;
                float[] values = event.values;
                int val = -1;
                if (values.length > 0)
                    val = (int) values[0];

                if (sensor1.getType() == Sensor.TYPE_STEP_COUNTER && val != -1) {
                    stepFromLastRead.addAndGet(val);
//                    Toast.makeText(MainActivity.this,"On sensor changed: "+val, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        manager.registerListener(li, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * restituisce i numeri di passi rilevati.
     * @return i numeri di passi rilevati dall'ultima invocazione di StalkerStepCounter#resetSteps
     */
    public long getSteps() {
        return stepFromLastRead.get();
    }


    /**
     * mette a 0 il contatore dei passi a 0
     */
    public void resetSteps(){
        stepFromLastRead.set(0);
    }

    private void removeListener() {
        manager.unregisterListener(li);
    }

}
