package org.proyectoblanco.pricing;

/**
 * Contador de avance de una importación en curso, para poder reportar
 * progreso mientras los workers procesan las filas del lote.
 */
public class ImportProgressStats {

    private int processedCount;

    public synchronized void recordProcessed() {
        processedCount++;
    }

    public int getProcessedCount() {
        return processedCount;
    }
}
