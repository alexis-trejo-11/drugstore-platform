package io.github.alexisTrejo11.drugstore.users.config.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class KafkaErrorHandler implements CommonErrorHandler {
		public static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KafkaErrorHandler.class);
    /**
     * Maneja errores al procesar un mensaje individual
     * 
     * Por ahora, solo registra el error y continúa.
     * En producción, considera:
     * - Enviar a DLQ después de N intentos
     * - Alertar al equipo de operaciones
     * - Implementar circuit breaker
     */
    @Override
    public boolean handleOne(
            Exception thrownException,
            ConsumerRecord<?, ?> record,
            Consumer<?, ?> consumer,
            MessageListenerContainer container) {
        
        log.error("Error processing message from topic: {}, partition: {}, offset: {}, key: {}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                thrownException);
        
        // TODO: Implementar lógica de DLQ o retry
        // Por ahora, retornamos false para que el mensaje no se reintente
        // y se haga commit del offset (evitando procesar el mismo mensaje infinitamente)
        
        return false; // false = no reintentar, hacer commit
    }

    /**
     * Maneja errores al procesar un batch de mensajes
     */
    @Override
    public void handleBatch(
            Exception thrownException,
            ConsumerRecords<?, ?> records,
            Consumer<?, ?> consumer,
            MessageListenerContainer container,
            Runnable invokeListener) {
        
        log.error("Error processing batch of {} messages from topics: {}",
                records.count(),
                records.partitions(),
                thrownException);
        
        // TODO: Implementar lógica de manejo de errores en batch
    }
}