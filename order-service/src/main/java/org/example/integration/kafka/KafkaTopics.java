package org.example.integration.kafka;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String ORDER_SENT_FOR_APPROVAL = "order.sent-for-approval";
    public static final String ORDER_APPROVAL_RESULT = "order.approval-result";
}